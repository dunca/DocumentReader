package io.github.sidf.documentreader.util;

import java.util.UUID;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import io.github.sidf.documentreader.system.Device;
import io.github.sidf.documentreader.system.enums.OperatingSystem;

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
	
	public static String resourcePathToFilePath(String relativeResourcePath) throws IOException {
		InputStream inputStream = classLoader.getResourceAsStream(relativeResourcePath);
		String filePath = getRandomFileName(null);
		FileUtil.inputStreamToFile(inputStream, filePath);
		return filePath;
	}
	
	public static String getRandomFileName(String suffix) {
		String uuid = UUID.randomUUID().toString().replace('-', '_');
		return uuid += suffix == null ? ".tmp" : suffix;
	}
}
