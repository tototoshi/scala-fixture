package com.github.tototoshi.fixture

import scala.language.reflectiveCalls

private[fixture] object IOUtil {

  type Closable = { def close(): Unit }

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