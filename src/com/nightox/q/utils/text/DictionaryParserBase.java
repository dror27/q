package com.nightox.q.utils.text;

import java.util.Iterator;

import com.nightox.q.api.ApiConst;
import com.nightox.q.api.ApiException;
import com.nightox.q.utils.records.Dictionary;
import com.nightox.q.utils.records.Record;

public abstract class DictionaryParserBase implements IDictionaryParser {

	@SuppressWarnings("serial")
	static protected class ExpectedException extends ApiException
	{
		protected ExpectedException(String expected, String line)
		{
			super(ApiConst.API_ERR_BAD_FORMAT, "excepted: " + expected + " on line: " + line);
		}
	}
	
	@Override
	public Dictionary<Record> parse(Iterator<String> iter) throws ApiException {
		return new Dictionary<Record>();
	}

	protected String getNextLine(Iterator<String> iter) throws ApiException
	{
		return getNextLine(iter, true);
	}
	
	protected String getNextLine(Iterator<String> iter, boolean exceptionOnEOF) throws ApiException
	{
		String		line = null;
		
		while ( iter.hasNext() )
		{
			line = iter.next().trim();
			if ( line.length() != 0 )
				return line;			
		}
		
		if ( exceptionOnEOF )
			throw new ApiException(ApiConst.API_ERR_BAD_FORMAT, "unexpected EOF");
		else
			return null;		
	}
	
}
