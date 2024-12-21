import utils.*

fun main() {
  Day20.run()
}

typealias Day20In = Map<Pair<String, String>, Int>

object Day20 : Solution<Day20In>() {
  override val name = "day20"
  override val parser: Parser<Day20In> = Parser.lines.mapItems {
    val (start, list) = it.cut(": ")
    list.split("| ; |").map { end ->
      val (city, time) = end.cut(" - ")
      (start to city) to time.removeSuffix("min").toInt()
    }
  }.map { it.flatten().toMap() }

  private fun tourLen(cities: List<String>) = cities.zipWithNext().sumBy { (from, to) ->
    input[from to to] ?: throw IllegalStateException("No path from $from to $to")
  }

  override fun part1(input: Day20In): String {
    val homeBase = "Lennarti Kodu"
    val cities = (input.keys.map { it.first }.toSet() - homeBase).toList()

    var best = Integer.MAX_VALUE to emptyList<String>()
    cities.permutations.forEach { list ->
      val tour = listOf(homeBase, *list.toTypedArray(), homeBase)
      val len = tourLen(tour)
      if (len < best.first) {
        best = len to tour
      }
    }

    return best.second.joinToString(" -> ")
  }
}
