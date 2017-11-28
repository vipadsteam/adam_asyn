/**
 * 
 */
package org.springframework.adam.common.utils;

import java.net.SocketTimeoutException;

import org.apache.http.conn.ConnectTimeoutException;

/**
 * @author user
 *
 */
public class TimeoutUtil {

	public static boolean isTimeOut(Throwable t) {
		if (t instanceof SocketTimeoutException || t instanceof ConnectTimeoutException) {
			return true;
		} else if (t.getCause() instanceof SocketTimeoutException || t.getCause() instanceof ConnectTimeoutException) {
			return true;
		}
		return false;
	}

}
