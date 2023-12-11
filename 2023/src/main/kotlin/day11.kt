import utils.Grid
import utils.Parser
import utils.Solution
import utils.Vec2l
import utils.pairs

fun main() {
  Day11.run()
}

object Day11 : Solution<Grid<Char>>() {
  override val name = "day11"
  override val parser = Parser.charGrid

  private fun expand(coord: Vec2l, expCols: List<Int>, expRows: List<Int>, times: Long = 1): Vec2l {
    return coord + (Vec2l(
      x = expCols.count { it < coord.x }.toLong(),
      y = expRows.count { it < coord.y }.toLong(),
    ) * (times - 1))
  }

  private fun solve(expandFactor: Long = 2L): Long {
    val expCols = input.columns.filter { it.cells.all { (_, c) -> c == '.' } }.map { it.x }
    val expRows = input.rows.filter { it.cells.all { (_, c) -> c == '.' } }.map { it.y }

    val galaxies = input.cells.filter { (_, c) -> c == '#' }.map { it.first.toVec2l() }
    val galaxiesExpanded = galaxies.map { expand(it, expCols, expRows, expandFactor) }

    return galaxiesExpanded.pairs.sumOf { (a, b) -> a.manhattanDistanceTo(b) }
  }

  override fun part1() = solve()
  override fun part2() = solve(expandFactor = 1000000L)
}
