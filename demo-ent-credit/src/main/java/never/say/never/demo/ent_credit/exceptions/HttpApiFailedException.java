/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.exceptions;

import feign.FeignException;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-31
 */
public class HttpApiFailedException extends FeignException {
    private static final long serialVersionUID = -7872725577150495790L;

    public HttpApiFailedException(int status, String message) {
        super(status, message);
    }

    public HttpApiFailedException(int status, String message, Throwable cause) {
        super(status, message, cause);
    }
}
