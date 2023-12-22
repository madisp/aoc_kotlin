package utils

import java.util.ArrayDeque
import java.util.PriorityQueue
import java.util.concurrent.atomic.AtomicLong

class Graph<Node : Any, Edge: Any>(
  val edgeFn: (Node) -> List<Pair<Edge, Node>>,
  val weightFn: (Edge) -> Int = { 1 },
  val nodes: Set<Node>? = null,
) {

  fun dfs(start: Node, visit: (Node) -> Boolean): List<Node> {
    val queue = ArrayDeque<Pair<Node, Int>>()
    queue.add(start to 0)
    val backtrace = mutableListOf(start)

    while (queue.isNotEmpty()) {
      val (node, depth) = queue.removeLast()

      if (backtrace.size < depth + 1) {
        backtrace.add(node)
      } else {
        backtrace[depth] = node
      }

      if (visit(node)) {
        return backtrace.take(depth + 1)
      }

      edgeFn(node).forEach { (_, nextNode) ->
        queue.add(nextNode to depth+1)
      }
    }

    return emptyList()
  }

  fun shortestPath(start: Node, end: Node, heuristic: (Node) -> Int = { _ -> 0 }): Pair<Int, List<Node>> {
    return shortestPath(start, heuristic = heuristic, end = { it == end })
  }

  fun shortestPath(
    start: Node,
    heuristic: (Node) -> Int = { _ -> 0 },
    end: (Node) -> Boolean,
  ): Pair<Int, List<Node>> {
    val queue = PriorityQueue<Pair<Node, Int>>(compareBy { it.second })
    queue.add(start to 0)
    val src = mutableMapOf<Node, Node?>(start to null)
    val cost = mutableMapOf(start to 0)

    val counter = AtomicLong(0)

    while (queue.isNotEmpty()) {
      val (node, currentRisk) = queue.remove()

      if (counter.incrementAndGet() % 1000000 == 0L) {
        println("--- states ---")
        println("Visited ${counter.get()} nodes")
        println("Current node: cost=$currentRisk node=$node heuristic=${heuristic(node)}")
      }

      if (end(node)) {
        val backtrace = generateSequence(node) { src[it] }.toList().reversed()
        return cost[node]!! to backtrace
      }

      edgeFn(node).forEach { (edge, nextNode) ->
        val newNextCost = cost[node]!! + weightFn(edge)
        val nextCost = cost[nextNode]
        if (nextCost == null || newNextCost < nextCost) {
          cost[nextNode] = newNextCost
          src[nextNode] = node
          queue.add(nextNode to (newNextCost + heuristic(nextNode)))
        }
      }
    }

    throw IllegalStateException("No path from $start to $end")
  }

  fun shortestTour(): Int {
    if (nodes == null) throw IllegalStateException("Cannot compute tour without nodes")
    return nodes
      .mapNotNull { shortestTour(it, nodes - it) }
      .minOrNull()!!
  }

  fun longestTour(): Int {
    if (nodes == null) throw IllegalStateException("Cannot compute tour without nodes")
    return nodes
      .mapNotNull { longestTour(it, nodes - it) }
      .maxOrNull()!!
  }

  private fun shortestTour(from: Node, nodes: Set<Node>): Int? {
    if (nodes.isEmpty()) return 0

    return edgeFn(from)
      .filter { (_, node) -> node in nodes }
      .map { (edge, node) -> edge to shortestTour(node, nodes - node) }
      .filter { (_, tour) -> tour != null }
      .minOfOrNull { (edge, tour) -> weightFn(edge) + tour!! }
  }

  private fun longestTour(from: Node, nodes: Set<Node>): Int? {
    if (nodes.isEmpty()) return 0

    return edgeFn(from)
      .filter { (_, node) -> node in nodes }
      .map { (edge, node) -> edge to longestTour(node, nodes - node) }
      .filter { (_, tour) -> tour != null }
      .maxOfOrNull { (edge, tour) -> weightFn(edge) + tour!! }
  }
}
