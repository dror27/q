package com.nightox.q.api;

import java.util.LinkedHashMap;
import java.util.Map;

public class ApiException extends Exception {

	private static final long serialVersionUID = 1L;

	static Map<Integer, String>		s_codeMessages = new LinkedHashMap<Integer, String>();
	static {
		
		s_codeMessages.put(ApiConst.API_ERR_OK, "sucess");
		s_codeMessages.put(ApiConst.API_ERR_BAD_CONF, "bad configuration");
		s_codeMessages.put(ApiConst.API_ERR_BAD_PARAM, "generic bad parameter");
		s_codeMessages.put(ApiConst.API_ERR_NO_SESSION, "not logged in as a user");
		s_codeMessages.put(ApiConst.API_ERR_NO_SUCH, "no such object");
		s_codeMessages.put(ApiConst.API_ERR_DB_CONSTRAINT, "database constraint violated");
		s_codeMessages.put(ApiConst.API_ERR_EXCEPTION, "internal: exception");
		s_codeMessages.put(ApiConst.API_ERR_BAD_CMD, "bad command");
		s_codeMessages.put(ApiConst.API_ERR_MISSING_PARAM, "missing parameter");
		s_codeMessages.put(ApiConst.API_ERR_INTERNAL, "internal exception, check logs");
		s_codeMessages.put(ApiConst.API_ERR_NOT_AUTH, "not authorized");
		s_codeMessages.put(ApiConst.API_ERR_BAD_DATE_FORMAT, "bad date format");
		
		s_codeMessages.put(ApiConst.API_ERR_IN_CLEANUP, "in cleanup");
	}
	
	private int						errorCode;
	private String					additionalMessage;

	static public String getCodeMessage(int code, String defaultValue)
	{
		if ( s_codeMessages.containsKey(code) )
			return s_codeMessages.get(code);
		else
			return defaultValue;
	}
	
	public ApiException()
	{
		this(ApiConst.API_ERR_INTERNAL);
	}
	
	public ApiException(Exception e)
	{
		this(ApiConst.API_ERR_EXCEPTION, e);
	}
	
	public ApiException(Exception e, String additionalMessage)
	{
		this(ApiConst.API_ERR_EXCEPTION, e, additionalMessage);
	}
	
	public ApiException(int errorCode)
	{
		super(getCodeMessage(errorCode, null));
		this.errorCode = errorCode;
	}

	public ApiException(int errorCode, Exception e)
	{
		super(getCodeMessage(errorCode, null), e);
		this.errorCode = errorCode;
	}

	public ApiException(int errorCode, Exception e, String additionalMesssage)
	{
		super(getCodeMessage(errorCode, null), e);
		this.errorCode = errorCode;
		this.additionalMessage = additionalMesssage;
	}

	public ApiException(int errorCode, String additionalMessage)
	{
		this(errorCode);
		this.additionalMessage = additionalMessage;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public String getAdditionalMessage() {
		return additionalMessage;
	}

	public void setAdditionalMessage(String additionalMessage) {
		this.additionalMessage = additionalMessage;
	}
}
