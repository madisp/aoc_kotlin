import utils.Grid
import utils.MutableGrid
import utils.Parser
import utils.Solution
import utils.Vec2i
import utils.createGrid
import utils.toMutable
import java.util.BitSet

fun main() {
  Day21.run(skipTest = true)
}

object Day21 : Solution<Grid<Char>>() {
  override val name = "day21"
  override val parser = Parser.charGrid

  operator fun BitSet.set(p: Vec2i, value: Boolean) {
    this.set(p.y * input.width + p.x, value)
  }

  operator fun BitSet.get(p: Vec2i) {
    this.get(p.y * input.width + p.x)
  }

  override fun part1(): Any {
    val steps = 65 + 66*7

    val expandfac = 11

    val w = input.width * expandfac
    val h = input.height * expandfac
    val ig = createGrid(w, h) {
      if (input[it.x % input.width][it.y % input.height] != '#') '.' else '#'
    }

    var a = ig.toMutable()
    var b = ig.toMutable()

    val start = Vec2i(input.width * (expandfac / 2) + 65, input.height * (expandfac / 2) + 65)
    a[start] = 'O'
    b[start] = 'O'

    repeat(steps) { step ->
      simulate(a, b)
      a = b.also { b = a }
    }

//    println(b.debugString)

    for (xtile in 0 until expandfac) {
      for (ytile in 0 until expandfac) {
        val count = input.coords.count { b[it + Vec2i(xtile * input.width, ytile * input.height)] == 'O' }
        if (count > 0) {
          println("---")
          println("tile[$xtile][$ytile] = $count")
//          println("Set coord:")
//          println(input.coords.first { b[it + Vec2i(xtile * input.width, ytile * input.height)] == 'O' })
        }
      }
    }

    println(part2(steps.toLong() + 1))

    return b.values.count { it == 'O' }

//    val grids = Array(steps) {
//      createGrid<BitSet>(w, h) { BitSet(w * h) }
//    }
//
//    // fill 0 by hand
//    ig.cells.filter { (_, c) -> c == '.' || c == 'S' }.forEach { (p, _) ->
//      grids[0][p][p] = true
//    }
//
//    for (i in 1 until steps) {
//      val g = grids[i]
//      for (p in ig.coords) {
//        p.adjacent.filter { it in ig && ig[it] != '#' }.map { grids[i - 1][it] }.forEach { p2 ->
//          g[p].or(p2)
//        }
//      }
//    }
//
//    val start = ig.cells.first { (_, c) -> c == 'S' }.first
//
//    return grids.last()[start].toLongArray().sumOf { java.lang.Long.bitCount(it) }
  }

//  override fun part1() = getCount(TileKind.INIT, 64)

  val evos: Map<TileKind, Pair<List<Int>, Pair<Int, Int>>> by lazy {
    TileKind.entries.associateWith {
      val (evo, steady) = evolve(it)
      (evo to steady)
    }
  }

  private fun getCount(kind: TileKind, step: Long): Int {
    if (step == 0L) return 1
    val (evo, steady) = evos[kind]!!
    if (step - 1 < evo.size) {
      return evo[(step - 1).toInt()] // guaranteed to fit
    } else {
      val off = step - 1 - evo.size
      return if (off % 2 == 0L) {
        steady.first
      } else {
        steady.second
      }
    }
  }

  private fun simulate(g: MutableGrid<Char>, prev: Grid<Char>) {
    g.coords.forEach { p ->
      if (g[p] != '#') {
        if (p.adjacent.any { it in prev && prev[it] == 'O' }) {
          g[p] = 'O'
        } else {
          g[p] = '.'
        }
      }
    }
  }

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

  private fun evolve(kind: TileKind): Pair<List<Int>, Pair<Int, Int>> {
    val out = mutableListOf<Int>()

    val start = input.coords.first { input[it] == 'S' }

    var a = input.toMutable()
    var b = input.toMutable()

    a[start] = '.'
    b[start] = '.'

    a[kind.start] = 'O'
    b[kind.start] = 'O'

    var c_4 = Integer.MIN_VALUE
    var c_3 = Integer.MIN_VALUE
    var c_2 = Integer.MIN_VALUE
    var c_1 = Integer.MIN_VALUE

    while (true) {
      simulate(a, b)
      a = b.also { b = a }

      val count = b.values.count { it == 'O' }

      if (count == c_4) {
        break
      }

      c_4 = c_3
      c_3 = c_2
      c_2 = c_1
      c_1 = count

      out.add(count)
    }

    return out to (c_2 to c_1)
  }

  override fun part2(): Number {
    return part2(26501365 + 1)
    //            1320874
  }

