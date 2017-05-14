package com.report.exception;

import java.io.Serializable;

public class ItJumpStartReportException extends RuntimeException implements Serializable {

  /**
   * This is a customized exception which overrides the runtime exception, to differentiate
   * reporting exception
   * 
   * @author Nainappa Illi
   */

  private static final long serialVersionUID = 6451047615995283896L;

  public ItJumpStartReportException() {
    super();
  }

  public ItJumpStartReportException(String msg) {
    super(msg);
  }

  /**
   * For wrapping up exception
   * 
   * @param message
   * @param cause
   */
  public ItJumpStartReportException(String message, Throwable cause) {
    super(message, cause);
  }

}
