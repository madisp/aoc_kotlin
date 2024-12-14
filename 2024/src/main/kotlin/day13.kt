import utils.Parse
import utils.Parser
import utils.Solution
import utils.Vec2l
import utils.mapItems

fun main() {
  Day13.run()
}

typealias Day13In = List<Day13.Configuration>

object Day13 : Solution<Day13In>() {
  override val name = "day13"
  override val parser: Parser<Day13In> = Parser { it.split("\n\n") }
    .mapItems { parseConfigurationStr(it) }
    .mapItems {
      Configuration(Vec2l(it.aX, it.aY), Vec2l(it.bX, it.bY), Vec2l(it.pX, it.pY))
    }

  @Parse("Button A: X+{aX}, Y+{aY}\nButton B: X+{bX}, Y+{bY}\nPrize: X={pX}, Y={pY}")
  data class ConfigurationStr(
    val aX: Long,
    val aY: Long,
    val bX: Long,
    val bY: Long,
    val pX: Long,
    val pY: Long,
  )

  data class Configuration(
    val a: Vec2l,
    val b: Vec2l,
    val prize: Vec2l,
  )

  override fun part1(input: Day13In): Long {
    return solve(input)
  }

  private fun solve(input: Day13In): Long {
    return input.sumOf { m ->
      // p_x = a * a_x + b * b_x
      // p_y = a * a_y + b * b_y
      // (p_x - b*b_x) / a_x = (p_y - b*b_y) / a_y
      // b = (p_y*a_x - p_x*a_y) / (b_y * a_x - b_x * a_y)
      // a = (p_y - b*b_y) / a_y
      val b = (m.prize.y * m.a.x - m.prize.x * m.a.y) / (m.b.y * m.a.x - m.b.x * m.a.y)
      val a = (m.prize.y - b * m.b.y) / m.a.y

      // only positive answers are correct
      if (a >= 0 && b >= 0) {
        // throw out non-valid solutions (due to long math truncation)
        if ((a * m.a.x + b * m.b.x) == m.prize.x) {
          if ((a * m.a.y + b * m.b.y) == m.prize.y) {
            return@sumOf a * 3 + b
          }
        }
      }

      return@sumOf 0L
    }
  }

  override fun part2(input: Day13In): Long {
    return solve(input.map { it.copy(prize = it.prize + 10_000_000_000_000) })
  }
}
