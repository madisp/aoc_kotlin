import utils.Parse
import utils.Parser
import utils.Solution
import utils.lcm

fun main() {
  Day8.run()
}

object Day8 : Solution<Day8.Input>() {
  override val name = "day8"
  override val parser = Parser { parseInput(it) }

  @Parse("{instructions}\n\n{r '\n' edges}")
  data class Input(
    val instructions: String,
    val edges: List<Edge>,
  )

  @Parse("{from} = ({left}, {right})")
  data class Edge(
    val from: String,
    val left: String,
    val right: String,
  )

  private fun len(from: String, arrived: (String) -> Boolean): Pair<String, Int>? {
    val edges = input.edges.groupBy { it.from }
    var node = from
    var steps = 0

    while (!arrived(node) || steps == 0) {
      val insn = input.instructions[steps % input.instructions.length]
      val edge = edges[node] ?: return null
      node = if (insn == 'L') edge.first().left else edge.first().right
      steps++
    }

    return node to steps
  }

  override fun part1(input: Day8.Input): Int? {
    return len("AAA") { it == "ZZZ" }?.second
  }

  override fun part2(input: Day8.Input): Long {
    val starts = input.edges.filter { it.from.endsWith("A") }.map { it.from }
    val ends = input.edges.filter { it.from.endsWith("Z") }.map { it.from }
    val arrival: (String) -> Boolean = { it.endsWith('Z') }

    // cycles between ends?
    val endCycles = ends.associateWith { e -> len(e, arrival) }
    val offs = starts.associateWith { s -> len(s, arrival) }

    val cycles = offs.entries.associate { (key, off) ->
      val cycleLen = endCycles[off!!.first]
      key to (off.second to cycleLen?.second)
    }

    if (cycles.values.any { it.first != it.second }) {
      // TODO(madis) can we get rid of this restriction?
      throw IllegalStateException("Offset is not the same as cycle length for starting point")
    }

    return lcm(cycles.values.map { it.first })
  }
}
