package com.nightox.q.api;


public class ApiConst {

	// error codes
	public static final int 	API_ERR_OK 				= 0;			// this is the success error code
	public static final int 	API_ERR_BAD_CONF 		= 1;			// bad configuration 
	public static final int 	API_ERR_BAD_PARAM 		= 2;			// generic bad parameter
	public static final int 	API_ERR_NO_SESSION 		= 4;			// not logged in as a user (after login/register)
	public static final int 	API_ERR_NO_SUCH			= 5;			// no such object
	public static final int 	API_ERR_DB_CONSTRAINT	= 6;			// duplicate key, etc.
	
	public static final int 	API_ERR_EXCEPTION 		= 11;			// internal: exception
	public static final int 	API_ERR_BAD_CMD 		= 12;			// bad command
	public static final int 	API_ERR_MISSING_PARAM	= 13;			// generic missing parameter
	public static final int 	API_ERR_INTERNAL		= 14;			// internal: inconsistency
	

	public static final int		API_ERR_NOT_AUTH		= 22;			// ...
	public static final int		API_ERR_BAD_DATE_FORMAT	= 23;			// ...
	
	public static final int 	API_ERR_BAD_STATE 		= 30;			// ...
	public static final int 	API_ERR_BAD_FORMAT 		= 31;			// ...
	public static final int 	API_ERR_CMD_FAILED 		= 32;			// ...

	public static final int 	API_ERR_IN_CLEANUP 		= 33;			// ...

	
	// api fields
	public static final String	API_FIELD_CODE					= "code";		// error code
	public static final String	API_FIELD_TECH_MSG				= "tech-msg";	// technical messages
	public static final String	API_FIELD_CAUSE_MSG				= "cause-msg";	
	public static final String	API_FIELD_SESSION				= "session";	// session id
	public static final String	API_FIELD_SESSION_USER_TYPE		= "session-user-type";
	public static final String	API_FIELD_CONSTRAINT_NAME		= "constraint-name";
	public static final String	API_FIELD_CONSTRAINT_MESSAGE	= "constraint-message";
	public static final String	API_FIELD_CONSTRAINT_KEY		= "constraint-key";
	public static final String	API_FIELD_CONSTRAINT_VALUE		= "constraint-value";
	public static final String	API_FIELD_ADDITIONAL_MESSAGE	= "additional-message";	
	
}
