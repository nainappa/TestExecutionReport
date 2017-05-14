package com.report.core;

import java.util.Properties;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import com.report.utils.PropLoader;

/**
 * This class is for implementing retry analyzer. When the test case gets failed due to consistency,
 * We can use these methods to try executing the same method for n no.of times.
 * 
 * @author Nainappa Illi
 *
 */

public class RetryAnalyzer implements IRetryAnalyzer {
  // Set this to twice the number of times to retry
  private static int remainingRetries;
  public static final int RETRY_ATTEMPTS;

  static {
    Properties RetryProperties = new PropLoader().loadProperties(HtmlReportListener.CONFIG_FILE);

    if (System.getProperty("retry.attempts") != null
        && !(System.getProperty("retry.attempts").equals(""))) {
      int retry_count = 0;
      try {
        retry_count = Integer.parseInt(System.getProperty("retry.attempts"));
      } catch (NumberFormatException e) {
        retry_count = 0;
      } finally {
        RETRY_ATTEMPTS = retry_count;
      }
    } else if (RetryProperties.getProperty("retry.attempts") != null
        && !(RetryProperties.getProperty("retry.attempts").equals(""))) {
      int retry_count = 0;
      try {
        retry_count = Integer.parseInt(RetryProperties.getProperty("retry.attempts"));
      } catch (NumberFormatException e) {
        retry_count = 0;
      } finally {
        RETRY_ATTEMPTS = retry_count;
      }
    } else {
      RETRY_ATTEMPTS = 0;
    }
    remainingRetries = RETRY_ATTEMPTS * 2;
  }

  /**
   * This is the overridden method of testng native method for retrying the same method.
   */
  @Override
  public boolean retry(ITestResult result) {
    if (remainingRetries > 0) {
      result.setStatus(ITestResult.SUCCESS_PERCENTAGE_FAILURE);
      remainingRetries = remainingRetries - 1;
      return true;
    } else {
      return false;
    }
  }
}
