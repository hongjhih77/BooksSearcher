import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RegexTest {

  @Test
  public void isbnsTest(){
    String regex = "[0-9, /,]+";
    assertThat("1234,234,345a".matches(regex)).isFalse();
    assertThat("1234,234,34".matches(regex)).isTrue();
    assertThat("1234".matches(regex)).isTrue();
  }
}
