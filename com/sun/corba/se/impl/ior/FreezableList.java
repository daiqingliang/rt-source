package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.MakeImmutable;
import java.util.AbstractList;
import java.util.List;

public class FreezableList extends AbstractList {
  private List delegate = null;
  
  private boolean immutable = false;
  
  public boolean equals(Object paramObject) {
    if (paramObject == null)
      return false; 
    if (!(paramObject instanceof FreezableList))
      return false; 
    FreezableList freezableList = (FreezableList)paramObject;
    return (this.delegate.equals(freezableList.delegate) && this.immutable == freezableList.immutable);
  }
  
  public int hashCode() { return this.delegate.hashCode(); }
  
  public FreezableList(List paramList, boolean paramBoolean) {
    this.delegate = paramList;
    this.immutable = paramBoolean;
  }
  
  public FreezableList(List paramList) { this(paramList, false); }
  
  public void makeImmutable() { this.immutable = true; }
  
  public boolean isImmutable() { return this.immutable; }
  
  public void makeElementsImmutable() {
    for (Object object : this) {
      if (object instanceof MakeImmutable) {
        MakeImmutable makeImmutable = (MakeImmutable)object;
        makeImmutable.makeImmutable();
      } 
    } 
  }
  
  public int size() { return this.delegate.size(); }
  
  public Object get(int paramInt) { return this.delegate.get(paramInt); }
  
  public Object set(int paramInt, Object paramObject) {
    if (this.immutable)
      throw new UnsupportedOperationException(); 
    return this.delegate.set(paramInt, paramObject);
  }
  
  public void add(int paramInt, Object paramObject) {
    if (this.immutable)
      throw new UnsupportedOperationException(); 
    this.delegate.add(paramInt, paramObject);
  }
  
  public Object remove(int paramInt) {
    if (this.immutable)
      throw new UnsupportedOperationException(); 
    return this.delegate.remove(paramInt);
  }
  
  public List subList(int paramInt1, int paramInt2) {
    List list = this.delegate.subList(paramInt1, paramInt2);
    return new FreezableList(list, this.immutable);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\ior\FreezableList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */