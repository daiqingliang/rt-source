package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import org.xml.sax.ext.Locator2;

public class LocatorProxy implements Locator2 {
  private final XMLLocator fLocator;
  
  public LocatorProxy(XMLLocator paramXMLLocator) { this.fLocator = paramXMLLocator; }
  
  public String getPublicId() { return this.fLocator.getPublicId(); }
  
  public String getSystemId() { return this.fLocator.getExpandedSystemId(); }
  
  public int getLineNumber() { return this.fLocator.getLineNumber(); }
  
  public int getColumnNumber() { return this.fLocator.getColumnNumber(); }
  
  public String getXMLVersion() { return this.fLocator.getXMLVersion(); }
  
  public String getEncoding() { return this.fLocator.getEncoding(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\interna\\util\LocatorProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */