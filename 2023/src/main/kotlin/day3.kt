import utils.Grid
import utils.Parser
import utils.Solution
import utils.Vec2i

fun main() {
  Day3.run(skipTest = false)
}

object Day3 : Solution<Grid<Char>>() {
  override val name = "day3"
  override val parser = Parser.charGrid

  data class PartNum(
    val num: Int,
    val start: Vec2i,
    val end: Vec2i,
  ) {
    val points: List<Vec2i> get() {
      return (start.x..end.x).map { Vec2i(it, start.y) }
    }
  }

  private fun partNums(grid: Grid<Char>): Set<PartNum> {
    val nums = grid.cells.filter { (p, c) ->
      c.isDigit() && p.surrounding.any { it in grid.coords && grid[it].isSymbol() }
      }.map { (p, _) ->
      val start = (p.x downTo 0).takeWhile { x -> grid[x][p.y].isDigit() }.last()
      val end = (p.x until grid.width).takeWhile { x -> grid[x][p.y].isDigit() }.last()
      val chars = (start..end).map { x -> grid[x][p.y] }
      PartNum(
        chars.joinToString("").toInt(),
        Vec2i(start, p.y),
        Vec2i(end, p.y),
      )
      }.toSet()
    return nums
  }

  override fun part1(grid: Grid<Char>): Int {
    return partNums(grid).sumOf { it.num }
  }

  override fun part2(grid: Grid<Char>): Int {
    val partNums = partNums(grid)
    val gearRatios = grid.cells.mapNotNull { (p, c) ->
      if (c != '*') return@mapNotNull null

      val adjacentParts = partNums.filter { partNum -> p.surrounding.any { it in partNum.points } }

      if (adjacentParts.size != 2) return@mapNotNull null

      return@mapNotNull adjacentParts[0].num * adjacentParts[1].num
    }

    return gearRatios.sum()
  }
}

private fun Char.isSymbol(): Boolean {
  return !isDigit() && this != '.'
}
