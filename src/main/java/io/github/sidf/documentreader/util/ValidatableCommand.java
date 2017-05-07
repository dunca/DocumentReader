package io.github.sidf.documentreader.util;

/**
 * Class that makes the evaluation of commands execution easier
 * @author sidf
 */
public class ValidatableCommand {
	private final String command;
	
	/**
	 * Should be set to 'true' for long running background commands 
	 */
	private final boolean backgroundApp;
	
	private final String successStdout;
	private final String successSubstring;
	
	private static final int successExitValue = 0;

	public ValidatableCommand(String command) {
		this(command, false);
	}
	
	public ValidatableCommand(String command, Boolean background) {
		this(command, background, null, null);
	}
	
	public ValidatableCommand(String command, boolean backgroundApp, String successStdout, String successSubstring) {
		this.command = command;
		this.backgroundApp = backgroundApp;
		this.successStdout = successStdout;
		this.successSubstring = successSubstring;
	}
	
	/**
	 * Runs the command
	 * @return 'true', if any of the following is true:
	 * <ul>
	 * <li>the resulted stdout matches the stdout string specified in the constructor</li>
	 * <li>the resulted stdout contains the substring specified in the constructor</li>
	 * <li>the exit code is 0</li> 
	 * </ul>
	 */
	public boolean runs() {
		CommandResult commandResult = null;
		
		try {
			commandResult = CommandUtil.executeCommand(command);
		} catch (Exception e) {
			return false;
		}
		
		if ((successStdout != null && commandResult.getStdout() == successStdout) || 
			(successSubstring != null && commandResult.getStdout().contains(successSubstring))) {
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
