import utils.Parser
import utils.Segment
import utils.Vec2i
import utils.mapItems

fun main() {
  Day5Func.run()
}

object Day5Func : Solution<List<Segment>> {
  override val name = "day5"
  override val parser = Parser.lines
    .mapItems(Segment::parse)
    .mapItems { if (it.start.x > it.end.x) Segment(it.end, it.start) else it }

  override fun part1(input: List<Segment>): Int? {
    return solve(input.filter { it.isVertical || it.isHorizontal })
  }

  override fun part2(input: List<Segment>): Int? {
    return solve(input)
  }

  private fun solve(lines: List<Segment>): Int {
    return lines
      .flatMapIndexed { index, line -> lines.drop(index + 1).map { line to it } }
      .flatMap { (line1, line2) -> line1 intersect line2 }
      .toSet()
      .count()
  }

  private infix fun Segment.intersect(other: Segment): Collection<Vec2i> {
    val startx = maxOf(this.start.x, other.start.x)
    val endx = minOf(this.end.x, other.end.x)

    val pts = if (isVertical) {
      (minOf(start.y, end.y) .. maxOf(start.y, end.y)).map {
        Vec2i(start.x, it)
      }
    } else {
      (startx .. endx).map { this[it] }
    }

    return pts.filter { it in other }
  }
}
