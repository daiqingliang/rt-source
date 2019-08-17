package com.sun.org.apache.xalan.internal.utils;

public final class ConfigurationError extends Error {
  private Exception exception;
  
  ConfigurationError(String paramString, Exception paramException) {
    super(paramString);
    this.exception = paramException;
  }
  
  public Exception getException() { return this.exception; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\interna\\utils\ConfigurationError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */