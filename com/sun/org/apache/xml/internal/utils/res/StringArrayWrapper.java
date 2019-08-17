package com.sun.org.apache.xml.internal.utils.res;

public class StringArrayWrapper {
  private String[] m_string;
  
  public StringArrayWrapper(String[] paramArrayOfString) { this.m_string = paramArrayOfString; }
  
  public String getString(int paramInt) { return this.m_string[paramInt]; }
  
  public int getLength() { return this.m_string.length; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\res\StringArrayWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */