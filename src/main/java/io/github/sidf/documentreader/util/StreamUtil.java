package io.github.sidf.documentreader.util;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.StandardCopyOption;

public class StreamUtil {
	private StreamUtil() {
		
	}
	
	public static String inputStreamToString(InputStream inputStream) throws IOException {
		StringBuilder stringBuilder = new StringBuilder();
		int charsRead;
		char[] buffer = new char[2048];
		try (Reader reader= new InputStreamReader(inputStream, "UTF-8")) {
			while ((charsRead = reader.read(buffer, 0, buffer.length)) != -1) {
				stringBuilder.append(buffer, 0, charsRead);
			}
		} 
		
		return stringBuilder.toString();
	}
	
	public static void inputStreamToFile(InputStream is, String locationPath) throws IOException {
		Files.copy(is, Paths.get(locationPath), StandardCopyOption.REPLACE_EXISTING);
	}
}
