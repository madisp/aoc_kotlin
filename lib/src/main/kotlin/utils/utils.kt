package utils

import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

object Utils

fun readFile(name: String): String {
  val stream = Utils.javaClass.getResourceAsStream("/$name.txt")
  require (stream != null) {
    "No input @ /$name.txt!"
  }
  return stream.use { it.readBytes().toString(Charsets.UTF_8) }
}

fun readInput(year: Int, day: Int): String {
  val file = File("").resolve(year.toString()).resolve("src").resolve("main").resolve("resources").resolve("day${day}.txt")

  if (!file.exists()) {
    val cookie = readFile("cookie").trim()
    val client = HttpClient.newBuilder()
      .version(HttpClient.Version.HTTP_1_1)
      .followRedirects(HttpClient.Redirect.NORMAL)
      .connectTimeout(Duration.ofSeconds(5))
      .build()

    val request = HttpRequest.newBuilder()
      .GET()
      .uri(URI("https://adventofcode.com/${year}/day/${day}/input"))
      .header("Cookie", cookie)
      .build()
    val resp = client.send(request, HttpResponse.BodyHandlers.ofInputStream())
    resp.body().use { input ->
      file.outputStream().use { output ->
        input.copyTo(output, bufferSize = 8192)
      }
    }
  }

  require (file.exists()) {
    "Could not fetch input for $year / day $day"
  }

  return file.readText(Charsets.UTF_8)
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

inline fun <reified T> Iterable<T>.takeWhileInclusive(predicate: (T) -> Boolean): List<T> {
  val list = ArrayList<T>()
  for (item in this) {
    list.add(item)
    if (!predicate(item))
      break
  }
  return list
}

fun String.split(): Pair<String, String> {
  val mid = length / 2
  if (mid * 2 != length) {
    badInput()
  }
  return substring(0, mid) to substring(mid)
}

fun <T> Collection<T>.split(): Pair<Collection<T>, Collection<T>> {
  val halfSize = size / 2
  if (halfSize * 2 != size) { badInput() }
  return take(halfSize) to drop(halfSize)
}

val <T1, T2> Pair<T1, T2>.flipped: Pair<T2, T1> get() = second to first

fun Int.wrap(max: Int): Int {
  return if (this > 0) {
    this % max
  } else {
    (max + (this % max)) % max
  }
}

fun badInput(): Nothing {
  throw IllegalArgumentException("bad input")
}
