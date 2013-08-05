package com.nightox.q.utils.text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class InputStreamLineIterator implements Iterator<String> {

	private BufferedReader			reader;
	private String					line = null;
	
	private static Log				log = LogFactory.getLog(InputStreamLineIterator.class);

	
	public InputStreamLineIterator(InputStream is) throws IOException
	{
		reader = new BufferedReader(new InputStreamReader(is));
	}
	
	public boolean hasNext() {
		if ( line == null )
		{
			try {
				line = reader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return line != null;
	}

	public String next() {
		if ( hasNext() )
		{
			String		tmp = line;
			
			line = null;
			
			log.debug("line: " + tmp);
			return tmp;
		}
		else
			throw new RuntimeException("eof");
	}

	public void remove() {
		throw new RuntimeException("not implemented");
	}

}
