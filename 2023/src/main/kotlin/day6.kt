import utils.Parser
import utils.Solution
import utils.cut
import utils.mapItems

fun main() {
  Day6.run(skipTest = false)
}

object Day6 : Solution<List<Day6.Race>>() {
  override val name = "day6"

  override val parser = Parser.lines.mapItems { input ->
    input.cut(":").second.split(" ").mapNotNull { it.trim().takeIf(String::isNotBlank)?.toLong() }
  }.map { (times, records) ->
    require(times.size == records.size)
    times.mapIndexed { i, time ->
      Race(time, records[i])
    }
  }

  data class Race(
    val time: Long,
    val record: Long,
  ) {
    operator fun plus(other: Race): Race {
      return Race(
        time = (this.time.toString() + other.time.toString()).toLong(),
        record = (this.record.toString() + other.record.toString()).toLong()
      )
    }
  }

  override fun part1(input: List<Race>): Int {
    return input.map { race ->
      (1 until race.time).count { speed ->
        (race.time - speed) * speed > race.record
      }
    }.reduce { a, b -> a * b }
  }

  override fun part2(input: List<Race>): Int {
    val race = input.reduce { a, b -> a + b }
    return (1 until race.time).count { speed ->
      (race.time - speed) * speed > race.record
    }
  }
}
