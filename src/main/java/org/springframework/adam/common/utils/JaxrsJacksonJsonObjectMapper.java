package org.springframework.adam.common.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Json工具类
 * @author longshaota
 */
public class JaxrsJacksonJsonObjectMapper extends ObjectMapper {

	public JaxrsJacksonJsonObjectMapper() {
		super();
		// false 不包含响应类名
		super.configure(SerializationConfig.Feature.WRAP_ROOT_VALUE, true);
		super.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);
		super.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		super.configure(SerializationConfig.Feature.WRITE_EMPTY_JSON_ARRAYS, true);
		super.configure(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES, true);
		super.configure(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
		super.setSerializationInclusion(JsonSerialize.Inclusion.ALWAYS);
		// super.getSerializerProvider().setNullValueSerializer(new
		// JaxrsNullValJsonSerializer());
		this.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
	}

}

class JaxrsNullValJsonSerializer extends JsonSerializer<Object> {

	public void serialize(Object key, JsonGenerator jsonGenerator, SerializerProvider unused) throws IOException,
			JsonProcessingException {
		jsonGenerator.writeString("");
	}

}
