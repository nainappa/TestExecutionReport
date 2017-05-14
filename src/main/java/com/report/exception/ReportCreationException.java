package com.report.exception;

import java.io.Serializable;

public class ReportCreationException extends RuntimeException implements Serializable {

  /**
   * This is a customized exception which overrides the runtime exception, to differentiate
   * reporting exception
   * 
   * @author Nainappa Illi
   */

  private static final long serialVersionUID = -1546026899179040910L;

  public ReportCreationException() {
    super();
  }

  public ReportCreationException(String msg) {
    super(msg);
  }

  /**
   * For wrapping up exception
   * 
   * @param message
   * @param cause
   */
  public ReportCreationException(String message, Throwable cause) {
    super(message, cause);
  }

}
