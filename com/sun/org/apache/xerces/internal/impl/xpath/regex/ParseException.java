package com.sun.org.apache.xerces.internal.impl.xpath.regex;

public class ParseException extends RuntimeException {
  static final long serialVersionUID = -7012400318097691370L;
  
  int location;
  
  public ParseException(String paramString, int paramInt) {
    super(paramString);
    this.location = paramInt;
  }
  
  public int getLocation() { return this.location; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xpath\regex\ParseException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */