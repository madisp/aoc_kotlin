import utils.Parser
import utils.cut

fun main() {
  Day24Imp.run(skipTest = true)
}

object Day24Imp : Solution<List<List<Day24Imp.Insn>>> {
  override val name = "day24"

  private const val DIGITS = 14

  override val parser = Parser { input ->
    val progStrings = input.split("inp w\n").map { it.trim() }.filter { it.isNotBlank() }
    val progs = progStrings.map { it.lines().filter(String::isNotBlank).map(Insn::parse) }

    // assumptions made about the input
    require(progs.size == DIGITS)
    progs.forEach { insns ->
      // W is only input
      require(insns.none { it.left == Register.W })
      // X and Y are always reset before calculating
      require(insns.first { it.left == Register.X } == Insn(Opcode.mul, Register.X, Literal(0)))
      require(insns.first { it.left == Register.Y } == Insn(Opcode.mul, Register.Y, Literal(0)))
    }

    progs
  }

  data class Regs(var x: Int = 0, var y: Int = 0, var z: Int = 0, var w: Int = 0)

  enum class Opcode { add, mul, div, mod, eql }

  sealed interface Value {
    operator fun get(regs: Regs): Int
  }

  enum class Register(val getter: (Regs) -> Int, val setter: (Regs, Int) -> Unit) : Value {
    X(Regs::x, { r, v -> r.x = v }),
    Y(Regs::y, { r, v -> r.y = v }),
    Z(Regs::z, { r, v -> r.z = v }),
    W(Regs::w, { r, v -> r.w = v });
    override fun get(regs: Regs) = getter(regs)
    operator fun set(regs: Regs, value: Int) = setter(regs, value)
  }

  data class Literal(val value: Int) : Value {
    override fun get(regs: Regs) = value
  }

  data class Insn(val op: Opcode, val left: Register, val right: Value) {
    companion object {
      fun parse(str: String): Insn {
        val (op, values) = str.cut(" ")
        val (left, right) = values.cut(" ")
        return Insn(Opcode.valueOf(op), Register.valueOf(left.uppercase()),
          right.toIntOrNull()?.let { Literal(it) } ?: Register.valueOf(right.uppercase()))
      }
    }
  }

  fun exec(insn: Insn, regs: Regs) {
    insn.left[regs] = when (insn.op) {
      Opcode.add -> insn.left[regs] + insn.right[regs]
      Opcode.mul -> insn.left[regs] * insn.right[regs]
      Opcode.div -> insn.left[regs] / insn.right[regs]
      Opcode.mod -> insn.left[regs] % insn.right[regs]
      Opcode.eql -> if (insn.left[regs] == insn.right[regs]) 1 else 0
    }
  }

  /**
   * Return Z register for the given input
   */
  fun checkDigit(insns: List<Insn>, inp: Int, z: Int): Int {
    val r = Regs(z = z, w = inp)
    insns.forEach { exec(it, r) }
    return r.z
  }

  private fun solve(input: List<List<Insn>>): List<Long> {
    var zRange = setOf(0)
    var idx = input.size - 1
    val constrained = Array<MutableMap<Int, MutableSet<Int>>>(14) { mutableMapOf() }

    input.reversed().forEach { prog ->
      val validZ = mutableSetOf<Int>()
      for (input in 1 .. 9) {
        for (z in 0 .. 1000000) {
          if (checkDigit(prog, input, z) in zRange) {
            val set = constrained[idx].getOrPut(input) { mutableSetOf() }
            set.add(z)
            validZ.add(z)
          }
        }
      }
      require(validZ.isNotEmpty()) { "No valid z for input input[$idx]?" }
      idx--
      zRange = validZ
    }

    fun findSerial(index: Int, z: Int): List<String> {
      if (index == 14) return listOf("")

      val opts = constrained[index].entries.filter { z in it.value }
      return opts.flatMap { (digit, _) ->
        val newZ = checkDigit(input[index], digit, z)

        findSerial(index + 1, newZ).map {
          digit.toString() + it
        }
      }
    }

    return findSerial(0, 0).map { it.toLong() }
  }

  override fun part1(input: List<List<Insn>>): Long? {
    return solve(input).maxOrNull()
  }

  override fun part2(input: List<List<Insn>>): Long? {
    return solve(input).minOrNull()
  }
}
