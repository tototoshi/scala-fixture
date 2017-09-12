package modules

import javax.inject.{ Inject, Provider, Singleton }

import play.api.Logger
import play.api.inject.ApplicationLifecycle
import scalikejdbc.config.TypesafeConfigReader
import scalikejdbc.{ Commons2ConnectionPoolFactory, ConnectionPool }

import scala.concurrent.{ ExecutionContext, Future }

class ConnectionPoolProvider extends Provider[ConnectionPool] {
  private val logger = Logger(classOf[ConnectionPoolProvider])
  override def get(): ConnectionPool = {
    val jdbc = TypesafeConfigReader.readJDBCSettings()
    logger.info("Preparing connection pool...")
    Commons2ConnectionPoolFactory(jdbc.url, jdbc.user, jdbc.password)
  }
}

@Singleton
class ConnectionPoolShutdown @Inject() (
  lifecycle: ApplicationLifecycle,
  connectionPool: ConnectionPool, executionContext: ExecutionContext) {
  private val logger = Logger(classOf[ConnectionPoolShutdown])
  lifecycle.addStopHook(() => Future {
    logger.info("Shutdown connection pool")
    connectionPool.close()
  }(executionContext))
}
