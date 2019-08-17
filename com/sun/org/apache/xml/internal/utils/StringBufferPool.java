package com.sun.org.apache.xml.internal.utils;

public class StringBufferPool {
  private static ObjectPool m_stringBufPool = new ObjectPool(FastStringBuffer.class);
  
  public static FastStringBuffer get() { return (FastStringBuffer)m_stringBufPool.getInstance(); }
  
  public static void free(FastStringBuffer paramFastStringBuffer) {
    paramFastStringBuffer.setLength(0);
    m_stringBufPool.freeInstance(paramFastStringBuffer);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\StringBufferPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */