package com.nightox.q.api;

import com.nightox.q.model.base.DbObject;

public interface IApiObjectCommand {

	void		doCommand(DbObject obj, ApiContext context) throws ApiException;
}
