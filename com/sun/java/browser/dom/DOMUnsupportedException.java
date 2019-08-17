package com.sun.java.browser.dom;

public class DOMUnsupportedException extends Exception {
  private Throwable ex;
  
  private String msg;
  
  public DOMUnsupportedException() { this(null, null); }
  
  public DOMUnsupportedException(String paramString) { this(null, paramString); }
  
  public DOMUnsupportedException(Exception paramException) { this(paramException, null); }
  
  public DOMUnsupportedException(Exception paramException, String paramString) {
    this.ex = paramException;
    this.msg = paramString;
  }
  
  public String getMessage() { return this.msg; }
  
  public Throwable getCause() { return this.ex; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\browser\dom\DOMUnsupportedException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */