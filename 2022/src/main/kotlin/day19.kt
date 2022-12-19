import Day19.Resource.geode
import Day19.Resource.ore
import utils.Component4
import utils.Parser
import utils.Solution
import utils.Vec4i
import utils.cut
import utils.mapItems

fun main() {
  Day19.run()
}

object Day19 : Solution<List<Day19.Blueprint>>() {
  override val name = "day19"
  override val parser = Parser.lines.mapItems { line ->
    val robots = line.cut(":").second.split(".").map { it.trim() }.filter { it.isNotBlank() }

    val bpmap = robots.associate { costLine ->
      val type = Resource.valueOf(costLine.removePrefix("Each").trim().split(" ", limit = 2).first())
      val costs = costLine.split("costs", limit = 2).last().trim().split("and").map { it.trim() }
      type to costs.map {
        val (cost, resname) = it.split(" ", limit = 2)
        Resource.valueOf(resname).v * cost.toInt()
      }.reduce { a, b -> a + b }
    }
    Blueprint(bpmap)
  }

  enum class Resource(val c: Component4, val v: Vec4i) {
    ore(Component4.X, Vec4i(1, 0, 0, 0)),
    clay(Component4.Y, Vec4i(0, 1, 0, 0)),
    obsidian(Component4.Z, Vec4i(0, 0, 1, 0)),
    geode(Component4.W, Vec4i(0, 0, 0, 1)),
  }

  data class Blueprint(val costs: Map<Resource, Vec4i>)

  data class State(val timePassed: Int, val robots: Vec4i, val resources: Vec4i) {
    companion object {
      val INITIAL = State(
        timePassed = 0,
        robots = ore.v, // 1 ore only
        resources = Vec4i(0, 0, 0, 0), // all zero
      )
    }
  }

  private fun step(blueprint: Blueprint, state: State, buildRobot: Resource? = null): State {
    return state.copy(
      timePassed = state.timePassed + 1,
      robots = if (buildRobot == null) state.robots else state.robots + buildRobot.v,
      resources = (if (buildRobot == null) state.resources else state.resources - blueprint.costs[buildRobot]!!) + state.robots
    )
  }

  private val Vec4i.isPositive: Boolean get() = x >= 0 && y >= 0 && z >= 0 && w >= 0

  private fun bestGeodes(blueprint: Blueprint, state: State, cache: MutableMap<State, Int>, maxTime: Int): Int {
    val cached = cache[state]
    if (cached != null) {
      return cached
    }

    if (state.timePassed == maxTime) {
      return state.resources[geode.c]
    }

    // choose which robot to build
    val opts = Resource.values().reversed().filter {
      (state.resources - blueprint.costs[it]!!).isPositive
    } + null

    val geodes = opts.maxOf {
      bestGeodes(blueprint, step(blueprint, state, it), cache, maxTime)
    }
    cache[state] = geodes
    return geodes
  }

  override fun part1(input: List<Blueprint>): Int {
    return input.mapIndexed { idx, bp -> idx to bp }.parallelStream().map { (idx, bp) ->
      (idx + 1) * bestGeodes(bp, State.INITIAL, HashMap(), 24).also { println("$idx done, best: $it") }
    }.reduce { a, b -> a + b }.get()
  }
}
