package com.sun.org.apache.xml.internal.dtm;

public abstract class DTMAxisTraverser {
  public int first(int paramInt) { return next(paramInt, paramInt); }
  
  public int first(int paramInt1, int paramInt2) { return next(paramInt1, paramInt1, paramInt2); }
  
  public abstract int next(int paramInt1, int paramInt2);
  
  public abstract int next(int paramInt1, int paramInt2, int paramInt3);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\dtm\DTMAxisTraverser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */