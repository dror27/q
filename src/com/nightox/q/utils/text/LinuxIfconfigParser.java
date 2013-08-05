package com.nightox.q.utils.text;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.nightox.q.api.ApiException;
import com.nightox.q.utils.records.Dictionary;
import com.nightox.q.utils.records.Record;

public class LinuxIfconfigParser extends DictionaryParserBase {

	static private Set<String>			domains = new LinkedHashSet<String>();
	static private Set<String>			noColumn = new LinkedHashSet<String>();
	static private Map<String,String>	replacements = new LinkedHashMap<String,String>();	
	static {
		domains.add("Link");
		domains.add("inet");
		domains.add("inet6");
		domains.add("RX");
		domains.add("TX");
		domains.add("IEEE");
		domains.add("Retry");
		
		noColumn.add("HWaddr");
		noColumn.add("addr:");
		
		replacements.put("Access Point: ", "Access_Point:");
		replacements.put("Tx-Power=", "Tx-Power:");
		replacements.put("dBm", "");
		replacements.put("long limit", "long_limit");
		replacements.put("RTS thr", "RTS_thr");
		replacements.put("Fragment thr", "Fragment_thr");
		replacements.put("Power Management", "Power_Management");
	}
	
	@Override
	public Dictionary<Record> parse(Iterator<String> iter) throws ApiException
	{
		Dictionary<Record>		dict = new Dictionary<Record>();
		Record					record = null;
		String					domain = "";
		
		// loop until end of input
		while ( iter.hasNext() )
		{
			// get line, if starts on first column, its a key (new record)
			String				line = iter.next();
			
			// perform replacements
			for ( String key : replacements.keySet() )
				line = line.replace(key, replacements.get(key));
			
			int					index = line.indexOf(' ');
			if ( index > 0 )
			{
				String			key = line.substring(0, index);
				
				dict.put(key, record = new Record());
				line = line.substring(index);
			}
			
			// parse line into tokens on spaces
			line = line.trim();
			if ( line.length() == 0 )
				continue;
			String[]			toks = StringUtils.split(line);			
			for ( int tokIndex = 0 ; tokIndex < toks.length ; tokIndex++ )
			{
				// domain?
				if ( domains.contains(toks[tokIndex]) )
				{
					domain = toks[tokIndex];
					continue;
				}
				else if ( tokIndex == 0 )
					domain = "";
				
				// ignore?
				if ( toks[tokIndex].startsWith("(") || toks[tokIndex].endsWith(")") )
					continue;
						
				
				// split on ':'
				String[]		parts;
				if ( noColumn.contains(toks[tokIndex]) )
				{
					parts = new String[2];
					parts[0] = toks[tokIndex++].split(":")[0];
					parts[1] = toks[tokIndex];
				}
				else
					parts = StringUtils.split(toks[tokIndex], ":");
				
				// make sure record is set
				if ( record == null )
					dict.put("", record = new Record());
				
				// enter into record
				String		value = parts.length > 1 ? parts[1] : "";
				if ( domain.length() > 0 )
					record.put(domain + "_" + parts[0], value);
				else
					record.put(parts[0], value);
			}
		}
		
		return dict;
	}
	
	public static void main(String[] args) throws IOException, ApiException
	{
		LinuxIfconfigParser		parser = new LinuxIfconfigParser();
		
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
