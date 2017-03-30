package io.github.sidf.documentreader.system;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.sidf.documentreader.system.enums.JvmArchitecture;
import io.github.sidf.documentreader.system.enums.OperatingSystem;
import io.github.sidf.documentreader.util.CommandResult;
import io.github.sidf.documentreader.util.CommandUtil;

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
}
