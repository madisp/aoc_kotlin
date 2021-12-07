import kotlin.math.abs

fun main() {
  Day7Func.run()
}

object Day7Func : Solution<List<Int>> {
  override val name = "day7"
  override val parser = Parser.ints

  override fun part1(input: List<Int>): Int {
    return solve(input, ::identity)
  }

  override fun part2(input: List<Int>): Int {
    return solve(input, ::cost)
  }

  private fun solve(input: List<Int>, costFn: (Int) -> Int): Int {
    return (0..input.maxOrNull()!!)
      .minOf { target -> input.sumOf { costFn(abs(it - target)) } }
  }

  private fun identity(distance: Int) = distance
  private fun cost(distance: Int) = distance * (distance + 1) / 2
}