  fun part2(steps: Long): Number {

    // legend: I - init rhombus, URDL - up-down-left-right pieces, C - corner pieces (4 different kinds)

    // .....
    // .....
    // ..I.. // init age always _steps_
    // .....
    // .....

    // after 65 steps:
    // .....
    // ..U..  // cardinal age = (steps + 65) % 130
    // .LIR.
    // ..D..
    // .....

    // after 65 steps:
    // .....
    // .CUC.  // cardinal age = (steps + 65) % 130
    // .LIR.
    // .CDC.
    // .....

    // after 65 steps:
    // ..U..
    // .CUC.
    // LLFRR
    // .CDC.
    // ..D..

    // after 65 steps:
    // .CUC.
    // CCUCC
    // LLFRR
    // CCDCC
    // .CDC.

    // after 65 steps:
    // ...U...
    // ..CUC..
    // .CCUCC.
    // LLLFRRR
    // .CCDCC.
    // ..CDC..
    // ...D...

    // after 65 steps:
    // ..CUC..
    // .CCUCC.
    // CCCUCCC
    // LLLFRRR
    // CCCDCCC
    // .CCDCC.
    // ..CDC..

    // corners
    // 0 evo: 0
    // 1 evo: 0
    // 2 evo: 4   // 1       x 4
    // 3 evo: 4   // 1       x 4
    // 4 evo: 12  // (1+2)   x 4
    // 4 evo: 12  // (1+2)   x 4
    // 5 evo: 24  // (1+2+3) x 4
    // 5 evo: 24  // (1+2+3) x 4

    // (n) * (n+1) / 2 ?
    // n=1 -> 1
    // n=2 -> 3
    // n=3 -> 6

//    val steps = 26501365L
//    val tiles = TileKind.entries.map {
//      it to getCount(it, steps - 1)
//    }

    val evolutions = (steps) / 65

    val tiles = mutableListOf(
      TileKind.INIT to 0L
    )

    // gen1 cardinals spawn at: 66*1 - 0 =  66
    // gen2 cardinals spawn at: 66*3 - 1 = 197 (prev + 131)
    // gen3 cardinals spawn at: 66*5 - 2 = 328 (prev + 131)
    // gen4 cardinals spawn at: 66*7 - 3 = 459 (prev + 131)

    // spawn cardinals
    val cardinals = listOf(TileKind.UP, TileKind.LEFT, TileKind.DOWN, TileKind.RIGHT)
    for (i in 66 until steps step 131) {
//      println("Added 4 cardinals at $i")
      cardinals.forEach {
        tiles.add(it to i)
      }
    }

    // gen1 corners spawn at: 65 + 66*1 + 1   (4) ?
    // gen2 corners spawn at: 65 + 66*3   (8)
    // gen3 corners spawn at: 65 + 66*5 - 1 ? (12)
    // gen4 corners spawn at: 65 + 66*7 - 2 ? (16) BUGGA
    // gen5 corners spawn at: 65 + 66*9 - 3 ? (20)

    // spawn corners

    // corners start spawning at 132
    // at every 131 inc more and more are added (+4 every time)
    // some spawn on evens, some on odds
    // corner cycle length is 262
    // we need to:
    // - know how many gens of corners are there overall
    // - count last 2 gens manually
    // - add all other corners either as odds or as evens and sum them up

    val cornergens = (steps - 1) / 131
    val oldgens = (steps - 1 - 262) / 131

    var evenolds = 0L
    var oddolds = 0L

    val corners = listOf(TileKind.RU, TileKind.RD, TileKind.LD, TileKind.LU)

    // 609298711563666

    var gen = 1
    for (i in 132 until steps step 131) {
      if (gen <= oldgens) {
        if ((gen + 1) % 2L == 0L) {
          evenolds += gen
        } else {
          oddolds += gen
        }
      } else {
        // add gen manually
        repeat(gen) {
          corners.forEach { kind ->
            tiles.add(kind to i)
          }
        }
      }
      gen++
    }

    val oldCount = corners.sumOf {
      evenolds * getCount(it, 401) + oddolds * getCount(it, 400)
    }

    // 10231120201
    // 10231221350

    println("$evenolds old even gens, $oddolds old odd gens, oldcount = $oldCount")
    println("$cornergens total corner gens")

    // 1 old gens -> 4 even, 0 odd
    // 2 old gens -> 4 even, 4 odd
    // 3 old gens -> 8 even, 4 odd
    // 4 old gens -> 8 even, 8 odd

    // old gens:
    // gen1 - even
    // gen2 - odd
    // gen3 - even
    // ...

//
//    for (i in 132 until steps step 131) {
//      val count = ((i - 132) / 131 + 1).toInt()
//      println("Added $count corners at $i")
//      repeat(count) {
//        corners.forEach { kind ->
//          tiles.add(kind to i)
//        }
//      }
//    }

    /*
     8714
     part1: 8691 (2.425428417s)
     */
//    tiles.forEach { (kind, birth) ->
//      val count = if (birth >= steps) 0L else getCount(kind, steps - birth - 1).toLong()
//      println("$kind spawned at $birth, count=$count")
//    }

//    4182
//    part1: 4202 (32.843703708s)

    val cardinalCellCount = tiles.sumOf { (kind, birth) -> if (birth >= steps) 0L else getCount(kind, steps - birth - 1).toLong() }

    return cardinalCellCount + oldCount

    // init is always full
    // cardinals evolve for 197 steps
    // diagonals evolve for 262 steps

//    val counts = mapOf(
//      TileKind.INIT to 1,
//      TileKind.UP to (evolutions + 1) / 2,
//      TileKind.LEFT to (evolutions + 1) / 2,
//      TileKind.RIGHT to (evolutions + 1) / 2,
//      TileKind.DOWN to (evolutions + 1) / 2,
//      TileKind.LD to (evolutions / 2) * ((evolutions / 2) + 1) / 2,
//      TileKind.LU to (evolutions / 2) * ((evolutions / 2) + 1) / 2,
//      TileKind.RD to (evolutions / 2) * ((evolutions / 2) + 1) / 2,
//      TileKind.RU to (evolutions / 2) * ((evolutions / 2) + 1) / 2,
//    )
//
//    return tiles.sumOf { (kind, num) ->
//      num.toBigInteger() * counts[kind]!!.toBigInteger()
//    }
  }
}
