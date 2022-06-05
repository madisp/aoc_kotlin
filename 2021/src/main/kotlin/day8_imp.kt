import utils.Parser
import utils.Solution
import utils.cut
import utils.mapItems

fun main() {
  Day8Imp.run()
}

object Day8Imp : Solution<List<Day8Imp.Key>>() {
  override val name = "day8"
  override val parser = Parser.lines.mapItems {
    it.cut("|") { input, output -> Key(input.split(' '), output.split(' ')) }
  }

  override fun part1(input: List<Key>): Int {
    var count = 0
    for (key in input) {
      for (out in key.output) {
        if (out.length == 2 || out.length == 4 || out.length == 3 || out.length == 7) {
          count++
        }
      }
    }
    return count
  }

  override fun part2(input: List<Key>): Int {
    var sum = 0
    for (key in input) {
      sum += solve(key)
    }
    return sum
  }

  fun <T> Set<T>.one(): T {
    if (this.size != 1) { throw IllegalStateException("More than one item in $this!") }
    return first()
  }

  fun solve(key: Key): Int {
    // 1:   c  f   // len = 2

    // 7: a c  f   // len = 3

    // 4:  bcd f   // len = 4

    // 2: a cde g  // len = 5
    // 3: a cd fg  // len = 5
    // 5: ab d fg  // len = 5

    // 0: abc efg  // len = 6
    // 6: ab defg  // len = 6
    // 9: abcd fg  // len = 6

    // 8: abcdefg  // len = 7 <-- ignore

    /*
    a = chars(3) - chars(2) // fix
    b = only char in chars(4) that appears in chars(5) _once_
    d = only char in chars(4) that appears in chars(5) _thrice_
    f = only char in chars(2) that appears in chars(6) _thrice_
    c = chars(2) - 'f'
    g = in all of chars(5), chars(6) but not 'a'
    e = not 'a', 'b', 'c', 'd', 'f' or 'g'
     */

    val track = Array(size = 8) { mutableListOf<Char>() }
    key.digits.forEach {
      track[it.length].addAll(it.toCharArray().toList())
    }

    val t5 = track[5].groupBy { g -> track[5].count { g == it } }.mapValues { (_, v) -> v.toSet() }
    val t6 = track[5].groupBy { g -> track[6].count { g == it } }.mapValues { (_, v) -> v.toSet() }

    val s = mutableMapOf<Char, Char>()
    s['a'] = (track[3].toSet() - track[2].toSet()).one()
    s['b'] = (track[4].toSet() intersect t5[1]!!).one()
    s['d'] = (track[4].toSet() intersect t5[3]!!).one()
    s['f'] = (track[2].toSet() intersect t6[3]!!).one()
    s['c'] = (track[2].toSet() - setOf(s['f']!!)).one()
    s['g'] = ((t5[3]!! intersect t6[3]!!) - setOf(s['a']!!)).one()
    s['e'] = (('a' .. 'g').toSet() - s.values.toSet()).one()

    val inv = mutableMapOf<Char, Char>()
    for ((k, v) in s) {
      inv[v] = k
    }

    var out = 0
    for (digit in key.output) {
      val mapped = CharArray(digit.length)
      digit.forEachIndexed { index, c ->
        mapped[index] = inv[c]!!
      }
      out *= 10
      try {
        out += A[mapped.sorted().joinToString("")]!!
      } catch (e: Exception) {
        println("Failed with ${mapped.sorted().joinToString("")}")
      }
    }

    return out
  }

  val A = mapOf(
    "abcefg" to 0,
    "cf" to 1,
    "acdeg" to 2,
    "acdfg" to 3,
    "bcdf" to 4,
    "abdfg" to 5,
    "abdefg" to 6,
    "acf" to 7,
    "abcdefg" to 8,
    "abcdfg" to 9
  )

  data class Key(val digits: List<String>, val output: List<String>)
}
