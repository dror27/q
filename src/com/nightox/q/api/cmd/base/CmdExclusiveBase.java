package com.nightox.q.api.cmd.base;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.apache.commons.logging.Log;

import com.nightox.q.api.ApiContext;
import com.nightox.q.api.ApiException;

public abstract class CmdExclusiveBase extends CmdBase {

	private Map<String, Semaphore>	semaphores = new LinkedHashMap<String, Semaphore>();
	protected abstract Log			getLog();
	protected abstract void			doExclusiveCommand(ApiContext context) throws ApiException;

	public void doCommand(ApiContext context) throws ApiException
	{
		// establish key and semaphore
		String		key = getExclusivityKey(context);
		Semaphore	semaphore;
		synchronized (this)
		{
			semaphore = semaphores.get(key);
			if ( semaphore == null )
				semaphores.put(key, semaphore = new Semaphore(1));
		}
		
		// protect using semaphore
		if ( semaphore.tryAcquire() )
		{
			try
			{
				getLog().info("semaphore aquired: " + key);
				super.doCommand(context);
				
				doExclusiveCommand(context);
			}
			finally
			{
				semaphore.release();
				getLog().info("semaphore released: " + key);
			}
		}
		else
			getLog().info("semaphore busy: " + key);
	}

	protected String getExclusivityKey(ApiContext context)
	{
		return this.getClass().getName();
	}
	


}
