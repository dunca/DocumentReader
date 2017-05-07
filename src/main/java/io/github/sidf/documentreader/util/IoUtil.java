package io.github.sidf.documentreader.util;

import java.io.File;
import java.io.Reader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.io.FileNotFoundException;
import javax.xml.bind.DatatypeConverter;
import java.nio.file.StandardCopyOption;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that provides static methods that deal with stream and file I/O operations
 * @author sidf
 */
public class IoUtil {
	private static final Pattern md5sumPattern = Pattern.compile("(?<=^)\\w{32}(?=.+$)");
	
	private IoUtil() {
		
	}
	
	/**
	 * Calculates the MD5 hash of a file
	 * @param filePath a string denoting the source file's path
	 * @return a string denoting the MD5 hash of the file
	 * @throws Exception 
	 * @throws IOException if an I/O error occurs when reading the file's content
	 */
	public static String getMd5Hash(String filePath) throws Exception {
//		byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
//		byte[] md5Hash = null;
//		
//		try {
//			 md5Hash = MessageDigest.getInstance("MD5").digest(fileContent);
//		} catch (NoSuchAlgorithmException e) {
//			
//		}
//		
//		// http://stackoverflow.com/a/5470279
//		return DatatypeConverter.printHexBinary(md5Hash);
		
		if (!new File(filePath).isFile()) {
			throw new IOException(String.format(filePath + " does not exist as a file"));
		}
		
		CommandResult commandResult = CommandUtil.executeCommand("md5sum " + filePath);
		Matcher matcher = md5sumPattern.matcher(commandResult.getStdout());	
		
		if (!matcher.find()) {
			throw new IOException("Could not parse the MD5 hash for file " + filePath);
		}
		
		return matcher.group();
	}
	
	/**
	 * Gets the content of a file as a string
	 * @param filePath a string denoting the source file's path
	 * @return a string denoting the content of the file
	 * @throws IOException if an I/O error occurs when reading the file's content
	 */
	public static String fileToString(String filePath) throws IOException {
		StringBuilder sb = new StringBuilder();
		
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(String.format("%s%n", line));
			}
		}
		
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
	
	/**
	 * Updates the content of a file
	 * @param content a string denoting the content source
	 * @param targetFilePath a string denoting the path to the target file
	 * @throws FileNotFoundException if the target file does not exist or it cannot be created
	 */
	public static void stringToFile(String content, String targetFilePath) throws FileNotFoundException {
		try(PrintWriter writer = new PrintWriter(targetFilePath, "UTF-8")) {
			writer.print(content);
		} catch (UnsupportedEncodingException e) {
			//
		}
	}
	
	/**
	 * Reads the content of a resource that resides in the jar and writes it to a temporary file
	 * @param absoluteResourcePath a string denoting the absolute path to a resource that resides in the jar file
	 * @return a string denoting the path to the temporary file that will be updated with the resource's content
	 * @throws IOException if an I/O error occurs
	 */
	public static String resourcePathToFile(String absoluteResourcePath) throws IOException {	
		InputStream inputStream = IoUtil.class.getResourceAsStream(absoluteResourcePath);
		File file = File.createTempFile("temp", null);
		String filePath = file.getPath();
		
		inputStreamToFile(inputStream, filePath);
		return filePath;
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
