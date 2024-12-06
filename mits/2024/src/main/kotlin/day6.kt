import utils.*

fun main() {
  Day6.run()
}

typealias Day6In = Pair<String, Grid<Char>>

object Day6 : Solution<Day6In>() {
  override val name = "day6"
  override val parser: Parser<Day6In> = Parser.compound("\n", Parser { it.trim() }, Parser.charGrid)

  override fun part1(input: Day6In): String {
    val nums = input.second.rows.flatMap { r -> r.cells.windowed(7).map { it.map { it.second }.joinToString("") } } +
      input.second.columns.flatMap { c -> c.cells.windowed(7).map { it.map { it.second }.joinToString("") } }

    // vale: 0807912, 0818343, 0821652, 0826350, 0833428, 0834368, 0839948, 0849051, 0853472, 0856747, 0865913, 0871889, 0881605, 0885920
    // õige: 807912, 818343, 821652, 826350, 833428, 834368, 839948, 849051, 853472, 856747, 865913, 871889, 881605, 885920
    return nums.filter { it.startsWith(input.first) }.sorted().distinct().map { it.toInt().toString() }.joinToString()
  }
}
