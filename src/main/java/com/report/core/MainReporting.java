package com.report.core;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.Writer;
import java.net.InetAddress;
import java.net.URI;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Level;
import org.jdom2.CDATA;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.testng.ITestResult;

import com.report.exception.ReportCreationException;
import com.report.utils.Utils;

/**
 * This class holds all the methods for generating html report
 * 
 * @author Nainappa Illi
 *
 */

public class MainReporting {
  private static String strReportFilePath = "";
  private static String strReportFolderPath = "";

  // private static boolean blnTestReportMethodFlag;
  private static boolean blnReportRowIDFlag;
  private static String strTestRowNumber;

  public static final int PASSED = 1;
  public static final int FAILED = 0;
  public static final int SKIPPED = 2;

  private static final String[] CSSRESOURCES =
      new String[] {"lightbox.min.css", "Report.css", "jquery-ui-1.8.16.custom.css"};

  private static final String[] IMGRESOURCES = new String[] {"error_bg.png", "error_sign.png",
      "False.ico", "FirstDown.ico", "loader.gif", "Logo.jpeg", "Minus.ico", "panorama.png",
      "Plus.ico", "skip.ico", "True.ico", "ui-bg_diagonals-thick_18_b81900_40x40.png",
      "ui-bg_diagonals-thick_20_666666_40x40.png", "ui-bg_flat_10_000000_40x100.png",
      "ui-bg_glass_100_f6f6f6_1x400.png", "ui-bg_glass_100_fdf5ce_1x400.png",
      "ui-bg_glass_65_ffffff_1x400.png", "ui-bg_gloss-wave_35_f6a828_500x100.png",
      "ui-bg_highlight-soft_100_eeeeee_1x100.png", "ui-bg_highlight-soft_75_ffe45c_1x100.png",
      "ui-icons_222222_256x240.png", "ui-icons_228ef1_256x240.png", "ui-icons_ef8c08_256x240.png",
      "ui-icons_ffd27a_256x240.png", "ui-icons_ffffff_256x240.png"};

  private static final String[] JSRESOURCES = new String[] {"Chart.min.js", "jquery-1.6.4.min.js",
      "jquery.ui.core.min.js", "jquery.ui.rlightbox.min.js", "jquery.ui.widget.min.js"};

  @SuppressWarnings("unused")
  private static String limitChars(String strValueToBeLimited, int intNoChar) {
    String strLimitedValue = "";
    if (strValueToBeLimited.length() < intNoChar) {
      strLimitedValue = strValueToBeLimited;
    } else {
      strLimitedValue = strValueToBeLimited.substring(0, intNoChar - 1);
    }
    return strLimitedValue;
  }

  public static void setStrReportFolderPath(String strReportFolderPath) {
    MainReporting.strReportFolderPath = strReportFolderPath;
    MainReporting.strReportFilePath = strReportFolderPath + File.separator + "TestReport.xml";
  }

  public static String getStrReportFolderPath() {
    return strReportFolderPath;
  }

  public static String getStrReportFilePath() {
    return strReportFilePath;
  }

  public static void createInitXML(String strReportFolderPath) {
    setStrReportFolderPath(strReportFolderPath);
    try {
      String xmlFile = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
      xmlFile = xmlFile + "<?xml-stylesheet href='Report11.xsl' type=\'text/xsl\'?>\n";
      xmlFile = xmlFile + "\n<Report LastXMLNum=\"1\">" + "\n</Report>";

      SAXBuilder builder = new SAXBuilder();
      Document doc = builder.build(new StringReader(xmlFile));

      writeToXMLFile(doc);
    } catch (Exception e) {
      throw new ReportCreationException("Unable to create the report file", e);
    }
  }

  private static void writeToXMLFile(Document doc) throws IOException {
    Writer writer;
    writer = new FileWriter(new File(strReportFilePath));
    XMLOutputter xmlOutput = new XMLOutputter(Format.getRawFormat());
    xmlOutput.setFormat(Format.getPrettyFormat().setEncoding("UTF-8"));
    xmlOutput.output(doc, writer);
  }

  private static Document getXMLDocument() throws JDOMException, IOException {
    SAXBuilder builder = new SAXBuilder();
    File xmlFile = new File(strReportFilePath);
    if (!xmlFile.exists()) {
      throw new ReportCreationException("Unable to locate the report file");
    }
    Document doc = null;
    doc = (Document) builder.build(xmlFile);
    return doc;
  }

  /**
   * This method sets all the header values to the corresponding nodes in the XML.
   */
  public static void writeEnvDetailsToXMLReport() {
    try {
      Document doc = getXMLDocument();
      Element rootElement = doc.getRootElement();

      // Add the attribute to the root node
      rootElement.setAttribute("ApplicationName", HtmlReportListener.APPLICATION_NAME);
      rootElement.setAttribute("Env", HtmlReportListener.ENVIRONMENT);
      rootElement.setAttribute("RunFlow", HtmlReportListener.SUITNAME);
      rootElement.setAttribute("BuildVersion", HtmlReportListener.BUILDNAME);
      rootElement.setAttribute("Host", InetAddress.getLocalHost().getHostName());
      rootElement.setAttribute("User", System.getProperty("user.name"));
      rootElement.setAttribute("XMLNum", String.valueOf(1));
      rootElement.setAttribute("DateTime", Utils.getCurrentDateTime("dd-MMM-yyyy hh:mm:ss.SSS"));

      rootElement.setAttribute("PrevXMLFileName", "");
      rootElement.setAttribute("NextXMLFileName", "");

      // rootElement.setAttribute("SeleniumMode",
      // WebTestListeners.GRID_ENABLED ? "Grid" :
      // "Standalone");
      rootElement.setAttribute("Screenshot",
          HtmlReportListener.CAPTURE_SCREENSHOT ? "On Failure" : "Turned Off");
      rootElement.setAttribute("Video",
          (HtmlReportListener.CAPTURE_VIDEO && !HtmlReportListener.getParallel()) ? "On Failure"
              : "Turned Off");

      writeToXMLFile(doc);

    } catch (Exception e) {
      throw new ReportCreationException("Unable to write Env details to report", e);
    }
  }

  /**
   * This method adds the test case nodes for the generated XML report
   * 
   * @param strTestCaseID
   */
  public static void addTestCaseNode(String strTestCaseID) {
    try {
      blnReportRowIDFlag = true;
      strTestRowNumber = "1";
      Document doc = getXMLDocument();
      Element rootNode = doc.getRootElement();
      // Create a new Test Suite node
      Element childTestCase = new Element("TestCase");

      // Add the attribute to the child
      List<Element> lstTestCase = rootNode.getChildren("TestCase");

      int newTestCaseID = lstTestCase.size() + 1;
      childTestCase.setAttribute("ID", String.valueOf(newTestCaseID));
      childTestCase.setAttribute("Desc", strTestCaseID);
      childTestCase.setAttribute("TCStatus", String.valueOf(SKIPPED));
      childTestCase.setAttribute("Row", strTestRowNumber); // Adding
                                                           // number
                                                           // tag
                                                           // having id
                                                           // attribute
                                                           // to root
                                                           // element
      rootNode.addContent(childTestCase);

      writeToXMLFile(doc);

    } catch (Exception e) {
      throw new ReportCreationException("Unable to add test ", e);
    }
  }

  /**
   * This method adds the test activity nodes for the generated XML report
   * 
   * @param strDescription
   * @throws IOException
   * @throws JDOMException
   */
  private static void addTestActivityNode(String strDescription) throws IOException, JDOMException {

    Document doc = getXMLDocument();
    List<Element> lstTestCase = doc.getRootElement().getChildren("TestCase");

    Element currentTestCase = (Element) lstTestCase.get(lstTestCase.size() - 1);

    // Create a new TestActivity node
    Element childTestActivity = new Element("TestActivity");

    // Add the attribute to the child
    List<Element> lstTestActivity = currentTestCase.getChildren("TestActivity");
    boolean testActivityElement = false;
    for (Element activity : lstTestActivity) {
      if (activity.getAttributeValue("Desc").equalsIgnoreCase(strDescription)) {
        testActivityElement = true;
      }
    }
    if (!testActivityElement) {
      int newTestActivityID = lstTestActivity.size() + 1;
      childTestActivity.setAttribute("ID", String.valueOf(newTestActivityID));
      childTestActivity.setAttribute("Desc", strDescription);
      currentTestCase.addContent(childTestActivity);
      writeToXMLFile(doc);
    }
  }

  /**
   * This method adds the test method nodes for the generated XML report
   * 
   * @param result
   */
  public static void addTestMethodNode(ITestResult result) {

    String browser = result.getMethod().getXmlTest().getParameter("Browser");
    String strTestMethod = "";
    if (browser == null || browser.equalsIgnoreCase("")) {
      strTestMethod = result.getMethod().getMethodName();
    } else {
      strTestMethod = result.getMethod().getMethodName() + browser;
    }

    String sTestClassName = result.getTestClass().getRealClass().getCanonicalName();
    String strIteration = String.valueOf(result.getMethod().getCurrentInvocationCount());
    String sTestXMLName = result.getMethod().getXmlTest().getName();
    String strDescription = strTestMethod;
    try {
      // blnTestReportMethodFlag= true;

      addTestActivityNode(sTestXMLName + " - " + sTestClassName);

      Document doc = getXMLDocument();

      List<Element> lstTestCase = doc.getRootElement().getChildren("TestCase");
      Element currentTestCase = (Element) lstTestCase.get(lstTestCase.size() - 1);
      List<Element> lstTestActivity = currentTestCase.getChildren("TestActivity");
      Element currentTestActivity = null;
      for (Element activity : lstTestActivity) {
        if (activity.getAttributeValue("Desc")
            .equalsIgnoreCase(sTestXMLName + " - " + sTestClassName)) {
          currentTestActivity = activity;
        }
      }
      if (currentTestActivity != null) {
        List<Element> lstTestMethod = currentTestActivity.getChildren("TestMethod");
        Element currentTestMethod = getTestMethodNode(lstTestMethod, strTestMethod, strIteration);
        // Create a new TestMethod node
        if (currentTestMethod == null) {
          Element childTestMethod = new Element("TestMethod");
          // Add the attribute to the child
          lstTestMethod = currentTestActivity.getChildren("TestMethod");
          int newTestMethodID = lstTestMethod.size() + 1;
          childTestMethod.setAttribute("ID", String.valueOf(newTestMethodID));
          childTestMethod.setAttribute("Desc", strDescription);
          childTestMethod.setAttribute("Iter", strIteration);
          // Adding number tag having id attribute to root element
          currentTestActivity.addContent(childTestMethod);
          XMLOutputter xmlOutput = new XMLOutputter(Format.getRawFormat());
          xmlOutput.setFormat(Format.getPrettyFormat().setEncoding("US-ASCII"));
          xmlOutput.output(doc, new FileWriter(strReportFilePath));
        }
      } else {
        throw new ReportCreationException("Failed to create the TestActivity Node");
      }
    } catch (Exception e) {
      throw new ReportCreationException("Failed to create test method node", e);
    }
  }

