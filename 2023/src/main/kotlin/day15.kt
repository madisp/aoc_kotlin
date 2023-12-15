import utils.Parser
import utils.Solution

fun main() {
  Day15arr.run()
}

object Day15arr : Solution<List<String>>() {
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

  override fun part1(): Int {
    return input.sumOf { hash(it) }
  }

  override fun part2(): Int {
    val hashmap = Array(256) {
      mutableListOf<Pair<String, Int>>()
    }

    input.forEach { str ->
      val oper = str.indexOfAny(charArrayOf('=', '-'))
      when (str[oper]) {
        '=' -> {
          // store
          val key = str.substring(0 until oper)
          val value = str.substring(oper + 1).toInt()
          val h = hash(key)
          val index = hashmap[h].indexOfFirst { it.first == key }
          if (index == -1) {
            hashmap[h].add(key to value)
          } else {
            hashmap[h][index] = key to value
          }
        }
        else -> {
          // delete
          val key = str.substring(0 until oper)
          hashmap[hash(key)].removeIf { it.first == key }
        }
      }
    }

    return hashmap.withIndex().flatMap { (box, list) ->
      list.withIndex().map { (slot, length) ->
        (box + 1) * (slot + 1) * length.second
      }
    }.sum()
  }
}
