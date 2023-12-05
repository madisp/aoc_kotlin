import com.google.common.truth.Truth.assertThat
import org.junit.Test
import utils.Parse

class MultilineTest {

  @Parse("{first}\n{second}")
  data class Multiline(
    val first: String,
    val second: String,
  )

  @Parse("eat\nthis\n{first}\n{second}")
  data class MultilinePrefixLiteral(
    val first: String,
    val second: String,
  )

  @Parse("{first}\n{second}\neat\nthis")
  data class MultilineSuffixLiteral(
    val first: String,
    val second: String,
  )

  @Parse("{header}\n{r '\n' values}")
  data class MultilineRepeats(
    val header: String,
    val values: List<String>,
  )

  @Test
  fun testParse() {
    val value = parseMultiline("foo\nbar")

    assertThat(value).isEqualTo(Multiline("foo", "bar"))
  }

  @Test
  fun testCompactsNewlines() {
    val value = parseMultiline("foo\n\n\nbar")

    assertThat(value).isEqualTo(Multiline("foo", "bar"))
  }

  @Test
  fun testPrefix() {
    val value = parseMultilinePrefixLiteral("eat\nthis\nfoo\nbar")

    assertThat(value).isEqualTo(MultilinePrefixLiteral("foo", "bar"))
  }

  @Test
  fun testSuffix() {
    val value = parseMultilineSuffixLiteral("foo\nbar\neat\nthis")

    assertThat(value).isEqualTo(MultilineSuffixLiteral("foo", "bar"))
  }

  @Test
  fun testRepeats() {
    val value = parseMultilineRepeats("shopping list\n\nmilk\neggs\nflour")
    assertThat(value).isEqualTo(
      MultilineRepeats("shopping list", listOf("milk", "eggs", "flour"))
    )
  }
}
