package foo;

import org.junit.Test;
import static org.junit.Assert.*;

public class FooTest {

  @Test
  public void testAdd() throws Exception {
	  int actual = 2;
	assertEquals(Foo.div(10, 5), actual);
  }
}
