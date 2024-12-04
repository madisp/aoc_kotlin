import utils.Parser
import utils.Solution
import utils.cut
import utils.findAll
import java.math.BigDecimal

fun main() {
  Day1.run()
}

data class Day1In(
  val prices: Map<String, BigDecimal>,
  val receipt: String,
)

object Day1 : Solution<Day1In>() {
  override val name = "day1"
  override val parser: Parser<Day1In> = Parser.lines.map { lines ->
    Day1In(
      prices = lines.dropLast(1).associate {
        val (item, price) = it.cut(":")
        item to price.toBigDecimal()
      },
      receipt = lines.last(),
    )
  }

  private val validItems = setOf("Kelluke", "ForMe", "Dynamit")

  override fun part1(input: Day1In): BigDecimal {
    return validItems.sumOf {
      input.prices[it]!! * input.receipt.findAll(it).size.toBigDecimal()
    }
  }
}
