package com.nightox.q.servlets;

import org.apache.commons.fileupload.ProgressListener;

public class FileUploadProgressListener implements ProgressListener {

	private long				bytesRead;
	private long				contentLength;
	private int					items;
	
	@Override
	public void update(long pBytesRead, long pContentLength, int pItems) 
	{
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
