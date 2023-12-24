import utils.Parse
import utils.Parser
import utils.Solution

fun main() {
  Day25.run()
}

object Day25 : Solution<Day25.Input>() {
  override val name = "day25"
  override val parser = Parser { parseInput(it.trim()) }

  @Parse("To continue, please consult the code grid in the manual.  Enter the code at row {y}, column {x}.")
  data class Input(
    val x: Int,
    val y: Int,
  )

  private fun getNumber(x: Int, y: Int): Long {
    var seq = 1
    for (row in 1 until y) {
      seq += row
    }
    for (col in 1 until x) {
      seq += (y + col)
    }

    var value = 20151125L
    repeat(seq - 1) {
      value *= 252533
      value %= 33554393
    }

    return value
  }

  override fun part1(): Long {
    return getNumber(input.x, input.y)
  }
}
