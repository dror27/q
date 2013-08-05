<%@page import="com.freebss.sprout.banner.util.StreamUtils"%><%@page import="com.freebss.sprout.core.utils.QueryStringUtils"%><%@page import="java.util.LinkedHashMap"%><%@page import="java.util.LinkedList"%><%@page import="org.apache.commons.fileupload.FileItem"%><%@page import="java.util.List"%><%@page import="java.io.File"%><%@page import="org.apache.commons.fileupload.disk.DiskFileItemFactory"%><%@page import="org.apache.commons.fileupload.FileItemFactory"%><%@page import="org.apache.commons.fileupload.servlet.ServletFileUpload"%><%@page import="com.nightox.q.types.QTypeField"%><%@page import="java.util.Map"%><%@page import="com.nightox.q.types.IQType"%><%@page import="com.nightox.q.db.Database"%><%@page import="com.nightox.q.db.IDatabaseSession"%><%@page import="com.nightox.q.db.ISessionManager"%><%@page import="com.nightox.q.db.HibernateCodeWrapper"%><%@page import="com.nightox.q.model.base.DbObject"%><%@page import="com.nightox.q.beans.Services"%><%@page import="com.nightox.q.model.m.Q"%><%@page import="com.nightox.q.beans.Factory"%><%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%

// some constants
final String		COOKIE_NAME = "Q_694601798f5a490e9a231f2805215e6b";
final String		rootPath = Factory.getConfProperty("html.rootPath");

// first thing first ... we must have a cookie 
String		cookie = null;
if ( request.getCookies() != null )
	for ( Cookie c : request.getCookies() )
		if ( c.getName().equals(COOKIE_NAME) )
		{
			cookie = c.getValue();
			break;
		}
if ( cookie == null )
	cookie = Factory.getServices().getqManager().newQ();
Cookie				c = new Cookie(COOKIE_NAME, cookie);
c.setMaxAge(Integer.MIN_VALUE);
response.addCookie(c);

// open database session
ISessionManager				sessionManager = Database.getInstance().getSessionManager();
IDatabaseSession			databaseSession = sessionManager.pushThreadSession();
try
{
	// next we was to establish get q we are working on
	String				qid = request.getPathInfo().replace("/", "");
	Q					q = null;
	if ( Factory.getServices().getqManager().isValidQ(qid) )
	{
		// fetch or create
		q = (Q)DbObject.getByProperty(Q.class, "q", qid, false);
		if ( q == null )
		{
			q = new Q();
			q.setQ(qid);
			q.save();
		}
	}

	// if we have no Q, escape to root
	if ( q == null )
	{
		response.sendRedirect(rootPath);
		return;
	}	
	
	// image?
	if ( request.getParameter("image") != null )
	{
		if ( q.getBinaryData() != null && q.getContentType() != null )
		{
			response.setContentType(q.getContentType());
			
			response.getOutputStream().write(q.getBinaryData());
		}
		else
			response.sendRedirect(rootPath);
		return;
	}
	
	// clear? (TEMP)
	if ( request.getParameter("clear") != null )
	{
		q.cleanData();
		
		// redirect to view page
		response.sendRedirect(q.getQ());
		return;
	}
	
	
	// uploading?
	if ( ServletFileUpload.isMultipartContent(request) )
	{
		// Create a factory for disk-based file items
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(16 * 1024 * 1024);

		// Configure a repository (to ensure a secure temp location is used)
		ServletContext servletContext = this.getServletConfig().getServletContext();
		File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
		factory.setRepository(repository);

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);

		// Parse the request
		Map<String, FileItem>	items = new LinkedHashMap<String, FileItem>();
		for ( FileItem item : upload.parseRequest(request) )
			items.put(item.getFieldName(), item);
		
		// must have upload field
		if ( items.containsKey("upload") )
		{
			// extract type
			String		dataType = items.get("type").getString();
			IQType		qType = Factory.getServices().getqTypeManager().getQType(dataType);
			if ( qType != null )
			{
				// build map of arguments with upload field names
				Map<String, FileItem>	uploadItems = new LinkedHashMap<String, FileItem>();
				for ( QTypeField field : qType.getUploadFields() )
					uploadItems.put(field.getName(), items.get(qType.getDataType() + "_" + field.getName()));
				
				// clean
				q.cleanData();

				// ask type to save
				qType.setUploadItems(q, uploadItems);
				
				// save data type
				q.setDataType(dataType);
				
				// redirect to view page
				response.sendRedirect(q.getQ());
				return;
			}
		}
	}
	
	// map datatype
	IQType		qType = null;
	if ( q.getDataType() != null ) 
		qType = Factory.getServices().getqTypeManager().getQType(q.getDataType());
	
	%><!DOCTYPE html>
