package com.nightox.q.jobs;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.nightox.q.beans.Factory;
import com.nightox.q.db.Database;
import com.nightox.q.model.Job;
import com.nightox.q.model.base.DbObject;

public class SchedulerJob implements org.quartz.Job {
	
	private static Log					log = LogFactory.getLog(SchedulerJob.class);
	
	private int							threads = 3;
	private int							blackListSeconds = 10;
	private Map<String, IJobRunner>		jobRunners = new LinkedHashMap<String, IJobRunner>();
	private ExecutorService				executorService = null;
	private Map<Integer,Date>			scheduleBlackList = new LinkedHashMap<Integer, Date>();

	private class MyJobTask implements Runnable 
	{
		int		id;

		public void run() 
		{
			log.debug("MyJobTask:run");
			
			Thread.currentThread().setName("SchedulerJob_" + Thread.currentThread().getId() + "_job_" + id);
			
			Database.wrapRunnable(new Runnable() {
				public void run() 
				{
					try
					{
						// retrieve job
						Job			job = (Job)DbObject.get(Job.class, id);
						log.info("starting job: " + job.getDisplayName());
						
						// if not scheduled, ignore
						if ( job.getStatus() != Job.STATUS_SCHEDULED )
						{
							log.warn("job startup ignored: " + job.getDisplayName() + ", status: " + job.getStatus());
							return;
						}
						
						// switch to running
						job.setStatus(Job.STATUS_RUNNING);
						job.setDatetimeStarted(new Date());
						Database.getSession().flush();
						
						// find runner
						IJobRunner		jobRunner = jobRunners.get(job.getKind());

						// do execution itself
						try
						{
							if ( jobRunner == null )
								throw new Exception("no runner for kind: " + job.getKind());
							
							// run!
							job.setMessage(null);
							job.setDatetimeNextRun(jobRunner.run(job));
							
							// if there, then done
							job.setStatus(Job.STATUS_DONE);
							job.setDatetimeDone(new Date());
							log.info("done job: " + job.getDisplayName());
						}
						catch (Throwable e)
						{
							log.error("job throw an exception: " + e.getMessage(), e);
							e.printStackTrace();
							
							// error
							job.setStatus(Job.STATUS_ERROR);
							job.setDatetimeDone(new Date());
							job.setMessage(e.getMessage());
							job.setDatetimeNextRun(jobRunner.nextRun(job));
						}
					}
					catch (Throwable e)
					{
						throw new RuntimeException(e);
					}
				}
			});
		}
	}
	
	private synchronized void init()
	{
		if ( executorService == null )
		{
			threads = Integer.parseInt(Factory.getInstance().getConf().getProperty("job.threads", Integer.toString(threads)));
			
			executorService = Executors.newFixedThreadPool(threads);
			log.debug("executorService created, " + threads + " threads");
			
		}
	}
	
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException 
	{
		log.debug("execute");
		
		// do it
		Database.wrapRunnable(new Runnable() {
			public void run() 
			{
				Factory.getInstance().getEnvironment().getSchedulerJob().tick();
			}
		});
	}

	@SuppressWarnings("unchecked")
	public void tick()  
	{
		synchronized (SchedulerJob.class) 
		{
			log.debug("tick");
			
			// make sure we are initialized
			init();
			
			// scan for pending jobs and schedule them
			Criteria		crit = Database.getSession().createCriteria(Job.class)
										.add(Restrictions.or(
												Restrictions.eq("status", Job.STATUS_PENDING),
												Restrictions.le("datetimeNextRun", new Date())))
										.addOrder(Order.asc("datetimeNextRun"));
			List<MyJobTask> tasks = new LinkedList<MyJobTask>();
			
			for ( Job job : (List<Job>)crit.list() )
			{
				int			id = job.getId();
				
				// skip this one?
				if ( scheduleBlackList.containsKey(id) )
				{
					log.warn("skipping job: " + job.getDisplayName());
					continue;
				}
				
				log.info("scheduling job: " + job.getDisplayName());
				
				// change status
				job.setStatus(Job.STATUS_SCHEDULED);
				job.setDatetimeNextRun(null);
				job.setDatetimeScheduled(new Date());
				
				// save
				Database.getSession().flush();
				
				// schedule
				scheduleBlackList.put(id, new Date());
				MyJobTask	task = new MyJobTask();
				task.id = id;
				tasks.add(task);
			}
			
			// commit the session
			Database.commitSession(null);

			// schedule tasks 
			for ( MyJobTask task : tasks )
				executorService.submit(task);
			
			// clear stale cache
			List<Integer>		stale = new LinkedList<Integer>();
			Date				now = new Date();
			for ( int id : scheduleBlackList.keySet() )
			{
				if ( now.getTime() - scheduleBlackList.get(id).getTime() >= blackListSeconds * 1000 )
					stale.add(id);
			}
			for ( int id : stale )
				scheduleBlackList.remove(id);
		}
	}

	public int getThreads() {
		return threads;
	}

	public void setThreads(int threads) {
		this.threads = threads;
	}

	public Map<String, IJobRunner> getJobRunners() {
		return jobRunners;
	}

	public void setJobRunners(Map<String, IJobRunner> jobRunners) {
		this.jobRunners = jobRunners;
	}

	public int getBlackListSeconds() {
		return blackListSeconds;
	}

	public void setBlackListSeconds(int blackListSeconds) {
		this.blackListSeconds = blackListSeconds;
	}
	

}
