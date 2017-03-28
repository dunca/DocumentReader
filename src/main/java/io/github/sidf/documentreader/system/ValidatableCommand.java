package io.github.sidf.documentreader.system;

public class ValidatableCommand {
	private final String command;
	private final int successExitValue;
	private final String outputOnSuccess;
	private final String substringOnSuccess;
	
	public ValidatableCommand(String command, int successExitValue) {
		this(command, null, null, successExitValue);
	}
	
	public ValidatableCommand(String command, String outputOnSuccess, String substringOnSuccess, int successExitValue) {
		this.command = command;
		this.outputOnSuccess = outputOnSuccess;
		this.successExitValue = successExitValue;
		this.substringOnSuccess = substringOnSuccess;
	}
	
	public boolean runs() {
		CommandResult commandResult = CommandHelper.launchNonBlockingCommand(command);
		
		if ((outputOnSuccess != null && commandResult.getStdout() == outputOnSuccess) || 
			(substringOnSuccess != null && commandResult.getStdout().contains(substringOnSuccess))) {
			return true;
		}

		return commandResult.getExitValue() == successExitValue;
	}
}