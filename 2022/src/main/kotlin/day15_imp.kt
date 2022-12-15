import utils.Parser
import utils.Segment
import utils.Solution
import utils.Vec2i
import utils.mapItems
import kotlin.math.abs

fun main() {
  Day15Imp.run()
}

object Day15Imp : Solution<List<Pair<Vec2i, Vec2i>>>() {
  override val name = "day15"
  override val parser: Parser<List<Pair<Vec2i, Vec2i>>> = Parser.lines.mapItems { line ->
    val (sp, bp) = line.split("Sensor at", ": closest beacon is at").map {
      it.trim()
        .replace("x=", "").replace("y=", "") }
        .filter { it.isNotBlank()
    }.map { Vec2i.parse(it) }

    sp to bp
  }

  private fun getExclusionSegmentAt(row: Int, sensor: Vec2i, beacon: Vec2i): Segment? {
    val yd = abs(sensor.y - row)
    val dist = sensor.manhattanDistanceTo(beacon)

    if (yd > dist) {
      // ignore
      return null
    }

    val left = Vec2i(sensor.x - (dist - yd), row)
    val right = Vec2i(sensor.x + (dist - yd), row)

    return Segment(left, right)
  }

  override fun part1(input: List<Pair<Vec2i, Vec2i>>): Int {
    // just so our test runs where there's a different row
    val row = if (input.size < 18) 10 else 2000000

    val sensors = input.map { it.first }.toSet()
    val beacons = input.map { it.second }.toSet()

    val segments = input.mapNotNull { getExclusionSegmentAt(row, it.first, it.second) }
    return ((segments.flatMap { it.points }.toSet() - sensors) - beacons).count()
  }

  override fun part2(input: List<Pair<Vec2i, Vec2i>>): Long {
    // just so our test runs where searchMax is smaller
    val searchMax = if (input.size < 18) 20 else 4000000

    val sensors = input.map { it.first }.toSet()
    val beacons = input.map { it.second }.toSet()

    for (y in 0 .. searchMax) {
      val segments = input.mapNotNull { getExclusionSegmentAt(y, it.first, it.second) }

      var x = 0
      val sorted = segments.filter { it.end.x >= 0 && it.start.x <= searchMax }.sortedBy { it.start.x }

      for (seg in sorted) {
        if (seg.start.x > x) {
          return x * 4000000L + y
        }
        x = maxOf(seg.end.x + 1, x)
        if (Vec2i(x, y) in sensors || Vec2i(x, y) in beacons) {
          x++
          continue
        }
      }
      if (x <= searchMax) {
        return x * 4000000L + y
      }
    }

    throw IllegalStateException("should not happen")
  }
}
