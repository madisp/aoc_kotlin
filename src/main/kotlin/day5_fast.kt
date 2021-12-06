import kotlin.math.abs
import kotlin.math.roundToInt

fun main() {
  Day5Fast.run()
}

object Day5All {
  @JvmStatic fun main(args: Array<String>) {
    mapOf("func" to Day5Func, "imp" to Day5Imp, "fast" to Day5Fast).forEach { (header, solution) ->
      solution.run(
        header = header,
        printParseTime = false,
        skipTest = true,
        skipPart1 = true
      )
    }
  }
}

object Day5Fast: Solution<List<Day5Fast.Line>> {
  override val name = "day5"
  override val parser = Parser.lines.mapItems(Line::fromString)

  override fun part1(input: List<Line>): Int {
    return solve(input.filter { it.isVertical || it.isHorizontal })
  }

  override fun part2(input: List<Line>): Int {
    return solve(input)
  }

  private fun solve(lines: List<Line>): Int {
    val intersections = Array(1000) { IntArray(1000) { 0 } }
    var count = 0

    lines.forEachIndexed { i, line ->
      for (j in (i + 1 until lines.size)) {
        val other = lines[j]

        if (other.start.x > line.end.x || other.end.x < line.start.x)
          continue
        if (minOf(other.start.y, other.end.y) > maxOf(line.start.y, line.end.y) ||
          maxOf(other.start.y, other.end.y) < minOf(line.start.y, line.end.y))
          continue

        if (line.isVertical) {
          count = verticalIntersections(other, line, intersections, count)
        } else if (other.isVertical) {
          count = verticalIntersections(line, other, intersections, count)
        } else if (line.slope != other.slope) {
          val y1 = line.start.y
          val y2 = other.start.y
          val x1 = line.start.x
          val x2 = other.start.x
          val s1 = line.slope
          val s2 = other.slope
          val fx = (y2 - y1 - x2 * s2 + x1 * s1) / (s1 - s2).toDouble()

          // bit of a hack, check whether the lines overlap when quantized
          if (abs(fx.roundToInt().toFloat() - fx) > 0.001) {
            continue
          }

          val x = fx.roundToInt()

          if (x in line.start.x .. line.end.x && x in other.start.x .. other.end.x) {
            val p = line.getPoint(x)
            if (intersections[p.x][p.y]++ == 0) {
              count++
            }
          }
        } else if (line.getPoint(0).y == other.getPoint(0).y) {
          val start = maxOf(line.start.x, other.start.x)
          val end = minOf(line.end.x, other.end.x)

          for (x in start .. end) {
            val p = line.getPoint(x)
            // if new intersection found, add to count
            if (intersections[p.x][p.y]++ == 0) {
              count++
            }
          }
        }
      }
    }

    return count
  }

  private fun verticalIntersections(
    other: Line,
    line: Line,
    intersections: Array<IntArray>,
    count: Int
  ): Int {
    var count1 = count
    if (!other.isVertical) {
      val p = other.getPoint(line.start.x)
      if (p.y in (minOf(line.start.y, line.end.y)..maxOf(line.start.y, line.end.y)) && p in other) {
        if (intersections[p.x][p.y]++ == 0) {
          count1++
        }
      }
    } else {
      if (line.start.x == other.start.x) {
        val x = line.start.x
        val l1sy = minOf(line.start.y, line.end.y)
        val l1ey = maxOf(line.start.y, line.end.y)
        val l2sy = minOf(other.start.y, other.end.y)
        val l2ey = maxOf(other.start.y, other.end.y)

        val start = maxOf(l1sy, l2sy)
        val end = minOf(l1ey, l2ey)

        for (y in start..end) {
          if (intersections[x][y]++ == 0) {
            count1++
          }
        }
      }
    }
    return count1
  }

  data class Point(val x: Int, val y: Int) {
    companion object {
      fun fromString(string: String): Point {
        val (x, y) = string.split(",", limit = 2).map { it.toInt() }
        return Point(x, y)
      }
    }
  }

  data class Line(val start: Point, val end: Point) {
    constructor(x1: Int, y1: Int, x2: Int, y2: Int) : this(
      if (x1 < x2) Point(x1, y1) else Point(x2, y2),
      if (x1 < x2) Point(x2, y2) else Point(x1, y1)
    )

    companion object {
      fun fromString(string: String): Line {
        val (start, end) = string.split(" -> ").map { Point.fromString(it) }
        return Line(start.x, start.y, end.x, end.y)
      }
    }

    val isVertical = start.x == end.x
    val isHorizontal = start.y == end.y

    val slope: Int get() = (end.y - start.y) / (end.x - start.x)

    fun getPoint(x: Int): Point {
      if (isVertical) {
        throw IllegalStateException("Can not get point by x for a vertical line")
      }

      val dx = (x - start.x)
      val y = start.y + dx * slope
      return Point(x, y)
    }

    operator fun contains(p: Point): Boolean {
      if (p.y !in (minOf(start.y, end.y) .. maxOf(start.y, end.y))) {
        return false
      }
      if (p.x !in (start.x .. end.x)) {
        return false
      }

      // vertical line
      if (end.x == start.x) {
        return true // p.x is always start.x due to the bounds check above
      } else {
        return getPoint(p.x) == p
      }
    }
  }
}
