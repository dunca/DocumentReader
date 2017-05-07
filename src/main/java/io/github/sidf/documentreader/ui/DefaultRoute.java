package io.github.sidf.documentreader.ui;


import spark.Route;
import spark.Spark;
import java.io.File;
import org.ini4j.Ini;
import java.util.Map;
import spark.Request;
import java.util.List;
import spark.Response;
import java.util.HashMap;
import spark.ModelAndView;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import javax.servlet.http.Part;
import javax.servlet.ServletException;
import javax.servlet.MultipartConfigElement;
import spark.template.freemarker.FreeMarkerEngine;

import io.github.sidf.documentreader.util.IoUtil;
import io.github.sidf.documentreader.ui.util.ConfigUtil;
import io.github.sidf.documentreader.ui.util.RequestUtil;
import io.github.sidf.documentreader.service.DocumentReaderService;

/**
 * The route that is tied to root url ("/")
 * @author sidf
 */
class DefaultRoute implements Route {
	private Request request;
	private Response response;
	
	/**
	 * The path to the directory in which the uploaded files are saved
	 */
	private String libraryPath;
	
	/**
	 * Mapping or variables that are evaluated in the UI's template
	 */
	private Map<String, Object> map;
	
	/**
	 * List of info/error messages that will be displayed in the UI
	 */
	private List<String> infoMessage = new ArrayList<>();
	private List<String> errorMessage = new ArrayList<>();
	
	/**
	 * Variable that is set to 'true' when the user hits the reading button
	 * This variable is evaluated in the UI's template in order to toggle certain functionality (mostly buttons)
	 */
	private boolean isReading;
	
	/**
	 * Corresponds to the reading speeds supported by the selected reading provider
	 */
	private List<String> supportedReaderSpeed;
	
	/**
	 * The fully qualified names of the existing reading providers
	 */
	private List<String> availableReaderProviders;
	
	/**
	 * Corresponds to the reading languages supported by the selected reading provider
	 */
	private List<String> supportedReaderLanguages;
	
	private static final Map<String, String> supportedVolumeLevels = new LinkedHashMap<>();
	
	/**
	 * Mapping of type <document id>:<document name> that is used to populate the UI's dropdown menu
	 */
	private Map<String, String> availableDocuments;
	
	/**
	 * Common options used in some of the UI's dropdown menus
	 */
	private static final List<String> standardSwitchOptions = new ArrayList<>();
	
	private static ConfigUtil config;
	
	/**
	 *  Used to indirectly manage and query the reader and document instances, the library etc.
	 */
	private static DocumentReaderService service;
	
	public DefaultRoute(String libraryPath, Ini ini, DocumentReaderService documentReaderService) throws Exception {
		this.libraryPath = libraryPath;
		service = documentReaderService;
		config = new ConfigUtil(ini);
		
		supportedVolumeLevels.put("100", "100 %");
		supportedVolumeLevels.put("50", "50 %");
		supportedVolumeLevels.put("25", "25 %");
		supportedVolumeLevels.put("0", "0 %");
		
		standardSwitchOptions.add("off");
		standardSwitchOptions.add("on");
		
		preconfiguration();
	}
	
	/**
	 * Called when the client accesses the root route
	 */
	@Override
	public Object handle(Request request, Response response) throws Exception {
		this.request = request;
		this.response = response;
		this.map = new HashMap<>();

		if (request.requestMethod().equals("GET")) {
			return handleGet();
		}
		
		return handlePost();
	}
	
	/**
	 * Handles GET requests. It updates the variable map using the current values, then passes it to the template
	 * @return an object denoting the content that is set in the response
	 */
	private Object handleGet() {
		map.put("infoMessage", infoMessage.toArray());
		map.put("errorMessage", errorMessage.toArray());
		
		map.put("isReading", isReading);
		map.put("selectedLog", config.getLog());
		map.put("selectedPageContent", config.getContent());
		map.put("selectedFeatureDetection", config.getFeatureDetection());
		map.put("standardSwitchOptions", standardSwitchOptions);
		
		map.put("selectedVolumeLevel", config.getVolume());
		map.put("supportedVolumeLevels", supportedVolumeLevels);
		
		map.put("selectedDocumentPageCount", service.getCurrentDocumentPageCount());
		map.put("selectedDocument", availableDocuments.get(config.getDocumentHash()));
		map.put("availableReaderProviders", availableReaderProviders);
		map.put("selectedReaderLanguage", config.getReaderLanguage());
		map.put("selectedReaderSpeed", config.getReaderSpeed());
		map.put("selectedReaderProvider", config.getReaderProvider());
		map.put("supportedReaderSpeed", supportedReaderSpeed);
		map.put("supportedReaderLanguages", supportedReaderLanguages);
		
		map.put("selectedDocumentHash", config.getDocumentHash());
		map.put("availableDocuments", service.getDocumentMap());
		
		infoMessage.clear();
		errorMessage.clear();
		return new FreeMarkerEngine().render(new ModelAndView(map, "index.ftl"));
	}
	
