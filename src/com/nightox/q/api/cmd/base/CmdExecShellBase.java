package com.nightox.q.api.cmd.base;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nightox.q.api.ApiContext;
import com.nightox.q.api.ApiException;
import com.nightox.q.utils.ExecUtils;

public class CmdExecShellBase extends CmdBase {

	@SuppressWarnings("unused")
	private static Log		log = LogFactory.getLog(CmdExecShellBase.class);

	private String			command;
	private boolean			returnFirstLine;
	private boolean			returnLastLine;
	private boolean			returnAllLines;
	
	public void doCommand(ApiContext context) throws ApiException
	{
		super.doCommand(context);
		
		List<String>		lines = ExecUtils.executeShellCatch(getCommand(context), null);
		
		if ( returnFirstLine && lines.size() > 0 )
			context.getApiResponse().setField(context.getRequestVerb(), lines.get(0));
		if ( returnLastLine && lines.size() > 0 )
			context.getApiResponse().setField(context.getRequestVerb(), lines.get(lines.size() - 1));
		else if ( returnAllLines )
			context.getApiResponse().setField(context.getRequestVerb(), lines);
	}

	public String getCommand(ApiContext context) throws ApiException
	{
		return getCommand();
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public boolean isReturnLastLine() {
		return returnLastLine;
	}

	public void setReturnLastLine(boolean returnLastLine) {
		this.returnLastLine = returnLastLine;
	}

	public boolean isReturnAllLines() {
		return returnAllLines;
	}

	public void setReturnAllLines(boolean returnAllLines) {
		this.returnAllLines = returnAllLines;
	}

	public boolean isReturnFirstLine() {
		return returnFirstLine;
	}

	public void setReturnFirstLine(boolean returnFirstLine) {
		this.returnFirstLine = returnFirstLine;
	}
	
}
