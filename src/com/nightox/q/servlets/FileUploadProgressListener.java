package com.nightox.q.servlets;

import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileUploadProgressListener implements ProgressListener {

	static final Log			log = LogFactory.getLog(FileUploadProgressListener.class);
	
	private long				bytesRead;
	private long				contentLength;
	private int					items;
	
	@Override
	public void update(long pBytesRead, long pContentLength, int pItems) 
	{
		log.info("bytes: " + pBytesRead + ", length: " + pContentLength + ", items: " + pItems);
		
		bytesRead = pBytesRead;
		contentLength = pContentLength;
		items = pItems;
	}

	public long getBytesRead() {
		return bytesRead;
	}

	public long getContentLength() {
		return contentLength;
	}

	public int getItems() {
		return items;
	}
	
}
