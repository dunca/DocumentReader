package io.github.sidf.documentreader;

import java.io.File;
import org.ini4j.Ini;
import java.util.logging.Logger;
import java.util.logging.FileHandler;

import io.github.sidf.documentreader.system.Device;
import io.github.sidf.documentreader.web.WebInterface;
import io.github.sidf.documentreader.system.AccessPoint;
import io.github.sidf.documentreader.util.HtmlLogFormatter;
import io.github.sidf.documentreader.service.DocumentReaderService;

public class Application {
	private static final int requiredFileCount = 3;
	private static String logPath;
	private static Logger logger;
	
	static {
		String packageName = Application.class.getPackage().getName();
		logger = Logger.getLogger(packageName);
		logPath = String.format("%s-log.html", packageName.substring(packageName.lastIndexOf('.') + 1));
		removeOldLogFiles();
	}
	
	public static void main(String[] args) throws Exception {
		FileHandler htmlHandler = new FileHandler(logPath, 1024 * 100, 1, false);
		htmlHandler.setFormatter(new HtmlLogFormatter());
		logger.addHandler(htmlHandler);
		
		if (!areArgsValid(args)) {
			logger.severe("This app requires some arguments in this exact order:\n"
					+ "a path to the document library directory\n"
					+ "a path to the general configuration file (config.ini)\n"
					+ "a path to the hostapd application configuration file (hostapd.ini)\n"
					);
			return;
		}
		
//		if (!Device.isSupported()) {
//			logger.severe("Your system is not supported");
//			return;
//		}
//		
//		if (!Device.dependenciesAreSatisfied()) {
//			return;
//		}
		
		Ini ini = new Ini(new File(args[1]));
		
		String ssid =  ini.get("Access point", "ssid");
		String password =  ini.get("Access point", "password");
		String ipAddress = ini.get("Access point", "ipAddress");
		AccessPoint accessPoint = new AccessPoint(ipAddress, password, ssid, args[2]);
		accessPoint.start();
		
		DocumentReaderService service = new DocumentReaderService(args[0]);
		WebInterface webInterface = new WebInterface(args[0], ini, logPath, service);
		webInterface.start();
	}
	
	private static boolean areArgsValid(String[] args) {
		if (args.length != requiredFileCount) {
			return false;
		}
		
		for (String arg : args) {
			if (!new File(arg).exists()) {
				return false;
			}
		}
		
		return true;
	}
	
	private static void removeOldLogFiles() {
		for (File file : new File(".").listFiles()) {
			if (file.getName().startsWith(logPath)) {
				file.delete();
			}
		}
	}
}
