package com.sun.org.apache.xerces.internal.impl.xs.traversers;

class SmallContainer extends Container {
  String[] keys;
  
  SmallContainer(int paramInt) {
    this.keys = new String[paramInt];
    this.values = new OneAttr[paramInt];
  }
  
  void put(String paramString, OneAttr paramOneAttr) {
    this.keys[this.pos] = paramString;
    this.values[this.pos++] = paramOneAttr;
  }
  
  OneAttr get(String paramString) {
    for (byte b = 0; b < this.pos; b++) {
      if (this.keys[b].equals(paramString))
        return this.values[b]; 
    } 
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\traversers\SmallContainer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */