package com.nightox.q.utils.text;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.nightox.q.api.ApiException;
import com.nightox.q.utils.records.Dictionary;
import com.nightox.q.utils.records.Record;

public class AirodumpNGParser extends DictionaryParserBase {

	static private Set<String>			spaceWipeFields = new LinkedHashSet<String>();
	static {
		spaceWipeFields.add("LAN IP");
	}
	
	@Override
	public Dictionary<Record> parse(Iterator<String> iter) throws ApiException
	{
		Dictionary<Record>		dict = new Dictionary<Record>();
		String[]				header = null;
		
		// loop until end of input
		while ( iter.hasNext() )
		{
			// get line, if starts on first column, its a key (new record)
			String				line = iter.next();
			line = line.trim();
			if ( line.length() == 0 )
				continue;
			
			// split
			String[]	toks = StringUtils.split(line, ",");
			for ( int n = 0 ; n < toks.length ; n++ )
				toks[n] = toks[n].trim();
			
			if ( toks.length <= 0 )
				continue;
			if ( toks[0].equals("BSSID") || toks[0].equals("Station MAC") )
			{
				header = toks;
				continue;
			} else if ( header == null )
				continue;
			
			// create new record
			Record		record = new Record();
			String		key = toks[0];
			dict.put(key,  record);
			
			// extract info
			int			size = Math.min(header.length, toks.length);
			for ( int n = 0 ; n < size ; n++ )
			{
				String		value = toks[n];
				
				if ( spaceWipeFields.contains(header[n]) )
					value = value.replace(" ", "");
				
				record.put(header[n], value);
			}
			
			// append extra fields to last
			String			lastField = header[size - 1];
			for ( int n = size ; n < toks.length ; n++ )
			{
				String		value = toks[n];
				
				record.put(lastField, record.get(lastField) + "," + value);
			}
		}
		
		return dict;
	}
	
	public static void main(String[] args) throws IOException, ApiException
	{
		AirodumpNGParser		parser = new AirodumpNGParser();
		
		for ( String arg : args )
		{
			InputStreamLineIterator		iter = new InputStreamLineIterator(new FileInputStream(arg));
			Dictionary<Record>			dict = parser.parse(iter);

			System.out.println(arg + ":");
		
			for ( String key : dict.keySet() )
				System.out.println(key + ": " + dict.get(key));
		}
	}
}
