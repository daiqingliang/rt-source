package com.sun.xml.internal.messaging.saaj.util;

import java.io.CharArrayWriter;

public class CharWriter extends CharArrayWriter {
  public CharWriter() {}
  
  public CharWriter(int paramInt) { super(paramInt); }
  
  public char[] getChars() { return this.buf; }
  
  public int getCount() { return this.count; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saa\\util\CharWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */