package com.nightox.q.types.spec;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.commons.fileupload.FileItem;

import com.nightox.q.model.m.Q;
import com.nightox.q.types.QTypeBase;

public class QLinkType extends QTypeBase {

	private List<QLinkTypeSpec>		specs;
	
	@Override
	public void setUploadItems(Q q, Map<String, FileItem> items) throws IOException
	{
		q.setTextData(items.get("link").getString().trim());
	}

	@Override
	public String renderHtml(Q q) 
	{
		String				link = q.getTextData();
		if ( link == null )
			return "";
		
		// scan specs
		for ( QLinkTypeSpec spec : specs )
		{
			Matcher		m = spec.getPattern().matcher(link);
			if ( spec.isFind() ? m.find() : m.matches() )
			{
				// replace vars
				String		html = spec.getHtml();
				for ( int n = 0 ; n <= 9 && n <= m.groupCount() ; n++ )
				{
					String		tok = "$" + n;
					if ( html.contains(tok) )
					{
						String		v = m.group(n);
						if ( spec.isUrlEncode() )
							try {
								v = URLEncoder.encode(v, "UTF-8");
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
						html = html.replace(tok, v);
					}
				}
				
				return html;
			}
		}
		
		// default 
		return "<a href=\"" + link + "\">" + link + "</a>";
	}

	public List<QLinkTypeSpec> getSpecs() {
		return specs;
	}

	public void setSpecs(List<QLinkTypeSpec> specs) {
		this.specs = specs;
	}

}
