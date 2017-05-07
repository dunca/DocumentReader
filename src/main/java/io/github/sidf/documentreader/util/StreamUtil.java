package io.github.sidf.documentreader.util;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.StandardCopyOption;

/**
 * Class that provides static methods that deal with input / output streams
 * @author sidf
 */
public class StreamUtil {
	private StreamUtil() {
		
	}
	
	/**
	 * Gets the string content read from an input stream
	 * @param soruceInputStream the source input stream
	 * @return a string denoting the content read from the input stream
	 * @throws IOException if an I/O error occurs
	 */
	public static String inputStreamToString(InputStream soruceInputStream) throws IOException {
		StringBuilder stringBuilder = new StringBuilder();
		int charsRead;
		char[] buffer = new char[2048];
		try (Reader reader= new InputStreamReader(soruceInputStream, "UTF-8")) {
			while ((charsRead = reader.read(buffer, 0, buffer.length)) != -1) {
				stringBuilder.append(buffer, 0, charsRead);
			}
		} 
		
		return stringBuilder.toString();
	}
	
	/**
	 * Writes the content read from an input stream to a file
	 * @param sourceInputStream the source input stream
	 * @param targetFilePath a string denoting the path to the target file
	 * @throws IOException if an I/O error occurs
	 */
	public static void inputStreamToFile(InputStream sourceInputStream, String targetFilePath) throws IOException {
		Files.copy(sourceInputStream, Paths.get(targetFilePath), StandardCopyOption.REPLACE_EXISTING);
	}
}
