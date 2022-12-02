import utils.Parser
import utils.Solution
import utils.badInput
import utils.cut
import utils.mapItems

fun main() {
  Day2Func.run()
}

enum class Play(val score: Int) {
  Rock(1),
  Paper(2),
  Scissors(3);

  val winsOver: Play get() = when (this) {
    Rock -> Scissors
    Paper -> Rock
    Scissors -> Paper
  }

  val losesTo: Play get() = values().first { it.winsOver == this }

  companion object {
    fun fromL(left: Char): Play {
      return when (left) {
        'A' -> Rock
        'B' -> Paper
        'C' -> Scissors
        else -> badInput()
      }
    }
    fun fromR(right: Char): Play {
      return when (right) {
        'X' -> Rock
        'Y' -> Paper
        'Z' -> Scissors
        else -> badInput()
      }
    }
  }
}

val Pair<Play, Play>.score: Int get() {
  val outcome = when {
    second.winsOver == first -> 6
    second == first -> 3
    else -> 0
  }
  return second.score + outcome
}

object Day2Func : Solution<List<Pair<Play, Char>>>() {
  override val name = "day2"
  override val parser = Parser.lines.mapItems { line -> line.cut(" ", { l -> Play.fromL(l[0]) }) { it[0] } }

  override fun part1(input: List<Pair<Play, Char>>): Number {
    return input
      .map { (play, char) -> play to Play.fromR(char) }
      .sumOf { it.score }
  }

  override fun part2(input: List<Pair<Play, Char>>): Number {
    return input.map { (play, char) ->
      val other = when (char) {
        'X' -> play.winsOver // lose
        'Y' -> play // draw
        'Z' -> play.losesTo // win
        else -> badInput()
      }
      play to other
    }.sumOf { it.score }
  }
}
