fun main() {
  Day5Imp.run()
}

object Day5Imp : Solution<List<Day5Imp.Line>> {
  override val name = "day5"
  override val parser = Parser.lines.mapItems(Line::fromString)

  override fun part1(input: List<Line>): Int {
    return solve(input.filter { it.isHorizontal || it.isVertical })
  }

  override fun part2(input: List<Line>): Int {
    return solve(input)
  }

  private fun solve(lines: List<Line>): Int {
    val intersections = mutableSetOf<Point>()

    lines.forEachIndexed { i, line ->
      for (j in (i + 1 until lines.size)) {
        val other = lines[j]

        if (line.isVertical) {
          for (y in minOf(line.start.y, line.end.y) .. maxOf(line.start.y, line.end.y)) {
            val p = Point(line.start.x, y)
            if (p in other) {
              intersections.add(p)
            }
          }
        } else {
          val start = maxOf(line.start.x, other.start.x)
          val end = minOf(line.end.x, other.end.x)

          for (x in start .. end) {
            val p = line.getPoint(x)
            if (p in other) {
              intersections.add(p)
            }
          }
        }
      }
    }

    return intersections.count()
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

    private val slope: Int get() = (end.y - start.y) / (end.x - start.x)

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
