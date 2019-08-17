package com.sun.xml.internal.bind.api;

public abstract class RawAccessor<B, V> extends Object {
  public abstract V get(B paramB) throws AccessorException;
  
  public abstract void set(B paramB, V paramV) throws AccessorException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\api\RawAccessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */