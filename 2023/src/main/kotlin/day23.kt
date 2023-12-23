import utils.Grid
import utils.Parser
import utils.Solution
import utils.Vec2i
import java.util.ArrayDeque

fun main() {
  Day23.run()
}

object Day23 : Solution<Grid<Char>>() {
  override val name = "day23"
  override val parser = Parser.charGrid

  private val dir = mapOf(
    '>' to Vec2i(1, 0),
    '<' to Vec2i(-1, 0),
    '^' to Vec2i(0, -1),
    'v' to Vec2i(0, 1),
  )

  private fun maxDist1(start: Vec2i, end: Vec2i): Int {
    val queue = ArrayDeque(listOf(start to 0))
    val visited = mutableSetOf<Vec2i>()
    val opts = mutableListOf<Pair<Vec2i, Int>>()
    while (queue.isNotEmpty()) {
      val (p, dist) = queue.removeFirst()
      visited += p

      if (p == end) {
        return dist
      }

      val v = dir[input[p]]
      if (v != null) {
        opts += (p + v) to (dist + 1)
        continue // don't add adj from arrows
      }

      val adj = p.adjacent.filter { it in input && (input[it] == '.' || dir[input[it]] == (it + (p * -1))) }
        .filter { it !in visited }
        .map { it to dist + 1 }
      queue.addAll(adj)
    }

    return opts.maxOf { (p, d) -> d + maxDist1(p, end) }
  }

  private fun maxDist2(start: Vec2i, end: Vec2i, visitedForks: Set<Vec2i>): Int {
    val queue = ArrayDeque(listOf(start to 0))
    val visited = mutableSetOf<Vec2i>()
    val opts = mutableListOf<Triple<Vec2i, Int, Set<Vec2i>>>()
    while (queue.isNotEmpty()) {
      val (p, dist) = queue.removeFirst()
      visited += p

      if (p == end) {
        return dist
      }

      val adj = p.adjacent.filter { it in input && (input[it] != '#') }
        .filter { it !in visited }
        .filter { it !in visitedForks }
        .map { it to dist + 1 }

      // fork
      if (adj.size > 1) {
        adj.forEach { (branch, dist) ->
          opts.add(Triple(branch, dist, visitedForks + p))
        }
        break
      }

      queue.addAll(adj)
    }

    return opts.maxOfOrNull { (p, d, f) -> d + maxDist2(p, end, f) } ?: Integer.MIN_VALUE
  }

  override fun part1(): Int {
    val start = input.getRow(0).cells.first { (p, c) -> c == '.' }.first
    val end = input.getRow(input.height - 1).cells.first { (p, c) -> c == '.' }.first
    return maxDist1(start, end)
  }

  // runs in ~30 minutes
  override fun part2(): Int {
    val start = input.getRow(0).cells.first { (p, c) -> c == '.' }.first
    val end = input.getRow(input.height - 1).cells.first { (p, c) -> c == '.' }.first
    return maxDist2(start, end, emptySet())
  }
}
