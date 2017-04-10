package io.github.sidf.documentreader.util;

import java.util.UUID;
import java.io.IOException;
import java.io.InputStream;

public class PathUtil {
	private static ClassLoader classLoader;
	
	static {
		classLoader = PathUtil.class.getClassLoader();
	}
	
	private PathUtil() {
		
	}
	
	public static String getResourcePath(String relativeResourcePath) {
		return classLoader.getResource(relativeResourcePath).getPath();
	}
	
	public static String resourcePathToFile(String relativeResourcePath) throws IOException {
		InputStream inputStream = classLoader.getResourceAsStream(relativeResourcePath);
		String filePath = "/tmp/" + getRandomFileName(null);
		FileUtil.inputStreamToFile(inputStream, filePath);
		return filePath;
	}
	
	public static String getRandomFileName(String suffix) {
		String uuid = UUID.randomUUID().toString().replace('-', '_');
		return uuid += suffix == null ? ".tmp" : suffix;
	}
}
