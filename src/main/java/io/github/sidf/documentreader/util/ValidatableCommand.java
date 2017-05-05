package io.github.sidf.documentreader.util;

public class ValidatableCommand {
	private final String command;
	private final boolean backgroundApp;
	private final String outputOnSuccess;
	private final String substringOnSuccess;
	
	private static final int successExitValue = 0;

	public ValidatableCommand(String command) {
		this(command, false);
	}
	
	public ValidatableCommand(String command, Boolean background) {
		this(command, background, null, null);
	}
	
	public ValidatableCommand(String command, boolean backgroundApp, String outputOnSuccess, String substringOnSuccess) {
		this.command = command;
		this.backgroundApp = backgroundApp;
		this.outputOnSuccess = outputOnSuccess;
		this.substringOnSuccess = substringOnSuccess;
	}
	
	public boolean runs() {
		CommandResult commandResult = null;
		
		try {
			commandResult = CommandUtil.executeCommand(command);
		} catch (Exception e) {
			return false;
		}
		
		if ((outputOnSuccess != null && commandResult.getStdout() == outputOnSuccess) || 
			(substringOnSuccess != null && commandResult.getStdout().contains(substringOnSuccess))) {
			return true;
		}

		return commandResult.getExitValue() == successExitValue;
	}
	
	public String getCommand() {
		return command;
	}
	
	public boolean isBackgroundApp() {
		return backgroundApp;
	}
}
