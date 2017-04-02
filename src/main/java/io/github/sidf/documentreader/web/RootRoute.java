package io.github.sidf.documentreader.web;

import spark.Route;
import spark.Spark;
import java.io.File;
import java.util.Map;
import org.ini4j.Ini;
import spark.Request;
import spark.Response;
import java.util.HashMap;
import spark.ModelAndView;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.Part;
import javax.servlet.ServletException;
import javax.imageio.spi.ServiceRegistry;
import javax.servlet.MultipartConfigElement;
import spark.template.freemarker.FreeMarkerEngine;
import io.github.sidf.documentreader.util.ArrayUtil;
import io.github.sidf.documentreader.util.FileUtil;
import io.github.sidf.documentreader.util.enums.Speed;
import io.github.sidf.documentreader.util.enums.Language;
import io.github.sidf.documentreader.web.util.RequestUtil;
import io.github.sidf.documentreader.service.DocumentReaderService;

class RootRoute implements Route {
	private Map<String, Object> map;
	private Request request;
	private Response response;
	private String configPath;
	private String libraryPath;
	
	private String message;
	private String errorMessage;
	
	private String selectedReaderLang;
	private String selectedReaderSpeed;
	private String selectedDocumentHash;
	private String selectedReaderProvider;
	private String[] supportedReaderSpeed;
	private String[] availableReaderProviders;
	private String[] supportedReaderLanguages;
	
	private String selectedVolumeLevel;
	private static final Map<String, String> supportedVolumeLevels = new LinkedHashMap<>();
	
	private String selectedLog;
	private String selectedPageContent;
	private String selectedFeatureDetection;
	private Map<String, String> availableDocuments;
	private static final String[] standardSwitchOptions = new String[] { "on", "off" };
	
	private static Ini ini;
	private static DocumentReaderService service;
	private static final Pattern buttonPattern = Pattern.compile("btn_set\\w+(?=\\=)");
	
	public RootRoute(String libraryPath, String configPath, DocumentReaderService documentReaderService) throws Exception {
		this.configPath = configPath;
		this.libraryPath = libraryPath;
		service = documentReaderService;
		ini = new Ini(new File(configPath));
		
		supportedVolumeLevels.put("0 %", "0");
		supportedVolumeLevels.put("25 %", "25");
		supportedVolumeLevels.put("50 %", "50");
		supportedVolumeLevels.put("100 %", "100");
		
		availableDocuments = service.getDocumentMap();
		availableReaderProviders = service.getReaderProviders();
		updateVariables(true);
	}
	
	@Override
	public Object handle(Request request, Response response) throws Exception {
		this.request = request;
		this.response = response;
		this.map = new HashMap<>();

		String requestMethod = request.requestMethod();
		
		switch (requestMethod) {
		case "GET":
			return handleGet();
		case "POST":
			return handlePost();
		default:
			response.status(404);
			return null;
		}
	}
	
	private Object handleGet() {
		if (selectedDocumentHash == null && availableDocuments.size() != 0) {
			selectedDocumentHash = availableDocuments.entrySet().iterator().next().getKey();
		}
		
		map.put("message", message);
		map.put("errorMessage", errorMessage);
		
		map.put("selectedLog", selectedLog);
		map.put("selectedPageContent", selectedPageContent);
		map.put("selectedFeatureDetection", selectedFeatureDetection);
		map.put("standardSwitchOptions", standardSwitchOptions);
		
		map.put("selectedVolumeLevel", selectedVolumeLevel);
		map.put("supportedVolumeLevels", supportedVolumeLevels);
		
		map.put("selectedDocument", availableDocuments.get(selectedDocumentHash));
		map.put("availableReaderProviders", availableReaderProviders);
		map.put("selectedReaderLang", selectedReaderLang);
		map.put("selectedReaderSpeed", selectedReaderSpeed);
		map.put("selectedReaderProvider", selectedReaderProvider);
		map.put("supportedReaderSpeed", supportedReaderSpeed);
		map.put("supportedReaderLanguages", supportedReaderLanguages);
		
		map.put("selectedDocumentHash", selectedDocumentHash);
		map.put("availableDocuments", service.getDocumentMap());
		
		message = errorMessage = null;
		return new FreeMarkerEngine().render(new ModelAndView(map, "index.ftl"));
	}
	
