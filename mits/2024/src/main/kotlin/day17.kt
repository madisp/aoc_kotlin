import utils.*

fun main() {
  Day17.run()
}

typealias Day17In = Grid<Char>

object Day17 : Solution<Day17In>() {
  override val name = "day17"
  override val parser: Parser<Day17In> = Parser.charGrid.map { it.withDefault('#') }

  override fun part1(input: Day17In): String {
    val g = Graph<Vec2i, Unit>(
      edgeFn = { p ->
        p.adjacent.filter { input[it] != '#' }.map { Unit to it }
      }
    )

    val start = input.coords.first { input[it] == 'L' }
    val end = input.coords.first { input[it] == 'K' }
    val path = g.shortestPath(start, end).second.map { it.first }

    var curPos = end
    var curDir = path[1] - curPos

    fun turn(dir: Vec2i, dir2: Vec2i): Char {
      if (dir.rotateCcw() == dir2) return 'P'
      if (dir.rotateCw() == dir2) return 'V'
      throw IllegalStateException("Invalid turn $dir $dir2")
    }

    return buildString {
      path.drop(1).forEach { p ->
        val dir = p - curPos
        if (dir != curDir) {
          append(turn(dir, curDir))
          curDir = dir
        }
        curPos = p
      }
    }.reversed()
  }
}
