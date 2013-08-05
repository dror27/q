package com.nightox.q.api.cmd;

import com.nightox.q.api.ApiContext;
import com.nightox.q.api.ApiException;
import com.nightox.q.api.cmd.base.CmdBase;

public class CmdLogout extends CmdBase {

	public void doCommand(ApiContext context) throws ApiException
	{
		context.getApiSession().setUser(null);
	}

}
