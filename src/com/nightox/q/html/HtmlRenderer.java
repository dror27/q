package com.nightox.q.html;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.markdown4j.Markdown4jProcessor;

import com.nightox.q.model.m.Q;
import com.nightox.q.types.spec.QLinkTypeSpec;

public class HtmlRenderer {
	
	private ImgRenderer				imgRenderer = new ImgRenderer();	
	private List<QLinkTypeSpec>		specs;
	
	public String renderHtml(Q q)
	{
		StringBuilder			sb = new StringBuilder();
		
		// has image?
		if ( imgRenderer.canRender(q) )
		{
			sb.append(imgRenderer.renderHtml(q));
			sb.append("\n");
		}
		
		// has text?
		String					text = q.getTextData();
		text = expandMarkdown(text);
		if ( !StringUtils.isEmpty(text) )
		{
			// prepare a place for the embeds
			List<String>		embeds = new LinkedList<String>();
			
			// ask specific types to generates embeds 
			for ( QLinkTypeSpec spec : specs )
				text = spec.processEmbeds(text, embeds);
			
			// all links must be on a new window
			text = text.replace("<a ", "<a target=\"_blank\" ");
			
			// process done, emit
			for ( String embed : embeds )
			{
				sb.append(embed);
				sb.append("\n<br/>\n");
			}
			sb.append(text);
		}
		
		
		return sb.toString();
	}

	private String expandMarkdown(String text) 
	{
		try {
			Markdown4jProcessor		mdp =  new Markdown4jProcessor();
			
			if ( !StringUtils.isEmpty(text) )
				text = mdp.process(text);				
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return text;
	}

	public List<QLinkTypeSpec> getSpecs() {
		return specs;
	}

	public void setSpecs(List<QLinkTypeSpec> specs) {
		this.specs = specs;
	}

	public ImgRenderer getImgRenderer() {
		return imgRenderer;
	}

	public void setImgRenderer(ImgRenderer imgRenderer) {
		this.imgRenderer = imgRenderer;
	}


}
