package io.github.sidf.documentreader.document;

import java.io.File;
import java.lang.reflect.Constructor;

import io.github.sidf.documentreader.util.ClassPathUtil;

public class DocumentFactory {
	private DocumentFactory() {
		
	}
	
	private static String[] documentProviders = ClassPathUtil.getSubclassNames(DocumentFactory.class.getPackage().getName(), 
																			   Document.class).toArray(new String[0]);
	
	public static Document getInstance(String className, File file, File bookmarkIniFilePath, File currentPagePath) throws Exception {
		Class<?> theClass = Class.forName(className); 
		Constructor<?> constructor = theClass.getConstructor(File.class, File.class, File.class);
		return (Document) constructor.newInstance(file, bookmarkIniFilePath, currentPagePath);
	}
	 
	public static String[] getDocumentProviders() {
		return documentProviders;
	}
}
