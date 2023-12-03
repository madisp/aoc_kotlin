import com.google.common.truth.Truth.assertThat
import org.junit.Test

class Day12Test {
  @Test
  fun part1Tests() {
    val s = Day12

    assertThat(s.part1("[1,2,3]")).isEqualTo(6)
    assertThat(s.part1("""{"a":2,"b":4}""")).isEqualTo(6)

    assertThat(s.part1("[[[3]]]")).isEqualTo(3)
    assertThat(s.part1("""{"a":{"b":4},"c":-1}""")).isEqualTo(3)

    assertThat(s.part1("""{"a":[-1,1]}""")).isEqualTo(0)
    assertThat(s.part1("""[-1,{"a":1}]""")).isEqualTo(0)

    assertThat(s.part1("[]")).isEqualTo(0)
    assertThat(s.part1("{}")).isEqualTo(0)
  }

  @Test
  fun part2Tests() {
    val s = Day12

    assertThat(s.part2("""[1,2,3]""")).isEqualTo(6)
    assertThat(s.part2("""[1,{"c":"red","b":2},3]""")).isEqualTo(4)
    assertThat(s.part2("""{"d":"red","e":[1,2,3,4],"f":5}""")).isEqualTo(0)
    assertThat(s.part2("""[1,"red",5]""")).isEqualTo(6)
  }
}
