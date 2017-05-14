package com.report.utils;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.Reporter;

import com.report.exception.ItJumpStartReportException;

/**
 * This class provides all the required methods for creating folder structure if we are archiving
 * the reports
 * 
 * @author Nainappa Illi
 *
 */

public class Utils {

  public static final int DEFAULT_FILE_SIZE_KB = 1;
  public static final String IMG_FORMAT_PNG = ".png";
  public static final String IMG_FORMAT_JPG = ".jpg";
  public static final String RUN_MODE = "maven";

  public static final String DT_FORMAT_MMDDYYYY = "MM/dd/yyyy";
  public static final String DT_FORMAT_DDMMYYYY = "dd/MM/yyyy";

  public static void createFolder(String strDirectoy) {
    boolean success = new File(strDirectoy).mkdirs();
    if (!success && !new File(strDirectoy).exists()) {
      throw new ItJumpStartReportException("Failed to create directory" + strDirectoy);
    }
  }

  public static String getCurrentDateTime(String strFormat) {
    Date currentDate = new Date();
    SimpleDateFormat newFormat = new SimpleDateFormat(strFormat);
    return newFormat.format(currentDate);
  }

  /**
   * utility method for capturing screenshot
   * 
   * @param ImageFileName
   */
  public static void takeScreenShot(String ImageFileName) {
    Robot robot = null;
    try {
      robot = new Robot();
    } catch (AWTException e1) {
      e1.printStackTrace();
    }
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Dimension screenSize = toolkit.getScreenSize();
    Rectangle screenRect = new Rectangle(screenSize);
    // create screen shot
    BufferedImage image = robot.createScreenCapture(screenRect);
    // save captured image to PNG file
    try {
      int count = 0;
      String currentImageFilePath = null;
      File file;
      do {
        currentImageFilePath = ImageFileName
            // + ((count == 0) ? "" : count) + ".png";
            + ((count == 0) ? "" : count) + IMG_FORMAT_PNG;
        file = new File(currentImageFilePath);
        count++;
      } while (file.exists());
      ImageIO.write(image, "png", new File(currentImageFilePath));

      Reporter.log("Final Screenshot:<br><a href='file:///" + currentImageFilePath
          + "' target='new'> <img src='file:///" + currentImageFilePath
          + "' width='300px' height='200px' /></a><br> ");

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Description : This Method will highlight the elements in red in the web page
   * 
   * @param driver
   * @param element
   * @throws Exception
   */
  /*
   * public void highlightElement(WebDriver driver, WebElement element) throws Exception { for (int
   * i = 0; i < 2; i++) { JavascriptExecutor js = (JavascriptExecutor) driver; js.executeScript(
   * "arguments[0].setAttribute('style', arguments[1]);", element,
   * "color: red; border: 3px solid red;"); Thread.sleep(100); js.executeScript(
   * "arguments[0].setAttribute('style', arguments[1]);", element, ""); } }
   */

  // Returns a random File name without any extension, 1-25 characters
  /**
   * @return
   */
  public static String randomFileName() {
    return RandomStringUtils.randomAlphanumeric(1 + new Random().nextInt(25));
  }

  // Make a copy of an existing file.
  /**
   * @param srcFilePath
   * @param destFilePath
   */
  public static void copyFile(String srcFilePath, String destFilePath) {
    File srcFile = new File(srcFilePath);
    File destFile = new File(destFilePath);
    try {
      FileUtils.copyFile(srcFile, destFile);
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  /**
   * Captures screenshot of the screen under the folder path stored in screenshotBaseFolder variable
   * 
   * @param fileName
   */
  public static void captureScreen(String folderPath, String fileName) {

    if (!new File(folderPath).isDirectory())
      try {
        FileUtils.forceMkdir(new File(folderPath));
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    // String filePath = folderPath + File.separator + fileName + ".jpg";
    String filePath = folderPath + File.separator + fileName + IMG_FORMAT_JPG;

    try {
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      Rectangle screenRectangle = new Rectangle(screenSize);
      Robot robot = new Robot();
      BufferedImage image = robot.createScreenCapture(screenRectangle);
      ImageIO.write(image, "jpg", new File(filePath));
    } catch (Exception e) {
      System.out.println(
          "Exception thrown while capturing screenshot '" + filePath + "' : " + e.toString());
    }
  }

  /**
   * @param folderPath
   */
  public static void deleteDirectory(String folderPath) {
    try {
      FileUtils.deleteDirectory(new File(folderPath));
    } catch (IOException e) {
      System.out.println("Exeception deleting folder : " + folderPath);
      e.printStackTrace();
    }
  }

  /**
   * @param format
   * @return
   */
  public static String getCurrentDate(String format) {
    DateFormat dateFormat;
    if (format.equals(DT_FORMAT_DDMMYYYY))
      dateFormat = new SimpleDateFormat(DT_FORMAT_DDMMYYYY);
    else if (format.equals(DT_FORMAT_MMDDYYYY))
      dateFormat = new SimpleDateFormat(DT_FORMAT_MMDDYYYY);
    else
      dateFormat = new SimpleDateFormat(DT_FORMAT_MMDDYYYY); // default
                                                             // format
    Date date = new Date();
    return dateFormat.format(date);
  }

  /**
   * Method to find the relative path from 2 absolute paths
   * 
   * @param absPath
   * @param basePath
   * @return relative path to the file/folder. If an exception occurs absPath will be returned
   */
  public static String getRelativePath(String absPath, String basePath) {
    try {
      Path pathAbsolute = Paths.get(absPath);
      Path pathBase = Paths.get(basePath);
      Path pathRelative = pathBase.relativize(pathAbsolute);
      return pathRelative.toString();
    } catch (Exception e) {
      return absPath;
    }
  }

  public synchronized String getVersion() {
    String version = null;

    // try to load from maven properties first
    try {
      Properties p = new Properties();
      InputStream is = getClass().getResourceAsStream(
          "/META-INF/maven/pom.properties");
      if (is != null) {
        p.load(is);
        version = p.getProperty("version", "");
      }
    } catch (Exception e) {
      // ignore
    }

    // fallback to using Java API
    if (version == null) {
      Package aPackage = getClass().getPackage();
      if (aPackage != null) {
        version = aPackage.getImplementationVersion();
        if (version == null) {
          version = aPackage.getSpecificationVersion();
        }
      }
    }

    if (version == null) {
      // we could not compute the version so use a blank
      version = "";
    }

    return version;
  }
}
