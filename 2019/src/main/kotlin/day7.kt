import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import utils.Parser
import utils.Solution
import utils.permutations
import java.util.concurrent.atomic.AtomicLong

fun main() {
  Day7.run()
}

object Day7 : Solution<LongArray>() {
  override val name = "day7"
  override val parser = Parser.longs.map { it.toLongArray() }

  override fun part1(input: LongArray): Long {
    return (0L..4L).toList().permutations.map {
      it.fold(0L) { acc, phase ->
        runBlocking {
          Computer().run {
            load(input)
            val chan = Channel<Long>()
            launch {
              chan.send(phase)
              chan.send(acc)
            }
            run(chan).first()
          }
        }
      }
    }.max()
  }

  override fun part2(input: LongArray): Long {
    return (5L .. 9L).toList().permutations.map { phases ->
      val cpus = phases.map { phase ->
        val chan = Channel<Long>(capacity = 5)
        runBlocking {
          chan.send(phase)
          val computer = Computer().apply {
            load(input)
          }
          val output = withContext(Dispatchers.Default) {
            computer.run(chan)
          }

          Triple(chan, computer, output)
        }
      }

      val out = AtomicLong(1)

      runBlocking {
        cpus[0].first.send(0)

        (0 until 4).forEach { i ->
          launch {
            cpus[i].third.collect {
              cpus[i + 1].first.send(it)
            }
          }
        }
        // fifth is special
        cpus[4].third.collect {
          out.set(it)
          cpus[0].first.send(it)
        }
      }

      return@map out.get()
    }.max()
  }
}
