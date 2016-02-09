# scala-fixture

simple fixture for scala


## Install

```scala
libraryDependencies += "com.github.tototoshi" %% "scala-fixture" % "0.1.2"
```


## Usage

```scala
package com.github.tototoshi.fixture

import org.scalatest.{ BeforeAndAfter, FunSuite }
import com.github.tototoshi.fixture._

class FixtureTest extends FunSuite with BeforeAndAfter {

  val driver = "org.h2.Driver"
  val url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"
  val username = "sa"
  val password = ""

  val fixture = Fixture(driver, url, username, password)
    .scriptLocation("db/fixtures/default")
    .scriptPackage("com.example.fixtures")
    .scripts(Seq("script1.sql", "script2.sql", "MyFixtureScript"))

  before {
    fixture.setUp()
  }

  after {
    fixture.tearDown()
  }

  test("load fixtures") {
    // write tests
  }

}
```

### SQL fixture script

SQL fixture script is simple SQL script that has `SetUp` part and `TearDown` part.
`SetUp` part is executed by `Fixture#setUp` and `TearDown` by `Fixture#tearDown`


```sql
#!SetUp
INSERT INTO users(id, name) VALUES (1, 'user1');
#!TearDown
DELETE FROM users WHERE id = 1;
```

You can replace `SetUp` with `Ups` and `TearDown` with `Downs` like play-evolutions or scalikejdbc-play-fixtures.


SQL fixture scripts are placed in resource directory.
The location can be specified by `Fixture#scriptLocation`.

```
 test/resources
 ├── db
 │   ├── fixtures
 │   │   └── default
 │   │       ├── script1.sql
 │   │       └── script2.sql
```


### Java(Scala)-based fixture script

Java-based fixture script is also available for the time you want to write a little complicated one.
Define the class that extends `com.github.tototoshi.fixture.FixtureScript` and implement `setUp` and `tearDown`


```scala
package com.example.fixtures

import java.sql.Connection
import com.github.tototoshi.fixture.FixtureScript

class MyFixtureScript extends FixtureScript {

  override def setUp(connection: Connection): Unit = {
    connection.prepareStatement("insert into users(id, name) values (3, 'user3')").execute()
  }

  override def tearDown(connection: Connection): Unit = {
    connection.prepareStatement("delete from users where id = 3").execute()
  }

}
```

If you define `com.example.MyFixtureScript`. You can specify it like `.scripts(Seq("com.example.MyFixtureScript"))` or `.scriptPackage("com.example.fixtures").scripts(Seq("MyFixtureScript"))`.

SQL fixture script and Java(Scala)-based Fixture script can be used in mixture. When SQL script not found, this library tries to find Java-based script. So you can write like the following.


```scala
  val fixture = Fixture(driver, url, username, password)
    .scriptLocation("db/fixtures/default")
    .scriptPackage("com.example")
    .scripts(Seq("script1.sql", "script2.sql", "MyFixtureScript"))
```


# scala-fixture Play Module

## Install

```scala
libraryDependencies += "com.github.tototoshi" %% "scala-fixture-play" % "0.1.0-SNAPSHOT"
```

## Cofiguration

```
play.modules.enabled += "com.github.tototoshi.fixture.play.FixtureModule"

db.default.driver=org.h2.Driver
db.default.url="jdbc:h2:mem:play"
db.default.username=sa
db.default.password=""
db.default.fixtures.scriptLocation="db/fixtures/default"
db.default.fixtures.scriptPackage="com.example.fixture"
db.default.fixtures.scripts=["script1.sql", "script2.sql"]
```

## LICENSE

Apache 2.0
