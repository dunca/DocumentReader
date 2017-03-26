package io.github.sidf.documentreader.system;

import java.io.IOException;

public class Device {
	public static OperatingSystem getOperatingSystem() {
		if (System.getProperty("os.name").contains("Windows")) {
			return OperatingSystem.WINDOWS;
		}
		return OperatingSystem.LINUX;
	}
	
	public static void shutDown() {
		shutDown(3);
	}
	
	public static void shutDown(int countdown) {
		String command = null;
		
		switch (getOperatingSystem()) {
		case WINDOWS:
			command = "shutdown.exe -s -t ";
			break;

		case LINUX:
			command = "shutdown -h ";
			break;
		}
		
		try {
			Runtime.getRuntime().exec(command + countdown);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
