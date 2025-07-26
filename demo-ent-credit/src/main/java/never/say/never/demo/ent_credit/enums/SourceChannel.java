/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.enums;

import never.say.never.demo.ent_credit.api.HttpEntCreditApi;
import never.say.never.demo.ent_credit.http.HttpApiClientPanel;
import org.apache.commons.lang3.EnumUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-18
 */
public enum SourceChannel {

    /**
     * 爱企查
     */
    AiQiCha(HttpApiClientPanel.AiQiCha),

    /**
     * 企查查
     */
    QiChaCha(HttpApiClientPanel.QiChaCha),

    /**
     * 天眼查
     */
    TianYanCha(HttpApiClientPanel.TianYanCha),

    /**
     * 亿企查
     */
    YiQiCha(HttpApiClientPanel.YiQiCha),

    /**
     * 企查猫
     */
    QiChaMao(HttpApiClientPanel.QiChaMao),
    ;

    public static final String ID_DELIMITER = "#";

    private final HttpEntCreditApi api;

    SourceChannel(HttpEntCreditApi api) {
        this.api = api;
    }

    public static SourceChannel of(String name) {
        return EnumUtils.getEnum(SourceChannel.class, name);
    }

    public static SourceChannel of(Class<?> clazz) {
        return Arrays.stream(values()).filter(sc -> Objects.equals(sc.api().getClass(), clazz)).findFirst()
                .orElse(null);
    }

    public static SourceChannel of(HttpEntCreditApi httpEntCreditApi) {
        return Arrays.stream(values()).filter(sc -> Objects.equals(sc.api(), httpEntCreditApi)).findFirst()
                .orElse(null);
    }

    public HttpEntCreditApi api() {
        return api;
    }

    public static List<HttpEntCreditApi> list() {
        return Arrays.stream(SourceChannel.values()).map(SourceChannel::api).collect(Collectors.toList());
    }
}
