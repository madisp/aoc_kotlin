import utils.Parse
import utils.Parser
import utils.Solution
import utils.Vec2l
import utils.badInput
import utils.mapItems
import utils.selections
import java.util.ArrayDeque

fun main() {
  Day24.run()
}

typealias Day24In = Pair<List<Day24.Const>, List<Day24.RawExpr>>

object Day24 : Solution<Day24In>() {
  override val name = "day24"
  override val parser: Parser<Day24In> = Parser.compound(
    first = Parser.lines.mapItems { parseConst(it) },
    second = Parser.lines.mapItems { parseRawExpr(it) },
  )

  @Parse("{name}: {value}")
  data class Const(val name: String, val value: Int)

  @Parse("{a} {op} {b} -> {target}")
  data class RawExpr(val a: String, val b: String, val op: Op, val target: String)

  enum class Op {
    AND, OR, XOR
  }

  enum class Variable {
    X, Y
  }

  sealed interface Expr {
    fun eval(inputs: Vec2l): Boolean

//    data class Const(val value: Boolean): Expr {
//      override fun eval(inputs: Vec2l) = value
//    }
    data class Ref(val variable: Variable, val bit: Int): Expr {
      override fun eval(inputs: Vec2l) = when (variable) {
        Variable.X -> inputs.x and (1L shl bit) != 0L
        Variable.Y -> inputs.y and (1L shl bit) != 0L
      }
    }
    data class BinOp(val a: Expr, val op: Op, val b: Expr): Expr {
      override fun eval(inputs: Vec2l) = when (op) {
        Op.AND -> a.eval(inputs) && b.eval(inputs)
        Op.OR -> a.eval(inputs) || b.eval(inputs)
        Op.XOR -> a.eval(inputs) xor b.eval(inputs)
      }
    }
  }

  private fun addExpr(expr: MutableMap<String, Expr>, rawExprs: Map<String, RawExpr>, rawExpr: RawExpr): Expr {
    expr[rawExpr.target]?.let { return it }
    val e = Expr.BinOp(
      a = expr[rawExpr.a] ?: addExpr(expr, rawExprs, rawExprs[rawExpr.a]!!),
      op = rawExpr.op,
      b = expr[rawExpr.b] ?: addExpr(expr, rawExprs, rawExprs[rawExpr.b]!!)
    )
    expr[rawExpr.target] = e
    return e
  }

  private fun buildExpr(swaps: Map<String, String> = emptyMap()): Pair<Vec2l, Map<String, Expr>> {
    var inputVars = Vec2l(0, 0)
    val (consts, unswapped) = input

    val rawExprs = unswapped.map {
      it.copy(target = swaps[it.target] ?: it.target)
    }

    val expr = mutableMapOf<String, Expr>()
    consts.forEach {
      val name = if (it.name[0] == 'x') Variable.X else Variable.Y
      val bit = it.name.substring(1).toInt(10)
      if (name == Variable.X) {
        inputVars = inputVars.copy(x = inputVars.x or (it.value.toLong() shl bit))
      } else {
        inputVars = inputVars.copy(y = inputVars.y or (it.value.toLong() shl bit))
      }
      expr[it.name] = Expr.Ref(name, bit)
    }

    val rawExpr = mutableMapOf<String, RawExpr>()
    rawExprs.forEach { rawExpr[it.target] = it }
    rawExprs.forEach {
      addExpr(expr, rawExpr, it)
    }

    return inputVars to expr
  }

  private fun evalExpr(exprMap: Map<String, Expr>, inputVars: Vec2l): Long {
    var answ = 0L

    var start = 0
    while (true) {
      val e = exprMap["z${start.toString(10).padStart(2, '0')}"]
      if (e == null) {
        break
      }
      if (e.eval(inputVars)) {
        answ += 1L shl (start)
      }
      start++
    }

    return answ
  }

  override fun part1(input: Day24In): Long {
    val (inputVars, exprMap) = buildExpr()
    return evalExpr(exprMap, inputVars)
  }

  private fun addLong(a: Long, b: Long, bits: Int = 45): Long {
    val mask = (1L shl bits) - 1
    return (a + b) and mask
  }
  // tests
  val simpleTests = listOf(
    0L to 0L,
    1L to 1L,
  )
  // zero + one
  val plusOne = (0 until 44).map {
    0L to (1L shl it)
  }
  // one + one shifted
  val shifted = (0 until 44).map {
    (1L shl it) to (1L shl it)
  }
  // test carries up to 2^44
  val carries = (0 until 44).map {
    (1L shl (it + 1)) - 1 to 1L
  }

  private val <T> List<Pair<T, T>>.mirror: List<Pair<T, T>> get() {
    return this + this.map { (a, b) -> b to a }
  }

  val tests = listOf(
    simpleTests.mirror,
    plusOne.mirror,
    shifted.mirror,
    carries.mirror,
  ).flatten()

  private fun getLowestDiffBit(a: Long, b: Long): Int {
    return (a xor b).countTrailingZeroBits()
  }

  private fun test(exprMap: Map<String, Expr>): Int {
    // return the lowest bit that failed
    var low = Int.MAX_VALUE

    tests.forEachIndexed { i, test ->
      val (a, b) = test
      val c = addLong(a, b)
      val d = evalExpr(exprMap, Vec2l(a, b))
      if (c != d) {
        low = minOf(low, getLowestDiffBit(c, d))
      }
    }
    return low
  }

  private fun findDeps(bit: Int, swaps: Map<String, String>): Set<String> {
    val rawExprs = input.second.map {
      it.copy(target = swaps[it.target] ?: it.target)
    }
    val rawExpr = mutableMapOf<String, RawExpr>()
    rawExprs.forEach { rawExpr[it.target] = it }

    val out = mutableSetOf<String>()

    val q = ArrayDeque<String>()
    q.add("z${bit.toString(10).padStart(2, '0')}")

    while (q.isNotEmpty()) {
      val item = q.poll()
      out += item
      val e = rawExpr[item] ?: continue
      q.add(e.a)
      q.add(e.b)
    }

    return out
  }

  private fun guessDeps(bit: Int, swaps: Map<String, String>): List<String> {
    val minus = findDeps(bit - 1, swaps)
    val deps = findDeps(bit + 1, swaps)
    return (deps - minus).toList()
  }

  private fun findBestSwap(swaps: Map<String, String>): Map<String, String> {
    var best = test(buildExpr(swaps).second)
    var bestSwap = swaps

    val guesses = guessDeps(best, swaps) + "z${best.toString(10).padStart(2, '0')}"

    guesses.selections(2).forEach {
      val newSwaps = swaps + mapOf(it[0] to it[1], it[1] to it[0])
      val (_, exprMap) = runCatching { buildExpr(newSwaps) }.getOrNull() ?: return@forEach
      val score = test(exprMap)
      if (score > best) {
        best = score
        bestSwap = newSwaps
      }
    }

    return bestSwap
  }

  override fun part2(input: Day24In): String {
    if (input.second.size < 100) {
      return ""
    }

    var swaps = mapOf<String, String>()
    repeat (4) {
      swaps = findBestSwap(swaps)
    }

    // check
    val (_, exprMap) = buildExpr(swaps)
    val test = test(exprMap)
    if (test != Int.MAX_VALUE) {
      badInput()
    }

    return swaps.keys.sorted().joinToString(",")
  }
}
