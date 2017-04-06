package io.github.sidf.documentreader.web;

import spark.Route;
import spark.Spark;
import java.util.Map;
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
import javax.servlet.MultipartConfigElement;
import spark.template.freemarker.FreeMarkerEngine;

import io.github.sidf.documentreader.util.FileUtil;
import io.github.sidf.documentreader.util.ArrayUtil;
import io.github.sidf.documentreader.web.util.ConfigUtil;
import io.github.sidf.documentreader.web.util.RequestUtil;
import io.github.sidf.documentreader.service.DocumentReaderService;

class RootRoute implements Route {
	private Request request;
	private Response response;
	private String libraryPath;
	private Map<String, Object> map;
	
	private String infoMessage;
	private String errorMessage;
	
	private boolean isReading;
	private String[] supportedReaderSpeed;
	private String[] availableReaderProviders;
	private String[] supportedReaderLanguages;
	
	private static final Map<String, String> supportedVolumeLevels = new LinkedHashMap<>();
	
	private Map<String, String> availableDocuments;
	private static final String[] standardSwitchOptions = new String[] { "on", "off" };
	
	private static ConfigUtil config;
	private static DocumentReaderService service;
	private static final Pattern buttonPattern = Pattern.compile("btn_set\\w+(?=\\=)");
	
	public RootRoute(String libraryPath, String configPath, DocumentReaderService documentReaderService) throws Exception {
		this.libraryPath = libraryPath;
		service = documentReaderService;
		config = new ConfigUtil(configPath);
		
		supportedVolumeLevels.put("100 %", "100");
		supportedVolumeLevels.put("50 %", "50");
		supportedVolumeLevels.put("25 %", "25");
		supportedVolumeLevels.put("0 %", "0");
		
		availableReaderProviders = service.getReaderProviders();
		availableDocuments = service.getDocumentMap();
		preconfiguration();
	}
	
