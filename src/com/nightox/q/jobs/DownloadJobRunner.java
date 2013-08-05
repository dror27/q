package com.nightox.q.jobs;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;

import com.freebss.sprout.banner.util.StreamUtils;
import com.nightox.q.model.Job;

public class DownloadJobRunner extends JobRunnerBase {

	public Date run(Job job) throws Exception 
	{
		URL				url = new URL(job.getParam0());
		String			path = job.getParam1();
		
		InputStream		is = url.openStream();
		OutputStream 	os = new FileOutputStream(path);
		
		StreamUtils.copy(is, os);
		
		is.close();
		os.close();
		
		return null;
	}
}
