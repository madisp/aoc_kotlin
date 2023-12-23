import utils.Graph
import utils.Parse
import utils.Parser
import utils.Solution
import utils.findAll
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

fun main() {
  Day19.run()
}

object Day19 : Solution<Day19.Input>() {
  override val name = "day19"
  override val parser = Parser { parseInput(it) }

  @Parse("{r '\n' rules}\n\n{molecule}")
  data class Input(
    val rules: List<Rule>,
    val molecule: Molecule,
  )

  class Molecule(
    val atoms: List<String>,
  ) {
    val key get() = atoms.joinToString("")

    override fun equals(other: Any?) = (other is Molecule) && key == other.key
    override fun hashCode() = key.hashCode()
    override fun toString() = key
  }

  fun parseMolecule(input: String) = Molecule(buildList {
    val sb = StringBuilder()
    for (char in input) {
      if (char.isUpperCase()) {
        if (sb.isNotEmpty()) {
          add(sb.toString())
          sb.clear()
        }
      }
      sb.append(char)
    }
    add(sb.toString())
  })

  @Parse("{input} => {output}")
  data class Rule(
    val input: String,
    val output: String,
  )

  private fun generate(molecule: Molecule, ruleMap: Map<String, List<Rule>>): List<Pair<Rule, Molecule>> {
    return molecule.atoms.flatMapIndexed { index, atom ->
      val rules = ruleMap[atom] ?: emptyList()
      rules.map { rule ->
        val list = (molecule.atoms.subList(0, index) + parseMolecule(rule.output).atoms + molecule.atoms.subList(index + 1, molecule.atoms.size))
        Molecule(list) to rule
      }
    }.toMap().entries.map { (k, v) -> v to k }
  }

  override fun part1(input: Input): Int {
    val ruleMap = input.rules.groupBy { it.input }

    return generate(input.molecule, ruleMap).size
  }

  /*
   * may or may not finish in a reasonable amount of time, depending on how we luck out with the random shuffle
   */
  override fun part2(input: Input): Int {
    val rules = input.rules.shuffled()
    val g = Graph<String, Rule>(
      edgeFn = { molecule ->
        // try applying each rule
        rules.flatMap { rule ->
          molecule.findAll(rule.output).map { atom ->
            rule to molecule.substring(0, atom) + rule.input + molecule.substring(atom + rule.output.length)
          }
        }
      }
    )

    return g.dfs(input.molecule.toString()) { it == "e" }.size - 1
  }
}
