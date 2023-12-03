import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import utils.Grid
import utils.Parser
import utils.Solution
import utils.Vec2i
import utils.createGrid
import utils.debugString

fun main() {
  Day11.run()
}

object Day11 : Solution<LongArray>() {
  override val name = "day11"
  override val parser = Parser.longs.map { it.toLongArray() }

  override fun part1(input: LongArray): Int = runBlocking {
    val colors = mutableMapOf<Vec2i, Long>()
    val painted = mutableSetOf<Vec2i>()

    run(input, colors, painted)

    return@runBlocking painted.size
  }

  override fun part2(input: LongArray): String = runBlocking {
    val colors = mutableMapOf<Vec2i, Long>()
    val painted = mutableSetOf<Vec2i>()

    // paint starting color white
    colors[Vec2i(0, 0)] = 1

    run(input, colors, painted)

    val minX = painted.minOf { it.x }
    val maxX = painted.maxOf { it.x }

    val minY = painted.minOf { it.y }
    val maxY = painted.maxOf { it.y }

    // from grid-coords to color-coords
    val transform = Vec2i(minX, minY)

    val grid = createGrid(maxX - minX + 1, maxY - minY + 1) { p ->
      if (colors[p + transform] == 1L) { '#' } else { ' ' }
    }

    // flip y axis
    val mirrored = createGrid(grid.width, grid.height) { p ->
      grid[p.x][grid.height - p.y - 1]
    }

    return@runBlocking mirrored.debugString
  }

  private suspend fun run(
    input: LongArray,
    colors: MutableMap<Vec2i, Long>,
    painted: MutableSet<Vec2i>,
  ) {
    var orientation = Vec2i(0, 1)
    var location = Vec2i(0, 0)
    Computer().run {
      load(input)
      val chan = Channel<Long>(10)
      chan.send(colors[location] ?: 0L)
      val flow = run(chan::receive)
      flow.chunked(2).collect { (color, direction) ->
        // paint
        colors[location] = color
        painted += location
        // turn
        orientation = if (direction == 1L) orientation.rotateCw() else orientation.rotateCcw()
        // move
        location += orientation
        // send color
        chan.send(colors[location] ?: 0L)
      }
    }
  }
}
fun <T> Flow<T>.chunked(chunkSize: Int): Flow<List<T>> {
  val buffer = mutableListOf<T>()
  return flow {
    this@chunked.collect {
      buffer.add(it)
      if (buffer.size == chunkSize) {
        emit(buffer.toList())
        buffer.clear()
      }
    }
    if (buffer.isNotEmpty()) {
      emit(buffer.toList())
    }
  }
}
