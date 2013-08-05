package com.nightox.q.jobs;

import java.util.Date;

import com.nightox.q.model.Job;

public class RunnableJobRunner extends JobRunnerBase {
	
	@Override
	public Date run(Job job) throws Exception 
	{
		String						className = job.getParam0();
		@SuppressWarnings("unchecked")
		Class<? extends Runnable>	clazz = (Class<? extends Runnable>)Class.forName(className);
		Runnable					obj = clazz.newInstance();
		
		obj.run();
		
		return calcNextRun(job);
	}

	@Override
	public int getPollingFrequencyMilli(Job job) {
		
		return Integer.parseInt(job.getParam1());
	}

}
