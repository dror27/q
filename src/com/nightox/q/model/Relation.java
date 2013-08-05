package com.nightox.q.model;

import java.util.LinkedHashMap;
import java.util.Map;

import com.nightox.q.model.base.DbObject;

public class Relation extends DbObject {
	
	private String		type;
	private int			leftId;
	private int			rightId;
	
	public Map<String, Object> getMapRepresentation(int level) 
	{
		Map<String, Object>			map = new LinkedHashMap<String, Object>(super.getMapRepresentation(level));
		
		map.put("type", getType());
		map.put("leftId", getLeftId());
		map.put("rightId", getRightId());
		
		return map;
	}
		
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getLeftId() {
		return leftId;
	}
	public void setLeftId(int leftId) {
		this.leftId = leftId;
	}
	public int getRightId() {
		return rightId;
	}
	public void setRightId(int rightId) {
		this.rightId = rightId;
	}
	

}
