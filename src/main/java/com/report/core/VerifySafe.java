package com.report.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.BeforeSuite;

/**
 * VerifySafe class provides method for safe assertion, wherein on assertion failure the failure is
 * put on to a HashMap and tests are continued. On Test Exit based on the HashMap, the status of the
 * test is determined
 * 
 * @author Nainappa Illi
 *
 */
public abstract class VerifySafe {
  private static Map<ITestResult, List<Throwable>> verificationFailuresHashMap =
      new HashMap<ITestResult, List<Throwable>>();

  private static final String ESCAPE_PROPERTY = "org.uncommons.reportng.escape-output";
  // protected static Long SYNC_DELAY= EnvParameters.syncDelayTime();

  protected void print(String s) {
    Reporter.log(s, true);
  }

  @BeforeSuite(alwaysRun = true)
  public void setupReport() {
    System.setProperty(ESCAPE_PROPERTY, "false"); // set this for TESTNG
  }

  /**
   * Description : This Method will get the current verification failure into a list
   * 
   * @return [List]
   */
  public static List<Throwable> getVerificationFailures() {
    List<Throwable> verificationFailures =
        verificationFailuresHashMap.get(Reporter.getCurrentTestResult());
    return verificationFailures == null ? new ArrayList<Throwable>() : verificationFailures;
  }

  /**
   * Description : This method will add verification failures to the hashmap
   * 
   * @param e
   */
  protected static void addVerificationFailure(Throwable e) {
    List<Throwable> verificationFailures = getVerificationFailures();
    verificationFailuresHashMap.put(Reporter.getCurrentTestResult(), verificationFailures);
    verificationFailures.add(e);
  }

  /**
   * Description : This Method will verify the the actual with expected string, will not throw
   * exception if the verification fails.
   * 
   * @param actual
   * @param expected
   * @param message
   */
  public static void verifySafely(Object actual, Object expected, String message) {
    try {
      Assert.assertEquals(actual, expected, message);
      LoggerUtil.log(
          "Expected value: " + expected + " Actual value: " + actual + " - PASSED : " + message);
    } catch (Throwable e) {
      LoggerUtil.log(
          "Expected value: " + expected + " Actual value: " + actual + " - FAILED : " + message);
      addVerificationFailure(e);
    }
  }

  /**
   * Description : This Method will verify the actual with the expected string, and throws runtime
   * exception if the verification fails, and the execution stops.
   * 
   * @param actual
   * @param expected
   * @param message
   * @throws RuntimeException
   */
  public static void verifyAssert(Object actual, Object expected, String message)
      throws RuntimeException {
    try {
      Assert.assertEquals(actual, expected, message);
      LoggerUtil.log(
          "Expected value: " + expected + " Actual value: " + actual + " - PASSED : " + message);
    } catch (AssertionError e) {
      LoggerUtil.log(
          "Expected value: " + expected + " Actual value: " + actual + " - FAILED  : " + message);
      throw new RuntimeException(e);
    } catch (Exception e) {
      LoggerUtil.log(
          "Expected value: " + expected + " Actual value: " + actual + " - FAILED  : " + message);
      throw new RuntimeException(e);
    }

  }

}
