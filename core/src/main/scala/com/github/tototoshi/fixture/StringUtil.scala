package com.github.tototoshi.fixture

private[fixture] object StringUtil {

  def trimTrailingSlashes(s: String): String = s.reverse.dropWhile('/' == _).reverse

  def trimLeadingSlashes(s: String): String = s.dropWhile('/' == _)

  def trimSlashes(s: String): String = trimLeadingSlashes(trimTrailingSlashes(s))

}
