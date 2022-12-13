import utils.Parser
import utils.Solution
import utils.badInput

fun main() {
  Day13Imp.run()
}

object Day13Imp : Solution<List<Pair<Day13Imp.Fragment.Packet, Day13Imp.Fragment.Packet>>>() {
  override val name = "day13"

  sealed class Fragment : Comparable<Fragment> {
    class Packet(val fragments: List<Fragment>): Fragment() {
      override fun compareTo(other: Fragment): Int {
        return if (other is Packet) {
          val itemCmp = fragments.zip(other.fragments).map { it.first.compareTo(it.second) }.firstOrNull { it != 0 }
          itemCmp ?: fragments.size.compareTo(other.fragments.size)
        } else {
          compareTo(Packet(listOf(other)))
        }
      }
    }
    class Value(val value: Int): Fragment() {
      override fun compareTo(other: Fragment): Int {
        return if (other is Value) {
          value.compareTo(other.value)
        } else {
          Packet(listOf(this)).compareTo(other)
        }
      }
    }

    companion object {
      fun parse(input: String): Pair<Fragment, Int> {
        if (input.startsWith("[")) {
          var read = 1
          val items = mutableListOf<Fragment>()
          while (read < input.length && input[read] != ']') {
            val (item, len) = parse(input.substring(read))
            items.add(item)
            read += len
            if (input[read] == ',') {
              // advance by one past the splitting comma
              read++
            }
          }
          return Packet(items) to read + 1 // add the trailing `]`
        }

        val end = input.indexOfFirst { it == ',' || it == ']' }.takeIf { it >= 0 } ?: input.length
        return Value(input.substring(0, end).toInt()) to end
      }
    }
  }
  override val parser = Parser { input ->
    input.split("\n\n").map { packetPair ->
      val (l, r) = packetPair.trim().split("\n").map { Fragment.parse(it.trim()).first }
      if (l !is Fragment.Packet) badInput()
      if (r !is Fragment.Packet) badInput()
      l to r
    }
  }

  override fun part1(input: List<Pair<Fragment.Packet, Fragment.Packet>>): Int {
    return input.mapIndexed { index, pair -> if (pair.first < pair.second) (index + 1) else 0 }.sum()
  }

  override fun part2(input: List<Pair<Fragment.Packet, Fragment.Packet>>): Int {
    val dividers = listOf(Fragment.parse("[[2]]"), Fragment.parse("[[6]]")).map { it.first }
    val packets = (input.flatMap { listOf(it.first, it.second) } + dividers).sorted()
    return packets.indices.filter { i -> packets[i] in dividers }
      .also { require(it.size == 2) }
      .map { it + 1 }
      .reduce { a, b -> a * b }
  }
}
