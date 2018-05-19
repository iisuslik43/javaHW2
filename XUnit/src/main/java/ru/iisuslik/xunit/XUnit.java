package ru.iisuslik.xunit;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class that runs tests in class, test class methods can be marked by some annotations
 */
public class XUnit {
  private Object instance;
  private Map<Class<?>, List<Method>> methods = new HashMap<>();
  private Set<Class<?>> annotations = new HashSet<>();
  private int testsCount = 0;
  private int successfulTestsCount = 0;
  private int ignoredTestsCount = 0;


  private XUnit(@NotNull Class<?> testClass) {
    try {
      instance = testClass.newInstance();
    } catch (InstantiationException e) {
      printError("Class object cannot be instantiated", e);
    } catch (IllegalAccessException e) {
      printError("Can't access to create class instance", e);
    }
    initialize();
    addMethodsToLists(testClass);
  }

  private static void printError(String message, Throwable e) {
    System.err.println(message);
    e.printStackTrace();
  }

  private void initialize() {
    annotations.add(Before.class);
    annotations.add(Test.class);
    annotations.add(BeforeClass.class);
    annotations.add(After.class);
    annotations.add(AfterClass.class);
    for (Class<?> a : annotations) {
      methods.put(a, new ArrayList<>());
    }
  }

  private void addMethodsToLists(Class<?> testClass) {
    for (Method method : testClass.getDeclaredMethods()) {
      for (Annotation a : method.getAnnotations()) {
        if (annotations.contains(a.annotationType())) {
          methods.get(a.annotationType()).add(method);
          if (a.annotationType().equals(Test.class)) {
            testsCount++;
          }
        }
      }
    }
  }

  private boolean invokeMethod(Method method) {
    try {
      method.invoke(instance);
    } catch (IllegalAccessException e) {
      printError("Can't access to run method " + method.getName(), e);
      return false;
    } catch (InvocationTargetException e) {
      printError("Exception inside method happened " + method.getName(), e.getTargetException());
      return false;
    }
    return true;
  }

  private Throwable invokeTest(Method test) {
    try {
      test.invoke(instance);
    } catch (IllegalAccessException e) {
      return e;
    } catch (InvocationTargetException e) {
      Test annotation = test.getAnnotation(Test.class);
      Class<?> expected = annotation.expected();
      if (!e.getTargetException().getClass().equals(expected)) {
        return e.getTargetException();
      }
    }
    return null;
  }

  private void runAllMethods(List<Method> methods) {
    for (Method method : methods) {
      invokeMethod(method);
    }
  }

  private void runAllTests(List<Method> tests) {
    for (Method test : tests) {
      Test testAnnotation = test.getAnnotation(Test.class);
      if (testAnnotation.ignore().equals("")) {
        runAllMethods(methods.get(Before.class));
        long time = System.currentTimeMillis();
        Throwable testResult = invokeTest(test);
        time = System.currentTimeMillis() - time;
        if (testResult == null) {
          successfulTestsCount++;
          System.out.println("Test " + test.getName() + " passed in " + time + " millis");
        } else {
          printError("Test " + test.getName() + " failed in " + time + " millis", testResult);
        }
        runAllMethods(methods.get(After.class));
      } else {
        ignoredTestsCount++;
        System.out.println("Test " + test.getName() + " has been ignored with reason: " + testAnnotation.ignore());
      }
    }
  }

  private TestsResult runTests() {
    runAllMethods(methods.get(BeforeClass.class));
    runAllTests(methods.get(Test.class));
    runAllMethods(methods.get(AfterClass.class));
    return printStatistics();
  }

  private TestsResult printStatistics() {
    int failedTestsCount = testsCount - successfulTestsCount - ignoredTestsCount;
    System.out.println("--------------------------");
    System.out.println(successfulTestsCount + " tests passed, " + failedTestsCount + " tests failed, " + ignoredTestsCount + " tests ignored");
    System.out.println("--------------------------");
    return new TestsResult(testsCount, successfulTestsCount, ignoredTestsCount);
  }


  /**
   * Runs test in .class file
   *
   * @param fileName    path to file
   * @param packageName class package name + class name
   * @return tests result (some statistics)
   */
  public static TestsResult runTests(String fileName, String packageName) {
    try {
      URL url = (new File(fileName)).toURI().toURL();
      ClassLoader loader = new URLClassLoader(new URL[]{url});
      System.out.println(packageName);
      Class<?> testClass = loader.loadClass(packageName);
      XUnit xUnit = new XUnit(testClass);
      return xUnit.runTests();
    } catch (MalformedURLException e) {
      printError("Can't parse URL", e);
    } catch (ClassNotFoundException e) {
      printError("There is no such class", e);
    } catch (ArrayIndexOutOfBoundsException e) {
      System.err.println("First argument should be path to test .class file, second - class.package.className");
    }
    return null;
  }

  /**
   * Runs tests in class
   *
   * @param args first arg should be path to file, second - package
   */
  public static void main(String[] args) {
    try {
      String fileName = args[0];
      String packageName = args[1];
      runTests(fileName, packageName);
    } catch (ArrayIndexOutOfBoundsException e) {
      System.err.println("First argument should be path to test .class file, second - class.package.className");
    }
  }

  /**
   * Data class to storage tests info
   */
  public static class TestsResult {
    private int testsCount;
    private int successfulTestsCount;
    private int ignoredTestsCount;

    /**
     * Creates TestsResult
     *
     * @param testsCount           number of all tests
     * @param successfulTestsCount number of successful tests
     * @param ignoredTestsCount    number of ignored test
     */
    public TestsResult(int testsCount, int successfulTestsCount, int ignoredTestsCount) {
      this.testsCount = testsCount;
      this.successfulTestsCount = successfulTestsCount;
      this.ignoredTestsCount = ignoredTestsCount;
    }

    /**
     * Getter
     *
     * @return number of ignored tests
     */
    public int getIgnoredTestsCount() {
      return ignoredTestsCount;
    }

    /**
     * Getter
     *
     * @return number of successful tests
     */
    public int getSuccessfulTestsCount() {
      return successfulTestsCount;
    }

    /**
     * Getter
     *
     * @return number of all tests
     */
    public int getTestsCount() {
      return testsCount;
    }

    /**
     * Getter
     *
     * @return number of failed tests
     */
    public int getFailedTestsCount() {
      return testsCount - successfulTestsCount - ignoredTestsCount;
    }
  }
}
