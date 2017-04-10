package io.github.sidf.documentreader.reader;

import java.lang.reflect.Constructor;

import io.github.sidf.documentreader.document.Document;
import io.github.sidf.documentreader.util.ClassPathUtil;

public class ReaderFactory {
	private ReaderFactory() {
		
	}
	
	private static String[] readerProviders = ClassPathUtil.getSubclassNames(ReaderFactory.class.getPackage().getName(), 
	   																	     Reader.class).toArray(new String[0]);

	public static Reader getInstance(String className, Document document) throws Exception {
		Class<?> theClass = Class.forName(className); 
		Constructor<?> constructor = theClass.getConstructor(Document.class);
		return (Reader) constructor.newInstance(document);
	}
		
	public static String[] getReaderProviders() {
		return readerProviders;
	}
}
