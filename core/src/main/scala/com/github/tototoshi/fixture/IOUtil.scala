package com.github.tototoshi.fixture

private[fixture] object IOUtil {

  type Closable = { def close() }

  def using[R <: Closable, A](resource: R)(f: R => A): A = {
    try {
      f(resource)
    } finally {
      try {
        resource.close()
      } catch {
        case scala.util.control.NonFatal(_) =>
      }
    }
  }

}