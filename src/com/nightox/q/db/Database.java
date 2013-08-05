package com.nightox.q.db;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.springframework.core.io.ClassPathResource;

import com.nightox.q.app.AppObjectsManager;
import com.nightox.q.beans.Factory;

public class Database {
	
	static private Database		instance;
	
	private ISessionManager		sessionManager;
	
	private static Log			log = LogFactory.getLog(Database.class);


	static public synchronized Database getInstance()
	{
		if ( instance == null )
		{
			instance = new Database();
			
			// initialize database
			try
			{
				instance.initDatabase();
			} catch (IOException e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			
			Database.wrapRunnable(new Runnable() {
				
				public void run() {
					for ( Runnable runnable : Factory.getInstance().getEnvironment().getInitRunnables() )
						runnable.run();
				}
			});

		}
		
		return instance;
	}
	
	Database()
	{
		// create database
		HibernateDatabase		db = new HibernateDatabase();
		db.setConfig(new ClassPathResource(Factory.getInstance().getConf().getProperty("db.config")));
		
		// create session manager
		HibernateSessionManager sessionManager = new HibernateSessionManager();
		sessionManager.setDb(db);
		this.sessionManager = sessionManager;		
	}
	
	void initDatabase() throws IOException
	{
		Boolean		hasSchema = (Boolean)(new HibernateCodeWrapper(){
			
			protected Object code() throws Exception 
			{
				// get session
				Session				session = sessionManager.peekThreadSession().getSession();
				
				// do a test query to determine if schema is there
				boolean				hasSchema = true;
				try
				{
					@SuppressWarnings("rawtypes")
					Class		testClass = Class.forName(
									Factory.getInstance().getConf().getProperty("db.test_class"
											,"com.geshem.model.Account"));
					
					// force an access to the database. defeat object proxy
					session.get(testClass, 0);
				}
				catch (ObjectNotFoundException e)
				{
					
				}
				catch (Exception e)
				{
					hasSchema = false;
				}
				
				return hasSchema;
				
			}}).execute();
		
		log.info("hasSchema: " + hasSchema);
		
		// create schema?
		if ( !hasSchema || Boolean.parseBoolean(Factory.getInstance().getConf().getProperty("db.always_init")) )
		{
			// create schema
			InputStream		is = (new ClassPathResource(Factory.getInstance().getConf().getProperty("db.schema"))).getInputStream();
			sessionManager.executeSql(is);
			is.close();
			
			// insert init objects
			IDatabaseSession session = sessionManager.pushThreadSession();
			for ( Object obj : Factory.getInstance().getEnvironment().getDatabaseInitData() )
			{
				session.getSession().save(obj);
			}
			session.commit();
			sessionManager.popThreadSession();
			
			hasSchema = true;
			
		}
	}


	public ISessionManager getSessionManager() {
		return sessionManager;
	}
	
	public static Session getSession()
	{
		return Database.getInstance().getSessionManager().peekThreadSession().getSession();
	}
	
	public static AppObjectsManager getAppObjectsManager()
	{
		return Database.getInstance().getSessionManager().peekThreadSession().getAppObjectsManager();
	}
	
	public static Map<Object,Object> getSessionObjectCache()
	{
		return Database.getInstance().getSessionManager().peekThreadSession().getSessionObjectCache();
	}
	
	public static void commitSession(Object obj)
	{
		getSession().flush();
		Database.getInstance().getSessionManager().commitThreadSession(obj);
	}
	
	public static void wrapRunnable(Runnable runnable)
	{
		// connect database
		Database.getInstance().getSessionManager().pushThreadSession();
		
		try
		{
			try
			{					
				runnable.run();
				
				Database.getInstance().getSessionManager().peekThreadSession().commit();
			}
			catch (Throwable e)
			{
				Database.getInstance().getSessionManager().peekThreadSession().rollback();

				e.printStackTrace();
			}
		}
		finally
		{
			// release database connection
			Database.getInstance().getSessionManager().popThreadSession();
		}
	}
}
