package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import org.xml.sax.Locator;
import org.xml.sax.ext.Locator2;

public final class SAXLocatorWrapper implements XMLLocator {
  private Locator fLocator = null;
  
  private Locator2 fLocator2 = null;
  
  public void setLocator(Locator paramLocator) {
    this.fLocator = paramLocator;
    if (paramLocator instanceof Locator2 || paramLocator == null)
      this.fLocator2 = (Locator2)paramLocator; 
  }
  
  public Locator getLocator() { return this.fLocator; }
  
  public String getPublicId() { return (this.fLocator != null) ? this.fLocator.getPublicId() : null; }
  
  public String getLiteralSystemId() { return (this.fLocator != null) ? this.fLocator.getSystemId() : null; }
  
  public String getBaseSystemId() { return null; }
  
  public String getExpandedSystemId() { return getLiteralSystemId(); }
  
  public int getLineNumber() { return (this.fLocator != null) ? this.fLocator.getLineNumber() : -1; }
  
  public int getColumnNumber() { return (this.fLocator != null) ? this.fLocator.getColumnNumber() : -1; }
  
  public int getCharacterOffset() { return -1; }
  
  public String getEncoding() { return (this.fLocator2 != null) ? this.fLocator2.getEncoding() : null; }
  
  public String getXMLVersion() { return (this.fLocator2 != null) ? this.fLocator2.getXMLVersion() : null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\interna\\util\SAXLocatorWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */