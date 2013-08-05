package com.nightox.q.types.spec;

import java.util.regex.Pattern;

public class QLinkTypeSpec {

	private Pattern			pattern;
	private String			html;
	private boolean			find;
	private boolean			urlEncode;
	
	
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
	public boolean isFind() {
		return find;
	}
	public void setFind(boolean find) {
		this.find = find;
	}
	public boolean isUrlEncode() {
		return urlEncode;
	}
	public void setUrlEncode(boolean urlEncode) {
		this.urlEncode = urlEncode;
	}
}
