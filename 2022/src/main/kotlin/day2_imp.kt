import utils.Parser
import utils.Solution
import utils.badInput
import utils.cut
import utils.mapItems

fun main() {
  Day2Imp.run()
}

object Day2Imp : Solution<List<Pair<Play, Char>>>() {
  override val name = "day2"
  override val parser = Parser.lines.mapItems { line -> line.cut(" ", { l -> Play.fromL(l[0]) }) { it[0] } }

  override fun part1(input: List<Pair<Play, Char>>): Int {
    var score = 0

    input.forEach { (play, char) ->
      val other = Play.fromR(char)
      score += (play to other).score
    }

    return score
  }

  override fun part2(input: List<Pair<Play, Char>>): Int {
    var score = 0

    input.forEach { (play, char) ->
      val other = when (char) {
        'X' -> play.winsOver // lose
        'Y' -> play // draw
        'Z' -> play.losesTo // win
        else -> badInput()
      }
      score += (play to other).score
    }

    return score
  }
}
