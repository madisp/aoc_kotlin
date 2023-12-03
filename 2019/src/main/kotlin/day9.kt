import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import utils.Parser
import utils.Solution

fun main() {
  Day9.run()
}

object Day9 : Solution<LongArray>() {
  override val name = "day9"
  override val parser = Parser.longs.map { it.toLongArray() }

  override fun part1(input: LongArray): Long = runBlocking {
    Computer().run {
      load(input)
      val chan = Channel<Long>()
      launch { chan.send(1) }
      run(chan).last()
    }
  }

  override fun part2(input: LongArray): Long = runBlocking {
    Computer().run {
      load(input)
      val chan = Channel<Long>()
      launch { chan.send(2) }
      run(chan).last()
    }
  }
}
