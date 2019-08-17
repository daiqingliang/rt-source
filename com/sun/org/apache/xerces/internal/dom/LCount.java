package com.sun.org.apache.xerces.internal.dom;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class LCount {
  static final Map<String, LCount> lCounts = new ConcurrentHashMap();
  
  public int captures = 0;
  
  public int bubbles = 0;
  
  public int defaults;
  
  public int total = 0;
  
  static LCount lookup(String paramString) {
    LCount lCount = (LCount)lCounts.get(paramString);
    if (lCount == null)
      lCounts.put(paramString, lCount = new LCount()); 
    return lCount;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\LCount.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */