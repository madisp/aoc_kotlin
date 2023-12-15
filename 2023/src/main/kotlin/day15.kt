import utils.Parser
import utils.Solution

fun main() {
  Day15.run()
}

object Day15 : Solution<List<String>>() {
  override val name = "day15"
  override val parser = Parser { it.trim().split(",") }

  private fun hash(str: String): Int {
    var reg = 0
    for (c in str) {
      reg += c.code
      reg *= 17
      reg %= 256
    }
    return reg
  }

  private class Hashmap {
    val buckets = Array(256) { mutableListOf<Pair<String, Int>>() }

    operator fun get(key: String): Int? {
      return buckets[hash(key)].firstNotNullOfOrNull { (k, v) -> v.takeIf { k == key } }
    }

    operator fun set(key: String, value: Int): Int {
      val h = hash(key)
      val index = buckets[h].indexOfFirst { it.first == key }
      if (index == -1) {
        buckets[h].add(key to value)
      } else {
        buckets[h][index] = key to value
      }
      return value
    }

    fun remove(key: String) {
      buckets[hash(key)].removeIf { it.first == key }
    }
  }

  override fun part1(): Int {
    return input.sumOf { hash(it) }
  }

  override fun part2(): Int {
    val hashmap = Hashmap()

    input.forEach { str ->
      val oper = str.indexOfAny(charArrayOf('=', '-'))
      when (str[oper]) {
        '=' -> {
          // store
          val key = str.substring(0 until oper)
          val value = str.substring(oper + 1).toInt()
          hashmap[key] = value
        }
        else -> {
          // delete
          val key = str.substring(0 until oper)
          hashmap.remove(key)
        }
      }
    }

    return hashmap.buckets.withIndex().flatMap { (box, list) ->
      list.withIndex().map { (slot, length) ->
        (box + 1) * (slot + 1) * length.second
      }
    }.sum()
  }
}
