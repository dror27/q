<%@page import="java.net.HttpURLConnection"%><%@page import="java.net.URL"%><%@page import="java.io.InputStream"%><%@page import="org.apache.commons.logging.LogFactory"%><%@page import="org.apache.commons.logging.Log"%><%@page import="org.apache.commons.lang.StringUtils"%><%@page import="java.awt.font.NumericShaper"%><%@page import="org.bouncycastle.util.test.NumberParsing"%><%@page import="com.nightox.q.generate.BasicPageGenerator"%><%@ page language="java" contentType="application/pdf" pageEncoding="UTF-8"%><%

BasicPageGenerator	generator = new BasicPageGenerator();
final Log			log = LogFactory.getLog(this.getClass());

InputStream			centerImage = null;


try
{
	if ( !StringUtils.isEmpty(request.getParameter("rows")) )
		generator.setCodeRows(Integer.parseInt(request.getParameter("rows")));
	if ( !StringUtils.isEmpty(request.getParameter("cols")) )
		generator.setCodeCols(Integer.parseInt(request.getParameter("cols")));
	
	if ( !StringUtils.isEmpty(request.getParameter("layout")) )
	{
		String[]		toks = request.getParameter("layout").split("x");
		if ( toks.length == 2 )
		{
			generator.setCodeCols(Integer.parseInt(toks[0]));
			generator.setCodeRows(Integer.parseInt(toks[1]));
		}
	}

	if ( !StringUtils.isEmpty(request.getParameter("caption")) )
	{
		String		caption = request.getParameter("caption");
		
		generator.setNoteText(caption);
	}
	
	try
	{
		if ( !StringUtils.isEmpty(request.getParameter("image")) )
		{
			URL					url = new URL(request.getParameter("image"));
			HttpURLConnection	urlConnection = (HttpURLConnection)url.openConnection();
			
			urlConnection.setRequestMethod("GET");
			urlConnection.connect();
			
			if ( urlConnection.getResponseCode() == 200 )
				centerImage = urlConnection.getInputStream();
		}
	}
	catch (Throwable e)
	{
		centerImage = null;	
	}
	if ( centerImage == null )
		centerImage = BasicPageGenerator.class.getClassLoader().getResourceAsStream("resources/Revolution-Fist-Small.jpg");

	
} catch (Throwable e)
{
	
}

generator.generatePage(response.getOutputStream(), centerImage);

%>