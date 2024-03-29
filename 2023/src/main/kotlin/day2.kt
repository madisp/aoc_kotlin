import utils.Parse
import utils.Parser
import utils.Solution
import utils.mapItems

private typealias Input = List<Day2.Game>

fun main() {
  Day2.run()
}

object Day2 : Solution<Input>() {
  override val name = "day2"
  override val parser = Parser.lines.mapItems(::parseGame)

  @Parse("Game {id}: {r '; ' rounds}")
  data class Game(
    val id: Int,
    val rounds: List<Round>,
  ) {
    fun isPossible(amounts: Map<Color, Int>): Boolean {
      return rounds.all {
        Color.entries.all { c ->
          (it.shown[c] ?: 0) <= (amounts[c] ?: 0)
        }
      }
    }

    val power: Int get() {
      return Color.entries.fold(1) { acc, c ->
        acc * rounds.maxOf { it.shown[c] ?: 0 }
      }
    }
  }

  @Parse("{r ', ' shown}")
  data class Round(
    @Parse("{value} {key}")
    val shown: Map<Color, Int>,
  )

  enum class Color {
    red, green, blue;
  }

  override fun part1(input: Input): Int {
    val amounts = mapOf(Color.red to 12, Color.green to 13, Color.blue to 14)
    return input.filter { it.isPossible(amounts) }.sumOf { it.id }
  }

  override fun part2(input: Input): Int {
    return input.sumOf { it.power }
  }
}
