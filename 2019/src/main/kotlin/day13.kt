import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import utils.Parser
import utils.Solution
import utils.Vec2i
import utils.createMutableGrid
import utils.debugString
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.atomic.AtomicLong

fun main() {
  Day13.run()
}

object Day13 : Solution<LongArray>() {
  override val name = "day13"
  override val parser = Parser.longs.map { it.toLongArray() }

  override fun part1(input: LongArray): Int = runBlocking {
    val blocks = mutableMapOf<Vec2i, Long>()
    run(input, blocks)
    return@runBlocking blocks.values.count { it == 2L }
  }

  override fun part2(input: LongArray): Long = runBlocking {
    // hack the quarters
    input[0] = 2L
    return@runBlocking runInteractive(input)
  }

  private suspend fun run(
    input: LongArray,
    blocks: MutableMap<Vec2i, Long>,
  ) {
    Computer().run {
      load(input)
      val chan = Channel<Long>(10)
      val flow = run(chan::receive)
      flow.chunked(3).collect { (x, y, block) ->
        blocks[Vec2i(x.toInt(), y.toInt())] = block
      }
    }
  }

  private suspend fun runInteractive(
    input: LongArray,
  ): Long {
    val screen = createMutableGrid(38, 21) { ' ' }
    var score = 0L
    var ballPos = Vec2i(0, 0)
    var paddlePos = Vec2i(0, 0)

    Computer().run {
      load(input)
      val joystick = AtomicLong(0L)
      val flow = run(input = { joystick.get() })
      flow.chunked(3).collect { (x, y, block) ->
        if (x == -1L && y == 0L) {
          score = block
        } else {
          val c = when (block) {
            1L -> '#'
            2L -> 'X'
            3L -> '-'.also { paddlePos = Vec2i(x.toInt(), y.toInt()) }
            4L -> 'O'.also { ballPos = Vec2i(x.toInt(), y.toInt()) }
            else -> ' '
          }
          screen[x.toInt()][y.toInt()] = c
        }

        if (paddlePos.x < ballPos.x) {
          joystick.set(1L)
        } else if (paddlePos.x > ballPos.x) {
          joystick.set(-1L)
        } else {
          joystick.set(0L)
        }
      }
    }
    return score
  }
}
