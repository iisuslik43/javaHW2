package ru.iisuslik.xunit;

public class IgnoredTest {
  @Test(ignore = "WHY")
  public void ignored() throws Exception {
    throw new Exception("WOW");
  }
}
