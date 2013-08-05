package com.nightox.q.api.cmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.nightox.q.api.ApiContext;
import com.nightox.q.api.ApiException;
import com.nightox.q.api.ApiPacket;
import com.nightox.q.api.IApiObjectCommand;
import com.nightox.q.db.Database;
import com.nightox.q.model.Refvalue;
import com.nightox.q.model.base.DbObject;

/*
 * params:
 * type			- type of refvalue
 * 
 * csv data is expected in the POST body (columns 0/1/[2] - code/displayName/description)
 */

public class CmdRefvalueImport implements IApiObjectCommand {

	public void doCommand(DbObject obj, ApiContext context) throws ApiException 
	{
		// get params
		ApiPacket				req = context.getApiRequest();
		String					type = req.getField("type");
		
		try
		{
			// loop over lines of data
			BufferedReader	reader = new BufferedReader(new InputStreamReader(context.getServletRequest().getInputStream()));
			String			line;
			@SuppressWarnings("unused")
			int				lineNumber = 0;
			while ( (line = reader.readLine()) != null )
			{
				lineNumber++;
				
				// skip empty and comment lines
				line = line.trim();
				if ( line.length() == 0 || line.charAt(0) == '#' )
					continue;
				
				// break line into tokens
				String[]	toks = line.split(",");
				
				// create refvalue
				Refvalue	refvalue = new Refvalue();
				refvalue.setType(type);
				refvalue.setCode(toks[0]);
				refvalue.setDisplayName(toks.length > 1 ? toks[1] : toks[0]);
				if ( toks.length > 2 )
					refvalue.setDescription(toks[2]);
				Database.getSession().persist(refvalue);
			}
			
			reader.close();
		}
		catch (IOException e)
		{
			throw new ApiException(e);
		}
	}
}
