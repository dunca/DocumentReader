package io.github.sidf.documentreader.reader;

import java.lang.reflect.Constructor;

import io.github.sidf.documentreader.util.enums.Speed;
import io.github.sidf.documentreader.document.Document;
import io.github.sidf.documentreader.util.ClassPathUtil;
import io.github.sidf.documentreader.util.enums.Language;

public class ReaderFactory {
	private ReaderFactory() {
		
	}
	
	private static String[] readerProviders = ClassPathUtil.getSubclassNames(ReaderFactory.class.getPackage().getName(), 
	   																	     Reader.class).toArray(new String[0]);

	public static Reader getInstance(String className, Document document, Language language, Speed speed) throws Exception {
		Class<?> theClass = Class.forName(className); 
		Constructor<?> constructor = theClass.getConstructor(Document.class, Language.class, Speed.class);
		return (Reader) constructor.newInstance(document, language, speed);
	}
		
	public static String[] getReaderProviders() {
		return readerProviders;
	}
}
