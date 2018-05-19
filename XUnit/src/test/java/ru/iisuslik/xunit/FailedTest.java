package ru.iisuslik.xunit;

public class FailedTest {
  @Test
  public void failed() throws Exception {
    throw new Exception("wow");
  }
}
