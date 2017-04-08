package io.github.sidf.documentreader.web.util;

import java.io.File;
import org.ini4j.Ini;
import java.io.IOException;

public class ConfigUtil {
	private Ini ini;
	
	public ConfigUtil(String configPath) throws IOException {
		this.ini = new Ini(new File(configPath));
	}
	
	public String getDocumentHash() {
		return ini.get("Document", "selectedDocumentHash");
	}
	
	public String getFeatureDetection() {
		return ini.get("Feature detection", "featureDetection");
	}
	
	public String getReaderProvider() {
		return ini.get("Reader", "provider");
	}
	
	public String getReaderLanguage() {
		return ini.get("Reader", "language");
	}
	
	public String getReaderSpeed() {
		return ini.get("Reader", "speed");
	}
	
	public String getVolume() {
		return ini.get("Device", "volume");
	}
	
	public String getLog() {
		return ini.get("Web UI", "log");
	}
	
	public String getContent() {
		return ini.get("Web UI", "content");
	}
	
	public String getPort() {
		return ini.get("Web UI", "port");
	}
	
	public String getApPassword() {
		return ini.get("Access point", "password");
	}
	
	public String getIpAddress() {
		return ini.get("Access point", "ipAddress");
	}
	
	public void setDocumentHash(String hash) {
		ini.put("Document", "selectedDocumentHash", hash);
	}
	
	public void setFeatureDetection(String mode) {
		ini.put("Feature detection", "featureDetection", mode);
	}
	
	public void setReaderProvider(String provider) {
		ini.put("Reader", "provider", provider);
	}
	
	public void setReaderLanguage(String language) {
		ini.put("Reader", "language", language);
	}
	
	public void setReaderSpeed(String speed) {
		ini.put("Reader", "speed", speed);
	}
	
	public void setVolume(String volume) {
		ini.put("Device", "volume", volume);
	}
	
	public void setLog(String mode) {
		ini.put("Web UI", "log", mode);
	}
	
	public void setContent(String mode) {
		ini.put("Web UI", "content", mode);
	}
	
	public void setApPassword(String password) {
		ini.put("Access point", "password", password);
	}
	
	public void store() throws IOException {
		ini.store();
	}
}
