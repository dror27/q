package com.nightox.q.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.freebss.sprout.banner.util.StreamUtils;
import com.nightox.q.api.ApiConst;
import com.nightox.q.api.ApiException;
import com.nightox.q.utils.text.ILineConsumer;

public class ExecUtils {

	private static Log		log = LogFactory.getLog(ExecUtils.class);

	private static boolean waitForStderr = false;
	
	private String			cmd;
	private String[]		cmdToks;
	private ILineConsumer	outputLineConsumer;
	
	private Process			proc;
	private File			workingFolder;

	public ExecUtils(String cmd) 
	{
		this.cmd = cmd;
	}
	
	public ExecUtils(String[] cmdToks, ILineConsumer lineConsumer) 
	{
		this.cmdToks = cmdToks;
		this.outputLineConsumer = lineConsumer;
	}
	
	public ExecUtils(String cmd, ILineConsumer lineConsumer) 
	{
		this(cmd);
		this.outputLineConsumer = lineConsumer;
	}
	
	public void start() throws ApiException
	{
		try
		{
			Process					proc;
			
			if ( cmdToks != null )
			{
				log.info("cmdToks: " + Arrays.asList(cmdToks));
				proc = Runtime.getRuntime().exec(cmdToks, null, workingFolder);
			}
			else
			{
				log.info("cmd: " + cmd);
				proc = Runtime.getRuntime().exec(cmd, null, workingFolder);
			}
			
			StreamReaderThread		stdout = new StreamReaderThread("executeShell", "stdout", proc.getInputStream(), System.out, outputLineConsumer);
			StreamReaderThread		stderr = new StreamReaderThread("executeShell", "stderr", proc.getErrorStream(), System.out);
			stderr.setDumpDisabled(false);
			
			stdout.start();
			stderr.start();
		}
		catch (IOException e)
		{
			throw new ApiException(e);
		}
	}
	
	public void stop() throws ApiException
	{
		if ( proc != null )
			proc.destroy();
	}

	static public List<String> executeShell(String[] command, InputStream is) throws IOException, InterruptedException, ApiException
	{
		return executeShell(StringUtils.join(command, " "), is);
	}
	
	static public List<String> executeShellCatch(String command, InputStream is) throws ApiException
	{
		return executeShellCatch(command, is, true);
	}
	
	static public List<String> executeShellCatch(String command, InputStream is, boolean throwException) throws ApiException
	{
		try
		{
			return executeShell(command, is, throwException);
		}
		catch (ApiException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new ApiException(e);
		}
	}
	
	static public List<String> executeShell(String command, InputStream is) throws IOException, InterruptedException, ApiException
	{
		return executeShell(command, is, true);
	}

	static public List<String> executeShell(String command, InputStream is, boolean throwException) throws IOException, InterruptedException, ApiException
	{
		log.info("command: " + command);
		
		Process					proc = Runtime.getRuntime().exec(command);
		StreamReaderThread		stdout = new StreamReaderThread("executeShell", "stdout", proc.getInputStream(), System.out);
		StreamReaderThread		stderr = new StreamReaderThread("executeShell", "stderr", proc.getErrorStream(), System.out);
		
		if ( is != null )
			StreamUtils.copy(is, proc.getOutputStream());
		
		stdout.start();
		stderr.start();
		int				result = proc.waitFor();
		log.debug("result: " + result);
		
		stdout.join();
		
		// no need to wait to stderr?
		if ( waitForStderr )
			stderr.join();
		
		int				errno = proc.waitFor();
		if ( errno != 0 )
		{
			if ( throwException )
				throw new ApiException(ApiConst.API_ERR_INTERNAL, "executeShell failed with: errno=" + errno + " on: " + command);
			else 
				log.error("failed with errno: " + errno);
		}
		
		return stdout.getLines();
	}

	public File getWorkingFolder() {
		return workingFolder;
	}

	public void setWorkingFolder(File workingFolder) {
		this.workingFolder = workingFolder;
	}
}
