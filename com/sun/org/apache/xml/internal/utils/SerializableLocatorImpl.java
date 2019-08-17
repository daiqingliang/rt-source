package com.sun.org.apache.xml.internal.utils;

import java.io.Serializable;
import org.xml.sax.Locator;

public class SerializableLocatorImpl implements Locator, Serializable {
  static final long serialVersionUID = -2660312888446371460L;
  
  private String publicId;
  
  private String systemId;
  
  private int lineNumber;
  
  private int columnNumber;
  
  public SerializableLocatorImpl() {}
  
  public SerializableLocatorImpl(Locator paramLocator) {
    setPublicId(paramLocator.getPublicId());
    setSystemId(paramLocator.getSystemId());
    setLineNumber(paramLocator.getLineNumber());
    setColumnNumber(paramLocator.getColumnNumber());
  }
  
  public String getPublicId() { return this.publicId; }
  
  public String getSystemId() { return this.systemId; }
  
  public int getLineNumber() { return this.lineNumber; }
  
  public int getColumnNumber() { return this.columnNumber; }
  
  public void setPublicId(String paramString) { this.publicId = paramString; }
  
  public void setSystemId(String paramString) { this.systemId = paramString; }
  
  public void setLineNumber(int paramInt) { this.lineNumber = paramInt; }
  
  public void setColumnNumber(int paramInt) { this.columnNumber = paramInt; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\SerializableLocatorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */