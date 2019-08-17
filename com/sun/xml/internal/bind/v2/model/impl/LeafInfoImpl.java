package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.core.LeafInfo;
import com.sun.xml.internal.bind.v2.runtime.Location;
import javax.xml.namespace.QName;

abstract class LeafInfoImpl<TypeT, ClassDeclT> extends Object implements LeafInfo<TypeT, ClassDeclT>, Location {
  private final TypeT type;
  
  private final QName typeName;
  
  protected LeafInfoImpl(TypeT paramTypeT, QName paramQName) {
    assert paramTypeT != null;
    this.type = paramTypeT;
    this.typeName = paramQName;
  }
  
  public TypeT getType() { return (TypeT)this.type; }
  
  public final boolean canBeReferencedByIDREF() { return false; }
  
  public QName getTypeName() { return this.typeName; }
  
  public Locatable getUpstream() { return null; }
  
  public Location getLocation() { return this; }
  
  public boolean isSimpleType() { return true; }
  
  public String toString() { return this.type.toString(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\LeafInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */