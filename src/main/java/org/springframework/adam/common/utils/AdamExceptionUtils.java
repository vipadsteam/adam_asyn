/**
 * 
 */
package org.springframework.adam.common.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author USER
 *
 */
public class AdamExceptionUtils {

	public static String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		return sw.toString();
	}
}
