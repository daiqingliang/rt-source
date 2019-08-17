package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.XMLString;

public class XMLStringBuffer extends XMLString {
  public static final int DEFAULT_SIZE = 32;
  
  public XMLStringBuffer() { this(32); }
  
  public XMLStringBuffer(int paramInt) { this.ch = new char[paramInt]; }
  
  public XMLStringBuffer(char paramChar) {
    this(1);
    append(paramChar);
  }
  
  public XMLStringBuffer(String paramString) {
    this(paramString.length());
    append(paramString);
  }
  
  public XMLStringBuffer(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    this(paramInt2);
    append(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public XMLStringBuffer(XMLString paramXMLString) {
    this(paramXMLString.length);
    append(paramXMLString);
  }
  
  public void clear() {
    this.offset = 0;
    this.length = 0;
  }
  
  public void append(char paramChar) {
    if (this.length + 1 > this.ch.length) {
      int i = this.ch.length * 2;
      if (i < this.ch.length + 32)
        i = this.ch.length + 32; 
      char[] arrayOfChar = new char[i];
      System.arraycopy(this.ch, 0, arrayOfChar, 0, this.length);
      this.ch = arrayOfChar;
    } 
    this.ch[this.length] = paramChar;
    this.length++;
  }
  
  public void append(String paramString) {
    int i = paramString.length();
    if (this.length + i > this.ch.length) {
      int j = this.ch.length * 2;
      if (j < this.ch.length + i + 32)
        j = this.ch.length + i + 32; 
      char[] arrayOfChar = new char[j];
      System.arraycopy(this.ch, 0, arrayOfChar, 0, this.length);
      this.ch = arrayOfChar;
    } 
    paramString.getChars(0, i, this.ch, this.length);
    this.length += i;
  }
  
  public void append(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    if (this.length + paramInt2 > this.ch.length) {
      int i = this.ch.length * 2;
      if (i < this.ch.length + paramInt2 + 32)
        i = this.ch.length + paramInt2 + 32; 
      char[] arrayOfChar = new char[i];
      System.arraycopy(this.ch, 0, arrayOfChar, 0, this.length);
      this.ch = arrayOfChar;
    } 
    if (paramArrayOfChar != null && paramInt2 > 0) {
      System.arraycopy(paramArrayOfChar, paramInt1, this.ch, this.length, paramInt2);
      this.length += paramInt2;
    } 
  }
  
  public void append(XMLString paramXMLString) { append(paramXMLString.ch, paramXMLString.offset, paramXMLString.length); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\interna\\util\XMLStringBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */