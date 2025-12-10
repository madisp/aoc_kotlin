package year2025

import utils.Parser
import utils.Solution
import utils.Z3Expr
import utils.combinations
import utils.cut
import utils.eq
import utils.greaterOrEqualThan
import utils.mapItems
import utils.plus
import utils.times
import utils.z3

fun main() {
  Day10.run(skipPuzzle = false)
}

typealias Day10In = List<Day10.Machine>

object Day10 : Solution<Day10In>() {
  override val name = "day10"
  override val parser: Parser<Day10In> = Parser.lines.mapItems { line ->
    val (target, rest) = line.cut("] (")
    val (switches, joltages) = rest.cut(") {")

    Machine(
      targetLights = target.drop(1).replace('.', '0').replace('#', '1').reversed().toInt(2),
      switches = switches.split(") (").map { swStr ->
        var out = 0
        swStr.split(",").map { idx ->
          out = out or (1 shl idx.toInt())
        }
        out
      }.toIntArray(),
      targetJoltage = joltages.dropLast(1).split(",").map { it.toInt() },
    )
  }

  class Machine(
    val targetLights: Int,
    val switches: IntArray,
    val targetJoltage: List<Int>,
  )

  override fun part1(input: Day10In): Int {
    return input.sumOf { machine ->
      machine.switches.toList().combinations.filter { sws ->
        var state = 0
        sws.forEach { state = (state and it.inv()) or (state.inv() and it) }
        state == machine.targetLights
      }.minOf { sws ->
        sws.size
      }
    }
  }

  override fun part2(input: Day10In): Long {
    return input.sumOf { machine ->
      // given a target vector of {t_0, t_1, ..., t_n}
      // and m + 1 vectors in format of {v0_0, v0_1, ..., v0_n} ... {vm_0, ... vm_n}
      // find vector multipliers mul_0, mul_1, ..., mul_m such that
      // t_0 = mul_0 * v0_0 + mul_1 * v1_0 + .. + mul_m * vm_0
      // t_1 = mul_0 * v0_1 + mul_1 * v1_1 + .. + mul_m * vm_1
      // ...
      // t_n = mul_0 * v0_n + mul_1 * v1_n + .. + mul_m * vm_n

      z3 {
        val n = machine.targetJoltage.size - 1
        val m = machine.switches.size - 1
        val t = machine.targetJoltage.indices.map { int("t_$it") }
        val mul = (0 .. m).map { int("mul_$it") }
        val v = machine.switches.indices.map { vi ->
          (0..n).map { mi ->
            int("v${vi}_${mi}")
          }
        }
        val tConst = (0..n).map { i ->
          t[i] eq const(machine.targetJoltage[i])
        }
        val vConst = (0..m).flatMap { vi ->
          (0..n).map { vm ->
            val switches = machine.switches[vi]
            v[vi][vm] eq const(if ((1 shl vm) and switches != 0) 1 else 0)
          }
        }
        val mulConstraints = mul.map {
          it greaterOrEqualThan const(0)
        }
        val eqs = (0 .. n).map { i ->
          t[i] eq (1 .. m).fold(mul[0] * v[0][i]) { expr, vi -> expr + (mul[vi] * v[vi][i]) }
        }

        val assertions = tConst + vConst + mulConstraints + eqs
        val solution = (1..m).fold(mul[0] as Z3Expr) { expr, i ->
          expr + mul[i]
        }

        minimize(assertions, solution)

        eval(solution).toLong()
      }
    }
  }
}
