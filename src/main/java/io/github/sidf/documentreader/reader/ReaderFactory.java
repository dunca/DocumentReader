package io.github.sidf.documentreader.reader;

import java.lang.reflect.Constructor;
import java.util.List;

import io.github.sidf.documentreader.util.ClassPathUtil;

public class ReaderFactory {
	private ReaderFactory() {
		
	}
	
	private static List<String> readerProviders = ClassPathUtil.getSubclassNames(ReaderFactory.class.getPackage().getName(), 
	   																	         Reader.class);

	public static Reader getInstance(String className) throws Exception {
		Class<?> theClass = Class.forName(className); 
		Constructor<?> constructor = theClass.getConstructor();
		return (Reader) constructor.newInstance();
	}
		
	public static List<String> getReaderProviders() {
		return readerProviders;
	}
}
