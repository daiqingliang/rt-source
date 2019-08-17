package com.sun.org.apache.xml.internal.utils;

public abstract class XMLStringFactory {
  public abstract XMLString newstr(String paramString);
  
  public abstract XMLString newstr(FastStringBuffer paramFastStringBuffer, int paramInt1, int paramInt2);
  
  public abstract XMLString newstr(char[] paramArrayOfChar, int paramInt1, int paramInt2);
  
  public abstract XMLString emptystr();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\XMLStringFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */