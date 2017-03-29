package io.github.sidf.documentreader.system;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandUtil {
	private static Logger logger = Logger.getLogger(StreamUtil.class.getName());
	
	private CommandUtil() {
		
	}
	
	public static CommandResult launchNonBlockingCommand(String command) throws IOException {
		String[] commandArray = null;
		
		if (command.contains("|")) {
			commandArray = new String[] {"/bin/sh", "-c", command};
		} else {
			commandArray = command.split(" ");
		}
		
		ProcessBuilder builder = new ProcessBuilder(commandArray);
		builder.redirectErrorStream(true);
		
		logger.info(String.format("Trying to run command %s", command));
		Process process = builder.start();
		
		int exitCode = -1;
		
		try {
			exitCode = process.waitFor();
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, String.format("Command %s was interrupted", command), e);
		}
		
		return new CommandResult(StreamUtil.inputStreamToString(process.getInputStream()), exitCode);
	}
	
	public static void quitUnixProcess(String processName) throws IOException {
		try {
			launchNonBlockingCommand(String.format("killall %s", processName));
		} catch (IOException e) {
			logger.log(Level.SEVERE, String.format("Could not quit %s", processName), e);
			if (isProcessRunning(processName)) {
				throw e;
			}
		}
	}
	
	public static boolean isProcessRunning(String processName) {
		try {
			launchNonBlockingCommand(String.format("ps aux | grep -v grep | grep %s", processName));
		} catch (IOException e) {
			logger.log(Level.SEVERE, String.format("Could check if %s is running", processName), e);
			return false;
		}
		
		return true;
	}
}
