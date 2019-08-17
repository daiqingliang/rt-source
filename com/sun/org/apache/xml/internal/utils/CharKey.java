package com.sun.org.apache.xml.internal.utils;

public class CharKey {
  private char m_char;
  
  public CharKey(char paramChar) { this.m_char = paramChar; }
  
  public CharKey() {}
  
  public final void setChar(char paramChar) { this.m_char = paramChar; }
  
  public final int hashCode() { return this.m_char; }
  
  public final boolean equals(Object paramObject) { return (((CharKey)paramObject).m_char == this.m_char); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\CharKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */