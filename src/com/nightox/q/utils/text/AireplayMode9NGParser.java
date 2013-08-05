package com.nightox.q.utils.text;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import com.nightox.q.api.ApiException;
import com.nightox.q.utils.records.Dictionary;
import com.nightox.q.utils.records.Record;

public class AireplayMode9NGParser extends AireplayNGParser {

	@Override
	public Dictionary<Record> parse(Iterator<String> iter) throws ApiException
	{
		Dictionary<Record>		dict = new Dictionary<Record>();
		LineType				lineType;
		String[]				fields = new String[32];
		boolean					working = false;

		// global record will store non AP specific data
		Record					globalRecord = new Record();
		dict.put("", globalRecord);
		
		// parse started line
		lineType = matchLineType(iter, fields, true);
		if ( lineType != LineType.T_STARTUP )
			throw new ExpectedException(LineType.T_STARTUP.toString(), fields[0]);
		globalRecord.put("startedTime", fields[1]);
		
		
		// loop for global info
		while ( true )
		{
			lineType = matchLineType(iter, fields, true);
			
			if ( lineType == LineType.T_INJECTION_WORKING )
				working = true;
			else if ( lineType == LineType.T_FOUND_APS )
				globalRecord.put("APCount", fields[2]);
			else if ( lineType == LineType.T_TRYING )
				break;
		}

		// loop on APs
		while ( iter.hasNext() )
		{
			Record			record = new Record();
			
			// must start with a starting ...
			lineType = matchLineType(iter, fields, false);
			if ( lineType == null )
				break;
			if ( lineType == LineType.T_INJECTION_WORKING )
			{
				working = true;
				continue;
			}
			if ( lineType != LineType.T_AP_STARTING)
				throw new ExpectedException(LineType.T_AP_STARTING.toString(), fields[0]);
			
			// store basic fields
			record.put("mac", fields[2]);
			record.put("channel", fields[3]);
			record.put("essid", fields[4]);
			dict.put(record.get("mac"), record);
			
			// unknown? skip
			lineType = matchLineType(iter, fields, true);
			while ( lineType == LineType.T_UNKNOWN && iter.hasNext() )
				lineType = matchLineType(iter, fields, true);
			
			// has ping line?
			if ( lineType == LineType.T_AP_PING )
			{
				record.put("pingMin", fields[2]);
				record.put("pingAvg", fields[3]);
				record.put("pingMax", fields[4]);
				record.put("power", fields[5]);
				
				// move to next line
				lineType = matchLineType(iter, fields, true);
			}
			
			// must be a done line
			if ( lineType != LineType.T_AP_DONE )
				throw new ExpectedException(LineType.T_AP_DONE.toString(), fields[0]);
			record.put("receivedCount", fields[2]);
			record.put("sentCount", fields[3]);
			record.put("percentage", fields[4]);
			globalRecord.put("doneTime", fields[1]);
		}
		
		globalRecord.put("working", Boolean.toString(working));
		
		return dict;
	}
	
	public static void main(String[] args) throws IOException, ApiException
	{
		AireplayMode9NGParser		parser = new AireplayMode9NGParser();
		
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
