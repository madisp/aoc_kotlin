import utils.Parser
import utils.Solution
import utils.combinations
import utils.cut
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.PriorityQueue

fun main() {
  Day8.run()
}

typealias Day8In = Pair<Int, List<Int>>

object Day8 : Solution<Day8In>() {
  override val name = "day8"

  override val parser = Parser.compoundList(
    headerDelimiter = "Tooted:",
    itemDelimiter = "\n",
    header = { it.trim().cut(":").second.toInt() },
    item = { it.toInt() }
  )

  private fun minus(a: List<Int>, b: List<Int>): List<Int> {
    val result = a.toMutableList()
    b.forEach { result.remove(it) }
    return result
  }

  // horribly inefficient bruteforce solution, but it will work with our input sizes
  private fun fit(truckCount: Int, maxWeight: Int, items: List<Int>, memo: MutableMap<Pair<Int, List<Int>>, Boolean> = mutableMapOf()): Boolean {
    if (truckCount == 1) {
      return (items.sum() <= maxWeight)
    }
    if (memo[truckCount to items] != null) {
      return memo[truckCount to items]!!
    }
    return items.combinations.any {
      it.sum() <= maxWeight && fit(truckCount - 1, maxWeight, minus(items, it), memo)
    }.also { if (truckCount <= 4) { memo[truckCount to items] = it } }
  }

  override fun part1(input: Day8In): Int {
    val (maxWeight, items) = input
    val best = (BigDecimal(items.sumOf { it }).divide(BigDecimal(maxWeight), RoundingMode.CEILING)).toInt()

    // try LPT first
    val trucks = PriorityQueue<Int>()
    items.sortedDescending().forEach { item ->
      if (trucks.isEmpty() || trucks.peek() + item > maxWeight) {
        trucks.add(item)
      } else {
        trucks.add(trucks.poll() + item)
      }
    }

    if (trucks.size == best) {
      return best
    }

    var bestGuess = trucks.size
    for (guess in trucks.size - 1 downTo best) {
      if (!fit(guess, maxWeight, items)) {
        return bestGuess
      }
      bestGuess = guess
    }

    throw IllegalStateException("No solution found")
  }
}
