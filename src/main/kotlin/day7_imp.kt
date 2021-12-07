import kotlin.math.abs

fun main() {
  Day7Func.run()
  Day7Imp.run()
}

object Day7Imp : Solution<List<Int>> {
  override val name = "day7"
  override val parser = Parser.ints

  override fun part1(crabs: List<Int>): Int {
    return solve(crabs, ::identity)
  }

  override fun part2(crabs: List<Int>): Int {
    return solve(crabs, ::cost)
  }

  private fun solve(crabs: List<Int>, costFn: (Int) -> Int): Int {
    var maxCrab = Integer.MIN_VALUE
    for (pos in crabs) {
      if (pos > maxCrab) {
        maxCrab = pos
      }
    }

    var minFuelCost = Integer.MAX_VALUE
    for (target in 0 .. maxCrab) {
      var cost = 0
      for (pos in crabs) {
        cost += costFn(abs(pos - target))
      }
      if (cost < minFuelCost) {
        minFuelCost = cost
      }
    }
    return minFuelCost
  }

  private fun identity(distance: Int) = distance
  private fun cost(distance: Int) = distance * (distance + 1) / 2
}