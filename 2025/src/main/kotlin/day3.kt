import utils.IntGrid
import utils.Parser
import utils.Solution

fun main() {
  Day3.run()
}

typealias Day3In = IntGrid

object Day3 : Solution<Day3In>() {
  override val name = "day3"
  override val parser: Parser<Day3In> = Parser.digitGrid

  fun joltage(batteries: List<Int>, len: Int, acc: Long = 0): Long {
    if (len == 0) return acc
    val idx = (0 .. batteries.size - len).maxBy { batteries[it] }
    return joltage(batteries.drop(idx + 1), len - 1, acc * 10 + batteries[idx])
  }

  override fun part1(input: Day3In): Long {
    return input.rows.sumOf { row -> joltage(row.values, 2) }
  }

  override fun part2(input: Day3In): Long {
    return input.rows.sumOf { row -> joltage(row.values, 12) }
  }
}
