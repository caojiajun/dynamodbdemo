package com.caojiajun.dynamodbdemo.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by caojiajun on 2016/4/15.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    String name();
}
