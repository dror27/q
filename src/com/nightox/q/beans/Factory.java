package com.nightox.q.beans;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

import com.freebss.sprout.core.time.MyDateEditor;
import com.freebss.sprout.core.time.MyPatternEditor;

public class Factory {
	
	private static Log			log = LogFactory.getLog(Factory.class);

	private static Factory		instance;
	private Properties			conf = new FactoryProperties();
	private XmlBeanFactory		beanFactory;
	private Environment			environment;
	
	@SuppressWarnings("serial")
	private class FactoryProperties extends Properties
	{
		public String getProperty(String key)
		{
			String			hostname = null;
			try
			{
				hostname = InetAddress.getLocalHost().getHostName();
			}
			catch (UnknownHostException e)
			{
			}

			if ( hostname != null )
			{
				String		hostkey = hostname + "." + key;
				String		value = super.getProperty(hostkey);
				
				if ( value != null )
					return value;				
			}
			
			return super.getProperty(key);
		}
	}
	
	static public synchronized Factory getInstance()
	{
		if ( instance == null )
			instance = new Factory();
		
		return instance;
	}
	
	Factory()
	{
		try
		{
			// starting up
			String		hostname = "?";
			try	{ hostname = InetAddress.getLocalHost().getHostName(); } catch (UnknownHostException e) {}
			log.info("**** starting up: hostname: " + hostname);
			
			// load configuration
			conf.load((new ClassPathResource("beans/factory.properties")).getInputStream());
			
			// create bean factory
			beanFactory = new XmlBeanFactory(new ClassPathResource(conf.getProperty("conf.context_xml")));
			beanFactory.registerCustomEditor(Date.class, new MyDateEditor());
			beanFactory.registerCustomEditor(Pattern.class, new MyPatternEditor());
			PropertyPlaceholderConfigurer 	cfg = new PropertyPlaceholderConfigurer();
			cfg.setProperties(conf);
			cfg.postProcessBeanFactory(beanFactory);
			
			// load environment
			environment = (Environment)beanFactory.getBean(conf.getProperty("conf.environment_bean"), Environment.class);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public XmlBeanFactory getBeanFactory() {
		return beanFactory;
	}
	
	public Environment getEnvironment()
	{
		return environment;
	}

	public Properties getConf() {
		return conf;
	}
	
	public static Environment getInstanceEnvironment()
	{
		return Factory.getInstance().getEnvironment();
	}
	
	public static Services getServices()
	{
		return getInstanceEnvironment().getServices();
	}
	
	public static String getConfProperty(String key)
	{
		return getInstance().getConf().getProperty(key, key);
	}
	
	public static String getConfProperty(String key, String defaultValue)
	{
		return getInstance().getConf().getProperty(key, defaultValue);
	}
	
		
}
