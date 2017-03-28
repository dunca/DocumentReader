package io.github.sidf.documentreader.system;

import java.util.HashMap;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AccessPoint implements AutoCloseable {
	private static Logger logger = Logger.getLogger(AccessPoint.class.getName());

	private final String ipAddress;
	private final String ipAddress24;
	private static String flushCommand;
	private static AccessPoint instance;
	private static String wlanInterface;
	private static final String[] wlanInterfacePatterns = { "wlan", "wlp" };
	private static final String wlanSearchCommandTemplate = "ls /sys/class/net | grep %s";
	private static final HashMap<String, ValidatableCommand> nonBlockingCommands = new HashMap<>();
	
	private AccessPoint(String ipAddr) throws IOException {
		wlanInterface = getWlanInterfaceName();
		
		ipAddress = ipAddr;
		ipAddress24 = ipAddress.substring(0, ipAddress.lastIndexOf('.'));
		flushCommand = String.format("ip addr flush dev %s", wlanInterface);
		String hostapdConfigPath = PathHelper.getResourcePath("hostapd/hostapd.ini");
		
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
	
	private AccessPoint() throws IOException {
		this("192.168.13.37");
	}
	
	public AccessPoint getInstance() throws IOException {
		if (instance == null) {
			instance =  new AccessPoint();
		}
		
		return instance;
	}
	
	private String getWlanInterfaceName() throws IOException {
		String iface = null;
		
		for (String pattern : wlanInterfacePatterns) {
			String stdout = CommandHelper.launchNonBlockingCommand(String.format(wlanSearchCommandTemplate, pattern)).getStdout();
			
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

	private void cleanup() throws IOException {
		for (String command : nonBlockingCommands.keySet()) {
			try {
				CommandHelper.quitUnixProcess(command);
			} catch (IOException e) {
				throw e;
			}
		}
		
		try {
			CommandHelper.launchNonBlockingCommand(flushCommand);
		} catch (IOException e) {
			logger.log(Level.WARNING, "Could not flush the wireless lan interface", e);
		}
		
	}

	public void start() throws IOException {
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
	
	@Override
	public void close() throws Exception {
		cleanup();
	}
}
