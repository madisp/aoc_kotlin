import utils.*

fun main() {
  Day22.run()
}

typealias Day22In = Pair<List<Vec4i>, Vec4i>

object Day22 : Solution<Day22In>() {
  override val name = "day22"
  override val parser: Parser<Day22In> = Parser.compound("Ressursid:",
    first = Parser.lines.map { it.drop(1) }.mapItems {
      val res = parseResources(it)
      Vec4i(res.wood, res.nails, res.rope, res.index)
    },
    second = Parser.lines.mapItems {
      it.cut(": ").let { it.first to it.second.toInt() }
    }.map {
      val map = it.toMap();
      Vec3i(map["puit"]!!, map["naelad"]!!, map["köis"]!!)
    }
  )

  @Parse("{index}: puit:{wood}, naelad:{nails}, köis:{rope}")
  data class Resources(
    val index: Int,
    val wood: Int,
    val nails: Int,
    val rope: Int,
  )

  private fun buildTraps(traps: List<Vec4i>, index: Int, res: Vec4i): Set<Int> {
    if (index >= traps.size) { return emptySet() }
    val nobuild = buildTraps(traps, index + 1, res)
    val trap = traps[index]
    if (res >= trap) {
      val build = buildTraps(traps, index + 1, res - trap) + index
      if (build.size >= nobuild.size) return build
    }
    return nobuild
  }

  override fun part1(input: Day22In): String {
    return buildTraps(input.first, 0, input.second).toList().sorted().joinToString(", ")
  }
}
