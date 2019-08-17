package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.runtime.Location;
import javax.xml.namespace.QName;

class AnyTypeImpl<T, C> extends Object implements NonElement<T, C> {
  private final T type;
  
  private final Navigator<T, C, ?, ?> nav;
  
  public AnyTypeImpl(Navigator<T, C, ?, ?> paramNavigator) {
    this.type = paramNavigator.ref(Object.class);
    this.nav = paramNavigator;
  }
  
  public QName getTypeName() { return ANYTYPE_NAME; }
  
  public T getType() { return (T)this.type; }
  
  public Locatable getUpstream() { return null; }
  
  public boolean isSimpleType() { return false; }
  
  public Location getLocation() { return this.nav.getClassLocation(this.nav.asDecl(Object.class)); }
  
  public final boolean canBeReferencedByIDREF() { return true; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\AnyTypeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */