import utils.Parse
import utils.Parser
import utils.Solution
import utils.mapItems

fun main() {
  Day10.run()
}

typealias Day10In = List<Day10.Cuboid>

object Day10 : Solution<Day10In>() {
  override val name = "day10"
  override val parser: Parser<Day10In> = Parser.lines.map { lines -> lines.drop(1) }.mapItems { parseCuboid(it) }

  @Parse("{w}x{h}x{d}")
  data class Cuboid(
    val w: Int,
    val h: Int,
    val d: Int,
  )

  private fun maxHeight(base: Cuboid, cuboids: List<Cuboid>, memo: MutableMap<Cuboid, Int> = mutableMapOf()): Int {
    memo[base]?.let { return it }

    val max = cuboids
      .filter { it.w < base.w && it.h < base.h }
      .maxOfOrNull { maxHeight(it, cuboids, memo) }

    if (max != null) {
      memo[base] = base.d + max
      return base.d + max
    }

    return base.d
  }

  override fun part1(input: Day10In): Int {
    val rotations = input.flatMap {
      listOf(
        it,
        it.copy(w = it.h, h = it.w), // rotate horizontally

        it.copy(w = it.d, d = it.w), // flip on one side
        it.copy(w = it.h, h = it.d, d = it.w), // rotate horizontally and flip on one side

        it.copy(h = it.d, d = it.h), // flip on the other side
        it.copy(h = it.w, w = it.d, d = it.h), // rotate horizontally and flip on the other side
      )
    }.toSet().toList()

    return maxHeight(Cuboid(Int.MAX_VALUE, Int.MAX_VALUE, 0), rotations)
  }
}
