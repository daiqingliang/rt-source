package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.xml.internal.bind.api.AccessorException;

public class NullSafeAccessor<B, V, P> extends Accessor<B, V> {
  private final Accessor<B, V> core;
  
  private final Lister<B, V, ?, P> lister;
  
  public NullSafeAccessor(Accessor<B, V> paramAccessor, Lister<B, V, ?, P> paramLister) {
    super(paramAccessor.getValueType());
    this.core = paramAccessor;
    this.lister = paramLister;
  }
  
  public V get(B paramB) throws AccessorException {
    Object object = this.core.get(paramB);
    if (object == null) {
      Object object1 = this.lister.startPacking(paramB, this.core);
      this.lister.endPacking(object1, paramB, this.core);
      object = this.core.get(paramB);
    } 
    return (V)object;
  }
  
  public void set(B paramB, V paramV) throws AccessorException { this.core.set(paramB, paramV); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\NullSafeAccessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */