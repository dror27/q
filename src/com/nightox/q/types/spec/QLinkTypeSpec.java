package com.nightox.q.types.spec;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QLinkTypeSpec {

	private Pattern pattern;
	private String html;
	private boolean urlEncode;
	private boolean replaceWithLink = true;

	public String processEmbeds(String text, List<String> embeds) 
	{
		int			startOfs = 0;
		Matcher		m = pattern.matcher(text);
		
		while ( m.find(startOfs) )
		{
			// advance start offset for next scan
			startOfs = m.end();
			
			// generate embed
			if ( html != null )
			{
				String		embed = html;
				for ( int n = 0 ; n <= m.groupCount() ; n++ )
					if ( embed.contains("$" + n) )
					{
						String		v = m.group(n);
						if ( urlEncode )
							try {
								v = java.net.URLEncoder.encode(v, "UTF-8");
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
						
						embed = embed.replace("$" + n, v);
					}
				embeds.add(embed);
			}
			
			// replace with link
			if ( replaceWithLink )
			{
				String		url = m.group();
				if ( !url.startsWith("http://") && !url.startsWith("https://") )
					url = "http://" + url;
				
				String		link = String.format("<a href=\"%s\">%s</a>", url, m.group());
				text = text.substring(0, m.start()) + link + text.substring(m.end());
				
				int			delta = link.length() - (m.end() - m.start());
				startOfs += delta;
				m = pattern.matcher(text);
			}
		}
		
		return text;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public boolean isUrlEncode() {
		return urlEncode;
	}

	public void setUrlEncode(boolean urlEncode) {
		this.urlEncode = urlEncode;
	}

	public boolean isReplaceWithLink() {
		return replaceWithLink;
	}

	public void setReplaceWithLink(boolean replaceWithLink) {
		this.replaceWithLink = replaceWithLink;
	}

}
