import utils.*

fun main() {
  Day25.run()
}

typealias Day25In = List<Grid<Char>>

object Day25 : Solution<Day25In>() {
  override val name = "day25"
  override val parser: Parser<Day25In> = Parser { it.trim().split("\n\n") }
    .mapItems { Parser.charGrid(it) }

  override fun part1(input: Day25In): Any? {
    val (lockGrids, keyGrids) = input.partition { g -> g.getRow(0).values.all { it == '#' } }

    val locks = lockGrids.map { g -> g.columns.map { c -> c.values.count { it == '#' } - 1 } }
    val keys = keyGrids.map { g -> g.columns.map { c -> c.values.count { it == '#' } - 1 } }

    return (locks * keys).map { (lock, key) -> lock.zip(key).map { (a, b) -> a + b } }
      .count { sum -> sum.all { it <= 5 } }
  }
}
