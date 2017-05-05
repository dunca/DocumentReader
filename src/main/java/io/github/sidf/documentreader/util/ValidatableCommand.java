package io.github.sidf.documentreader.util;

public class ValidatableCommand {
	private final String command;
	private final boolean background;
	private final int successExitValue;
	private final String outputOnSuccess;
	private final String substringOnSuccess;

	public ValidatableCommand(String command, Boolean background, int successExitValue) {
		this(command, background, null, null, successExitValue);
	}
	
	public ValidatableCommand(String command, int successExitValue) {
		this(command, false, null, null, successExitValue);
	}
	
	public ValidatableCommand(String command, boolean background, String outputOnSuccess, String substringOnSuccess, int successExitValue) {
		this.command = command;
		this.background = background;
		this.outputOnSuccess = outputOnSuccess;
		this.successExitValue = successExitValue;
		this.substringOnSuccess = substringOnSuccess;
	}
	
	public boolean runs() {
		CommandResult commandResult = null;
		
		try {
			commandResult = CommandUtil.launchNonBlockingCommand(command);
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
	
	public boolean isBackground() {
		return background;
	}
}
