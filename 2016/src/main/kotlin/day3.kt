import utils.Parser
import utils.Solution
import utils.mapItems

fun main() {
  Day3.run()
}

object Day3 : Solution<List<List<Int>>>() {
  override val name = "day3"
  override val parser = Parser.lines.mapItems { it.split(" ").filter { it.trim().isNotBlank() }.map { it.trim().toInt() } }

  override fun part1(): Int {
    return input.count {
      it.max() < (it.sum() - it.max())
    }
  }

  override fun part2(): Int {
    return (0..2).flatMap { index ->
      input.chunked(3).map { cols ->
        cols.map { it[index] }
      }
    }.count {
      it.max() < (it.sum() - it.max())
    }
  }
}
