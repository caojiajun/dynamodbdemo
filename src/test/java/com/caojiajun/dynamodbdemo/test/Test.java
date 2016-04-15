package com.caojiajun.dynamodbdemo.test;

import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.caojiajun.dynamodbdemo.dynamodb.DynamodbOperation;
import com.caojiajun.dynamodbdemo.model.SomeOne;
import com.caojiajun.dynamodbdemo.model.Student;

import java.util.Arrays;
import java.util.List;

/**
 * Created by caojiajun on 2016/4/15.
 */
public class Test {
    DynamodbOperation dynamodbOperation = new DynamodbOperation();

    public void testCreateTable() {
        dynamodbOperation.createTable(SomeOne.class, 10L, 10L);
//        dynamodbOperation.createTable(Student.class, 10L, 10L);
    }

    public void testDeleteTable() {
        dynamodbOperation.deleteTable(SomeOne.class);
//        dynamodbOperation.deleteTable(Student.class);
    }

    public void testPut1() {

        SomeOne someOne1 = new SomeOne(1, "Mao", 123, "library", Arrays.asList("xingxingzhihuo keyiliaoyuan", "hunannongminyundongkaocha"));
        dynamodbOperation.put(someOne1, SomeOne.class, SomeOne.ALL_PRIVATE_KEY_FIELDS);

        SomeOne someOne2 = new SomeOne(2, "Deng", 112, "france", Arrays.asList("white cat", "black cat", "the science and technology is first productivity"));
        dynamodbOperation.put(someOne2, SomeOne.class, SomeOne.ALL_PRIVATE_KEY_FIELDS);

        SomeOne someOne3 = new SomeOne(3, "Jiang", 90, "jtdx", Arrays.asList("excited", "too young", "too naive", "angry"));
        dynamodbOperation.put(someOne3, SomeOne.class, SomeOne.ALL_PRIVATE_KEY_FIELDS);

        SomeOne someOne4 = new SomeOne(4, "Hu", 74, "tsinghua", Arrays.asList("lasazhihu", "siwanyi", "aoyunhui"));
        dynamodbOperation.put(someOne4, SomeOne.class, SomeOne.ALL_PRIVATE_KEY_FIELDS);

        SomeOne someOne5 = new SomeOne(1, "Xi", 63, "gongnongbing", Arrays.asList("qingfeng", "diangezan", "dalaohu", "paicangying"));//id = 1，but uname != 'Mao', so it is legal
        dynamodbOperation.put(someOne5, SomeOne.class, SomeOne.ALL_PRIVATE_KEY_FIELDS);
    }

    public void testPut2() {
        Student student1 = new Student(1, "hanmeimei", 1, "English", "lilei or jim?");
        dynamodbOperation.put(student1, Student.class, Student.ALL_PRIVATE_KEY_FIELDS);

        Student student2 = new Student(2, "lilei", 2, "pick up the girl", "i love hanmeimei");
        dynamodbOperation.put(student2, Student.class, Student.ALL_PRIVATE_KEY_FIELDS);

        Student student3 = new Student(3, "jim", 2, "hit on a girl", "i love hanmeimei,too");
        dynamodbOperation.put(student3, Student.class, Student.ALL_PRIVATE_KEY_FIELDS);
    }

    public void testGet1() {

        SomeOne someOne1 = new SomeOne();
        someOne1.setId(1);
        List<SomeOne> someOnes1 = dynamodbOperation.get(someOne1, SomeOne.class, SomeOne.ALL_PRIVATE_KEY_FIELDS);
        for (SomeOne one : someOnes1) {//two records
            System.out.println(one);
        }

        SomeOne someOne2 = new SomeOne();
        someOne2.setId(1);
        someOne2.setUname("Mao");
        List<SomeOne> someOnes2 = dynamodbOperation.get(someOne2, SomeOne.class, SomeOne.ALL_PRIVATE_KEY_FIELDS);
        for (SomeOne one : someOnes2) {//one record
            System.out.println(one);
        }
    }

    public void testScan() {
        List<SomeOne> scan1 = dynamodbOperation.scan(SomeOne.class, SomeOne.ALL_PRIVATE_KEY_FIELDS, null, null, null);
        for (SomeOne one : scan1) {
            System.out.println(one);
        }

        System.out.println("====");

        List<SomeOne> scan2 = dynamodbOperation.scan(SomeOne.class, SomeOne.ALL_PRIVATE_KEY_FIELDS,
                "age between :start and :end", null, new ValueMap().withNumber(":start", 100).withNumber(":end", 130));
        for (SomeOne one : scan2) {
            System.out.println(one);
        }
    }

    public void testDelete() {
        SomeOne someOne1 = new SomeOne();
        someOne1.setId(2);
//        someOne1.setUname("Mao");
        dynamodbOperation.delete(someOne1, SomeOne.class);
    }

    public static void main(String[] args) {
        Test test = new Test();
        test.testDelete();
//        test.testScan();

    }

}
