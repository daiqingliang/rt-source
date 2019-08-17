package com.sun.corba.se.impl.naming.cosnaming;

import org.omg.CosNaming.NameComponent;

public class InternalBindingKey {
  public NameComponent name;
  
  private int idLen;
  
  private int kindLen;
  
  private int hashVal;
  
  public InternalBindingKey() {}
  
  public InternalBindingKey(NameComponent paramNameComponent) {
    this.idLen = 0;
    this.kindLen = 0;
    setup(paramNameComponent);
  }
  
  protected void setup(NameComponent paramNameComponent) {
    this.name = paramNameComponent;
    if (this.name.id != null)
      this.idLen = this.name.id.length(); 
    if (this.name.kind != null)
      this.kindLen = this.name.kind.length(); 
    this.hashVal = 0;
    if (this.idLen > 0)
      this.hashVal += this.name.id.hashCode(); 
    if (this.kindLen > 0)
      this.hashVal += this.name.kind.hashCode(); 
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null)
      return false; 
    if (paramObject instanceof InternalBindingKey) {
      InternalBindingKey internalBindingKey = (InternalBindingKey)paramObject;
      return (this.idLen != internalBindingKey.idLen || this.kindLen != internalBindingKey.kindLen) ? false : ((this.idLen > 0 && !this.name.id.equals(internalBindingKey.name.id)) ? false : (!(this.kindLen > 0 && !this.name.kind.equals(internalBindingKey.name.kind))));
    } 
    return false;
  }
  
  public int hashCode() { return this.hashVal; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\naming\cosnaming\InternalBindingKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */