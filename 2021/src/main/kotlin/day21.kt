import utils.Parser
import utils.Solution
import utils.mapItems

fun main() {
  Day21.run()
}

object Day21 : Solution<Day21.State>() {
  override val name = "day21"
  override val parser = Parser.lines
    .mapItems { it.split(":").last().trim() }
    .map {
      State(
        true,
        it.first().toInt() - 1,
        it.last().toInt() - 1,
        0,
        0
      )
    }

  data class State(
    val p1Rolls: Boolean, // 0-1, 2 values
    val p1Pos: Int,       // 0-9, 10 values
    val p2Pos: Int,       // 0-9, 10 values
    val p1Score: Int,     // 0-21, 22 values
    val p2Score: Int      // 0-21, 22 values
  )

  private fun State.withRoll(roll: Int): State {
    if (p1Rolls) {
      // p1 rolls
      val newP1Pos = (p1Pos + roll) % 10
      return copy(
        p1Rolls = false,
        p1Pos = newP1Pos,
        p1Score = p1Score + newP1Pos + 1
      )
    } else {
      // p2 rolls
      val newP2Pos = (p2Pos + roll) % 10
      return copy(
        p1Rolls = true,
        p2Pos = newP2Pos,
        p2Score = p2Score + newP2Pos + 1
      )
    }
  }

  private fun playDeterministic(state: State, round: Int): State {
    val roll = (round * 3 + 1 + round * 3 + 2 + round * 3 + 3)
    return state.withRoll(roll)
  }

  val cache = mutableMapOf<State, Pair<Long, Long>>()

  private fun playQuantum(round: Int, state: State): Pair<Long, Long> {
    val cached = cache[state]
    if (cached != null) {
      return cached
    }

    if (state.p1Score >= 21) {
      return 1L to 0L
    } else if (state.p2Score >= 21) {
      return 0L to 1L
    }

    val rolls = (1 .. 3).flatMap { r1 ->
      (1 .. 3).flatMap { r2 ->
        (1 .. 3).map { r3 ->
          r1 + r2 + r3
        }
      }
    }

    val answ = rolls.map {
      playQuantum(round + 1, state.withRoll(it))
    }.reduce { p1, p2 -> p1.first + p2.first to p1.second + p2.second }
    cache[state] = answ
    return answ
  }

  override fun part1(input: State): Int {
    val lastRound = generateSequence(input to 0) { (state, round) -> playDeterministic(state, round) to round + 1 }
      .takeWhile { it.first.p1Score < 1000 && it.first.p2Score < 1000 }
      .last()

    val winning = playDeterministic(lastRound.first, lastRound.second)

    return ((lastRound.second + 1) * 3) * minOf(winning.p1Score, winning.p2Score)
  }

  override fun part2(input: State): Number? {
    val winCounts = playQuantum(0, input)
    return maxOf(winCounts.first, winCounts.second)
  }
}