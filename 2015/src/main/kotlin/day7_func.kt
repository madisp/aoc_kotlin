import utils.Parser
import utils.Solution
import utils.cut
import utils.mapItems

fun main() {
  Day7Func.run()
}

object Day7Func : Solution<List<Day7Func.Opcode>>() {
  override val name = "day7"
  override val parser = Parser.lines.mapItems(Opcode::parse)

  private fun value(name: String, ops: Map<String, Opcode>, cache: Map<String, Int>): Pair<Map<String, Int>, Int> {
    if (name.first().isDigit()) {
      return cache to name.toInt()
    }
    val cached = cache[name]
    if (cached != null) {
      return cache to cached
    }

    val (newCache, value) = ops[name]!!.get(ops, cache)

    return (newCache + (name to value)) to value
  }

  sealed class Opcode(open val dst: String) {
    abstract fun get(ops: Map<String, Opcode>, cache: Map<String, Int>): Pair<Map<String, Int>, Int>

    data class Assign(val src: String, override val dst: String) : Opcode(dst) {
      override fun get(ops: Map<String, Opcode>, cache: Map<String, Int>): Pair<Map<String, Int>, Int> {
        return value(src, ops, cache)
      }
    }

    data class And(val l: String, val r: String, override val dst: String) : Opcode(dst) {
      override fun get(ops: Map<String, Opcode>, cache: Map<String, Int>): Pair<Map<String, Int>, Int> {
        val (lcache, lval) = value(l, ops, cache)
        val (rcache, rval) = value(r, ops, lcache)
        return rcache to (lval and rval)
      }
    }

    data class Or(val l: String, val r: String, override val dst: String) : Opcode(dst) {
      override fun get(ops: Map<String, Opcode>, cache: Map<String, Int>): Pair<Map<String, Int>, Int> {
        val (lcache, lval) = value(l, ops, cache)
        val (rcache, rval) = value(r, ops, lcache)
        return rcache to (lval or rval)
      }
    }

    data class Not(val src: String, override val dst: String) : Opcode(dst) {
      override fun get(ops: Map<String, Opcode>, cache: Map<String, Int>): Pair<Map<String, Int>, Int> {
        val (ncache, nval) = value(src, ops, cache)
        return ncache to nval.inv()
      }
    }

    data class Lshift(val src: String, val n: Int, override val dst: String) : Opcode(dst) {
      override fun get(ops: Map<String, Opcode>, cache: Map<String, Int>): Pair<Map<String, Int>, Int> {
        val (ncache, nval) = value(src, ops, cache)
        return ncache to (nval shl n)
      }
    }

    data class Rshift(val src: String, val n: Int, override val dst: String) : Opcode(dst) {
      override fun get(ops: Map<String, Opcode>, cache: Map<String, Int>): Pair<Map<String, Int>, Int> {
        val (ncache, nval) = value(src, ops, cache)
        return ncache to (nval ushr n)
      }
    }

    companion object {
      fun parse(line: String): Opcode {
        val (expr, dst) = line.cut(" -> ")
        if (expr.startsWith("NOT")) {
          return Not(expr.removePrefix("NOT").trim(), dst)
        }
        if ("AND" in expr) {
          val (l, r) = expr.cut(" AND ")
          return And(l, r, dst)
        }
        if ("OR" in expr) {
          val (l, r) = expr.cut(" OR ")
          return Or(l, r, dst)
        }
        if ("LSHIFT" in expr) {
          val (l, n) = expr.cut(" LSHIFT ")
          return Lshift(l, n.toInt(), dst)
        }
        if ("RSHIFT" in expr) {
          val (l, n) = expr.cut(" RSHIFT ")
          return Rshift(l, n.toInt(), dst)
        }
        return Assign(expr, dst)
      }
    }
  }

  override fun part1(input: List<Opcode>): Int {
    val ops = input.groupBy { it.dst }.mapValues { it.value.first() }
    return value("a", ops, emptyMap()).second
  }

  override fun part2(input: List<Opcode>): Int {
    val ops = input.groupBy { it.dst }.mapValues { it.value.first() }
    val a = value("a", ops, emptyMap()).second
    return value("a", ops + ("b" to Opcode.Assign(a.toString(), "b")), emptyMap()).second
  }
}
