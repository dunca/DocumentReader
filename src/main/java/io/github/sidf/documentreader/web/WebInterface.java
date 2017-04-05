package io.github.sidf.documentreader.web;

import spark.Route;
import spark.Spark;
import java.io.File;
import spark.Request;
import spark.Response;
import java.io.IOException;
import org.ini4j.InvalidFileFormatException;

import io.github.sidf.documentreader.util.FileUtil;
import io.github.sidf.documentreader.web.util.ConfigUtil;
import io.github.sidf.documentreader.service.DocumentReaderService;

public class WebInterface {
	private int port;
	private String logPath;
	private String configPath;
	private String libraryPath;
	private String currentPagePath;
	private static DocumentReaderService service;
	
	public WebInterface(String libraryPath, String configPath, String logPath, DocumentReaderService documentReaderService) throws InvalidFileFormatException, IOException {
		ConfigUtil configUtil = new ConfigUtil(configPath);
		
		this.port = Integer.valueOf(configUtil.getPort());
		
		this.logPath = logPath;
		this.configPath = configPath;
		this.libraryPath = libraryPath;
		service = documentReaderService;
		
		currentPagePath = configUtil.getCurrentPagePath();
	}
	
	public void start() throws Exception {
		Spark.port(port);
		Spark.ipAddress("0.0.0.0");
		Spark.staticFileLocation("/spark/public");
		Route route = new RootRoute(libraryPath, configPath ,service);
		
		Spark.get("/", route);
		
		Spark.post("/", route);
		
		Spark.get("/log", new Route() {
			@Override
			public Object handle(Request request, Response response) throws Exception {
				return FileUtil.fileToString(new File(logPath));
			}
		});
		
		Spark.get("/currentPage", new Route() {
			@Override
			public Object handle(Request request, Response response) throws Exception {
				return FileUtil.fileToString(new File(currentPagePath));
			}
		});
	}
	
	public void stop() {
		Spark.stop();
	}
}
