package models

import com.github.tototoshi.fixture.Fixture
import org.flywaydb.core.Flyway
import org.scalatest.{ BeforeAndAfterAll, FunSuite }
import scalikejdbc.Commons2ConnectionPoolFactory
import scalikejdbc.config.TypesafeConfigReader

class UserRepositoryTest extends FunSuite with BeforeAndAfterAll {

  val jdbc = TypesafeConfigReader.readJDBCSettings('test)
  val pool = Commons2ConnectionPoolFactory(jdbc.url, jdbc.user, jdbc.password)

  val flyway = new Flyway()
  flyway.setDataSource(jdbc.url, jdbc.user, jdbc.password)
  flyway.setLocations("db/migration/default")

  val fixture = Fixture(jdbc.driverName, jdbc.url, jdbc.user, jdbc.password)
    .scriptLocation("db/fixtures/default")
    .scripts(Seq("script1.sql", "script2.sql"))

  flyway.migrate()
  fixture.setUp()

  override def afterAll(): Unit = {
    fixture.tearDown()
    flyway.clean()
    pool.close()
  }

  test("UserRepository can find all users") {
    val userRepository = new UserRepository(pool)
    assert(userRepository.findAll() == Seq(User(1, "user1"), User(2, "user2")))
  }

}
