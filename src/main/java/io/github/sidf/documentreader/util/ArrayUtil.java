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
	
	public static String[] joinStringArrays(String[]... arrays) {
		int totalSize = 0;
		for (String[] array : arrays) {
			totalSize += array.length;
		}
		
		String[] resultArray = new String[totalSize];
		
		int index = 0;
		for (String[] array : arrays) {
			for (String item : array) {
				resultArray[index++] = item;
			}
		}
		
		return resultArray;
	}
}
