import utils.Parser
import utils.Solution
import utils.cut
import utils.mapItems
import kotlin.reflect.KMutableProperty1

fun main() {
  Day23.run()
}

object Day23 : Solution<List<Day23.Insn>>() {
  override val name = "day23"
  override val parser = Parser.lines.mapItems { Insn.parse(it) }

  enum class Opcode {
    hlf, tpl, inc, jmp, jie, jio
  }

  enum class Register(val reg: KMutableProperty1<State, Int>) {
    a(State::a),
    b(State::b)
  }

  data class State(
    var pc: Int = 0,
    var a: Int = 0,
    var b: Int = 0,
  )

  sealed interface Insn {
    data class Hlf(val r: Register): Insn
    data class Tpl(val r: Register): Insn
    data class Inc(val r: Register): Insn
    data class Jmp(val off: Int): Insn
    data class Jie(val r: Register, val off: Int): Insn
    data class Jio(val r: Register, val off: Int): Insn

    companion object {
      fun parse(line: String): Insn {
        val (op, rest) = line.cut(" ")
        val opcode = Opcode.valueOf(op)
        return when (opcode) {
          Opcode.hlf -> Hlf(Register.valueOf(rest))
          Opcode.tpl -> Tpl(Register.valueOf(rest))
          Opcode.inc -> Inc(Register.valueOf(rest))
          Opcode.jmp -> Jmp(rest.toInt())
          Opcode.jie -> {
            val (reg, off) = rest.cut(", ")
            Jie(Register.valueOf(reg), off.toInt())
          }
          Opcode.jio -> {
            val (reg, off) = rest.cut(", ")
            Jio(Register.valueOf(reg), off.toInt())
          }
        }
      }
    }
  }

  private fun run(state: State) {
    while (state.pc in input.indices) {
      val insn = input[state.pc]
      when (insn) {
        is Insn.Hlf -> {
          insn.r.reg.set(state, insn.r.reg.get(state) ushr 1)
        }
        is Insn.Tpl -> {
          insn.r.reg.set(state, insn.r.reg.get(state) * 3)
        }
        is Insn.Inc -> {
          insn.r.reg.set(state, insn.r.reg.get(state) + 1)
        }
        is Insn.Jmp -> {
          state.pc += insn.off
          continue
        }
        is Insn.Jie -> {
          if (insn.r.reg.get(state) % 2 == 0) {
            state.pc += insn.off
            continue
          }
        }
        is Insn.Jio -> {
          if (insn.r.reg.get(state) == 1) {
            state.pc += insn.off
            continue
          }
        }
      }
      state.pc++
    }
  }

  override fun part1(): Int {
    val state = State()
    run(state)
    return state.b
  }

  override fun part2(): Int {
    val state = State(a = 1)
    run(state)
    return state.b
  }
}
