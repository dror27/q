package com.nightox.q.api;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Restrictions;

import com.nightox.q.db.Database;
import com.nightox.q.model.User;

public class ApiSessionManager {

	private static Log						log = LogFactory.getLog(ApiSessionManager.class);
	
	private Map<String, ApiSession>			apiSessions = new LinkedHashMap<String, ApiSession>();
	private int								staleSessionInterval;		// in seconds
	private Map<Long, String>				apiThreadSesionIds = new LinkedHashMap<Long, String>();
	
	public synchronized ApiSession getApiSession(String id)
	{
		ApiSession		apiSession = null;
		
		if ( id != null && apiSessions.containsKey(id) )
			apiSession = apiSessions.get(id);
		else
		{
			apiSession = new ApiSession();
			
			if ( id != null && id.indexOf(":") > 0 )
			{
				String[]		toks = id.split(":");
				String			username = toks[0];
				String			password = toks[1];
				User			user;
				
				if ( ApiSession.isInternalUser(username, password) )
					user = ApiSession.getInternalUser();
				else
					user = (User)Database.getSession().createCriteria(User.class)
											.add(Restrictions.eq("username", toks[0]))
											.add(Restrictions.eq("password", toks[1]))
											.uniqueResult();
				apiSession.setUser(user);
			}
		}
		
		apiSessions.put(apiSession.getId(), apiSession);
		apiSession.touch();
		
		apiThreadSesionIds.put(Thread.currentThread().getId(), apiSession.getId());
		
		return apiSession;
	}
	
	public synchronized ApiSession getCurrentThreadApiSession()
	{
		String			sessionId = apiThreadSesionIds.get(Thread.currentThread().getId());
		
		return getApiSession(sessionId);
	}
	
	public synchronized void gc()
	{
		List<String>		stale = new LinkedList<String>();
		Date				now = new Date();
		long				fence = now.getTime() - staleSessionInterval * 1000;
		int					sizeBefore = apiSessions.size();
		
		for ( ApiSession session : apiSessions.values() )
			if ( session.getLastTouched().getTime() < fence )
			{
				session.cleanup();
				stale.add(session.getId());
			}
		
		for ( String id : stale )
			apiSessions.remove(id);
		
		log.info("session gc: " + sizeBefore + " -> " + apiSessions.size());
	}

	public void setStaleSessionInterval(int staleSessionInterval) {
		this.staleSessionInterval = staleSessionInterval;
	}
}
