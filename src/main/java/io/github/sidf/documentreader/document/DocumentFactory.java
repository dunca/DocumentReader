package io.github.sidf.documentreader.document;

import java.io.File;
import java.util.List;
import java.lang.reflect.Constructor;

import io.github.sidf.documentreader.util.ClassPathUtil;

public class DocumentFactory {
	private static List<String> documentProviders = ClassPathUtil.getSubclassNames(DocumentFactory.class.getPackage().getName(), 
																				   Document.class);
	
	public static Document getInstance(String className, File file) throws Exception {
		Class theClass = Class.forName(className); 
		Constructor constructor = theClass.getConstructor(File.class);
		return (Document) constructor.newInstance(file);
	}
	 
	public static List<String> getDocumentProviders() {
		return documentProviders;
	}
}
