package com.report.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.Reporter;

import com.report.exception.ItJumpStartReportException;

/**
 * Utility class for log4j
 * 
 * @author Nainappa Illi
 *
 */
public class LoggerUtil {

  static Logger logger = Logger.getLogger(LoggerUtil.class);

  /**
   * Logs the given message at info level
   * 
   * @param message
   */
  public static void log(String message) {
    logger.info(message);
    Reporter.log(message + "<br/>", false);
  }

  /**
   * Logs the given message at the given log level
   * 
   * @param message
   * @param lv (Log Level)
   */
  public static void log(String message, Level lv) {
    if (lv == Level.INFO) {
      logger.info(message);
      Reporter.log(message + "<br/>", false);
    }
    if (lv == Level.DEBUG) {
      logger.debug(message);
      // Reporter.log(message + "<br/>", false);
    }
    if (lv == Level.ERROR) {
      logger.error(message);
      // Reporter.log(message + "<br/>", false);
    }
    if (lv == Level.WARN) {
      logger.warn(message);
    }
  }

  /**
   * Logs the given message at the given log level in string format
   * 
   * @param message
   * @param lv (String Log Level)
   */
  public static void log(String message, String lv) {
    if (lv.equalsIgnoreCase("debug")) {
      log(message, Level.DEBUG);
    } else if (lv.equalsIgnoreCase("error")) {
      log(message, Level.ERROR);
    } else {
      log(message, Level.INFO); // default is info
    }
  }

  public static void updateLog4jConfiguration(String logFile) throws IOException {
    Properties props = new Properties();
    InputStream configStream = null;
    try {
      configStream = LoggerUtil.class.getResourceAsStream("/log4j.properties");
      props.load(configStream);
    } catch (IOException e) {
      System.out.println("Error log4j configuration file not loaded");
    } finally {
      if (configStream != null) {
        try {
          configStream.close();
        } catch (IOException e) {
          throw new ItJumpStartReportException("Unable to close Property File " + logFile, e);
        }
      }
    }

    props.setProperty("log4j.appender.FileAppender.File", logFile);
    // System.out.println(
    // props.getProperty("log4j.appender.FileAppender.File"));

    LogManager.resetConfiguration();
    PropertyConfigurator.configure(props);
    // System.out.println(
    // props.getProperty("log4j.appender.FileAppender.File"));
  }
}
