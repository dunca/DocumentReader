package io.github.sidf.documentreader.system;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import java.io.IOException;
import java.util.LinkedHashMap;
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
	private static String flushCommand;
	private String tempHostapdConfigPath;
	private static String wlanInterfaceName;
	
	private static int setupRetryCount = 3;
	private static final String[] wlanInterfaceNamePatterns = { "wlan", "wlp" };
	private static final String wlanInterfaceNameCommandTemplate = "ls /sys/class/net | grep %s";
	private static final Map<String, ValidatableCommand> validatableCommands = new LinkedHashMap<>();
	
	public AccessPoint(String ipAddress, String password, String ssid) throws Exception {
		AccessPoint.ssid = ssid;
		AccessPoint.password = password;
		
		tempHostapdConfigPath = PathUtil.resourcePathToTempFilePath("hostapd/hostapd.ini");
		
		wlanInterfaceName = getWlanInterfaceName();
		updateHostapdConfigFile();
		
		String ipAddressPart = ipAddress.substring(0, ipAddress.lastIndexOf('.'));
		flushCommand = String.format("ip addr flush dev %s", wlanInterfaceName);
		
		ValidatableCommand staticIpCmd = new ValidatableCommand(String.format("ip addr add %s/24 broadcast %s.255 dev %s"
																			  , ipAddress, ipAddressPart, wlanInterfaceName), 0);
		ValidatableCommand hostapdCmd = new ValidatableCommand(String.format("hostapd -B %s", tempHostapdConfigPath), true, 0);
		ValidatableCommand dnsmasqCmd = new ValidatableCommand(String.format("dnsmasq --dhcp-authoritative --interface=%s "
																			 + "--dhcp-range=%s.50,%s.100,255.255.255.0,6h"
													   						 , wlanInterfaceName, ipAddressPart, ipAddressPart), true, 0);
		validatableCommands.put("ip", staticIpCmd);
		validatableCommands.put("hostapd", hostapdCmd);
		validatableCommands.put("dnsmasq", dnsmasqCmd);
	}
	
	private String getWlanInterfaceName() throws Exception {
		String iface = null;
		
		for (String pattern : wlanInterfaceNamePatterns) {
			String stdout = CommandUtil.launchNonBlockingCommand(String.format(wlanInterfaceNameCommandTemplate, pattern)).getStdout();
			
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
		for (Entry<String, ValidatableCommand> command : validatableCommands.entrySet()) {
			if (!command.getValue().isBackground()) {
				continue;
			}
			
			try {
				CommandUtil.quitUnixProcess(command.getKey());
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
		
		for (ValidatableCommand command : validatableCommands.values()) {
			if (!command.runs()) {
				String message = String.format("Command %s did not run as intended", command.getCommand());
				logger.severe(message);

				if (setupRetryCount-- > 0) {
					logger.info("Retrying to set up access point");
					start();
				}
				
				cleanup();
				throw new IOException(message);
			}
		}
	}
	
	private void updateHostapdConfigFile() throws IOException {
		String content = FileUtil.fileToString(new File(tempHostapdConfigPath));
		content = content.replaceFirst("(?<=interface\\=).*", wlanInterfaceName);
		content = content.replaceFirst("(?<=wpa_passphrase\\=).*", password);
		content = content.replaceFirst("(?<=ssid\\=).*", ssid);
		FileUtil.stringToFile(content, tempHostapdConfigPath);
	}
}
