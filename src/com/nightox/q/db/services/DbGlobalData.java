package com.nightox.q.db.services;

import java.util.Date;

import org.hibernate.criterion.Restrictions;

import com.nightox.q.beans.Factory;
import com.nightox.q.db.Database;
import com.nightox.q.model.Refvalue;
import com.nightox.q.model.base.DbObject;
import com.nightox.q.utils.DateUtils;

public class DbGlobalData {
	
	static private final String	MY_TYPE = "DbGlobalData";

	public static DbGlobalData getInstance()
	{
		return Factory.getInstanceEnvironment().getDbGlobalData();
	}
	
	public synchronized DbObject getGlobalDataObjectForClass(Class<?> clazz)
	{
		return getGlobalDataObjectForName(getClassName(clazz));
	}
	
	public synchronized DbObject getGlobalDataObjectForName(String name)
	{
		Refvalue			obj = (Refvalue)Database.getSession().createCriteria(Refvalue.class)
									.add(Restrictions.eq("displayName", name))
									.add(Restrictions.eq("type", MY_TYPE))
									.uniqueResult();
		if ( obj == null )
		{
			obj = new Refvalue();
			obj.setDisplayName(name);
			obj.setType(MY_TYPE);
			obj.setCode(DateUtils.formatDate(new Date()));
		}
		
		return obj;
	}
	
	public synchronized String getGlobalDataPropForClass(Class<?> clazz, String propName, String defaultValue)
	{
		return getGlobalDataPropForName(getClassName(clazz), propName, defaultValue);
	}

	public synchronized String getGlobalDataPropForName(String name, String propName, String defaultValue) 
	{
		return getGlobalDataObjectForName(name).getProp(propName, defaultValue);
	}
	
	public synchronized void setGlobalDataPropForClass(Class<?> clazz, String propName, String propValue)
	{
		 setGlobalDataPropForName(getClassName(clazz), propName, propValue);
	}

	public synchronized void setGlobalDataPropForName(String name, String propName, String propValue) 
	{
		 getGlobalDataObjectForName(name).setProp(propName, propValue);
	}
	
	private String getClassName(Class<?> clazz)
	{
		return clazz.getSimpleName();
	}

}
