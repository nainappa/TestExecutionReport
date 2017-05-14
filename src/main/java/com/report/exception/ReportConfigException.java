package com.report.exception;

import java.io.Serializable;

public class ReportConfigException extends RuntimeException implements Serializable {

  /**
   * This is a customized exception which overrides the runtime exception, to differentiate
   * reporting exception
   * 
   * @author Nainappa Illi
   */

  private static final long serialVersionUID = -3644742957748395150L;

  public ReportConfigException() {
    super();
  }

  public ReportConfigException(String msg) {
    super(msg);
  }

  /**
   * For wrapping up exception
   * 
   * @param message
   * @param cause
   */
  public ReportConfigException(String message, Throwable cause) {
    super(message, cause);
  }

}
