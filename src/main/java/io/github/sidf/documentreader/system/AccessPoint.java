package io.github.sidf.documentreader.system;

import java.util.Map;
import java.util.Map.Entry;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.github.sidf.documentreader.util.IoUtil;
import io.github.sidf.documentreader.util.CommandUtil;
import io.github.sidf.documentreader.util.CommandResult;
import io.github.sidf.documentreader.util.ValidatableCommand;

/**
 * Represents a wireless access point
 * @author sidf
 */
public class AccessPoint {
	private static Logger logger = Logger.getLogger(AccessPoint.class.getName());
	
	/**
	 * The name of the wireless access point
	 */
	private static String ssid;
	
	/**
	 * The password of the wireless access point
	 */
	private static String password;
	
	/**
	 * Command that clears any IP address that's set on the wlan adapter
	 */
	private static String flushCommand;
	
	/**
	 * The path to the temporary hostapd configuration file. It resides in the /tmp system directory
	 */
	private String tempHostapdConfigPath;
	
	/**
	 * Holds the identifier of the connected wlan adapter (wlanX, wlpXsY, etc.)
	 */
	private static String wlanInterfaceName;
	
	/**
	 * The maximum number of times the {@link #start} method with call itself in case errors during start occur
	 */
	private static int setupRetryCount = 3;
	
	/**
	 * Wlan adapter identifiers usually begin with any of these strings
	 */
	private static final String[] wlanInterfaceNamePatterns = { "wlan", "wlp" };
	
	/**
	 * Command that is used to extract the wlan interface's identifier
	 */
	private static final String wlanInterfaceNameCommandTemplate = "ls /sys/class/net | grep %s";
	
	/**
	 * Mapping of commands that are run in order to start the wlan access point. The keys are used
	 * in the {@link #cleanup()} method in order to terminate potentially existing processes
	 */
	private static final Map<String, ValidatableCommand> validatableCommands = new LinkedHashMap<>();
	
	public AccessPoint(String ipAddress, String password, String ssid) throws Exception {
		AccessPoint.ssid = ssid;
		AccessPoint.password = password;
		
		tempHostapdConfigPath = IoUtil.resourcePathToFile("/hostapd/hostapd.ini");
		
		wlanInterfaceName = getWlanInterfaceName();
		updateHostapdConfigFile();
		
		String ipAddressPart = ipAddress.substring(0, ipAddress.lastIndexOf('.'));
		flushCommand = String.format("ip addr flush dev %s", wlanInterfaceName);
		
		// command that configures the wlan interface with a static IP address
		ValidatableCommand staticIpCmd = new ValidatableCommand(String.format("ip addr add %s/24 broadcast %s.255 dev %s"
																			  , ipAddress, ipAddressPart, wlanInterfaceName));
		
		// command that starts the wlan access point
		ValidatableCommand hostapdCmd = new ValidatableCommand(String.format("hostapd -B %s", tempHostapdConfigPath), true);
		
		// command that starts the DHCP server
		ValidatableCommand dnsmasqCmd = new ValidatableCommand(String.format("dnsmasq --dhcp-authoritative --interface=%s "
																			 + "--dhcp-range=%s.50,%s.100,255.255.255.0,6h"
													   						 , wlanInterfaceName, ipAddressPart, ipAddressPart), true);
		validatableCommands.put("ip", staticIpCmd);
		validatableCommands.put("hostapd", hostapdCmd);
		validatableCommands.put("dnsmasq", dnsmasqCmd);
	}
	
	/**
	 * Gets the identifier of a wlan adapter connected to the device
	 * @return the identifier of a wlan adapter as a string
	 * @throws Exception if a wlan adapter is not connected or an error occurs while trying to 
	 * find out its identifier
	 */
	private String getWlanInterfaceName() throws Exception {
		String iface = null;
		
		for (String pattern : wlanInterfaceNamePatterns) {
			CommandResult commandResult = CommandUtil.executeCommand(String.format(wlanInterfaceNameCommandTemplate, pattern));
			
			if (commandResult.getExitValue() == 0) {
				iface = commandResult.getStdout().split("\n")[0];
				logger.info(String.format("Found a wireless lan interface called %s", iface));
				return iface;
			}
		}
		
		String message = "Could not identify a wireless lan interface";
		
		logger.severe(message);
		throw new IOException(message);
	}
	
	/**
	 * Terminates processes that might interfere with the creation of the access point, such as 
	 * existing hostapd / dnsmasq processes. It also clears the wireless LAN interface's IP address
	 * @throws Exception if an error occurs while trying to run the necessary system commands
	 */
	private void cleanup() throws Exception {
		for (Entry<String, ValidatableCommand> command : validatableCommands.entrySet()) {
			// skip processes that don't run in the background (ip, etc.)
			if (!command.getValue().isBackgroundApp()) {
				continue;
			}
			
			CommandUtil.terminateProcess(command.getKey());
		}
		
		try {
			CommandUtil.executeCommand(flushCommand);
		} catch (IOException e) {
			logger.log(Level.WARNING, "Could not flush the wireless lan interface", e);
		}
	}

	/**
	 * Runs the software that creates the wireless access point (hostapd, dnsmasq, etc.)
	 * @throws Exception if any of the required software fails to run
	 */
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
	
	/**
	 * Updates the hostapd config file with the new values that were provided in the constructor
	 * @throws IOException if an I/O error occurs while trying to read/write the temporary hostapd config file
	 */
	private void updateHostapdConfigFile() throws IOException {
		String content = IoUtil.fileToString(tempHostapdConfigPath);
		content = content.replaceFirst("(?<=interface\\=).*", wlanInterfaceName);
		content = content.replaceFirst("(?<=wpa_passphrase\\=).*", password);
		content = content.replaceFirst("(?<=ssid\\=).*", ssid);
		IoUtil.stringToFile(content, tempHostapdConfigPath);
	}
}
