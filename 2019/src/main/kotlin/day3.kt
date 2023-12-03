import utils.Parser
import utils.Segment
import utils.Solution
import utils.Vec2i
import utils.mapItems
import utils.times

private typealias D3Input = Pair<List<Vec2i>, List<Vec2i>>

fun main() {
  Day3.run(skipTest = false)
}

object Day3 : Solution<D3Input>() {
  override val name = "day3"
  override val parser = Parser.lines.mapItems { line ->
    line.split(",").map {
      val direction = it.first()
      val distance = it.substring(1).toInt()
      when (direction) {
        'U' -> Vec2i(0, distance)
        'D' -> Vec2i(0, -distance)
        'L' -> Vec2i(-distance, 0)
        'R' -> Vec2i(distance, 0)
        else -> throw IllegalArgumentException("Unknown direction $direction")
      }
    }
  }.map { it[0] to it[1] }

  private fun intersectionsWithSteps(input: D3Input): List<Pair<Vec2i, Int>> {
    val s1 = mutableListOf<Pair<Segment, Int>>()
    val s2 = mutableListOf<Pair<Segment, Int>>()

    var origin = Vec2i(0, 0)
    var steps = 0
    input.first.forEach {
      s1 += Segment(origin, origin + it) to steps
      origin += it
      steps += it.manhattanDistanceTo(Vec2i(0, 0))
    }

    origin = Vec2i(0, 0)
    steps = 0
    input.second.forEach {
      s2 += Segment(origin, origin + it) to steps
      origin += it
      steps += it.manhattanDistanceTo(Vec2i(0, 0))
    }

    return (s1 * s2).mapNotNull { (a, b) ->
        val p = (a.first.points.toSet() intersect b.first.points.toSet()).firstOrNull()

        if (p == null) { null } else {
          p to a.second + (p.manhattanDistanceTo(a.first.start)) + b.second + (p.manhattanDistanceTo(b.first.start))
        }
      }
      .filter { it.first != Vec2i(0, 0) }
  }

  override fun part1(input: D3Input): Int {
    return intersectionsWithSteps(input)
      .minOf { (pt, _) -> pt.manhattanDistanceTo(Vec2i(0, 0)) }
  }

  override fun part2(input: D3Input): Int {
    return intersectionsWithSteps(input)
      .minOf { (_, steps) -> steps }
  }
}
