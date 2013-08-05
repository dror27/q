package com.nightox.q.db;

import java.util.Map;

import org.hibernate.Session;

import com.nightox.q.app.AppObjectsManager;

public interface IDatabaseSession {
	
	void		commit();
	void		rollback();
	
	void		close();
	
	Session		getSession();
	
	void		onCommit(IPostCommand command);
	
	AppObjectsManager getAppObjectsManager();
	Map<Object, Object>	getSessionObjectCache();
}
