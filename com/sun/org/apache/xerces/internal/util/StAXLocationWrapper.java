package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import javax.xml.stream.Location;

public final class StAXLocationWrapper implements XMLLocator {
  private Location fLocation = null;
  
  public void setLocation(Location paramLocation) { this.fLocation = paramLocation; }
  
  public Location getLocation() { return this.fLocation; }
  
  public String getPublicId() { return (this.fLocation != null) ? this.fLocation.getPublicId() : null; }
  
  public String getLiteralSystemId() { return (this.fLocation != null) ? this.fLocation.getSystemId() : null; }
  
  public String getBaseSystemId() { return null; }
  
  public String getExpandedSystemId() { return getLiteralSystemId(); }
  
  public int getLineNumber() { return (this.fLocation != null) ? this.fLocation.getLineNumber() : -1; }
  
  public int getColumnNumber() { return (this.fLocation != null) ? this.fLocation.getColumnNumber() : -1; }
  
  public int getCharacterOffset() { return (this.fLocation != null) ? this.fLocation.getCharacterOffset() : -1; }
  
  public String getEncoding() { return null; }
  
  public String getXMLVersion() { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\interna\\util\StAXLocationWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */