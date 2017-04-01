package io.github.sidf.documentreader.util;

import java.io.Reader;
import java.io.InputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.InputStreamReader;

public class StreamUtil {
	private StreamUtil() {
		
	}
	
	private static Logger logger = Logger.getLogger(StreamUtil.class.getName());
	
	public static String inputStreamToString(InputStream inputStream) {
		StringBuilder stringBuilder = new StringBuilder();
		int charsRead;
		char[] buffer = new char[2048];
		try (Reader reader= new InputStreamReader(inputStream, "UTF-8")) {
			while ((charsRead = reader.read(buffer, 0, buffer.length)) != -1) {
				stringBuilder.append(buffer, 0, charsRead);
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Could not read the input stream", e);
		}
		
		return stringBuilder.length() != 0 ? stringBuilder.toString().trim() : null;
	}
}
