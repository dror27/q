package com.nightox.q.types;

import java.util.UUID;

public class QManager {
	
	private int			qLength = newQ().length();

	public String newQ()
	{
		return UUID.randomUUID().toString().replace("-", "");
	}
	
	public boolean isValidQ(String q)
	{
		if ( q == null || q.length() != qLength )
			return false;
		
		for ( int n = 0 ; n < qLength ; n++ )
		{
			char		ch = q.charAt(n);
			
			if ( !((ch >= '0') && (ch <= '9')) && !((ch >= 'a') && (ch <= 'f')) )
				return false;
		}
		
		return true;
	}
	
}
