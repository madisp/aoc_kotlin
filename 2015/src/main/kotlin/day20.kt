import utils.Parser
import utils.Solution
import utils.factors

fun main() {
  Day20.run()
}

object Day20 : Solution<Int>() {
  override val name = "day20"
  override val parser = Parser { it.trim().toInt() }

  override fun part1(): Int {
    val street = IntArray(input / 10)
    for (elf in 1 until street.size) {
      for (house in elf until street.size step elf) {
        street[house] += elf * 10
      }
    }
    return street.indexOfFirst { it >= input }
  }

  private fun p2score(house: Int): Int {
    val factors = (1 .. 50).filter { house % it == 0 }
    return factors.sumOf { house / it * 11 }
  }

  override fun part2(): Int {
    var mul = 1
    while (p2score(mul * 50) < input) {
      mul++
    }
    return mul * 50
  }
}