	@Override
	public Object handle(Request request, Response response) throws Exception {
		this.request = request;
		this.response = response;
		this.map = new HashMap<>();

		String requestMethod = request.requestMethod();
		response.header("Accept-Charset", "utf-8");
		
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
		updateDocumentInfo(config.getDocumentHash());

		map.put("message", infoMessage);
		map.put("errorMessage", errorMessage);
		
		map.put("isReading", isReading);
		map.put("selectedLog", config.getLog());
		map.put("selectedPageContent", config.getContent());
		map.put("selectedFeatureDetection", config.getFeatureDetection());
		map.put("standardSwitchOptions", standardSwitchOptions);
		
		map.put("selectedVolumeLevel", config.getVolume());
		map.put("supportedVolumeLevels", supportedVolumeLevels);
		
		map.put("selectedDocument", availableDocuments.get(config.getDocumentHash()));
		map.put("availableReaderProviders", availableReaderProviders);
		map.put("selectedReaderLang", config.getReaderLanguage());
		map.put("selectedReaderSpeed", config.getReaderSpeed());
		map.put("selectedReaderProvider", config.getReaderProvider());
		map.put("supportedReaderSpeed", supportedReaderSpeed);
		map.put("supportedReaderLanguages", supportedReaderLanguages);
		
		map.put("selectedDocumentHash", config.getDocumentHash());
		map.put("availableDocuments", service.getDocumentMap());
		
		infoMessage = errorMessage = null;
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
			    	infoMessage = "Successful upload";
			    } catch (IOException e) {
					errorMessage = "Could not save the file";
			    }
			}
	    } else {
	    	Matcher matcher = buttonPattern.matcher(request.body());
	    	
			if (matcher.find()) {
				String pressedButton = matcher.group();
				handleButtonPress(pressedButton, request);
			}
	    }
	    
	    map.put("infoMessage", infoMessage);
	    map.put("errorMessage", errorMessage);
		response.redirect("/");
		return null;
	}
	
	private void preconfiguration() throws Exception {
		if (!supportedVolumeLevels.containsValue(config.getVolume())) {
			String firstVolume = supportedVolumeLevels.values().iterator().next();
			config.setVolume(firstVolume);
			int volume = Integer.valueOf(firstVolume);
			if (service.getAudioVolume() != volume) {
				service.setAudioVolume(volume);
			}
		}
		
		if (config.getFeatureDetection() == null) {
			config.setFeatureDetection("off");
		}
		
		if (config.getLog() == null) {
			config.setLog("on");
		}
		
		if (config.getContent() == null) {
			config.setContent("on");
		}
		
		updateDocumentInfo(config.getDocumentHash());
		
		String provider = config.getReaderProvider();
		if (!ArrayUtil.arrayContains(availableReaderProviders, provider)) {
			provider = availableReaderProviders[0];
		} 
		updateReaderInfo(provider);
		
		config.store();
	}
	
	private void updateReaderInfo(String provider) {
		String currentProvider = config.getReaderProvider();
		
		if (!provider.equals(currentProvider)) {
			config.setReaderProvider(provider);
		}
		
		try {
			service.setCurrentReader(provider);
		} catch (Exception e) {
			errorMessage = "Could not update reader settings";
			// TODO do something about it, otherwise the next 2 lines will probably fail too
		}
		
		supportedReaderSpeed = service.getCurrentSupportedSpeed();
		supportedReaderLanguages = service.getCurrentSupportedLanguages();
		
		try {
			updateReaderSettings();
		} catch (Exception e) {
			config.setReaderSpeed(supportedReaderSpeed[0]);
			config.setReaderLanguage(supportedReaderLanguages[0]);
			
			try {
				updateReaderSettings();
			} catch (IOException e1) {
				errorMessage = "Could not update speed and / or language settings";
			}
		}
	}
	
	private void updateReaderSettings() throws IOException {
		service.setCurrentReaderSpeed(config.getReaderSpeed());
		service.setCurrentReaderLanguage(config.getReaderLanguage());
	}
	
	private void updateDocumentInfo(String documentHash) {
		if (!availableDocuments.containsKey(documentHash)) {
			if (availableDocuments.size() != 0) {
				documentHash = availableDocuments.keySet().iterator().next();
			} else {
				documentHash = null;
			}
		}
		
		config.setDocumentHash(documentHash);
		
		try {
			service.setDocument(documentHash);
		} catch (Exception e) {
			errorMessage = "Could not set the document";
		}
	}
	
	private void handleButtonPress(String buttonName, Request request) {
		switch (buttonName) {
		case "btn_set_delete":
			String documentHash = config.getDocumentHash();
			service.deleteDocument(documentHash);
			availableDocuments.remove(documentHash);
			updateDocumentInfo(null);
			break;
		case "btn_set_read":
			try {
				service.startReading(config.getFeatureDetection().equals("on"));
				isReading = true;
			} catch (IOException e) {
				errorMessage = "Coult not start the reader";
			}
			break;
		case "btn_set_stop":
			service.stopReading();
			isReading = false;
			break;
		case "btn_set_reset_bookmark":
			service.resetCurrentDocumentBookmark();
			break;
		case "btn_set_document":
			updateDocumentInfo(RequestUtil.parseBodyString(request.body(), "set_document"));
			break;
		case "btn_set_reader":
			updateReaderInfo(RequestUtil.parseBodyString(request.body(), "set_reader"));
			break;
		case "btn_set_lang":
			String selectedReaderLang = RequestUtil.parseBodyString(request.body(), "set_lang");
			try {
				service.setCurrentReaderLanguage(selectedReaderLang);
				config.setReaderLanguage(selectedReaderLang);
			} catch (IOException e) {
				errorMessage = "Could not set the reader's language" + e.getMessage();
			}
			break;
		case "btn_set_reading_speed":
			String selectedReaderSpeed = RequestUtil.parseBodyString(request.body(), "set_reading_speed");
			try {
				service.setCurrentReaderSpeed(selectedReaderSpeed);
				config.setReaderSpeed(selectedReaderSpeed);
			} catch (IOException e) {
				errorMessage = "Could not set the reader's speed";
			}
			break;
		case "btn_set_volume":
			String selectedVolumeLevel = RequestUtil.parseBodyString(request.body(), "set_volume");
			try {
				service.setAudioVolume(Integer.parseInt(selectedVolumeLevel));
				config.setVolume(selectedVolumeLevel);
			} catch (Exception e) {
				errorMessage = "Could not change the volume";
			}
			break;
		case "btn_set_feature_detection":
			config.setFeatureDetection(RequestUtil.parseBodyString(request.body(), "set_feature_detection"));
			break;
		case "btn_set_logs":
			config.setLog(RequestUtil.parseBodyString(request.body(), "set_logs"));
			break;
		case "btn_set_page_content":
			config.setContent(RequestUtil.parseBodyString(request.body(), "set_page_content"));
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
			config.store();
		} catch (IOException e) {
			errorMessage = "Could not store settings";
		}
	}
	
	public void setIsReading(boolean flag) {
		isReading = flag;
	}
}
