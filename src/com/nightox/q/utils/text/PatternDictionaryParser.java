package com.nightox.q.utils.text;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.nightox.q.api.ApiException;

public abstract class PatternDictionaryParser extends DictionaryParserBase {

	protected Map<Enum<?>, Pattern>	linePatterms = new LinkedHashMap<Enum<?>, Pattern>();

	protected Enum<?> matchLine(Iterator<String> iter, String[] fields) throws ApiException
	{
		return matchLine(iter, fields, true);
	}
	
	protected Enum<?> matchLine(Iterator<String> iter, String[] fields, boolean exceptionOnEOF) throws ApiException 
	{
		String		line = getNextLine(iter, exceptionOnEOF);
		Matcher		m;
		
		Arrays.fill(fields, null);
		
		if ( line == null )
			return null;

		//System.out.println("line: " + line);
		
		// loop on patterns
		for ( Map.Entry<Enum<?>, Pattern> entry : linePatterms.entrySet() )
		{
			//System.out.println("pattern: " + entry.getValue());
			
			// extract fields
			if ( (m = entry.getValue().matcher(line)).matches() )
			{
				int			limit = Math.min(m.groupCount() + 1, fields.length);
				for ( int n = 0 ; n < limit ; n++ )
					fields[n] = m.group(n);
				
				// return line type
				return entry.getKey();
			}
		}
		
		// no match
		throw new ExpectedException("valid pattern matching", line);
	}

}
