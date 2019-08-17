package com.sun.org.apache.xerces.internal.impl.xpath;

public class XPathException extends Exception {
  static final long serialVersionUID = -948482312169512085L;
  
  private String fKey = "c-general-xpath";
  
  public XPathException() {}
  
  public XPathException(String paramString) {}
  
  public String getKey() { return this.fKey; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xpath\XPathException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */