package io.github.sidf.documentreader.document;

import java.util.List;
import java.lang.reflect.Constructor;
import io.github.sidf.documentreader.util.ClassPathUtil;

public class ReaderFactory {
	private static List<String> readerProviders = ClassPathUtil.getSubclassNames(ReaderFactory.class.getPackage().getName(), 
		   																	     Reader.class);

	public static Reader getInstance(String className, DocumentPage page) throws Exception {
		Class theClass = Class.forName(className); 
		Constructor constructor = theClass.getConstructor(DocumentPage.class);
		return (Reader) constructor.newInstance(page);
	}
		
	public static List<String> getReaderProviders() {
		return readerProviders;
	}
}
