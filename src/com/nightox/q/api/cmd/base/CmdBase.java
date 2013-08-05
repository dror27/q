package com.nightox.q.api.cmd.base;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;

import com.nightox.q.api.ApiConst;
import com.nightox.q.api.ApiContext;
import com.nightox.q.api.ApiException;
import com.nightox.q.api.IApiCommand;
import com.nightox.q.beans.Factory;

public abstract class CmdBase implements IApiCommand {
	
	private static Log		log = LogFactory.getLog(CmdBase.class);
	private static String[] roles = {"guest", "user", "admin", "super", "internal"};
	
	private String			minUserRole = "user";
	private int				minUserRoleIndex;
	
	static private	Boolean	security = null;
	
	public void doCommand(ApiContext context) throws ApiException
	{
	}
	
	public void checkAuthorized(ApiContext context) throws ApiException
	{
		int			roleIndex = getRoleIndex(context.getApiSession().getUserType());
		if ( roleIndex < 0 || minUserRoleIndex < 0 )
			throwNotAuthorized();
		
		if ( roleIndex < minUserRoleIndex )
			throwNotAuthorized();
	}
		
	protected int getCountQueryResult(Criteria crit)
	{
		@SuppressWarnings("rawtypes")
		List		results = crit.list();
		int			count = 0;
		
		if ( !results.isEmpty() )
			count = ((Integer)results.get(0)).intValue();
		
		return count;
	}
	
	protected int getRoleIndex(String role)
	{
		for ( int n = 0 ; n < roles.length ; n++ )
			if ( roles[n].equals(role) )
				return n;
		
		return -1;
	}
	
	protected Log getLog()
	{
		return log;
	}

	public String getMinUserRole() {
		return minUserRole;
	}

	public void setMinUserRole(String minUserRole) {
		this.minUserRole = minUserRole;
		this.minUserRoleIndex = getRoleIndex(minUserRole);
	}	
	
	protected void throwNotAuthorized() throws ApiException
	{
		if ( securityEnabled() )
			throw new ApiException(ApiConst.API_ERR_NOT_AUTH);
		else
			getLog().debug("not authroized, but security disabled");
	}
	
	protected boolean securityEnabled()
	{
		synchronized (CmdBase.class) {
		
			if ( security == null )
				security = Boolean.parseBoolean(Factory.getInstance().getConf().getProperty("security", "true"));
		}
		
		return security;
	}
}
