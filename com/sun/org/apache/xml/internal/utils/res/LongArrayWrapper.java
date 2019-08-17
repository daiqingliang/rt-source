package com.sun.org.apache.xml.internal.utils.res;

public class LongArrayWrapper {
  private long[] m_long;
  
  public LongArrayWrapper(long[] paramArrayOfLong) { this.m_long = paramArrayOfLong; }
  
  public long getLong(int paramInt) { return this.m_long[paramInt]; }
  
  public int getLength() { return this.m_long.length; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\res\LongArrayWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */