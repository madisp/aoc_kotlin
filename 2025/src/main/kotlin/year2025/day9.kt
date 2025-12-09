package year2025

import utils.Parser
import utils.SegmentD
import utils.Solution
import utils.Vec2d
import utils.Vec2l
import utils.cut
import utils.mapItems
import utils.times
import kotlin.math.absoluteValue

fun main() {
  Day9.run(skipPuzzle = false)
}

typealias Day9In = List<Vec2l>

object Day9 : Solution<Day9In>() {
  override val name = "day9"
  override val parser: Parser<Day9In> = Parser.lines.mapItems {
    val (x, y) = it.cut(",")
    Vec2l(x.toLong(), y.toLong())
  }

  override fun part1(input: Day9In): Long {
    return (input * input).maxOf { (a, b) ->
      val d = a - b
      (d.x.absoluteValue + 1) * (d.y.absoluteValue + 1)
    }
  }

  // build a point-polygon out of input "square"-polygon
  private fun pts(input: Day9In): List<Vec2d> {
    // reorder the list to the leftmost-topmost point
    val tl = input.min()
    val idx = input.indexOf(tl)
    val reordered = input.subList(idx, input.size) + input.subList(0, idx)

    var state = Vec2l(0, 0)
    return (reordered + reordered[0] + reordered[1]).windowed(3).map { (p, n, nn) ->
      val outp = (p + state)
      // handle outer right turns
      val x = when {
        state.x == 0L && n.x > p.x && nn.y > n.y -> 1L
        state.x == 1L && n.x < p.x && nn.y < n.y -> 0L
        else -> state.x
      }
      val y = when {
        state.y == 0L && n.y > p.y && nn.x < n.x -> 1L
        state.y == 1L && n.y < p.y && nn.x > n.x -> 0L
        else -> state.y
      }

      state = Vec2l(x, y)
      Vec2d(outp.x.toDouble(), outp.y.toDouble())
    }
  }

  private fun List<Vec2d>.toPolygon(): List<SegmentD> {
    return (this + this.first()).windowed(2).map { (a, b) -> SegmentD(a, b) }
  }

  private operator fun List<SegmentD>.contains(p: Vec2d): Boolean {
    // cast a ray from -1, p.y to p, count intersections. If odd, then it's in the polygon.
    val ray = SegmentD(Vec2d(-1.0, p.y), p)
    return count { ray intersects it } % 2 == 1
  }

  private infix fun SegmentD.intersects(polygon: List<SegmentD>): Boolean {
    return polygon.any { it intersects this }
  }

  private infix fun SegmentD.intersects(other: SegmentD): Boolean {
    if (this.isVertical) {
      if (other.isVertical) return this.start.x == other.start.x
      val p = other[this.start.x]
      return p in this && p in other
    }
    if (other.isVertical) {
      val p = this[other.start.x]
      return p in this && p in other
    }
    // no need to handle this case, all inputs are perpendicular
    require(this.isHorizontal && other.isHorizontal) { "sloped segment $this or $other?" }
    return false
  }

  override fun part2(input: Day9In): Long {
    val pts = pts(input)
    val polygon = pts.toPolygon()
    return (input * input).filter { (a, b) -> a < b }.maxOf { (a, b) ->
      val d = a - b

      val minx = minOf(a.x, b.x).toDouble()
      val maxx = maxOf(a.x, b.x).toDouble()
      val miny = minOf(a.y, b.y).toDouble()
      val maxy = maxOf(a.y, b.y).toDouble()

      val corners = listOf(
        Vec2d(minx, miny), // tl
        Vec2d(maxx, miny), // tr
        Vec2d(maxx, maxy), // br
        Vec2d(minx, maxy), // bl
      ).map { it + Vec2d(0.5, 0.5) } // shift into the center a bit

      val edges = corners.toPolygon()

      // rect is valid if all corners are in the polygon and the edges don't intersect with the polygon
      val valid = corners.all { it in polygon } && edges.none { it intersects polygon }

      if (!valid) {
        0L
      } else {
        (d.x.absoluteValue + 1) * (d.y.absoluteValue + 1)
      }
    }
  }
}
