package com.caojiajun.dynamodbdemo.annotation;

import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by caojiajun on 2016/4/15.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TablePrimaryKey {
    KeyType type();
    ScalarAttributeType attribute();
}
