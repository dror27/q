package com.nightox.q.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import com.nightox.q.utils.text.ILineConsumer;

public class StreamReaderThread extends Thread
{
	String					runKey;
	String					channel;
	BufferedReader			reader;
	PrintStream				dumpWriter;
	ILineConsumer			lineConsumer;
	List<String>			lines = new LinkedList<String>();
	String					creatorThreadName;
	boolean					ignoreLines;
	int						limitLines = 10000;
	boolean					dumpDisabled = true;
	
	public StreamReaderThread(String runKey, String channel, InputStream is, PrintStream dumpWriter, ILineConsumer lineConsumer) throws IOException
	{
		this(runKey, channel, is, dumpWriter);
		
		this.lineConsumer = lineConsumer;
	}

	public StreamReaderThread(String runKey, String channel, InputStream is, PrintStream dumpWriter) throws IOException
	{
		this.runKey = runKey;
		this.channel = channel;
		this.reader = new BufferedReader(new InputStreamReader(is));
		this.dumpWriter = dumpWriter;
		
		this.creatorThreadName = Thread.currentThread().getName();
	}

	public void run() 
	{
		Thread.currentThread().setName("StreamReaderThread_" + Thread.currentThread().getId() + "_" + creatorThreadName + "_" + runKey);
		try
		{
			String		line;
			while ( (line = reader.readLine()) != null )
			{
				if ( dumpWriter != null && !dumpDisabled)
					dumpWriter.println("[" + Thread.currentThread().getName() + "]" + runKey + "-" + channel + ": " + line);

				if ( lineConsumer != null )
					lineConsumer.consumeLine(line);
				
				if ( !ignoreLines )
				{
					if ( limitLines > 0 )
						while ( lines.size() >= limitLines )
							lines.remove(0);
					lines.add(line);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public List<String> getLines() {
		return lines;
	}

	public void setIgnoreLines(boolean ignoreLines) {
		this.ignoreLines = ignoreLines;
	}

	public boolean isDumpDisabled() {
		return dumpDisabled;
	}

	public void setDumpDisabled(boolean dumpDisabled) {
		this.dumpDisabled = dumpDisabled;
	}

	public int getLimitLines() {
		return limitLines;
	}

	public void setLimitLines(int limitLines) {
		this.limitLines = limitLines;
	}

	public ILineConsumer getLineConsumer() {
		return lineConsumer;
	}

	public void setLineConsumer(ILineConsumer lineConsumer) {
		this.lineConsumer = lineConsumer;
	}
}
	