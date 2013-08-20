<%@page import="java.text.DecimalFormat"%><%@page import="org.apache.commons.logging.LogFactory"%><%@page import="org.apache.commons.logging.Log"%><%@page import="org.apache.commons.lang.StringEscapeUtils"%><%@page import="org.apache.commons.lang.StringUtils"%><%@page import="com.freebss.sprout.banner.util.StreamUtils"%><%@page import="com.freebss.sprout.core.utils.QueryStringUtils"%><%@page import="java.util.LinkedHashMap"%><%@page import="java.util.LinkedList"%><%@page import="org.apache.commons.fileupload.FileItem"%><%@page import="java.util.List"%><%@page import="java.io.File"%><%@page import="org.apache.commons.fileupload.disk.DiskFileItemFactory"%><%@page import="org.apache.commons.fileupload.FileItemFactory"%><%@page import="org.apache.commons.fileupload.servlet.ServletFileUpload"%><%@page import="java.util.Map"%><%@page import="com.nightox.q.db.Database"%><%@page import="com.nightox.q.db.IDatabaseSession"%><%@page import="com.nightox.q.db.ISessionManager"%><%@page import="com.nightox.q.db.HibernateCodeWrapper"%><%@page import="com.nightox.q.model.base.DbObject"%><%@page import="com.nightox.q.beans.Services"%><%@page import="com.nightox.q.model.m.Q"%><%@page import="com.nightox.q.beans.Factory"%><%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%

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
			factory.setSizeThreshold(16777216);
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
        	padding-bottom: 5px;
        }
        div.data_varsize {
        	display: inline;
        	overflow: hidden;
        	white-space: nowrap;
        }
        div.data_varsize p {
        	display: inline;
        	overflow: hidden;
        	white-space: nowrap;
        }
        </style>
    </head>
    <body onload="data_onload()">

    <!-- Add your site or application content here -->
