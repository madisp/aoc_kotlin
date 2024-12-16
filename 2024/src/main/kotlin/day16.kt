import utils.*
import java.util.ArrayDeque
import java.util.PriorityQueue

fun main() {
  Day16.run()
}

typealias Day16In = Grid<Char>

object Day16 : Solution<Day16In>() {
  override val name = "day16"
  override val parser: Parser<Day16In> = Parser.charGrid.map { it.withDefault('#') }

  data class State(
    val pos: Vec2i,
    val dir: Vec2i,
  )

  override fun part1(input: Day16In): Any? {
    val start = State(
      input.coords.first { input[it] == 'S' },
      Vec2i.RIGHT
    )
    val endPos = input.coords.first { input[it] == 'E' }

    val queue = PriorityQueue<Pair<State, Int>> { a, b ->
      a.second.compareTo(b.second)
    }

    queue.add(start to 0)

    val best = mutableMapOf<State, Int>()

    while (queue.isNotEmpty()) {
      val next = queue.poll()

      val bestCost = best[next.first]
      if (bestCost != null && bestCost <= next.second) {
        continue
      }

      best[next.first] = next.second

      val turns = listOf(
        1000 to (next.first.copy(dir = next.first.dir.rotateCcw())),
        1000 to (next.first.copy(dir = next.first.dir.rotateCw())),
      )
      turns.forEach { (c, ns) ->
        queue.add(ns to c + next.second)
      }

      if (input[next.first.pos + next.first.dir] in "SE.") {
        queue.add((next.first.copy(pos = next.first.pos + next.first.dir)) to (next.second + 1))
      }
    }

    return best[State(endPos, Vec2i.UP)]
  }

  override fun part2(input: Day16In): Any? {
    val start = State(
      input.coords.first { input[it] == 'S' },
      Vec2i.RIGHT
    )
    val endPos = input.coords.first { input[it] == 'E' }

    val queue = PriorityQueue<Triple<State, State, Int>> { a, b ->
      a.third.compareTo(b.third)
    }

    queue.add(Triple(start, start, 0))

    val best = mutableMapOf<State, Int>()
    val srcs = mutableMapOf<State, MutableSet<State>>()

    while (queue.isNotEmpty()) {
      val next = queue.poll()

      val bestCost = best[next.first]
      if (bestCost != null && bestCost < next.third) {
        continue
      }

      best[next.first] = next.third
      if (bestCost == null || bestCost > next.third) {
        srcs[next.first] = mutableSetOf()
      }
      srcs.getOrPut(next.first) { mutableSetOf() }.add(next.second)

      val turns = listOf(
        1000 to (next.first.copy(dir = next.first.dir.rotateCcw())),
        1000 to (next.first.copy(dir = next.first.dir.rotateCw())),
      )
      turns.forEach { (c, ns) ->
        queue.add(Triple(ns, next.first, c + next.third))
      }

      if (input[next.first.pos + next.first.dir] in "SE.") {
        queue.add(Triple(
          (next.first.copy(pos = next.first.pos + next.first.dir)), next.first, (next.third + 1)
        ))
      }
    }

    val pts = mutableSetOf<State>()
    val btQueue = ArrayDeque<State>()
    btQueue.add(State(endPos, Vec2i.UP))

    while (btQueue.isNotEmpty()) {
      val node = btQueue.poll()
      if (node !in pts) {
        pts.add(node)
        btQueue.addAll(srcs[node] ?: emptyList())
      }
    }

    return pts.map{ it.pos }.toSet().size
  }

//  override fun part1(input: Day16In): Any? {
//    val g = Graph<Pair<Vec2i, Vec2i>, Int>(
//      edgeFn = { (pos, dir) ->
//        listOf(
//          1000 to (pos to dir.rotateCcw()),
//          1000 to (pos to dir.rotateCw()),
//        ) +
//          if (input[pos + dir] in "SE.") { listOf(1 to ((pos + dir) to dir)) } else emptyList()
//      },
//      weightFn = { it }
//    )
//
//    val startPos = input.coords.first { input[it] == 'S' }
//    val endPos = input.coords.first { input[it] == 'E' }
//
//    val shortest = listOf(
//      g.shortestPath(startPos to Vec2i.RIGHT, endPos to Vec2i.UP),
//      g.shortestPath(startPos to Vec2i.RIGHT, endPos to Vec2i.LEFT),
//      g.shortestPath(startPos to Vec2i.RIGHT, endPos to Vec2i.RIGHT),
//      g.shortestPath(startPos to Vec2i.RIGHT, endPos to Vec2i.DOWN),
//    ).minBy { it.first }
//    return shortest.first
//  }
//
//  override fun part2(input: Day16In): Any? {
//    val g = Graph<Pair<Vec2i, Vec2i>, Int>(
//      edgeFn = { (pos, dir) ->
//        listOf(
//          1000 to (pos to dir.rotateCcw()),
//          1000 to (pos to dir.rotateCw()),
//        ) +
//          if (input[pos + dir] in "SE.") { listOf(1 to ((pos + dir) to dir)) } else emptyList()
//      },
//      weightFn = { it }
//    )
//
//    val startPos = input.coords.first { input[it] == 'S' }
//    val endPos = input.coords.first { input[it] == 'E' }
//
//    val paths = listOf(
//      g.shortestPaths(startPos to Vec2i.RIGHT, endPos to Vec2i.UP),
////      g.shortestPaths(startPos to startDir, endPos to Vec2i.RIGHT),
////      g.shortestPaths(startPos to startDir, endPos to Vec2i.DOWN),
////      g.shortestPaths(startPos to startDir, endPos to Vec2i.LEFT)
//    )
//
//    val minCost = paths.minOf { it.first }
//    val pts = paths.filter { it.first == minCost }
//      .flatMap { it.second.map { it.first } }
//      .toSet()
//
//    println(minCost)
//
//    return pts.size
//  }
}
