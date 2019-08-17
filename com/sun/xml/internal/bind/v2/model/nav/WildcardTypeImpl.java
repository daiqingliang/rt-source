package com.sun.xml.internal.bind.v2.model.nav;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;

final class WildcardTypeImpl implements WildcardType {
  private final Type[] ub;
  
  private final Type[] lb;
  
  public WildcardTypeImpl(Type[] paramArrayOfType1, Type[] paramArrayOfType2) {
    this.ub = paramArrayOfType1;
    this.lb = paramArrayOfType2;
  }
  
  public Type[] getUpperBounds() { return this.ub; }
  
  public Type[] getLowerBounds() { return this.lb; }
  
  public int hashCode() { return Arrays.hashCode(this.lb) ^ Arrays.hashCode(this.ub); }
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof WildcardType) {
      WildcardType wildcardType = (WildcardType)paramObject;
      return (Arrays.equals(wildcardType.getLowerBounds(), this.lb) && Arrays.equals(wildcardType.getUpperBounds(), this.ub));
    } 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\nav\WildcardTypeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */