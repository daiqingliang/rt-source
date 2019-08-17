package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import org.xml.sax.Locator;

public class LocatorWrapper implements XMLLocator {
  private final Locator locator;
  
  public LocatorWrapper(Locator paramLocator) { this.locator = paramLocator; }
  
  public int getColumnNumber() { return this.locator.getColumnNumber(); }
  
  public int getLineNumber() { return this.locator.getLineNumber(); }
  
  public String getBaseSystemId() { return null; }
  
  public String getExpandedSystemId() { return this.locator.getSystemId(); }
  
  public String getLiteralSystemId() { return this.locator.getSystemId(); }
  
  public String getPublicId() { return this.locator.getPublicId(); }
  
  public String getEncoding() { return null; }
  
  public int getCharacterOffset() { return -1; }
  
  public String getXMLVersion() { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\interna\\util\LocatorWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */