package io.github.sidf.documentreader.ui;

import spark.Route;
import spark.Spark;
import org.ini4j.Ini;
import spark.Request;
import spark.Response;
import java.io.IOException;

import io.github.sidf.documentreader.util.IoUtil;
import io.github.sidf.documentreader.ui.util.ConfigUtil;
import io.github.sidf.documentreader.service.DocumentReaderService;

public class UiManager {
	private Ini ini;
	private int port;
	private String logPath;
	private String libraryPath;
	private static DocumentReaderService service;
	
	public UiManager(String libraryPath, Ini ini, String logPath,
						DocumentReaderService documentReaderService) throws IOException {
		ConfigUtil configUtil = new ConfigUtil(ini);
		
		this.port = Integer.valueOf(configUtil.getPort());
		
		this.ini = ini;
		this.logPath = logPath;
		this.libraryPath = libraryPath;
		service = documentReaderService;
	}
	
	public void start() throws Exception {
		Spark.port(port);
		Spark.ipAddress("0.0.0.0");
		Spark.staticFileLocation("/spark/public");
		DefaultRoute defaultRoute = new DefaultRoute(libraryPath, ini, service);
		
		Spark.get("/", defaultRoute);
		
		Spark.post("/", defaultRoute);
		
		Spark.get("/log", new Route() {
			@Override
			public Object handle(Request request, Response response) throws Exception {
				return IoUtil.fileToString(logPath);
			}
		});
		
		Spark.get("/currentPage", new Route() {
			@Override
			public Object handle(Request request, Response response) {
				return String.format("<p><b>Page %s</b></p>", service.getCurrentPageNumber()) + service.getCurrentPageContent();
			}
		});
		
		Spark.get("/isReading", new Route() {
			@Override
			public Object handle(Request request, Response response) {
				boolean isReading = service.isReading();
				
				if (!service.isReading()) {
					defaultRoute.setIsReading(false);
				}
				
				return isReading;
			}
		});
	}
	
	public void stop() {
		Spark.stop();
	}
}
