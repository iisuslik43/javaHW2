package ru.iisuslik.xunit;

import java.io.IOException;

public class AllTests {

  @Test
  public void success() {
    System.out.println(Thread.currentThread());
  }

  @Test(ignore = "NO REASON")
  public void ignored() {
    System.out.println(Thread.currentThread());
  }

  @Test
  public void failed() throws Exception {
    System.out.println(Thread.currentThread());
    Thread.sleep(20);
    throw new IOException("Unexpected exception");
  }

  @Test (expected = IOException.class)
  public void exception() throws IOException {
    throw new IOException("Expected exception");
  }

}
