package com.sun.org.apache.xml.internal.utils.res;

public class IntArrayWrapper {
  private int[] m_int;
  
  public IntArrayWrapper(int[] paramArrayOfInt) { this.m_int = paramArrayOfInt; }
  
  public int getInt(int paramInt) { return this.m_int[paramInt]; }
  
  public int getLength() { return this.m_int.length; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\res\IntArrayWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */