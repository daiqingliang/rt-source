package com.sun.org.apache.xerces.internal.xni;

public class XNIException extends RuntimeException {
  static final long serialVersionUID = 9019819772686063775L;
  
  private Exception fException;
  
  public XNIException(String paramString) { super(paramString); }
  
  public XNIException(Exception paramException) {
    super(paramException.getMessage());
    this.fException = paramException;
  }
  
  public XNIException(String paramString, Exception paramException) {
    super(paramString);
    this.fException = paramException;
  }
  
  public Exception getException() { return this.fException; }
  
  public Throwable getCause() { return this.fException; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xni\XNIException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */