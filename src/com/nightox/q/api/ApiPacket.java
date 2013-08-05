package com.nightox.q.api;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.exception.ConstraintViolationException;
import org.json.JSONObject;

import com.freebss.sprout.banner.util.StreamUtils;
import com.freebss.sprout.core.utils.QueryStringUtils;
import com.nightox.q.beans.Factory;
import com.nightox.q.model.base.DbObject;

public class ApiPacket {

	private Map<String, Object>			fields = new ConcurrentHashMap<String, Object>();
	
	public ApiPacket()
	{
		setField(ApiConst.API_FIELD_CODE, ApiConst.API_ERR_OK);		
	}
	
	public ApiPacket(ApiPacket other)
	{
		if ( other != null )
			this.fields.putAll(other.fields);
	}
	
	public ApiPacket(HttpServletRequest servletRequest)
	{
		// on POST, add from message body
		if ( servletRequest.getMethod().equals("POST") )
		{
			String		contentType = servletRequest.getContentType();
			if ( isApiRequestContentType(contentType) )
				try
				{
					InputStream			is = servletRequest.getInputStream();
					String				text = StreamUtils.readInputStream(is);
					JSONObject			json = new JSONObject(text);
					@SuppressWarnings("rawtypes")
					Iterator			names = json.keys();
					while ( names.hasNext() )
					{
						String		name = names.next().toString();
						
						setField(name, json.getString(name));
					}
				}
				catch (Exception e)
				{
					throw new RuntimeException(e);
				}
			
		}
		
		// get from request parameters
		Enumeration<String>		paramNames = servletRequest.getParameterNames();
		while ( paramNames.hasMoreElements() )
		{
			String			name = paramNames.nextElement();
			
			setField(name, servletRequest.getParameter(name));
		}
	}

	private boolean isApiRequestContentType(String contentType) 
	{
		if ( contentType == null )
			return true;
		
		if ( contentType.startsWith("image") || contentType.startsWith("application/octet-stream") )
			return false;
		
		return true;
	}

	public ApiPacket(ApiException exception) 
	{
		clearFields();
		setField(ApiConst.API_FIELD_CODE, exception.getErrorCode());
		if ( exception.getMessage() != null )
			setField(ApiConst.API_FIELD_TECH_MSG, exception.getMessage());
		if ( exception.getAdditionalMessage() != null )
			setField(ApiConst.API_FIELD_ADDITIONAL_MESSAGE, exception.getAdditionalMessage());
		if ( exception.getCause() != null && exception.getCause() != exception &&  exception.getCause().getMessage() != null )
			setField(ApiConst.API_FIELD_CAUSE_MSG, exception.getCause().getMessage());
			
	}
	
	public ApiPacket(String queryString)
	{
		if ( queryString != null )
		{
			Map<String, String>		map = QueryStringUtils.decode(queryString);
			
			for ( String key : map.keySet() )
				fields.put(key, map.get(key));
		}
	}

	public ApiPacket(Throwable exception) 
	{
		clearFields();
		
		if ( exception instanceof ConstraintViolationException )
		{
			setField(ApiConst.API_FIELD_CODE, ApiConst.API_ERR_DB_CONSTRAINT);
			
			ConstraintViolationException		e = (ConstraintViolationException)exception;
			if ( e.getConstraintName() != null )
				setField(ApiConst.API_FIELD_CONSTRAINT_NAME, e.getConstraintName());
			
			String 			message = e.getCause().getMessage();
			Pattern			pattern = Pattern.compile("Duplicate entry '([^']*)' for key '([^']*)'");
			Matcher			matcher = pattern.matcher(message);
			
			setField(ApiConst.API_FIELD_CONSTRAINT_MESSAGE, message);
			if ( matcher.matches() )
			{
				setField(ApiConst.API_FIELD_CONSTRAINT_VALUE, matcher.group(1));
				setField(ApiConst.API_FIELD_CONSTRAINT_KEY, matcher.group(2));
			}
		}
		else
		{
			setField(ApiConst.API_FIELD_CODE, ApiConst.API_ERR_EXCEPTION);
			if ( exception.getMessage() != null )
				setField(ApiConst.API_FIELD_TECH_MSG, exception.getMessage());
		}

		if ( exception.getMessage() != null )
			setField(ApiConst.API_FIELD_TECH_MSG, exception.getMessage());			
	}
	
	public String format()
	{
		JSONObject		json = new JSONObject(fields);
		
		return json.toString();
	}
	
	public Map<String, Object> getFields()
	{
		return this.fields;
	}
	
	public String formatText()
	{
		StringBuffer		sb = new StringBuffer();
		
		try
		{
			for ( Map.Entry<String, Object> field : fields.entrySet() )
				if ( field.getValue() != null )
				{
					
					sb.append(field.getKey());
					sb.append(": ");
					sb.append(URLEncoder.encode(field.getValue().toString(), "UTF-8"));
					sb.append("\n");
				}
		}
		catch (UnsupportedEncodingException exception)
		{
			throw new RuntimeException(exception);
		}
		
		return sb.toString();
	}
	
	public void clearFields()
	{
		fields.clear();
	}
	
	public void removeField(String name)
	{
		fields.remove(name);
	}
	
	public boolean hasField(String name)
	{
		return fields.containsKey(name);
	}
	
	public Collection<String> getFieldNames()
	{
		return fields.keySet();
	}
	
	public String getField(String name) throws ApiException
	{
		if ( fields.containsKey(name) )
			return fields.get(name).toString();
		else
			throw new ApiException(ApiConst.API_ERR_MISSING_PARAM, name);
	}
	
	public int getIntegerField(String name) throws ApiException
	{
		if ( fields.containsKey(name) )
			return new Integer(fields.get(name).toString()).intValue();
		else
			throw new ApiException(ApiConst.API_ERR_MISSING_PARAM, name);
	}
	
	public int getIntegerField(String name, int defaultValue)
	{
		if ( fields.containsKey(name) )
			return new Integer(fields.get(name).toString()).intValue();
		else
			return defaultValue;
	}
	
	public long getLongField(String name) throws ApiException
	{
		if ( fields.containsKey(name) )
			return new Long(fields.get(name).toString()).longValue();
		else
			throw new ApiException(ApiConst.API_ERR_MISSING_PARAM, name);
	}
	
	public long getLongField(String name, int defaultValue)
	{
		if ( fields.containsKey(name) )
			return new Long(fields.get(name).toString()).longValue();
		else
			return defaultValue;
	}
	
	public double getDoubleField(String name) throws ApiException
	{
		if ( fields.containsKey(name) )
			return new Double(fields.get(name).toString()).doubleValue();
		else
			throw new ApiException(ApiConst.API_ERR_MISSING_PARAM, name);
	}
	
	public double getDoubleField(String name, double defaultValue)
	{
		if ( fields.containsKey(name) )
			return new Double(fields.get(name).toString()).doubleValue();
		else
			return defaultValue;
	}
	
	public Date getDateField(String name, TimeZone timeZone) throws ApiException
	{
		
		if ( fields.containsKey(name) )
			try {
				return Factory.getInstance().getEnvironment().getApiDateFormat(timeZone).parse(fields.get(name).toString());
			} catch (ParseException e) {
				throw new ApiException(e);
			}
		else
			throw new ApiException(ApiConst.API_ERR_BAD_DATE_FORMAT, name);
	}
	
	public Date getDateField(String name, Date defaultValue, TimeZone timeZone) throws ApiException
	{
		
		if ( fields.containsKey(name) )
			try {
				return Factory.getInstance().getEnvironment().getApiDateFormat(timeZone).parse(fields.get(name).toString());
			} catch (ParseException e) {
				throw new ApiException(e);
			}
		else
			return defaultValue;
	}

