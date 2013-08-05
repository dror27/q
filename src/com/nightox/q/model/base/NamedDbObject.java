package com.nightox.q.model.base;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class NamedDbObject extends DbObject {

	private String		displayName;

	public Map<String, Object> getMapRepresentation(int level) 
	{
		Map<String, Object>			map = new LinkedHashMap<String, Object>(super.getMapRepresentation(level));
		
		mapProp(map, "displayName", getDisplayName());
		
		return map;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
}
