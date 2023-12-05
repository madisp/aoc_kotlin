import utils.Parser
import utils.Solution
import utils.mapItems

fun main() {
  Day5.run(skipTest = false)
}

object Day5 : Solution<Pair<List<Long>, List<Day5.SeedMap>>>() {
  override val name = "day5"
  override val parser = Parser.compound(
    { it.removePrefix("seeds: ").split(' ').map { it.toLong() } },
    Parser.blocks.mapItems { SeedMap.parse(it) }
  )

  data class SeedMap(
    val src: String,
    val dst: String,
    val rules: List<MapRule>
  ) {
    companion object {
      fun parse(input: String): SeedMap {
        val lines = input.lines()
        val (src, dst) = lines.first().trim().removeSuffix(" map:").split("-to-")
        val rules = lines.drop(1).map { MapRule.parse(it.trim()) }
        return SeedMap(src, dst, rules)
      }
    }
  }

  data class MapRule(
    val dstRangeStart: Long,
    val srcRangeStart: Long,
    val length: Long,
  ) {
    companion object {
      fun parse(input: String): MapRule {
        val (dst, src, len) = input.split(" ").map { it.toLong() }
        return MapRule(dst, src, len)
      }
    }
  }

  private fun map(num: Long, rule: MapRule): Long {
    require (num in rule.srcRangeStart until (rule.srcRangeStart + rule.length)) {
      "$num out of range for $rule!"
    }
    return (num - rule.srcRangeStart + rule.dstRangeStart)
  }

  private fun mapRange(range: LongRange, mapRule: MapRule): LongRange {
    return map(range.first, mapRule) .. map(range.last, mapRule)
  }

  private fun mapTesselate(range: LongRange, mapRule: MapRule): Pair<LongRange?, List<LongRange>> {
    val rule = mapRule.srcRangeStart until (mapRule.srcRangeStart + mapRule.length)

    if (range.first > rule.last || range.last < rule.first) {
      // case 1: nothing mapped
      // range:            |------|
      // rule:  |------|
      // return mapped:null unmapped:|-------|
      return null to listOf(range)
    } else if (range.first in rule && range.last in rule) {
      // case 2: everything mapped
      // range:   |--|
      // rule:  |------|
      // return mapped:|--| unmapped:null
      return mapRange(range, mapRule) to emptyList()
    } else if (range.first < rule.first && range.last > rule.last) {
      // case 3: something mapped
      // range: |---------|
      // rule:      |-|
      // return mapped:|-| unmapped:|---|,|---|
      return mapRange(rule, mapRule) to listOf(range.first until rule.first, rule.last + 1 .. range.last)
    } else if (range.last > rule.last) {
      // case 4: something mapped (split the range!)
      // range:      |------|
      // rule:  |------|
      // return mapped:|-| unmapped:|------|
      return mapRange(range.first .. rule.last, mapRule) to listOf(rule.last + 1 .. range.last)
    } else {
      // case 5: something mapped (split the range!)
      // range: |------|
      // rule:       |------|
      // return mapped:|-| unmapped:|------|
      return mapRange(rule.first .. range.last, mapRule) to listOf(range.first until rule.first)
    }
  }

  private fun solveRanges(
    inputMap: List<SeedMap>,
    inputNums: List<LongRange>,
  ): Long {
    var nums = inputNums
    val maps = inputMap.associateBy { it.src }
    var state = "seed"
    while (state != "location") {
      val seedMap = maps[state]!!

      val mapped = mutableListOf<LongRange>()
      var unmapped = mutableListOf<LongRange>()
      unmapped += nums

      seedMap.rules.forEach { rule ->
        val newUnmapped = mutableListOf<LongRange>()
        unmapped.forEach { unmappedRange ->
          val (ruleMapped, ruleUnmapped) = mapTesselate(unmappedRange, rule)
          if (ruleMapped != null) {
            mapped += ruleMapped
          }
          newUnmapped.addAll(ruleUnmapped)
        }
        unmapped = newUnmapped
      }

      nums = mapped + unmapped

      state = seedMap.dst
    }

    return nums.minOf { it.first }
  }

  override fun part1(input: Pair<List<Long>, List<SeedMap>>): Long {
    return solveRanges(input.second, input.first.map { it .. it })
  }

  override fun part2(input: Pair<List<Long>, List<SeedMap>>): Long {
    return solveRanges(input.second, input.first.chunked(2).map { (start, len) -> start until (start + len) })
  }
}
