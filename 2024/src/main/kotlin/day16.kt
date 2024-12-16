import utils.Graph
import utils.Grid
import utils.Parser
import utils.Solution
import utils.Vec2i
import utils.withDefault

fun main() {
  Day16.run()
}

typealias Day16In = Grid<Char>

object Day16 : Solution<Day16In>() {
  override val name = "day16"
  override val parser: Parser<Day16In> = Parser.charGrid.map { it.withDefault('#') }

  data class State(
    val pos: Vec2i,
    val dir: Vec2i,
  )

  private fun buildGraph(): Graph<State, Int> = Graph(
    edgeFn = { state ->
      buildList {
        add(1000 to state.copy(dir = state.dir.rotateCcw()))
        add(1000 to state.copy(dir = state.dir.rotateCw()))
        if (input[state.pos + state.dir] in "SE.") {
          add(1 to state.copy(pos = state.pos + state.dir))
        }
      }
    },
    weightFn = { it }
  )

  private val startState get() = State(
    pos = input.coords.first { input[it] == 'S' },
    dir = Vec2i.RIGHT
  )

  private val endStates: List<State> get() {
    val endPos = input.coords.first { input[it] == 'E' }
    return endPos.adjacent.filter { input[it] in ".S" }.map { State(endPos, endPos - it) }
  }

  override fun part1(input: Day16In): Int {
    val g = buildGraph()
    return endStates.map { g.shortestPath(startState, it) }.minBy { it.first }.first
  }

  override fun part2(input: Day16In): Int {
    val g = buildGraph()
    val paths = endStates.map { g.shortestPaths(startState, it) }
    val minCost = paths.minOf { it.first }
    return paths.filter { it.first == minCost }
      .flatMap { it.third.flatMap { path -> path.map { (s, _) -> s.pos } } }
      .toSet()
      .size
  }
}
