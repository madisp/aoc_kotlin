package utils

fun interface Parser<In> {
  operator fun invoke(input: String): In

  fun <T> map(fn: (In) -> T): Parser<T> {
    return Parser { fn(invoke(it)) }
  }

  companion object {
    val lines = Parser { it.split('\n').map { it.trim() }.filter(String::isNotBlank) }
    val ints = Parser { it.trim().split(',').map { num -> num.trim().toInt() } }
    val longs = Parser { it.trim().split(',').map { num -> num.trim().toLong() } }
    val spacedInts = Parser { it.parts.map(String::toInt) }
    val spacedLongs = Parser { it.parts.map(String::toLong) }
    val intLines = lines.mapItems { it.toInt() }
    val longLines = lines.mapItems { it.toLong() }
    val chars = Parser { it.trim().toCharArray().toList() }
    val blocks = Parser { it.split("\n\n").map(String::trim).filter(String::isNotBlank) }
    val digitGrid = IntGrid.singleDigits
    val charGrid = Grid.chars()

    /**
     * combine two parsers into one, separate by a delimiter
     */
    fun <R1, R2> compound(first: Parser<R1>, second: Parser<R2>): Parser<Pair<R1, R2>> = compound("\n\n", first, second)
    fun <R1, R2> compound(delimiter: String, first: Parser<R1>, second: Parser<R2>): Parser<Pair<R1, R2>> {
      return Parser { input ->
        val (p1, p2) = input.split(delimiter, limit = 2)
        first(p1) to second(p2)
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

fun <T> Parser<String>.mapParser(parser: Parser<T>): Parser<T> {
  return Parser { input -> parser(this(input)) }
}

fun <T, U> Parser<List<T>>.mapItems(fn: (T) -> U): Parser<List<U>> {
  return Parser { this.invoke(it).map(fn) }
}

fun <T> Parser<List<String>>.parseItems(parser: Parser<T>): Parser<List<T>> {
  return mapItems { item -> parser.invoke(item) }
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
