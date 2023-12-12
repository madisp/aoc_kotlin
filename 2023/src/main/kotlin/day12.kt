import utils.Memo
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

      return Input(map.substring(minOf(index + group + 1, map.length)), dmgGroups.drop(1))
    }
  }

  private val countArrangements = Memo<Input, Long> { input ->
    val firstNonDotChar = input.map.indexOfFirst { it != '.' }

    if (input.dmgGroups.isEmpty()) {
      return@Memo if (input.map.none { it == '#' }) 1L else 0L
    } else if (input.map.isEmpty() || firstNonDotChar == -1) {
      return@Memo if (input.dmgGroups.isEmpty()) 1L else 0L
    }

    // check whether we can place a group
    val nextWithPlaced = input.place(firstNonDotChar)?.let { this(it) } ?: 0L

    // if first char is not '#' then we can also not place a group
    val nextWithoutPlaced = if (input.map[firstNonDotChar] != '#') {
      this(Input(input.map.substring(firstNonDotChar + 1), input.dmgGroups))
    } else 0L

    return@Memo (nextWithPlaced + nextWithoutPlaced)
  }

  override fun part1(): Long {
    return input.sumOf { countArrangements(it) }
  }

  override fun part2(): Long {
    return input
      .map { line -> Input(List(5) { line.map }.joinToString("?"), List(5) { line.dmgGroups }.flatten()) }
      .sumOf { countArrangements(it) }
  }
}
