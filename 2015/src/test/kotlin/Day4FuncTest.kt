import Day4Func.hash
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class Day4FuncTest {
  @Test
  fun testHash() {
    assertThat(hash("abcdef609043")).startsWith("000001dbbfa")
  }
}
