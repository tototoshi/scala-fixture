package com.github.tototoshi.fixture

private[fixture] object StringUtil {

  def trimTrailingSlashes(s: String): String = s.reverse.dropWhile('/'==).reverse

  def trimLeadingSlashes(s: String): String = s.dropWhile('/'==)

  def trimSlashes(s: String): String = trimLeadingSlashes(trimTrailingSlashes(s))

}
