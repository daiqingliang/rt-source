package com.sun.corba.se.impl.naming.pcosnaming;

import java.io.Serializable;
import org.omg.CORBA.Object;
import org.omg.CosNaming.BindingType;

public class InternalBindingValue implements Serializable {
  public BindingType theBindingType;
  
  public String strObjectRef;
  
  private Object theObjectRef;
  
  public InternalBindingValue() {}
  
  public InternalBindingValue(BindingType paramBindingType, String paramString) {
    this.theBindingType = paramBindingType;
    this.strObjectRef = paramString;
  }
  
  public Object getObjectRef() { return this.theObjectRef; }
  
  public void setObjectRef(Object paramObject) { this.theObjectRef = paramObject; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\naming\pcosnaming\InternalBindingValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */