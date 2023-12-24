import com.google.common.truth.Truth.assertThat
import org.junit.Test
import utils.Parse

class DefaultValueTest {
  @Parse("{a} = {b}")
  data class DefaultValues(
    val a: Int,
    val b: Int,
    val c: Int = 0,
  )

  @Test
  fun default() {
    val values = parseDefaultValues("2 = 3")
    assertThat(values.a).isEqualTo(2)
    assertThat(values.b).isEqualTo(3)
    assertThat(values.c).isEqualTo(0)
  }
}
