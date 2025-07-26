/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.Locale;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-01
 */
public class JacksonUtil {
    public static final ObjectMapper JSON_MAPPER;

    static {
        JSON_MAPPER = Jackson2ObjectMapperBuilder.json()
                .timeZone("GMT+8").locale(Locale.CHINESE)
                .build();
        JSON_MAPPER.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
        JSON_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        JSON_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        JSON_MAPPER.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
    }
}
