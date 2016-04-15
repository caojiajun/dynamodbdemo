# dynamodbdemo

this project try to provide a common interface for java programmer to use amazon dynamodb java sdk more conveniently.

in this demo, we design a class named DynamodbOperation.java. meanwhile, we provide two annotation named Table.java and TablePrimaryKey.java, and a util named ClassUtil.java.

if you want to use our interface, you should design a model mapping to a table in dynamodb, such like Someone.java and Student.java in our demo.

in the model, you should add two annotation, one declares the tableName, the other declares the primary key, and make sure your model have a constructor without arguments.

then, you can use the interface in DynamodnOperation.java to interact with the dynamodb, we support such public methods:

1) createTable

2) deleteTable

3) put

4) get

5) scan

6) delete

..........................
before you can run our demo, you should make sure:

(1) you have downloaded the local dynamodb and run it

if not, go to [amazon.dynamodb](http://docs.aws.amazon.com/amazondynamodb/latest/gettingstartedguide/GettingStarted.JsShell.html)

(2) you have installed maven(a excellent project build tool for java)

if not, go to [apache.maven](http://maven.apache.org/)

while you startup the local version and build success our demo, you can run Test.java to use the interfaces in DynamodnOperation.java



