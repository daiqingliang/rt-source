package com.sun.corba.se.impl.orbutil;

import java.util.ArrayList;

public class DenseIntMapImpl {
  private ArrayList list = new ArrayList();
  
  private void checkKey(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException("Key must be >= 0."); 
  }
  
  public Object get(int paramInt) {
    checkKey(paramInt);
    Object object = null;
    if (paramInt < this.list.size())
      object = this.list.get(paramInt); 
    return object;
  }
  
  public void set(int paramInt, Object paramObject) {
    checkKey(paramInt);
    extend(paramInt);
    this.list.set(paramInt, paramObject);
  }
  
  private void extend(int paramInt) {
    if (paramInt >= this.list.size()) {
      this.list.ensureCapacity(paramInt + 1);
      int i = this.list.size();
      while (i++ <= paramInt)
        this.list.add(null); 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orbutil\DenseIntMapImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */