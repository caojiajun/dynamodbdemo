package com.caojiajun.dynamodbdemo.model;

import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.caojiajun.dynamodbdemo.annotation.Table;
import com.caojiajun.dynamodbdemo.util.ClassUtil;
import com.caojiajun.dynamodbdemo.annotation.TablePrimaryKey;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by caojiajun on 2016/4/15.
 */
@Table(name="someone")
public class SomeOne {

    public static final List<Field> ALL_PRIVATE_KEY_FIELDS = ClassUtil.getAllPrivateFields(SomeOne.class);

    @TablePrimaryKey(type = KeyType.HASH, attribute = ScalarAttributeType.N)
    private Integer id;

    @TablePrimaryKey(type = KeyType.RANGE, attribute = ScalarAttributeType.S)
    private String uname;

    private Integer age;

    private String school;

    private List<String> interests;

    public SomeOne() {
    }

    public SomeOne(Integer id, String name, Integer age, String school, List<String> interests) {
        this.id = id;
        this.uname = name;
        this.age = age;
        this.school = school;
        this.interests = interests;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    @Override
    public String toString() {
        return ClassUtil.toString(this, SomeOne.class, ALL_PRIVATE_KEY_FIELDS);
    }
}