  /**
   * This method adds the result nodes for the generated XML report
   * 
   * @param result
   * @param intStatus
   * @return
   */
  public static boolean addResultNode(ITestResult result, int intStatus) {

    String browser = result.getMethod().getXmlTest().getParameter("Browser");
    String strTestMethod = "";
    if (browser == null || browser.equalsIgnoreCase("")) {
      strTestMethod = result.getMethod().getMethodName();
    } else {
      strTestMethod = result.getMethod().getMethodName() + browser;
    }

    String strDuration = Long.toString((result.getEndMillis() - result.getStartMillis()) / 1000);
    String testName = result.getTestClass().getName() + "." + result.getMethod().getMethodName();
    String strIteration = String.valueOf(result.getMethod().getCurrentInvocationCount());
    String sTestClassName = result.getTestClass().getRealClass().getCanonicalName();

    String strDescription = "";
    if (intStatus == 1)
      strDescription = "Test Passed: " + testName;
    if (intStatus == 0)
      strDescription =
          "Test Failed: " + testName + " ; Exception Occured:" + result.getThrowable().toString();
    if (intStatus == 2)
      strDescription = "Test Skipped: " + testName + (result.getThrowable() != null
          ? " ; Exception Occured:" + result.getThrowable().toString() : "");
    String sTestXMLName = result.getMethod().getXmlTest().getName();

    try {
      strTestRowNumber = "1";

      Document doc = getXMLDocument();

      List<Element> lstTestCase = doc.getRootElement().getChildren("TestCase");
      Element currentTestCase = (Element) lstTestCase.get(lstTestCase.size() - 1);
      List<Element> lstTestActivity = currentTestCase.getChildren("TestActivity");

      Element currentTestActivity = null;
      for (Element activity : lstTestActivity) {
        if (activity.getAttributeValue("Desc")
            .equalsIgnoreCase(sTestXMLName + " - " + sTestClassName)) {
          currentTestActivity = activity;
        }
      }

      List<Element> lstTestMethod = currentTestActivity.getChildren("TestMethod");
      // Element currentTestMethod = (Element)
      // lstTestMethod.get(lstTestMethod.size() - 1);
      Element currentTestMethod = getTestMethodNode(lstTestMethod, strTestMethod, strIteration);

      // Create a new MethodResult node
      Element childMethodResult = new Element("MethodResult");

      // Add the attribute to the child
      childMethodResult.setAttribute("Status", String.valueOf(intStatus));
      childMethodResult.setAttribute("Time", String.valueOf(new Date()));

      // - Giving ID for the first failed node
      if (blnReportRowIDFlag && (intStatus % 10 == 0)) {
        childMethodResult.setAttribute("ID", "fail_" + strTestRowNumber);
        blnReportRowIDFlag = false;
      }
      childMethodResult.setAttribute("duration", strDuration);
      childMethodResult.setText(strDescription);

      currentTestMethod.addContent(childMethodResult);

      if (intStatus == 0) {
        Element stacktrace = createStackTraceElement(result);
        currentTestMethod.addContent(stacktrace);
      }
      if (intStatus == 2) {
        for (ITestResult result1 : result.getTestContext().getFailedConfigurations()
            .getAllResults()) {
          Element stacktrace = createStackTraceElement(result1);
          stacktrace.setAttribute("configMethod",
              "Failed Config Method: " + result1.getInstanceName() + "." + result1.getName());
          currentTestMethod.addContent(stacktrace);
        }
        if (result.getThrowable() != null) {
          Element stacktrace = createStackTraceElement(result);
          currentTestMethod.addContent(stacktrace);
        }
      }
      // Adding number tag having id attribute to root element

      // Update overall result for the TestCase node
      if (intStatus == 0 || intStatus == 20) {
        if (!currentTestCase.getAttributeValue("TCStatus").equals("10")) {
          currentTestCase.setAttribute("TCStatus", "0");
        } else {
          currentTestCase.setAttribute("TCStatus", "10");
        }
      } else {
        if (!currentTestCase.getAttributeValue("TCStatus").equals("0")
            && !currentTestCase.getAttributeValue("TCStatus").equals("10")) {
          if (!currentTestCase.getAttributeValue("TCStatus").equals("11")) {
            currentTestCase.setAttribute("TCStatus", "1");
          } else {
            currentTestCase.setAttribute("TCStatus", "11");
          }
        }
      }

      XMLOutputter xmlOutput = new XMLOutputter(Format.getRawFormat());
      xmlOutput.setFormat(Format.getPrettyFormat().setEncoding("US-ASCII"));
      xmlOutput.output(doc, new FileWriter(strReportFilePath));
      return true;
    } catch (IndexOutOfBoundsException e) {
      // MainReporting.reportSpecialError( "Exception: '" +
      // e.getMessage()+ "'. Message: '" +
      // strDescription + "'");
      LoggerUtil.log(e.getMessage(), Level.DEBUG);
      return false;
    } catch (Exception e) {
      LoggerUtil.log(e.getMessage(), Level.DEBUG);
      return false;
    }
  }

  public static void addResultInfoNode(ITestResult result, String strDescription, int intStatus) {

    String browser = result.getMethod().getXmlTest().getParameter("Browser");
    String strTestMethod = "";
    if (browser == null || browser.equalsIgnoreCase("")) {
      strTestMethod = result.getMethod().getMethodName();
    } else {
      strTestMethod = result.getMethod().getMethodName() + browser;
    }

    // String strDuration = Long.toString((result.getEndMillis() -
    // result.getStartMillis()) / 1000);
    // String testName = result.getTestClass().getName() + "." +
    // result.getMethod().getMethodName();
    String strIteration = String.valueOf(result.getMethod().getCurrentInvocationCount());
    String sTestClassName = result.getTestClass().getRealClass().getCanonicalName();

    String sTestXMLName = result.getMethod().getXmlTest().getName();

    try {

      strTestRowNumber = "1";

      Document doc = getXMLDocument();
      List<Element> lstTestCase = doc.getRootElement().getChildren("TestCase");
      Element currentTestCase = (Element) lstTestCase.get(lstTestCase.size() - 1);
      List<Element> lstTestActivity = currentTestCase.getChildren("TestActivity");

      Element currentTestActivity = null;
      for (Element activity : lstTestActivity) {
        if (activity.getAttributeValue("Desc")
            .equalsIgnoreCase(sTestXMLName + " - " + sTestClassName)) {
          currentTestActivity = activity;
        }
      }

      List<Element> lstTestMethod = currentTestActivity.getChildren("TestMethod");
      // Element currentTestMethod = (Element)
      // lstTestMethod.get(lstTestMethod.size() - 1);
      Element currentTestMethod = getTestMethodNode(lstTestMethod, strTestMethod, strIteration);

      // Create a new MethodResult node
      Element childMethodResult = new Element("MethodResult");

      // Add the attribute to the child
      childMethodResult.setAttribute("Status", String.valueOf(intStatus));
      childMethodResult.setAttribute("Time", String.valueOf(new Date()));

      // - Giving ID for the first failed node
      if (blnReportRowIDFlag && (intStatus % 10 == 0)) {
        childMethodResult.setAttribute("ID", "fail_" + strTestRowNumber);
        blnReportRowIDFlag = false;
      }
      childMethodResult.setText(strDescription);

      // Adding number tag having id attribute to root element
      currentTestMethod.addContent(childMethodResult);

      XMLOutputter xmlOutput = new XMLOutputter(Format.getRawFormat());
      xmlOutput.setFormat(Format.getPrettyFormat().setEncoding("US-ASCII"));
      xmlOutput.output(doc, new FileWriter(strReportFilePath));
    } catch (Exception e) {
      throw new ReportCreationException("Unable to add Result info node", e);
    }
  }

  private static Element getTestMethodNode(List<Element> lstTestMethod, String strDescription,
      String strIteration) {
    for (Element method : lstTestMethod) {
      if (method.getAttributeValue("Desc").equalsIgnoreCase(strDescription)
          && method.getAttributeValue("Iter").equalsIgnoreCase(strIteration)) {
        return method;
      }
    }
    return null;
  }

  /**
   * This method adds all the required information to the result node if the test case gets passed
   * for the generated XML report
   * 
   * @param result
   */
  public static void reportPass(ITestResult result) {
    String browser = result.getMethod().getXmlTest().getParameter("Browser");
    String strTestMethod = "";
    if (browser == null || browser.equalsIgnoreCase("")) {
      strTestMethod = result.getMethod().getMethodName();
    } else {
      strTestMethod = result.getMethod().getMethodName() + browser;
    }

    String strDuration = Long.toString((result.getEndMillis() - result.getStartMillis()) / 1000);
    String testName = result.getTestClass().getName() + "." + result.getMethod().getMethodName();
    String strIteration = String.valueOf(result.getMethod().getCurrentInvocationCount());
    String sTestClassName = result.getTestClass().getRealClass().getCanonicalName();

    String strDescription = "Test Passed: " + testName;
    String sTestXMLName = result.getMethod().getXmlTest().getName();

    try {
      strTestRowNumber = "1";

      Document doc = getXMLDocument();

      List<Element> lstTestCase = doc.getRootElement().getChildren("TestCase");
      Element currentTestCase = (Element) lstTestCase.get(lstTestCase.size() - 1);
      List<Element> lstTestActivity = currentTestCase.getChildren("TestActivity");

      Element currentTestActivity = null;
      for (Element activity : lstTestActivity) {
        if (activity.getAttributeValue("Desc")
            .equalsIgnoreCase(sTestXMLName + " - " + sTestClassName)) {
          currentTestActivity = activity;
        }
      }

      List<Element> lstTestMethod = currentTestActivity.getChildren("TestMethod");
      // Element currentTestMethod = (Element)
      // lstTestMethod.get(lstTestMethod.size() - 1);
      Element currentTestMethod = getTestMethodNode(lstTestMethod, strTestMethod, strIteration);

      // Create a new MethodResult node
      Element childMethodResult = new Element("MethodResult");

      // Add the attribute to the child
      childMethodResult.setAttribute("Status", String.valueOf(PASSED));
      childMethodResult.setAttribute("Time", String.valueOf(new Date()));

      childMethodResult.setAttribute("duration", strDuration);
      childMethodResult.setText(strDescription);

      currentTestMethod.addContent(childMethodResult);

      // Update overall result for the TestCase node
      if (Integer.parseInt(currentTestCase.getAttributeValue("TCStatus")) == SKIPPED) {
        currentTestCase.setAttribute("TCStatus", String.valueOf(PASSED));
      }

      XMLOutputter xmlOutput = new XMLOutputter(Format.getRawFormat());
      xmlOutput.setFormat(Format.getPrettyFormat().setEncoding("US-ASCII"));
      xmlOutput.output(doc, new FileWriter(strReportFilePath));
    } catch (Exception e) {
      throw new ReportCreationException("Unable to update test result", e);
    }
  }

  /**
   * This method adds all the required information to the result node if the test case gets failed
   * for the generated XML report
   * 
   * @param result
   */
  public static void reportError(ITestResult result) {
    String browser = result.getMethod().getXmlTest().getParameter("Browser");
    String strTestMethod = "";
    if (browser == null || browser.equalsIgnoreCase("")) {
      strTestMethod = result.getMethod().getMethodName();
    } else {
      strTestMethod = result.getMethod().getMethodName() + browser;
    }

    String strDuration = Long.toString((result.getEndMillis() - result.getStartMillis()) / 1000);
    String testName = result.getTestClass().getName() + "." + result.getMethod().getMethodName();
    String strIteration = String.valueOf(result.getMethod().getCurrentInvocationCount());
    String sTestClassName = result.getTestClass().getRealClass().getCanonicalName();

    String strDescription =
        "Test Failed: " + testName + " ; Exception Occured:" + result.getThrowable().toString();
    String sTestXMLName = result.getMethod().getXmlTest().getName();

    try {
      strTestRowNumber = "1";

      Document doc = getXMLDocument();

      List<Element> lstTestCase = doc.getRootElement().getChildren("TestCase");
      Element currentTestCase = (Element) lstTestCase.get(lstTestCase.size() - 1);
      List<Element> lstTestActivity = currentTestCase.getChildren("TestActivity");

      Element currentTestActivity = null;
      for (Element activity : lstTestActivity) {
        if (activity.getAttributeValue("Desc")
            .equalsIgnoreCase(sTestXMLName + " - " + sTestClassName)) {
          currentTestActivity = activity;
        }
      }

      List<Element> lstTestMethod = currentTestActivity.getChildren("TestMethod");
      // Element currentTestMethod = (Element)
      // lstTestMethod.get(lstTestMethod.size() - 1);
      Element currentTestMethod = getTestMethodNode(lstTestMethod, strTestMethod, strIteration);

      // Create a new MethodResult node
      Element childMethodResult = new Element("MethodResult");

      // Add the attribute to the child
      childMethodResult.setAttribute("Status", String.valueOf(FAILED));
      childMethodResult.setAttribute("Time", String.valueOf(new Date()));

      // - Giving ID for the first failed node
      if (blnReportRowIDFlag) {
        childMethodResult.setAttribute("ID", "fail_" + strTestRowNumber);
        blnReportRowIDFlag = false;
      }
      childMethodResult.setAttribute("duration", strDuration);
      childMethodResult.setText(strDescription);
      currentTestMethod.addContent(childMethodResult);

      Element stacktrace = createStackTraceElement(result);
      currentTestMethod.addContent(stacktrace);

      // Update overall result for the TestCase node
      currentTestCase.setAttribute("TCStatus", String.valueOf(FAILED));

      XMLOutputter xmlOutput = new XMLOutputter(Format.getRawFormat());
      xmlOutput.setFormat(Format.getPrettyFormat().setEncoding("US-ASCII"));
      xmlOutput.output(doc, new FileWriter(strReportFilePath));
    } catch (Exception e) {
      throw new ReportCreationException("Unable to update test result", e);
    }
  }

  public static void reportWarning(ITestResult result) {
    String browser = result.getMethod().getXmlTest().getParameter("Browser");
    String strTestMethod = "";
    if (browser == null || browser.equalsIgnoreCase("")) {
      strTestMethod = result.getMethod().getMethodName();
    } else {
      strTestMethod = result.getMethod().getMethodName() + browser;
    }

    String strDuration = Long.toString((result.getEndMillis() - result.getStartMillis()) / 1000);
    String testName = result.getTestClass().getName() + "." + result.getMethod().getMethodName();
    String strIteration = String.valueOf(result.getMethod().getCurrentInvocationCount());
    String sTestClassName = result.getTestClass().getRealClass().getCanonicalName();

    String strDescription = "Test Skipped: " + testName + (result.getThrowable() != null
        ? " ; Exception Occured:" + result.getThrowable().toString() : "");
    String sTestXMLName = result.getMethod().getXmlTest().getName();

    try {
      strTestRowNumber = "1";

      Document doc = getXMLDocument();

      List<Element> lstTestCase = doc.getRootElement().getChildren("TestCase");
      Element currentTestCase = (Element) lstTestCase.get(lstTestCase.size() - 1);
      List<Element> lstTestActivity = currentTestCase.getChildren("TestActivity");

      Element currentTestActivity = null;
      for (Element activity : lstTestActivity) {
        if (activity.getAttributeValue("Desc")
            .equalsIgnoreCase(sTestXMLName + " - " + sTestClassName)) {
          currentTestActivity = activity;
        }
      }

      List<Element> lstTestMethod = currentTestActivity.getChildren("TestMethod");
      // Element currentTestMethod = (Element)
      // lstTestMethod.get(lstTestMethod.size() - 1);
      Element currentTestMethod = getTestMethodNode(lstTestMethod, strTestMethod, strIteration);

      // Create a new MethodResult node
      Element childMethodResult = new Element("MethodResult");

      // Add the attribute to the child
      childMethodResult.setAttribute("Status", String.valueOf(SKIPPED));
      childMethodResult.setAttribute("Time", String.valueOf(new Date()));
      childMethodResult.setAttribute("duration", strDuration);
      childMethodResult.setText(strDescription);

      currentTestMethod.addContent(childMethodResult);

      for (ITestResult result1 : result.getTestContext().getFailedConfigurations()
          .getAllResults()) {
        Element stacktrace = createStackTraceElement(result1);
        stacktrace.setAttribute("configMethod",
            "Failed Config Method: " + result1.getInstanceName() + "." + result1.getName());
        currentTestMethod.addContent(stacktrace);
      }
      if (result.getThrowable() != null) {
        Element stacktrace = createStackTraceElement(result);
        currentTestMethod.addContent(stacktrace);
      }
      XMLOutputter xmlOutput = new XMLOutputter(Format.getRawFormat());
      xmlOutput.setFormat(Format.getPrettyFormat().setEncoding("US-ASCII"));
      xmlOutput.output(doc, new FileWriter(strReportFilePath));
    } catch (Exception e) {
      throw new ReportCreationException("Unable to update test result", e);
    }
  }

  /**
   * This method is to transform the generated XML report into HTML by using the XSLT file
   */
  public static void generateHtmlReport() {
    String outputFileName = "";
    // Copy report resources
    try {
      for (String fileName : CSSRESOURCES) {
        String cssfolder = getStrReportFolderPath() + File.separator + "css";
        InputStream is = MainReporting.class.getResourceAsStream("/reporting/css/" + fileName);
        if (is == null) {
          throw new AssertionError("Couldn't find resource: " + fileName);
        }
        copyFile(is, new File(cssfolder, fileName));
      }
      for (String fileName : IMGRESOURCES) {
        String cssfolder = getStrReportFolderPath() + File.separator + "img";
        InputStream is = MainReporting.class.getResourceAsStream("/reporting/img/" + fileName);
        if (is == null) {
          throw new AssertionError("Couldn't find resource: " + fileName);
        }
        copyFile(is, new File(cssfolder, fileName));
      }
      for (String fileName : JSRESOURCES) {
        String cssfolder = getStrReportFolderPath() + File.separator + "js";
        InputStream is = MainReporting.class.getResourceAsStream("/reporting/js/" + fileName);
        if (is == null) {
          throw new AssertionError("Couldn't find resource: " + fileName);
        }
        copyFile(is, new File(cssfolder, fileName));
      }
    } catch (IOException e) {
      // e.printStackTrace();
      LoggerUtil.log(e.getMessage(), Level.DEBUG);
    }
    try {
      TransformerFactory tFactory = TransformerFactory.newInstance();
      InputStream is = MainReporting.class.getResourceAsStream("/reporting/Report.xsl");
      Source xslDoc = new StreamSource(is);
      Source xmlDoc = new StreamSource(strReportFilePath);
      outputFileName = strReportFilePath.replaceAll("xml", "html");
      OutputStream htmlFile = new FileOutputStream(outputFileName);
      Transformer trasform = tFactory.newTransformer(xslDoc);
      trasform.transform(xmlDoc, new StreamResult(htmlFile));
      htmlFile.close();
    } catch (Exception e) {
      // e.printStackTrace();
      LoggerUtil.log(e.getMessage(), Level.DEBUG);
      return;
    }
    try {
      if (HtmlReportListener.OPENREPORT) {
        File htmlFile = new File(outputFileName);
        URI uri = htmlFile.toURI();
        //_executeCmd("chrome", uri.toString());
        try{
         Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null; if (desktop
         != null && desktop.isSupported(Desktop.Action.BROWSE)) desktop.browse(uri);
        }
        catch(Exception e){
          _executeCmd("chrome", uri.toString());
        }
      }
    } catch (Exception e) {
      // e.printStackTrace();
      LoggerUtil.log(e.getMessage(), Level.DEBUG);
    }
  }

  /*
   * This method is for opening the report on the default browser up on execution gets completed.
   */
  public static void _executeCmd(String browserPath, String theUrl) {
    String cmdLine = null;
    String osName = System.getProperty("os.name");

    if (osName.startsWith("Windows")) {
      cmdLine = "start " + theUrl;
      // on NT, you need to start cmd.exe because start is not
      // an external command but internal, you need to start the
      // command interpreter
      // cmdLine = "cmd.exe /c " + cmdLine;
      cmdLine = "rundll32 SHELL32.DLL,ShellExec_RunDLL " + browserPath + " " + theUrl;
    } else if (osName.startsWith("Mac")) {
      cmdLine = "open " + theUrl;
    } else {
      // Linux
      cmdLine = "open " + browserPath + " " + theUrl;
    }
    try {
      Runtime.getRuntime().exec(cmdLine);
    } catch (Exception e) {
      // logger.info(e);
    }
  }

  /**
   * Convert a Throwable into a list containing all of its causes.
   * 
   * @param t The throwable for which the causes are to be returned.
   * @return A (possibly empty) list of {@link Throwable}s.
   */
  public static List<Throwable> getCauses(Throwable t) {
    List<Throwable> causes = new LinkedList<Throwable>();
    Throwable next = t;
    while (next.getCause() != null) {
      next = next.getCause();
      causes.add(next);
    }
    return causes;
  }

  /**
   * Replace any angle brackets, quotes, apostrophes or ampersands with the corresponding XML/HTML
   * entities to avoid problems displaying the String in an XML document. Assumes that the String
   * does not already contain any entities (otherwise the ampersands will be escaped again).
   * 
   * @param s The String to escape.
   * @return The escaped String.
   */
  public static String escapeString(String s) {
    if (s == null) {
      return null;
    }

    StringBuilder buffer = new StringBuilder();
    for (int i = 0; i < s.length(); i++) {
      buffer.append(escapeChar(s.charAt(i)));
    }
    return buffer.toString();
  }

  /**
   * Converts a char into a String that can be inserted into an XML document, replacing special
   * characters with XML entities as required.
   * 
   * @param character The character to convert.
   * @return An XML entity representing the character (or a String containing just the character if
   *         it does not need to be escaped).
   */
  private static String escapeChar(char character) {
    switch (character) {
      case '<':
        return "&lt;";
      case '>':
        return "&gt;";
      case '"':
        return "&quot;";
      case '\'':
        return "&apos;";
      case '&':
        return "&amp;";
      default:
        return String.valueOf(character);
    }
  }

  /**
   * Works like {@link #escapeString(String)} but also replaces line breaks with &lt;br /&gt; tags
   * and preserves significant whitespace.
   * 
   * @param s The String to escape.
   * @return The escaped String.
   */
  public static String escapeHTMLString(String s) {
    if (s == null) {
      return null;
    }

    StringBuilder buffer = new StringBuilder();
    for (int i = 0; i < s.length(); i++) {
      char ch = s.charAt(i);
      switch (ch) {
        case ' ':
          // All spaces in a block of consecutive spaces are converted to
          // non-breaking space (&nbsp;) except for the last one. This
          // allows
          // significant whitespace to be retained without prohibiting
          // wrapping.
          char nextCh = i + 1 < s.length() ? s.charAt(i + 1) : 0;
          buffer.append(nextCh == ' ' ? "&nbsp;" : " ");
          break;
        case '\n':
          buffer.append("<br/>\n");
          break;
        default:
          buffer.append(escapeChar(ch));
      }
    }
    return buffer.toString();
  }

  private static Element createStackTraceElement(ITestResult result) {
    Element stacktrace = new Element("stacktrace");
    StringBuffer htmlsec = new StringBuffer("");
    htmlsec.append("<span class=\"exception\">");
    htmlsec.append(escapeHTMLString(result.getThrowable().toString()));
    htmlsec.append("</span><br/>");
    for (StackTraceElement stack : result.getThrowable().getStackTrace()) {
      htmlsec.append(escapeHTMLString(stack.toString()));
      htmlsec.append("<br/>");
    }
    for (Throwable t : getCauses(result.getThrowable())) {
      htmlsec.append("<span class=\"exception\">Caused By:" + escapeHTMLString(t.toString()));
      htmlsec.append("</span><br/>");
      for (StackTraceElement stack : t.getStackTrace()) {
        htmlsec.append(escapeHTMLString(stack.toString()));
        htmlsec.append("<br/>");
      }
    }
    CDATA cdata = new CDATA(htmlsec.toString());
    stacktrace.addContent(cdata);
    return stacktrace;
  }

  public static void copyFile(InputStream from, File to) throws IOException {
    if (!to.getParentFile().exists()) {
      to.getParentFile().mkdirs();
    }
    OutputStream os = null;
    try {
      os = new FileOutputStream(to);
      byte[] buffer = new byte[65536];
      int count = from.read(buffer);
      while (count > 0) {
        os.write(buffer, 0, count);
        count = from.read(buffer);
      }
    } finally {
      if (os != null)
        os.close();
    }
  }
}
