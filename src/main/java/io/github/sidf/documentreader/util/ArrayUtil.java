package io.github.sidf.documentreader.util;

public class ArrayUtil {
	private ArrayUtil() {
		
	}
	
	public static <T> boolean arrayContains(T[] array, T item) {
		for (T element : array) {
			if (element.equals(item)) {
				return true;
			}
		}
		
		return false;
	}
}
