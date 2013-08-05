package com.nightox.q.utils.text;

import java.util.Iterator;

import com.nightox.q.api.ApiException;
import com.nightox.q.utils.records.Dictionary;
import com.nightox.q.utils.records.Record;

public interface IDictionaryParser {
	
	Dictionary<Record>		parse(Iterator<String> iter) throws ApiException;
}
