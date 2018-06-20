package ru.iisuslik.xunit;

public class BeforeTest {
  private boolean beforeClass = false;
  private boolean before = false;

  @Before
  public void before() {
    before = true;
  }

  @BeforeClass
  public void beforeClass() {
    beforeClass = true;
  }

  @Test
  public void test() throws Exception {
    if (! beforeClass || ! before) {
      throw new Exception("no init");
    }
  }
}
