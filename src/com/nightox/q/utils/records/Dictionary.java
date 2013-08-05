package com.nightox.q.utils.records;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class Dictionary<V> extends LinkedHashMap<String, V>{

	public String toStringx()
	{
		StringBuilder		sb = new StringBuilder();
		
		for ( String key : keySet() )
		{
			V			value = get(key);
			
			sb.append(key + " = ");
			if ( value instanceof Collection || value instanceof Map )
				sb.append("\n");
			sb.append(value + "\n");
		}
		
		return sb.toString();
	}
}
