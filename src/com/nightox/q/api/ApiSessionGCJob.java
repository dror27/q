package com.nightox.q.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.nightox.q.beans.Factory;

public class ApiSessionGCJob implements Job {

	private static Log		log = LogFactory.getLog(ApiSessionGCJob.class);
	
	public ApiSessionGCJob()
	{
		log.debug("ApiSessionGCJob()");
	}

	@Override
	public void execute(JobExecutionContext jec) throws JobExecutionException 
	{
		log.debug("execute()");
		
		Factory.getInstance().getEnvironment().getApiSessionManager().gc();
	}
}
