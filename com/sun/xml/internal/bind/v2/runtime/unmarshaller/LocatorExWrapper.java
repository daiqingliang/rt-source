package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.helpers.ValidationEventLocatorImpl;
import org.xml.sax.Locator;

class LocatorExWrapper implements LocatorEx {
  private final Locator locator;
  
  public LocatorExWrapper(Locator paramLocator) { this.locator = paramLocator; }
  
  public ValidationEventLocator getLocation() { return new ValidationEventLocatorImpl(this.locator); }
  
  public String getPublicId() { return this.locator.getPublicId(); }
  
  public String getSystemId() { return this.locator.getSystemId(); }
  
  public int getLineNumber() { return this.locator.getLineNumber(); }
  
  public int getColumnNumber() { return this.locator.getColumnNumber(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtim\\unmarshaller\LocatorExWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */