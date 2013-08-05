package com.nightox.q.api;

import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import com.nightox.q.beans.Factory;
import com.nightox.q.model.Account;
import com.nightox.q.model.User;
import com.nightox.q.model.base.DbObject;

public class ApiSession {

	private String			id;
	private Date			lastTouched;
	
	private Integer			userId;
	
	public ApiSession()
	{
		this.id = UUID.randomUUID().toString();
		touch();
	}
	
	public ApiSession(String id)
	{
		this.id = id;
		touch();
	}
	
	public void touch()
	{
		this.lastTouched = new Date();
	}
	
	public void cleanup()
	{
		
	}

	public String getId() {
		return id;
	}

	public String getUserType()
	{
		try
		{
			User		user = getUser();
			
			if ( user == null )
				return "guest";
			else 
			{
				int		role = user.getRole();
				
				if ( role == 1 )
					return "admin";
				else if ( role == 2 )
					return "super";
				else if ( role == 3 )
					return "internal";
				else
					return "user";
			}
		}
		catch (ApiException e)
		{
			return "guest";
		}
	}
	
	public boolean isUserSuperAtLeast()
	{
		String		type = getUserType();
		
		return type.equals("super") || type.equals("internal");
	}
	
	public Date getLastTouched() {
		return lastTouched;
	}

	public User getUser() throws ApiException 
	{
		return userId == null ? null : 
			(userId.intValue() == 0 ? getInternalUser() : (User)DbObject.get(User.class, userId));
	}

	public void setUser(User user) {
		if ( user != null )
			this.userId = user.getId();
		else
			this.userId = null;
	}

	public TimeZone getTimeZone() 
	{
		try
		{
			User		user = getUser();
			
			if ( user == null || user.getTimeZone() == null || user.getTimeZone().length() == 0 )
				return TimeZone.getDefault();
			else
				return TimeZone.getTimeZone(user.getTimeZone());
		}
		catch (Throwable e)
		{
			return TimeZone.getDefault();
		}
	}
	
	public static User getInternalUser()
	{
		String			internalUsername = Factory.getInstance().getConf().getProperty("internal.username");
		String			internalPassword = Factory.getInstance().getConf().getProperty("internal.password");

		User			user = new User();
		user.setUsername(internalUsername);
		user.setPassword(internalPassword);
		user.setRole(3);
		user.setAccount(new Account());
		
		return user;
	}
	
	public static boolean isInternalUser(String username, String password) 
	{
		String			internalUsername = Factory.getInstance().getConf().getProperty("internal.username");
		String			internalPassword = Factory.getInstance().getConf().getProperty("internal.password");

		return username.equals(internalUsername) && password.equals(internalPassword);
	}
	
	public static String getInternalSessionString()
	{
		String			internalUsername = Factory.getInstance().getConf().getProperty("internal.username");
		String			internalPassword = Factory.getInstance().getConf().getProperty("internal.password");

		return internalUsername + ":" + internalPassword;
	}
}
