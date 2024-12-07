import utils.Parser
import utils.Solution
import utils.cut
import utils.map

fun main() {
  Day7.run()
}

typealias Day7In = Pair<String, String>

object Day7 : Solution<Day7In>() {
  override val name = "day7"
  override val parser: Parser<Day7In> = Parser { it.cut("\n").map { line -> line.trim() } }

  // slow brute-force solution, runs in about 5ish seconds
  override fun part1(input: Day7In): String {
    val (keyChars, s) = input

    val locations = s.indices.filter { s[it] in keyChars }

    var best = locations[0] to locations.last()

    locations.indices.forEach { start ->
      locations.indices.filter { it > start }.forEach { end ->
        if (locations[end] - locations[start] < (best.second - best.first)) {
          if (locations[end] - locations[start] >= keyChars.length) {
            if (keyChars.all { it in s.substring(locations[start], locations[end] + 1) }) {
              best = locations[start] to locations[end]
            }
          }
        }
      }
    }

    return s.substring(best.first, best.second + 1)
  }

  // faster variant, linear scan of the input string, <10ms
  override fun part2(input: Day7In): String {
    val (key, s) = input

    val seen = IntArray(key.length) { -10000 }
    var best = Integer.MAX_VALUE to -1

    s.forEachIndexed { i, c ->
      val ci = key.indexOf(c)
      if (ci != -1) {
        seen[ci] = i
        val start = seen.min()
        val end = seen.max()
        if (end - start < best.first) {
          best = (end - start) to start
        }
      }
    }

    return s.substring(best.second, best.second + best.first + 1)
  }
}
