package io.github.sidf.documentreader.document;

import java.io.File;
import java.lang.reflect.Constructor;
import io.github.sidf.documentreader.util.ClassPathUtil;

public class ReaderFactory {
	private ReaderFactory() {
		
	}
	
	private static String[] readerProviders = ClassPathUtil.getSubclassNames(ReaderFactory.class.getPackage().getName(), 
	   																	     Reader.class).toArray(new String[0]);

	public static Reader getInstance(String className, Document document, File isReadingPath) throws Exception {
		Class<?> theClass = Class.forName(className); 
		Constructor<?> constructor = theClass.getConstructor(Document.class, File.class);
		return (Reader) constructor.newInstance(document, isReadingPath);
	}
		
	public static String[] getReaderProviders() {
		return readerProviders;
	}
}
