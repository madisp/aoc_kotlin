import utils.Grid
import utils.MutableGrid
import utils.Solution
import utils.createMutableGrid
import utils.toMutable

fun main() {
  Day18.run()
}

object Day18 : Solution<Grid<Char>>() {
  override val name = "day18"
  override val parser = Grid.chars(oobBehaviour = Grid.OobBehaviour.Default('.'))

  private const val STEPS = 100

  private fun simulate(prev: Grid<Char>, next: MutableGrid<Char>) {
    next.coords.forEach { p ->
        val neighbours = p.surrounding.count { prev[it] == '#' }
        if (prev[p] == '#') {
          next[p] = if (neighbours in 2 .. 3) '#' else '.'
        } else {
          next[p] = if (neighbours == 3) '#' else '.'
        }
      }
  }

  private fun solve(input: Grid<Char>, modify: MutableGrid<Char>.() -> Unit = {}): Int {
    var a = input.toMutable()
    var b = createMutableGrid<Char>(a.width, a.height, input.oobBehaviour) { '.' }

    repeat(STEPS) {
      simulate(a, b)
      b.modify()
      a = b.also {
        b = a
      }
    }

    return a.values.count { it == '#' }
  }

  override fun part1(input: Grid<Char>): Any? {
    return solve(input)
  }

  override fun part2(input: Grid<Char>): Any? {
    return solve(input) {
      // turn on the 4 corners
      corners.forEach { (p, _) -> this[p] = '#' }
    }
  }
}
