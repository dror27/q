package com.nightox.q.html;

import java.util.LinkedHashSet;
import java.util.Set;

import com.nightox.q.model.m.Q;

public class ImgRenderer {

	static private Set<String>		imageContentTypes = new LinkedHashSet<String>();
	
	static {
		imageContentTypes.add("image/jpg");
		imageContentTypes.add("image/jpeg");
		imageContentTypes.add("image/png");
		imageContentTypes.add("image/gif");
		imageContentTypes.add("image/tiff");
	}
	
	public boolean isImageQ(Q q)
	{
		return q.getContentType() != null && imageContentTypes.contains(q.getContentType());
	}
	
	public String renderHtml(Q q)
	{
		return String.format("<div class=\"data_img\"><img x-width=\"0\" width=\"100%%\" src=\"%s?image\"/></div>", q.getQ());
	}
}
