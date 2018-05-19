package ru.iisuslik.xunit;

public class AfterTest {
  public static boolean after = false;
  public static boolean afterClass = false;
  @After
  public void after() {
    after = true;
  }

  @AfterClass
  public void afterClass() {
    afterClass = true;
  }

  @Test
  public void test() throws Exception {
  }
}
