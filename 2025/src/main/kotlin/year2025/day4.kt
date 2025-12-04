package year2025

import utils.Grid
import utils.Parser
import utils.Solution
import utils.Vec2i
import utils.toMutable

fun main() {
  Day4.run()
}

typealias Day4In = Grid<Char>

private val Grid<Char>.removableRolls: List<Vec2i> get() {
  return coords.filter { p ->
    this[p] == '@' && this[p.surrounding].filter { it == '@' }.size < 4
  }
}

private val Grid<Char>.rollCount get() = values.count { it == '@' }

object Day4 : Solution<Day4In>() {
  override val name = "day4"
  override val parser: Parser<Day4In> = Parser.charGrid('.')

  override fun part1(input: Day4In): Int {
    return input.removableRolls.size
  }

  override fun part2(input: Day4In): Int {
    val g = input.toMutable()
    var toRemove = emptyList<Vec2i>()
    do {
      toRemove.forEach { g[it] = '.' }
      toRemove = g.removableRolls
    } while (toRemove.isNotEmpty())
    return input.rollCount - g.rollCount
  }
}
