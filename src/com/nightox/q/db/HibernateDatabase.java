package com.nightox.q.db;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.core.io.Resource;

import com.freebss.sprout.core.utils.FileUtils;
import com.nightox.q.utils.StreamReaderThread;

public class HibernateDatabase {

	private Resource			config;
	private List<Class<?>>		additionalClasses = new LinkedList<Class<?>>();	
	private List<Resource>		additionalHbms = new LinkedList<Resource>();	
	
	private Configuration		configuration;
	private SessionFactory		sessionFactory;	
	
	
	public Session open() 
	{
		try
		{
			fill();
			
			return sessionFactory.openSession();
			
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	private synchronized void fill() throws IOException
	{
		if ( configuration == null )
		{
			configuration = new Configuration();
			try
			{
				configuration.configure(config.getURL());
				
				for ( Class<?> clazz : additionalClasses )
					configuration.addClass(clazz);
				
				for ( Resource hbm : additionalHbms )
					configuration.addURL(hbm.getURL());
				
				sessionFactory = configuration.buildSessionFactory();
			}
			catch (HibernateException e)
			{
				// allow for setting a breakpoint on hibernate config failure
				throw e;
			}
		}
	}

	public void setConfig(Resource config) {
		this.config = config;
	}
	
	public void executeSql(InputStream is)
	{
		String		username = configuration.getProperty("hibernate.connection.username");
		String		password = configuration.getProperty("hibernate.connection.password");
		String		url = configuration.getProperty("hibernate.connection.url");
		String		mysql = configuration.getProperty("mysql.tool");
		
		if ( url.startsWith("jdbc:mysql:") )
		{
			String		toks[] = url.split(":")[2].split("/");
			
			String		host = toks[2];
			String		schema = toks[3];
			
			String		cmd = mysql + " --user=" + username + " --password=" + password + " --host=" + host + " " + schema;
			
			try
			{
				System.out.print("executingSql ... ");
				
				Process			proc = Runtime.getRuntime().exec(cmd);
				OutputStream	os = proc.getOutputStream();
				
				(new StreamReaderThread("executeSql", "stdout", proc.getInputStream(), System.out)).start();
				(new StreamReaderThread("executeSql", "stderr", proc.getErrorStream(), System.out)).start();
				FileUtils.copyStreams(is, os);
				os.close();
				
				int				result = proc.waitFor();
				
				System.out.println("" + result);
				
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}
	}
}
