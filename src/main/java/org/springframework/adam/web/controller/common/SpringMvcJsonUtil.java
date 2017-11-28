/**
 * 
 */
package org.springframework.adam.web.controller.common;

import java.text.SimpleDateFormat;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author user
 *
 */
@Component
public class SpringMvcJsonUtil {
	private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final ObjectMapper mapper;

	public ObjectMapper getMapper() {
		return mapper;
	}

	static {
		SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
		mapper = new ObjectMapper();
		mapper.setDateFormat(dateFormat);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

}
