package utils

object Utils

fun readFile(name: String): String {
  return Utils.javaClass.getResourceAsStream("/$name.txt").readBytes().toString(Charsets.UTF_8)
}

fun <A, B> Pair<A, A>.map(fn: (A) -> B): Pair<B, B> = fn(first) to fn(second)

/**
 * Merge a list of pairs into a map. If items are present with the same key multiple times then they will be
 * reduced according to the collision function.
 *
 * Normal `mapOf`: `mapOf("a" to 1, "a" to 4, "b" to 2) == mapOf("a" to 4, "b" to 2)`
 * `mergeToMap`: `listOf("a" to 1, "a" to 4, "b" to 2).mergeToMap { _, v1, v2 -> v1 + v2 } == mapOf("a" to 5, "b" to 2)`
 *
 */
fun <K, V> List<Pair<K, V>>.mergeToMap(collisionFn: (K, V, V) -> V): Map<K, V> {
  return groupBy(keySelector = Pair<K, V>::first, valueTransform = Pair<K, V>::second)
    .mapValues { (k, v) -> v.reduce { v1, v2 -> collisionFn(k, v1, v2) } }
    .toMap()
}

fun <T> Iterable<T>.withCounts(): Map<T, Int> {
  return groupBy { it }.mapValues { (_, c) -> c.size }
}

fun <T> Iterable<T>.withLCounts(): Map<T, Long> {
  return groupBy { it }.mapValues { (_, c) -> c.size.toLong() }
}

operator fun <T> List<T>.times(other: List<T>): List<Pair<T, T>> {
  return flatMap { first ->
    other.map { second ->
      first to second
    }
  }
}

fun <T> List<T>.product(other: List<T>): List<Pair<T, T>> = this * other

inline fun <T, V> List<T>.product(other: List<T>, fn: (v1: T, v2: T) -> V): List<V> {
  return flatMap { first ->
    other.map { second ->
      fn(first, second)
    }
  }
}

inline fun <T, V> List<T>.productIndexed(other: List<T>, fn: (i1: Int, v1: T, i2: Int, v2: T) -> V): List<V> {
  return flatMapIndexed { i1, v1 ->
    other.mapIndexed { i2, v2 ->
      fn(i1, v1, i2, v2)
    }
  }
}

fun <T> List<T>.product(): List<Pair<T, T>> {
  return flatMapIndexed { index, first ->
    subList(index + 1, size).map { second ->
      first to second
    }
  }
}

inline fun <T, V> List<T>.product(fn: (v1: T, v2: T) -> V): List<V> {
  return flatMapIndexed { index, first ->
    subList(index + 1, size).map { second ->
      fn(first, second)
    }
  }
}

inline fun <T, V> List<T>.productIndexed(fn: (i1: Int, v1: T, i2: Int, v2: T) -> V): List<V> {
  return flatMapIndexed { i1, v1 ->
    subList(i1 + 1, size).mapIndexed { i2, v2 ->
      fn(i1, v1, i1 + 1 + i2, v2)
    }
  }
}

fun <T> Collection<T>.startsWith(other: Collection<T>): Boolean {
  if (this.size < other.size) {
    return false
  }
  val me = iterator()
  val them = other.iterator()
  while (them.hasNext()) {
    if (me.next() != them.next()) {
      return false
    }
  }
  return true
}

val <T1, T2> Pair<T1, T2>.flipped: Pair<T2, T1> get() = second to first

fun Int.wrap(max: Int): Int {
  return if (this > 0) {
    this % max
  } else {
    (max + (this % max)) % max
  }
}
