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

/**
 * Class that manages the server's configuration and configures the routes
 * @author sidf
 */
public class WebUi {
	private Ini ini;
	private int port;
	private String logPath;
	private String libraryPath;
	private static DocumentReaderService service;
	
	public WebUi(String libraryPath, Ini ini, String logPath, DocumentReaderService documentReaderService) throws IOException {
		ConfigUtil configUtil = new ConfigUtil(ini);
		
		this.port = Integer.valueOf(configUtil.getPort());
		
		this.ini = ini;
		this.logPath = logPath;
		this.libraryPath = libraryPath;
		service = documentReaderService;
	}
	
	public void start() throws Exception {
		// configures the underlying server
		Spark.port(port);
		Spark.ipAddress("0.0.0.0");
		Spark.staticFileLocation("/spark/public");
		
		// the main route. The only route that's meant to be used directly by the UI's user
		DefaultRoute defaultRoute = new DefaultRoute(libraryPath, ini, service);
		
		Spark.get("/", defaultRoute);
		
		Spark.post("/", defaultRoute);
		
		// a route which returns the content of the HTML log file
		Spark.get("/log", new Route() {
			@Override
			public Object handle(Request request, Response response) throws Exception {
				return IoUtil.fileToString(logPath);
			}
		});
		
		// a route which returns the content of the page that's being read
		Spark.get("/currentPage", new Route() {
			@Override
			public Object handle(Request request, Response response) {
				return String.format("<p><b>Page %s</b></p>", service.getCurrentPageNumber()) + service.getCurrentPageContent();
			}
		});
		
		// a route which returns 'true' or 'false', depending if reading is taking place
		// this method is used to handle the automatic page refresh after the reader runs out of document pages
		Spark.get("/isReading", new Route() {
			@Override
			public Object handle(Request request, Response response) {
				boolean isReading = service.isReading();
				
				if (!service.isReading()) {
					// necessary, otherwise the UI won't change it's state, unless the user manually stops reading
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
