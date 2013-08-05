package com.nightox.q.jobs;

import java.util.Date;

import com.nightox.q.model.Job;
import com.nightox.q.servlets.AppApi;

public class ApiJobRunner extends JobRunnerBase {

	public Date run(Job job) throws Exception 
	{
		AppApi.executeApiCommandAsInternal(job.getParam0());
		
		return calcNextRun(job);
	}

}
