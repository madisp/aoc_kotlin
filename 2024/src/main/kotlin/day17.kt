import utils.Parse
import utils.Parser
import utils.Solution

fun main() {
  Day17.run()
}

typealias Day17In = Pair<Day17.Regs, List<Int>>

object Day17 : Solution<Day17In>() {
  override val name = "day17"
  override val parser: Parser<Day17In> = Parser.compound(
    first = { parseRegs(it) },
    second = { it.removePrefix("Program: ").trim().split(",").map { n -> n.toInt() } }
  )

  @Parse("Register A: {a}\nRegister B: {b}\nRegister C: {c}")
  data class Regs(
    val a: Long,
    val b: Long,
    val c: Long,
    val ip: Int = 0,
  )

  enum class Opcode(val code: Int) {
    adv(0),
    bxl(1),
    bst(2),
    jnz(3),
    bxc(4),
    oot(5),
    bdv(6),
    cdv(7)
  }

  data class Op(
    val opcode: Opcode,
    val operand: Int,
  )

  private inline fun apply(regs: Regs, op: Op, out: (Int) -> Unit): Regs {
    val combo = listOf(0L, 1L, 2L, 3L, regs.a, regs.b, regs.c)
    return when (op.opcode) {
      Opcode.adv -> {
        regs.copy(a = (regs.a ushr combo[op.operand].toInt()).toInt().toLong(), ip = regs.ip + 2)
      }
      Opcode.bxl -> regs.copy(b = regs.b xor op.operand.toLong(), ip = regs.ip + 2)
      Opcode.bst -> regs.copy(b = combo[op.operand] and 7, ip = regs.ip + 2)
      Opcode.jnz -> if (regs.a == 0L) regs.copy(ip = regs.ip + 2) else regs.copy(ip = op.operand)
      Opcode.bxc -> regs.copy(b = regs.b xor regs.c, ip = regs.ip + 2)
      Opcode.oot -> regs.copy(ip = regs.ip + 2).also {
        out(combo[op.operand].toInt() and 7)
      }
      Opcode.bdv -> {
        regs.copy(b = (regs.a ushr combo[op.operand].toInt()).toInt().toLong(), ip = regs.ip + 2)
      }
      Opcode.cdv -> {
        regs.copy(c = (regs.a ushr combo[op.operand].toInt()).toInt().toLong(), ip = regs.ip + 2)
      }
    }
  }

  private fun run(input: Day17In, overrideA: Long? = null, breakOnFirstOutput: Boolean = false): List<Int> {
    var regs = input.first.copy(a = overrideA ?: input.first.a)
    val insns = input.second

    val output = mutableListOf<Int>()

    while (regs.ip < insns.size - 1) {
      val op = insns[regs.ip]
      val operand = insns[regs.ip + 1]
      regs = apply(regs, Op(Opcode.entries.first { it.code == op }, operand)) {
        output.add(it)
        if (breakOnFirstOutput) {
          return output
        }
      }
    }

    return output
  }

  private fun findBits(a: Long, output: Int): List<Long> {
    val variants = mutableListOf<Long>()
    for (bits in 0b000 .. 0b111) {
      val answ = (a shl 3) + bits
      val out = run(input, overrideA = answ, breakOnFirstOutput = true)
      if (out.size == 1 && out.first() == output) {
        variants += answ
      }
    }
    return variants
  }

  override fun part1(input: Day17In): String {
    return run(input).joinToString(",")
  }

  override fun part2(input: Day17In): Long {
    var candidates = listOf(0L)
    input.second.reversed().forEach { insn ->
      candidates = candidates.flatMap { a ->
        findBits(a, insn)
      }
    }
    return candidates.min()
  }
}
