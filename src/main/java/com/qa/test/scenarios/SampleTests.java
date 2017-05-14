package com.qa.test.scenarios;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * This class is for testing the report
 * 
 * @author Nainappa Illi
 *
 */
public class SampleTests {

  /**
   * Pass Test
   * @throws Exception
   */
  @Test
  public void happyPathReceiverSingleMessageValidation() throws Exception {
    Assert.assertTrue(true);
  }

  /**
   * Fail Test
   * 
   * @throws Exception
   */
  @Test
  public void happyPathReceiverMultipleMessagesValidation() throws Exception {
    throw new RuntimeException("This throws the exception");
  }

  /**
   * Ignore Test
   * 
   * @throws Exception
   */
  @Test(enabled=false)
  public void moreThan10MessagesValidation() throws Exception {
    
  }

}

