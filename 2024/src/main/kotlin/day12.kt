import utils.Grid
import utils.Parser
import utils.Solution
import utils.Vec2i
import utils.withDefault

fun main() {
  Day12.run()
}

typealias Day12In = Grid<Char>

object Day12 : Solution<Day12In>() {
  override val name = "day12"
  override val parser: Parser<Day12In> = Parser.charGrid.map { it.withDefault('.') }

  data class Region(
    val area: Int,
    val perimeter: Int = 0,
    val bulkFences: Int = 0,
  )

  private fun getRegions(input: Grid<Char>): List<Region> {
    val unvisited = mutableSetOf<Vec2i>()
    unvisited.addAll(input.coords)
    val regions = mutableListOf<Region>()

    // keep fences buffer out of loop for perf
    val fences = mutableSetOf<Pair<Vec2i, Vec2i>>()

    while (unvisited.isNotEmpty()) {
      val queue = ArrayDeque<Vec2i>()
      val start = unvisited.first()
      val char = input[start]

      var area = 0
      var perimeter = 0
      var bulkFences = 0

      queue.add(start)
      unvisited.remove(start)
      while (queue.isNotEmpty()) {
        val p = queue.removeFirst()
        area++

        p.adjacent.forEach { f ->
          if (input[f] != char) {
            fences += (p - f) to f
            perimeter++
          }
        }

        val next = p.adjacent.filter { input[it] == char && it in unvisited }
        next.forEach { unvisited.remove(it) }
        queue.addAll(next)
      }

      // collapse the perimeter to fences
      while (fences.isNotEmpty()) {
        val side = fences.first()
        fences.remove(side)
        val (dir, p) = side
        bulkFences++

        val rots = listOf(dir.rotateCw(), dir.rotateCcw())
        rots.forEach { rot ->
          var rotLen = 1
          while (dir to (p + (rot * rotLen)) in fences) {
            fences.remove(dir to (p + (rot * rotLen)))
            rotLen++
          }
        }
      }

      regions.add(Region(area, perimeter, bulkFences))
    }

    return regions
  }

  override fun part1(input: Day12In): Int {
    return getRegions(input).sumOf { it.area * it.perimeter }
  }

  override fun part2(input: Day12In): Int {
    return getRegions(input).sumOf { it.area * it.bulkFences }
  }
}
