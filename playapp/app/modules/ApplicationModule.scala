package modules

import play.api.inject.{ Binding, Module }
import play.api.{ Configuration, Environment }
import scalikejdbc.ConnectionPool

class ApplicationModule extends Module {
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = Seq(
    bind[ConnectionPool].toProvider[ConnectionPoolProvider].eagerly(),
    bind[ConnectionPoolShutdown].toSelf.eagerly())
}
