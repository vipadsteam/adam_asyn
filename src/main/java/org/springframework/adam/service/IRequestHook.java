package org.springframework.adam.service;

import java.util.Map;

public interface IRequestHook {

	default Object doBefore(String url, Map<String, String> headersMap, Object[] income, Object output) throws Exception {
		return null;
	}

	default Object doAfter(String url, Map<String, String> headersMap, Object[] income, Object output) throws Exception {
		return output;
	}

}
