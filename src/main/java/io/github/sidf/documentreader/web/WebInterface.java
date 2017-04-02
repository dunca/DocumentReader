package io.github.sidf.documentreader.web;

import spark.Route;
import spark.Spark;
import java.io.File;
import org.ini4j.Ini;
import java.io.IOException;
import org.ini4j.InvalidFileFormatException;

import io.github.sidf.documentreader.service.DocumentReaderService;

public class WebInterface {
	private int port;
	private String configPath;
	private String libraryPath;
	private static DocumentReaderService service;
	
	public WebInterface(String libraryPath, String configPath, DocumentReaderService documentReaderService) throws InvalidFileFormatException, IOException {
		Ini ini = new Ini(new File(configPath));
		this.port = Integer.valueOf(ini.get("Web UI", "port"));
		
		this.configPath = configPath;
		this.libraryPath = libraryPath;
		service = documentReaderService;
	}
	
	public void start() throws Exception {
		Spark.port(port);
		Spark.ipAddress("0.0.0.0");
		Spark.staticFileLocation("/spark/public");
		Route route = new RootRoute(libraryPath, configPath, service);
		
		Spark.get("/", route);
		Spark.post("/", route);
	}
	
	public void stop() {
		Spark.stop();
	}
}
