package com.nightox.q.html;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.text.DecimalFormat;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;

import com.nightox.q.model.m.Q;

public class ImgRenderer {

	static private Set<String>		imageContentTypes = new LinkedHashSet<String>();
	private boolean					useImageSize = false;
	
	static {
		imageContentTypes.add("image/jpg");
		imageContentTypes.add("image/jpeg");
		imageContentTypes.add("image/png");
		imageContentTypes.add("image/gif");
		imageContentTypes.add("image/tiff");
	}
	
	public boolean canRender(Q q)
	{
		return !StringUtils.isEmpty(q.getContentType()) && (q.getBinaryData() != null);
	}
	
	public static boolean isImageContentType(String contentType)
	{
		return contentType != null && imageContentTypes.contains(contentType);
	}
	
	public boolean isImageQ(Q q)
	{
		return isImageContentType(q.getContentType());
	}
	
	public String renderHtml(Q q)
	{
		String			url = q.getQ() + "?image";
		if ( q.getVersion() != null )
			url += "&version=" + q.getVersion();
		
		if ( isImageContentType(q.getContentType()) )
		{
			Dimension		imageSize = getImageSize(q);
			
			String			widthAttr = "";
			if ( imageSize == null || imageSize.width > 300 )
				widthAttr = "width=\"100%\"";
			
			return String.format("<div class=\"data_img\"><img x-width=\"0\" %s src=\"%s\"/></div>", widthAttr, url);
		}
		else
		{
			DecimalFormat		format = new DecimalFormat();
			String				text = "";
			
			if ( q.getContentType() != null )
				text += q.getContentType();
			
			if ( q.getBinaryData() != null )
				text += " " + format.format(q.getBinaryData().length) + " bytes";
	
			text = text.trim();
			
			return String.format("<div class=\"data_other\"><a href=\"%s\"/>%s</a></div>" ,url, text);
		}
	}

	public Dimension getImageSize(Q q) 
	{
		if ( !isUseImageSize() )
			return null;
		
		if ( !isImageQ(q) )
			return null;
		byte[]		bytes = q.getBinaryData();
		if ( bytes == null )
			return null;
		try
		{
			BufferedImage		image = ImageIO.read(new ByteArrayInputStream(bytes));
			if ( image == null )
				return null;
			
			Dimension			dim = new Dimension();
			dim.width = image.getWidth();
			dim.height = image.getHeight();
			
			return dim;
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public boolean isUseImageSize() {
		return useImageSize;
	}

	public void setUseImageSize(boolean useImageSize) {
		this.useImageSize = useImageSize;
	}
}
