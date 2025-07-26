/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.http;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-07
 */
@Getter
public enum HeaderTemplate {
    AiQiCha_header,
    AiQiCha_Html_header,
    QiChaCha_header,
    QiChaCha_wxheader,
    TianYanCha_header,
    TianYanCha_Html_header,
    QiXinBao_Html_header,
    QiXinBao_wxheader,
    YiQiCha_header,
    QiChaMao_header,
    WeiBo_header,
    ;

    private final Map<String, String> kvMap = new LinkedHashMap<>();

    HeaderTemplate() {
        try {
            String template = StreamUtils.copyToString(new ClassPathResource("header/" + name()).getInputStream(),
                    StandardCharsets.UTF_8);
            Arrays.stream(template.split("\r\n")).forEach(kvStr -> {
                if (StringUtils.isBlank(kvStr) || kvStr.startsWith(":")) {
                    return;
                }
                int splitIndex = kvStr.indexOf(":");
                String key = kvStr.substring(0, splitIndex);
                String value;
                try {
                    value = kvStr.substring(splitIndex + 1);
                } catch (Exception e) {
                    return;
                }
                key = key.trim().toLowerCase();
                value = value.trim();
                if ("cookie".equals(key) && StringUtils.isNotBlank(kvMap.get(key))) {
                    value = kvMap.get(key) + ";" + value;
                }
                kvMap.put(key, value);
            });
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
