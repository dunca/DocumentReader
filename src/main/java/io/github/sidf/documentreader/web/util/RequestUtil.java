package io.github.sidf.documentreader.web.util;

import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.UnsupportedEncodingException;

/**
 * Class that provides static methods that deal with HTTP request related tasks
 * @author sidf
 */
public class RequestUtil {
	private RequestUtil() {
		
	}
	
	/**
	 * Gets the value of a parameter that is contained in the POST request's body or in a URL query string
	 * @param source the body of a POST request or a URL query string
	 * @param parameterName the name of the parameter
	 * @return a string denoting the value of the parameter or null if the parameter has no value
	 */
	public static String getRequestParameterValue(String source, String parameterName) {
		try {
			source = URLDecoder.decode(source, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// do nothing about it
		}
		
		Pattern pattern = Pattern.compile(String.format("(?<=%s\\=)[\\w\\.]+(?=(&|$))", parameterName), Pattern.UNICODE_CHARACTER_CLASS);
		Matcher matcher = pattern.matcher(source);
		if (matcher.find()) {
			return matcher.group();
		}
		return null;
	}
}
