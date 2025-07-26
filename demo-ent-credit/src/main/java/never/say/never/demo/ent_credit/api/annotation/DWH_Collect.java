/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.api.annotation;

import java.lang.annotation.Retention;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-17
 */
@java.lang.annotation.Target(METHOD)
@Retention(RUNTIME)
public @interface DWH_Collect {
    String value() default "";
    String successEL() default "";
    boolean ignore() default false;
}
