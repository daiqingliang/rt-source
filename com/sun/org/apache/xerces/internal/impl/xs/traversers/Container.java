package com.sun.org.apache.xerces.internal.impl.xs.traversers;

abstract class Container {
  static final int THRESHOLD = 5;
  
  OneAttr[] values;
  
  int pos = 0;
  
  static Container getContainer(int paramInt) { return (paramInt > 5) ? new LargeContainer(paramInt) : new SmallContainer(paramInt); }
  
  abstract void put(String paramString, OneAttr paramOneAttr);
  
  abstract OneAttr get(String paramString);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\traversers\Container.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */