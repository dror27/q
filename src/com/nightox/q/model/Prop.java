package com.nightox.q.model;

import java.util.LinkedHashMap;
import java.util.Map;

import com.nightox.q.api.ApiException;
import com.nightox.q.db.HibernateCodeWrapper;
import com.nightox.q.model.base.DbObject;

public class Prop extends DbObject {
	
	private String		name;
	private String		value;
	private int			objectId;
	private String		objectClass;
	
	public Map<String, Object> getMapRepresentation(int level) 
	{
		Map<String, Object>			map = new LinkedHashMap<String, Object>(super.getMapRepresentation(level));
		
		map.put("name", getName());
		if ( getValue() != null )
			map.put("value", getValue());
		if ( getObjectId() > 0 )
			map.put("objectId", getObjectId());
		if ( getObjectClass() != null )
			map.put("objectClass", getObjectClass());
		
		return map;
	}

	static public String getGlobalProp(final String name)
	{
		try
		{
			if ( HibernateCodeWrapper.isWrapped() )
			{
				Prop			prop = (Prop)DbObject.getByProperty(Prop.class, "name", name, false);
			
				return (prop != null) ? prop.getValue() : null;
			}
			else
			{
				return (String)(new HibernateCodeWrapper() 
				{
					
					@Override
					protected Object code() throws Exception {
						Prop			prop = (Prop)DbObject.getByProperty(Prop.class, "name", name, false);
						
						return (prop != null) ? prop.getValue() : null;
					}
				}).execute();
			}
		}
		catch (ApiException e)
		{
			return null;
		}
		
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getObjectId() {
		return objectId;
	}

	public void setObjectId(int objectId) {
		this.objectId = objectId;
	}

	public String getObjectClass() {
		return objectClass;
	}

	public void setObjectClass(String objectClass) {
		this.objectClass = objectClass;
	}
		


}
