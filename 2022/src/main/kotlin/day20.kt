import utils.Parser
import utils.Solution
import utils.badInput
import utils.mapItems

fun main() {
  Day20.run(skipTest = true)
}

class Node(val value: Int, var head: Boolean = false) {
  lateinit var prev: Node
  lateinit var next: Node
}

object Day20 : Solution<List<Int>>() {
  override val name = "day20"
  override val parser = Parser.lines.mapItems { it.toInt() }

  private fun move(node: Node, amount: Int) {
    if (amount == 0) {
      return // nothing to do
    }

    var n = node
    if (amount > 0) {
      repeat(amount) {
        n = n.next
      }
    } else {
      badInput()
//      repeat(amount.absoluteValue + 1) {
//        n = n.prev
//      }
    }

    if (n == node) {
      return // nothing to do
    }

    if (node.head) {
      node.next.head = true
      node.head = false
    } else if (n.head) {
      n.head = false
      node.head = true
    }

    // remove node from orig place
    node.prev.next = node.next
    node.next.prev = node.prev
    // insert node after n
    val oldNext = n.next
    n.next = node
    oldNext.prev = node
    node.next = oldNext
    node.prev = n
  }

  private fun toArray(node: Node, sz: Int): IntArray {
    // find head
    var n = node
    while (!n.head) {
      n = n.next
    }
    val arr = IntArray(sz) { 0 }
    repeat(sz) { i ->
      arr[i] = n.value
      n = n.next
    }
    return arr
  }

  override fun part1(input: List<Int>): Any? {
    val nodes = Array<Node?>(input.size) { null }

    val head = Node(input.first(), true)
    nodes[0] = head

    var prev = head
    for (i in 1 until input.size) {
      prev.next = Node(input[i])
      nodes[i] = prev.next
      prev.next.prev = prev
      prev = prev.next
    }
    // prev is now tail
    prev.next = head
    head.prev = prev

    nodes.filterNotNull().also { if (it.size != input.size) badInput() }.forEachIndexed { i, node ->
      // move node ahead or prev by X steps
      var move = node.value
      while (move <= 0) {
        move += input.size
      }
      if (node.value < 0) {
        move -= 1
      }
      println("$i: move ${node.value} / $move")
      println("before: ${toArray(node, input.size).mapIndexed { index, i -> "[${index.toString().padStart(4)}]${i.toString().padStart(6)}" }.joinToString(" ")}")
      move(node, move)
      println("after : ${toArray(node, input.size).mapIndexed { index, i -> "[${index.toString().padStart(4)}]${i.toString().padStart(6)}" }.joinToString(" ")}")
      println()
    }

    val arr = toArray(head, input.size)
//
    println("yarr")

    // find head
    var n = head
    var sum = 0

    while (n.value != 0) {
      n = n.next
    }
    repeat (3) {
      repeat(1000) {
        n = n.next
      }
      sum += n.value
    }

    return sum
  }
}
