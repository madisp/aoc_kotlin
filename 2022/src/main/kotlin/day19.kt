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

  data class Blueprint(val costs: Map<Resource, Vec4i>) {
    fun computeMaxRobots() = Vec4i(
      costs.values.maxOf { it.x },
      costs.values.maxOf { it.y },
      costs.values.maxOf { it.z },
      Integer.MAX_VALUE, // produce as many geode bots as possible
    )
  }

  class Context(
    val bp: Blueprint,
    val cache: MutableMap<State, Int>,
    val maxRobots: Vec4i,
    val maxTime: Int,
    val maxGeodeBotsAt: IntArray,
  )

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

  private fun bestGeodes(ctx: Context, state: State): Int {
    val cached = ctx.cache[state]
    if (cached != null) {
      return cached
    }

    if (state.timePassed == ctx.maxTime) {
      return state.resources[geode.c]
    }

    val maxGBots = ctx.maxGeodeBotsAt[state.timePassed]
    if (state.robots.w < maxGBots - 1) {
      return 0 // not worth exploring as we are so much behind
    } else if (state.robots.w > maxGBots) {
      ctx.maxGeodeBotsAt[state.timePassed] = state.robots.w
    }

    // choose which robot to build
    val opts = Resource.values().reversed().filter {
      (state.resources - ctx.bp.costs[it]!!).isPositive && state.robots[it.c] < ctx.maxRobots[it.c]
    } + null

    // if you can build a geode bot, greedily build it?
    val geodes = if (opts.first() == geode) {
      bestGeodes(ctx, step(ctx.bp, state, geode))
    } else {
      opts.maxOf {
        bestGeodes(ctx, step(ctx.bp, state, it))
      }
    }
    ctx.cache[state] = geodes
    return geodes
  }

  override fun part1(input: List<Blueprint>): Int {
    return input.mapIndexed { idx, bp -> idx to bp }.parallelStream().map { (idx, bp) ->
      val ctx = Context(bp, HashMap(), bp.computeMaxRobots(), 24, IntArray(24) { 0 })
      (idx + 1) * bestGeodes(ctx, State.INITIAL)
    }.reduce { a, b -> a + b }.get()
  }

  override fun part2(input: List<Blueprint>): Int {
    return input.take(3).parallelStream().map { bp ->
      val ctx = Context(bp, HashMap(), bp.computeMaxRobots(), 32, IntArray(32) { 0 })
      bestGeodes(ctx, State.INITIAL)
    }.reduce { a, b -> a * b }.get()
  }
}
