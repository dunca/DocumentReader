package io.github.sidf.documentreader.document;

import java.io.File;
import java.lang.reflect.Constructor;

import io.github.sidf.documentreader.util.ClassPathUtil;

public class DocumentFactory {
	private DocumentFactory() {
		
	}
	
	private static String[] documentProviders = ClassPathUtil.getSubclassNames(DocumentFactory.class.getPackage().getName(), 
																			   Document.class).toArray(new String[0]);
	
	static Document getInstance(String className, String filePath, File bookmarkFile) throws Exception {
		Class<?> theClass = Class.forName(className); 
		Constructor<?> constructor = theClass.getConstructor(String.class, File.class);
		return (Document) constructor.newInstance(filePath, bookmarkFile);
	}
	 
	static String[] getDocumentProviders() {
		return documentProviders;
	}
}
