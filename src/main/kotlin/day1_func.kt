import utils.Parser

fun main() {
  Day1Func.run()
}

object Day1Func : Solution<List<Int>> {
  override val name = "day1"
  override val parser = Parser.intLines

  override fun part1(input: List<Int>): Int {
    return input.windowed(2).count { (a, b) -> a < b }
  }

  override fun part2(input: List<Int>): Number? {
    return input.windowed(3).map { it.sum() }.windowed(2).count { (a, b) -> a < b }
  }
}
