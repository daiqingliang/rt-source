package com.sun.java.browser.dom;

public class DOMAccessException extends Exception {
  private Throwable ex;
  
  private String msg;
  
  public DOMAccessException() { this(null, null); }
  
  public DOMAccessException(String paramString) { this(null, paramString); }
  
  public DOMAccessException(Exception paramException) { this(paramException, null); }
  
  public DOMAccessException(Exception paramException, String paramString) {
    this.ex = paramException;
    this.msg = paramString;
  }
  
  public String getMessage() { return this.msg; }
  
  public Throwable getCause() { return this.ex; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\browser\dom\DOMAccessException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */