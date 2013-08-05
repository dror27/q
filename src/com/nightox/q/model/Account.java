package com.nightox.q.model;

import java.util.LinkedHashMap;
import java.util.Map;

import com.nightox.q.model.base.NamedDbObject;

public class Account extends NamedDbObject {

	private int			role;
	
	public Map<String, Object> getMapRepresentation(int level) 
	{
		Map<String, Object>			map = new LinkedHashMap<String, Object>(super.getMapRepresentation(level));
	
		map.put("role", getRole());
		
		return map;
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}
}
