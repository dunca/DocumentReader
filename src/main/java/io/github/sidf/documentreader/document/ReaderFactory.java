package io.github.sidf.documentreader.document;

import java.util.List;
import java.lang.reflect.Constructor;
import io.github.sidf.documentreader.util.ClassPathUtil;

public class ReaderFactory {
	private static List<String> readerProviders = ClassPathUtil.getSubclassNames(ReaderFactory.class.getPackage().getName(), 
		   																	     Reader.class);

	public static Reader getInstance(String className, Document document) throws Exception {
		Class<?> theClass = Class.forName(className); 
		Constructor<?> constructor = theClass.getConstructor(Document.class);
		return (Reader) constructor.newInstance(document);
	}
		
	public static List<String> getReaderProviders() {
		return readerProviders;
	}
}
