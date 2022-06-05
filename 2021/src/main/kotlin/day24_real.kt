import utils.Parser
import utils.Solution

fun main() {
  Day24Real.run(skipTest = true)
}

object Day24Real : Solution<String>() {
  override val name = "day24"
  override val parser = Parser { it }

  data class Regs(var x: Int = 0, var y: Int = 0, var z: Int = 0, var w: Int = 0)

  fun checkDigit(input: Int, args: Args, regZ: Int): Int {
    var z = regZ
    var x = z
    x %= 26
    z /= args.zDiv
    x += args.xAdd
    x = if (x != input) 1 else 0
    var y = 25
    y *= x
    y += 1
    z *= y
    y = input
    y += args.yAdd
    y *= x
    z += y
    return z
  }

  data class Args(val zDiv: Int, val xAdd: Int, val yAdd: Int)

  // TODO parse this out of the input
  val argsList = listOf(
    Args(1, 11, 3),
    Args(1, 14, 7),
    Args(1, 13, 1),
    Args(26, -4, 6),
    Args(1, 11, 14),
    Args(1, 10, 7),
    Args(26, -4, 9),
    Args(26, -12, 9),
    Args(1, 10, 6),
    Args(26, -11, 4),
    Args(1, 12, 0),
    Args(26, -1, 7),
    Args(26, 0, 12),
    Args(26, -11, 1),
  )

  private fun solve(argsList: List<Args>): List<String> {
    var zRange = setOf(0)
    var idx = argsList.size - 1

    val constrained = Array<MutableMap<Int, MutableSet<Int>>>(14) { mutableMapOf() }

    argsList.reversed().forEach { args ->
      val validZ = mutableSetOf<Int>()
      for (input in 1 .. 9) {
        for (z in 0 .. 1000000) {
          if (checkDigit(input, args, z) in zRange) {
            val set = constrained[idx].getOrPut(input) { mutableSetOf() }
            set.add(z)

            validZ.add(z)
          }
        }
      }
      if (validZ.isEmpty()) {
        println("No valid z for input input[$idx]?")
      }
      idx--
      zRange = validZ
    }

    fun findSerial(index: Int, z: Int): List<String> {
      if (index == 14) return listOf("")

      val opts = constrained[index].entries.filter { z in it.value }
      return opts.flatMap { (digit, _) ->
        val newZ = checkDigit(digit, argsList[index], z)

        findSerial(index + 1, newZ).map {
          digit.toString() + it
        }
      }
    }

    return findSerial(0, 0)
  }

  override fun part1(input: String): Long {
    return solve(argsList).maxOf { it.toLong() }
  }

  override fun part2(input: String): Long {
    return solve(argsList).minOf { it.toLong() }
  }
}
