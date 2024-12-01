import utils.Graph
import utils.Parser
import utils.Solution
import utils.Vec2i
import utils.badInput
import utils.mapParser

fun main() {
  Day24.run()
}

object Day24 : Solution<Pair<Day24.Context, List<Day24.Blizzard>>>() {
  override val name = "day24"
  override val parser = Parser { it.trim() }.mapParser(Parser.charGrid).map { grid ->
    val ctx = Context(Vec2i(0, -1), Vec2i(grid.width - 3, grid.height - 2), grid.width - 2, grid.height - 2)
    val blizzards = grid.cells.filter { (p, char) ->
      char in setOf('<', '>', '^', 'v') && p.x in 1 until grid.width - 1 && p.y in 1 until grid.height
    }.map { (pos, char) ->
      Blizzard(pos + Vec2i(-1, -1), when (char) {
        '^' -> Direction.UP
        'v' -> Direction.DOWN
        '<' -> Direction.LEFT
        '>' -> Direction.RIGHT
        else -> badInput()
      })
    }

    ctx to blizzards
  }

  enum class Direction(val v: Vec2i) {
    UP(Vec2i(0, -1)),
    DOWN(Vec2i(0, 1)),
    LEFT(Vec2i(-1, 0)),
    RIGHT(Vec2i(1, 0)),
    STAY(Vec2i(0, 0)),
  }

  data class Blizzard(val pos: Vec2i, val dir: Direction) {
    fun move(bounds: Vec2i) = Blizzard((pos + dir.v + bounds) % bounds, dir)
  }

  data class State(val pos: Vec2i, val blizzards: List<Blizzard>)
  data class Context(val startPos: Vec2i, val endPos: Vec2i, val width: Int, val height: Int)

  override fun part1(input: Pair<Context, List<Blizzard>>): Int {
    val ctx = input.first
    val graph = makeGraph(ctx)
    val initialState = State(ctx.startPos, input.second)

    return graph.shortestPath(initialState) { it.pos == ctx.endPos }.first
  }

  override fun part2(input: Pair<Context, List<Blizzard>>): Int {
    val ctx = input.first
    val graph = makeGraph(ctx)
    val initialState = State(ctx.startPos, input.second)

    val pt1 = graph.shortestPath(initialState) { it.pos == ctx.endPos }
    val pt2 = graph.shortestPath(pt1.second.first().first) { it.pos == ctx.startPos }
    val pt3 = graph.shortestPath(pt2.second.first().first) { it.pos == ctx.endPos }

    return pt1.first + pt2.first + pt3.first
  }

  private fun makeGraph(ctx: Context): Graph<State, Unit> {
    val bounds = Vec2i(ctx.width, ctx.height)
    val graph = Graph<State, Unit>(
      edgeFn = { state ->
        // move blizzards by 1
        val newBlizzards = state.blizzards.map { b -> b.move(bounds) }
        val blizzCoords = newBlizzards.map { it.pos }.toSet()

        // try out all possible moves
        Direction.values().map { state.pos + it.v }
          .filter { pos ->
            // must be in bounds or start/end
            pos == ctx.startPos || pos == ctx.endPos || (pos.x in 0 until ctx.width && pos.y in 0 until ctx.height)
          }
          .filter { pos ->
            // cannot be occupied by blizzard
            pos !in blizzCoords
          }
          .map { pos ->
            Unit to State(pos, newBlizzards)
          }
      }
    )
    return graph
  }
}
