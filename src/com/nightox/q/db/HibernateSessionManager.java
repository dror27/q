package com.nightox.q.db;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

public class HibernateSessionManager implements ISessionManager {

	private HibernateDatabase					db;
	private Map<Long, Stack<IDatabaseSession>>	sessions = new LinkedHashMap<Long, Stack<IDatabaseSession>>();
	
	public synchronized IDatabaseSession peekThreadSession()
	{
		Long					key = Thread.currentThread().getId();
		Stack<IDatabaseSession>	stack = sessions.get(key);
		
		return stack != null ? stack.peek() : null;
	}
	
	public synchronized IDatabaseSession pushThreadSession()
	{
		Long					key = Thread.currentThread().getId();
		Stack<IDatabaseSession>	stack = sessions.get(key);
		
		if ( stack == null )
			sessions.put(key, stack = new Stack<IDatabaseSession>());
		
		IDatabaseSession			session = new HibernateDatabaseSession(db);
		
		stack.add(session);
		
		return session;
	}
	
	public synchronized void popThreadSession()
	{
		Long					key = Thread.currentThread().getId();
		Stack<IDatabaseSession>	stack = sessions.get(key);
		
		if ( stack != null && stack.size() > 0 )
		{
			IDatabaseSession			session = stack.pop();

			session.close();
			
			if ( stack.size() == 0 )
				sessions.remove(key);
		}
	}
	
	public synchronized void commitThreadSession(Object obj)
	{
		if ( obj != null )
			peekThreadSession().getSession().save(obj);			
		
		peekThreadSession().commit();
		popThreadSession();
		pushThreadSession();
		
		if ( obj != null )
			peekThreadSession().getSession().update(obj);
		
	}

	public void setDb(HibernateDatabase db) {
		this.db = db;
	}

	public void executeSql(InputStream is) {
		db.executeSql(is);
	}
	
}
