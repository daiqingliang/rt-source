package com.sun.xml.internal.ws.util.xml;

import javax.xml.stream.Location;

public final class DummyLocation implements Location {
  public static final Location INSTANCE = new DummyLocation();
  
  public int getCharacterOffset() { return -1; }
  
  public int getColumnNumber() { return -1; }
  
  public int getLineNumber() { return -1; }
  
  public String getPublicId() { return null; }
  
  public String getSystemId() { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\xml\DummyLocation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */