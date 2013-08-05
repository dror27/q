package com.nightox.q.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.UrlResource;

import com.freebss.sprout.banner.util.StreamUtils;
import com.nightox.q.api.ApiConst;
import com.nightox.q.api.ApiContext;
import com.nightox.q.api.ApiException;
import com.nightox.q.api.ApiPacket;
import com.nightox.q.api.ApiSession;
import com.nightox.q.api.IApiCommand;
import com.nightox.q.beans.Factory;
import com.nightox.q.db.Database;
import com.nightox.q.db.HibernateCodeWrapper;
import com.nightox.q.utils.TimeUtils;

@WebServlet(name = "AppApi", urlPatterns = "/api/*", loadOnStartup = 1)
public class AppApi extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private static Log		log = LogFactory.getLog(AppApi.class);
	private static boolean	initDone = false;
	
	public void init() throws ServletException
	{
		super.init();
		log.info("init()");
		
		Database.getInstance();
		
		Database.wrapRunnable(new Runnable() {
			
			@Override
			public void run() {
				try {
					Factory.getInstance().getEnvironment().setup();
				} catch (ApiException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		});
		
		
		initDone = true;
		log.info("init() DONE");
	}
	
	public void destroy()
	{
		super.destroy();
		log.info("destroy()");
		
		Database.wrapRunnable(new Runnable() {
			
			@Override
			public void run() {
				try {
					Factory.getInstance().getEnvironment().teardown();
				} catch (ApiException e) {
					e.printStackTrace();
				}
			}
		});
		
		log.info("destroy() DONE");
	}
	
	static public ApiContext executeApiCommandAsInternal(String apiCommand) throws ApiException
	{
		if ( apiCommand.indexOf('?') >= 0 )
			apiCommand += "&session=" + ApiSession.getInternalSessionString();
		else
			apiCommand += "?session=" + ApiSession.getInternalSessionString();
	
		return executeApiCommand(apiCommand);
	}

	static public ApiContext executeApiCommand(String apiCommand) throws ApiException
	{
		if ( HibernateCodeWrapper.isWrapped() )
		{
			String[]		toks = apiCommand.split("\\?");
		
			return executeApiVerb(toks[0], toks.length > 1 ? toks[1] : null);
		}
		else
			return executeApiCommandWrapped(apiCommand);
	}
	
	static public ApiContext executeApiVerb(String apiVerb, String paramsQueryString) throws ApiException
	{
		ApiContext			context = new ApiContext(null, null);

		context.setApiRequest(new ApiPacket(paramsQueryString));
		context.getApiRequest().setField("_path", apiVerb);
		
		String				sessionId = context.getApiRequest().getField("session", null);
		context.setApiSession(Factory.getInstance().getEnvironment().getApiSessionManager().getApiSession(sessionId));
		
		executeApiVerb(apiVerb, context);
		
		return context;
	}
	
	static public void executeApiVerb(String apiVerb, ApiContext context) throws ApiException
	{
		Date 			startedAt = new Date();
		
		// establish command
		IApiCommand		cmd = null;
		for ( String key : Factory.getInstance().getEnvironment().getApiCommands().keySet() )
		{
			if ( apiVerb.startsWith(key) )
			{
				cmd = Factory.getInstance().getEnvironment().getApiCommands().get(key);
				break;
			}
		}
		if ( cmd == null )
			throw new ApiException(ApiConst.API_ERR_BAD_CMD, apiVerb);
		
		// dump command
		if ( log.isDebugEnabled() )
		{
			log.debug("apiVerb: " + apiVerb);
			log.debug("request: " + context.getApiRequest().format());
		}
		
		// execute
		cmd.checkAuthorized(context);
		cmd.doCommand(context);
		
		// dump response?
		if ( log.isDebugEnabled() )
			log.debug("response: " + context.getApiResponse().format());
		
		// _debug?
		if ( context.getApiRequest().hasField("_debug") )
		{
			Map<String, Object>		debug = new LinkedHashMap<String, Object>();
			
			// timing
			Map<String, Object>		timing = new LinkedHashMap<String, Object>();
			Date					finishedAt = new Date();
			timing.put("startedAt", Factory.getInstance().getEnvironment().getApiDateFormat(null).format(startedAt));
			timing.put("finishedAt", Factory.getInstance().getEnvironment().getApiDateFormat(null).format(finishedAt));
			timing.put("millis", finishedAt.getTime() - startedAt.getTime());
			debug.put("timing", timing);
			
			// verb, request
			debug.put("apiVerb", apiVerb);
			debug.put("apiRequest", context.getApiRequest().getFields());
			
			context.getApiResponse().setField("_debug", debug);
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		doGet(request, response);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		request.setCharacterEncoding("UTF-8");
		
		// delay?
		if ( Factory.getInstance().getConf().containsKey("api.delay") )
			TimeUtils.delay(Long.parseLong(Factory.getInstance().getConf().getProperty("api.delay")));
		
		// build context
		ApiContext		context = new ApiContext(request, response);

		// ping?
		if ( context.getApiRequest().hasField("_ping") )
			return;
		
		// map session
		String			sessionId = context.getApiRequest().getField(ApiConst.API_FIELD_SESSION, null);
		String			userType = "guest";
		context.setDatabaseSession(Database.getInstance().getSessionManager().pushThreadSession());
		context.setApiSession(Factory.getInstance().getEnvironment().getApiSessionManager().getApiSession(sessionId));

		try
		{
			try
			{					
				executeApiVerb(context.getRequestPath(), context);

				userType = context.getApiSession().getUserType();

				// make sure all database operations get executed
				context.getDatabaseSession().getSession().flush();
				context.getDatabaseSession().commit();
			}
			catch (ApiException exception)
			{

				context.getDatabaseSession().rollback();
				
				// command has generated an exception
				context.setApiResponse(new ApiPacket(exception));
			}
			catch (Throwable exception)
			{
				context.getDatabaseSession().rollback();

				exception.printStackTrace();
				
				// command has generated an exception
				context.setApiResponse(new ApiPacket(exception));
			}
			finally
			{
			}
			
			// write response back
			context.getApiResponse().setField(ApiConst.API_FIELD_SESSION, context.getApiSession().getId());			
			context.getApiResponse().setField(ApiConst.API_FIELD_SESSION_USER_TYPE, userType);

			if ( context.getApiResponse().hasField("contentType") )
			{
				try
				{
					response.setContentType(context.getApiResponse().getField("contentType"));

					String				filename = context.getApiResponse().getField("contentFilename", null);
					if ( filename != null )
						response.addHeader("Content-disposition", "attachment; filename=" + filename);
					
					InputStream			is = null;
					if ( context.getApiResponse().hasField("contentUrl") )
					{
						UrlResource		url = new UrlResource(context.getApiResponse().getField("contentUrl"));
						is = url.getInputStream();
					}
					else if ( context.getApiResponse().hasField("contentStream") )
					{
						is = (InputStream)context.getApiResponse().getObjectField("contentStream");
					}

					if ( is != null )
					{
						StreamUtils.copy(is, response.getOutputStream());
						is.close();
					}
				}
				catch (Exception e)
				{
					log.warn("exception ignored", e);
				}
			}
			else
			{
				String				responseText;
				response.setCharacterEncoding("UTF-8");
				response.getWriter().print(responseText = context.getApiResponse().format());
				if ( log.isDebugEnabled() )
					log.debug("responseText:\n" + responseText);
			}
		}
		finally
		{
			// release database connection
			Database.getInstance().getSessionManager().popThreadSession();
		}
	}

	static public ApiContext executeApiCommandWrapped(final String apiCommand) throws ApiException
	{
		if ( HibernateCodeWrapper.isWrapped() )
			return AppApi.executeApiCommand(apiCommand);
		else
		{
			Object	result = (new HibernateCodeWrapper() {
			
					@Override
					protected Object code() throws Exception 
					{
						try
						{
							return AppApi.executeApiCommand(apiCommand);
						}
						catch (ApiException e)
						{
							return e;
						}
					}
				}).execute();
			
			if ( result instanceof ApiContext )
				return (ApiContext)result;
			else if ( result instanceof ApiException )
				throw (ApiException)result;
			else if ( result instanceof Exception )
				throw new ApiException((Exception)result);
			else
				throw new ApiException(ApiConst.API_ERR_INTERNAL, "unknown result class: " + result.getClass().getName());
		}
	}	

	
	public static boolean isInitDone() {
		return initDone;
	}
}
