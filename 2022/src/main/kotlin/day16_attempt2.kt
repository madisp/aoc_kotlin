import utils.Parse
import utils.Parser
import utils.Solution
import utils.mapItems
import utils.times

fun main() {
  Day16A2.run()
}

object Day16A2 : Solution<List<Day16A2.Valve>>() {
  override val name = "day16"
  override val parser = Parser.lines
    .mapItems { it.replace("tunnel leads to valve", "tunnels lead to valves") }
    .mapItems(::parseValve)

  @Parse("Valve {name} has flow rate={flowRate}; tunnels lead to valves {r ', ' out}")
  data class Valve(val name: String, val flowRate: Int, val out: List<String>)

  private fun computeDistMap(input: List<Valve>): Array<IntArray> {
    val inf = input.size * 4 // something large enough
    val distMap = Array(input.size) {
      IntArray(input.size) { inf }
    }

    // populate initial edges
    input.forEachIndexed { i, valve ->
      valve.out.forEach { target ->
        val j = input.indexOfFirst { it.name == target }
        distMap[i][j] = 1
        distMap[j][i] = 1
      }
    }

    for (k in input.indices) {
      for (i in input.indices) {
        for (j in input.indices) {
          if (distMap[i][j] > distMap[i][k] + distMap[k][j]) {
            distMap[i][j] = distMap[i][k] + distMap[k][j]
          }
        }
      }
    }

    return distMap
  }

  // state space 16 * 16 * 2^15 * 26 * 26, too big?
  data class StateKey(val loc1: Int, val loc2: Int, val remainingValves: List<Int>, val time1: Int, val time2: Int)

  class Context(
    val valves: List<Valve>,
    val distMap: Array<IntArray>,
    val cache: MutableMap<StateKey, Int>,
    val maxTime: Int,
  )

  private fun bestScore(
    ctx: Context,
    time1: Int,
    location1: Int,
    time2: Int,
    location2: Int,
    remainingValves: List<Int>,
    root: Boolean,
  ): Int {
    // nothing to open
    if (remainingValves.isEmpty()) {
      return 0
    }

    // out of time
    if (time1 >= ctx.maxTime && time2 >= ctx.maxTime) {
      return 0
    }

    val key = StateKey(location1, location2, remainingValves, time1, time2)
    val cached = ctx.cache[key]
    if (cached != null) {
      return cached
    }

    val opts =
      if (remainingValves.size == 1) {
        setOf(remainingValves[0] to null, null to remainingValves[0])
      } else {
        (remainingValves * remainingValves).filter { (a, b) -> a != b }
          .map { (a, b) ->
            a.takeIf { time1 + ctx.distMap[location1][a] < ctx.maxTime } to b.takeIf { time2 + ctx.distMap[location2][b] < ctx.maxTime }
          }.toSet()
      }

    var cur = 0

    val score = opts.maxOfOrNull { (open1, open2) ->
      var remaining = remainingValves
      var pressure1 = 0
      var t1 = time1
      var l1 = location1

      if (open1 != null) {
        val travel1 = ctx.distMap[location1][open1]
        pressure1 = (ctx.maxTime - (time1 + travel1)) * ctx.valves[open1].flowRate
        remaining = remaining.filter { it != open1 }
        t1 = time1 + travel1 + 1
        l1 = open1
      }

      var pressure2 = 0
      var t2 = time2
      var l2 = location2
      if (open2 != null) {
        val travel2 = ctx.distMap[location2][open2]
        pressure2 = (ctx.maxTime - (time2 + travel2)) * ctx.valves[open2].flowRate
        remaining = remaining.filter { it != open2 }
        t2 += travel2 + 1
        l2 = open2
      }

      if (root) {
        println("$cur / ${opts.size}")
      }

      if (pressure1 == 0 && pressure2 == 0) 0 else {
        pressure1 + pressure2 + bestScore(ctx, t1, l1, t2, l2, remaining, false)
      }.also { cur++ }
    } ?: 0

    ctx.cache[key] = score

    return score
  }

  override fun part1(input: List<Valve>): Int {
    val start = input.indexOfFirst { it.name == "AA" }
    val context = Context(
      valves = input,
      distMap = computeDistMap(input),
      cache = mutableMapOf(),
      maxTime = 30,
    )

    return bestScore(context, 1, start, context.maxTime, start, input.indices.filter { input[it].flowRate > 0 }, true)
  }

  override fun part2(input: List<Valve>): Any? {
    val start = input.indexOfFirst { it.name == "AA" }
    val context = Context(
      valves = input,
      distMap = computeDistMap(input),
      cache = mutableMapOf(),
      maxTime = 26,
    )

    return bestScore(context, 1, start, 1, start, input.indices.filter { input[it].flowRate > 0 }, true)
  }
}
