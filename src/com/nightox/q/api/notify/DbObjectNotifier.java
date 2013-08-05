package com.nightox.q.api.notify;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.freebss.sprout.banner.util.StreamUtils;
import com.nightox.q.model.base.DbObject;

public class DbObjectNotifier {
	
	final public static String 		NOTIFY_PROP_URL	= "notifyUrl";
	private static Log				log = LogFactory.getLog(DbObjectNotifier.class);
	
	static public Map<String, String> notify(DbObject obj, String operation, String message)
	{
		Map<String, String>		result = new LinkedHashMap<String, String>();
		try
		{
			// access base url
			String				baseUrl = obj.getProp(NOTIFY_PROP_URL, null);
			if ( baseUrl == null )
				return result;
			
			// build additional params
			StringBuilder		sb = new StringBuilder();
			sb.append(baseUrl);
			if ( baseUrl.contains("?") )
				sb.append("&id=" + obj.getId());
			else
				sb.append("?id=" + obj.getId());
			sb.append("&class=" + obj.getAbbrClassName());
			if ( operation != null )
				sb.append("&operation=" + URLEncoder.encode(operation, "UTF-8"));
			if ( message != null )
				sb.append("&message=" + URLEncoder.encode(message, "UTF-8"));
			
			// hit url
			URL						url = new URL(sb.toString());
			result.put("url", sb.toString());
			log.info("notify: url=" + url);
			InputStream				is = url.openStream();
			ByteArrayOutputStream	os = new ByteArrayOutputStream();
			StreamUtils.copy(is, os);
			String					response = new String(os.toByteArray());
			log.debug("notify: content=" + response);
			result.put("response", response);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			
			log.error("exception: " + e.getMessage());
			
			result.put("exception", e.getMessage());
		}
		
		return result;
	}

}
