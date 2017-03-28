package io.github.sidf.documentreader.system;

import java.util.HashMap;
import java.io.IOException;

public class AccessPoint implements Runnable, AutoCloseable {
	private static String ipAddress24;
	private static final String ipAddress = "192.168.13.37";
	
	private static String wlanInterface;
	private static String flushCommand;
	private static String staticIpCommand;
	private static final String[] wlanInterfacePatterns = { "wlan", "wlp" };
	private static final String wlanSearchCommandTemplate = "ls /sys/class/net | grep %s";
	private static final HashMap<String, ValidatableCommand> daemonCommands = new HashMap<>();
	
	public AccessPoint() throws IOException {
		wlanInterface = getWlanInterfaceName();
		ipAddress24 = ipAddress.substring(0, ipAddress.lastIndexOf('.'));
		flushCommand = String.format("ip addr flush dev %s", wlanInterface);
		staticIpCommand = String.format("ip addr add %s/24 broadcast %s.255 dev %s", ipAddress, ipAddress24, wlanInterface);
		
		String hostapdConfigPath = PathHelper.getResourcePath("hostapd/hostapd.ini");
		
		ValidatableCommand hostapdCmd = new ValidatableCommand(String.format("hostapd -B %s", hostapdConfigPath), 0);
		ValidatableCommand dnsmasqCmd = new ValidatableCommand(String.format("dnsmasq --dhcp-authoritative --interface=%s --dhcp-range=%s.50,%s.100,255.255.255.0,6h"
													   						 , wlanInterface, ipAddress24, ipAddress24), 0);
		
		daemonCommands.put("hostapd", hostapdCmd);
		daemonCommands.put("dnsmasq", dnsmasqCmd);
	}
	
	private String getWlanInterfaceName() throws IOException {
		String iface = null;
		
		for (String pattern : wlanInterfacePatterns) {
			String stdout = CommandHelper.launchNonBlockingCommand(String.format(wlanSearchCommandTemplate, pattern)).getStdout();
			
			if (stdout != null && (stdout = stdout.trim()) != "") {
				return stdout.split("\n")[0];
			}
		}

		throw new IOException("Could not identify a wlan interface");
	}

	@Override
	public void close() throws Exception {
		cleanup();
	}
	
	private void cleanup() {
		for (String daemon : daemonCommands.keySet()) {
			CommandHelper.quitUnixProcess(daemon);
		}
		
		CommandHelper.launchNonBlockingCommand(flushCommand);
	}

	@Override
	public void run() {
		start();
	}
	
	private void start() {
		cleanup();
		
		for (ValidatableCommand command : daemonCommands.values()) {
			if (!command.runs()) {
				System.out.println(command);
				cleanup();
				return;
			}
		}
	}
	
	public static void main(String[] stringArray) throws IOException {
		AccessPoint apAccessPoint = new AccessPoint();
		apAccessPoint.run();
	}
}
