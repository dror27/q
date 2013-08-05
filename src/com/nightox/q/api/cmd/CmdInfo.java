package com.nightox.q.api.cmd;

import java.util.LinkedHashMap;
import java.util.Map;

import com.nightox.q.api.ApiConst;
import com.nightox.q.api.ApiContext;
import com.nightox.q.api.ApiException;
import com.nightox.q.api.cmd.base.CmdBase;

public class CmdInfo extends CmdBase {
	
	private Map<String, String>		apiInfo = new LinkedHashMap<String, String>();

	public void doCommand(ApiContext context) throws ApiException
	{
		super.doCommand(context);

		String					type = context.getApiRequest().getField("type", "api");
		Map<String, String>		info = new LinkedHashMap<String, String>();
		
		if ( type.equals("api") )
			info.putAll(apiInfo);
		else if ( type.equals("env") )
			info.putAll(System.getenv());
		else if ( type.equals("props") )
		{
			for ( Object key : System.getProperties().keySet() )
				info.put(key.toString(), System.getProperty(key.toString()));
		}
		else
			throw new ApiException(ApiConst.API_ERR_BAD_PARAM, "type");
		
		context.getApiResponse().setField("info", info);
			
	}

	public Map<String, String> getApiInfo() {
		return apiInfo;
	}

	public void setApiInfo(Map<String, String> apiInfo) {
		this.apiInfo = apiInfo;
	}

}
