package io.github.sidf.documentreader.web;

import spark.Spark;

public class WebInterface {
	private String configPath;
	private String libraryPath;
	private String hostapdConfigPath;
	
	public WebInterface(String libraryPath, String configPath, String hostapdConfigPath) {
		this.configPath = configPath;
		this.libraryPath = libraryPath;
		this.hostapdConfigPath = hostapdConfigPath;
	}
	
	public void start() {
		Spark.port(80);
		Spark.ipAddress("0.0.0.0");
		Spark.staticFileLocation("/spark/public");
		
		Spark.get("/", new RootRoute(libraryPath, configPath, hostapdConfigPath));
		Spark.post("/", new RootRoute(libraryPath, configPath, hostapdConfigPath));
	}
	
	public void stop() {
		Spark.stop();
	}
	
	
	public static void main(String[] args) throws InterruptedException {
		WebInterface webInterface = new WebInterface("","", "");
		webInterface.start();
	}
}
