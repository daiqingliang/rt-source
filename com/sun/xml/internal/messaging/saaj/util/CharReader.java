package com.sun.xml.internal.messaging.saaj.util;

import java.io.CharArrayReader;

public class CharReader extends CharArrayReader {
  public CharReader(char[] paramArrayOfChar, int paramInt) { super(paramArrayOfChar, 0, paramInt); }
  
  public CharReader(char[] paramArrayOfChar, int paramInt1, int paramInt2) { super(paramArrayOfChar, paramInt1, paramInt2); }
  
  public char[] getChars() { return this.buf; }
  
  public int getCount() { return this.count; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saa\\util\CharReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */