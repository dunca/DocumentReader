package io.github.sidf.documentreader.web;

import spark.Route;
import java.util.Map;
import spark.Request;
import spark.Response;
import java.util.HashMap;
import spark.ModelAndView;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.Part;
import javax.servlet.ServletException;
import javax.servlet.MultipartConfigElement;
import spark.template.freemarker.FreeMarkerEngine;
import io.github.sidf.documentreader.service.DocumentReaderService;
import io.github.sidf.documentreader.util.FileUtil;


class RootRoute implements Route {
	private Map map;
	private Request request;
	private Response response;
	
	private String configPath;
	private String libraryPath;
	
	private static DocumentReaderService service;
	private static final Pattern btnPattern = Pattern.compile("btn_set\\w+(?=\\=)");
	
	public RootRoute(String libraryPath, String configPath, DocumentReaderService documentReaderService) {
		this.configPath = configPath;
		this.libraryPath = libraryPath;
		service = documentReaderService;
	}
	
	@Override
	public Object handle(Request request, Response response) throws Exception {
		this.request = request;
		this.response = response;
		this.map = new HashMap<>();

		map.put("name", "007");
		
		String requestMethod = request.requestMethod();
		switch (requestMethod) {
		case "GET":
			return handleGet();
		case "POST":
			return handlePost();
		default:
			response.status(404);
			return "blah";
		}
	}
	
	private Object handleGet() {
		return new FreeMarkerEngine().render(new ModelAndView(map, "index.ftl"));
	}
	
	private Object handlePost() {
		String message = null;
		String errorMessage = null;

	    request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("C:\\Users\\Esc\\Desktop\\crap"));
	    if (request.contentType().startsWith("multipart/form-data")) {
		    Part uploadedFile = null;
			try {
				uploadedFile = request.raw().getPart("uploaded_file");
			} catch (IOException e1) {
				errorMessage = "Could not retrieve the file";
			} catch (ServletException e1) {
				// ignore, since with actually check this manually
			}
			
			if (uploadedFile != null && uploadedFile.getSize() != 0) {
			    String fileName = uploadedFile.getSubmittedFileName();
			    try (InputStream inputStream = uploadedFile.getInputStream()) {
			    	FileUtil.inputStreamToFile(inputStream, "C:\\Users\\Esc\\Desktop\\" + fileName + ".newshit");
			    	
			    	// TODO make sure it's accepted by one of the reader providers
			    	
			    	message = "Successful upload";
			    } catch (IOException e) {
					errorMessage = "Could not save the file";
			    }
			}
	    } else {
	    	Matcher matcher = btnPattern.matcher(request.body());
	    	String pressedButton = null;
	    	
			if (matcher.find()) {
				pressedButton = matcher.group();
				message = "button pressed " + pressedButton;
				
				// todo handle button press: update config if necessary
			} else {
				errorMessage = "no button pressed";
			}
	    }
	    
	    map.put("message", message);
	    map.put("errorMessage", errorMessage);
		return new FreeMarkerEngine().render(new ModelAndView(map, "index.ftl"));
	}
}
