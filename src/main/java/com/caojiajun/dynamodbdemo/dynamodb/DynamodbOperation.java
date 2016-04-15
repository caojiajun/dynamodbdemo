package com.caojiajun.dynamodbdemo.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.internal.IteratorSupport;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.caojiajun.dynamodbdemo.annotation.TablePrimaryKey;
import com.caojiajun.dynamodbdemo.util.ClassUtil;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by caojiajun on 2016/4/15.
 */
public class DynamodbOperation {

    private AmazonDynamoDBClient client = new AmazonDynamoDBClient()
            .withEndpoint("http://localhost:8000");
    private DynamoDB dynamoDB = new DynamoDB(client);

    public <T> boolean createTable(Class<T> clazz, Long readCapacityUnits, Long writeCapacityUnits) {
        String tableName = ClassUtil.getTableName(clazz);
        Table table = this.dynamoDB.createTable(tableName,
                getKeySchemaElement(clazz),
                getAttributeDefinition(clazz),
                new ProvisionedThroughput(readCapacityUnits, writeCapacityUnits));
        try {
            table.waitForActive();
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public <T> boolean deleteTable(Class<T> clazz) {
        String tableName = ClassUtil.getTableName(clazz);
        Table table = this.dynamoDB.getTable(tableName);
        try {
            table.delete();
            table.waitForDelete();
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public <T> boolean put(T obj, Class<T> clazz, List<Field> fields) {
        String tableName = ClassUtil.getTableName(clazz);
        Table table = this.dynamoDB.getTable(tableName);

        Item item = toItem(obj, fields);
        try {
            table.putItem(item);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public <T> List<T> get(T obj, Class<T> clazz, List<Field> projectFields) {
        String tableName = ClassUtil.getTableName(clazz);
        Table table = this.dynamoDB.getTable(tableName);
        QuerySpec querySpec = toQuerySpec(obj, clazz, projectFields);
        ItemCollection<QueryOutcome> query = table.query(querySpec);
        IteratorSupport<Item, QueryOutcome> iterator = query.iterator();

        List<T> res = new ArrayList<>();
        while(iterator.hasNext()) {
            Item item = iterator.next();
            res.add(toObject(item, clazz, projectFields));
        }
        return res;
    }

    public <T> List<T> scan(Class<T> clazz, List<Field> projectFields, String filterExpression, NameMap nameMap, ValueMap valueMap) {
        String tableName = ClassUtil.getTableName(clazz);
        Table table = this.dynamoDB.getTable(tableName);

        ScanSpec scanSpec = toScanSpec(projectFields, filterExpression, nameMap, valueMap);
        ItemCollection<ScanOutcome> scan = table.scan(scanSpec);
        IteratorSupport<Item, ScanOutcome> iterator = scan.iterator();
        List<T> res = new ArrayList<>();
        while(iterator.hasNext()) {
            Item item = iterator.next();
            T obj = toObject(item, clazz, projectFields);
            res.add(obj);
        }
        return res;
    }

    public <T> boolean delete(T obj, Class<T> clazz) {
        String tableName = ClassUtil.getTableName(clazz);
        Table table = this.dynamoDB.getTable(tableName);

        DeleteItemSpec deleteItemSpec = toDeleteItemSpec(obj, clazz);
        try {
            if(deleteItemSpec == null) {
                throw new Exception("primary key should not be null");
            }
            table.deleteItem(deleteItemSpec);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private <T> DeleteItemSpec toDeleteItemSpec(T obj, Class<T> clazz) {
        DeleteItemSpec deleteItemSpec = null;
        try {
            deleteItemSpec = new DeleteItemSpec();
            List<Field> primaryKeys = ClassUtil.getPrimaryKeys(clazz);
            if(primaryKeys.size() == 1) {
                Field field = primaryKeys.get(0);
                field.setAccessible(true);
                String name = field.getName();
                Object value = field.get(obj);
                if(value == null) return null;
                deleteItemSpec.withPrimaryKey(new PrimaryKey(name, value));
            } else if(primaryKeys.size() == 2) {
                Field field1 = primaryKeys.get(0);
                field1.setAccessible(true);
                String name1 = field1.getName();
                Object value1 = field1.get(obj);
                Field field2 = primaryKeys.get(1);
                field2.setAccessible(true);
                String name2 = field2.getName();
                Object value2 = field2.get(obj);
                if(value1 == null || value2 == null) return null;
                deleteItemSpec.withPrimaryKey(new PrimaryKey(name1, value1, name2, value2));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return deleteItemSpec;
    }

    private ScanSpec toScanSpec(List<Field> projectFields, String filterExpression, NameMap nameMap, ValueMap valueMap) {
        ScanSpec scanSpec = new ScanSpec();
        scanSpec = scanSpec.withProjectionExpression(toProjectExpression(projectFields));
        if(filterExpression != null && !filterExpression.equals("")) {
            scanSpec = scanSpec.withFilterExpression(filterExpression);
        }
        if(nameMap != null) {
            scanSpec = scanSpec.withNameMap(nameMap);
        }
        if(valueMap != null) {
            scanSpec = scanSpec.withValueMap(valueMap);
        }
        return scanSpec;
    }


    private <T> T toObject(Item item, Class<T> clazz, List<Field> projectFields) {
        try {
            T res = clazz.newInstance();
            for (Field field : projectFields) {
                field.setAccessible(true);
                String name = field.getName();
                Object value = item.get(name);
                if(value.getClass().equals(BigDecimal.class)) {
                    setBigDecimal(res, field, (BigDecimal)value);
                } else {
                    field.set(res, value);
                }
            }
            return res;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private <T> void setBigDecimal(T obj, Field field, BigDecimal bigDecimal) {
        try {
            if(field.getType().equals(Integer.class)) {
                field.set(obj, bigDecimal.intValue());
            } else if(field.getType().equals(Long.class)) {
                field.set(obj, bigDecimal.longValue());
            } else if(field.getType().equals(Short.class)) {
                field.set(obj, bigDecimal.shortValue());
            } else if(field.getType().equals(Float.class)) {
                field.set(obj, bigDecimal.floatValue());
            } else if(field.getType().equals(Double.class)) {
                field.set(obj, bigDecimal.doubleValue());
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private <T> QuerySpec toQuerySpec(T obj, Class<T> clazz, List<Field> projectFields) {
        List<Field> primaryKeys = ClassUtil.getPrimaryKeys(clazz);
        QuerySpec res = new QuerySpec();
        ValueMap valueMap = new ValueMap();
        StringBuilder keyConditionExpression = new StringBuilder();
        for (Field field : primaryKeys) {
            field.setAccessible(true);
            try {
                String name = field.getName();
                Object value = field.get(obj);
                if(value != null) {
                    keyConditionExpression.append(name);
                    keyConditionExpression.append(" = :");
                    keyConditionExpression.append(name);
                    keyConditionExpression.append(" and ");
                    valueMap = valueMap.with(":" + name, value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        keyConditionExpression.delete(keyConditionExpression.length() - 5, keyConditionExpression.length() - 1);
        res = res.withKeyConditionExpression(keyConditionExpression.toString());
        res = res.withValueMap(valueMap);
        res = res.withProjectionExpression(toProjectExpression(projectFields));
        return res;
    }

    private String toProjectExpression(List<Field> projectFields) {
        StringBuilder projectionExpression = new StringBuilder();
        for (Field field : projectFields) {
            projectionExpression.append(field.getName());
            projectionExpression.append(",");
        }
        projectionExpression.deleteCharAt(projectionExpression.length() - 1);
        return projectionExpression.toString();
    }

    private <T> Item toItem(T obj, List<Field> fields) {
        Item item = new Item();
        for (Field field : fields) {
            item = addData(obj, field, item);
        }
        return item;
    }

    private <T> Item addData(T obj, Field field, Item item) {
        try {
            field.setAccessible(true);
            Object value = field.get(obj);
            if(value != null) {
                if(value instanceof String) {
                    return item.with(field.getName(), value);
                } else if(value instanceof Integer) {
                    return item.withInt(field.getName(), (Integer)value);
                } else if(value instanceof Long) {
                    return item.withLong(field.getName(), (Long)value);
                } else if(value instanceof List) {
                    return item.withList(field.getName(), (List)value);
                } else if(value instanceof Map){
                    return item.withMap(field.getName(), (Map)value);
                } else {
                    throw new IllegalArgumentException("not support attribute");
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return item;
    }

    private <T> List<KeySchemaElement> getKeySchemaElement(Class<T> clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();
        List<KeySchemaElement> res = new ArrayList<>();
        for (Field field : declaredFields) {
            if(field.isAnnotationPresent(TablePrimaryKey.class)) {
                res.add(new KeySchemaElement(field.getName(), field.getAnnotation(TablePrimaryKey.class).type()));
            }
        }
        return res;
    }

    private <T> List<AttributeDefinition> getAttributeDefinition(Class<T> clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();
        List<AttributeDefinition> res = new ArrayList<>();
        for (Field field : declaredFields) {
            if(field.isAnnotationPresent(TablePrimaryKey.class)) {
                res.add(new AttributeDefinition(field.getName(), field.getAnnotation(TablePrimaryKey.class).attribute()));
            }
        }
        return res;
    }

}
