import utils.*

fun main() {
  Day21.run()
}

typealias Day21In = Grid<Char>

object Day21 : Solution<Day21In>() {
  override val name = "day21"
  override val parser: Parser<Day21In> = Parser.charGrid

  private fun fill(grid: Grid<Char>, max: Boolean = false): Grid<Char> {
    val g = grid.toMutable()
    var changes = true

    while (changes) {
      changes = false
      val stars = g.coords.filter { g[it] == '*' }
      stars.forEach { star ->
        star.adjacent.forEach { p ->
          if (max) {
            if (p in g && g[p] != '*' && (g[p] - '0' > 0)) {
              g[p] = '*'
              changes = true
            }
          } else {
            if (p in g && g[p] != '*' && ('4' - g[p]) < p.adjacent.count { it in g && g[it] == '*' }) {
              g[p] = '*'
              changes = true
            }
          }
        }
      }
    }

    return g
  }

  override fun part1(input: Day21In): String {
    val min = fill(input)
    val max = fill(min, max = true)
    return listOf(min, max).map { it.values.count { c -> c == '*' } }.joinToString(", ")
  }
}
