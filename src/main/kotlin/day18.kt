import utils.Parser
import utils.mapItems

fun main() {
  Day18Func.run()
}

object Day18Func : Solution<List<Day18Func.Tree>> {
  override val name = "day18"
  override val parser = Parser.lines.mapItems { Tree.from(it).first }

  override fun part1(input: List<Tree>): Int {
    val sum = input.reduce { acc, tree -> (acc + tree).reduce() }
    return sum.magnitude
  }

  override fun part2(input: List<Tree>): Int {
    return input.flatMap { a ->
      input.flatMap { b ->
        listOf((a + b).reduce(), (b + a).reduce())
      }
    }.maxOf { it.magnitude }
  }

  sealed interface Tree {
    operator fun plus(other: Tree) = Branch(this, other)

    val magnitude: Int

    fun explode(depth: Int = 4): Pair<Tree, Pair<Int?, Int?>?>
    fun split(): Pair<Tree, Boolean>
    fun reduceOnce(): Pair<Tree, Boolean>

    fun reduce(): Tree {
      return generateSequence(this) { it.reduceOnce().takeIf { it.second }?.first }
        .last()
    }

    fun addFirstLeft(value: Int): Pair<Tree, Int?>
    fun addFirstRight(value: Int): Pair<Tree, Int?>

    companion object {
      fun from(string: String): Pair<Tree, Int> {
        if (string[0].isDigit()) {
          // find last digit
          val len = minOf(
              string.indexOf(',').takeIf { it > 0 } ?: Integer.MAX_VALUE,
            string.indexOf(']').takeIf { it > 0 } ?: Integer.MAX_VALUE,
              string.length
            )
          return Node(string.substring(0, len).toInt()) to len
        } else if (string[0] != '[') {
          bad(string, 0)
        }

        val (left, leftSize) = from(string.substring(1))
        if (string[1 + leftSize] != ',') {
          bad(string, 1+ leftSize)
        }

        val (right, rightSize) = from(string.substring(leftSize + 2))

        return Branch(left, right) to leftSize + rightSize + 3
      }

      private fun bad(string: String, col: Int) {
        throw IllegalArgumentException("Bad input at '${string.substring(col..minOf(col + 6, string.length))}'")
      }
    }
  }
  data class Node(val value: Int): Tree {
    override val magnitude = value

    override fun explode(depth: Int) = this to null

    override fun split(): Pair<Tree, Boolean> {
      return if (value >= 10) {
        Branch(Node(value / 2), Node((value + 1) / 2)) to true
      } else this to false
    }

    override fun addFirstLeft(value: Int): Pair<Tree, Int?> {
      return Node(this.value + value) to null
    }

    override fun addFirstRight(value: Int): Pair<Tree, Int?> {
      return Node(this.value + value) to null
    }

    override fun reduceOnce(): Pair<Tree, Boolean> {
      return this to false
    }

    override fun toString() = value.toString()
  }
  data class Branch(val left: Tree, val right: Tree): Tree {
    override val magnitude: Int get() = left.magnitude * 3 + right.magnitude * 2

    override fun reduceOnce(): Pair<Tree, Boolean> {
      val (exploded, pair) = explode()
      if (pair != null) {
        return exploded to true
      }

      return split()
    }

    override fun explode(depth: Int): Pair<Tree, Pair<Int?, Int?>?> {
      if (depth == 0) {
        if (left !is Node || right !is Node) {
          throw IllegalStateException("Unexpected branch $this at this depth!")
        }
        return Node(0) to (left.value to right.value)
      } else {
        val (newLeft, leftPair) = left.explode(depth - 1)
        if (leftPair != null) {
          if (leftPair.second != null) {
            val (newRight, rightValue) = right.addFirstLeft(leftPair.second!!)
            return Branch(newLeft, newRight) to leftPair.copy(second = rightValue)
          }
          return Branch(newLeft, right) to leftPair
        }

        val (newRight, rightPair) = right.explode(depth - 1)
        if (rightPair != null) {
          if (rightPair.first != null) {
            val (newLeft, leftValue) = left.addFirstRight(rightPair.first!!)
            return Branch(newLeft, newRight) to rightPair.copy(first = leftValue)
          }
          return Branch(left, newRight) to rightPair
        }

        return this to null
      }
    }
    override fun split(): Pair<Tree, Boolean> {
      val (newLeft, leftDidSplit) = left.split()
      if (leftDidSplit) {
        return Branch(newLeft, right) to true
      }

      val (newRight, rightDidSplit) = right.split()
      return Branch(left, newRight) to rightDidSplit
    }
    override fun addFirstLeft(value: Int): Pair<Tree, Int?> {
      val added = left.addFirstLeft(value)
      return Branch(added.first, right) to added.second
    }

    override fun addFirstRight(value: Int): Pair<Tree, Int?> {
      val added = right.addFirstRight(value)
      return Branch(left, added.first) to added.second
    }

    override fun toString() = "[$left,$right]"
  }
}
