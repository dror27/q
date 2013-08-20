package com.nightox.q.html;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.imageio.ImageIO;

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
		Dimension		imageSize = getImageSize(q);
		
		String			widthAttr = "";
		if ( imageSize != null && imageSize.width > 300 )
			widthAttr = "width=\"100%\"";
		
		return String.format("<div class=\"data_img\"><img x-width=\"0\" %s src=\"%s?image\"/></div>", widthAttr, q.getQ());
	}

	public Dimension getImageSize(Q q) 
	{
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
}
