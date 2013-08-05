package com.nightox.q.utils.text;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import com.nightox.q.api.ApiException;
import com.nightox.q.utils.records.Dictionary;
import com.nightox.q.utils.records.Record;

public class AirmonNGParser extends DictionaryParserBase {

	@Override
	public Dictionary<Record> parse(Iterator<String> iter) throws ApiException
	{
		Dictionary<Record>		dict = new Dictionary<Record>();
		boolean					headerSeen = false;
		Record					record = null;
		
		// loop until end of input
		while ( iter.hasNext() )
		{
			// get line, if starts on first column, its a key (new record)
			String				line = iter.next();
			line = line.trim();
			if ( line.length() == 0 )
				continue;
			
			// split, skip until header line
			String[]	toks = StringUtils.split(line, "\t");
			if ( toks[0].equals("Interface") )
			{
				headerSeen = true;
				continue;
			}
			else if ( !headerSeen )
				continue;
			
			// some simple verification
			if ( toks.length < 3 )
			{
				// special case for driver concat
				if ( toks.length == 1 && toks[0].startsWith("(") && toks[0].endsWith(")") && record != null && record.get("Driver") != null )
					record.put("Driver", record.get("Driver") + " " + toks[0]);
					
				continue;
			}
			
			// create new record
			record = new Record();
			String		key = toks[0].trim();
			dict.put(key,  record);
			
			// extract info
			record.put("Chipset", toks[1].trim());
			record.put("Driver", toks[2].trim());
		}
		
		// post process driver records
		for ( String key : dict.keySet() )
		{
			String		driver = dict.get(key).get("Driver");
			if ( driver == null )
				continue;
			
			if ( driver.endsWith("(removed)") )
				dict.get(key).put("Removed", "");
			
			String		phrase = "(monitor mode enabled on ";
			int			index = driver.indexOf(phrase);
			if ( index >= 0 )
			{
				String	mon = driver.substring(index + phrase.length(), driver.length() - 1);
				dict.get(key).put("Monitor", mon);
			}
		}
		
		return dict;
	}
	
	public static void main(String[] args) throws IOException, ApiException
	{
		AirmonNGParser		parser = new AirmonNGParser();
		
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
