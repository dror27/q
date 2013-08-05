package com.nightox.q.jobs;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.nightox.q.db.Database;
import com.nightox.q.model.Job;

public class JobsInitializer implements Runnable {

	@SuppressWarnings("unchecked")
	public void run() 
	{
		// all jobs that are either scheduled or running must be returned to being queued
		Criteria		crit = Database.getSession().createCriteria(Job.class)
								.add(Restrictions.or(
										Restrictions.eq("status", Job.STATUS_SCHEDULED),
										Restrictions.eq("status", Job.STATUS_RUNNING)));
		for ( Job job : (List<Job>)crit.list() )
			job.setStatus(Job.STATUS_PENDING);
	}

}