	public TimeZone getTimeZoneField(String name) throws ApiException
	{
		if ( fields.containsKey(name) )
			return TimeZone.getTimeZone(fields.get(name).toString());
		else
			throw new ApiException(ApiConst.API_ERR_MISSING_PARAM, name);
	}
	
	public TimeZone getTimeZoneField(String name, TimeZone defaultValue) throws ApiException
	{	
		if ( fields.containsKey(name) )
			return TimeZone.getTimeZone(fields.get(name).toString());
		else
			return defaultValue;
	}

	public DbObject getDbObjectField(String name, DbObject defualtValue, Class<? extends DbObject> clazz) throws ApiException
	{
		if ( fields.containsKey(name) )
			return DbObject.get(clazz, getField(name));
		else
			return defualtValue;
	}
	
	public List<DbObject> getDbObjectListField(String name, List<DbObject> defualtValue, Class<? extends DbObject> clazz) throws ApiException
	{
		if ( fields.containsKey(name) )
			return DbObject.getList(clazz, getField(name));
		else
			return defualtValue;
	}
	
	public Object getObjectField(String name) throws ApiException
	{
		if ( fields.containsKey(name) )
			return fields.get(name);
		else
			throw new ApiException(ApiConst.API_ERR_MISSING_PARAM, name);
	}
	
	public Object getObjectField(String name, Object defaultValue) throws ApiException
	{
		if ( fields.containsKey(name) )
			return fields.get(name);
		else
			return defaultValue;
	}
	
	public String getField(String name, String defaultValue)
	{
		if ( fields.containsKey(name) )
			return fields.get(name).toString();
		else
			return defaultValue;
	}
	
	public void setField(String name, int value)
	{
		fields.put(name, Integer.toString(value));
	}

	public void setField(String name, double value)
	{
		fields.put(name, Double.toString(value));
	}

	public void setField(String name, boolean value)
	{
		fields.put(name, Boolean.toString(value));
	}

	public void setField(String name, String value)
	{
		if ( value != null )
			fields.put(name, value);
	}

	public void setField(String name, Object value)
	{
		fields.put(name, value);
	}

	public void setFields(Map<String, String> map, boolean override) 
	{
		@SuppressWarnings("rawtypes")
		Map			notypeMap = (Map)map;
		
		for ( String key : map.keySet() )
		{
			if ( notypeMap.get(key) instanceof String )
			{
				String		value = map.get(key).toString();
				boolean		fieldOverride = override;
				
				if ( key.charAt(0) == '+' )
				{
					fieldOverride = true;
					key = key.substring(1);
				}
				else if ( key.charAt(0) == '-' )
				{
					fieldOverride = false;
					key = key.substring(1);
				}
				
				if ( fieldOverride || !fields.containsKey(key) )
					fields.put(key, value);
			}
		}
	}
	
	public void copyFields(ApiPacket src, String[] names) throws ApiException
	{
		for ( String name : names )
			setField(name, src.getField(name));
	}

	@Override
	public String toString() {
		if ( fields == null )
			return super.toString();
		else
			return fields.toString();
	}

	public ApiPacket getMapFieldAsApiPacket(String name) throws ApiException 
	{
		Object		value = fields.get(name);
		if ( value == null )
			throw new ApiException(ApiConst.API_ERR_NO_SUCH);
		if ( !(value instanceof Map) )
			throw new ApiException(ApiConst.API_ERR_BAD_PARAM);
		
		@SuppressWarnings("unchecked")
		Map<String, Object>		map = (Map<String, Object>)value;
		ApiPacket				packet = new ApiPacket();
		for ( Map.Entry<String, Object> entry : map.entrySet() )
			if ( entry.getKey() != null && entry.getValue() != null )
				packet.getFields().put(entry.getKey(), entry.getValue());
		
		return packet;
	}

}
