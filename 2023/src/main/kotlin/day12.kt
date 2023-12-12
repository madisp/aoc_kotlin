import utils.Parse
import utils.Parser
import utils.Solution
import utils.mapItems

fun main() {
  Day12.run()
}

object Day12 : Solution<List<Day12.Input>>() {
  override val name = "day12"
  override val parser = Parser.lines.mapItems { parseInput(it) }

  @Parse("{map} {r ',' dmgGroups}")
  data class Input(
    val map: String,
    val dmgGroups: List<Int>
  ) {
    fun place(index: Int): Input? {
      val group = dmgGroups.firstOrNull() ?: return null
      if (map.length < index + group) return null
      if (map.substring(index, index + group).any { it == '.' }) return null
      if (index + group < map.length && map[index + group] == '#') return null

      val ret = Input(map.substring(minOf(index + group + 1, map.length)), dmgGroups.drop(1))

      return ret
    }
  }

  private val arrangeCache = mutableMapOf<Input, Long>()

  private fun countArrangements(input: Input): Long {
    val cached = arrangeCache[input]
    if (cached != null) {
      return cached
    }

    val firstNonDotChar = input.map.indexOfFirst { it != '.' }

    if (input.dmgGroups.isEmpty()) {
      return if (input.map.none { it == '#' }) 1L else 0L
    } else if (input.map.isEmpty() || firstNonDotChar == -1) {
      return if (input.dmgGroups.isEmpty()) 1L else 0L
    }

    // check whether we can place a group
    val nextWithPlaced = input.place(firstNonDotChar)?.let { countArrangements(it) } ?: 0L

    // if first char is not '#' then we can also not place a group
    val nextWithoutPlaced = if (input.map[firstNonDotChar] != '#') {
      countArrangements(Input(input.map.substring(firstNonDotChar + 1), input.dmgGroups))
    } else 0L

    return (nextWithPlaced + nextWithoutPlaced).also { arrangeCache[input] = it }
  }

  override fun part1(): Long {
    val arrangements = input.map { countArrangements(it) }
    countArrangements(input.last())
    return arrangements.sum()
  }

  override fun part2(): Long {
    return input
      .map { line ->
        Input(List(5) { line.map }.joinToString("?"), List(5) { line.dmgGroups }.flatten())
      }
      .sumOf { countArrangements(it) }
  }
}
