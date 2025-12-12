package year2025

import utils.Grid
import utils.Parser
import utils.Solution
import utils.cut
import utils.map

fun main() {
  Day12.run()
}

data class Day12In(
  val shapes: List<Grid<Char>>,
  val regions: List<Region>,
)

data class Region(
  val w: Int,
  val h: Int,
  val presentShapes: List<Int>,
)

object Day12 : Solution<Day12In>() {
  override val name = "day12"
  override val parser: Parser<Day12In> = Parser { input ->
    val parts = input.trim().split("\n\n")
    val (shapeParts, regionParts) = parts.partition {
      it.lineSequence().first().endsWith(":")
    }

    require(regionParts.size == 1)

    Day12In(
      shapes = shapeParts.map { Parser.charGrid(it.lines().drop(1).joinToString("\n")) },
      regions = regionParts.first().lines().map { line ->
        val (dimens, presents) = line.cut(":")
        val (w, h) = dimens.cut("x").map { it.toInt() }
        Region(w, h, presents.split(" ").map { it.toInt() })
      },
    )
  }

  override fun part1(): Int {
    val area = input.shapes.map { shape -> shape.values.count { it == '#' } }
    return input.regions.count {
      (it.w * it.h) > it.presentShapes.withIndex().sumOf { (shape, count) -> count * area[shape] }
    }
  }
}
