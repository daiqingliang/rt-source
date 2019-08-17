package jdk.internal.util.xml.impl;

import jdk.internal.org.xml.sax.Attributes;

public class Attrs implements Attributes {
  String[] mItems = new String[64];
  
  private char mLength;
  
  private char mAttrIdx = Character.MIN_VALUE;
  
  public void setLength(char paramChar) {
    if (paramChar > (char)(this.mItems.length >> 3))
      this.mItems = new String[paramChar << '\003']; 
    this.mLength = paramChar;
  }
  
  public int getLength() { return this.mLength; }
  
  public String getURI(int paramInt) { return (paramInt >= 0 && paramInt < this.mLength) ? this.mItems[paramInt << 3] : null; }
  
  public String getLocalName(int paramInt) { return (paramInt >= 0 && paramInt < this.mLength) ? this.mItems[(paramInt << 3) + 2] : null; }
  
  public String getQName(int paramInt) { return (paramInt < 0 || paramInt >= this.mLength) ? null : this.mItems[(paramInt << 3) + 1]; }
  
  public String getType(int paramInt) { return (paramInt >= 0 && paramInt < this.mItems.length >> 3) ? this.mItems[(paramInt << 3) + 4] : null; }
  
  public String getValue(int paramInt) { return (paramInt >= 0 && paramInt < this.mLength) ? this.mItems[(paramInt << 3) + 3] : null; }
  
  public int getIndex(String paramString1, String paramString2) {
    char c = this.mLength;
    char c1;
    for (c1 = Character.MIN_VALUE; c1 < c; c1 = (char)(c1 + 1)) {
      if (this.mItems[c1 << 3].equals(paramString1) && this.mItems[(c1 << 3) + 2].equals(paramString2))
        return c1; 
    } 
    return -1;
  }
  
  int getIndexNullNS(String paramString1, String paramString2) {
    char c = this.mLength;
    if (paramString1 != null) {
      char c1;
      for (c1 = Character.MIN_VALUE; c1 < c; c1 = (char)(c1 + 1)) {
        if (this.mItems[c1 << 3].equals(paramString1) && this.mItems[(c1 << 3) + 2].equals(paramString2))
          return c1; 
      } 
    } else {
      char c1;
      for (c1 = Character.MIN_VALUE; c1 < c; c1 = (char)(c1 + 1)) {
        if (this.mItems[(c1 << 3) + 2].equals(paramString2))
          return c1; 
      } 
    } 
    return -1;
  }
  
  public int getIndex(String paramString) {
    char c = this.mLength;
    for (char c1 = Character.MIN_VALUE; c1 < c; c1 = (char)(c1 + 1)) {
      if (this.mItems[(c1 << 3) + 1].equals(paramString))
        return c1; 
    } 
    return -1;
  }
  
  public String getType(String paramString1, String paramString2) {
    int i = getIndex(paramString1, paramString2);
    return (i >= 0) ? this.mItems[(i << 3) + 4] : null;
  }
  
  public String getType(String paramString) {
    int i = getIndex(paramString);
    return (i >= 0) ? this.mItems[(i << 3) + 4] : null;
  }
  
  public String getValue(String paramString1, String paramString2) {
    int i = getIndex(paramString1, paramString2);
    return (i >= 0) ? this.mItems[(i << 3) + 3] : null;
  }
  
  public String getValue(String paramString) {
    int i = getIndex(paramString);
    return (i >= 0) ? this.mItems[(i << 3) + 3] : null;
  }
  
  public boolean isDeclared(int paramInt) {
    if (paramInt < 0 || paramInt >= this.mLength)
      throw new ArrayIndexOutOfBoundsException(""); 
    return (this.mItems[(paramInt << 3) + 5] != null);
  }
  
  public boolean isDeclared(String paramString) {
    int i = getIndex(paramString);
    if (i < 0)
      throw new IllegalArgumentException(""); 
    return (this.mItems[(i << 3) + 5] != null);
  }
  
  public boolean isDeclared(String paramString1, String paramString2) {
    int i = getIndex(paramString1, paramString2);
    if (i < 0)
      throw new IllegalArgumentException(""); 
    return (this.mItems[(i << 3) + 5] != null);
  }
  
  public boolean isSpecified(int paramInt) {
    if (paramInt < 0 || paramInt >= this.mLength)
      throw new ArrayIndexOutOfBoundsException(""); 
    String str = this.mItems[(paramInt << 3) + 5];
    return (str != null) ? ((str.charAt(0) == 'd')) : true;
  }
  
  public boolean isSpecified(String paramString1, String paramString2) {
    int i = getIndex(paramString1, paramString2);
    if (i < 0)
      throw new IllegalArgumentException(""); 
    String str = this.mItems[(i << 3) + 5];
    return (str != null) ? ((str.charAt(0) == 'd')) : true;
  }
  
  public boolean isSpecified(String paramString) {
    int i = getIndex(paramString);
    if (i < 0)
      throw new IllegalArgumentException(""); 
    String str = this.mItems[(i << 3) + 5];
    return (str != null) ? ((str.charAt(0) == 'd')) : true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\interna\\util\xml\impl\Attrs.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */