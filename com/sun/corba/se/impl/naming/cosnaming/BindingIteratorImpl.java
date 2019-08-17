package com.sun.corba.se.impl.naming.cosnaming;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.Binding;
import org.omg.CosNaming.BindingHolder;
import org.omg.CosNaming.BindingIteratorPOA;
import org.omg.CosNaming.BindingListHolder;

public abstract class BindingIteratorImpl extends BindingIteratorPOA {
  protected ORB orb;
  
  public BindingIteratorImpl(ORB paramORB) throws Exception { this.orb = paramORB; }
  
  public boolean next_one(BindingHolder paramBindingHolder) { return NextOne(paramBindingHolder); }
  
  public boolean next_n(int paramInt, BindingListHolder paramBindingListHolder) {
    if (paramInt == 0)
      throw new BAD_PARAM(" 'how_many' parameter is set to 0 which is invalid"); 
    return list(paramInt, paramBindingListHolder);
  }
  
  public boolean list(int paramInt, BindingListHolder paramBindingListHolder) {
    int i = Math.min(RemainingElements(), paramInt);
    Binding[] arrayOfBinding = new Binding[i];
    BindingHolder bindingHolder = new BindingHolder();
    byte b;
    for (b = 0; b < i && NextOne(bindingHolder) == true; b++)
      arrayOfBinding[b] = bindingHolder.value; 
    if (b == 0) {
      paramBindingListHolder.value = new Binding[0];
      return false;
    } 
    paramBindingListHolder.value = arrayOfBinding;
    return true;
  }
  
  public void destroy() { Destroy(); }
  
  protected abstract boolean NextOne(BindingHolder paramBindingHolder);
  
  protected abstract void Destroy();
  
  protected abstract int RemainingElements();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\naming\cosnaming\BindingIteratorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */