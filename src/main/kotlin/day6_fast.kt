import utils.Parser

fun main() {
  Day6Fast.run()
}

object Day6All {
  @JvmStatic fun main(args: Array<String>) {
    mapOf("func" to Day6Func, "imp" to Day6Imp, "fast" to Day6Fast).forEach { (header, solution) ->
      solution.run(header = header, skipPart1 = true, skipTest = true, printParseTime = false)
    }
  }
}

object Day6Fast : Solution<LongArray> {
  override val name = "day6"
  override val parser = Parser { input ->
    val fishies = LongArray(9) { 0L }
    input.split(",").map { it.toInt() }.forEach {
      fishies[it]++
    }
    fishies
  }

  override fun part1(input: LongArray): Long {
    return simulate(input, forDays = 80)
  }

  override fun part2(input: LongArray): Long {
    return simulate(input, forDays = 256)
  }

  private fun simulate(input: LongArray, forDays: Int): Long {
    // pull the array into locals for stack access (or registers, really..)
    var (t0, t1, t2, t3, t4) = input
    // Kotlin doesn't have more than 5 components for arrs :|
    var t5 = input[5]
    var t6 = input[6]
    var t7 = input[7]
    var t8 = input[8]

    for (i in 1 .. forDays) {
      val count = t0
      t0 = t1
      t1 = t2
      t2 = t3
      t3 = t4
      t4 = t5
      t5 = t6
      t6 = t7 + count
      t7 = t8
      t8 = count
    }

    return (t0 + t1 + t2 + t3 + t4 + t5 + t6 + t7 + t8)
  }
}