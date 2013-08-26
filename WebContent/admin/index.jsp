<%@page import="org.hibernate.criterion.Subqueries"%><%@page import="org.hibernate.criterion.DetachedCriteria"%><%@page import="com.nightox.q.html.ImgRenderer"%><%@page import="org.hibernate.criterion.Projections"%><%@page import="org.hibernate.criterion.Projection"%><%@page import="org.hibernate.Criteria"%><%@page import="java.text.SimpleDateFormat"%><%@page import="java.text.DateFormat"%><%@page import="org.hibernate.criterion.Order"%><%@page import="java.util.Date"%><%@page import="org.hibernate.criterion.Restrictions"%><%@page import="java.io.InputStream"%><%@page import="com.nightox.q.logic.LeaseManager"%><%@page import="java.text.DecimalFormat"%><%@page import="org.apache.commons.logging.LogFactory"%><%@page import="org.apache.commons.logging.Log"%><%@page import="org.apache.commons.lang.StringEscapeUtils"%><%@page import="org.apache.commons.lang.StringUtils"%><%@page import="com.freebss.sprout.banner.util.StreamUtils"%><%@page import="com.freebss.sprout.core.utils.QueryStringUtils"%><%@page import="java.util.LinkedHashMap"%><%@page import="java.util.LinkedList"%><%@page import="org.apache.commons.fileupload.FileItem"%><%@page import="java.util.List"%><%@page import="java.io.File"%><%@page import="org.apache.commons.fileupload.disk.DiskFileItemFactory"%><%@page import="org.apache.commons.fileupload.FileItemFactory"%><%@page import="org.apache.commons.fileupload.servlet.ServletFileUpload"%><%@page import="java.util.Map"%><%@page import="com.nightox.q.db.Database"%><%@page import="com.nightox.q.db.IDatabaseSession"%><%@page import="com.nightox.q.db.ISessionManager"%><%@page import="com.nightox.q.db.HibernateCodeWrapper"%><%@page import="com.nightox.q.model.base.DbObject"%><%@page import="com.nightox.q.beans.Services"%><%@page import="com.nightox.q.model.m.Q"%><%@page import="com.nightox.q.beans.Factory"%><%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%

final Log			log = LogFactory.getLog(this.getClass());

// some constants
final String		COOKIE_NAME = "Q_694601798f5a490e9a231f2805215e6b";
final String		rootPath = Factory.getConfProperty("html.rootPath");
final String		cdnUrl = Factory.getConfProperty("html.cdnUrl");
final Date			now = new Date();
final DateFormat	dateFormat = new SimpleDateFormat("MM/dd HH:mm:ss");
final String		googleApiKey = "AIzaSyBJVvzXM9LXFZ7T5M2PeW07JbhgssDe_jc";

// first thing first ... we must have a device cookie 
String		device = null;
if ( request.getCookies() != null )
	for ( Cookie c : request.getCookies() )
		if ( c.getName().equals(COOKIE_NAME) )
		{
			device = c.getValue();
			break;
		}
if ( device == null )
	device = Factory.getServices().getqManager().newQ();
Cookie				c = new Cookie(COOKIE_NAME, device);
c.setMaxAge(Integer.MIN_VALUE);
response.addCookie(c);

// open database session
ISessionManager				sessionManager = Database.getInstance().getSessionManager();
IDatabaseSession			databaseSession = sessionManager.pushThreadSession();
try
{
	%><!DOCTYPE html>
<!--[if IEMobile 7 ]>    <html class="no-js iem7"> <![endif]-->
<!--[if (gt IEMobile 7)|!(IEMobile)]><!--> <html class="no-js"> <!--<![endif]-->
    <head>
        <meta charset="utf-8">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title><%=Factory.getConfProperty("html.pageTitle")%> - Admin</title>
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
        		padding: 5px;
        	}
        
        	th {
        		padding: 5px;
        	}

        	td {
        		padding: 5px;
        	}
        
        	table.query tr th {
        		text-align: right;
        	}

        	table.results tr th {
        		text-align: center;
        	}
        	
        	table.results tr td {
				border: 1px solid;
        	}
        	table.results tr th {
        		background: #C0C0C0;
				border: 1px solid;
        	}

	      	html { height: 100% }
      		body { height: 100%; margin: 0; padding: 0 }

		  	#map-canvas { height: 100% }
		  
		  	/*
		  	div#query-div {
		  			display: none;
		  	}

		  	div#results-div {
		  			display: none;
		  	}
		  	*/
        </style>
        
        
	<script type="text/javascript"
      src="https://maps.googleapis.com/maps/api/js?key=<%=googleApiKey%>&sensor=true">
    </script>
	<script type="text/javascript">
      var script = '<script type="text/javascript" src="<%=rootPath%>js/vendor/markerclusterer/markerclusterer';
      if (document.location.search.indexOf('compiled') !== -1) {
        script += '_compiled';
      }
      script += '.js"><' + '/script>';
      document.write(script);
    </script>    </head>
    <body>
    
    	<!-- query form -->
    	<div id="query-div"/>
    	<form>
    		<table class="query">
    		
    			<tr>
    				<th>Lease Status:</th>
    				<td>
    					<select name="lease-status" id="lease-status">
    						<option value="" <%=getParamSelected(request, "lease-status", "")%>></option>
    						<option value="empty" <%=getParamSelected(request, "lease-status", "empty")%>>Empty</option>	
    						<option value="false" <%=getParamSelected(request, "lease-status", "false")%>>Free</option>	
    						<option value="true" <%=getParamSelected(request, "lease-status", "true")%>>Leased</option>	
    					</select>
    				</td>
    			</tr>
    			
    			<tr>
    				<th>Position:</th>
    				<td>
    					<input name="position" id="position" value="<%=getParam(request, "position", "")%>"/>
    					R:<input name="radius" id="radius" value="<%=getParam(request, "radius", "")%>"/>    					
    				</td>
    			</tr>
    			
    			<tr>
    				<th>Has File:</th>
    				<td>
    					<select name="has-file" id="has-file">
    						<option value="" <%=getParamSelected(request, "has-file", "")%>></option>
    						<option value="false" <%=getParamSelected(request, "has-file", "false")%>>No</option>	
    						<option value="true" <%=getParamSelected(request, "has-file", "true")%>>Yes</option>	
    					</select>
    				</td>
    			</tr>
    			
    			<tr>
    				<th/>
    				<td>
    					<button onclick="return button_clear()">Clear</button>
    					<input type="submit"/>
    				</td>
    			</tr>
    		</table>
    	</form>
    	</div>
    	
    	<%
    		// build criteria
    		int					offset = 0;
    		Criteria			crit = Database.getSession().createCriteria(Q.class)
										.add(Restrictions.eq("dataType", "post"))
										.add(Restrictions.isNull("version"))
										.addOrder(Order.desc("version"));
    		// lease-status
    		if ( getParam(request, "lease-status", "").equals("true") )
    			crit = crit.add(Restrictions.isNotNull("leaseEndsAt"))
    					   .add(Restrictions.gt("leaseEndsAt", now));
    		else if ( getParam(request, "lease-status", "").equals("false") )
    			crit = crit.add(Restrictions.or( 
    						Restrictions.isNull("leaseEndsAt"),
    					    Restrictions.le("leaseEndsAt", now)));
    		else if ( getParam(request, "lease-status", "").equals("empty") )
    			crit = crit.add(Restrictions.and( 
    						Restrictions.isNull("textData"),
    					    Restrictions.isNull("contentType")));
    		
    		// position
    		String[]		toks;
    		if ( !StringUtils.isEmpty(request.getParameter("position")) && 
    			 	(toks = request.getParameter("position").split(",")).length == 2 )
    		{
    			double		latitude = Double.parseDouble(toks[0]);
    			double		longitude = Double.parseDouble(toks[1]);
    			double		radius = Double.parseDouble(getParam(request, "radius", "0.1"));
    			
    			
    			crit = crit.add(Restrictions.between("latitude", latitude - radius, latitude + radius))
    					   .add(Restrictions.between("longitude", longitude - radius, longitude + radius));
    		}
    	
    		// has file
    		if ( getParam(request, "has-file", "").equals("true") )
    			crit = crit.add(Restrictions.isNotNull("contentType"));
    		else if ( getParam(request, "has-file", "").equals("false") )
    			crit = crit.add(Restrictions.isNull("contentType"));
    		
    		// execute critria
    		List<Q>				list = crit.list();
    		List<Q>				positionList = new LinkedList();
    		
    		// prepare to collect locations
    		double				latitude_min = Double.MAX_VALUE;
    		double				latitude_max = Double.MIN_VALUE;
    		double				longitude_min = Double.MAX_VALUE;
    		double				longitude_max = Double.MIN_VALUE;
    		
    		    		
    	%>
    	
    	<!-- results table -->
    	<div id="results-div">
    	<table class="results">
    		<tr>
    			<th/>
    			<th>Q</th>
    			<th>Text</th>
    			<th>ContentType</th>
    			<th>Postion</th>
    			<th>Lease</th>
    		</tr>
    		<% for (Q q : list) { %>
    			<tr>
    				<td>
    					<%=++offset%>
    				</td>
    				
    				<td>
    					<a target="_blank" href="qa/<%=q.getQ()%>"><%=q.getQ()%></a>
    				</td>
    				
    				<td>
    					<%
    						String		text = q.getTextData();
    						int			limit = 30;
    						if ( text != null )
    							text = text.trim();
    						if ( text != null && text.length() > limit )
    							text = text.substring(0, limit - 4) + " ...";

    						if ( !StringUtils.isEmpty(text) ) {
    							%><%=text%><%
    						}
    					%>
    				</td>
    				
    				<td>
    					<% if ( !StringUtils.isEmpty(q.getContentType()) ) { %>
    						<a target="_blank" href="qa/<%=q.getQ()%>?image"/><%=q.getContentType()%></a>
    					<% } %>
    				</td>
    				
    				<td>
    					<%
    						String		position = "";
    						if ( q.getLatitude() != null && q.getLongitude() != null )
    							position += q.getLatitude() + "," + q.getLongitude();

    						if ( !StringUtils.isEmpty(position) ) {
    							%><a target="_blank" href="http://maps.google.com/?q=<%=position%>"><%=position%></a><%
    						}
    						if ( q.getAltitude() != null ){
    							%> (<%=q.getAltitude()%>)<%
    						}
    						
    						if ( q.getLatitude() != null && q.getLongitude() != null )
    						{
    							latitude_min = Math.min(q.getLatitude(), latitude_min);
    							latitude_max = Math.max(q.getLatitude(), latitude_max);
    							
    							longitude_min = Math.min(q.getLongitude(), longitude_min);
    							longitude_max = Math.max(q.getLongitude(), longitude_max);
    							
    							positionList.add(q);
    						}
    					%>
    				</td>
    				
    				<td>
    					<%
    						if ( (q.getLeaseEndsAt() != null) && (q.getLeaseStartedAt() != null) && q.getLeaseEndsAt().after(now) )
    						{
    							int			period = (int)((q.getLeaseEndsAt().getTime() - now.getTime()) / 1000);
    							int			secs = period % 60; period /= 60;
    							int			mins = period % 60; period /= 60;
    							int			hours = period;
    							%><%=String.format("%02d:%02d:%02d", hours, mins, secs)%><%
    						}
    					%>
    				</td>
    			</tr>
    		<% } %>
    	</table>
    	</div>
    
    	<% if ( latitude_min != Double.MAX_VALUE && longitude_min != Double.MAX_VALUE ) { %>
    		<div id="map-canvas"/>
    	<% } %>

        <script src="<%=cdnUrl%>js/vendor/zepto.min.js"></script>
        <script src="<%=cdnUrl%>js/helper.js"></script>

		<script>
		Zepto(function($){
			});
		
		function button_clear()
		{
			$("#lease-status")[0].value = "";
			$("#position")[0].value = "";
			$("#radius")[0].value = "";
			$("#has-file")[0].value = "";
			
			return false;
		}
		
	      function initializeMap() 
	      {
	    	if ( $("#map-canvas").length > 0 )
	    	{
	    		var 	bounds = new google.maps.LatLngBounds();
	    		var		latitude = <%=(latitude_max + latitude_min) / 2.0%>;
	    		var		longitude = <%=(longitude_max + longitude_min) / 2.0%>;
	    		
		        var mapOptions = {
		          center: new google.maps.LatLng(latitude, longitude),
		          zoom: 8,
		          mapTypeId: google.maps.MapTypeId.ROADMAP
		        };
		        var map = new google.maps.Map(document.getElementById("map-canvas"),
		            mapOptions);
		        
		        // markers
		         var markers = [];
		        <% for ( Q q : positionList ) { %>
		        
		        	var position = new google.maps.LatLng(<%=q.getLatitude()%>, <%=q.getLongitude()%>);
		        	bounds.extend(position);
		        
			        var marker = new google.maps.Marker({
			            position: position,
			            title:"<%=q.getQ()%>"
			        });
	
			        google.maps.event.addListener(marker, "click", function() {
			            window.open("qa/<%=q.getQ()%>", "_blank");
			        });
			        
			        marker.setMap(map);
			        markers.push(marker);
		        <% } %>
		        
		        map.fitBounds(bounds);
		        var markerCluster = new MarkerClusterer(map, markers);
	    	}
	      }
	      google.maps.event.addDomListener(window, 'load', initializeMap);

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

<%! 
public String getParam(HttpServletRequest request, String name, String defaultValue) 
{
	String		value = request.getParameter(name);
	
	return !StringUtils.isEmpty(value) ? value.trim() : defaultValue;
}

public String getParamSelected(HttpServletRequest request, String name, String selectedValue) 
{
	String		value = request.getParameter(name);
	
	return selectedValue.equals(value) ? "selected" : "";
}


%>