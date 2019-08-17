package com.sun.beans;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;

final class WildcardTypeImpl implements WildcardType {
  private final Type[] upperBounds;
  
  private final Type[] lowerBounds;
  
  WildcardTypeImpl(Type[] paramArrayOfType1, Type[] paramArrayOfType2) {
    this.upperBounds = paramArrayOfType1;
    this.lowerBounds = paramArrayOfType2;
  }
  
  public Type[] getUpperBounds() { return (Type[])this.upperBounds.clone(); }
  
  public Type[] getLowerBounds() { return (Type[])this.lowerBounds.clone(); }
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof WildcardType) {
      WildcardType wildcardType = (WildcardType)paramObject;
      return (Arrays.equals(this.upperBounds, wildcardType.getUpperBounds()) && Arrays.equals(this.lowerBounds, wildcardType.getLowerBounds()));
    } 
    return false;
  }
  
  public int hashCode() { return Arrays.hashCode(this.upperBounds) ^ Arrays.hashCode(this.lowerBounds); }
  
  public String toString() {
    Type[] arrayOfType;
    StringBuilder stringBuilder;
    if (this.lowerBounds.length == 0) {
      if (this.upperBounds.length == 0 || Object.class == this.upperBounds[false])
        return "?"; 
      arrayOfType = this.upperBounds;
      stringBuilder = new StringBuilder("? extends ");
    } else {
      arrayOfType = this.lowerBounds;
      stringBuilder = new StringBuilder("? super ");
    } 
    for (byte b = 0; b < arrayOfType.length; b++) {
      if (b)
        stringBuilder.append(" & "); 
      stringBuilder.append((arrayOfType[b] instanceof Class) ? ((Class)arrayOfType[b]).getName() : arrayOfType[b].toString());
    } 
    return stringBuilder.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\WildcardTypeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */