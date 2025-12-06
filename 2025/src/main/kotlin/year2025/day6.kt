package year2025

import utils.Grid
import utils.Parser
import utils.Solution
import utils.createGrid

typealias Day6In = List<Pair<Grid<Char>, Day6.Oper>>

fun main() {
  Day6.run()
}

object Day6 : Solution<Day6In>() {
  override val name = "day6"
  override val parser: Parser<Day6In> = Parser.charGrid.map { g ->
    val splits = listOf(-1) + (0 until g.width).filter { x -> g[x].values.all { it == ' ' } } + g.width
    splits.windowed(2).map { (start, end) ->
      val oper = Oper.entries.first { it.char == g.rows.last().values.drop(start + 1).first { c -> c != ' '} }
      createGrid(end - (start + 1), g.height - 1) { (x, y) ->
        g[start + x + 1][y]
      } to oper
    }
  }

  enum class Oper(val char: Char, val unit: Long, val fn: (Long, Long) -> Long) {
    PLUS('+', 0L, { a, b -> a + b }),
    TIMES('*', 1L, { a, b -> a * b});
    fun applyTo(vals: Collection<Long>) = vals.fold(unit, fn)
  }

  private fun nums(g: Collection<Collection<Char>>): List<Long> {
    return g.map {
      it.joinToString("").replace(" ", "").toLong()
    }
  }

  override fun part1(input: Day6In): Long {
    return input.sumOf { (g, oper) ->
      oper.applyTo(nums(g.rows.map { it.values }))
    }
  }

  override fun part2(input: Day6In): Long {
    return input.sumOf { (g, oper) ->
      oper.applyTo(nums(g.columns.map { it.values }))
    }
  }
}
