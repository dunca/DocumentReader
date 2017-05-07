package io.github.sidf.documentreader.system;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.github.sidf.documentreader.util.CommandUtil;
import io.github.sidf.documentreader.util.CommandResult;

/**
 * Class that handles device related I/O 
 * @author sidf
 */
public class Device {
	private static Logger logger = Logger.getLogger(Device.class.getName());
	
	/**
	 * Command that is used to check if a program is installed on the system
	 */
	private static final String debianPackageCheckTemplate = "dpkg -s %s";
	
	/**
	 * Regex pattern that is used to extract the numeric audio volume level
	 */
	private static final Pattern volumePattern = Pattern.compile("(?<=\\[)\\d+(?=%\\])");
	
	/**
	 * List of program packages that contain programs which are necessary for the application to function
	 */
	private static String[] dependecies = { "espeak", "hostapd", "dnsmasq", "libopencv2.4-jni", "poppler-utils",
											"psmisc", "grep", "iproute2", "uvcdynctrl", "procps", "alsa-utils", "coreutils"};
	
	private Device() {
		
	}
	
	/**
	 * Checks if the device fulfills the requirements necessary for the application to function
	 * @return 'true' if all of the following methods return 'true'
	 * <ul>
	 * <li>the operating system is supported</li>
	 * <li>the applications was ran with root permissions</li>
	 * <li>all the software dependencies are satisfied</li>
	 * </ul>
	 */
	public static boolean meetsRequirements() {	
		return osSupported() && runsAsRoot() && dependenciesSatisfied();
	}
	
	/**
	 * Reboots or shuts down the device, depending on the value of the parameter
	 * @param reboot if 'true', the device will be rebooted
	 * @throws IOException if an I/O error occurs while running the system command
	 */
	private static void togglePowerState(boolean reboot) throws IOException {
		Runtime.getRuntime().exec(reboot ? "reboot" : "poweroff");
	}
	
	
	/**
	 * Shuts down the device
	 * @throws IOException if an I/O error occurs
	 */
	public static void shutDown() throws IOException {
		togglePowerState(false);
	}
	
	/**
	 * Reboots the device
	 * @throws IOException if an I/O error occurs
	 */
	public static void reboot() throws IOException {
		togglePowerState(true);
	}
	
	/**
	 * Sets the audio volume level
	 * @param level a integer between 0 and 100, denoting the volume level
	 * @throws Exception if the volume cannot be set due to various reasons (amixer is not installed)
	 */
	public static void setVolume(int level) throws Exception {
		CommandUtil.executeCommand(String.format("amixer sset PCM %d%%", level));
	}
	
	/**
	 * Gets the audio volume level
	 * @return an integer between 0 and 100 denoting the current volume level
	 * @throws Exception if the output provided by amixer cannot be parsed or if an I/O error occurs
	 */
	public static Integer getVolume() throws Exception {
		CommandResult commandResult = CommandUtil.executeCommand("amixer get PCM | tail -1");
		Matcher matcher = volumePattern.matcher(commandResult.getStdout());
		
		if (!matcher.find()) {
			throw new IOException("Could not read the speaker's volume");
		}
		
		return Integer.valueOf(matcher.group());
	}
	
	/**
	 * Checks if all the programs that this application relies on in order to function are installed
	 * @return 'true' if all the required programs seem to be installed
	 */
	private static boolean dependenciesSatisfied() {
		List<String> unsatisfiedDependencies = new ArrayList<>();
		
		for (String dependency : dependecies) {
			CommandResult commandResult = null;
			
			try {
				commandResult = CommandUtil.executeCommand(String.format(debianPackageCheckTemplate, dependency));
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Couldn't check if dependency is satisfied", e);
				return false;
			}
			
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
	
	/**
	 * @return 'true' if the application seems to be running with root permissions
	 */
	private static boolean runsAsRoot() {
		CommandResult commandResult = null;
		
		try {
			commandResult = CommandUtil.executeCommand("whoami");
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Couldn't check if the program is running as root", e);
			return false;
		} 
		
		return commandResult.getStdout().equals("root");
	}
	
	/**
	 * @return 'true' if the operating system running on the device is supported  by the
	 * application. (Debian or Debian derivatives)
	 */
	private static boolean osSupported() {
		return new File("/etc/debian_version").exists();
	}
}
