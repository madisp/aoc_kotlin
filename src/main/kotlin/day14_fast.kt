import utils.Parser

fun main() {
  Day14Fast.run()
}

object Day14All {
  @JvmStatic fun main(args: Array<String>) {
    mapOf("func" to Day14Func, "imp" to Day14Imp, "fast" to Day14Fast).forEach { (header, solution) ->
      solution.run(header = header, skipPart1 = false, skipTest = false, printParseTime = false)
    }
  }
}

object Day14Fast : Solution<Pair<Day14Fast.Polymer, IntArray>> {
  private val elements = "HKFVNSBCPO"
  private val sz = elements.length
  private val psh = generateSequence(1) { it * 2 }.indexOfFirst { it > sz }
  private val psz = 1 shl psh

  private val Char.int: Int get() {
    return elements.indexOf(this).takeIf { it >= 0 } ?:
      throw IllegalArgumentException("Bad input char '$this'")
  }

  override val name = "day14"
  override val parser = Parser { input ->
    val (polymer, ruleLines) = input.split("\n\n")

    val p = Polymer(LongArray(psz * psz) { 0 }, LongArray(sz) { 0 })
    val r = IntArray(psz * psz) { -1 }

    p.chars[polymer.first().int]++
    for (i in 0 until polymer.length - 1) {
      val c1 = polymer[i].int
      val c2 = polymer[i + 1].int
      p.pairs[(c1 shl psh) + c2]++
      p.chars[c2]++
    }

    ruleLines.lines().filter { it.isNotBlank() }.forEach { line ->
      val (pair, replace) = line.split(" -> ").map { it.trim() }
      r[(pair[0].int shl psh) + pair[1].int] = replace[0].int
    }

    return@Parser p to r
  }

  class Polymer(val pairs: LongArray, val chars: LongArray)

  fun solve(polymer: Polymer, rules: IntArray, days: Int): Long {
    val pairs = polymer.pairs.clone()
    val chars = polymer.chars.clone()

    val pairDeltas = LongArray(pairs.size) { 0 }
    val charDeltas = LongArray(chars.size) { 0 }

    repeat(days) {
      pairDeltas.fill(0)
      charDeltas.fill(0)

      for (c1 in 0 until sz) {
        for (c2 in 0 until sz) {
          val pair = (c1 shl psh) + c2
          val char = rules[pair]

          if (char == -1) continue

          val count = pairs[pair]
          pairDeltas[pair] -= count
          pairDeltas[(pair and ((psz - 1) shl psh)) or char] += count
          pairDeltas[(char shl psh) or (pair and (psz - 1))] += count
          charDeltas[char] += count
        }
      }

      for (c1 in 0 until sz) {
        for (c2 in 0 until sz) {
          val index = (c1 shl psh) or c2
          pairs[index] += pairDeltas[index]
        }
      }

      charDeltas.forEachIndexed { i, v -> chars[i] += v }
    }

    var maxChar = Long.MIN_VALUE
    var minChar = Long.MAX_VALUE
    for (count in chars) {
      if (count == 0L) continue
      if (count > maxChar) maxChar = count
      if (count < minChar) minChar = count
    }

    return maxChar - minChar
  }

  override fun part1(input: Pair<Polymer, IntArray>): Long {
    return solve(input.first, input.second, 10)
  }

  override fun part2(input: Pair<Polymer, IntArray>): Long {
    return solve(input.first, input.second, 40)
  }
}
