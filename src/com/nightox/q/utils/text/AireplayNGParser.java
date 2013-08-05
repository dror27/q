package com.nightox.q.utils.text;

import java.util.Iterator;
import java.util.regex.Pattern;

import com.nightox.q.api.ApiException;

public class AireplayNGParser extends PatternDictionaryParser {

	protected enum LineType
	{
		T_STARTUP,						// Trying broadcast probe requests...
		T_INJECTION_WORKING,			// Injection is working!
		T_FOUND_APS,					// Found 5 APs
		T_TRYING,						// Trying directed probe requests...
		T_AP_STARTING,					// 00:09:5B:5C:CD:2A - channel: 11 - 'NETGEAR'
		T_AP_PING,						// Ping (min/avg/max): 2.763ms/4.190ms/8.159ms Power: -24.30
		T_AP_DONE,						// 27/30: 90%
		T_UNKNOWN,						// (catch-all)
	}
		
	public AireplayNGParser()
	{
		String		macExpr = "\\p{XDigit}\\p{XDigit}:\\p{XDigit}\\p{XDigit}:\\p{XDigit}\\p{XDigit}:\\p{XDigit}\\p{XDigit}:\\p{XDigit}\\p{XDigit}:\\p{XDigit}\\p{XDigit}";
		String		prefix = "^\\s*(\\d\\d:\\d\\d:\\d\\d)\\s+";
		String		suffix = "\\s*$";
		
		// initialize patterns
		linePatterms.put(LineType.T_STARTUP, Pattern.compile(prefix + "Trying broadcast probe requests..." + suffix));
		linePatterms.put(LineType.T_INJECTION_WORKING, Pattern.compile(prefix + "Injection is working!" + suffix));
		linePatterms.put(LineType.T_FOUND_APS, Pattern.compile(prefix + "Found\\s+(\\d+)\\s+APs" + suffix));

		linePatterms.put(LineType.T_TRYING, Pattern.compile(prefix + "Trying directed probe requests..." + suffix));
		linePatterms.put(LineType.T_AP_STARTING, Pattern.compile(prefix + "(" + macExpr + ")\\s+-\\s+channel:\\s+(\\d+)\\s+-\\s+'([^']*)'" + suffix));
		linePatterms.put(LineType.T_AP_PING, Pattern.compile(prefix + "Ping\\s+\\(min/avg/max\\):\\s+(\\d+\\.\\d+)ms/(\\d+\\.\\d+)ms/(\\d+\\.\\d+)ms\\s+Power:\\s+(-\\d+\\.\\d+)" + suffix));
		linePatterms.put(LineType.T_AP_DONE, Pattern.compile(prefix + "(\\d+)/\\s*(\\d+):\\s+(\\d+(\\.\\d+)?)%" + suffix));

		linePatterms.put(LineType.T_UNKNOWN, Pattern.compile(".*"));
	}
	
	protected LineType matchLineType(Iterator<String> iter, String[] fields, boolean exceptionOnEOF) throws ApiException
	{
		return (LineType)super.matchLine(iter, fields, exceptionOnEOF);
	}

}
