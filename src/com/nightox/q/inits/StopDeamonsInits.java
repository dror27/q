package com.nightox.q.inits;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nightox.q.api.ApiException;
import com.nightox.q.beans.Factory;
import com.nightox.q.utils.ExecUtils;

public class StopDeamonsInits implements Runnable {

	private List<String>		deamons;
	
	private static Log			log = LogFactory.getLog(StopDeamonsInits.class);

	
	@Override
	public void run() 
	{
		if ( deamons != null && deamons.size() > 0 )
		{
			String		psCmd = Factory.getInstanceEnvironment().getShellCommand("ps");
			String		killCmd = Factory.getInstanceEnvironment().getShellCommand("kill");
			
			// get all process
			List<String> psLines;
			try
			{
				psLines = ExecUtils.executeShellCatch	(psCmd + " -eo pid,args", null);
			}
			catch (ApiException e)
			{
				log.warn(e);
				return;
			}
			
			for ( String psLine : psLines )
			{
				psLine = psLine.trim();
				
				// check if includes one of the deamons to kill
				int			pid = -1;
				for ( String deamon : deamons )
					if ( psLine.indexOf(deamon) >= 0 )
					{
						try
						{
							pid = Integer.parseInt(psLine.split(" ")[0]);
						}
						catch (NumberFormatException e)
						{
							log.error("NumberFormatError on getting pid from: " + psLine);
							break;
						}
						break;
					}
				
				// kill?
				if ( pid > 0 )
				{
					try
					{
						ExecUtils.executeShellCatch(killCmd + " " + pid, null, false);
						Thread.sleep(500);
						ExecUtils.executeShellCatch(killCmd + " -9 " + pid, null, false);
					}
					catch (ApiException e)
					{
						log.warn(e);
					}
					catch (InterruptedException e)
					{
						Thread.currentThread().interrupt();
					}
				}
			}
		}
	}


	public List<String> getDeamons() {
		return deamons;
	}

	public void setDeamons(List<String> deamons) {
		this.deamons = deamons;
	}

}
