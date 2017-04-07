package io.github.sidf.documentreader.util;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Handler;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class HtmlLogFormatter extends Formatter {
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	
	@Override
	public String format(LogRecord record) {
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append(String.format("<tr><td>%s %s %s</td></tr>\n", dateFormat.format(new Date(record.getMillis())), 
										   trimClassName(record.getSourceClassName()), 
										   record.getSourceMethodName()));
		String tag = "<tr>";
		if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
			tag = "<tr style=\"color:red\">";
		}
		
		stringBuilder.append(String.format("%s<td>%s: %s</td><tr>\n", tag, record.getLevel(), record.getMessage()));
		
		if (record.getThrown() != null) {
			Throwable thrown = record.getThrown();
			stringBuilder.append(String.format("<tr><td>%s: %s</td><tr>\n", thrown.getClass().getTypeName(), 
											   thrown.getMessage()));
			for (StackTraceElement stackTraceElement : thrown.getStackTrace()) {
				stringBuilder.append(String.format("<tr><td style=\"padding-left:2em\">at %s</td></tr>\n", 
												   stackTraceElement));
			}
		}
		stringBuilder.append("<tr><td><hr></td></tr>\n");
		
		return stringBuilder.toString();
	}
	
	public String getHead(Handler h) {
		return "<!DOCTYPE html>\n"
                + "<head>\n</head>\n"
                + "<body>\n"
                + "<table style=\"width: 100%\">\n"
                + "<hr />\n";
	}
	
    public String getTail(Handler h) {
        return "</table>\n</body>\n</html>";
    }
    
    private static String trimClassName(String fullClassName) {
    	int lastDotIndex = fullClassName.lastIndexOf('.');
    	if (lastDotIndex == -1) {
    		return fullClassName;
    	}
    	return fullClassName.substring(lastDotIndex + 1);
    }
}
