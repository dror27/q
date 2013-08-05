package com.nightox.q.jobs;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nightox.q.db.Database;
import com.nightox.q.model.Job;

public abstract class JobRunnerBase implements IJobRunner {

	private static Log		log = LogFactory.getLog(JobRunnerBase.class);
	
	private int				pollingFrequencyMilli = 0;
	
	public Date nextRun(Job job)
	{
		return calcNextRun(job);
	}
	
	protected Date calcNextRun(Job job)
	{
		int			milli = getPollingFrequencyMilli(job);
		
		if ( milli > 0 )
		{
			Calendar		now = Calendar.getInstance();
			
			now.add(Calendar.MILLISECOND, milli);
			
			return now.getTime();
		}
		else
			return null;
	}

	protected void commitJobProgress(Job job, double progress, String message)
	{
		log.info("progress: " + progress + ", message: " + message);
		
		if ( progress >= 0 )
			job.setProgress(progress);
		
		if ( message != null )
			job.setMessage(message);
		
		Database.commitSession(job);
	}	
	
	public void setPollingFrequencyMilli(int pollingFrequencyMilli) {
		this.pollingFrequencyMilli = pollingFrequencyMilli;
	}

	public int getPollingFrequencyMilli() {
		return pollingFrequencyMilli;
	}
	
	public int getPollingFrequencyMilli(Job job) {
		return getPollingFrequencyMilli(); 
	}

}
