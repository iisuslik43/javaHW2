package ru.iisuslik.xunit;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for XUnit class
 */
public class XUnitTest {

  /**
   * Runs tests in file with only ignored test
   */
  @Test
  public void ignored() {
    XUnit.TestsResult res = XUnit.runTests("src/test/resources/IgnoredTest.class",
        "ru.iisuslik.xunit.IgnoredTest");
    assertResult(1, 0, 1, res);
  }

  /**
   * Runs tests in file with only failed test
   */
  @Test
  public void failed() {
    XUnit.TestsResult res = XUnit.runTests("src/test/resources/ru/iisuslik/xunit/FailedTest.class",
        "ru.iisuslik.xunit.FailedTest");
    assertResult(1, 0, 0, res);
  }

  /**
   * Runs tests in file with only successful test
   */
  @Test
  public void success() {
    XUnit.TestsResult res = XUnit.runTests("src/test/resources/ru/iisuslik/xunit/SuccessfulTest.class",
        "ru.iisuslik.xunit.SuccessfulTest");
    assertResult(1, 1, 0, res);
  }

  /**
   * Runs tests in file with all kind of tests
   */
  @Test
  public void all() {
    XUnit.TestsResult res = XUnit.runTests("src/test/resources/ru/iisuslik/xunit/AllTests.class",
        "ru.iisuslik.xunit.AllTests");
    assertResult(4, 2, 1, res);
  }

  /**
   * Runs tests in file with Before and BeforeClass annotations
   */
  @Test
  public void before() {
    XUnit.TestsResult res = XUnit.runTests("src/test/resources/ru/iisuslik/xunit/BeforeTest.class",
        "ru.iisuslik.xunit.BeforeTest");
    assertResult(1, 1, 0, res);
  }

  /**
   * Runs tests in file with After and AfterClass annotations
   */
  @Test
  public void after() {
    XUnit.TestsResult res = XUnit.runTests("src/test/resources/ru/iisuslik/xunit/AfterTest.class",
        "ru.iisuslik.xunit.AfterTest");
    assertResult(1, 1, 0, res);
    assertTrue(ru.iisuslik.xunit.AfterTest.after);
    assertTrue(ru.iisuslik.xunit.AfterTest.afterClass);
  }

  private void assertResult(int all, int success, int ignored, XUnit.TestsResult res) {
    assertEquals(all, res.getTestsCount());
    assertEquals(success, res.getSuccessfulTestsCount());
    assertEquals(ignored, res.getIgnoredTestsCount());
    assertEquals(all - success - ignored, res.getFailedTestsCount());
  }

}