<% if ( q.getDataType() == null || request.getParameter("replace") != null || request.getParameter("edit") != null ) { %>
	<div class="ctrl">
		<% if ( request.getParameter("replace") != null || request.getParameter("edit") != null ) { %>
		<a title="back" href="<%=q.getQ()%>"><img src="<%=cdnUrl%>img/icons/glyphish/113-navigation-mirror.png"/></a>
		<% } %>
		<a title="attach" href="javascript:toggle_file()"><img id="file_enable" src="<%=cdnUrl%>img/icons/glyphish/68-paperclip.png"/></a>
		<a title="print" href="<%=rootPath%>" target="_blank" style="float:right"><img src="<%=cdnUrl%>img/icons/glyphish/10-medical.png"/></a>
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
		<% if ( request.getParameter("source") != null ) { %>
		<a title="back" href="<%=q.getQ()%>"><img src="<%=cdnUrl%>img/icons/glyphish/113-navigation-mirror.png"/></a>
		<% } else { %>
		<a title="replace" href="<%=q.getQ()%>?replace"><img src="<%=cdnUrl%>img/icons/glyphish/08-chat.png"/></a>	
		<a title="edit" href="<%=q.getQ()%>?edit"><img src="<%=cdnUrl%>img/icons/glyphish/19-gear.png"/></a>
		<a title="clear" href="<%=q.getQ()%>?clear"><img src="<%=cdnUrl%>img/icons/glyphish/22-skull-n-bones.png"/></a>	
		<a title="source" href="<%=q.getQ()%>?source"><img src="<%=cdnUrl%>img/icons/glyphish/12-eye.png"/></a>
		<% } %> 
		<a href="<%=rootPath%>" target="_blank" style="float:right"><img src="<%=cdnUrl%>img/icons/glyphish/10-medical.png"/></a>
	</div>
	<div id="data" class="data">
		<% if ( request.getParameter("source") != null ) { 
			if ( Factory.getServices().getHtmlRenderer().getImgRenderer().isImageQ(q) )
			{
				DecimalFormat		format = new DecimalFormat();
				%><pre>[Image: <%=q.getContentType()%>, <%=format.format(q.getBinaryData().length)%> bytes]</pre><%	
			}
			%><pre><%=q.getTextData()%></pre><%
			
		} else {
			%><div id="data_varsize" class="data_varsize"><%=Factory.getServices().getHtmlRenderer().renderHtml(q)%></div><%
		} %>
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
			  
			  
			  auto_resize();
			});
			
		function data_onload()
		{
			auto_resize_image();
		}
		
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
		
		function auto_resize()
		{
			try
			{
				// must have a data_varsize
				var		varsize = $('#data_varsize');
				if ( varsize == null || varsize.length == 0 )
					return;
				
				// prepare
				var		data = $("#data");
				var		fontSize = parseInt(varsize.css("font-size"));
				var		orgFontSize = fontSize;
				var		orgHtml = varsize.html();
				var		maxFontSize = 80;
				var		maxWidth = data.width();
				
				// loop until overflows or too big
				var		modHtml = orgHtml.replace("</p>", "</p><br/>");
				varsize.html(modHtml)
				while ( fontSize < maxFontSize )
				{
					var		nextFontSize = Math.min(Math.floor(fontSize * 3 / 2), maxFontSize);
					
					varsize.css("font-size", nextFontSize + "px");
					var		nextWidth = varsize.width();
					if ( nextWidth > maxWidth )
						break;
					
					fontSize = nextFontSize;
				}
				
				// set font size we've settled on
				varsize.html(orgHtml);
				varsize.css("font-size", fontSize + "px");
				if ( fontSize != orgFontSize )
					varsize.css("line-height", 1.05);
				
				// reset inner paragraphs
				$("#data_varsize p").css("display", "block");
				$("#data_varsize p").css("margin", "0px 0px " + Math.floor(fontSize / 2) + "px 0px");
				
				// auto-center
				if ( is_auto_center(varsize.html()) )
					data.css("text-align", "center");
				
				// auto rtl?
				if ( is_auto_rtl(varsize.text()) )
					data.css("direction", "rtl");				
								
			} catch (e)
			{
				
			}
		}
		
		function is_auto_center(html)
		{
			if ( html.indexOf("http://") > 0 )
				return false;
				
			if ( html.indexOf("<ul") > 0 )
				return false;
				
			if ( html.indexOf("<ol") > 0 )
				return false;
				
			if ( html.indexOf("<h") > 0 )
				return false;
				
			if ( html.indexOf("<div") > 0 )
				return false;
				
			return true;
		}
		
		function is_auto_rtl(text)
		{
			// try to use library code
			if ( is_script_rtl(text) )
				return true;
			
			// empty?
			text = text.trim();
			if ( text.length == 0 )
				return false;
			
			// look at first char
			ch0 = text.charCodeAt(0);
			
			// hebrew/arabic?
			var		ranges = [
					// hebrew - http://en.wikipedia.org/wiki/Unicode_and_HTML_for_the_Hebrew_alphabet
					0x0591, 0x05F4, 
					0xFB00, 0xFB00,
					
					// arabic - http://en.wikipedia.org/wiki/Arabic_script_in_Unicode
					0x0600, 0x06FF,
					0x0750, 0x077F,
					0x08A0, 0x08FF,
					0xFB50, 0xFDFF,
					0xFE70, 0xFEFF,
					0x10E60, 0x10E7F,
					0x1EE00, 0x1EEFF					
			];
			for ( var n = 0 ; n < ranges.length ; n += 2)
			if ( ch0 >= ranges[n] && ch0 <= ranges[n+1] )
				return true;
			
			// if here, nop
			return false;
		}
		
		// image auto resize
		function auto_resize_image()
		{
			try
			{
				// adjust image size?
				var			img = $(".data_img img");
				if ( img.length == 1 )
				{
					var			imgWidth = img.width();
					var			naturalWidth = img[0].naturalWidth;
					
					if ( naturalWidth > 0 && naturalWidth < imgWidth )
						img.width(naturalWidth);
				}
			} catch (e)
			{
				
			}
		}
		
		// credit: http://stackoverflow.com/questions/12006095/javascript-how-to-check-if-character-is-rtl
		function is_script_rtl(t) {
		    var d, s1, s2, bodies;

		    //If the browser doesn’t support this, it probably doesn’t support Unicode 5.2
		    if (!("getBoundingClientRect" in document.documentElement))
		        return false;

		    //Set up a testing DIV
		    d = document.createElement('div');
		    d.style.position = 'absolute';
		    d.style.visibility = 'hidden';
		    d.style.width = 'auto';
		    d.style.height = 'auto';
		    d.style.fontSize = '10px';
		    d.style.fontFamily = "'Ahuramzda'";
		    d.appendChild(document.createTextNode(t));

		    s1 = document.createElement("span");
		    s1.appendChild(document.createTextNode(t));
		    d.appendChild(s1);

		    s2 = document.createElement("span");
		    s2.appendChild(document.createTextNode(t));
		    d.appendChild(s2);

		    d.appendChild(document.createTextNode(t));

		    bodies = document.getElementsByTagName('body');
		    if (bodies) {
		        var body, r1, r2;

		        body = bodies[0];
		        body.appendChild(d);
		        var r1 = s1.getBoundingClientRect();
		        var r2 = s2.getBoundingClientRect();
		        body.removeChild(d);

		        return r1.left > r2.left;
		    }

		    return false;   
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