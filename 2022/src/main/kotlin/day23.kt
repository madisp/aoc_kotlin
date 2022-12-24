import utils.Grid
import utils.Parser
import utils.Solution
import utils.Vec2i
import utils.borderWith
import utils.createGrid
import utils.mapParser
import java.util.Collections
import kotlin.math.absoluteValue

fun main() {
  Day23.run()
}

object Day23 : Solution<Grid<Char>>() {
  override val name = "day23"
  override val parser = Parser { it.trim() }.mapParser(Parser.charGrid)

  enum class Direction(val vs: List<Vec2i>, val move: Vec2i) {
    NORTH((-1 .. 1).map { Vec2i(it, -1) }, Vec2i(0, -1)),
    SOUTH((-1 .. 1).map { Vec2i(it, 1) }, Vec2i(0, 1)),
    WEST((-1 .. 1).map { Vec2i(-1, it) }, Vec2i(-1, 0)),
    EAST((-1 .. 1).map { Vec2i(1, it) }, Vec2i(1, 0)),
  }

  fun makeRound(grid: Grid<Char>, round: Int): Grid<Char> {
    val moves = mutableMapOf<Vec2i, Vec2i>()
    val moveCounts = mutableMapOf<Vec2i, Int>()
    val elves = grid.coords.filter { grid[it] == '#' }

    for (elf in elves) {
      if (elf.surrounding.all { grid[it] == '.' }) {
        // do nothing
        moves[elf] = elf
        moveCounts[elf] = moveCounts.getOrDefault(elf, 0) + 1
        continue
      }

      val opts = Direction.values().toMutableList()
      Collections.rotate(opts, 0 - (round % 4))
      val dir = opts.firstOrNull { dir ->
        dir.vs.all { grid[elf + it] == '.' }
      }
      if (dir != null) {
        moves[elf] = elf + dir.move
        moveCounts[elf + dir.move] = moveCounts.getOrDefault(elf + dir.move, 0) + 1
      } else {
        // don't move
        moves[elf] = elf
        moveCounts[elf] = moveCounts.getOrDefault(elf, 0) + 1
      }
    }

    val invalidMoves = moveCounts.entries.filter { (_, count) -> count > 1 }.map { (coord, _) -> coord }.toSet()

    moves.keys.forEach {
      // reset to starting if invalid
      if (moves[it] in invalidMoves) {
        moves[it] = it
      }
    }

    val newElves = moves.values.toSet()

    require(newElves.size == elves.size)

    return createGrid(grid.width, grid.height) {
      if (it in newElves) '#' else '.'
    }
  }

  override fun part1(input: Grid<Char>): Int {
    var grid = input
    repeat(10) { round ->
      val (tl, br) = grid.bounds { it == '#' }
      if (tl.x == 0 || tl.y == 0 || br.x == grid.width - 1 || br.y == grid.height - 1) {
        // grow the grid to make room for automata
        grid = grid.borderWith('.')
      }
      grid = makeRound(grid, round)
    }

    val (tl, br) = grid.bounds { it == '#' }

    return (((tl.x - br.x).absoluteValue + 1) * (((tl.y - br.y).absoluteValue) + 1)) - grid.values.count { it == '#' }
  }

  override fun part2(input: Grid<Char>): Any? {
    var grid = input
    var round = 0
    while (true) {
      val (tl, br) = grid.bounds { it == '#' }
      if (tl.x == 0 || tl.y == 0 || br.x == grid.width - 1 || br.y == grid.height - 1) {
        // grow the grid to make room for automata
        grid = grid.borderWith('.')
      }
      val newGrid = makeRound(grid, round)
      round++

      // break if newGrid is the same as old grid
      val newElves = newGrid.cells.filter { (_, c) -> c == '#' }.map { (v, _) -> v }.toSet()
      val oldElves = grid.cells.filter { (_, c) -> c == '#' }.map { (v, _) -> v }.toSet()

      if ((oldElves - newElves).isEmpty()) {
        break
      }

      grid = newGrid
    }

    return round
  }
}
