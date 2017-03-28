package io.github.sidf.documentreader.system;

import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class StreamHelper {
	public static String inputStreamToString(InputStream inputStream) {
		StringBuilder stringBuilder = new StringBuilder();
		String line;
		
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(String.format("%s%n", line));
			}
		} catch (IOException e) {
			// ignore, and send what we've got until now
		}
		
		return stringBuilder.length() != 0 ? stringBuilder.toString() : null;
	}
}
