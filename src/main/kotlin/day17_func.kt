import utils.Coord
import utils.Parser

fun main() {
  Day17Func.run()
}

object Day17Func : Solution<Day17Func.Rectangle> {
  override val name = "day17"
  override val parser = Parser { input ->
    val (xeq, yeq) = input.removePrefix("target area: ")
      .split(", ")

    val (xs, xe) = xeq.removePrefix("x=").split("..").map { it.toInt() }
    val (ys, ye) = yeq.removePrefix("y=").split("..").map { it.toInt() }

    return@Parser Rectangle(Point(xs, ys), Point(xe, ye))
  }

  private fun gravity(velocity: Vector) = velocity.copy(y = velocity.y - 1)

  private fun drag(velocity: Vector): Vector {
    return if (velocity.x > 0) {
      velocity.copy(x = velocity.x - 1)
    } else if (velocity.x < 0) {
      velocity.copy(x = velocity.x + 1)
    } else {
      velocity
    }
  }

  private fun simulate(position: Point, velocity: Vector, area: Rectangle, highest: Int = position.y): Int? {
    val newPosition = position + velocity

    if (newPosition in area) {
      return highest
    }

    if (newPosition.y < area.bl.y || newPosition.x > area.tr.x) {
      return null
    }

    return simulate(newPosition, gravity(drag(velocity)), area, maxOf(highest, newPosition.y))
  }

  override fun part1(input: Rectangle): Int {
    val start = Point(0, 0)

    val height = (1 .. 100).flatMap {
        x -> (1.. 100).map {
          y -> Vector(x, y)
        }
      }
      .mapNotNull { simulate(start, it, input) }
      .maxOrNull()!!

    return height
  }

  override fun part2(input: Rectangle): Number? {
    val start = Point(0, 0)

    val heights = (1 .. input.tr.x).flatMap {
        x -> (input.bl.y.. 100).map {
          y -> Vector(x, y)
        }
      }
      .mapNotNull { simulate(start, it, input) }

    return heights.count()
  }

  data class Point(val x: Int, val y: Int) {
    operator fun plus(v: Vector) = copy(x = x + v.x, y = y + v.y)
  }

  data class Vector(val x: Int, val y: Int)

  data class Rectangle(val bl: Point, val tr: Point) {
    operator fun contains(point: Point): Boolean {
      return point.x in (bl.x .. tr.x) && point.y in (bl.y .. tr.y)
    }
  }
}
