package io.github.sidf.documentreader.util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A set of utility methods that deal with commands and processes
 * @author sidf
 */
public class CommandUtil {
	private static Logger logger = Logger.getLogger(CommandUtil.class.getName());
	
	private CommandUtil() {
		
	}
	
	/**
	 * Executes a system command
	 * @param command the command to run, with space separated parameters (if any)
	 * <p>
	 * Special treatment for commands that rely on a shell in order to run correctly. The pipe "|" is a 
	 * shell feature. We also have to use a shell to run commands that contain quotes, especially if 
	 * there are spaces inside the quotes (http://stackoverflow.com/a/31776547)
	 * @return a CommandResult instance containing information about the command that 
	 * either finished running or became a daemon
	 * @throws Exception if an I/O error occurs (command not found, etc.) or if the current thread is interrupted while
	 * waiting for the process to finish its execution
	 */
	public static CommandResult executeCommand(String command) throws Exception {
		String[] commandArray = null;
		
		if (command.contains("|") || command.contains("'") || command.contains("\"")) {
			commandArray = new String[] {"/bin/sh", "-c", command};
		} else {
			commandArray = command.split(" ");
		}
		
		ProcessBuilder processBuilder = new ProcessBuilder(commandArray);
		
		// will redirect stderr to stdout so we can capture both using the same input stream
		processBuilder.redirectErrorStream(true);
		
		logger.info(String.format("Trying to run command %s", command));
		Process process = processBuilder.start();
		
		int exitCode = process.waitFor();
		return new CommandResult(IoUtil.inputStreamToString(process.getInputStream()), exitCode);
	}
	
	/**
	 * Terminates a system process
	 * @param processName the name of the process
	 * @throws Exception if an error occurs while trying to terminate the process (killall is not installed, etc.)
	 */
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
	
	/**
	 * Checks if a process is running
	 * @param processName the name of the process
	 * @return 'true' if a process with the given name is running
	 */
	public static boolean processRunning(String processName) {
		return new ValidatableCommand(String.format("pgrep %s", processName)).runs();
	}
}
