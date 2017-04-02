package io.github.sidf.documentreader.web;

import io.github.sidf.documentreader.service.DocumentReaderService;
import spark.Route;
import spark.Spark;

public class WebInterface {
	private String configPath;
	private String libraryPath;
	private static DocumentReaderService service;
	
	public WebInterface(String libraryPath, String configPath, DocumentReaderService documentReaderService) {
		this.configPath = configPath;
		this.libraryPath = libraryPath;
		documentReaderService = service;
	}
	
	public void start() {
		Spark.port(80);
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
