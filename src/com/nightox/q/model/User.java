package com.nightox.q.model;

import java.util.LinkedHashMap;
import java.util.Map;

import com.nightox.q.model.base.AccountNamedDbObject;

public class User extends AccountNamedDbObject {

	private int			role;
	
	private String		username;
	private String		password;
	
	private	String		timeZone;
	
	public Map<String, Object> getMapRepresentation(int level) 
	{
		Map<String, Object>			map = new LinkedHashMap<String, Object>(super.getMapRepresentation(level));
		
		map.put("role", getRole());
		
		if ( getUsername() != null )
			map.put("username", getUsername());

		if ( getTimeZone() != null )
			map.put("timeZone", getTimeZone());
		
		return map;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	
}


