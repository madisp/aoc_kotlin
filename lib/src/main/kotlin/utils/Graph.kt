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

  fun shortestPath(start: Node, end: Node, heuristic: (Node) -> Int = { _ -> 0 }): Pair<Int, List<Pair<Node, Edge?>>> {
    return shortestPath(start, heuristic = heuristic, end = { it == end })
  }

  fun shortestPaths(start: Node, end: Node, heuristic: (Node) -> Int = { _ -> 0 }): Pair<Int, Sequence<List<Pair<Node, Edge?>>>> {
    return shortestPaths(start, heuristic = heuristic, end = { it == end })
  }

  fun shortestPaths(
    start: Node,
    heuristic: (Node) -> Int = { _ -> 0},
    end: (Node) -> Boolean,
  ): Pair<Int, Sequence<List<Pair<Node, Edge?>>>> {
    val queue = PriorityQueue<Pair<Node, Int>>(compareBy { it.second })
    queue.add(start to 0)
    val src = mutableMapOf<Node, MutableSet<Pair<Node, Edge>>>()
    val cost = mutableMapOf(start to 0)

    val counter = AtomicLong(0)

    var endNode: Node? = null

    while (queue.isNotEmpty()) {
      val (node, currentRisk) = queue.remove()

      if (counter.incrementAndGet() % 1000000 == 0L) {
        println("--- states ---")
        println("Visited ${counter.get()} nodes")
        println("Current node: cost=$currentRisk node=$node heuristic=${heuristic(node)}")
      }

      if (end(node)) {
        endNode = node
      }

      edgeFn(node).forEach { (edge, nextNode) ->
        val newNextCost = cost[node]!! + weightFn(edge)
        val nextCost = cost[nextNode]
        if (nextCost == null || newNextCost <= nextCost) {
          cost[nextNode] = newNextCost
          if (nextCost != null && newNextCost < nextCost) {
            src[nextNode] = mutableSetOf()
          }
          src.getOrPut(nextNode) { mutableSetOf() }.add(node to edge)
          queue.add(nextNode to (newNextCost + heuristic(nextNode)))
        }
      }
    }

    if (endNode == null) {
      throw IllegalStateException("No path to the end node")
    }

    val bt = mutableListOf<Pair<Node, Edge?>>()
    suspend fun SequenceScope<List<Pair<Node, Edge?>>>.backwards(node: Node) {
      if (node == start) {
        yield(bt)
      } else {
        src[node]?.forEach {
          bt.add(it)
          backwards(it.first)
          bt.removeLast()
        }
      }
    }

    val bts = sequence {
      bt.add(endNode to null)
      backwards(endNode)
    }

    return cost[endNode]!! to bts
  }

  fun shortestPath(
    start: Node,
    heuristic: (Node) -> Int = { _ -> 0 },
    end: (Node) -> Boolean,
  ): Pair<Int, List<Pair<Node, Edge?>>> {
    val queue = PriorityQueue<Pair<Node, Int>>(compareBy { it.second })
    queue.add(start to 0)
    val src = mutableMapOf<Node, Pair<Node, Edge>?>(start to null)
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
        var btnode = src[node]
        val bt = mutableListOf<Pair<Node, Edge?>>(node to null)
        while (btnode != null) {
          bt += btnode
          btnode = src[btnode.first]
        }
        return cost[node]!! to bt
      }

      edgeFn(node).forEach { (edge, nextNode) ->
        val newNextCost = cost[node]!! + weightFn(edge)
        val nextCost = cost[nextNode]
        if (nextCost == null || newNextCost < nextCost) {
          cost[nextNode] = newNextCost
          src[nextNode] = node to edge
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
