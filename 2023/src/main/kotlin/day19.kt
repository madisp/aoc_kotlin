import utils.Component4
import utils.Parse
import utils.Parser
import utils.Solution
import utils.Vec4i
import utils.cut
import utils.mapItems

fun main() {
  Day19.run()
}

object Day19 : Solution<Pair<List<Day19.Workflow>, List<Vec4i>>>() {
  override val name = "day19"
  override val parser = Parser.compound(
    Parser.lines.mapItems { Workflow.parseWorkflow(it) },
    Parser.lines.mapItems { parseItem(it.removeSurrounding("{", "}")) }
      .mapItems { Vec4i(it.variables["x"]!!, it.variables["m"]!!, it.variables["a"]!!, it.variables["s"]!!) },
  )

  @Parse("{r ',' variables}")
  data class Item(
    @Parse("{key}={value}")
    val variables: Map<String, Int>
  )

  private val varMap = mapOf(
    "x" to Component4.X,
    "m" to Component4.Y,
    "a" to Component4.Z,
    "s" to Component4.W,
  )

  data class Workflow(
    val name: String,
    val rules: List<Rule>,
  ) {
    companion object {
      fun parseWorkflow(input: String): Workflow {
        val (name, rulestr) = input.trim().cut("{")
        val ruleStrs = rulestr.removeSuffix("}").split(",")
        return Workflow(
          name,
          ruleStrs.map { Rule.parse(it) },
        )
      }
    }
  }

  sealed interface Rule {
    val target: String

    data class DefaultRule(override val target: String): Rule
    data class LtRule(override val target: String, val varIndex: Component4, val constant: Int): Rule
    data class GtRule(override val target: String, val varIndex: Component4, val constant: Int): Rule

    companion object {
      fun parse(input: String): Rule {
        if (':' !in input) {
          return DefaultRule(input)
        }
        val (expr, target) = input.cut(":")
        val variable = expr[0]
        val const = expr.substring(2).toInt()
        return when (expr[1]) {
          '>' -> GtRule(target, varMap[variable.toString()]!!, const)
          '<' -> LtRule(target, varMap[variable.toString()]!!, const)
          else -> throw IllegalArgumentException("Unsupported operator ${expr[1]}")
        }
      }
    }
  }

  override fun part1(): Int {
    val rules = input.first.associateBy { it.name }

    var result = Vec4i(0, 0, 0, 0)

    input.second.forEach { item ->
      var target = "in"
      outer@while (target !in setOf("A", "R")) {
        val workflow = rules[target]!!
        for (rule in workflow.rules) {
          when (rule) {
            is Rule.DefaultRule -> {
              target = rule.target
              continue@outer
            }
            is Rule.GtRule -> if (item[rule.varIndex] > rule.constant) {
              target = rule.target
              continue@outer
            }
            is Rule.LtRule -> if (item[rule.varIndex] < rule.constant) {
              target = rule.target
              continue@outer
            }
          }
        }
      }

      if (target == "A") {
        result += item
      }
    }

    return Component4.entries.sumOf { result[it] }
  }

  sealed interface Node {
    data class Leaf(val accept: Boolean) : Node
    data class Tree(val expr: Rule, val left: Node, val right: Node) : Node
  }

  private fun buildNode(workflows: Map<String, Workflow>, rules: List<Rule>): Node {
    if (rules.size == 1) {
      val rule = rules.last()
      require(rule is Rule.DefaultRule)
      return if (rule.target == "R" || rule.target == "A") {
        Node.Leaf(rule.target == "A")
      } else {
        buildNode(workflows, workflows[rule.target]!!.rules)
      }
    }

    val rule = rules.first()
    return Node.Tree(
      expr = rule,
      left = workflows[rule.target]?.let { buildNode(workflows, it.rules) } ?: Node.Leaf(rule.target == "A"),
      right = buildNode(workflows, rules.drop(1)),
    )
  }

  private fun Vec4i.coerceAtLeast(i: Component4, value: Int) = copy(i, maxOf(value, this[i]))
  private fun Vec4i.coerceAtMost(i: Component4, value: Int) = copy(i, minOf(value, this[i]))

  private fun countVariants(node: Node, minimum: Vec4i, maximum: Vec4i): Long {
    return when (node) {
      is Node.Leaf -> if (!node.accept) 0L else {
        val answ = (maximum - minimum + Vec4i(1, 1, 1, 1))
        Component4.entries.map { answ[it].toLong() }.reduce { a, b -> a * b }
      }
      is Node.Tree -> {
        when (val expr = node.expr) {
          is Rule.DefaultRule -> TODO("should not happen")
          is Rule.GtRule -> {
            val left = countVariants(node.left, minimum.coerceAtLeast(expr.varIndex, expr.constant + 1), maximum)
            val right = countVariants(node.right, minimum, maximum.coerceAtMost(expr.varIndex, expr.constant))
            left + right
          }
          is Rule.LtRule -> {
            val left = countVariants(node.left, minimum, maximum.coerceAtMost(expr.varIndex, expr.constant - 1))
            val right = countVariants(node.right, minimum.coerceAtLeast(expr.varIndex, expr.constant), maximum)
            left + right
          }
        }
      }
    }
  }

  override fun part2(): Long {
    val workflows = input.first.associateBy { it.name }
    val root = buildNode(workflows, workflows["in"]!!.rules)
    return countVariants(root, Vec4i(1, 1, 1, 1), Vec4i(4000, 4000, 4000, 4000))
  }
}
