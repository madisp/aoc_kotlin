import utils.Parser
import utils.mapItems

fun main() {
  Day12Imp.run()
}

object Day12Imp : Solution<Map<String, List<String>>> {
  override val name = "day12"
  override val parser = Parser.lines.mapItems {
      val (start, end) = it.split('-', limit = 2)
      start to end
    }.map {
      it.flatMap { (start, end) ->
        listOf(start to end, end to start)
      }
      .groupBy({ (start, _) -> start }) { (_, end) -> end }
    }

  val String.isBig get() = toCharArray().all(Char::isUpperCase)

  /**
   * Perform a breadth-first search to count all the pathys
   */
  private fun countPaths(input: Map<String, List<String>>, doubleVisitAlllowed: Boolean = false): Int {

    data class Frame(val vertex: String, val visited: Set<String>, val doubleVisitUsed: Boolean)

    val stack = ArrayDeque<Frame>()
    var count = 0
    stack.add(Frame("start", setOf("start"), !doubleVisitAlllowed))

    while (stack.isNotEmpty()) {
      val (vertex, visited, doubleUsed) = stack.removeFirst()
      if (vertex == "end") {
        count++
      } else {
        val vs = input[vertex]!!
        for (v in vs) {
          if (!v.isBig && v in visited) {
            if (!doubleUsed && v != "start" && v != "end") {
              // use our double visit for this vertex
              stack.add(Frame(v, visited + v, true))
            }
          } else {
            stack.add(Frame(v, visited + v, doubleUsed))
          }
        }
      }
    }
    return count
  }

  override fun part1(input: Map<String, List<String>>): Int {
    return countPaths(input)
  }

  override fun part2(input: Map<String, List<String>>): Int {
    return countPaths(input, doubleVisitAlllowed = true)
  }
}
