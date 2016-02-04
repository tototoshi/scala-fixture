package com.github.tototoshi.fixture.play

import java.io.{ InputStream, ByteArrayOutputStream }

private[fixture] object IOUtil {

  def readInputStreamToString(stream: InputStream): String = {
    val buffer = new Array[Byte](8192)
    var len = stream.read(buffer)
    using(new ByteArrayOutputStream()) { out =>
      while (len != -1) {
        out.write(buffer, 0, len)
        len = stream.read(buffer)
      }
      new String(out.toByteArray)
    }
  }

  def using[A <: { def close(): Unit }, B](resource: A)(f: A => B): B = {
    try {
      f(resource)
    } finally {
      resource.close()
    }
  }
}

