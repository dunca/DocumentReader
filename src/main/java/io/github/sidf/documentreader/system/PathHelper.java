package io.github.sidf.documentreader.system;

public class PathHelper {
	private static boolean runsLinux;
	private static ClassLoader classLoader;
	
	static {
		classLoader = PathHelper.class.getClassLoader();
		runsLinux = Device.getOperatingSystem() == OperatingSystem.LINUX;
	}
	
	public static String getResourcePath(String relativeResourcePath) {
		String path = classLoader.getResource(relativeResourcePath).getPath();
		return runsLinux ? path : path.substring(1);
	}
}
