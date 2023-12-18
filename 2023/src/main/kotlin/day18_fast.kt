import utils.Parser
import utils.Solution
import utils.Vec2l
import utils.mapItems
import kotlin.math.absoluteValue

fun main() {
  Day18Fast.run()
}

object Day18Fast : Solution<List<Day18.Line>>() {
  override val name = "day18"
  override val parser = Parser.lines.mapItems { Day18.parseLine(it) }

  private fun solve(insns: List<Day18.Instruction>): Long {
    val (area, pts, _) = insns.fold(Triple(0L, 0L, 0L)) { (area, pts, y), i ->
      Triple(area + i.direction.x * i.len * y, pts + i.len, y + i.direction.y * i.len)
    }
    return area.absoluteValue + (pts / 2) + 1
  }

  override fun part1(): Long {
    val insns = input.map { line ->
      val direction = when (line.direction) {
        "R" -> Vec2l.RIGHT
        "D" -> Vec2l.DOWN
        "U" -> Vec2l.UP
        "L" -> Vec2l.LEFT
        else -> throw IllegalStateException("Unknown direction char ${line.direction}")
      }
      Day18.Instruction(direction, line.len)
    }
    return solve(insns)
  }

  override fun part2(): Long {
    val directions = listOf(Vec2l.RIGHT, Vec2l.DOWN, Vec2l.LEFT, Vec2l.UP)
    val corrected = input.map { line ->
      val direction = directions[line.color.last() - '0']
      val len = line.color.substring(0, line.color.length - 1).toLong(16)
      Day18.Instruction(direction, len)
    }
    return solve(corrected)
  }
}
