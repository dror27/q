<%@page import="org.apache.commons.logging.LogFactory"%><%@page import="org.apache.commons.logging.Log"%><%@page import="org.apache.commons.lang.StringEscapeUtils"%><%@page import="org.apache.commons.lang.StringUtils"%><%@page import="com.freebss.sprout.banner.util.StreamUtils"%><%@page import="com.freebss.sprout.core.utils.QueryStringUtils"%><%@page import="java.util.LinkedHashMap"%><%@page import="java.util.LinkedList"%><%@page import="org.apache.commons.fileupload.FileItem"%><%@page import="java.util.List"%><%@page import="java.io.File"%><%@page import="org.apache.commons.fileupload.disk.DiskFileItemFactory"%><%@page import="org.apache.commons.fileupload.FileItemFactory"%><%@page import="org.apache.commons.fileupload.servlet.ServletFileUpload"%><%@page import="java.util.Map"%><%@page import="com.nightox.q.db.Database"%><%@page import="com.nightox.q.db.IDatabaseSession"%><%@page import="com.nightox.q.db.ISessionManager"%><%@page import="com.nightox.q.db.HibernateCodeWrapper"%><%@page import="com.nightox.q.model.base.DbObject"%><%@page import="com.nightox.q.beans.Services"%><%@page import="com.nightox.q.model.m.Q"%><%@page import="com.nightox.q.beans.Factory"%><%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%

final Log			log = LogFactory.getLog(this.getClass());

// some constants
final String		COOKIE_NAME = "Q_694601798f5a490e9a231f2805215e6b";
final String		rootPath = Factory.getConfProperty("html.rootPath");
final String		cdnUrl = Factory.getConfProperty("html.cdnUrl");

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
	
	// reporting position?
	if ( request.getParameter("position") != null )
	{
		// save
		if ( request.getParameter("latitude") != null )
			q.setLatitude(Double.parseDouble(request.getParameter("latitude")));
		if ( request.getParameter("longitude") != null )
			q.setLongitude(Double.parseDouble(request.getParameter("longitude")));
		if ( request.getParameter("altitude") != null )
			q.setAltitude(Double.parseDouble(request.getParameter("altitude")));
		
		// set empty response back
		return;
	}			
	
	// uploading?
	if ( ServletFileUpload.isMultipartContent(request) )
	{
		try
		{
			// starting upload
			log.info("starting upload");
			
			// Create a factory for disk-based file items
			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setSizeThreshold(16 * 1024 * 1024);
			log.info("disk-based factory created");
	
			// Configure a repository (to ensure a secure temp location is used)
			ServletContext servletContext = this.getServletConfig().getServletContext();
			File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
			factory.setRepository(repository);
			log.info("repository created");
	
			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);
			log.info("upload handler created");
	
			// Parse the request
			Map<String, FileItem>	items = new LinkedHashMap<String, FileItem>();
			for ( FileItem item : upload.parseRequest(request) )
			{
				log.info("item: " + item.getFieldName());
				items.put(item.getFieldName(), item);
			}
			
			// must have upload field
			if ( items.containsKey("upload") )
			{
				// process
				if ( !items.containsKey("edit") )
					q.cleanData();
				q.setDataType("post");
				q.setTextData(items.get("text").getString("UTF-8"));
				if ( items.containsKey("file") 
							&& !StringUtils.isEmpty(items.get("file").getContentType()) 
							&& items.get("file").getSize() > 0 )
				{
					q.setBinaryData(items.get("file").getInputStream());
					q.setContentType(items.get("file").getContentType());
				}
				
				
				// redirect to view page
				log.info("redirecting");
			}
		} catch (Throwable e) {
			log.error("upload failed", e);
		}
		response.sendRedirect(q.getQ());
		return;
	}
	
	
	%><!DOCTYPE html>
<!--[if IEMobile 7 ]>    <html class="no-js iem7"> <![endif]-->
<!--[if (gt IEMobile 7)|!(IEMobile)]><!--> <html class="no-js"> <!--<![endif]-->
    <head>
        <meta charset="utf-8">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title><%=Factory.getConfProperty("html.pageTitle")%></title>
        <meta name="description" content="">
        <meta name="HandheldFriendly" content="True">
        <meta name="MobileOptimized" content="320">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta http-equiv="cleartype" content="on">

        <link rel="apple-touch-icon-precomposed" sizes="144x144" href="<%=cdnUrl%>img/touch/apple-touch-icon-144x144-precomposed.png">
        <link rel="apple-touch-icon-precomposed" sizes="114x114" href="<%=cdnUrl%>img/touch/apple-touch-icon-114x114-precomposed.png">
        <link rel="apple-touch-icon-precomposed" sizes="72x72" href="<%=cdnUrl%>img/touch/apple-touch-icon-72x72-precomposed.png">
        <link rel="apple-touch-icon-precomposed" href="<%=cdnUrl%>img/touch/apple-touch-icon-57x57-precomposed.png">
        <link rel="shortcut icon" href="<%=cdnUrl%>img/touch/apple-touch-icon.png">

        <!-- Tile icon for Win8 (144x144 + tile color) -->
        <meta name="msapplication-TileImage" content="<%=cdnUrl%>img/touch/apple-touch-icon-144x144-precomposed.png">
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

        <link rel="stylesheet" href="<%=cdnUrl%>css/normalize.css">
        <link rel="stylesheet" href="<%=cdnUrl%>css/main.css">
        <script src="<%=cdnUrl%>js/vendor/modernizr-2.6.2.min.js"></script>
        
        <style type="text/css">
        body {
        	background: #99CCFF;
        }
        div.ctrl {
        	background: #8AB8E6;
        	padding-top: 5px;
        	padding-bottom: 5px;
        }
        div.button_div {
        	padding-top: 5px;
        	padding-bottom: 5px;
        }
        div.ctrl a {
        	font-size: 20pt;
        	padding-left: 5px;
        	padding-right: 5px;
        	text-decoration: none !important;
        }
        div.data {
        	margin: 5px;
        }
        div.data_img {
        	padding-bottom:5px;
        }
        </style>
    </head>
    <body>

    <!-- Add your site or application content here -->
