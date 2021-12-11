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
  }
}

fun <T, U> Parser<List<T>>.mapItems(fn: (T) -> U): Parser<List<U>> {
  return Parser { this.invoke(it).map(fn) }
}
