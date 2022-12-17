import utils.Parser
import utils.Solution
import utils.mapItems

fun main() {
  Day16.run()
}

object Day16 : Solution<List<Day16.Valve>>() {
  override val name = "day16"
  override val parser = Parser.lines.mapItems { line ->
    val (name, flowRate, valves) = line.split("Valve ",
      " has flow rate=",
      "; tunnels lead to valves ",
      "; tunnel leads to valve ")
      .map { it.trim() }
      .filter { it.isNotBlank() }

    Valve(name, flowRate.toInt(), valves.split(", "))
  }

  data class Valve(val name: String, val flowRate: Int, val out: List<String>)

  data class CacheKeyP1(val releasedPressure: Int, val curTime: Int, val curNode: String, val openValves: String)
  private val cachep1 = mutableMapOf<CacheKeyP1, Int>()

  fun getBestFlowRateP1(releasedPressure: Int, curTime: Int, curNode: String, openValves: Set<String>, valves: Map<String, Valve>): Int {
    val flowRate = openValves.map { valves[it]!! }.sumOf { it.flowRate }
    val newReleasedPressure = releasedPressure + flowRate

    if (curTime == 30) {
      return newReleasedPressure
    }

    // if all valves are open, just advance time
    if (openValves.size == valves.size) {
      return newReleasedPressure + (30 - curTime) * flowRate
    }

    val cacheKey = CacheKeyP1(releasedPressure, curTime, curNode, openValves.sorted().joinToString(","))
    val cachedAnsw = cachep1[cacheKey]
    if (cachedAnsw != null) {
      return cachedAnsw
    }

    val curValve = valves[curNode]!!

    // try opening current valve if it gives any flowrate
    val bestAfterOpen = if (curValve.flowRate > 0 && curValve.name !in openValves) {
      getBestFlowRateP1(newReleasedPressure, curTime + 1, curNode, openValves + curValve.name, valves)
    } else {
      Integer.MIN_VALUE // don't consider opening
    }

    val bestAfterVisit = curValve.out.map { valves[it]!! }.maxOf {
      getBestFlowRateP1(newReleasedPressure, curTime + 1, it.name, openValves, valves)
    }

    val best = maxOf(bestAfterOpen, bestAfterVisit)
    cachep1[cacheKey] = best
    return best
  }

  data class CacheKeyP2(val releasedPressure: Int, val curTime: Int, val myNode: String, val elephantNode: String, val openValves: String)
//  private val cachep2 = mutableMapOf<CacheKeyP2, Int>()

  sealed class Next {
    object Open : Next()
    data class Move(val to: String) : Next()
  }

  var bestOpenTime = Integer.MAX_VALUE
  var bestReleasedPressure = IntArray(40) { Integer.MIN_VALUE }

  fun getBestFlowRateP2(releasedPressure: Int, curTime: Int, myNode: String, ellyNode: String, openValves: Set<String>, valves: Map<String, Valve>, root: Boolean = false): Int {
    val flowRate = openValves.map { valves[it]!! }.sumOf { it.flowRate }
    val newReleasedPressure = releasedPressure + flowRate

    if (curTime == 26) {
      return newReleasedPressure
    }

    // if all valves are open, just advance time
//    if (openValves.size == valves.values.filter { it.flowRate > 0 }.size) {
//      if (curTime < bestOpenTime) {
//        println("Found best open time at $curTime")
//      }
//      bestOpenTime = minOf(curTime, bestOpenTime)
//      bestReleasedPressure[curTime] = maxOf(bestReleasedPressure[curTime], newReleasedPressure)
//      return newReleasedPressure + (26 - curTime) * flowRate
//    } else if (curTime > bestOpenTime) {
//      if (bestReleasedPressure[bestOpenTime] > newReleasedPressure) {
//        // no point
//        return Integer.MIN_VALUE
//      }
//    }

//    val cacheKey = CacheKeyP2(releasedPressure, curTime, myNode, ellyNode, openValves.sorted().joinToString(","))
//    val cachedAnsw = cachep2[cacheKey]
//    if (cachedAnsw != null) {
//      return cachedAnsw
//    }

    val curMyValve = valves[myNode]!!
    val curEllyValve = valves[ellyNode]!!

//    val myOpts = if (curMyValve.flowRate > 0 && curMyValve.name !in openValves) { listOf(Next.Open) } else curMyValve.out.map { Next.Move(it) }
//    val ellyOpts = if (curEllyValve.flowRate > 0 && curEllyValve.name !in openValves) { listOf(Next.Open) } else curEllyValve.out.map { Next.Move(it) }
    val myOpts = listOf(Next.Open.takeIf { curMyValve.flowRate > 0 && curMyValve.name !in openValves }) + curMyValve.out.map { Next.Move(it) }
    val ellyOpts = listOf(Next.Open.takeIf { curEllyValve.flowRate > 0 && curEllyValve.name !in openValves }) + curEllyValve.out.map { Next.Move(it) }

    val opts = myOpts.filterNotNull().flatMap { myOpt ->
      ellyOpts.filterNotNull().map { myOpt to it }
    }
    val best = opts.mapIndexed { i, (myNext, ellyNext) ->
      val myNextNode = if (myNext is Next.Move) myNext.to else null
      val ellyNextNode = if (ellyNext is Next.Move) ellyNext.to else null

      val newOpenValves = openValves + listOfNotNull(
        curMyValve.name.takeIf { myNextNode == null },
        curEllyValve.name.takeIf { ellyNextNode == null },
      )

      if (root) {
        println("$i / ${opts.size}")
      }
      getBestFlowRateP2(newReleasedPressure, curTime + 1, myNextNode ?: myNode, ellyNextNode ?: ellyNode, newOpenValves, valves)
    }.max()

//    cachep2[cacheKey] = best
    return best
  }

  override fun part1(input: List<Valve>): Int {
    return 0
//    cachep1.clear()
//    val valves = input.associateBy { it.name }
//    return getBestFlowRateP1(0, 1, "AA", emptySet(), valves)
  }

  override fun part2(input: List<Valve>): Any? {
//    cachep2.clear()
    bestOpenTime = Integer.MAX_VALUE
    val valves = input.associateBy { it.name }
    return getBestFlowRateP2(0, 1, "AA", "AA", emptySet(), valves, root = true)
  }
}
