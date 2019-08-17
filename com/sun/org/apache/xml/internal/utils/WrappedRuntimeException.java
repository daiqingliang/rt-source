package com.sun.org.apache.xml.internal.utils;

public class WrappedRuntimeException extends RuntimeException {
  static final long serialVersionUID = 7140414456714658073L;
  
  private Exception m_exception;
  
  public WrappedRuntimeException(Exception paramException) {
    super(paramException.getMessage());
    this.m_exception = paramException;
  }
  
  public WrappedRuntimeException(String paramString, Exception paramException) {
    super(paramString);
    this.m_exception = paramException;
  }
  
  public Exception getException() { return this.m_exception; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\WrappedRuntimeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */