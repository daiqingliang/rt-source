package com.sun.xml.internal.fastinfoset.util;

public class CharArrayString extends CharArray {
  protected String _s;
  
  public CharArrayString(String paramString) { this(paramString, true); }
  
  public CharArrayString(String paramString, boolean paramBoolean) {
    this._s = paramString;
    if (paramBoolean) {
      this.ch = this._s.toCharArray();
      this.start = 0;
      this.length = this.ch.length;
    } 
  }
  
  public String toString() { return this._s; }
  
  public int hashCode() { return this._s.hashCode(); }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject instanceof CharArrayString) {
      CharArrayString charArrayString = (CharArrayString)paramObject;
      return this._s.equals(charArrayString._s);
    } 
    if (paramObject instanceof CharArray) {
      CharArray charArray = (CharArray)paramObject;
      if (this.length == charArray.length) {
        int i = this.length;
        int j = this.start;
        int k = charArray.start;
        while (i-- != 0) {
          if (this.ch[j++] != charArray.ch[k++])
            return false; 
        } 
        return true;
      } 
    } 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfose\\util\CharArrayString.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */