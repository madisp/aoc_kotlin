package utils

import java.io.File
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.util.Arrays

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

    if (resp.statusCode() >= 400) {
      throw IOException("Request to AoC servers failed, status=${resp.statusCode()}")
    }

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

val <T> List<T>.permutations: Sequence<List<T>> get() {
  val indices = IntArray(size) { it }
  val list = MutableList(size) { this[it] }
  return generateSequence(list as List<T>) { _ ->
    val f = (size - 2 downTo 0).firstOrNull { indices[it] < indices[it + 1] } ?: return@generateSequence null

    val n = (f + 1 until size).filter { indices[it] > indices[f] }.minBy { indices[it] }

    indices[f] = indices[n].also {
      indices[n] = indices[f]
    }

    Arrays.sort(indices, f + 1, size)

    list.clear()
    indices.mapTo(list) { this[it] }

    list
  }
}

val <T> List<T>.combinations: Sequence<List<T>> get() {
  val list = MutableList(size) { this[it] }
  val take = BooleanArray(size) { false }
  return generateSequence(emptyList()) { _ ->
    val lastFalse = take.lastIndexOf(false)
    if (lastFalse == -1) {
      return@generateSequence null
    }

    // flip lastFalse to true, following items to false
    take[lastFalse] = true
    (lastFalse + 1 until take.size).forEach {
      take[it] = false
    }

    list.clear()
    (0 .. lastFalse).forEach { i ->
      if (take[i]) {
        list.add(this[i])
      }
    }

    return@generateSequence list
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

inline fun pow2(n: Int): Int = 1 shl n

fun Int.pow(n: Int): Int {
  var value = 1
  require (n >= 0) {
    "Cannot do int pow with negative exponent"
  }
  repeat(n) {
    value *= this
  }
  return value
}

fun Long.pow(n: Int): Long {
  var value = 1L
  require (n >= 0) {
    "Cannot do long pow with negative exponent"
  }
  repeat(n) {
    value *= this
  }
  return value
}

fun Int.wrap(max: Int): Int {
  return if (this > 0) {
    this % max
  } else {
    (max + (this % max)) % max
  }
}

/**
 * Split the range into a triplet of ranges:
 * - intersection of the two ranges
 * - list of ranges in the first range but not the second
 */
infix fun LongRange.tesselateWith(other: LongRange): Pair<LongRange?, List<LongRange>> {
  val intersection = (maxOf(first, other.first) .. minOf(last, other.last))
  val left = (first .. minOf(last, other.first - 1))
  val right = (maxOf(first, other.last + 1) .. last)
  return intersection.takeIf { !it.isEmpty() } to listOf(left, right).filter { !it.isEmpty() }
}

/**
 * Split the range into a triplet of ranges:
 * - intersection of the two ranges
 * - list of ranges in the first range but not the second
 */
infix fun IntRange.tesselateWith(other: IntRange): Pair<IntRange?, List<IntRange>> {
  val intersection = (maxOf(first, other.first) .. minOf(last, other.last))
  val left = (first .. minOf(last, other.first - 1))
  val right = (maxOf(first, other.last + 1) .. last)
  return intersection.takeIf { !it.isEmpty() } to listOf(left, right).filter { !it.isEmpty() }
}

fun badInput(): Nothing {
  throw IllegalArgumentException("bad input")
}