<!--[if IEMobile 7 ]>    <html class="no-js iem7"> <![endif]-->
<!--[if (gt IEMobile 7)|!(IEMobile)]><!--> <html class="no-js"> <!--<![endif]-->
    <head>
        <meta charset="utf-8">
        <title><%=Factory.getConfProperty("html.pageTitle")%></title>
        <meta name="description" content="">
        <meta name="HandheldFriendly" content="True">
        <meta name="MobileOptimized" content="320">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta http-equiv="cleartype" content="on">

        <link rel="apple-touch-icon-precomposed" sizes="144x144" href="<%=rootPath%>img/touch/apple-touch-icon-144x144-precomposed.png">
        <link rel="apple-touch-icon-precomposed" sizes="114x114" href="<%=rootPath%>img/touch/apple-touch-icon-114x114-precomposed.png">
        <link rel="apple-touch-icon-precomposed" sizes="72x72" href="<%=rootPath%>img/touch/apple-touch-icon-72x72-precomposed.png">
        <link rel="apple-touch-icon-precomposed" href="<%=rootPath%>img/touch/apple-touch-icon-57x57-precomposed.png">
        <link rel="shortcut icon" href="<%=rootPath%>img/touch/apple-touch-icon.png">

        <!-- Tile icon for Win8 (144x144 + tile color) -->
        <meta name="msapplication-TileImage" content="<%=rootPath%>img/touch/apple-touch-icon-144x144-precomposed.png">
        <meta name="msapplication-TileColor" content="#222222">


        <!-- For iOS web apps. Delete if not needed. https://github.com/h5bp/mobile-boilerplate/issues/94 -->
        <!--
        <meta name="apple-mobile-web-app-capable" content="yes">
        <meta name="apple-mobile-web-app-status-bar-style" content="black">
        <meta name="apple-mobile-web-app-title" content="">
        -->

        <!-- This script prevents links from opening in Mobile Safari. https://gist.github.com/1042026 -->
        <!--
        <script>(function(a,b,c){if(c in b&&b[c]){var d,e=a.location,f=/^(a|html)$/i;a.addEventListener("click",function(a){d=a.target;while(!f.test(d.nodeName))d=d.parentNode;"href"in d&&(d.href.indexOf("http")||~d.href.indexOf(e.host))&&(a.preventDefault(),e.href=d.href)},!1)}})(document,window.navigator,"standalone")</script>
        -->

        <link rel="stylesheet" href="<%=rootPath%>css/normalize.css">
        <link rel="stylesheet" href="<%=rootPath%>css/main.css">
        <script src="<%=rootPath%>js/vendor/modernizr-2.6.2.min.js"></script>
    </head>
    <body>

    <!-- Add your site or application content here -->
<% if ( qType == null || request.getParameter("replace") != null ) { %>
	<div>
		<% if ( request.getParameter("replace") != null ) { %>
		<a href="<%=q.getQ()%>">cancel</a> |
		<% } %>
		<a href="<%=rootPath%>" target="_blank">create</a> (<a href="<%=rootPath%>?rows=2&cols=1" target="_blank">big</a>,<a href="<%=rootPath%>?rows=1&cols=1" target="_blank">huge</a>)
	</div>

	<div>
	<h2><%=Factory.getConfProperty("html.fillMe") %></h2>
	<form method="post" enctype="multipart/form-data" action="<%=q.getQ()%>">
	<input type="hidden" name="upload" value="1"/>
	<table>
		
		<tr>
			<th>Type:</th>
			<td>
				<select name="type" id="type" onchange="javascript:updateUploadFields()">
					<% for ( Map.Entry<String,String> entry : Factory.getServices().getqTypeManager().getDisplayNamesMap().entrySet() ) { %>
						<option value="<%=entry.getKey()%>"><%=entry.getValue()%></option>
					<% } %>
				</select>
			</td>
		</tr>
		
		<% 
		for ( IQType type : Factory.getServices().getqTypeManager().getQTypes() ) 
		{
			if ( type.getUploadFields() != null )
				for ( QTypeField field : type.getUploadFields() )
				{
					String			name = type.getDataType() + "_" + field.getName();
					%>
						<tr x-type="<%=type.getDataType()%>" style="display:none">
						<th><%=field.getLabel()%>:</th>
						<td>
					<%
					if ( field.getType().equals("textarea") )
					{
						%>
						<textarea name="<%=name%>" id="<%=name%>"></textarea>
						<%
					}
					else
					{
						%>
						<input name="<%=name%>" id="<%=name%>" type="<%=field.getType()%>"/>
						<%
					}
					%>
						</td>
					<%
				}
		}
		%>		
		<tr>
			<td/>
			<td>
				<input type="submit" name="post" value="Post"/>
			</td>
		</tr>
	</table>
	</form>
	<script language="javascript">
	function updateUploadFields()
	{
		var			type = $("#type")[0].value;
		
		$.each($('[x-type]'), function(index, elem) {
			
			if ( elem.getAttribute("x-type") == type )
				elem.style.display="";
			else
				elem.style.display="none";
			
		});
		
	}
	</script>
	</div>
<% } else { %>
	<div>
		<a href="<%=q.getQ()%>?replace">replace</a> |	
		<a href="<%=q.getQ()%>?clear">clear</a> |	
		<a href="<%=rootPath%>" target="_blank">create</a> (<a href="<%=rootPath%>?rows=2&cols=1" target="_blank">big</a>,<a href="<%=rootPath%>?rows=1&cols=1" target="_blank">huge</a>)
	</div>
	<div>
		<%=qType.renderHtml(q)%>
	</div>
<% } %>



        <script src="<%=rootPath%>js/vendor/zepto.min.js"></script>
        <script src="<%=rootPath%>js/helper.js"></script>

		<script>
		Zepto(function($){
			  updateUploadFields();
			})
		</script>

        <!-- Google Analytics: change UA-XXXXX-X to be your site's ID. -->
        <script>
            var _gaq=[["_setAccount","UA-XXXXX-X"],["_trackPageview"]];
            (function(d,t){var g=d.createElement(t),s=d.getElementsByTagName(t)[0];g.async=1;
            g.src=("https:"==location.protocol?"//ssl":"//www")+".google-analytics.com/ga.js";
            s.parentNode.insertBefore(g,s)}(document,"script"));
        </script>
    </body>
</html><%
	
} catch (Exception e)
{
	databaseSession.rollback();
	
	throw new RuntimeException(e);
}
finally
{
	sessionManager.popThreadSession();
}
%>