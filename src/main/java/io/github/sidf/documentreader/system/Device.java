package io.github.sidf.documentreader.system;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Device {
	private static Logger logger = Logger.getLogger(Device.class.getName());
	private static final Pattern volumePattern = Pattern.compile("(?<=\\[)\\d+(?=%\\])");
	
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
	
	public static void shutDown() {
		shutDown(3);
	}
	
	public static void shutDown(int countdown) {
		String command = null;
		
		switch (getOperatingSystem()) {
			case WINDOWS:
				command = "shutdown.exe -s -t %d";
				break;
	
			case LINUX:
				command = "shutdown -h %d";
				break;
		}
		
		try {
			logger.info("Trying to shut down the system");
			Runtime.getRuntime().exec(String.format(command, countdown));
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Could not shut down the system", e);
		}
	}
	
	public static void setVolume(int level) {
		try {
			CommandUtil.launchNonBlockingCommand(String.format("amixer sset PCM %d%%", level));
		} catch (IOException e) {
			logger.log(Level.WARNING, "Could not set the speaker's volume", e);
		}
	}
	
	public static Integer getVolume() {
		CommandResult commandResult = null;
		
		try {
			commandResult = CommandUtil.launchNonBlockingCommand("amixer get PCM | tail -1");
		} catch (IOException e) {
			logger.log(Level.WARNING, "Could not get the speaker's volume", e);
		}
		
		if (commandResult == null) {
			return null;
		}
		
		Matcher matcher = volumePattern.matcher(commandResult.getStdout());
		if (!matcher.find()) {
			return null;
		}
		
		return Integer.valueOf(matcher.group());
	}
}
