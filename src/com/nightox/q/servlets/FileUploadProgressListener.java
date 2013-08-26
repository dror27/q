package com.nightox.q.servlets;

import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.ProgressListener;

public class FileUploadProgressListener implements ProgressListener {

	private HttpSession			httpSession;
	
	public FileUploadProgressListener(HttpSession httpSession)
	{
		this.httpSession = httpSession;
	}
	
	@Override
	public void update(long pBytesRead, long pContentLength, int pItems) 
	{
		httpSession.setAttribute("_upload.pBytesRead", pBytesRead);
		httpSession.setAttribute("_upload.pContentLength", pContentLength);
		httpSession.setAttribute("_upload.pItems", pItems);
	}
	
	static public long getBytesRead(HttpSession httpSession)
	{
		Number			number = (Number)httpSession.getAttribute("_upload.pBytesRead");
		
		if ( number != null )
			return number.longValue();
		else
			return 0;
	}

	static public long getContentLength(HttpSession httpSession)
	{
		Number			number = (Number)httpSession.getAttribute("_upload.pContentLength");
		
		if ( number != null )
			return number.longValue();
		else
			return 0;
	}
}
