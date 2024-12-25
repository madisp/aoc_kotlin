import utils.Grid
import utils.Parser
import utils.Solution
import utils.map
import utils.mapItems
import utils.times

fun main() {
  Day25.run()
}

typealias Day25In = List<Grid<Char>>

object Day25 : Solution<Day25In>() {
  override val name = "day25"
  override val parser: Parser<Day25In> = Parser { it.trim().split("\n\n") }.mapItems { Parser.charGrid(it) }

  private val Grid<Char>.pinHeights get() = columns.map { c -> c.values.count { it == '#' } - 1 }
  private val Grid<Char>.isLock get() = getRow(0).values.all { it == '#' }

  override fun part1(input: Day25In): Int {
    val (locks, keys) = input.partition { it.isLock }.map { list -> list.map { it.pinHeights } }
    return (locks * keys).map { (lock, key) -> lock.zip(key).map { (a, b) -> a + b } }
      .count { sum -> sum.all { it <= 5 } }
  }
}
