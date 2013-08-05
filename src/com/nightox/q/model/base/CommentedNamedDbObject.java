package com.nightox.q.model.base;

import java.util.LinkedHashMap;
import java.util.Map;

public class CommentedNamedDbObject extends NamedDbObject {

	private String	comment;

	public Map<String, Object> getMapRepresentation(int level) 
	{
		Map<String, Object>			map = new LinkedHashMap<String, Object>(super.getMapRepresentation(level));
		
		mapProp(map, "comment", getComment());

		return map;
	}
		
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	
}
