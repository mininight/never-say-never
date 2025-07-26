/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.http;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-06
 */
public enum RefreshingCookie {
    /**
     * 爱企查PC API+HTML
     */
    AiQiCha_cookie,
    /**
     * 企查查PC API+HTML
     */
    QiChaCha_cookie,
    /**
     * 企查查wx API
     */
    QiChaCha_token,
    /**
     * 天眼查PC API
     */
    TianYanCha_cookie,
    /**
     * 天眼查PC HTML
     */
    TianYanCha_Html_cookie,
    /**
     * 启信宝PC HTML
     */
    QiXinBao_cookie,
    /**
     * 启信宝wx API
     */
    QiXinBao_wxcookie,
    /**
     * 亿企查PC API+HTML
     */
    YiQiCha_cookie,
    /**
     * 企查猫PC API+HTML
     */
    QiChaMao_cookie,
    /**
     * 微博
     */
    WeiBo_cookie,
    ;

    @Getter
    @Setter
    private boolean expired = false;
    private String oldValue;

    RefreshingCookie() {
        if (CacheHolder.cookieCache == null) {
            CacheHolder.cookieCache = CacheBuilder.newBuilder()
                    .refreshAfterWrite(90, TimeUnit.MINUTES)
                    .build(new CacheLoader<>() {
                        @Override
                        public String load(String key) throws Exception {
                            String newValue = loadCookie(key);
                            if (key.toLowerCase().endsWith("token")) {
                                setExpired(Objects.equals(oldValue, newValue));
                            }
                            oldValue = newValue;
                            return oldValue;
                        }
                    });
            Runtime.getRuntime().addShutdownHook(new Thread("RefreshingCookie") {
                @Override
                public void run() {
                    CacheHolder.cookieCache.cleanUp();
                }
            });
        }
        String name = name();
        oldValue = loadCookie(name);
        CacheHolder.cookieCache.put(name, oldValue);
    }

    public static String loadCookie(String name) {
        String tk = "cookie:";
        int tkLen = tk.length();
        try {
            String cookie = StreamUtils.copyToString(new ClassPathResource("cookie/" + name).getInputStream(),
                    StandardCharsets.UTF_8);
            if (StringUtils.isBlank(cookie) || cookie.length() <= tkLen) {
                return "";
            }
            if (cookie.substring(0, tkLen).equalsIgnoreCase(tk)) {
                cookie = Arrays.stream(cookie.split(tk)).filter(StringUtils::isNotBlank)
                        .map(c -> c.replaceAll("\r\n", ""))
                        .map(c -> c.replaceAll("\n", "").trim())
                        .collect(Collectors.joining(";"));
            }
            return cookie;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public String val() {
        return CacheHolder.cookieCache.getIfPresent(name());
    }

    static class CacheHolder {
        public static LoadingCache<String, String> cookieCache;
    }

}
