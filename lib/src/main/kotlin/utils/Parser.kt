package utils

fun interface Parser<In> {
  operator fun invoke(input: String): In

  fun <T> map(fn: (In) -> T): Parser<T> {
    return Parser { fn(invoke(it)) }
  }

  companion object {
    val lines = Parser { it.split('\n').filter(String::isNotBlank) }
    val ints = Parser { it.trim().split(',').map { num -> num.trim().toInt() } }
    val intLines = lines.mapItems { it.toInt() }
    val chars = Parser { it.trim().toCharArray().toList() }

    /**
     * combine two parsers into one, separate by a delimiter
     */
    fun <R1, R2> compound(first: Parser<R1>, second: Parser<R2>): Parser<Pair<R1, R2>> = compound("\n\n", first, second)
    fun <R1, R2> compound(delimiter: String, first: Parser<R1>, second: Parser<R2>): Parser<Pair<R1, R2>> {
      return Parser { input ->
        input.cut(delimiter, { first(it) }, { second(it) })
      }
    }

    /**
     * Combine two parsers into one - a header parser and a repeating item parser
     */
    fun <R1, R2> compoundList(header: Parser<R1>, item: Parser<R2>) = compoundList("\n\n", header, item)
    fun <R1, R2> compoundList(delimiter: String, header: Parser<R1>, item: Parser<R2>): Parser<Pair<R1, List<R2>>> {
      return Parser { input ->
        val (header, rest) = input.cut(delimiter)

        header(header) to rest.split(delimiter).map { it.trim() }.filter(String::isNotBlank).map { item(it) }
      }
    }
  }
}

fun <T, U> Parser<List<T>>.mapItems(fn: (T) -> U): Parser<List<U>> {
  return Parser { this.invoke(it).map(fn) }
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
