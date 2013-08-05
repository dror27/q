package com.nightox.q.inits;

import com.nightox.q.api.ApiContext;
import com.nightox.q.api.ApiException;
import com.nightox.q.servlets.AppApi;
import com.nightox.q.utils.records.Dictionary;
import com.nightox.q.utils.records.Record;

public class AirmonInitRunnable implements Runnable {
	
	@Override
	public void run() 
	{
		try
		{
			// enumerate interface, stop monitoring interfaces
			ApiContext				context = AppApi.executeApiCommand("/airmon/listraw");
			@SuppressWarnings("unchecked")
			Dictionary<Record>		dict = (Dictionary<Record>)context.getApiResponse().getFields().get("listraw");
			for ( String name : dict.keySet() )
			{
				if ( name.matches("mon[0-9]+") )
				{
					AppApi.executeApiCommand("/airmon/stop?name=" + name);
				}
			}
		}
		catch (ApiException e)
		{
			new RuntimeException(e);
		}
	}

}
