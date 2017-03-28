package io.github.sidf.documentreader.system;

public class CommandResult {
	public String stdout;
	public int exitValue;
	
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
