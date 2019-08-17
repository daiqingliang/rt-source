package com.sun.org.apache.xml.internal.security.c14n.implementations;

import java.util.ArrayList;
import java.util.List;

class SymbMap implements Cloneable {
  int free = 23;
  
  NameSpaceSymbEntry[] entries = new NameSpaceSymbEntry[this.free];
  
  String[] keys = new String[this.free];
  
  void put(String paramString, NameSpaceSymbEntry paramNameSpaceSymbEntry) {
    int i = index(paramString);
    String str = this.keys[i];
    this.keys[i] = paramString;
    this.entries[i] = paramNameSpaceSymbEntry;
    if ((str == null || !str.equals(paramString)) && --this.free == 0) {
      this.free = this.entries.length;
      int j = this.free << 2;
      rehash(j);
    } 
  }
  
  List<NameSpaceSymbEntry> entrySet() {
    ArrayList arrayList = new ArrayList();
    for (byte b = 0; b < this.entries.length; b++) {
      if (this.entries[b] != null && !"".equals((this.entries[b]).uri))
        arrayList.add(this.entries[b]); 
    } 
    return arrayList;
  }
  
  protected int index(Object paramObject) {
    String[] arrayOfString = this.keys;
    int i = arrayOfString.length;
    int j = (paramObject.hashCode() & 0x7FFFFFFF) % i;
    String str = arrayOfString[j];
    if (str == null || str.equals(paramObject))
      return j; 
    i--;
    do {
      j = (j == i) ? 0 : ++j;
      str = arrayOfString[j];
    } while (str != null && !str.equals(paramObject));
    return j;
  }
  
  protected void rehash(int paramInt) {
    int i = this.keys.length;
    String[] arrayOfString = this.keys;
    NameSpaceSymbEntry[] arrayOfNameSpaceSymbEntry = this.entries;
    this.keys = new String[paramInt];
    this.entries = new NameSpaceSymbEntry[paramInt];
    int j = i;
    while (j-- > 0) {
      if (arrayOfString[j] != null) {
        String str = arrayOfString[j];
        int k = index(str);
        this.keys[k] = str;
        this.entries[k] = arrayOfNameSpaceSymbEntry[j];
      } 
    } 
  }
  
  NameSpaceSymbEntry get(String paramString) { return this.entries[index(paramString)]; }
  
  protected Object clone() {
    try {
      SymbMap symbMap = (SymbMap)super.clone();
      symbMap.entries = new NameSpaceSymbEntry[this.entries.length];
      System.arraycopy(this.entries, 0, symbMap.entries, 0, this.entries.length);
      symbMap.keys = new String[this.keys.length];
      System.arraycopy(this.keys, 0, symbMap.keys, 0, this.keys.length);
      return symbMap;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      cloneNotSupportedException.printStackTrace();
      return null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\c14n\implementations\SymbMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */