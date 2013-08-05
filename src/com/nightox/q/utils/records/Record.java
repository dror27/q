package com.nightox.q.utils.records;

import java.util.LinkedHashMap;

@SuppressWarnings("serial")
public class Record extends LinkedHashMap<String, String>{

	public String get(String key, String defaultValue)
	{
		if ( containsKey(key) )
			return get(key);
		else
			return defaultValue;
	}
	
	public int getInt(String key, int defaultValue)
	{
		if ( containsKey(key) )
			return Integer.parseInt(get(key));
		else
			return defaultValue;
	}
	
	public double getDouble(String key, double defaultValue)
	{
		if ( containsKey(key) )
			return Double.parseDouble(get(key));
		else
			return defaultValue;
	}
	
	public String toStringx()
	{
		StringBuilder		sb = new StringBuilder();
		
		for ( String key : keySet() )
			sb.append(key + " = " + get(key) + "\n");
		
		return sb.toString();
	}
}
