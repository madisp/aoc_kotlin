import utils.Parse
import utils.Parser
import utils.Solution
import utils.tesselateWith

fun main() {
  Day5.run()
}

object Day5 : Solution<Day5.Input>() {
  override val name = "day5"
  override val parser = Parser { parseInput(it) }

  @Parse("{state}s: {r ' ' nums}\n\n{r '\n\n' rules}")
  data class Input(
    val state: String,
    val nums: List<Long>,
    @Parse("{key}-{value}")
    val rules: Map<String, RuleTable>,
  )

  // this is a bit ugly, line was {src}-to-{dst}, but we took {src} already as map key
  @Parse("to-{dst} map:\n{r '\n' rules}")
  data class RuleTable(
    val dst: String,
    val rules: List<MapRule>
  )

  @Parse("{dstRangeStart} {srcRangeStart} {length}")
  data class MapRule(
    val dstRangeStart: Long,
    val srcRangeStart: Long,
    val length: Long,
  )

  private fun map(num: Long, rule: MapRule): Long {
    require (num in rule.srcRangeStart until (rule.srcRangeStart + rule.length)) {
      "$num out of range for $rule!"
    }
    return (num - rule.srcRangeStart + rule.dstRangeStart)
  }

  private fun mapRange(range: LongRange, mapRule: MapRule): LongRange {
    return map(range.first, mapRule) .. map(range.last, mapRule)
  }

  private fun applyRule(range: LongRange, mapRule: MapRule): Pair<LongRange?, List<LongRange>> {
    val rule = mapRule.srcRangeStart until (mapRule.srcRangeStart + mapRule.length)
    val (intersect, unmapped) = range tesselateWith rule
    return intersect?.let { mapRange(intersect, mapRule) } to unmapped
  }

  private tailrec fun solve(
    state: String,
    nums: List<LongRange>,
  ): List<LongRange> {
    val map = input.rules[state] ?: return nums

    val newNums = map.rules.fold(emptyList<LongRange>() to nums) { (mapped, unmapped), rule ->
      val applied = unmapped.map { applyRule(it, rule) }
      val newMapped = mapped + applied.mapNotNull { it.first }
      val newUnmapped = applied.flatMap { it.second }
      newMapped to newUnmapped
    }

    return solve(map.dst, newNums.first + newNums.second)
  }

  override fun part1(input: Input): Long {
    val inputRanges = input.nums.map { it..it }
    return solve("seed", inputRanges).minOf { it.first }
  }

  override fun part2(input: Input): Long {
    val inputRanges = input.nums.chunked(2).map { (start, len) -> start until (start + len) }
    return solve("seed", inputRanges).minOf { it.first }
  }
}
