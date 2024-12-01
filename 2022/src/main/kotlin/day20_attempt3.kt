import utils.Parser
import utils.Solution
import utils.mapItems
import kotlin.math.absoluteValue

fun main() {
  Day20_3.run()
}

object Day20_3 : Solution<List<Long>>() {
  override val name = "day20"
  override val parser = Parser.lines.mapItems { it.toLong() }

  data class Item(
    val origIndex: Int,
    val value: Long,
  )

  override fun part1(input: List<Long>): Any? {
    return solve(input, 1, 1)
  }

  override fun part2(input: List<Long>): Any? {
    return solve(input, 811589153, 10)
  }

  fun solve(input: List<Long>, key: Long, mixTimes: Int): Long {
    val items = MutableList(input.size) { Item(it, input[it] * key) }

    fun wrap(idx: Int): Int {
      return (input.size + idx) % input.size
    }

    fun wrapRepeats(r: Long): Int {
      return (r % (input.size - 1)).toInt() // why?
    }

    repeat(mixTimes) {
      for (i in input.indices) {
        val curIndex = items.indexOfFirst { it.origIndex == i }
        val item = items[curIndex]

        // swap forward N times
        if (item.value > 0) {
          var idx = curIndex

          repeat(wrapRepeats(item.value)) {
            val swap = items[wrap(idx)]
            items[wrap(idx)] = items[wrap(idx + 1)]
            items[wrap(idx + 1)] = swap
            idx = wrap(idx + 1)
          }
        } else {
          var idx = curIndex
          repeat(wrapRepeats(item.value.absoluteValue)) {
            val swap = items[wrap(idx - 1)]
            items[wrap(idx - 1)] = items[wrap(idx)]
            items[wrap(idx)] = swap
            idx = wrap(idx - 1)
          }
        }
      }
    }

    val zi = items.indexOfFirst { it.value == 0L }

    return items[(zi + 1000) % input.size].value + items[(zi + 2000) % input.size].value + items[(zi + 3000) % input.size].value
  }
}
