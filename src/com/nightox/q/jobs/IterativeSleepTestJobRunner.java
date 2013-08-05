package com.nightox.q.jobs;

import java.util.Date;

import com.nightox.q.db.Database;
import com.nightox.q.model.Job;

public class IterativeSleepTestJobRunner extends JobRunnerBase {

	private		int		iterations = 1;
	private 	long	millis = 1000;
	
	public Date run(Job job) throws Exception 
	{
		commitJobProgress(job, 0, null);
		
		for ( int iter = 0 ; iter < iterations ; iter++ )
		{
			commitJobProgress(job, (double)iter / iterations, null);
			Database.commitSession(job);
			Thread.sleep(millis);
		}
		
		commitJobProgress(job, 1, null);
		
		return null;
	}

	public void setIterations(int iterations) {
		this.iterations = iterations;
	}

	public void setMilli(long millis) {
		this.millis = millis;
	}

}
