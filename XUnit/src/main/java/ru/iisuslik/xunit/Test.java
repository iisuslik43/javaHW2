package ru.iisuslik.xunit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks that method is test
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test {
  /**
   * Marks that method should be ignored with some message
   */
  public String ignore() default "";

  /**
   * Marks that some exception expected in test
   */
  public Class<?> expected() default Object.class;
}
