package year2025

import utils.Grid
import utils.Memo
import utils.Parser
import utils.Solution
import utils.Vec2i

fun main() {
  Day7.run()
}

typealias Day7In = Grid<Char>

object Day7 : Solution<Day7In>() {
  override val name = "day7"
  override val parser: Parser<Day7In> = Parser.charGrid

  override fun part1(input: Day7In): Int {
    val start = input.coords.first { input[it] == 'S' }
    val queue = ArrayDeque(listOf(start))
    var splits = 0
    while (queue.isNotEmpty()) {
      val pos = queue.removeFirst()
      val next = pos + Vec2i.DOWN
      if (next !in input || next in queue) { continue }
      val nextPos = when(input[next]) {
        '^' -> listOf(next + Vec2i.LEFT, next + Vec2i.RIGHT)
        else -> listOf(next)
      }
      splits += nextPos.size - 1
      queue.addAll(nextPos)
    }
    return splits
  }

  val countTimelines = Memo<Vec2i, Long> { pos ->
    when {
      pos !in input -> 1L
      input[pos] == '^' -> this(pos + Vec2i.RIGHT) + this(pos + Vec2i.LEFT)
      else -> this(pos + Vec2i.DOWN)
    }
  }

  override fun part2(input: Day7In): Long {
    val start = input.coords.first { input[it] == 'S' }
    return countTimelines(start)
  }
}
