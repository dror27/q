package com.nightox.q.model.m;

import java.util.LinkedHashMap;
import java.util.Map;

import com.nightox.q.model.base.DbObject;


public class Keyword extends DbObject {
	
	private String		keyword;
	
	public Map<String, Object> getMapRepresentation(int level) 
	{
		Map<String, Object>			map = new LinkedHashMap<String, Object>(super.getMapRepresentation(level));
		
		mapProp(map, "keyword", getKeyword());
				
		return map;
	}
	
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
}
