import utils.*

fun main() {
  Day24.run()
}

typealias Day24In = List<Map<String, Int>>

object Day24 : Solution<Day24In>() {
  override val name = "day24"
  override val parser: Parser<Day24In> = Parser.lines.mapItems { line ->
    line.cut(": ").second.split(";").associate {
      val (k, v) = it.split("-")
      k to v.removeSuffix("min").trim().toInt()
    }
  }

  private fun time(chores: List<String>, assignments: List<Int>): Int {
    return input.indices.maxOf { person ->
      assignments.indices.filter { assignments[it] == person }.sumOf {
        input[person][chores[it]]!!
      }
    }
  }

  override fun part1(input: Day24In): Int {
    val chores = input.flatMap { it.keys }.toSet().toList()

    return (0 until input.size.pow(chores.size)).minOf { x ->
      val assignments = chores.indices.map { x / input.size.pow(chores.size - it - 1) % input.size }
      time(chores, assignments)
    }
  }
}
