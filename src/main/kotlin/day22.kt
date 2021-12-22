import utils.Component4
import utils.Component4.X
import utils.Component4.Y
import utils.Component4.Z
import utils.Parser
import utils.Point3i
import utils.Vec4i
import utils.mapItems

fun main() {
  Day22.run()
}

object Day22 : Solution<List<Day22.Command>> {
  override val name = "day22"
  override val parser = Parser.lines
    .mapItems { line ->
      val (on, cuboidStr) = line.split(" ")
      Command(on == "on", Cuboid.fromString(cuboidStr.trim()))
    }

  data class Cuboid(val start: Vec4i, val end: Vec4i) {
    val valid = end.x > start.x && end.y > start.y && end.z > start.z

    fun volume(): Long {
      return (end.x.toLong() - start.x) * (end.y - start.y) * (end.z - start.z)
    }

    fun cut(component: Component4, value: Int): Pair<Cuboid?, Cuboid?> {
      return Cuboid(start, end.copy(component, minOf(value, end[component]))).takeIf { it.valid } to
          Cuboid(start.copy(component, maxOf(value, start[component])), end).takeIf { it.valid }
    }

    operator fun minus(right: Cuboid): List<Cuboid> {
      if ((this intersect right) == null) return listOf(this)

      val finalCut = ArrayList<Cuboid>(6)
      var cut = this

      for (c in listOf(X, Y, Z)) {
        for (start in listOf(true, false)) {
          val (l, r) = if (start) cut.cut(c, right.start[c]) else cut.cut(c, right.end[c]).flipped
          if (l != null) { finalCut.add(l) }
          if (r == null) { return finalCut }
          cut = r
        }
      }

      return finalCut
    }

    infix fun intersect(right: Cuboid): Cuboid? {
      require(this.valid && right.valid)
      return Cuboid(
        Point3i(maxOf(start.x, right.start.x), maxOf(start.y, right.start.y), maxOf(start.z, right.start.z)),
        Point3i(minOf(end.x, right.end.x), minOf(end.y, right.end.y), minOf(end.z, right.end.z)),
      ).takeIf { it.valid }
    }

    companion object {
      fun fromString(str: String): Cuboid {
        val components = str.split(",").map { component ->
          val (name, value) = component.split("=")
          val (from, to) = value.split("..")
          name to (from to to)
        }.toMap()
        return Cuboid(
          Point3i(components["x"]!!.first.toInt(), components["y"]!!.first.toInt(), components["z"]!!.first.toInt()),
          Point3i(components["x"]!!.second.toInt() + 1, components["y"]!!.second.toInt() + 1, components["z"]!!.second.toInt() + 1),
        )
      }
    }
  }

  data class Command(val on: Boolean, val cuboid: Cuboid)

  fun solve(input: List<Command>): Long {
    var cuboids = listOf<Cuboid>()

    input.forEachIndexed { _, cmd ->
      val (on, cuboid) = cmd
      require(cuboid.start.x < cuboid.end.x)
      require(cuboid.start.y < cuboid.end.y)
      require(cuboid.start.z < cuboid.end.z)

      val new = mutableListOf<Cuboid>()
      cuboids.forEach {
        new.addAll(it - cuboid)
      }
      if (on) {
        new.add(cuboid)
      }
      cuboids = new
    }

    return cuboids.sumOf { it.volume() }
  }

  override fun part1(input: List<Command>): Long {
    val pt1Input = input.map { (cmd, cuboid) -> Command(cmd, Cuboid(
        Point3i(cuboid.start.x.coerceAtLeast(-50), cuboid.start.y.coerceAtLeast(-50), cuboid.start.z.coerceAtLeast(-50)),
        Point3i(cuboid.end.x.coerceAtMost(50), cuboid.end.y.coerceAtMost(50), cuboid.end.z.coerceAtMost(50))
      ))
    }.filter { (_, cuboid) -> cuboid.valid }

    return solve(pt1Input)
  }

  override fun part2(input: List<Command>): Long {
    return solve(input)
  }
}
