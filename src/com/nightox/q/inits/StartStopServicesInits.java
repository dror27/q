package com.nightox.q.inits;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nightox.q.api.ApiException;
import com.nightox.q.beans.Factory;
import com.nightox.q.utils.ExecUtils;

public class StartStopServicesInits implements Runnable {

	private List<String>		services;
	
	private static Log			log = LogFactory.getLog(StartStopServicesInits.class);

	
	@Override
	public void run() 
	{
		if ( services != null )
		{
			String		serviceCmd = Factory.getInstanceEnvironment().getShellCommand("service");
			
			for ( String service : services )
			{
				String		verb = "stop";
				if ( service.charAt(0) == '-' )
					service = service.substring(1);
				else if ( service.charAt(0) == '+' )
				{
					verb = "start";
					service = service.substring(1);
				}
				
				String		cmd = serviceCmd + " " + service + " " + verb;
				
				try {
					ExecUtils.executeShellCatch(cmd, null, false);
				} catch (ApiException e) {
					log.error("failed to " + verb + " service: " + service, e);
				}
			}
		}
	}

	public List<String> getServices() {
		return services;
	}

	public void setServices(List<String> services) {
		this.services = services;
	}

}
