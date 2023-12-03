import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class Computer(
  memorySize: Int = 1024 * 1024,
) {
  val memory = LongArray(memorySize) { 0L }

  private var register: Long = 0L
  private var ip: Int = 0

  fun reboot() {
    memory.fill(0L)
    register = 0
    ip = 0
  }

  fun load(program: LongArray, address: Int = 0) {
    program.copyInto(memory, destinationOffset = address)
  }

  suspend fun run(input: Channel<Long>, debug: Boolean = false): Flow<Long> = flow {
    while (true) {
      val insn = decode(memory, ip)

      if (debug) {
        debug(ip, register, memory, insn)
      }

      when (insn.op) {
        Op.ADD -> {
          memory[o(register, insn, 2)] = v(memory, register, insn, 0) + v(memory, register, insn, 1)
        }
        Op.MUL -> {
          memory[o(register, insn, 2)] = v(memory, register, insn, 0) * v(memory, register, insn, 1)
        }
        Op.GET -> {
          memory[o(register, insn, 0)] = input.receive()
        }
        Op.PUT -> {
          emit(v(memory, register, insn, 0))
        }
        Op.JIT -> {
          if (v(memory, register, insn, 0) != 0L) {
            ip = v(memory, register, insn, 1).toInt()
            continue
          }
        }
        Op.JIF -> {
          if (v(memory, register, insn, 0) == 0L) {
            ip = v(memory, register, insn, 1).toInt()
            continue
          }
        }
        Op.LTN -> {
          if (v(memory, register, insn, 0) < v(memory, register, insn, 1)) {
            memory[o(register, insn, 2)] = 1
          } else {
            memory[o(register, insn, 2)] = 0
          }
        }
        Op.EQL -> {
          if (v(memory, register, insn, 0) == v(memory, register, insn, 1)) {
            memory[o(register, insn, 2)] = 1
          } else {
            memory[o(register, insn, 2)] = 0
          }
        }
        Op.REL -> {
          register += v(memory, register, insn, 0)
        }
        Op.HLT -> {
          break
        }
      }
      ip += insn.op.params + 1
    }
  }
}

enum class Op(val code: Int, val params: Int, val modeMask: Long) {
  ADD(1, 3, 233),
  MUL(2, 3, 233),
  GET(3, 1, 2),
  PUT(4, 1, 3),
  JIT(5, 2, 33),
  JIF(6, 2, 33),
  LTN(7, 3, 233),
  EQL(8, 3, 233),
  REL(9, 1, 3),
  HLT(99, 0, 0),
}

enum class Mode(val code: Int) {
  Position(0),
  Immediate(1),
  Relative(2),
}

class Insn(
  val code: Long, // for debuggin'
  val op: Op,
  val p: LongArray,
  val m: Array<Mode?>,
)

private fun Long.flag(pos: Int): Int {
  var value = this
  repeat(pos) { value /= 10 }
  return value.toInt() % 10
}

private fun decode(mem: LongArray, ip: Int): Insn {
  val opcode = mem[ip]
  val op = Op.entries.firstOrNull { it.code.toLong() == opcode % 100 }
    ?: throw IllegalArgumentException("Illegal opcode $opcode at ip=$ip")

  val p = LongArray(op.params)
  val m = Array<Mode?>(op.params) { null }

  (0 until op.params).forEach { pi ->
    p[pi] = mem[ip + 1 + pi]
    m[pi] = null
    val mask = op.modeMask.flag(pi)
    if (mask != 0) {
      val mode = Mode.entries.find { it.code == opcode.flag(2 + pi) }
        ?: throw IllegalArgumentException("Unrecognized opcode mode ${opcode.flag(2 + pi)}")
      if (mode.code == 0 || mode.code and mask != 0) {
        m[pi]  = mode
      }
    }
  }

  return Insn(opcode, op, p, m)
}

private fun o(reg: Long, insn: Insn, i: Int): Int {
  val off = if (insn.m[i] == Mode.Relative) reg else 0
  return (off + insn.p[i]).toInt()
}

private fun v(mem: LongArray, reg: Long, insn: Insn, i: Int): Long {
  return when(insn.m[i]) {
    Mode.Position -> mem[insn.p[i].toInt()]
    Mode.Immediate -> insn.p[i]
    Mode.Relative -> mem[(reg + insn.p[i]).toInt()]
    null -> insn.p[i]
  }
}

private fun debug(ip: Int, reg: Long, mem: LongArray, insn: Insn) {
  println(String.format("%04d +%04d", ip, reg))
  print(String.format("%04d %s", insn.code, insn.op.name))
  insn.p.indices.forEach { i ->
    print(" ")
    if (insn.m[i] == Mode.Position) {
      print(String.format("&%04x ", insn.p[i]))
    } else if (insn.m[i] == Mode.Relative) {
      print(String.format("&%04d+%04d ", insn.p[i], reg))
    }
    print(v(mem, reg, insn, i))
  }
  println()
}
