import kotlinx.coroutines.runBlocking
import utils.Parser
import utils.Solution

fun main() {
  Day2.run()
}

object Day2 : Solution<LongArray>() {
  override val name = "day2"
  override val parser: Parser<LongArray> = Parser.longs.map { it.toLongArray() }

  override fun part1(input: LongArray) = runBlocking {
    val cpu = Computer()
    cpu.load(input)
    cpu.memory[1] = 12
    cpu.memory[2] = 2
    cpu.run()

    return@runBlocking cpu.memory[0]
  }

  override fun part2(input: LongArray): Long = runBlocking {
    val cpu = Computer()

    val search = 100L
    (0L until search).forEach { x ->
      (0L until search).forEach { y ->
        cpu.reboot()
        cpu.load(input)
        cpu.memory[1] = x
        cpu.memory[2] = y
        cpu.run()
        if (cpu.memory[0] == 19690720L) {
          return@runBlocking x * 100 + y
        }
      }
    }

    throw IllegalArgumentException("No input satisfies condition (== 19690720)")
  }
}
