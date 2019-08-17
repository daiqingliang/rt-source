package com.sun.org.apache.xml.internal.resolver;

public class CatalogException extends Exception {
  public static final int WRAPPER = 1;
  
  public static final int INVALID_ENTRY = 2;
  
  public static final int INVALID_ENTRY_TYPE = 3;
  
  public static final int NO_XML_PARSER = 4;
  
  public static final int UNKNOWN_FORMAT = 5;
  
  public static final int UNPARSEABLE = 6;
  
  public static final int PARSE_FAILED = 7;
  
  public static final int UNENDED_COMMENT = 8;
  
  private Exception exception = null;
  
  private int exceptionType = 0;
  
  public CatalogException(int paramInt, String paramString) {
    super(paramString);
    this.exceptionType = paramInt;
    this.exception = null;
  }
  
  public CatalogException(int paramInt) {
    super("Catalog Exception " + paramInt);
    this.exceptionType = paramInt;
    this.exception = null;
  }
  
  public CatalogException(Exception paramException) {
    this.exceptionType = 1;
    this.exception = paramException;
  }
  
  public CatalogException(String paramString, Exception paramException) {
    super(paramString);
    this.exceptionType = 1;
    this.exception = paramException;
  }
  
  public String getMessage() {
    String str = super.getMessage();
    return (str == null && this.exception != null) ? this.exception.getMessage() : str;
  }
  
  public Exception getException() { return this.exception; }
  
  public int getExceptionType() { return this.exceptionType; }
  
  public String toString() { return (this.exception != null) ? this.exception.toString() : super.toString(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\resolver\CatalogException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */