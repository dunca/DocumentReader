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
	private static final int requiredFileCount = 4;
	private static String projectName;
	private static Logger logger;
	
	static {
		String packageName = Application.class.getPackage().getName();
		logger = Logger.getLogger(packageName);
		projectName = packageName.substring(packageName.lastIndexOf('.') + 1);
	}
	
	public static void main(String[] args) throws Exception {
		FileHandler htmlHandler = new FileHandler(String.format("%s-log.html", projectName), 10240, 1, true);
		htmlHandler.setFormatter(new HtmlLogFormatter());
		logger.addHandler(htmlHandler);
		
		if (!argsArValid(args)) {
			logger.severe("This app requires some arguments in this exact order:\n"
					+ "a path to the document library directory\n"
					+ "a path to the general configuration file (config.ini)\n"
					+ "a path to the hostapd application configuration file (hostapd.ini)\n"
					+ "a path to the bookmark database file (bookmark.ini)"
					);
			return;
		}
		
		if (!Device.isRoot()) {
			logger.severe("This application requires root permissions");
			return;
		}
		
		if (!Device.dependenciesAreSatisfied()) {
			return;
		}
		
		Ini ini = new Ini(new File(args[1]));
		String ipAddress = ini.get("Access point", "ipAddress");
		AccessPoint accessPoint = new AccessPoint(ipAddress, args[2]);
		accessPoint.start();
		
		WebInterface webInterface = new WebInterface(args[0], args[1],
													 new DocumentReaderService(new File(args[0]), new File(args[3])));
		webInterface.start();
	}
	
	private static boolean argsArValid(String[] args) {
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
}
