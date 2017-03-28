package io.github.sidf.documentreader.system;

import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StreamHelper {
	private static Logger logger = Logger.getLogger(StreamHelper.class.getName());
	
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
