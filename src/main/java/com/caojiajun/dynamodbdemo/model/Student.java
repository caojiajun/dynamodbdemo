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
@Table(name = "student")
public class Student {

    public static final List<Field> ALL_PRIVATE_KEY_FIELDS = ClassUtil.getAllPrivateFields(Student.class);

    @TablePrimaryKey(type = KeyType.HASH, attribute = ScalarAttributeType.N)
    private Integer id;

    @TablePrimaryKey(type = KeyType.RANGE, attribute = ScalarAttributeType.S)
    private String sname;

    private Integer grade;

    private String major;

    private String info;

    public Student() {
    }

    public Student(Integer id, String sname, Integer grade, String major, String info) {
        this.id = id;
        this.sname = sname;
        this.grade = grade;
        this.major = major;
        this.info = info;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return ClassUtil.toString(this, Student.class, ALL_PRIVATE_KEY_FIELDS);
    }
}
