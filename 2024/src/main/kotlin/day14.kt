import utils.*
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.PrintWriter

fun main() {
//  Day14.run()
  for (k in 0 until 1000) {
    for (l in 0 until 1000) {
      if (103 * k - 101 * l == 37) {
        println("k=${k} l=${l}")
      }
    }
  }
}

typealias Day14In = List<Day14.Robot>

object Day14 : Solution<Day14In>() {
  override val name = "day14"
  override val parser: Parser<Day14In> = Parser.lines
    .mapItems { parseRobotstr(it) }
    .mapItems { Robot(Vec2l(it.px, it.py), Vec2l(it.vx, it.vy)) }

  data class Robot(val p: Vec2l, val v: Vec2l)
  @Parse("p={px},{py} v={vx},{vy}")
  data class Robotstr(val px: Long, val py: Long, val vx: Long, val vy: Long)

  override fun part1(): Int {
    val dimen = if (input.size < 20) Vec2l(11, 7) else Vec2l(101, 103)
    val newPos = input.map {
      it.copy(p = (it.p + (dimen * 100) + (it.v * 100)) % dimen)
    }

    val quadrants = listOf(
      // tl
      Vec2l(0, 0) to Vec2l(dimen.x / 2, dimen.y / 2),
      // tr
      Vec2l(dimen.x / 2 + 1, 0) to Vec2l(dimen.x, dimen.y / 2),
      // bl
      Vec2l(0, dimen.y / 2 + 1) to Vec2l(dimen.x / 2, dimen.y),
      // br
      Vec2l(dimen.x / 2 + 1, dimen.y / 2 + 1) to Vec2l(dimen.x, dimen.y),
    )

    val muls = quadrants.map { q ->
      newPos.count {
        it.p.x in q.first.x until q.second.x &&
          it.p.y in q.first.y until q.second.y
      }
    }

    return muls.fold(1) { acc, it -> acc * it }
  }

  fun mirror(p: Vec2l, dimen: Vec2l): Vec2l = Vec2l(dimen.x - p.x - 1, p.y)

  override fun part2(): Any? {
    val dimen = if (input.size < 20) Vec2l(11, 7) else Vec2l(101, 103)

    val attempts = if (input.size < 20) 1 else 1_000_000_000L

//    var normRobots = input.map {
//      it.copy(v = (it.v + dimen) % dimen)
//    }
    var normRobots = input

    val quadrants = listOf(
      // tl
      Vec2l(0, 0) to Vec2l(dimen.x / 2, dimen.y / 2),
      // tr
      Vec2l(dimen.x / 2 + 1, 0) to Vec2l(dimen.x, dimen.y / 2),
      // bl
      Vec2l(0, dimen.y / 2 + 1) to Vec2l(dimen.x / 2, dimen.y),
      // br
      Vec2l(dimen.x / 2 + 1, dimen.y / 2 + 1) to Vec2l(dimen.x, dimen.y),
    )

    val out = File("out.txt")
    val pw = PrintWriter(out)

    for (step in 0 until attempts) {
      if (step % 10_000L == 0L) {
        println("Step $step")
      }
      normRobots = normRobots.map {
        it.copy(p = (it.p + it.v + dimen) % dimen)
      }

      val qcount = IntArray(4)
      normRobots.forEach { (p, _) ->
        for (i in 0 until 4) {
          if (p.x in quadrants[i].first.x until quadrants[i].second.x &&
            p.y in quadrants[i].first.y until quadrants[i].second.y) {
            qcount[i]++
          }
        }
      }


      val grid = createMutableGrid<Char>(dimen.x.toInt(), dimen.y.toInt()) { '.' }
      normRobots.forEach { (p, _) ->
        grid[Vec2i(p.x.toInt(), p.y.toInt())] = '#'
      }
      pw.println("Step $step")
      pw.println(grid.debugString)
      pw.println("---")
//      BufferedReader(InputStreamReader(System.`in`)).readLine()

      // check whether quadrants mirror each other
//      if (qcount[0] == qcount[1] && qcount[2] == qcount[3]) {
//        val qpos = quadrants.map { q ->
//          newPos.filter {
//            it.x in q.first.x until q.second.x &&
//              it.y in q.first.y until q.second.y
//          }
//        }
////        val qpos1 = qpos[1].toSet()
////        if (qpos[0].all { mirror(it, dimen) in qpos1 }/* &&
////        qpos[2].all { mirror(it, dimen) in qpos[3] }*/) {
//          val grid = createMutableGrid<Char>(dimen.x.toInt(), dimen.y.toInt()) { '.' }
//          newPos.forEach {
//            grid[Vec2i(it.x.toInt(), it.y.toInt())] = '#'
//          }
//          println("Step $step")
//          println(grid.debugString)
//          println("----")
//          BufferedReader(InputStreamReader(System.`in`)).readLine()
////        }
//      }
    }
    return null
  }
}
