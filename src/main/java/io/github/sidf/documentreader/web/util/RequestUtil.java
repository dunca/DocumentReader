package io.github.sidf.documentreader.web.util;

import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.UnsupportedEncodingException;

public class RequestUtil {
	private RequestUtil() {
		
	}
	
	public static String parseBodyString(String body, String elementName) {
		try {
			body = URLDecoder.decode(body, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// do nothing about it
		}
		
		Pattern pattern = Pattern.compile(String.format("(?<=%s\\=)[\\w\\.]+(?=(&|$))", elementName), Pattern.UNICODE_CHARACTER_CLASS);
		Matcher matcher = pattern.matcher(body);
		if (matcher.find()) {
			return matcher.group();
		}
		return null;
	}
}
