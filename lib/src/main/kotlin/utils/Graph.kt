package utils

import java.util.PriorityQueue

class Graph<Node, Edge>(
  val edgeFn: (Node) -> List<Pair<Edge, Node>>,
  val weightFn: (Edge) -> Int,
  val nodes: Set<Node>? = null
) {
  fun shortestPath(start: Node, end: Node): Int {
    val visited = mutableSetOf<Node>()
    val queue = PriorityQueue<Pair<Node, Int>>(compareBy { it.second })
    queue.add(start to 0)

    while (queue.isNotEmpty()) {
      val (node, currentRisk) = queue.remove()
      if (node in visited) continue

      if (node == end) {
        return currentRisk
      }

      visited.add(node)

      edgeFn(node).forEach { (edge, nextNode) ->
        queue.add(nextNode to currentRisk + weightFn(edge))
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
