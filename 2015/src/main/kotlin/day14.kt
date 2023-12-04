import utils.Parse
import utils.Parser
import utils.Solution
import utils.mapItems

fun main() {
  Day14.run()
}

object Day14: Solution<List<Day14.Reindeer>>() {
  override val name = "day14"
  override val parser = Parser.lines.mapItems { parseReindeer(it) }

  private const val ROUNDS = 2503

  @Parse("{name} can fly {speed} km/s for {activeDuration} seconds, but then must rest for {restDuration} seconds.")
  data class Reindeer(
    val name: String,
    val speed: Int,
    val activeDuration: Int,
    val restDuration: Int,
  ) {
    fun traveledAt(time: Int): Int {
      val cycleTime = activeDuration + restDuration
      val cycleTravel = (time / cycleTime) * activeDuration * speed
      val travelRest = minOf(activeDuration, time % cycleTime) * speed
      return cycleTravel + travelRest
    }
  }

  override fun part1(input: List<Reindeer>): Int {
    return input.maxOf { it.traveledAt(ROUNDS) }
  }

  override fun part2(input: List<Reindeer>): Int {
    val startScores = input.associate { it.name to 0 }
    return (1 .. ROUNDS).fold(startScores) { scores, time ->
      val leading = input.maxBy { it.traveledAt(time) }
      scores + mapOf(leading.name to scores[leading.name]!! + 1)
    }.entries.maxOf { (_, v) -> v }
  }
}
