package com.nightox.q.db;

import org.springframework.core.io.Resource;

public class ResourceBean {

	private Resource		resource;
	private String			contentType;

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}
	
	
}
