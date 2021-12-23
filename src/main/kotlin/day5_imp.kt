import utils.Parser
import utils.Segment
import utils.Vec2i
import utils.mapItems

fun main() {
  Day5Imp.run()
}

object Day5Imp : Solution<List<Segment>> {
  override val name = "day5"
  override val parser = Parser.lines
    .mapItems(Segment::parse)
    .mapItems { if (it.start.x > it.end.x) Segment(it.end, it.start) else it }

  override fun part1(input: List<Segment>): Int {
    return solve(input.filter { it.isHorizontal || it.isVertical })
  }

  override fun part2(input: List<Segment>): Int {
    return solve(input)
  }

  private fun solve(lines: List<Segment>): Int {
    val intersections = mutableSetOf<Vec2i>()

    lines.forEachIndexed { i, line ->
      for (j in (i + 1 until lines.size)) {
        val other = lines[j]

        if (line.isVertical) {
          for (y in minOf(line.start.y, line.end.y) .. maxOf(line.start.y, line.end.y)) {
            val p = Vec2i(line.start.x, y)
            if (p in other) {
              intersections.add(p)
            }
          }
        } else {
          val start = maxOf(line.start.x, other.start.x)
          val end = minOf(line.end.x, other.end.x)

          for (x in start .. end) {
            val p = line[x]
            if (p in other) {
              intersections.add(p)
            }
          }
        }
      }
    }

    return intersections.count()
  }
}
