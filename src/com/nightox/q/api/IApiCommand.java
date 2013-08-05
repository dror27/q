package com.nightox.q.api;

public interface IApiCommand {

	void 		checkAuthorized(ApiContext context) throws ApiException;
	void		doCommand(ApiContext context) throws ApiException;
}
