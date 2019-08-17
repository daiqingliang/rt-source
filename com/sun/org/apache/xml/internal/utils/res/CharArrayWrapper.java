package com.sun.org.apache.xml.internal.utils.res;

public class CharArrayWrapper {
  private char[] m_char;
  
  public CharArrayWrapper(char[] paramArrayOfChar) { this.m_char = paramArrayOfChar; }
  
  public char getChar(int paramInt) { return this.m_char[paramInt]; }
  
  public int getLength() { return this.m_char.length; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\res\CharArrayWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */