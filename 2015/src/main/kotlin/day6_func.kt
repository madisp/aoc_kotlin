import utils.Grid
import utils.Parser
import utils.Solution
import utils.Vec2i
import utils.cut
import utils.map
import utils.mapItems

fun main() {
  Day6Func.run()
}

object Day6Func : Solution<List<Day6Func.Opcode>>() {
  override val name = "day6"
  override val parser = Parser.lines.mapItems(Opcode::parse)

  enum class Insn(val desc: String, val applyBool: (Int) -> Int, val applyNumeric: (Int) -> Int) {
    on("turn on", { 1 }, { it + 1 }),
    off("turn off", { 0 }, { maxOf(it - 1, 0) }),
    toggle("toggle", { if (it == 0) 1 else 0 }, { it + 2 });

    companion object {
      fun parse(input: String): Pair<Insn, String> {
        val insn = Insn.values().first { input.startsWith(it.desc) }
        return insn to input.substring(insn.desc.length + 1)
      }
    }
  }

  data class Opcode(val insn: Insn, val p1: Vec2i, val p2: Vec2i) {
    fun apply(grid: Grid, part2: Boolean): Grid {
      val xr = minOf(p1.x, p2.x) .. maxOf(p1.x, p2.x)
      val yr = minOf(p1.y, p2.y) .. maxOf(p1.y, p2.y)
      return grid.map { (x, y), v ->
        if (x in xr && y in yr) {
          if (part2) insn.applyNumeric(v) else insn.applyBool(v)
        } else v
      }
    }

    companion object {
      fun parse(line: String): Opcode {
        val (insn, reminder) = Insn.parse(line)
        val (p1, p2) = reminder.cut(" through ")
        val (p1x, p1y) = p1.cut(",").map { it.toInt() }
        val (p2x, p2y) = p2.cut(",").map { it.toInt() }
        return Opcode(insn, Vec2i(p1x, p1y), Vec2i(p2x, p2y))
      }
    }
  }

  override fun part1(input: List<Opcode>): Int {
    val grid = Grid(1000, 1000) { 0 }
    return input.fold(grid) { it, op -> op.apply(it, false) }
      .cells
      .count { (_, v) -> v == 1 }
  }

  override fun part2(input: List<Opcode>): Int {
    val grid = Grid(1000, 1000) { 0 }
    return input.fold(grid) { it, op -> op.apply(it, true) }
      .cells
      .sumOf { (_, v) -> v }
  }
}
