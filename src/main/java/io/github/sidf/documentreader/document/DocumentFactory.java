package io.github.sidf.documentreader.document;

import java.io.File;
import java.util.List;
import java.lang.reflect.Constructor;

import io.github.sidf.documentreader.util.ClassPathUtil;

public class DocumentFactory {
	private DocumentFactory() {
		
	}
	
	private static List<String> documentProviders = ClassPathUtil.getSubclassNames(DocumentFactory.class.getPackage().getName(), 
																			       Document.class);
	
	static Document getInstance(String className, String filePath, File bookmarkFile) throws Exception {
		Class<?> theClass = Class.forName(className); 
		Constructor<?> constructor = theClass.getConstructor(String.class, File.class);
		return (Document) constructor.newInstance(filePath, bookmarkFile);
	}
	 
	static List<String> getDocumentProviders() {
		return documentProviders;
	}
}
