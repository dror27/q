package com.nightox.q.db;

public abstract class HibernateCodeWrapper {
	
	protected abstract Object		code() throws Exception;
	
	public Object execute()
	{
		if ( isWrapped() )
		{
			try {
				return code();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		ISessionManager				sessionManager = Database.getInstance().getSessionManager();
		IDatabaseSession			databaseSession = sessionManager.pushThreadSession();

		try
		{
			return code();
		} catch (Exception e)
		{
			databaseSession.rollback();
			
			throw new RuntimeException(e);
		}
		finally
		{
			sessionManager.popThreadSession();
		}
	}
	
	static public boolean isWrapped()
	{
		ISessionManager				sessionManager = Database.getInstance().getSessionManager();
		IDatabaseSession			databaseSession = sessionManager.peekThreadSession();
		
		return databaseSession != null;
	}
}
