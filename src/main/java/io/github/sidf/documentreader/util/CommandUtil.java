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
		
		// special treatment for commands that rely on a shell in order to run correctly. The pipe "|" is a 
		// shell feature. We also have to use a shell to run commands that contain quotes, especially if 
		// there are spaces inside the quotes (http://stackoverflow.com/a/31776547)
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
	
	public static boolean processRunning(String processName) {
		return new ValidatableCommand(String.format("pgrep %s", processName)).runs();
	}
}
