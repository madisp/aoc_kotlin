import utils.Segment
import kotlin.math.abs
import kotlin.math.roundToInt
import utils.Parser
import utils.mapItems

fun main() {
  Day5Fast.run()
}

object Day5All {
  @JvmStatic fun main(args: Array<String>) {
    mapOf("func" to Day5Func, "imp" to Day5Imp, "fast" to Day5Fast).forEach { (header, solution) ->
      solution.run(
        header = header,
        printParseTime = false
      )
    }
  }
}

object Day5Fast: Solution<List<Segment>> {
  override val name = "day5"
  override val parser = Parser.lines
    .mapItems(Segment::parse)
    .mapItems { if (it.start.x > it.end.x) Segment(it.end, it.start) else it }

  override fun part1(input: List<Segment>): Int {
    return solve(input.filter { it.isVertical || it.isHorizontal })
  }

  override fun part2(input: List<Segment>): Int {
    return solve(input)
  }

  private fun solve(segments: List<Segment>): Int {
    val intersections = Array(1000) { IntArray(1000) { 0 } }
    var count = 0

    segments.forEachIndexed { i, line ->
      for (j in (i + 1 until segments.size)) {
        val other = segments[j]

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
            val p = line[x]
            if (intersections[p.x][p.y]++ == 0) {
              count++
            }
          }
        } else if (line[0].y == other[0].y) {
          val start = maxOf(line.start.x, other.start.x)
          val end = minOf(line.end.x, other.end.x)

          for (x in start .. end) {
            val p = line[x]
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
    other: Segment,
    segment: Segment,
    intersections: Array<IntArray>,
    count: Int
  ): Int {
    var count1 = count
    if (!other.isVertical) {
      val p = other[segment.start.x]
      if (p.y in (minOf(segment.start.y, segment.end.y)..maxOf(segment.start.y, segment.end.y)) && p in other) {
        if (intersections[p.x][p.y]++ == 0) {
          count1++
        }
      }
    } else {
      if (segment.start.x == other.start.x) {
        val x = segment.start.x
        val l1sy = minOf(segment.start.y, segment.end.y)
        val l1ey = maxOf(segment.start.y, segment.end.y)
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
}
