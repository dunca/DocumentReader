package io.github.sidf.documentreader.system;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Logger;

import io.github.sidf.documentreader.util.CommandUtil;
import io.github.sidf.documentreader.util.CommandResult;
import io.github.sidf.documentreader.system.enums.JvmArchitecture;
import io.github.sidf.documentreader.system.enums.OperatingSystem;

public class Device {
	private static final String debianPackageCheckTemplate = "dpkg -s %s";
	private static Logger logger = Logger.getLogger(Device.class.getName());
	private static final Pattern volumePattern = Pattern.compile("(?<=\\[)\\d+(?=%\\])");
	private static String[] dependecies = { "espeak", "hostapd", "dnsmasq", "libopencv2.4-jni", "poppler-utils",
											"psmisc", "grep", "iproute2", "uvcdynctrl"};
	
	public static OperatingSystem getOperatingSystem() {
		if (System.getProperty("os.name").contains("Windows")) {
			return OperatingSystem.WINDOWS;
		}
		return OperatingSystem.LINUX;
	}
	
	public static JvmArchitecture getJvmArchitecture() {
		String arch = System.getProperty("sun.arch.data.model");
		return arch == "64" ? JvmArchitecture.JVM64 : JvmArchitecture.JVM32;
	}
	
	private static void togglePowerState(boolean reboot) throws IOException {
		String command = null;
		
		switch (getOperatingSystem()) {
			case WINDOWS:
				command = "shutdown.exe " + (reboot ? "-r" : "-s");
				break;
	
			case LINUX:
				command = "shutdown "  + (reboot ? "-r" : "-h");;
				break;
		}
		
		Runtime.getRuntime().exec(command);
	}
	
	public static void shutDown() throws IOException {
		togglePowerState(false);
	}
	
	public static void reboot() throws IOException {
		togglePowerState(true);
	}
	
	public static void setVolume(int level) throws Exception {
		CommandUtil.launchNonBlockingCommand(String.format("amixer sset PCM %d%%", level));
	}
	
	public static Integer getVolume() throws Exception {
		 CommandResult commandResult = CommandUtil.launchNonBlockingCommand("amixer get PCM | tail -1");
		
		Matcher matcher = volumePattern.matcher(commandResult.getStdout());
		if (!matcher.find()) {
			throw new IOException("Could not parse the speaker's volume");
		}
		
		return Integer.valueOf(matcher.group());
	}
	
	public static boolean dependenciesAreSatisfied() throws Exception {
		List<String> unsatisfiedDependencies = new ArrayList<>();
		
		for (String dependency : dependecies) {
			CommandResult commandResult = CommandUtil.launchNonBlockingCommand(String.format(debianPackageCheckTemplate, dependency));
			if (commandResult.exitValue != 0) {
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
	
	public static boolean isRoot() throws Exception {
		CommandResult commandResult = CommandUtil.launchNonBlockingCommand("whoami");
		return commandResult.getStdout().equals("root");
	}
}
