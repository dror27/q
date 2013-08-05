package com.nightox.q.model.base;

import java.util.LinkedHashMap;
import java.util.Map;

import com.nightox.q.model.Account;

public abstract class AccountNamedDbObject extends NamedDbObject implements IHasAccount {

	private Account			account;
	
	public Map<String, Object> getMapRepresentation(int level) 
	{
		Map<String, Object>			map = new LinkedHashMap<String, Object>(super.getMapRepresentation(level));
		
		if ( getAccount() != null )
		{
			if ( level == LEVEL_SHALOW )
				map.put("accountId", getAccount().getId());
			else if ( level == LEVEL_NEST )
				map.put("account", getAccount().getMapRepresentation(LEVEL_SHALOW));
		}
		
		return map;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}
}
