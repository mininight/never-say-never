/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.http;

import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import org.apache.commons.compress.compressors.CompressorInputStream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;

public class HttpCompressDecoder implements Decoder {

    private final Decoder decoder;

    public HttpCompressDecoder(Decoder decoder) {
        this.decoder = decoder;
    }

    @Override
    public Object decode(final Response response, Type type)
            throws IOException, FeignException {
        Collection<String> encodings = response.headers().entrySet().stream()
                .filter(entry -> "Content-Encoding".equalsIgnoreCase(entry.getKey()))
                .map(Map.Entry::getValue).findFirst().orElse(null);
        HttpCompressEncoding httpCompressEncoding = encodings == null || encodings.isEmpty() ? null :
                encodings.stream().map(HttpCompressEncoding::of).findFirst().orElse(null);
        if (httpCompressEncoding != null) {
            String decompressedBody = decompress(response, httpCompressEncoding);
            if (decompressedBody != null) {
                Response decompressedResponse = response.toBuilder().body(decompressedBody.getBytes()).build();
                return decoder.decode(decompressedResponse, type);
            }
        }
        return decoder.decode(response, type);
    }

    private String decompress(Response response, HttpCompressEncoding httpCompressEncoding) {
        if (response.body() == null) {
            return null;
        }
        try (CompressorInputStream compressorInputStream = httpCompressEncoding.getConverter().apply(
                response.body().asInputStream());
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(compressorInputStream, StandardCharsets.UTF_8))) {
            StringBuilder outputString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                outputString.append(line);
            }
            return outputString.toString();
        } catch (Throwable e) {
            throw new DecodeException(response.status(), "解压缩Http响应数据出错",
                    HttpApiRequestContext.getCurrent().getRequest().request(), e);
        }
    }

}

