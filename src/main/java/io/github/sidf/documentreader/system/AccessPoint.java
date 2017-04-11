package io.github.sidf.documentreader.system;

import java.io.File;
import java.util.HashMap;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.github.sidf.documentreader.util.FileUtil;
import io.github.sidf.documentreader.util.PathUtil;
import io.github.sidf.documentreader.util.CommandUtil;
import io.github.sidf.documentreader.util.ValidatableCommand;

public class AccessPoint {
	private static Logger logger = Logger.getLogger(AccessPoint.class.getName());

	private static String ssid;
	private static String password;
	private String hostapdConfigPath;
	private static String flushCommand;
	private static String wlanInterface;
	private static final String[] wlanInterfacePatterns = { "wlan", "wlp" };
	private static final String wlanSearchCommandTemplate = "ls /sys/class/net | grep %s";
	private static final HashMap<String, ValidatableCommand> nonBlockingCommands = new HashMap<>();
	
	public AccessPoint(String ipAddress, String password, String ssid) throws Exception {
		AccessPoint.ssid = ssid;
		AccessPoint.password = password;
		
		hostapdConfigPath = PathUtil.resourcePathToFile("hostapd/hostapd.ini");
		
		wlanInterface = getWlanInterfaceName();
		updateConfigFile();
		
		String ipAddress24 = ipAddress.substring(0, ipAddress.lastIndexOf('.'));
		flushCommand = String.format("ip addr flush dev %s", wlanInterface);
		
		ValidatableCommand staticIpCmd = new ValidatableCommand(String.format("ip addr add %s/24 broadcast %s.255 dev %s"
																			  , ipAddress, ipAddress24, wlanInterface), 0);
		ValidatableCommand hostapdCmd = new ValidatableCommand(String.format("hostapd -B %s", hostapdConfigPath), 0);
		ValidatableCommand dnsmasqCmd = new ValidatableCommand(String.format("dnsmasq --dhcp-authoritative --interface=%s "
																			 + "--dhcp-range=%s.50,%s.100,255.255.255.0,6h"
													   						 , wlanInterface, ipAddress24, ipAddress24), 0);
		nonBlockingCommands.put("ip", staticIpCmd);
		nonBlockingCommands.put("hostapd", hostapdCmd);
		nonBlockingCommands.put("dnsmasq", dnsmasqCmd);
	}
	
	private String getWlanInterfaceName() throws Exception {
		String iface = null;
		
		for (String pattern : wlanInterfacePatterns) {
			String stdout = CommandUtil.launchNonBlockingCommand(String.format(wlanSearchCommandTemplate, pattern)).getStdout();
			
			if (stdout != null && (stdout = stdout.trim()) != "") {
				iface = stdout.split("\n")[0];
				logger.info(String.format("Found a wireless lan interface called %s", iface));
				return iface;
			}
		}
		
		String message = "Could not identify a wireless lan interface";
		logger.severe(message);
		throw new IOException(message);
	}

	private void cleanup() throws Exception {
		for (String command : nonBlockingCommands.keySet()) {
			try {
				CommandUtil.quitUnixProcess(command);
			} catch (IOException e) {
				throw e;
			}
		}
		
		try {
			CommandUtil.launchNonBlockingCommand(flushCommand);
		} catch (IOException e) {
			logger.log(Level.WARNING, "Could not flush the wireless lan interface", e);
		}
		
	}

	public void start() throws Exception {
		cleanup();
		
		for (ValidatableCommand command : nonBlockingCommands.values()) {
			if (!command.runs()) {
				String message = String.format("Command %s did not run as intended", command.getCommand());
				logger.severe(message);
				cleanup();
				
				throw new IOException(message);
			}
		}
	}
	
	private void updateConfigFile() throws IOException {
		String content = FileUtil.fileToString(new File(hostapdConfigPath));
		content = content.replaceFirst("(?<=interface\\=).*", wlanInterface);
		content = content.replaceFirst("(?<=wpa_passphrase\\=).*", password);
		content = content.replaceFirst("(?<=ssid\\=).*", ssid);
		FileUtil.stringToFile(content, hostapdConfigPath);
	}
}
