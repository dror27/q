package com.nightox.q.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nightox.q.db.IDatabaseSession;

public class ApiContext {

	private HttpServletRequest	servletRequest;
	private HttpServletResponse	servletResponse;
	private ApiPacket			apiRequest;
	private ApiPacket			apiResponse;
	private IDatabaseSession	databaseSession;
	private ApiSession			apiSession;
	private ApiContext			parent;
	
	public ApiContext(HttpServletRequest servletRequest, HttpServletResponse servletResponse)
	{
		this.servletRequest = servletRequest;
		this.servletResponse = servletResponse;
		
		if ( servletRequest != null )
			this.apiRequest = new ApiPacket(servletRequest);
		else
			this.apiRequest = new ApiPacket();
		
		this.apiResponse = new ApiPacket();
	}

	public ApiContext newChildContext(String[] copyRequestFieldNames) throws ApiException
	{
		ApiContext		child = new ApiContext(null, null);
		
		child.parent = this;
	
		if ( copyRequestFieldNames != null )
			child.getApiRequest().copyFields(getApiRequest(), copyRequestFieldNames);
		
		return child;
	}

	public ApiContext newChildContext() throws ApiException
	{
		return newChildContext(null);
	}

	public HttpServletRequest getServletRequest() {
		return (servletRequest != null || parent == null) ? servletRequest : parent.getServletRequest();
	}

	public void setServletRequest(HttpServletRequest servletRequest) {
		this.servletRequest = servletRequest;
	}

	public HttpServletResponse getServletResponse() {
		return (servletResponse != null || parent == null) ? servletResponse : parent.getServletResponse();
	}

	public void setServletResponse(HttpServletResponse servletResponse) {
		this.servletResponse = servletResponse;
	}

	public ApiPacket getApiRequest() {
		return (apiRequest != null || parent == null) ? apiRequest : parent.getApiRequest();
	}

	public void setApiRequest(ApiPacket apiRequest) {
		this.apiRequest = apiRequest;
	}

	public ApiPacket getApiResponse() {
		return (apiResponse != null || parent == null) ? apiResponse : parent.getApiResponse();
	}

	public void setApiResponse(ApiPacket apiResponse) {
		this.apiResponse = apiResponse;
	}

	public IDatabaseSession getDatabaseSession() {
		return (databaseSession != null || parent == null) ? databaseSession : parent.getDatabaseSession();
	}

	public void setDatabaseSession(IDatabaseSession databaseSession) {
		this.databaseSession = databaseSession;
	}

	public ApiSession getApiSession() {
		return (apiSession != null || parent == null) ? apiSession : parent.getApiSession();
	}

	public void setApiSession(ApiSession apiSession) {
		this.apiSession = apiSession;
	}
	
	public String getRequestVerb() throws ApiException
	{
		String		toks[] = getRequestVerbToks();
		
		return toks[toks.length - 1];
	}
	
	public String[] getRequestVerbToks() throws ApiException
	{
		return getRequestPath().split("/");
	}

	public String getRequestPath() throws ApiException
	{
		if ( getApiRequest().hasField("_path") )
			return getApiRequest().getField("_path");
		else
		{
			HttpServletRequest		request = getServletRequest();
			
			return request.getPathInfo();
		}
	}

	
}
