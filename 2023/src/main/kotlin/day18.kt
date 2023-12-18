import utils.Parse
import utils.Parser
import utils.SegmentL
import utils.Solution
import utils.Vec2l
import utils.mapItems

fun main() {
  Day18.run()
}

object Day18 : Solution<List<Day18.Line>>() {
  override val name = "day18"
  override val parser = Parser.lines.mapItems { parseLine(it) }

  @Parse("{direction} {len} (#{color})")
  data class Line(
    val direction: String,
    val len: Long,
    val color: String,
  )

  data class Instruction(
    val direction: Vec2l,
    val len: Long
  )

  private fun solve(insns: List<Instruction>): Long {
    var location = Vec2l(0, 0)
    var min = location
    var max = location

    val segments = mutableListOf<SegmentL>()
    val areas = mutableListOf<Long>()

    insns.forEach { insn ->
      segments.add(SegmentL(location, location + (insn.direction * insn.len)))

      location += (insn.direction * insn.len)

      min = Vec2l(minOf(min.x, location.x), minOf(min.y, location.y))
      max = Vec2l(maxOf(max.x, location.x), maxOf(max.y, location.y))

      if (insn.direction.x != 0L) {
        areas.add(location.y)
      }
    }

    val edges = segments.map { if (it.start.y > it.end.y) SegmentL(it.end, it.start) else it }
    val sortedAreas = areas.toSet().sorted()
    val verts = edges.filter { it.isVertical }
    val hors = edges.filter { it.isHorizontal }.map { if (it.start.x > it.end.x) SegmentL(it.end, it.start) else it }

    var sz = 0L

    sortedAreas.windowed(2).forEach { (top, bottom) ->
      sz += countFilled(verts, hors, top)
      if (bottom > top + 1) {
        sz += countFilled(verts, hors, top + 1) * (bottom - top - 1)
      }
    }

    // add last bottom to the size
    sz += countFilled(verts, hors, sortedAreas.last())

    return sz
  }

  private fun countFilled(verts: List<SegmentL>, hors: List<SegmentL>, y: Long): Long {
    var sz = 0L
    val topSq = verts.filter { y in minOf(it.start.y, it.end.y) .. maxOf(it.start.y, it.end.y) }.sortedBy { it.start.x }
    val horiz = hors.filter { it.start.y == y }.toSet()
    var fill = true

    for (i in 0 until topSq.size - 1) {
      val s1 = topSq[i]
      val s2 = topSq[i + 1]
      if (SegmentL(Vec2l(s1.start.x, y), Vec2l(s2.start.x, y)) in horiz) {
        // always add segments on this line
        sz += (s2.start.x - s1.start.x)

        val s1up = minOf(s1.start.y, s1.end.y) < y
        val s2up = minOf(s2.start.y, s2.end.y) < y
        if (s1up != s2up) {
          // twist only, it doesn't change current fill state
          fill = !fill
        }
      } else {
        if (fill) {
          sz += (s2.start.x - s1.start.x)
        } else sz += 1
      }
      fill = !fill
    }

    sz += 1 // add the final right edge

    return sz
  }

  override fun part1(input: List<Line>): Long {
    val insns = input.map { line ->
      val direction = when (line.direction) {
        "R" -> Vec2l.RIGHT
        "D" -> Vec2l.DOWN
        "U" -> Vec2l.UP
        "L" -> Vec2l.LEFT
        else -> throw IllegalStateException("Unknown direction char ${line.direction}")
      }
      Instruction(direction, line.len)
    }
    return solve(insns)
  }

  override fun part2(): Long {
    val directions = listOf(Vec2l.RIGHT, Vec2l.DOWN, Vec2l.LEFT, Vec2l.UP)
    val corrected = input.map { line ->
      val direction = directions[line.color.last() - '0']
      val len = line.color.substring(0, line.color.length - 1).toLong(16)
      Instruction(direction, len)
    }
    return solve(corrected)
  }
}
