package com.nightox.q.model;

import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import com.nightox.q.beans.Factory;
import com.nightox.q.model.base.NamedDbObject;

public class Job extends NamedDbObject {
	
	static public final int	STATUS_NONE	= 0;
	static public final int	STATUS_PENDING = 1;
	static public final int	STATUS_SCHEDULED = 2;
	static public final int	STATUS_RUNNING = 3;
	static public final int	STATUS_DONE = 4;
	static public final int	STATUS_ERROR = 5;
	
	private String		kind;
	private int			status;
	
	private Date		datetimeCreated = new Date();
	private Date		datetimePending;
	private Date		datetimeScheduled;
	private Date		datetimeStarted;
	private Date		datetimeDone;
	
	private String		message;
	private double		progress;
	
	private String		param0;
	private String		param1;
	private String		param2;
	private String		param3;
	
	private Date		datetimeNextRun;
	
	public Map<String, Object> getMapRepresentation(int level) 
	{
		Map<String, Object>			map = new LinkedHashMap<String, Object>(super.getMapRepresentation(level));
		DateFormat					df = Factory.getInstance().getEnvironment().getApiDateFormat(null);
		
		map.put("kind", getKind());
		map.put("status", getStatus());
		map.put("datetimeCreated", df.format(getDatetimeCreated()));
		if ( getDatetimePending() != null )
			map.put("datetimePending", df.format(getDatetimePending()));
		if ( getDatetimeScheduled() != null )
			map.put("datetimeScheduled", df.format(getDatetimeScheduled()));
		if ( getDatetimeStarted() != null )
			map.put("datetimeStarted", df.format(getDatetimeStarted()));
		if ( getDatetimeDone() != null )
			map.put("datetimeDone", df.format(getDatetimeDone()));
		if ( getDatetimeNextRun() != null )
			map.put("datetimeNextRun", df.format(getDatetimeNextRun()));
		
		if ( getMessage() != null )
			map.put("message", getMessage());
		map.put("progress", getProgress());
		
		if ( getParam0() != null )
			map.put("param0", getParam0());
		if ( getParam1() != null )
			map.put("param1", getParam1());
		
		return map;
	}
	
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Date getDatetimeCreated() {
		return datetimeCreated;
	}
	public void setDatetimeCreated(Date datetimeCreated) {
		this.datetimeCreated = datetimeCreated;
	}
	public Date getDatetimePending() {
		return datetimePending;
	}
	public void setDatetimePending(Date datetimePending) {
		this.datetimePending = datetimePending;
	}
	public Date getDatetimeStarted() {
		return datetimeStarted;
	}
	public void setDatetimeStarted(Date datetimeStarted) {
		this.datetimeStarted = datetimeStarted;
	}
	public Date getDatetimeDone() {
		return datetimeDone;
	}
	public void setDatetimeDone(Date datetimeDone) {
		this.datetimeDone = datetimeDone;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public double getProgress() {
		return progress;
	}
	public void setProgress(double progress) {
		this.progress = progress;
	}
	public String getParam0() {
		return param0;
	}
	public void setParam0(String param0) {
		this.param0 = param0;
	}
	public String getParam1() {
		return param1;
	}
	public void setParam1(String param1) {
		this.param1 = param1;
	}

	public Date getDatetimeScheduled() {
		return datetimeScheduled;
	}

	public void setDatetimeScheduled(Date datetimeScheduled) {
		this.datetimeScheduled = datetimeScheduled;
	}

	public String getParam2() {
		return param2;
	}

	public void setParam2(String param2) {
		this.param2 = param2;
	}

	public String getParam3() {
		return param3;
	}

	public void setParam3(String param3) {
		this.param3 = param3;
	}

	public Date getDatetimeNextRun() {
		return datetimeNextRun;
	}

	public void setDatetimeNextRun(Date datetimeNextRun) {
		this.datetimeNextRun = datetimeNextRun;
	}

}
