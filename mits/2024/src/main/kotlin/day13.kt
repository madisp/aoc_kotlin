import utils.Parser
import utils.Point3i
import utils.Solution
import utils.Vec2i
import utils.Vec4i
import utils.badInput

fun main() {
  Day13.run()
}

typealias Day13In = List<String>

object Day13 : Solution<Day13In>() {
  override val name = "day13"
  override val parser: Parser<Day13In> = Parser { it.split("----------------------------------").map { inv -> inv.trim() } }

  private fun getPieces(dimens: Vec2i): Vec4i {
    return when (dimens.y) {
      1 -> Point3i(0, 0, dimens.x)
      2 -> Point3i(0, dimens.x / 2, (dimens.x % 2) * 2)
      3 -> Point3i(
        dimens.x / 3,
        (dimens.x % 3 - 1).coerceAtLeast(0),
        (4 - dimens.x % 3) % 4
      )
      else -> badInput()
    }
  }

  override fun part1(input: Day13In): String {
    val dimens = input.map { it.split("\n").map { l -> l.trim().length } }.map {
      Vec2i(it.max(), it.size)
    }
    val answ = dimens.fold(Point3i(0, 0, 0)) { acc, it -> acc + getPieces(it) }
    return "${answ.x}, ${answ.y}, ${answ.z}"
  }
}
