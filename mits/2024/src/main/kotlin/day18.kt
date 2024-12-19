import utils.*

fun main() {
  Day18.run()
}

typealias Day18In = Grid<Char>

object Day18 : Solution<Day18In>() {
  override val name = "day18"
  override val parser: Parser<Day18In> = Parser.charGrid

  override fun part1(input: Day17In): String {
    val w = input.width / 5 + 1
    val h = input.height / 3

    val pristine = "|==||  ||==|"
    val can = (0..2).flatMap { y -> (0..3).map { x -> Vec2i(x, y) } }

    val out = StringBuilder()

    for (y in 0 until h) {
      out.append("|")
      val borken = mutableListOf<Int>()
      for (x in 0 until w) {
        val pts = can.map { input[it + Vec2i(x * 5, y * 3)] }
        if (pts.joinToString("") != pristine) {
          borken += (x + 1)
        }
      }
      out.append(borken.joinToString(", "))
      out.append("|")
    }

    return out.toString()
  }
}
