package ru.iisuslik;

/**
 * Exception that throws if Function or Supplier inside Task throws some exception e.
 * This e will be added as suppressed exception to this LightExecutionException.
 */
public class LightExecutionException extends Exception {
}
