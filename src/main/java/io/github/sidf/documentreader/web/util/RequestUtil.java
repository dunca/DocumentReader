package io.github.sidf.documentreader.web.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestUtil {
	private RequestUtil() {
		
	}
	
	public static String parseBodyString(String body, String elementName) {
		Pattern pattern = Pattern.compile(String.format("(?<=%s\\=)\\w+(?=(&|$))", elementName));
		Matcher matcher = pattern.matcher(body);
		if (matcher.find()) {
			return matcher.group();
		}
		return null;
	}
}
