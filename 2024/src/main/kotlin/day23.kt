import utils.Parser
import utils.Solution
import utils.badInput
import utils.cut
import utils.mapItems
import utils.selections

fun main() {
  Day23.run(skipPuzzle = false)
}

typealias Day23In = List<Pair<String, String>>

object Day23 : Solution<Day23In>() {
  override val name = "day23"
  override val parser: Parser<Day23In> = Parser.lines.mapItems { it.cut("-") }

  override fun part1(input: Day23In): Int {
    val connected = mutableMapOf<Pair<String, String>, Boolean>()
    val computers = mutableSetOf<String>()
    input.forEach { (a, b) ->
      connected[a to b] = true
      connected[b to a] = true
      computers += a
      computers += b
    }

    val answ = mutableSetOf<String>()

    input.forEach { (a, b) ->
      if (a.startsWith("t") || b.startsWith("t")) {
        computers.forEach {
          if (it != a && it != b && (connected[it to a] == true) && (connected[it to b] == true)) {
            answ += listOf(a, b, it).sorted().joinToString(",")
          }
        }
      }
    }

    return answ.size
  }

  override fun part2(input: Day23In): String {
    val computers = mutableMapOf<String, MutableSet<String>>()

    input.forEach { (a, b) ->
      computers.getOrPut(a) { mutableSetOf() }.add(b)
      computers.getOrPut(b) { mutableSetOf() }.add(a)
    }

    val maxSize = computers.maxOf { it.value.size } + 1

    for (sz in maxSize downTo 3) {
      val groupComputers = computers.filter { it.value.size >= (sz - 1) }.keys
      if (groupComputers.size < sz) {
        continue
      }
      groupComputers.forEach { one ->
        val others = computers[one]!!.toList()
        if (others.size >= sz - 1) {
          others.selections(sz - 1).forEach { selection ->
            val fits = selection.count {
              computers[it]!!.containsAll(selection - it + one)
            } >= (sz - 1)
            if (fits) {
              return (selection + one).toList().sorted().joinToString(",")
            }
          }
        }
      }
    }

    badInput()
  }
}
