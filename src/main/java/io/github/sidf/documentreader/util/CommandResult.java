package io.github.sidf.documentreader.util;

/**
 * Represents the information resulted from a command's execution 
 * @author Esc
 */
public class CommandResult {
	private String stdout;
	private int exitValue;
	
	public CommandResult(String stdout, int exitValue) {
		this.stdout = stdout;
		this.exitValue = exitValue;
	}

	/**
	 * Gets the standard output
	 * @return a string denoting the standard output of the command. It's what usually shows up in the
	 * terminal (the default standard output)
	 */
	public String getStdout() {
		return stdout;
	}

	/**
	 * Gets the exit value
	 * @return an integer denoting the commands exit code. A value of 0 indicates normal process termination by convention
	 */
	public int getExitValue() {
		return exitValue;
	}
}
