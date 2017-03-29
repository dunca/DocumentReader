package io.github.sidf.documentreader.util;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandUtil {
	private static Logger logger = Logger.getLogger(StreamUtil.class.getName());
	
	private CommandUtil() {
		
	}
	
	public static CommandResult launchNonBlockingCommand(String command) throws IOException, InterruptedException {
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
		
		int exitCode = process.waitFor();
		return new CommandResult(StreamUtil.inputStreamToString(process.getInputStream()), exitCode);
	}
	
	public static void quitUnixProcess(String processName) throws Exception {
		try {
			launchNonBlockingCommand(String.format("killall %s", processName));
		} catch (IOException | InterruptedException e) {
			logger.log(Level.SEVERE, String.format("Could not quit %s", processName), e);
			if (isProcessRunning(processName)) {
				throw e;
			}
		}
	}
	
	public static boolean isProcessRunning(String processName) throws Exception {
		CommandResult commandResult = launchNonBlockingCommand(String.format("ps aux | grep -v grep | grep %s", processName));
		return commandResult.getStdout().trim().length() != 0;
	}
}
