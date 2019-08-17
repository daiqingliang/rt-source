package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import java.util.HashMap;
import java.util.Map;

class LargeContainer extends Container {
  Map items;
  
  LargeContainer(int paramInt) {
    this.items = new HashMap(paramInt * 2 + 1);
    this.values = new OneAttr[paramInt];
  }
  
  void put(String paramString, OneAttr paramOneAttr) {
    this.items.put(paramString, paramOneAttr);
    this.values[this.pos++] = paramOneAttr;
  }
  
  OneAttr get(String paramString) { return (OneAttr)this.items.get(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\traversers\LargeContainer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */