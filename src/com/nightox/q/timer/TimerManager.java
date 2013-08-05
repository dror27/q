package com.nightox.q.timer;

import java.util.LinkedList;
import java.util.List;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.nightox.q.db.Database;
import com.nightox.q.servlets.AppApi;
import com.nightox.q.utils.DateUtils;

public class TimerManager {

	private Scheduler 		scheduler;
	private List<TimerSpec>	initTimers = new LinkedList<TimerSpec>();
	private boolean			enabled = true;
	
	public synchronized void startManager()
	{
		if ( !enabled )
			return;
		if ( scheduler == null )
		{
			try
			{
		        // start
				scheduler = StdSchedulerFactory.getDefaultScheduler();
		        scheduler.start();
		        
		        // create initial timers on a separate thread to wait for startup to be over
		        Thread		thread = new Thread(new Runnable() {
					
					@Override
					public void run() {
						
						// wait for factory to finish initialization
						while ( !AppApi.isInitDone() )
							DateUtils.sleep(1000);
						
						// execute under a database session
						Database.wrapRunnable(new Runnable() {
							
							@Override
							public void run() {
						        for ( TimerSpec spec : initTimers )
						        	startTimer(spec);								
							}
						});
						
					}
				});
		        
		        thread.start();
			}
			catch (SchedulerException e)
			{
				throw new RuntimeException(e);
			}
		}
	}
	
	public synchronized void stopManager()
	{
		if ( !enabled )
			return;
		if ( scheduler != null )
		{
			try
			{
				scheduler.shutdown();
			}
			catch (SchedulerException e)
			{
				throw new RuntimeException(e);
			}
		}
	}
	
	public synchronized void startTimer(TimerSpec spec)
	{
		if ( !enabled )
			return;
		if ( spec.getTimeout() <= 0 )
			return;
		if ( scheduler == null )
			startManager();
		
		try
		{
			@SuppressWarnings("unchecked")
	    	JobDetail		job = JobBuilder.newJob(spec.getClazz())
	    								.withIdentity(spec.getJob(), spec.getGroup())
	    								.build();
	
	
	    	TriggerBuilder<Trigger> builder = TriggerBuilder.newTrigger()
										.withIdentity(spec.getTrigger(), spec.getGroup())
										.startNow();
			Trigger 		trigger;
			if ( spec.isForever() )
				trigger = builder.withSchedule(SimpleScheduleBuilder.simpleSchedule()
							.withIntervalInSeconds(spec.getTimeout())
							.repeatForever()).build();
			else
				trigger = builder.withSchedule(SimpleScheduleBuilder.simpleSchedule()
							.withIntervalInSeconds(spec.getTimeout())).build();
			
			scheduler.scheduleJob(job, trigger);
		}
		catch (SchedulerException e)
		{
			throw new RuntimeException(e);
		}
		
	}

	public List<TimerSpec> getInitTimers() {
		return initTimers;
	}

	public void setInitTimers(List<TimerSpec> initTimers) {
		this.initTimers = initTimers;
	}

	public synchronized Scheduler getScheduler() 
	{
		if ( !enabled )
			return null;
		if ( scheduler == null )
			startManager();
		
		return scheduler;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
