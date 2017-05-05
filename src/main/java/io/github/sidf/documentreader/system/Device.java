package io.github.sidf.documentreader.system;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Logger;

import io.github.sidf.documentreader.util.CommandUtil;
import io.github.sidf.documentreader.util.CommandResult;

public class Device {
	private static Logger logger = Logger.getLogger(Device.class.getName());
	
	private static final String debianPackageCheckTemplate = "dpkg -s %s";
	private static final Pattern volumePattern = Pattern.compile("(?<=\\[)\\d+(?=%\\])");
	private static String[] dependecies = new String[] { "espeak", "hostapd", "dnsmasq", "libopencv2.4-jni", 
														 "poppler-utils", "psmisc", "grep", "iproute2", "uvcdynctrl", "procps"};
	
	private Device() {
		
	}
	
	public static boolean meetsRequirements() {
		boolean runsAsRoot = false;
		boolean dependenciesSatisfied = false;
		
		try {
			runsAsRoot = runsAsRoot();
		} catch (Exception e) {
			//
		}
		
		try {
			dependenciesSatisfied = dependenciesSatisfied();
		} catch (Exception e) {
			//
		}
		
		return osSupported() && runsAsRoot && dependenciesSatisfied;
	}
	
	private static void togglePowerState(boolean reboot) throws IOException {
		Runtime.getRuntime().exec(reboot ? "reboot" : "poweroff");
	}
	
	public static void shutDown() throws IOException {
		togglePowerState(false);
	}
	
	public static void reboot() throws IOException {
		togglePowerState(true);
	}
	
	public static void setVolume(int level) throws Exception {
		CommandUtil.executeCommand(String.format("amixer sset PCM %d%%", level));
	}
	
	public static Integer getVolume() throws Exception {
		CommandResult commandResult = CommandUtil.executeCommand("amixer get PCM | tail -1");
		Matcher matcher = volumePattern.matcher(commandResult.getStdout());
		
		if (!matcher.find()) {
			throw new IOException("Could not read the speaker's volume");
		}
		
		return Integer.valueOf(matcher.group());
	}
	
	private static boolean dependenciesSatisfied() throws Exception {
		List<String> unsatisfiedDependencies = new ArrayList<>();
		
		for (String dependency : dependecies) {
			CommandResult commandResult = CommandUtil.executeCommand(String.format(debianPackageCheckTemplate, dependency));
			if (commandResult.getExitValue() != 0) {
				unsatisfiedDependencies.add(dependency);
			}
		}
		
		if (unsatisfiedDependencies.size() != 0) {
			String joinedUnsatisfied = String.join("\n", unsatisfiedDependencies);
			logger.severe(String.format("The following dependencies are missing:\n%s", joinedUnsatisfied));
			return false;
		}
		
		return true;
	}
	
	private static boolean runsAsRoot() throws Exception {
		CommandResult commandResult = CommandUtil.executeCommand("whoami");
		return commandResult.getStdout().equals("root");
	}
	
	private static boolean osSupported() {
		return new File("/etc/debian_version").exists();
	}
}
