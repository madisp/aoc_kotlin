object Utils

fun readFile(name: String): String {
  return Utils.javaClass.getResourceAsStream("/$name.txt").readBytes().toString(Charsets.UTF_8)
}

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

fun <T> List<T>.withCounts(): Map<T, Int> {
  return groupBy { it }.mapValues { (_, c) -> c.size }
}
