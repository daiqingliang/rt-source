package com.sun.org.apache.xerces.internal.impl.xs.util;

public final class XIntPool {
  private static final short POOL_SIZE = 10;
  
  private static final XInt[] fXIntPool = new XInt[10];
  
  public final XInt getXInt(int paramInt) { return (paramInt >= 0 && paramInt < fXIntPool.length) ? fXIntPool[paramInt] : new XInt(paramInt); }
  
  static  {
    for (byte b = 0; b < 10; b++)
      fXIntPool[b] = new XInt(b); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\x\\util\XIntPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */