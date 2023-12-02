import utils.Parser
import utils.Solution
import utils.cut
import utils.mapItems

private typealias Input = List<Game>

data class Game(
  val id: Int,
  val sets: List<Set>,
) {
  fun isPossible(amounts: Map<Color, Int>): Boolean {
    return sets.all {
      Color.entries.all { c ->
        (it.shown[c] ?: 0) <= (amounts[c] ?: 0)
      }
    }
  }

  val power: Int get() {
    return Color.entries.fold(1) { acc, c ->
      acc * sets.maxOf { it.shown[c] ?: 0 }
    }
  }
}

data class Set(
  val shown: Map<Color, Int>,
)

enum class Color {
  red, green, blue;
}

fun main() {
  Day2.run()
}

private object Day2 : Solution<Input>() {
  override val name = "day2"
  override val parser = Parser.lines.mapItems { line ->
    val (s, setStrs) = line.cut(": ")
    val sets = setStrs.split("; ").map { set ->
      val colors = set.split(", ")

      Set(
        colors.associate {
          val (num, color) = it.cut(" ")
          Color.valueOf(color) to num.toInt()
        }
      )
    }
    Game(
      id = s.removePrefix("Game ").toInt(),
      sets = sets,
    )
  }

  override fun part1(input: Input): Int {
    val amounts = mapOf(Color.red to 12, Color.green to 13, Color.blue to 14)
    return input.filter { it.isPossible(amounts) }.sumOf { it.id }
  }

  override fun part2(input: Input): Int {
    return input.sumOf { it.power }
  }
}
