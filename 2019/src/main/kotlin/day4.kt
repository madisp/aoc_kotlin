import utils.Parser
import utils.Solution
import utils.cut
import utils.map

fun main() {
  Day4.run()
}

object Day4 : Solution<Pair<Int, Int>>() {
  override val name = "day4"
  override val parser = Parser { input ->
    input.cut("-").map { it.toInt() }
  }

  fun fits(pwd: String, triplet: Boolean = false): Boolean {
    if ((0..4).none { pwd[it] == pwd[it + 1] }) {
      // no doubles, doesn't fit
      return false
    }

    if ((0..4).any { pwd[it] > pwd[it + 1] }) {
      // decreasing pair of digits
      return false
    }

    if (triplet) {
      val doubleChars = (0 .. 4).filter {
        pwd[it] == pwd[it + 1]
      }.map { pwd[it] }.toSet()

      if (doubleChars.all { c -> (0..3).any { pwd[it] == c && pwd[it + 1] == c && pwd[it + 2] == c } }) {
        return false
      }
    }

    return true
  }

  override fun part1(input: Pair<Int, Int>): Any? {
    return (input.first .. input.second).count { fits(it.toString()) }
  }

  override fun part2(input: Pair<Int, Int>): Any? {
    return (input.first .. input.second).count { fits(it.toString(), true) }
  }
}
