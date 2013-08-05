<%@page import="java.awt.font.NumericShaper"%>
<%@page import="org.bouncycastle.util.test.NumberParsing"%>
<%@page import="com.nightox.q.generate.BasicPageGenerator"%><%@ page language="java" contentType="application/pdf" pageEncoding="ISO-8859-1"%><%

BasicPageGenerator	generator = new BasicPageGenerator();

try
{
	if ( request.getParameter("rows") != null )
		generator.setCodeRows(Integer.parseInt(request.getParameter("rows")));
	if ( request.getParameter("cols") != null )
		generator.setCodeCols(Integer.parseInt(request.getParameter("cols")));
} catch (Throwable e)
{
	
}

generator.generatePage(response.getOutputStream(), 
			BasicPageGenerator.class.getClassLoader().getResourceAsStream("resources/Revolution-Fist-Small.jpg"));

%>