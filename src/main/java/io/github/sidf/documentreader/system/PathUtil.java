package io.github.sidf.documentreader.system;

public class PathUtil {
	private static boolean runsLinux;
	private static ClassLoader classLoader;
	
	private PathUtil() {
		
	}
	
	static {
		classLoader = PathUtil.class.getClassLoader();
		runsLinux = Device.getOperatingSystem() == OperatingSystem.LINUX;
	}
	
	public static String getResourcePath(String relativeResourcePath) {
		String path = classLoader.getResource(relativeResourcePath).getPath();
		return runsLinux ? path : path.substring(1);
	}
}
