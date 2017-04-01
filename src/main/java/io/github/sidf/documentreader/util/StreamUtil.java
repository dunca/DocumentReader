package io.github.sidf.documentreader.util;

import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.InputStreamReader;

public class StreamUtil {
	private StreamUtil() {
		
	}
	
	private static Logger logger = Logger.getLogger(StreamUtil.class.getName());
	
	public static String inputStreamToString(InputStream inputStream) {
		StringBuilder stringBuilder = new StringBuilder();
		String line;
		
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(String.format("%s%n", line));
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Could not read the input stream", e);
		}
		
		return stringBuilder.length() != 0 ? stringBuilder.toString() : null;
	}
}
