package utils

val String.parts: List<String> get() = split(" ").mapNotNull { it.trim().takeIf(String::isNotEmpty) }
fun String.split(): Pair<String, String> {
  val mid = length / 2
  if (mid * 2 != length) {
    badInput()
  }
  return substring(0, mid) to substring(mid)
}

fun String.findAll(needle: String): List<Int> {
  val out = mutableListOf<Int>()
  var index = indexOf(needle, 0)
  while (index < this.length && index != -1) {
    out += index
    index = indexOf(needle, index + needle.length)
  }

  return out
}

fun String.triplicut(d1: String, d2: String): Triple<String, String, String> {
  val (ab, c) = cut(d2)
  val (a, b) = ab.cut(d1)
  return Triple(a, b, c)
}

fun String.cut(delimiter: String = ","): Pair<String, String> {
  val idx = indexOf(delimiter)
  require(idx != -1)
  return substring(0, idx).trim() to substring(idx + delimiter.length).trim()
}

fun <R> String.cut(delimiter: String, out: (String, String) -> R): R {
  val idx = indexOf(delimiter)
  require(idx != -1)
  return out(substring(0, idx).trim(), substring(idx + delimiter.length).trim())
}

fun <R> String.cut(p: (String) -> R): Pair<R, R> = cut(",", p)
fun <R> String.cut(delimiter: String, p: (String) -> R): Pair<R, R> {
  val idx = indexOf(delimiter)
  require(idx != -1)
  return p(substring(0, idx).trim()) to p(substring(idx + delimiter.length).trim())
}

fun <Left, Right> String.cut(p1: (String) -> Left, p2: (String) -> Right) = cut(",", p1, p2)
fun <Left, Right> String.cut(delimiter: String, p1: (String) -> Left, p2: (String) -> Right): Pair<Left, Right> {
  val idx = indexOf(delimiter)
  require(idx != -1)

  return p1(substring(0, idx).trim()) to p2(substring(idx + delimiter.length).trim())
}

fun <Left, Right, R> String.cut(p1: (String) -> Left, p2: (String) -> Right, p3: (Left, Right) -> R) = cut(",", p1, p2, p3)
fun <Left, Right, R> String.cut(delimiter: String, p1: (String) -> Left, p2: (String) -> Right, p3: (Left, Right) -> R): R {
  val idx = indexOf(delimiter)
  require(idx != -1)
  return p3(p1(substring(0, idx).trim()), p2(substring(idx + delimiter.length).trim()))
}
