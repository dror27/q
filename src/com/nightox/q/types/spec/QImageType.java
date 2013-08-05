package com.nightox.q.types.spec;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringEscapeUtils;

import com.nightox.q.model.m.Q;
import com.nightox.q.types.QTypeBase;

public class QImageType extends QTypeBase {

	@Override
	public void setUploadItems(Q q, Map<String, FileItem> items) throws IOException
	{
		q.setTextData(items.get("title").getString().trim());
		q.setBinaryData(items.get("file").getInputStream());
		q.setContentType(items.get("file").getContentType());
	}
	
	@Override
	public String renderHtml(Q q) 
	{
		StringBuilder		sb = new StringBuilder();
		
		if ( q.getTextData() != null )
			sb.append("<h2>" + StringEscapeUtils.escapeHtml(q.getTextData()) + "</h2>");
		
		if ( q.getBinaryData() != null && q.getContentType() != null )
			sb.append("<img src=\"?image\"/>");
		
		return sb.toString();
	}

}
