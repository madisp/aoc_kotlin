import utils.Parser
import utils.SegmentD
import utils.Solution
import utils.Vec2d
import utils.Vec4d
import utils.cut
import utils.eq
import utils.map
import utils.minus
import utils.pairs
import utils.parseItems
import utils.times
import utils.z3

fun main() {
  Day24.run()
}

object Day24 : Solution<List<Day24.Ball>>() {
  override val name = "day24"
  override val parser = Parser.lines.parseItems { parseBall(it) }

  private fun parseBall(line: String): Ball {
    val (pos, vel) = line.cut(" @ ").map {
      val (x, y, z) = it.split(", ").map { it.toDouble() }
      Vec4d(x, y, z, 0.0)
    }
    return Ball(pos, vel.copy(w = 1.0))
  }

  data class Ball(
    val position: Vec4d,
    val velocity: Vec4d,
  )

  fun intersect1(a: Ball, b: Ball, start: Vec2d, end: Vec2d): Boolean {
    // check whether x-y intersects
    val al = SegmentD(Vec2d(a.position.x, a.position.y), Vec2d((a.position + a.velocity).x, (a.position + a.velocity).y))
    val bl = SegmentD(Vec2d(b.position.x, b.position.y), Vec2d((b.position + b.velocity).x, (b.position + b.velocity).y))

    // intersection?
    // ay = a[0] + ax * ad
    // by = b[0] + bx * bd
    // ay = by = x
    // ax = bx = y
    // y = a[0] + x * ad
    // y = b[0] + x * bd
    // b[0] + x * bd = a[0] + x * ad
    // x * bd - x * ad = a[0] - b[0]
    // x * (bd - ad) = a[0] - b[0]
    // x = (a[0] - b[0]) / (bd - ad)
    val x = (al[0.0].y - bl[0.0].y) / (bl.slope - al.slope)
    val y = al[x].y

    if (x !in start.x .. end.x || y !in start.y .. end.y) {
      return false
    }

    // time?
    // x = a.position.x + a.velocity.x * at
    // at = (x - a.position.x) / a.velocity.x
    val at = (x - a.position.x) / a.velocity.x
    val bt = (x - b.position.x) / b.velocity.x

    return (at > 0.0 && bt > 0.0)
  }

  override fun part1(): Int {
    val isTest = input.size < 10
    val (start, end) = if (isTest) {
      Vec2d(7.0, 7.0) to Vec2d(27.0, 27.0)
    } else {
      Vec2d(200000000000000.0, 200000000000000.0) to Vec2d(400000000000000.0, 400000000000000.0)
    }

    return input.pairs.count { (a, b) -> intersect1(a, b, start, end) }
  }

  override fun part2(): Long {
    // we need for our throw
    // time?
    // pos?
    // velocity?

    // x_t / xvel_t

    // x = x_t + dt * xvel_t <-- solve for t_x / t_xvel
    // x = x_1 + dt * xvel_1
    // x = ...
    // x = x_n + dt * xvel_n

    // x_i, xvel_i = known

    // x1 = x_t + dt1 * xvel_t
    // x1 = x_1 + dt1 * xvel_1

    // x2 = x_t + dt2 * xvel_t
    // x2 = x_2 + dt2 * xvel_2

    // x3 = x_t + dt3 * xvel_t
    // x3 = x_3 + dt3 * xvel_3

    // x_t + dt1 * xvel_t = x_1 + dt1 * xvel_1
    // x_t - x_1 = dt1 * xvel_1 - dt1 * xvel_t

    // x_t, y_t, z_t, xvel_t, yvel_t, zvel_t, dt1, dt2, dt3



    // x_t - dt_i * xvel_i + dt_i * xvel_t = x_i
    // y_t - dt_i * yvel_i + dt_i * yvel_t = y_i
    // z_t - dt_i * zvel_i + dt_i * zvel_t = z_i


    // x_t - x_i = dt_i * (xvel_i - xvel_t)

    // dt_i * (xvel_i - xvel_t) = x_t - x_i
    // dt_i = (x_t - x_i) / (xvel_i - xvel_t)

    // dt1 = (x_t - x_1) / (xvel_1 - xvel_t)
    // dt1 = (y_t - y_1) / (yvel_1 - yvel_t)
    // dt1 = (z_t - z_1) / (zvel_1 - zvel_t)
//    val dt = listOf("g", "h", "i", "j", "k", "l")

    // x_t - x_1 = dt1 * (xvel_1 - xvel_t)
    // y_t - y_1 = dt1 * (yvel_1 - yvel_t)
    // z_t - z_1 = dt1 * (zvel_1 - zvel_t)
    // x_t - x_2 = dt2 * (xvel_2 - xvel_t)
    // y_t - y_2 = dt2 * (yvel_2 - yvel_t)
    // z_t - z_2 = dt2 * (zvel_2 - zvel_t)
    // x_t - x_3 = dt3 * (xvel_3 - xvel_t)
    // y_t - y_3 = dt3 * (yvel_3 - yvel_t)
    // z_t - z_3 = dt3 * (zvel_3 - zvel_t)

    return z3 {
      val x_t = int("x_t")
      val y_t = int("y_t")
      val z_t = int("z_t")
      val xvel_t = int("xvel_t")
      val yvel_t = int("yvel_t")
      val zvel_t = int("zvel_t")
      val dt1 = int("dt1")
      val dt2 = int("dt2")
      val dt3 = int("dt3")

      val dt = listOf(dt1, dt2, dt3)

      val eqs = input.take(3).flatMapIndexed { idx, ball ->
        listOf(
          (x_t - ball.position.x.toLong()) eq (dt[idx] * (ball.velocity.x.toLong() - xvel_t)),
          (y_t - ball.position.y.toLong()) eq (dt[idx] * (ball.velocity.y.toLong() - yvel_t)),
          (z_t - ball.position.z.toLong()) eq (dt[idx] * (ball.velocity.z.toLong() - zvel_t)),
        )
      }

      solve(eqs)

      listOf(x_t, y_t, z_t).sumOf { getValue(it) }
    }
  }
}
