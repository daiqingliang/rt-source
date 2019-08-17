package com.sun.corba.se.impl.naming.cosnaming;

import org.omg.CORBA.Object;
import org.omg.CosNaming.Binding;

public class InternalBindingValue {
  public Binding theBinding;
  
  public String strObjectRef;
  
  public Object theObjectRef;
  
  public InternalBindingValue() {}
  
  public InternalBindingValue(Binding paramBinding, String paramString) {
    this.theBinding = paramBinding;
    this.strObjectRef = paramString;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\naming\cosnaming\InternalBindingValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */