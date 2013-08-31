<%@page import="java.util.LinkedHashMap"%><%@page import="org.apache.commons.fileupload.FileItem"%><%@page import="java.util.Map"%><%@page import="com.nightox.q.servlets.FileUploadProgressListener"%><%@page import="java.io.File"%><%@page import="org.apache.commons.fileupload.disk.DiskFileItemFactory"%><%@page import="org.apache.commons.fileupload.servlet.ServletFileUpload"%><%@page import="java.io.ByteArrayInputStream"%><%@page import="java.io.ByteArrayOutputStream"%><%@page import="java.awt.image.BufferedImage"%><%@page import="javax.imageio.ImageIO"%><%@page import="java.awt.Image"%><%@page import="java.net.HttpURLConnection"%><%@page import="java.net.URL"%><%@page import="java.io.InputStream"%><%@page import="org.apache.commons.logging.LogFactory"%><%@page import="org.apache.commons.logging.Log"%><%@page import="org.apache.commons.lang.StringUtils"%><%@page import="java.awt.font.NumericShaper"%><%@page import="org.bouncycastle.util.test.NumberParsing"%><%@page import="com.nightox.q.generate.BasicPageGenerator"%><%@ page language="java" contentType="application/pdf" pageEncoding="UTF-8"%><%

BasicPageGenerator	generator = new BasicPageGenerator();
final Log			log = LogFactory.getLog(this.getClass());

String				centerImageUrl = null;
InputStream			centerImageOriginal = null;
InputStream			centerImage = null;

if ( ServletFileUpload.isMultipartContent(request) )
{
	try
	{
		// starting upload
		log.debug("starting upload");
		
		// Create a factory for disk-based file items
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(16777216);
		log.debug("disk-based factory created");

		// Configure a repository (to ensure a secure temp location is used)
		ServletContext servletContext = this.getServletConfig().getServletContext();
		File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
		factory.setRepository(repository);
		log.debug("repository created");

		// Create a new file upload handler
		ServletFileUpload 				upload = new ServletFileUpload(factory);
		log.debug("upload handler created");

		// Parse the request
		Map<String, FileItem>	items = new LinkedHashMap<String, FileItem>();
		for ( FileItem item : upload.parseRequest(request) )
		{
			log.debug("item: " + item.getFieldName());
			items.put(item.getFieldName(), item);
		}
		
		// get non file args
		if ( items.containsKey("layout") && !StringUtils.isEmpty(items.get("layout").getString("UTF-8")) )
		{
			String[]		toks = items.get("layout").getString("UTF-8").split("x");
			if ( toks.length == 2 )
			{
				generator.setCodeCols(Integer.parseInt(toks[0]));
				generator.setCodeRows(Integer.parseInt(toks[1]));
			}
		}
	
		if ( items.containsKey("caption") && !StringUtils.isEmpty(items.get("caption").getString("UTF-8")) )
			generator.setNoteText(items.get("caption").getString("UTF-8"));
		
		if ( items.containsKey("image") && !StringUtils.isEmpty(items.get("image").getString("UTF-8")) )
			centerImageUrl = items.get("image").getString("UTF-8");
		
		if ( items.containsKey("style") && !StringUtils.isEmpty(items.get("style").getString("UTF-8")) )
			generator.setDotStyle(BasicPageGenerator.DotStyle.valueOf(items.get("style").getString("UTF-8")));

		// must have file field
		if ( items.containsKey("file") )
		{
			// extract info
			InputStream			fileInputStream = null;
			String				fileContentType = null;
			long				size = 0;
			if ( items.containsKey("file") 
					&& !StringUtils.isEmpty(items.get("file").getContentType()) 
					&& (size = items.get("file").getSize()) > 0 )
			{
				fileInputStream = items.get("file").getInputStream();
				fileContentType = items.get("file").getContentType();
			}
			boolean				hasContent = (fileInputStream != null);
			if ( size > 16000000 )
				hasContent = false;
			
			if ( hasContent )
				centerImageOriginal = fileInputStream;
		}
	} catch (Throwable e) {
		log.error("", e);
	}
}
else
{
	try
	{
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
			generator.setNoteText(request.getParameter("caption"));
		
		if ( !StringUtils.isEmpty(request.getParameter("image")) )
			centerImageUrl = request.getParameter("image");
		
		if ( !StringUtils.isEmpty(request.getParameter("style")) )
			generator.setDotStyle(BasicPageGenerator.DotStyle.valueOf(request.getParameter("style")));
		
	} catch (Throwable e)
	{
		log.error("", e);		
	}
}

try
{
	if ( centerImageUrl != null )
	{
		log.debug("image: " + centerImageUrl);
		
		URL					url = new URL(centerImageUrl);
		HttpURLConnection	urlConnection = (HttpURLConnection)url.openConnection();
		
		urlConnection.setRequestMethod("GET");
		urlConnection.connect();
		
		if ( urlConnection.getResponseCode() == 200 )
		{
			String		contentType = urlConnection.getContentType();
			log.debug("contentType: " + contentType);
		}
		
		centerImageOriginal = urlConnection.getInputStream();
	
	}
	
	if ( centerImageOriginal != null )
	{
		BufferedImage		image = ImageIO.read(centerImageOriginal);
		log.debug("image size: " + image.getWidth() + "x" + image.getHeight());
		
		ByteArrayOutputStream jpegStream = new ByteArrayOutputStream();
		boolean				writeResult = ImageIO.write(image, "jpg", jpegStream);
		log.debug("writeResult: " + writeResult);
		if ( writeResult )				
			centerImage = new ByteArrayInputStream(jpegStream.toByteArray());
		
	}
	
	if ( centerImage == null )
		centerImage = BasicPageGenerator.class.getClassLoader().getResourceAsStream("resources/Revolution-Fist-Small.jpg");
} catch (Throwable e)
{
	log.error("", e);
}
generator.generatePage(response.getOutputStream(), centerImage);

%>