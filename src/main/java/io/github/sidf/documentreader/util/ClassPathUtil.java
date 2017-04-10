package io.github.sidf.documentreader.util;

import java.util.List;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;

public class ClassPathUtil {
	// http://stackoverflow.com/questions/7317179/scan-the-classpath-for-classes-with-custom-annotation
	public static List<String> getSubclassNames(String packageName, Class<?> abstractClass) {
		List<String> classNames = new FastClasspathScanner(packageName).scan().getNamesOfSubclassesOf(abstractClass);
		return classNames;
	}
}
