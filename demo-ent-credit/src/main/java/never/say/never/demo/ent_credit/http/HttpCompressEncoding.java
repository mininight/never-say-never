/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.http;

import lombok.Getter;
import never.say.never.demo.ent_credit.util.FuConverter;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.brotli.BrotliCompressorInputStream;
import org.apache.commons.compress.compressors.deflate.DeflateCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorInputStream;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-10
 */
@Getter
public enum HttpCompressEncoding {
    /**
     * GZIP
     */
    GZIP(GzipCompressorInputStream::new),
    /**
     * DEFLATE
     */
    DEFLATE(DeflateCompressorInputStream::new),
    /**
     * BR
     */
    BR(BrotliCompressorInputStream::new),
    /**
     * ZSTD
     */
    ZSTD(ZstdCompressorInputStream::new),
    ;
    private final FuConverter<InputStream, CompressorInputStream> converter;

    HttpCompressEncoding(FuConverter<InputStream, CompressorInputStream> converter) {
        this.converter = converter;
    }

    public static HttpCompressEncoding of(String encoding) {
        if (StringUtils.isBlank(encoding)) {
            return null;
        }
        for (HttpCompressEncoding enc : HttpCompressEncoding.values()) {
            if (enc.name().equalsIgnoreCase(encoding)) {
                return enc;
            }
        }
        return null;
    }
}
