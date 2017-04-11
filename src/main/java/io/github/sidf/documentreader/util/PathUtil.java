package io.github.sidf.documentreader.util;

import java.io.File;
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
		File file = File.createTempFile("temp", null);
		String filePath = file.getPath();
		
		StreamUtil.inputStreamToFile(inputStream, filePath);
		return filePath;
	}
}
