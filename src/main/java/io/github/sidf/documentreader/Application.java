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
	private static final String configName = "config.ini";
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
		
		if (!isValidStart(args)) {
			logger.severe("Cannot start application:\n"
					+ "Make sure that you specify the document library directory as an argument\n"
					+ "and that you have a valid configuration file called " + configName + "\n"
					);
			return;
		}
		
//		if (!Device.meetsRequirements()) {
//			logger.severe("Your system is not supported");
//			return;
//		}
			
		Ini ini = new Ini(new File(configName));
		
		String ssid =  ini.get("Access point", "ssid");
		String password =  ini.get("Access point", "password");
		String ipAddress = ini.get("Access point", "ipAddress");
		AccessPoint accessPoint = new AccessPoint(ipAddress, password, ssid);
		accessPoint.start();
		
		DocumentReaderService service = new DocumentReaderService(args[0]);
		WebInterface webInterface = new WebInterface(args[0], ini, logPath, service);
		webInterface.start();
	}
	
	private static boolean isValidStart(String[] args) {
		return args.length == 1 && new File(args[0]).isDirectory() && new File(configName).isFile();
	}
	
	private static void removeOldLogFiles() {
		for (File file : new File(".").listFiles()) {
			if (file.getName().startsWith(logPath)) {
				file.delete();
			}
		}
	}
}
