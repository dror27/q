package com.nightox.q.db;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.nightox.q.app.AppObjectsManager;

public class HibernateDatabaseSession implements IDatabaseSession {

	private Session				session;
	private Transaction			transaction;
	private List<IPostCommand>	commitCommands = new LinkedList<IPostCommand>();
	private AppObjectsManager	appObjectsManager = new AppObjectsManager();
	private Map<Object, Object>	sessionObjectCache = new LinkedHashMap<Object, Object>();


	public HibernateDatabaseSession(HibernateDatabase db)
	{
		session = db.open();
		transaction = session.beginTransaction();
	}
	
	public synchronized void commit()
	{
		if ( transaction != null )
		{
			transaction.commit();
			for ( IPostCommand command : commitCommands )
				command.execute();
			transaction = null;
		}
		if ( session != null && session.isOpen() )
		{
			session.close();
			session = null;
		}
	}
	
	public synchronized void rollback()
	{
		if ( transaction != null )
		{
			transaction.rollback();
			transaction = null;
		}
		if ( session != null && session.isOpen() )
		{
			session.close();
			session = null;
		}
	}
	
	public synchronized void close()
	{
		commit();
	}
	
	public Session getSession() {
		return session;
	}
	public Transaction getTransaction() {
		return transaction;
	}

	public void onCommit(IPostCommand command) 
	{
		commitCommands.add(command);
	}

	public AppObjectsManager getAppObjectsManager() {
		return appObjectsManager;
	}

	public Map<Object, Object> getSessionObjectCache() {
		return sessionObjectCache;
	}	
}
