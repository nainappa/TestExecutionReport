package com.report.exception;

import java.io.Serializable;

public class MissingAnnotationException extends RuntimeException implements Serializable {

  /**
   * This is a customized exception which overrides the runtime exception, to differentiate
   * reporting exception
   * 
   * @author Nainappa Illi
   */

  private static final long serialVersionUID = 5904071269567294830L;

  public MissingAnnotationException() {
    super();
  }

  public MissingAnnotationException(String msg) {
    super(msg);
  }

  /**
   * For wrapping up exception
   * 
   * @param message
   * @param cause
   */
  public MissingAnnotationException(String message, Throwable cause) {
    super(message, cause);
  }

}