	private Object handlePost() {
	    if (request.contentType().startsWith("multipart/form-data")) {
	    	request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/tmp"));
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
			    	FileUtil.inputStreamToFile(inputStream, libraryPath + "/" + fileName);
			    	service.updateDocumentLibrary();
			    	availableDocuments = service.getDocumentMap();
			    	message = "Successful upload";
			    } catch (IOException e) {
					errorMessage = "Could not save the file";
			    }
			}
	    } else {
	    	Matcher matcher = buttonPattern.matcher(request.body());
	    	String pressedButton = null;
	    	
			if (matcher.find()) {
				pressedButton = matcher.group();
				handleButtonPress(pressedButton, request);
			}
	    }
	    
	    map.put("message", message);
	    map.put("errorMessage", errorMessage);
		response.redirect("/");
		return null;
	}
	
	private void updateVariables(boolean readFromConfig) throws Exception {
		if (readFromConfig) {
			selectedVolumeLevel = ini.get("Device", "volume");
			if (!supportedVolumeLevels.containsValue(selectedVolumeLevel)) {
				selectedVolumeLevel = supportedVolumeLevels.values().iterator().next();
				int volume = Integer.valueOf(selectedVolumeLevel);
				if (service.getAudioVolume() != volume) {
					service.setAudioVolume(volume);
				}
			}
			
			selectedFeatureDetection = ini.get("Feature detection", "featureDetection");
			if (selectedFeatureDetection == null) {
				selectedFeatureDetection = "off";
			}
			
			selectedLog = ini.get("Web UI", "log");
			if (selectedLog == null) {
				selectedLog = "on";
			}
			
			selectedPageContent = ini.get("Web UI", "content");
			if (selectedPageContent == null) {
				selectedPageContent = "on";
			}
			
			selectedDocumentHash = ini.get("Document", "selectedDocumentHash");
			if (!availableDocuments.containsKey(selectedDocumentHash)) {
				selectedDocumentHash = availableDocuments.keySet().iterator().next();
			}
			
			String provider = ini.get("Reader", "provider");
			String speed = ini.get("Reader", "speed");
			String lang = ini.get("Reader", "language");
			
			if (ArrayUtil.arrayContains(availableReaderProviders, provider)) {
				selectedReaderProvider = provider;
				service.setCurrentReader(provider);
			}
			
			selectedReaderLang = lang;
			selectedReaderSpeed = speed;
		}
		
		if (selectedReaderProvider == null) {
			selectedReaderProvider = availableReaderProviders[0];
			service.setCurrentReader(selectedReaderProvider);
			
			supportedReaderSpeed = service.getCurrentSupportedSpeed();
			supportedReaderLanguages = service.getCurrentSupportedLanguages();
			
			selectedReaderLang = supportedReaderLanguages[0];
			selectedReaderSpeed = supportedReaderSpeed[0];
		}
	}
	
	private void handleButtonPress(String buttonName, Request request) {
		switch (buttonName) {
		case "btn_set_book":
			selectedDocumentHash = RequestUtil.parseBodyString(request.body(), "set_book");
			ini.put("Document", "selectedDocumentHash", selectedDocumentHash);
			break;
		case "btn_set_reader":
			selectedReaderProvider = RequestUtil.parseBodyString(request.body(), "set_reader");
			try {
				service.setCurrentReader(selectedReaderProvider);
			} catch (Exception e1) {
				errorMessage = "Could not set the reader provider";
			}
			ini.put("Reader", "provider", selectedReaderProvider);
			break;
		case "btn_set_lang":
			selectedReaderLang = RequestUtil.parseBodyString(request.body(), "set_lang");
			try {
				service.setCurrentReaderLanguage(selectedReaderLang);
			} catch (IOException e) {
				errorMessage = "Could not set the reader's language";
			}
			ini.put("Reader", "language", selectedReaderLang);
			break;
		case "btn_set_reading_speed":
			selectedReaderSpeed = RequestUtil.parseBodyString(request.body(), "set_reading_speed");
			try {
				service.setCurrentReaderSpeed(selectedReaderSpeed);
			} catch (IOException e) {
				errorMessage = "Could not set the reader's speed";
			}
			ini.put("Reader", "speed", selectedReaderSpeed);
			break;
		case "btn_set_volume":
			selectedVolumeLevel = RequestUtil.parseBodyString(request.body(), "set_volume");
			try {
				service.setAudioVolume(Integer.parseInt(selectedVolumeLevel));
				ini.put("Device", "volume", selectedVolumeLevel);
			} catch (Exception e) {
				errorMessage = "Could not change the volume";
			}
			break;
		case "btn_set_feature_detection":
			selectedFeatureDetection = RequestUtil.parseBodyString(request.body(), "set_feature_detection");
			ini.put("Feature detection", "featureDetection", selectedFeatureDetection);
			break;
		case "btn_set_logs":
			selectedLog = RequestUtil.parseBodyString(request.body(), "set_logs");
			ini.put("Web UI", "log", selectedLog);
			break;
		case "btn_set_page_content":
			selectedPageContent = RequestUtil.parseBodyString(request.body(), "set_page_content");
			ini.put("Web UI", "content", selectedPageContent);
			break;
		case "btn_set_manage_device":
			String action = RequestUtil.parseBodyString(request.body(), "set_manage_device");
			if (action.equals("reboot")) {
				try {
					service.rebootDevice();
				} catch (IOException e) {
					errorMessage = "Could not reboot the system";
				}
			} else {
				try {
					service.shutDownDevice();
				} catch (IOException e) {
					errorMessage = "Could not power off the system";
				}
			}
			break;
		default:
			Spark.halt(404);
		}
		
		try {
			ini.store();
		} catch (IOException e) {
			errorMessage = "Could not store settings";
		}
	}
}
