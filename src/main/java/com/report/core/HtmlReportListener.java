package com.report.core;

import java.io.File;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Level;
import org.testng.IExecutionListener;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.TestNG;
import org.testng.xml.XmlTest;

import com.report.exception.ItJumpStartReportException;
import com.report.exception.ReportConfigException;
import com.report.exception.ReportCreationException;
import com.report.utils.PropLoader;
import com.report.utils.Utils;

/**
 * This class overrides all the testng default listeners to configure the HTML reporting
 * 
 * @author Nainappa Illi
 *
 */

public class HtmlReportListener
    implements IInvokedMethodListener, ITestListener, ISuiteListener, IExecutionListener {

  public String sFilename;
  private static final String ESCAPE_PROPERTY = "org.uncommons.reportng.escape-output";
  private static String screenshotPath = "";
  public static String testResultVideos = "";
  private static String resultDir;
  private static boolean runParallel = false;
  private int retryCount = 0;

  public static String itJumpStart_ReportDir;
  private static String outdir;
  public static Utils utils = new Utils();
  public static final String CONFIG_FILE;
  public static final boolean CAPTURE_SCREENSHOT;
  public static final boolean CAPTURE_VIDEO;
  public static final String APPLICATION_NAME;
  public static final String ENVIRONMENT;
  public static final String SUITNAME;
  public static String BUILDNAME;
  public static final String TESTTYPE;
  public static final boolean OPENREPORT;
  public static final boolean GEN_ADVANCED_REPORT;
  public static final boolean ARCHIVE_REPORT;

  static {

    String custom_property = System.getProperty("customProperties");
    if (custom_property != null && !(custom_property.equalsIgnoreCase(""))) {
      CONFIG_FILE = custom_property;
    } else {
      CONFIG_FILE = "testconfig.properties";
    }
    Properties ReportProperties = new PropLoader().loadProperties(CONFIG_FILE);
    // Load CAPTURE_SCREENSHOT from capture.screenshot
    if (System.getProperty("capture.screenshot") != null) {
      CAPTURE_SCREENSHOT =
          System.getProperty("capture.screenshot").equalsIgnoreCase("true") ? true : false;
    } else if (ReportProperties.getProperty("capture.screenshot") != null) {
      CAPTURE_SCREENSHOT =
          ReportProperties.getProperty("capture.screenshot").equalsIgnoreCase("true") ? true
              : false;
    } else {
      CAPTURE_SCREENSHOT = true;
    }
    // Load CAPTURE_VIDEO from capture.video
    if (System.getProperty("capture.video") != null) {
      CAPTURE_VIDEO = System.getProperty("capture.video").equalsIgnoreCase("true") ? true : false;
    } else if (ReportProperties.getProperty("capture.video") != null) {
      CAPTURE_VIDEO =
          ReportProperties.getProperty("capture.video").equalsIgnoreCase("true") ? true : false;
    } else {
      CAPTURE_VIDEO = false;
    }
    // Load ENVIRONMENT from environment
    if (ReportProperties.getProperty("ENV") != null
        && !(ReportProperties.getProperty("ENV").equals(""))) {
      ENVIRONMENT = ReportProperties.getProperty("ENV");
    } else {
      ENVIRONMENT = "Default Environment";
    }
    
 // Load ENVIRONMENT from environment
    if (ReportProperties.getProperty("ApplicationName") != null
        && !(ReportProperties.getProperty("ApplicationName").equals(""))) {
      APPLICATION_NAME = ReportProperties.getProperty("ApplicationName");
    } else {
      APPLICATION_NAME = "Automation Test Execution Report";
    }
    
    // Load SUITNAME from suitname
    if (ReportProperties.getProperty("suitname") != null
        && !(ReportProperties.getProperty("suitname").equals(""))) {
      SUITNAME = ReportProperties.getProperty("suitname");
    } else {
      SUITNAME = "Default Suite";
    }
    BUILDNAME = utils.getVersion();
    // Load TESTTYPE from testtype
    if (ReportProperties.getProperty("testtype") != null
        && !(ReportProperties.getProperty("testtype").trim().equalsIgnoreCase(""))) {
      TESTTYPE = ReportProperties.getProperty("testtype");
    } else {
      TESTTYPE = "Regression Test";
    }
    // Load OPENREPORT from openreport
    if (ReportProperties.getProperty("openreport") != null
        && !(ReportProperties.getProperty("openreport").trim().equalsIgnoreCase(""))) {
      OPENREPORT = Boolean.parseBoolean(ReportProperties.getProperty("openreport"));
    } else {
      OPENREPORT = true;
    }
    // Load GEN_ADVANCED_REPORT from advanced.report
    if (ReportProperties.getProperty("advanced.report") != null
        && !(ReportProperties.getProperty("advanced.report").trim().equalsIgnoreCase(""))) {
      GEN_ADVANCED_REPORT = Boolean.parseBoolean(ReportProperties.getProperty("advanced.report"));
    } else {
      GEN_ADVANCED_REPORT = true;
    }
    // Load ARCHIVE_REPORT from archive.report
    if (ReportProperties.getProperty("archive.report") != null && !(ReportProperties.getProperty("archive.report").trim().equalsIgnoreCase(""))){
        ARCHIVE_REPORT = Boolean.parseBoolean(ReportProperties.getProperty("archive.report"));
    }else{
        ARCHIVE_REPORT = false ;
    }
    //ARCHIVE_REPORT = false;
  }

  /**
   * @return the screenshotPath
   */
  public static String getScreenshotPath() {
    return screenshotPath;
  }

  /**
   * @return true if tests are to be run in parallel, false otherwise
   */
  public static boolean getParallel() {
    return runParallel;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.testng.ITestListener#onTestFailedButWithinSuccessPercentage(org. testng .ITestResult)
   */
  @Override
  public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {
    // Do Nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.testng.ITestListener#onFinish(org.testng.ITestContext)
   */
  @Override
  public void onFinish(ITestContext context) {
    // recorder = null;
    removeIncorrectlyFailedTests(context);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.testng.ITestListener#onStart(org.testng.ITestContext)
   */
  @Override
  public void onStart(ITestContext context) {
    System.setProperty(ESCAPE_PROPERTY, "false"); // set this for TESTNG
    for (ITestNGMethod testMethod : context.getAllTestMethods()) {
      testMethod.setRetryAnalyzer(new RetryAnalyzer());
    }
    // recorder = null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.testng.ITestListener#onTestStart(org.testng.ITestResult)
   */
  @Override
  public void onTestStart(ITestResult iTestResult) {
    // Get the current test case name
    String sTestMethodName = iTestResult.getMethod().getMethodName();
    String sTestSuiteName = iTestResult.getTestClass().getRealClass().getSimpleName();
    LoggerUtil.log(
        "=====================================================================================");
    LoggerUtil.log("<<<*** START: " + sTestSuiteName + "." + sTestMethodName + " ***>>> ");
    int invocationCount = iTestResult.getMethod().getCurrentInvocationCount() + 1;
    String browser = iTestResult.getMethod().getXmlTest().getParameter("Browser");
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.testng.ITestListener#onTestFailure(org.testng.ITestResult)
   */
  /**
   * captures screenshot/video/updates the excel sheet on failure with the test details
   */
  @Override
  public void onTestFailure(ITestResult result) {
    // Get the current test name
    String sTestMethodName = result.getMethod().getMethodName();
    String sTestSuiteName = result.getTestClass().getRealClass().getSimpleName();
    String timeTaken = Long.toString((result.getEndMillis() - result.getStartMillis()) / 1000);
    String testName = result.getTestClass().getName() + "." + result.getMethod().getMethodName();
    int invocationCount = result.getMethod().getCurrentInvocationCount();
    String browser = result.getMethod().getXmlTest().getParameter("Browser");
    if (RetryAnalyzer.RETRY_ATTEMPTS != 0) {
      if (result.getMethod().getRetryAnalyzer().retry(result)) {
        retryCount++;
        LoggerUtil.log("Retrying test : " + testName + ", " + retryCount + " time(s)");
        result.setStatus(ITestResult.SUCCESS_PERCENTAGE_FAILURE);
      } else {
        LoggerUtil.log("Test failed after max allowed retries  : " + testName);
        retryCount = 0;
      }
    }

    if (GEN_ADVANCED_REPORT) {
      MainReporting.addTestMethodNode(result);
      MainReporting.reportError(result);
    }

    LoggerUtil.log("<<<*** END: " + sTestSuiteName + "." + sTestMethodName + " ***>>> ");
    LoggerUtil.log(
        "=====================================================================================");
    LoggerUtil.log("Test Failed :" + testName + ", Took " + timeTaken + " seconds");
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.testng.ITestListener#onTestSkipped(org.testng.ITestResult)
   */
  @Override
  public void onTestSkipped(ITestResult iTestResult) {
    String timeTaken =
        Long.toString((iTestResult.getEndMillis() - iTestResult.getStartMillis()) / 1000);
    String testName =
        iTestResult.getTestClass().getName() + "." + iTestResult.getMethod().getMethodName();
    LoggerUtil.log(
        "/////////////////////////////////////////////////////////////////////////////////////////");
    LoggerUtil.log("Test Skipped :" + testName + ", Took " + timeTaken + " seconds");
    if (GEN_ADVANCED_REPORT) {
      MainReporting.addTestMethodNode(iTestResult);
      StringBuffer methodsDependOn = new StringBuffer();
      for (String str : iTestResult.getMethod().getMethodsDependedUpon()) {
        methodsDependOn.append(str);
        methodsDependOn.append(", ");
      }
      if (!methodsDependOn.toString().equalsIgnoreCase("")) {
        MainReporting.addResultInfoNode(iTestResult, "Test has Dependent methods: "
            + methodsDependOn.substring(0, methodsDependOn.length() - 2), 4);
      }
      StringBuffer groupsDependedOn = new StringBuffer();
      for (String str : iTestResult.getMethod().getGroupsDependedUpon()) {
        groupsDependedOn.append(str);
        groupsDependedOn.append(", ");
      }
      if (!groupsDependedOn.toString().equalsIgnoreCase("")) {
        MainReporting.addResultInfoNode(iTestResult, "Test has Dependent groups: "
            + groupsDependedOn.substring(0, groupsDependedOn.length() - 2), 4);
      }
      MainReporting.reportWarning(iTestResult);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.testng.ITestListener#onTestSuccess(org.testng.ITestResult)
   */
  /**
   * updates the testlink,excel and removes the video on test success
   */
  @Override
  public void onTestSuccess(ITestResult iTestResult) {
    String sTestMethodName = iTestResult.getMethod().getMethodName();
    String sTestSuiteName = iTestResult.getTestClass().getRealClass().getSimpleName();
    String timeTaken =
        Long.toString((iTestResult.getEndMillis() - iTestResult.getStartMillis()) / 1000);
    String testName =
        iTestResult.getTestClass().getName() + "." + iTestResult.getMethod().getMethodName();

    if (retryCount > 1) {
      Reporter
          .log("Test '" + sTestMethodName + "' passed after " + (retryCount - 1) + " retrie(s)");
      retryCount = 1;
    }
    if (GEN_ADVANCED_REPORT) {
      MainReporting.addTestMethodNode(iTestResult);
      MainReporting.reportPass(iTestResult);
    }

    LoggerUtil.log("<<<*** END: " + sTestSuiteName + "." + sTestMethodName + " ***>>> ");
    LoggerUtil.log(
        "=====================================================================================");
    LoggerUtil.log("Test Passed :" + testName + ", Took " + timeTaken + " seconds");
  }

  /**
   * @param arg0
   * @param arg1
   */
  @Override
  public void beforeInvocation(IInvokedMethod arg0, ITestResult arg1) {
    // Do Nothing
  }

  /**
   * @param method
   * @param result
   */
  @Override
  public void afterInvocation(IInvokedMethod method, ITestResult result) {
    Reporter.setCurrentTestResult(result);

    if (method.isTestMethod()) {

      List<Throwable> verificationFailures = VerifySafe.getVerificationFailures();
      // if there are verification failures...
      if (verificationFailures.size() > 0) {
        // set the test to failed
        result.setStatus(ITestResult.FAILURE);

        // if there is an assertion failure add it to
        // verificationFailures
        if (result.getThrowable() != null) {
          verificationFailures.add(result.getThrowable());
        }

        int size = verificationFailures.size();
        // if there's only one failure just set that
        if (size == 1) {
          result.setThrowable(verificationFailures.get(0));
        } else {
          StringBuffer failureMessage =
              new StringBuffer("Multiple asserts failed (").append(size).append("):\n");
          for (int i = 0; i < size; i++) {
            failureMessage.append("Assertion ").append(i + 1).append(" of ").append(size)
                .append(":\n");
            Throwable t = verificationFailures.get(i);
            failureMessage.append(t.getMessage()).append("\n");
          }

          // final failure
          Throwable last = verificationFailures.get(size - 1);

          // set merged throwable
          Throwable merged = new Throwable(failureMessage.toString());
          merged.setStackTrace(last.getStackTrace());

          result.setThrowable(merged);
        }
      }
    }
  }

  /**
   * removes the incorrectly failed tests from the testresult
   * 
   * @param test
   * @return
   */
  private void removeIncorrectlyFailedTests(ITestContext test) {
    IResultMap failedwithSuccess = test.getFailedButWithinSuccessPercentageTests();
    int countOffailedwithSuccessResults = test.getSkippedTests().getAllResults().size();
    int countOfPassedResults = test.getPassedTests().getAllResults().size();
    int countOfFailedResults = test.getFailedTests().getAllResults().size();
    int countOfAllResults = test.getAllTestMethods().length;
    // remove rerun FailedButWithinSuccessPercentage tests
    if ((countOfAllResults == countOfPassedResults || countOfAllResults == countOfFailedResults)
        && countOffailedwithSuccessResults != 0) {
      for (ITestNGMethod method : test.getFailedButWithinSuccessPercentageTests().getAllMethods()) {
        failedwithSuccess.removeResult(method);
      }
    }

  }

  @Override
  public void onFinish(ISuite arg0) {
    // Do Nothing
  }

  @Override
  public void onStart(ISuite arg0) {
    File folder = new File(arg0.getOutputDirectory());
    File resultDir = new File(folder.getParent());
    File screenshotFolder = new File(folder.getParent() + File.separator + "screenshots");
    if (!screenshotFolder.exists()) {
      screenshotFolder.mkdirs();
    }
    screenshotPath = screenshotFolder.getPath();
    File videoFolder = new File(folder.getParent() + File.separator + "video");
    if (!videoFolder.exists()) {
      videoFolder.mkdirs();
    }
    testResultVideos = videoFolder.getPath();

    HtmlReportListener.resultDir = resultDir.getPath();

    if (!arg0.getParallel().equalsIgnoreCase("false")) {
      if (arg0.getParallel().equalsIgnoreCase("methods")) {
        throw new ItJumpStartReportException(
            "parallel=\"methods\" at suite level in testng XML is not supported as test methods are not thread safe");
      } else {
        runParallel = true;
        LoggerUtil.log("Video Recording will be disabled as test(s) will be run in parallel",
            Level.WARN);
      }
    }
    if (!runParallel) {
      for (XmlTest test : arg0.getXmlSuite().getTests()) {
        if (!test.getParallel().equals("false")) {
          // System.out.println(test.getParallel());
          if (test.getParallel().equals("methods")) {
            throw new ItJumpStartReportException(
                "parallel=\"methods\" at test level in testng XML is not supported as test methods are not thread safe");
          } else {
            runParallel = true;
            LoggerUtil.log("Video Recording will be disabled as test(s) will be run in parallel",
                Level.WARN);
            break;
          }
        }
      }
    }

  }

  @SuppressWarnings("deprecation")
  @Override
  public void onExecutionStart() {
    Method f;
    outdir = "";
    if (ARCHIVE_REPORT) {
      String archivePath = "";
      try {
        f = TestNG.class.getMethod("setOutputDirectory", String.class);
        DateFormat dtYearFormat = new SimpleDateFormat("yyyy");
        DateFormat dtMonthFormat = new SimpleDateFormat("M");
        String strCurrYear = dtYearFormat.format(new Date());
        String strCurrMonth = dtMonthFormat.format(new Date());
        archivePath = System.getProperty("user.home") + File.separator
            + "Advanced_Test_Execution_Reports" + File.separator + HtmlReportListener.TESTTYPE
            + " Results" + File.separator + strCurrYear + File.separator
            + theMonth(Integer.parseInt(strCurrMonth)) + File.separator;
        archivePath += Utils.getCurrentDateTime("ddMMMyy_hhmmss") + "_" + "TestExecution";
        // createFolder(strReportFolderPath + strReportFolderName);
        Thread.sleep(2000);
        try {
          if (!(new File(archivePath).mkdirs())) {
            throw new ReportConfigException("Failed to create the archive report directory");
          }
        } catch (Exception e) {
          throw new ReportConfigException(e.getMessage(), e);
        }
        // outdir = archivePath + File.separator + "TestNG-Results";
        outdir = archivePath;
        LoggerUtil.updateLog4jConfiguration(
            archivePath + File.separator + "logs" + File.separator + "report.log");
        Object a[] = {outdir};
        f.invoke(TestNG.getDefault(), a);
      } catch (Exception e) {
        // e.printStackTrace();
        throw new ReportConfigException("Unable to set archive report directory", e);
      }
    } else {
      try {
        f = TestNG.class.getMethod("getOutputDirectory");
        Object a[] = {};
        outdir = (String) f.invoke(TestNG.getDefault(), a);
      } catch (Exception e) {
        // e.printStackTrace();
        throw new ReportConfigException("Unable to read report directory", e);
      }
      try {
        LoggerUtil.updateLog4jConfiguration(
            outdir + File.separator + "logs" + File.separator + "report.log");
      } catch (Exception e) {
        LoggerUtil.log(e.getMessage());
      }
    }
    if (GEN_ADVANCED_REPORT) {
      if (outdir.endsWith("test-output") || outdir.endsWith("surefire-reports")) {
        itJumpStart_ReportDir = outdir + File.separator + "Advanced_Test_Execution_Reports";
      } else if (outdir.equalsIgnoreCase("")) {
        throw new ReportConfigException("Unable to read report directory");
      } else {
        itJumpStart_ReportDir = outdir + File.separator + "Advanced_Test_Execution_Reports";;
      }
      File outputDirectory = new File(itJumpStart_ReportDir);
      outputDirectory.mkdirs();
      if (outputDirectory.exists()) {
        MainReporting.createInitXML(itJumpStart_ReportDir);
        MainReporting.writeEnvDetailsToXMLReport();
        MainReporting.addTestCaseNode(HtmlReportListener.SUITNAME + " Test Results");
      } else {
        throw new ReportCreationException("Unable to create XML report");
      }
    }
  }

  @SuppressWarnings("deprecation")
  @Override
  public void onExecutionFinish() {
    if (GEN_ADVANCED_REPORT) {
      MainReporting.generateHtmlReport();
    }
    Method f;
    try {
      f = TestNG.class.getMethod("getOutputDirectory");
      Object a[] = {};
      outdir = (String) f.invoke(TestNG.getDefault(), a);

    } catch (Exception e) {
      // e.printStackTrace();
      LoggerUtil.log(e.toString(), Level.WARN);
    }
    LoggerUtil.log("Report Directory: " + outdir.replaceAll("TestNG-Results", ""));
  }

  private static String theMonth(int month) {
    String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August",
        "September", "October", "November", "December"};
    return monthNames[month - 1];
  }

  /**
   * @return the report output directory
   */
  public static String getOutdir() {
    return outdir;
  }
}
