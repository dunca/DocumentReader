package io.github.sidf.documentreader.util;

public class CommandResult {
	private String stdout;
	private int exitValue;
	
	public CommandResult(String stdout, int exitValue) {
		this.stdout = stdout;
		this.exitValue = exitValue;
	}

	public String getStdout() {
		return stdout;
	}

	public int getExitValue() {
		return exitValue;
	}
}
