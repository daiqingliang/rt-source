package com.sun.org.apache.xerces.internal.xs;

public class XSException extends RuntimeException {
  static final long serialVersionUID = 3111893084677917742L;
  
  public short code;
  
  public static final short NOT_SUPPORTED_ERR = 1;
  
  public static final short INDEX_SIZE_ERR = 2;
  
  public XSException(short paramShort, String paramString) {
    super(paramString);
    this.code = paramShort;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xs\XSException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */