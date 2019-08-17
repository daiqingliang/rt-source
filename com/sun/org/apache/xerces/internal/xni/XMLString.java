package com.sun.org.apache.xerces.internal.xni;

public class XMLString {
  public char[] ch;
  
  public int offset;
  
  public int length;
  
  public XMLString() {}
  
  public XMLString(char[] paramArrayOfChar, int paramInt1, int paramInt2) { setValues(paramArrayOfChar, paramInt1, paramInt2); }
  
  public XMLString(XMLString paramXMLString) { setValues(paramXMLString); }
  
  public void setValues(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    this.ch = paramArrayOfChar;
    this.offset = paramInt1;
    this.length = paramInt2;
  }
  
  public void setValues(XMLString paramXMLString) { setValues(paramXMLString.ch, paramXMLString.offset, paramXMLString.length); }
  
  public void clear() {
    this.ch = null;
    this.offset = 0;
    this.length = -1;
  }
  
  public boolean equals(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    if (paramArrayOfChar == null)
      return false; 
    if (this.length != paramInt2)
      return false; 
    for (int i = 0; i < paramInt2; i++) {
      if (this.ch[this.offset + i] != paramArrayOfChar[paramInt1 + i])
        return false; 
    } 
    return true;
  }
  
  public boolean equals(String paramString) {
    if (paramString == null)
      return false; 
    if (this.length != paramString.length())
      return false; 
    for (int i = 0; i < this.length; i++) {
      if (this.ch[this.offset + i] != paramString.charAt(i))
        return false; 
    } 
    return true;
  }
  
  public String toString() { return (this.length > 0) ? new String(this.ch, this.offset, this.length) : ""; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xni\XMLString.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */