package com.nightox.q.types;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QTypeManager {

	private Map<String, IQType>		types;

	public Map<String, IQType> getTypes() {
		return types;
	}

	public void setTypes(Map<String, IQType> types) {
		this.types = types;
	}
	
	public void setTypesList(List<IQType> types)
	{
		if ( this.types == null )
			this.types = new LinkedHashMap<String, IQType>();
		
		for ( IQType type : types )
			this.types.put(type.getDataType(), type);
		
	}
	
	public Collection<String> getDataTypes()
	{
		return types.keySet();
	}
	
	public Collection<IQType> getQTypes()
	{
		return types.values();
	}
	
	public IQType getQType(String key)
	{
		return types.get(key);
	}
	
	public Map<String, String> getDisplayNamesMap()
	{
		Map<String, String>		map = new LinkedHashMap<String, String>();
		
		for ( Map.Entry<String, IQType> entry : types.entrySet() )
			map.put(entry.getKey(), entry.getValue().getDisplayName());
		
		return map;
	}
}
