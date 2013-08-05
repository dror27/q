package com.nightox.q.api.cmd.base;

import java.util.List;

import com.nightox.q.api.ApiException;
import com.nightox.q.utils.ExecUtils;

public abstract class CmdShellCommandBase {

	private String		shellCommand;

	protected List<String> executeShell() throws ApiException
	{
		return ExecUtils.executeShellCatch(getShellCommand(), null);
	}
	
	public String getShellCommand() {
		return shellCommand;
	}

	public void setShellCommand(String shellCommand) {
		this.shellCommand = shellCommand;
	}
}
