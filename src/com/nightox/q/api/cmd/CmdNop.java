package com.nightox.q.api.cmd;

import com.nightox.q.api.ApiContext;
import com.nightox.q.api.ApiException;
import com.nightox.q.api.cmd.base.CmdBase;

public class CmdNop extends CmdBase {

	public void doCommand(ApiContext context) throws ApiException
	{
		super.doCommand(context);
		
		// delay?
		long	delay = context.getApiRequest().getLongField("delay", -1);
		if ( delay > 0 )
		{
			try 
			{
				Thread.sleep(delay);
			}
			catch (InterruptedException e)
			{
				throw new ApiException(e);
			}
		}
	}
}
