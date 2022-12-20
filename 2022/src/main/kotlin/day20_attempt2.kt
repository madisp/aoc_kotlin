import utils.Parser
import utils.Solution
import utils.badInput
import utils.mapItems

fun main() {
  Day20_2.run()
}

object Day20_2 : Solution<List<Int>>() {
  override val name = "day20"
  override val parser = Parser.lines.mapItems { it.toInt() }

  data class Item(
    val origIndex: Int,
    val value: Int,
  )

  override fun part1(input: List<Int>): Int {
    val items = MutableList(input.size) { Item(it, input[it]) }

    for (i in input.indices) {
      val curIndex = items.indexOfFirst { it.origIndex == i }
      val item = items[curIndex]

      var move = item.value
      while (move <= 0) {
        move += input.size
      }
      if (item.value < 0) {
        move -= 1 // backwards off-by-1
      }
      move %= input.size

      // swap forward N times
      var idx = curIndex
      repeat(move) {
        val swap = items[idx % input.size]
        items[idx % input.size] = items[(idx + 1) % input.size]
        items[(idx + 1) % input.size] = swap
        idx++
      }
    }

    val zi = items.indexOfFirst { it.value == 0 }

    return items[(zi + 1000) % input.size].value + items[(zi + 2000) % input.size].value + items[(zi + 3000) % input.size].value
  }
}
