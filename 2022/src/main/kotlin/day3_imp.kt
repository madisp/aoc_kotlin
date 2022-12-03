import utils.Parser
import utils.Solution
import utils.map
import utils.mapItems
import utils.split

object Day3All {
  @JvmStatic fun main(args: Array<String>) {
    mapOf("func" to Day3Func, "imp" to Day3Imp, "fast" to Day3Fast).forEach { (header, solution) ->
      solution.run(
        header = header,
        printParseTime = false
      )
    }
  }
}

object Day3Imp : Solution<Day3Input>() {
  override val name = "day3"
  override val parser = Parser.lines.mapItems { line -> line.split().map { it.toCharArray().toSet() } }

  override fun part1(input: Day3Input): Int {
    var sum = 0
    for (rucksack in input) {
      sum += (rucksack.first intersect rucksack.second).first().priority + 1
    }
    return sum
  }

  override fun part2(input: Day3Input): Number {
    var sum = 0
    (input.indices step 3).forEach { i ->
      var set = input[i].first + input[i].second
      for (j in i + 1  until i + 3) {
        set = set intersect (input[j].first + input[j].second)
      }
      sum += set.first().priority + 1
    }
    return sum
  }
}
