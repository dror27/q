package com.nightox.q.db;

import java.io.InputStream;


public interface ISessionManager {

	IDatabaseSession		pushThreadSession();
	IDatabaseSession 		peekThreadSession();
	void 					popThreadSession();
	void 					commitThreadSession(Object obj);
	void					executeSql(InputStream is);
}