	/**
	 * Handles POST requests
	 * @return a null reference
	 */
	private Object handlePost() {
		// handles file uploads
	    if (request.contentType().startsWith("multipart/form-data")) {
	    	request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/tmp"));
	    	
		    Part uploadedFilePart = null;
			try {
				uploadedFilePart = request.raw().getPart("uploaded_file");
			} catch (IOException e) {
				errorMessage.add("Could not retrieve the file");
			} catch (ServletException e) {
				// ignore, since with actually check if the request is "multipart/form-data"
			}
			
			if (uploadedFilePart != null) {
			    String fileName = uploadedFilePart.getSubmittedFileName();
			    
			    // replace all spaces with underscores to avoid running in to issues later
			    String secureFileName = fileName.replaceAll("\\s", "_");
			    
			    try (InputStream inputStream = uploadedFilePart.getInputStream()) {
			    	IoUtil.inputStreamToFile(inputStream, libraryPath + File.separator + secureFileName);
			    	
			    	service.updateDocumentLibrary();
			    	availableDocuments = service.getDocumentMap();
			    	infoMessage.add("Successfully uploaded " + fileName);
			    } catch (IOException e) {
					errorMessage.add("Could not upload " + fileName);
			    }
			} else {
				errorMessage.add("Select a file to upload");
			}
	    } else {
	    	String pressedButtonName = RequestUtil.getPressedButtonName(request.body());
	    	
			if (pressedButtonName != null) {
				handleButtonPress(pressedButtonName);
			}
	    }

		response.redirect("/");
		return null; // necessary, but it won't change anything since the response will be already updated
	}
	
	/**
	 * Handles different button presses
	 * @param buttonName the underlying name of a UI button
	 */
	private void handleButtonPress(String buttonName) {
		switch (buttonName) {
		case "btn_delete_document":
			String documentHash = config.getDocumentHash();
			service.deleteDocument(documentHash);
			availableDocuments.remove(documentHash);
			updateDocumentSettings(null);
			break;
		case "btn_start_reading":
			try {
				service.startReading(config.getFeatureDetection().equals("on"));
				isReading = true;
			} catch (Exception e) {
				errorMessage.add("Could not start the reader");
			}
			break;
		case "btn_stop_reading":
			service.stopReading();
			isReading = false;
			break;
		case "btn_reset_bookmark":
			service.resetCurrentDocumentBookmark();
			break;
		case "btn_set_document":
			updateDocumentSettings(RequestUtil.getRequestParameterValue(request.body(), "set_document"));
			break;
		case "btn_set_reader":
			updateReaderSettings(RequestUtil.getRequestParameterValue(request.body(), "set_reader"));
			break;
		case "btn_set_language":
			String selectedReaderLanguage = RequestUtil.getRequestParameterValue(request.body(), "set_language");
			try {
				service.setCurrentReaderLanguage(selectedReaderLanguage);
				config.setReaderLanguage(selectedReaderLanguage);
			} catch (IOException e) {
				errorMessage.add("Could not set the reader's language");
			}
	
			break;
		case "btn_set_reading_speed":
			String selectedReaderSpeed = RequestUtil.getRequestParameterValue(request.body(), "set_reading_speed");
			try {
				service.setCurrentReaderSpeed(selectedReaderSpeed);
				config.setReaderSpeed(selectedReaderSpeed);
			} catch (IOException e) {
				errorMessage.add("Could not set the reader's speed");
			}
			
			break;
		case "btn_set_volume":
			String selectedVolumeLevel = RequestUtil.getRequestParameterValue(request.body(), "set_volume");
			try {
				service.setAudioVolume(Integer.parseInt(selectedVolumeLevel));
				config.setVolume(selectedVolumeLevel);
			} catch (Exception e) {
				errorMessage.add("Could not set the volume");
			}
			break;
		case "btn_set_feature_detection":
			config.setFeatureDetection(RequestUtil.getRequestParameterValue(request.body(), "set_feature_detection"));
			break;
		case "btn_set_logs":
			config.setLog(RequestUtil.getRequestParameterValue(request.body(), "set_logs"));
			break;
		case "btn_set_page_content":
			config.setContent(RequestUtil.getRequestParameterValue(request.body(), "set_page_content"));
			break;
		case "btn_set_device_state":
			String action = RequestUtil.getRequestParameterValue(request.body(), "set_device_state");
			
			try {
				if (action.equals("reboot")) {
					service.rebootDevice();
				} else {
					service.shutDownDevice();
				}
			} catch (IOException e) {
				errorMessage.add("Could not reboot/shutdown the device");
			}
			break;
		case "btn_set_ap_ssid":
			String ssid = RequestUtil.getRequestParameterValue(request.body(), "set_ap_ssid");
			if (ssid != null && ssid.matches("^\\p{ASCII}{1,32}$")) {
				config.setApName(ssid);
				infoMessage.add("Access point name updated");
			} else {
				errorMessage.add("The name should be between 1 and 32 ASCII characters long");
			}
			break;
		case "btn_set_ap_password":
			String password = RequestUtil.getRequestParameterValue(request.body(), "set_ap_password");
			if (password != null && password.matches("^\\p{ASCII}{8,63}$")) {
				config.setApPassword(password);
				infoMessage.add("Access point password updated");
			} else {
				errorMessage.add("The password should be between 8 and 63 ASCII characters long");
			}
			break;
		default:
			Spark.halt(404, "No logic implemented for button " + buttonName);
		}
		
		try {
			config.store();
		} catch (IOException e) {
			errorMessage.add("Could not store the settings");
		}
	}
	