<% if ( q.getDataType() == null || request.getParameter("replace") != null || request.getParameter("edit") != null ) { %>
	<div class="ctrl">
		<% if ( request.getParameter("replace") != null || request.getParameter("edit") != null ) { %>
		<a href="<%=q.getQ()%>"><img src="<%=cdnUrl%>img/icons/glyphish/113-navigation-mirror.png"/></a>
		<% } %>
		<a href="javascript:toggle_file()"><img id="file_enable" src="<%=cdnUrl%>img/icons/glyphish/68-paperclip.png"/></a>
		<a href="<%=rootPath%>" target="_blank" style="float:right"><img src="<%=cdnUrl%>img/icons/glyphish/10-medical.png"/></a>
	</div>

	<div class="data">
	<form method="post" enctype="multipart/form-data" action="<%=q.getQ()%>" acceptcharset="UTF-8">
	<input type="hidden" name="upload" value="1"/>
	<%
	if ( q.getTextData() != null && request.getParameter("edit") != null ) {
		%><input type="hidden" name="edit" value="1"/><%
	}
	%>
	
	<div class="button_div" id="file_div" style="display:none">
		<input id="file" name="file" type="file" placeholder="upload image here">
	</div>
	<textarea id="text" name="text" cols="33" rows="6" placeholder="type text here"><%
	if ( q.getTextData() != null && request.getParameter("edit") != null ) {
		%><%=StringEscapeUtils.escapeHtml(q.getTextData())%><%
	}
	%></textarea>
	<div class="button_div">
		<input type="submit" name="post" value="Post"/>
	</div>
	</form>
	</div>
<% } else { %>
	<div class="ctrl">
		<a href="<%=q.getQ()%>?replace"><img src="<%=cdnUrl%>img/icons/glyphish/08-chat.png"/></a>	
		<a href="<%=q.getQ()%>?edit"><img src="<%=cdnUrl%>img/icons/glyphish/19-gear.png"/></a>
		<a href="<%=q.getQ()%>?clear"><img src="<%=cdnUrl%>img/icons/glyphish/22-skull-n-bones.png"/></a>	
		<a href="<%=rootPath%>" target="_blank" style="float:right"><img src="<%=cdnUrl%>img/icons/glyphish/10-medical.png"/></a>
	</div>
	<div class="data">
		<%=Factory.getServices().getHtmlRenderer().renderHtml(q)%>
	</div>
<% } %>



        <script src="<%=cdnUrl%>js/vendor/zepto.min.js"></script>
        <script src="<%=cdnUrl%>js/helper.js"></script>

		<script>
		Zepto(function($){
			  $(".video_iframe").each(function(index, iframe) {
				adjust_video_iframe(iframe);
			  });
			  
			  get_position();
			});
			
		function toggle_file()
		{
			var		file = $('#file_div')[0];
			var		display = file.style.display;
			
			if ( display == 'none' )
				file.style.display = "";
			else
				file.style.display = "none";
		}
		
		function adjust_video_iframe(iframe)
		{
			try
			{
				var		width = iframe.contentWindow.document.body.scrollWidth;
				var		height = width * 9 / 16;
				var		scrollHeight = (Math.floor(height)) + "px";
				
				iframe.height = scrollHeight;
			}
			catch (e)
			{
				
			}
		}
		
		function get_position()
		{
			if ( navigator.geolocation ) 
			{
				var timeoutVal = 10 * 1000 * 1000;
				
				navigator.geolocation.getCurrentPosition(
				    get_position_success, 
				    get_position_error,
				    { enableHighAccuracy: true, timeout: timeoutVal, maximumAge: 0 }
				  );
			}
			else
			{
				//alert("Geolocation is not supported by this browser");
			}
		}
		
		function get_position_success(position)
		{
			//alert("Latitude: " + position.coords.latitude + ", Longitude: " + position.coords.longitude);
			
			var		url = "<%=q.getQ()%>?position";
			var		data = new Object();
			data.latitude = position.coords.latitude;
			data.longitude = position.coords.longitude;
			if ( position.coords.altitude != null )
				data.altitude = position.coords.altitude;
			
			$.post(url, data);			
		}
		
		function get_position_error(error)
		{
			 var errors = { 
					    1: 'Permission denied',
					    2: 'Position unavailable',
					    3: 'Request timeout'
					  };
			 
			//alert("Error: " + errors[error.code]);
		}
		
		</script>

        <!-- Google Analytics: change UA-XXXXX-X to be your site's ID. -->
		<script>
		  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
		  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
		  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
		  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
		
		  ga('create', 'UA-42970023-1', 'nightox.com');
		  ga('send', 'pageview');
		
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