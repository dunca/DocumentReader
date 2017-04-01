package io.github.sidf.documentreader.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import javax.xml.bind.DatatypeConverter;
import java.security.NoSuchAlgorithmException;

public class FileUtil {
	public static String getMd5Hash(String filePath) throws IOException {
		byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
		byte[] md5Hash = null;
		
		try {
			 md5Hash = MessageDigest.getInstance("MD5").digest(fileContent);
		} catch (NoSuchAlgorithmException e) {
			// it won't reach here since MD5 is valid
		}
		
		// http://stackoverflow.com/questions/5470219/get-md5-string-from-message-digest
		return DatatypeConverter.printHexBinary(md5Hash);
	}
	
	public static String fileToString(File file) throws IOException {
		StringBuilder sb = new StringBuilder();
		
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
		}
		
		return sb.toString();
	}
}
