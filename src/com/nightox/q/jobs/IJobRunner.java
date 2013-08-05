package com.nightox.q.jobs;

import java.util.Date;

import com.nightox.q.model.Job;

public interface IJobRunner {

	Date		run(Job job) throws Exception;
	Date		nextRun(Job job);
}
