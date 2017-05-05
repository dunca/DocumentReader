package io.github.sidf.documentreader.util;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandUtil {
	private static Logger logger = Logger.getLogger(CommandUtil.class.getName());
	
	private CommandUtil() {
		
	}
	
	public static CommandResult executeCommand(String command) throws IOException, InterruptedException {
		String[] commandArray = null;
		
		if (command.contains("|") || command.contains("'") || command.contains("\"")) {
			commandArray = new String[] {"/bin/sh", "-c", command};
		} else {
			commandArray = command.split(" ");
		}
		
		ProcessBuilder processBuilder = new ProcessBuilder(commandArray);
		processBuilder.redirectErrorStream(true);
		
		logger.info(String.format("Trying to run command %s", command));
		Process process = processBuilder.start();
		
		int exitCode = process.waitFor();
		return new CommandResult(StreamUtil.inputStreamToString(process.getInputStream()), exitCode);
	}
	
	public static void terminateProcess(String processName) throws Exception {
		if (!processRunning(processName)) {
			logger.info(String.format("Can't terminate %s. It isn't running", processName));
		}
		
		try {
			executeCommand(String.format("killall %s", processName));
		} catch (Exception e) {
			logger.log(Level.SEVERE, String.format("Couldn't gracefully terminate %s", processName), e);
		}
	}
	
	public static boolean processRunning(String processName) throws Exception {
		CommandResult commandResult = executeCommand(String.format("pgrep %s", processName));
		return commandResult.getExitValue() == 0;
	}
}
