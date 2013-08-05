package com.nightox.q.beans;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import com.nightox.q.api.ApiException;
import com.nightox.q.api.ApiSession;
import com.nightox.q.api.ApiSessionManager;
import com.nightox.q.api.IApiCommand;
import com.nightox.q.db.services.DbGlobalData;
import com.nightox.q.jobs.SchedulerJob;
import com.nightox.q.timer.TimerManager;

public class Environment {
	
	public static class ApiCommandsMap 
	{
		private Map<String, IApiCommand>		apiCommands;

		public Map<String, IApiCommand> getApiCommands() {
			return apiCommands;
		}

		public void setApiCommands(Map<String, IApiCommand> apiCommands) {
			this.apiCommands = apiCommands;
		}
	}
	
	private List<Object>					databaseInitData;
	private TimerManager					timerManager;
	private ApiSessionManager				apiSessionManager;
	private Map<String, IApiCommand>		apiCommands;
	
	private String							apiDateFormatString;
	
	private	SchedulerJob					schedulerJob;
	
	private List<Runnable>					initRunnables;
	
	private Services						services;
	
	private DbGlobalData					dbGlobalData = new DbGlobalData();
	
	private String							tmpFolder;
	
	private String							shellCommandPrefix;
		
	public void setup() throws ApiException {
		getTimerManager().startManager();
	}

	public void teardown() throws ApiException {
		getTimerManager().stopManager();
	}
	
	public List<Object> getDatabaseInitData() {
		return databaseInitData;
	}

	public void setDatabaseInitData(List<Object> databaseInitData) {
		this.databaseInitData = databaseInitData;
	}

	public TimerManager getTimerManager() {
		return timerManager;
	}

	public void setTimerManager(TimerManager timerManager) {
		this.timerManager = timerManager;
	}

	public ApiSessionManager getApiSessionManager() {
		return apiSessionManager;
	}

	public void setApiSessionManager(ApiSessionManager apiSessionManager) {
		this.apiSessionManager = apiSessionManager;
	}

	public Map<String, IApiCommand> getApiCommands() {
		return apiCommands;
	}

	public void setApiCommands(Map<String, IApiCommand> apiCommands) {
		this.apiCommands = apiCommands;
	}

	public void setApiCommandsList(List<ApiCommandsMap> apiCommandsList) {
		this.apiCommands = new LinkedHashMap<String, IApiCommand>();
		for ( ApiCommandsMap apiCommands : apiCommandsList )
			this.apiCommands.putAll(apiCommands.getApiCommands());
	}
	
	public DateFormat getApiDateFormat(TimeZone timeZone) {
		
		SimpleDateFormat		dateFormat = new SimpleDateFormat(apiDateFormatString);

		if ( timeZone == null )
		{
			ApiSession		session = getApiSessionManager().getCurrentThreadApiSession();
			
			timeZone = session.getTimeZone();
		}
		
		dateFormat.setTimeZone(timeZone);

		return dateFormat;
	}

	public SchedulerJob getSchedulerJob() {
		return schedulerJob;
	}

	public void setSchedulerJob(SchedulerJob schedulerJob) {
		this.schedulerJob = schedulerJob;
	}

	public void setApiDateFormatString(String apiDateFormatString) {
		this.apiDateFormatString = apiDateFormatString;
	}

	public List<Runnable> getInitRunnables() {
		return initRunnables;
	}

	public void setInitRunnables(List<Runnable> initRunnables) {
		this.initRunnables = initRunnables;
	}

	public Services getServices() {
		return services;
	}

	public void setServices(Services services) {
		this.services = services;
	}

	public DbGlobalData getDbGlobalData() {
		return dbGlobalData;
	}

	public void setDbGlobalData(DbGlobalData dbGlobalData) {
		this.dbGlobalData = dbGlobalData;
	}

	public String getTmpFolder() {
		return tmpFolder;
	}

	public void setTmpFolder(String tmpFolder) {
		this.tmpFolder = tmpFolder;
	}

	public String getShellCommandPrefix() {
		return shellCommandPrefix;
	}

	public void setShellCommandPrefix(String shellCommandPrefix) {
		this.shellCommandPrefix = shellCommandPrefix;
	}
	
	public String getShellCommand(String cmd)
	{
		return getShellCommand(cmd, true);
	}
	
	public String getShellCommand(String cmd, boolean sudo)
	{
		StringBuilder		sb = new StringBuilder();
		
		if ( !StringUtils.isEmpty(shellCommandPrefix) )
		{
			sb.append(shellCommandPrefix);
			sb.append(" ");
		}
		
		if ( sudo )
			sb.append("sudo ");
		
		sb.append(cmd);
		
		return sb.toString();
	}

}
