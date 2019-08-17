package com.sun.xml.internal.bind.v2.runtime.output;

import java.io.IOException;

public abstract class Pcdata implements CharSequence {
  public abstract void writeTo(UTF8XmlOutput paramUTF8XmlOutput) throws IOException;
  
  public void writeTo(char[] paramArrayOfChar, int paramInt) { toString().getChars(0, length(), paramArrayOfChar, paramInt); }
  
  public abstract String toString();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\output\Pcdata.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */