package com.nightox.q.timer;

public class TimerSpec {
	
	@SuppressWarnings("rawtypes")
	private Class		clazz;
	private String		job = "job_" + this.toString();
	private String		trigger = "trigger_" + this.toString();
	private String		group = "group_" + this.toString();
	private int			timeout = 60;
	private boolean		forever = true;
	
	public String getJob() {
		return job;
	}
	public void setJob(String job) {
		this.job = job;
	}
	public String getTrigger() {
		return trigger;
	}
	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	public boolean isForever() {
		return forever;
	}
	public void setForever(boolean forever) {
		this.forever = forever;
	}
	@SuppressWarnings("rawtypes")
	public Class getClazz() {
		return clazz;
	}
	@SuppressWarnings("rawtypes")
	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
}
