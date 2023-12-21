import utils.Grid
import utils.MutableGrid
import utils.Parser
import utils.Solution
import utils.Vec2i
import utils.toMutable

fun main() {
  Day21.run(skipTest = true)
}

object Day21 : Solution<Grid<Char>>() {
  override val name = "day21"
  override val parser = Parser.charGrid

  override fun part1() = solve(64)
  override fun part2() = solve(26501365)

  enum class TileKind(val start: Vec2i) {
    // init square
    INIT(Vec2i(65, 65)),
    // cardinal directions
    UP(Vec2i(65, 130)),
    RIGHT(Vec2i(0, 65)),
    DOWN(Vec2i(65, 0)),
    LEFT(Vec2i(130, 65)),
    // diagonal directions
    LU(Vec2i(130, 130)),
    RU(Vec2i(0, 130)),
    LD(Vec2i(130, 0)),
    RD(Vec2i(0, 0));
  }

  data class TileInfo(
    val initialCounts: List<Int>,
    val repeats: List<Int>,
  )

  /**
   * Simulate one step of cellular automata based on [prev], output is fed into [g]
   *
   * @return the number of "on" states in output
   */
  private fun simulate(g: MutableGrid<Char>, prev: Grid<Char>): Int {
    var count = 0
    g.coords.forEach { p ->
      if (g[p] != '#') {
        if (p.adjacent.any { it in prev && prev[it] == 'O' }) {
          g[p] = 'O'
          count++
        } else {
          g[p] = '.'
        }
      }
    }
    return count
  }

  /**
   * Evolve a tile init condition as indicated by [kind] into
   * a repeating steady state.
   */
  private fun evolve(kind: TileKind): TileInfo {
    val out = mutableListOf<Int>()

    val start = input.coords.first { input[it] == 'S' }

    var a = input.toMutable()
    var b = input.toMutable()

    b[start] = '.'
    b[kind.start] = 'O'

    var c2 = Integer.MIN_VALUE
    var c1 = Integer.MIN_VALUE

    while (true) {
      val count = simulate(a, b)
      a = b.also { b = a }
      if (count == c2) {
        break
      }
      c2 = c1
      c1 = count
      out.add(count)
    }

    return TileInfo(out, listOf(c2, c1))
  }

  private fun getCount(info: TileInfo, step: Long): Int {
    if (step == 0L) {
      return 1
    }
    if (step - 1 < info.initialCounts.size) {
      return info.initialCounts[(step - 1).toInt()]
    } else {
      val off = step - 1 - info.initialCounts.size
      return info.repeats[(off % 2).toInt()]
    }
  }

  fun solve(steps: Long): Long {
    val tiles = mutableListOf(
      Triple(TileKind.INIT, 0L, 1)
    )

    // spawn cardinals
    val cardinals = listOf(TileKind.UP, TileKind.LEFT, TileKind.DOWN, TileKind.RIGHT)
    for (i in 66 .. steps step 131) {
      cardinals.forEach {
        tiles.add(Triple(it, i, 1))
      }
    }

    val oldgens = (steps / 131) - 2

    var evenolds = 0L
    var oddolds = 0L

    val corners = listOf(TileKind.RU, TileKind.RD, TileKind.LD, TileKind.LU)

    var gen = 1
    for (i in 132 .. steps step 131) {
      if (gen <= oldgens) {
        if ((gen + 1) % 2L == 0L) {
          evenolds += gen
        } else {
          oddolds += gen
        }
      } else {
        // add gen manually
        corners.forEach { kind ->
          tiles.add(Triple(kind, i, gen))
        }
      }
      gen++
    }

    val tileInfos = TileKind.entries.associateWith { evolve(it) }

    val oldCount = corners.sumOf {
      val tileInfo = tileInfos[it]!!
      evenolds * getCount(tileInfo, 401) + oddolds * getCount(tileInfo, 400)
    }

    val cardinalCellCount = tiles.sumOf { (kind, birth, amount) ->
      getCount(tileInfos[kind]!!, steps - birth).toLong() * amount
    }

    return cardinalCellCount + oldCount
  }
}
