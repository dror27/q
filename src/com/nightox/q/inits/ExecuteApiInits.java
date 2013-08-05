package com.nightox.q.inits;

import java.util.List;

import com.nightox.q.api.ApiException;
import com.nightox.q.servlets.AppApi;

public class ExecuteApiInits implements Runnable {

	private List<String>		cmds;
	
	public void run() 
	{
		try
		{
			if ( cmds != null )
				for ( String cmd : cmds )
				{
					AppApi.executeApiCommand(cmd);
				}
		}
		catch (ApiException e)
		{
			new RuntimeException(e);
		}	
	}

	public void setCmds(List<String> cmds) {
		this.cmds = cmds;
	}
}
