package com.nightox.q.model;

import java.util.LinkedHashMap;
import java.util.Map;

import com.nightox.q.model.base.NamedDbObject;

public class Refvalue extends NamedDbObject {
	
	private String		type;
	private String		description;
	private String		code;
	
	public Map<String, Object> getMapRepresentation(int level) 
	{
		Map<String, Object>			map = new LinkedHashMap<String, Object>(super.getMapRepresentation(level));
		
		map.put("type", getType());
		map.put("code", getCode());
		if ( getDescription() != null )
			map.put("description", getDescription());
		
		return map;
	}
		
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	

}
