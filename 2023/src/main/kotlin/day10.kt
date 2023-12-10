import utils.Grid
import utils.MutableIntGrid
import utils.Parser
import utils.Solution
import utils.Vec2i
import utils.borderWith
import utils.expand
import utils.map
import utils.toMutable

fun main() {
  Day10.run()
}

private fun guessStart(g: Grid<Char>, s: Vec2i): List<Char> {
  val up = s.y > 2 && g[s.x][s.y - 2] == '|'
  val down = s.y < g.height - 2 && g[s.x][s.y + 2] == '|'
  val left = s.x > 2 && g[s.x - 2][s.y] == '-'
  val right = s.x < g.width - 2 && g[s.x + 2][s.y] == '-'
  return buildList {
    if (up && down) add('|')
    if (left && right) add('-')
    if (up && right) add('L')
    if (up && left) add('J')
    if (left && down) add('7')
    if (right && down) add('F')
  }
}

private val EXPAND = mapOf(
  '|' to Parser.charGrid(".|.\n.|.\n.|."),
  '-' to Parser.charGrid("...\n---\n..."),
  'L' to Parser.charGrid(".|.\n.L-\n..."),
  'J' to Parser.charGrid(".|.\n-J.\n..."),
  '7' to Parser.charGrid("...\n-7.\n.|."),
  'F' to Parser.charGrid("...\n.F-\n.|."),
  '.' to Parser.charGrid("...\n...\n..."),
  'S' to Parser.charGrid("SSS\nSSS\nSSS"),
)

object Day10 : Solution<Pair<Vec2i, Grid<Char>>>() {
  override val name = "day10"
  override val parser: Parser<Pair<Vec2i, Grid<Char>>> = Parser.charGrid
    .map { g -> g.expand(3) { _, c -> EXPAND[c]!! } }
    .map { g ->
      val startLocation = (0 until g.width / 3).asSequence().flatMap { x ->
        (0 until g.height / 3).asSequence().map { y ->
          Vec2i(x, y)
        }
      }.map { (it * 3) + 1 }.first { g[it] == 'S' }

      val startOpts = guessStart(g, startLocation)
      require(startOpts.size == 1) {
        //TODO(madis) what if this doesn't hold true?
        "Grid has more than 1 start options: $startOpts"
      }
      val ret = g.toMutable()

      val start = EXPAND[startOpts.first()]!!

      start.cells.forEach { (p, c) ->
        ret[p + startLocation + Vec2i(-1, -1)] = c
      }

      startLocation to ret
    }

  private val NEXT = mapOf(
    '|' to listOf(Vec2i.UP, Vec2i.DOWN),
    '-' to listOf(Vec2i.LEFT, Vec2i.RIGHT),
    'L' to listOf(Vec2i.UP, Vec2i.RIGHT),
    'J' to listOf(Vec2i.UP, Vec2i.LEFT),
    '7' to listOf(Vec2i.LEFT, Vec2i.DOWN),
    'F' to listOf(Vec2i.DOWN, Vec2i.RIGHT),
  )

  data class QueueItem(
    val p: Vec2i,
    val c: Char,
    val s: Int
  )

  private fun computeVisited(): MutableIntGrid {
    val (start, g) = input

    val visited = MutableIntGrid(IntArray(g.width * g.height / 9) { -1 }, g.width / 3, g.height / 3)

    val q = ArrayDeque(listOf(QueueItem(start, g[start], 0)))

    while (q.isNotEmpty()) {
      val p = q.removeFirst()
      visited[p.p / 3] = p.s

      NEXT[p.c]?.map { p.p + it * 3 }
        ?.filter { it.x >= 0 && it.y >= 0 && it.x < g.width && it.y < g.height }
        ?.filter { visited[it / 3] == -1 }
        ?.forEach {
          q.add(QueueItem(it, g[it], p.s + 1))
        }
    }

    return visited
  }

  override fun part1(input: Pair<Vec2i, Grid<Char>>): Int {
    return computeVisited().values.max()
  }

  override fun part2(input: Pair<Vec2i, Grid<Char>>): Int {
    val visited = computeVisited().borderWith(-1)
    val expandedInput = input.second.borderWith('.', 3).map(Grid.OobBehaviour.Throw()) { p, c ->
      if (visited[p / 3] == -1) '.' else c
    }.toMutable()

    val start = Vec2i(0, 0)
    val q = ArrayDeque(listOf(start))
    while (q.isNotEmpty()) {
      val p = q.removeLast()
      expandedInput[p] = 'x'
      p.adjacent
        .filter { it.x >= 0 && it.y >= 0 && it.x < expandedInput.width && it.y < expandedInput.height }
        .filter { expandedInput[it] == '.' }
        .forEach { q.add(it) }
    }

    var count = 0
    (0 until expandedInput.width / 3).forEach { x ->
      (0 until expandedInput.height / 3).forEach { y ->
        val expandedCoords = (3 * x until 3 * x + 3).flatMap { ex ->
          (3 * y until 3 * y + 3).map { ey ->
            Vec2i(ex, ey)
          }
        }
        if (expandedCoords.all { expandedInput[it] == '.' }) {
          count++
        }
      }
    }

    return count
  }
}
