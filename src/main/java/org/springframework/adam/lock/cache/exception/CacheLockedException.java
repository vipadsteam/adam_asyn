package org.springframework.adam.lock.cache.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author Administrator
 *
 */
public class CacheLockedException extends RuntimeException {

	private static final long serialVersionUID = 1780336117175976968L;

	public CacheLockedException() {
	}

	public CacheLockedException(String message) {
		super(message);
	}

	public CacheLockedException(Throwable cause) {
		super(cause);
	}

	public CacheLockedException(String message, Throwable cause) {
		super(message, cause);
	}
}
