package io.github.sidf.documentreader.system;

import java.io.IOException;

public class CommandHelper {
	public static CommandResult launchNonBlockingCommand(String command) {
		Process process = null;
		try {
			String[] commandArray = null;
			
			if (command.contains("|")) {
				commandArray = new String[] {"/bin/sh", "-c", command};
			} else {
				commandArray = command.split(" ");
			}
			
			ProcessBuilder builder = new ProcessBuilder(commandArray);
			builder.redirectErrorStream(true);
			process = builder.start();
		} catch (IOException e) {
			return null;
		}
		
		int exitCode = -1;
		
		try {
			exitCode = process.waitFor();
		} catch (InterruptedException e) {
			
		}
		
		return new CommandResult(StreamHelper.inputStreamToString(process.getInputStream()), exitCode);
	}
	
	public static void quitUnixProcess(String processName) {
		launchNonBlockingCommand(String.format("killall %s", processName));
	}
}
