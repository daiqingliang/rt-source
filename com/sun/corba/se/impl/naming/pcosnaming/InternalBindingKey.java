package com.sun.corba.se.impl.naming.pcosnaming;

import java.io.Serializable;
import org.omg.CosNaming.NameComponent;

public class InternalBindingKey implements Serializable {
  private static final long serialVersionUID = -5410796631793704055L;
  
  public String id;
  
  public String kind;
  
  public InternalBindingKey() {}
  
  public InternalBindingKey(NameComponent paramNameComponent) { setup(paramNameComponent); }
  
  protected void setup(NameComponent paramNameComponent) {
    this.id = paramNameComponent.id;
    this.kind = paramNameComponent.kind;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null)
      return false; 
    if (paramObject instanceof InternalBindingKey) {
      InternalBindingKey internalBindingKey = (InternalBindingKey)paramObject;
      if (this.id != null && internalBindingKey.id != null) {
        if (this.id.length() != internalBindingKey.id.length())
          return false; 
        if (this.id.length() > 0 && !this.id.equals(internalBindingKey.id))
          return false; 
      } else if ((this.id == null && internalBindingKey.id != null) || (this.id != null && internalBindingKey.id == null)) {
        return false;
      } 
      if (this.kind != null && internalBindingKey.kind != null) {
        if (this.kind.length() != internalBindingKey.kind.length())
          return false; 
        if (this.kind.length() > 0 && !this.kind.equals(internalBindingKey.kind))
          return false; 
      } else if ((this.kind == null && internalBindingKey.kind != null) || (this.kind != null && internalBindingKey.kind == null)) {
        return false;
      } 
      return true;
    } 
    return false;
  }
  
  public int hashCode() {
    int i = 0;
    if (this.id.length() > 0)
      i += this.id.hashCode(); 
    if (this.kind.length() > 0)
      i += this.kind.hashCode(); 
    return i;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\naming\pcosnaming\InternalBindingKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */