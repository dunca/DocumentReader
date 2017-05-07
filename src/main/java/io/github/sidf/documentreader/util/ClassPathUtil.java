package io.github.sidf.documentreader.util;

import java.util.List;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;

/**
 * Class that provides methods that deal with the Java classpath
 * @author sidf
 */
public class ClassPathUtil {
	private ClassPathUtil() {
		
	}
	
	/**
	 * Gets a list of classes that extend a particular class
	 * @param packageName the name of the package in which the search will take place
	 * @param abstractClass the superclass of the classes to search for
	 * @return a list with the full names of the classes that extend the provided class
	 */
	public static List<String> getSubclassNames(String packageName, Class<?> abstractClass) {
		List<String> classNames = new FastClasspathScanner(packageName).scan().getNamesOfSubclassesOf(abstractClass);
		return classNames;
	}
}
