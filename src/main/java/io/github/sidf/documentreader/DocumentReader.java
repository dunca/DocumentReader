package io.github.sidf.documentreader;

import java.io.File;
import org.ini4j.Ini;
import java.util.logging.Logger;
import java.util.logging.FileHandler;

import io.github.sidf.documentreader.ui.WebUi;
import io.github.sidf.documentreader.system.Device;
import io.github.sidf.documentreader.system.AccessPoint;
import io.github.sidf.documentreader.util.HtmlLogFormatter;
import io.github.sidf.documentreader.service.DocumentReaderService;

/**
 * This class represents entry point of the application
 * @author sidf
 */
public class DocumentReader {
	/**
	 * The name of the application's configuration file
	 */
	private static final String configFileName = "config.ini";
	
	/**
	 * The path to the HTML formatted log file that will be loaded in the web UI
	 */
	private static String logPath;
	private static Logger logger;
	
	static {
		String packageName = DocumentReader.class.getPackage().getName();
		logger = Logger.getLogger(packageName);
		
		logPath = String.format("%s-log.html", packageName.substring(packageName.lastIndexOf('.') + 1));
	}
	
	public static void main(String[] args) throws Exception {
		FileHandler htmlHandler = new FileHandler(logPath, 1024 * 100, 1, false);
		htmlHandler.setFormatter(new HtmlLogFormatter());
		logger.addHandler(htmlHandler);
		
		if (!isValidStart(args)) {
			logger.severe("Cannot start application:\n"
					+ "Make sure that you specify the document library directory as an argument\n"
					+ "and that you have a valid configuration file called " + configFileName + "\n"
					);
			return;
		}
		
//		if (!Device.meetsRequirements()) {
//			logger.severe("Your system is not supported");
//			return;
//		}
			
		Ini ini = new Ini(new File(configFileName));
		
		String ssid =  ini.get("Access point", "ssid");
		String password =  ini.get("Access point", "password");
		String ipAddress = ini.get("Access point", "ipAddress");
		AccessPoint accessPoint = new AccessPoint(ipAddress, password, ssid);
		accessPoint.start();
		
		DocumentReaderService service = new DocumentReaderService(args[0]);
		WebUi webUi = new WebUi(args[0], ini, logPath, service);
		webUi.start();
	}
	
	private static boolean isValidStart(String[] args) {
		return args.length == 1 && new File(args[0]).isDirectory() && new File(configFileName).isFile();
	}
}
