package com.caojiajun.dynamodbdemo.util;

import com.caojiajun.dynamodbdemo.annotation.TablePrimaryKey;
import com.caojiajun.dynamodbdemo.annotation.Table;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by caojiajun on 2016/4/15.
 */
public class ClassUtil {
    public static String getTableName(Class<?> clazz) {
        Table annotation = clazz.getAnnotation(Table.class);
        if(annotation != null) {
            return annotation.name();
        }
        return null;
    }

    public static List<Field> getPrimaryKeys(Class<?> clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();
        List<Field> res = new ArrayList<Field>();
        for (Field field : declaredFields) {
            if(field.isAnnotationPresent(TablePrimaryKey.class)) {
                res.add(field);
            }
        }
        return res;
    }

    public static List<Field> getAllPrivateFields(Class<?> clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();
        ArrayList<Field> res = new ArrayList<Field>(declaredFields.length);
        for (Field field : declaredFields) {
            int modifiers = field.getModifiers();
            if(Modifier.isPrivate(modifiers) && !Modifier.isStatic(modifiers)) {
                res.add(field);
            }
        }
        return res;
    }

    public static <T> String toString(T obj, Class<T> clazz, List<Field> fields) {
        StringBuilder res = new StringBuilder();
        res.append(clazz.getName());
        res.append(": { ");
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                String name = field.getName();
                Object value = field.get(obj);
                res.append(name);
                res.append("=");
                res.append(value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            res.append(",");
        }
        res.deleteCharAt(res.length() - 1);
        res.append(" }");
        return res.toString();
    }
}
