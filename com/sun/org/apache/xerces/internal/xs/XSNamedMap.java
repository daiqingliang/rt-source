package com.sun.org.apache.xerces.internal.xs;

import java.util.Map;

public interface XSNamedMap extends Map {
  int getLength();
  
  XSObject item(int paramInt);
  
  XSObject itemByName(String paramString1, String paramString2);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xs\XSNamedMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */