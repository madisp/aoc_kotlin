import utils.Parse
import utils.Parser
import utils.Solution
import utils.mapItems

fun main() {
  Day12.run()
}

typealias Day12In = Pair<List<Day12.Resident>, List<Day12.Entry>>

object Day12 : Solution<Day12In>() {
  override val name = "day12"
  override val parser: Parser<Day12In> = Parser.compound(
    Parser.lines.mapItems { Day12.parseResident(it) },
    Parser.lines.mapItems { Day12.parseEntry(it) }
  )

  @Parse("{name}-{height}-{age}-{clothes}")
  data class Resident(
    val name: String,
    val height: Int,
    val age: Int,
    val clothes: String,
  )

  @Parse("{height} cm, {age} aastat vana, seljas on {clothes}")
  data class Entry(
    val height: Int,
    val age: Int,
    val clothes: String,
  )

  override fun part1(input: Day12In): String {
    val (residents, entries) = input

    val allowed = residents.count { resident ->
      entries.any { entry ->
        resident.height == entry.height && resident.age == entry.age && resident.clothes == entry.clothes
      }
    }

    return "Sissepääs lubatud: $allowed. Sissepääs keelatud: ${entries.size - allowed}."
  }
}