	/**
	 * Makes sure that configuration file values are valid then indirectly updates reader and document related settings 
	 * @throws Exception if an error occurs
	 */
	private void preconfiguration() throws Exception {
		availableReaderProviders = service.getReaderProviders();
		availableDocuments = service.getDocumentMap();
		
		if (!supportedVolumeLevels.containsValue(config.getVolume())) {
			String volume = supportedVolumeLevels.keySet().iterator().next();
			
			int intVolume = Integer.valueOf(volume);
			if (service.getAudioVolume() != intVolume) {
				service.setAudioVolume(intVolume);
			}
			
			config.setVolume(volume);
		}
		
		if (!standardSwitchOptions.contains(config.getFeatureDetection())) {
			config.setFeatureDetection("off");
		}
		
		if (!standardSwitchOptions.contains(config.getLog())) {
			config.setLog("on");
		}
		
		if (!standardSwitchOptions.contains(config.getContent())) {
			config.setContent("on");
		}
		
		updateReaderSettings(config.getReaderProvider());
		updateDocumentSettings(config.getDocumentHash());
		
		config.store();
	}
	
	/**
	 * Updates the reader provider's settings and the list of supported speeds and languages presented in the UI
	 * @param provider a string denoting the full name of a supported reading provider
	 */
	private void updateReaderSettings(String provider) {
		if (!availableReaderProviders.contains(provider)) {
			provider = availableReaderProviders.get(0);
		} 
		
		config.setReaderProvider(provider);
		
		try {
			service.setCurrentReader(provider);
		} catch (Exception e) {
			errorMessage.add("Could not set the current reader");
			return;
		}
		
		supportedReaderSpeed = service.getCurrentSupportedSpeed();
		supportedReaderLanguages = service.getCurrentSupportedLanguages();
		
		config.setReaderSpeed(supportedReaderSpeed.get(0));
		config.setReaderLanguage(supportedReaderLanguages.get(0));
		
		try {
			service.setCurrentReaderSpeed(config.getReaderSpeed());
			service.setCurrentReaderLanguage(config.getReaderLanguage());
		} catch (Exception e) {
			errorMessage.add("Could not update reader settings");
		}
	}
	
	/**
	 * Updates the underlying document to make it point to the currently selected document
	 * @param documentHash a string denoting the hash of an existing document. If the value is null null
	 * and there are documents available, the underlying document will be set to the first document in the document list,
	 * otherwise, the document will remain unset
	 */
	private void updateDocumentSettings(String documentHash) {
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
			errorMessage.add("Could not set the document");
		}
	}
	
	public void setIsReading(boolean flag) {
		isReading = flag;
	}
}
