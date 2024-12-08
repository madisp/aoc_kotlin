import utils.*

fun main() {
  Day8.run()
}

typealias Day8In = Grid<Char>

object Day8 : Solution<Day8In>() {
  override val name = "day8"
  override val parser: Parser<Day8In> = Parser.charGrid

  private fun countAntinodes(harmonics: Boolean = false): Int {
    val antinodes = mutableSetOf<Vec2i>()
    val freqs = input.cells.filter { (_, c) -> c != '.' }
      .groupBy { (_, c) -> c }
      .mapValues { (_, cells) -> cells.map { (p, _) -> p } }

    freqs.forEach { (_, ps) ->
      ps.product().filter { (a, b) -> a != b }.forEach { (a, b) ->
        val d = a - b
        if (harmonics) {
          for (step in 0 until (maxOf(input.width, input.height))) {
            val h = a - (d * step) to a + (d * step)
            if (h.first !in input && h.second !in input) {
              break
            }
            antinodes += h.first
            antinodes += h.second
          }
        } else {
          antinodes += a + d
          antinodes += b - d
        }
      }
    }

    return antinodes.filter { it in input }.size
  }

  override fun part1(input: Day8In) = countAntinodes()
  override fun part2(input: Day8In) = countAntinodes(harmonics = true)
}
