/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.flow.center.tasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2023-02-25
 */
public class JsonUtil {
    static ObjectMapper objectMapper = new ObjectMapper();

    public static String toString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("序列化失败", e);
        }
    }
}
