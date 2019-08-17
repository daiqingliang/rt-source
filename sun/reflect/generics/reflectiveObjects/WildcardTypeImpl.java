package sun.reflect.generics.reflectiveObjects;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.tree.FieldTypeSignature;
import sun.reflect.generics.visitor.Reifier;

public class WildcardTypeImpl extends LazyReflectiveObjectGenerator implements WildcardType {
  private Type[] upperBounds;
  
  private Type[] lowerBounds;
  
  private FieldTypeSignature[] upperBoundASTs;
  
  private FieldTypeSignature[] lowerBoundASTs;
  
  private WildcardTypeImpl(FieldTypeSignature[] paramArrayOfFieldTypeSignature1, FieldTypeSignature[] paramArrayOfFieldTypeSignature2, GenericsFactory paramGenericsFactory) {
    super(paramGenericsFactory);
    this.upperBoundASTs = paramArrayOfFieldTypeSignature1;
    this.lowerBoundASTs = paramArrayOfFieldTypeSignature2;
  }
  
  public static WildcardTypeImpl make(FieldTypeSignature[] paramArrayOfFieldTypeSignature1, FieldTypeSignature[] paramArrayOfFieldTypeSignature2, GenericsFactory paramGenericsFactory) { return new WildcardTypeImpl(paramArrayOfFieldTypeSignature1, paramArrayOfFieldTypeSignature2, paramGenericsFactory); }
  
  private FieldTypeSignature[] getUpperBoundASTs() {
    assert this.upperBounds == null;
    return this.upperBoundASTs;
  }
  
  private FieldTypeSignature[] getLowerBoundASTs() {
    assert this.lowerBounds == null;
    return this.lowerBoundASTs;
  }
  
  public Type[] getUpperBounds() {
    if (this.upperBounds == null) {
      FieldTypeSignature[] arrayOfFieldTypeSignature = getUpperBoundASTs();
      Type[] arrayOfType = new Type[arrayOfFieldTypeSignature.length];
      for (byte b = 0; b < arrayOfFieldTypeSignature.length; b++) {
        Reifier reifier = getReifier();
        arrayOfFieldTypeSignature[b].accept(reifier);
        arrayOfType[b] = reifier.getResult();
      } 
      this.upperBounds = arrayOfType;
    } 
    return (Type[])this.upperBounds.clone();
  }
  
  public Type[] getLowerBounds() {
    if (this.lowerBounds == null) {
      FieldTypeSignature[] arrayOfFieldTypeSignature = getLowerBoundASTs();
      Type[] arrayOfType = new Type[arrayOfFieldTypeSignature.length];
      for (byte b = 0; b < arrayOfFieldTypeSignature.length; b++) {
        Reifier reifier = getReifier();
        arrayOfFieldTypeSignature[b].accept(reifier);
        arrayOfType[b] = reifier.getResult();
      } 
      this.lowerBounds = arrayOfType;
    } 
    return (Type[])this.lowerBounds.clone();
  }
  
  public String toString() {
    Type[] arrayOfType1 = getLowerBounds();
    Type[] arrayOfType2 = arrayOfType1;
    StringBuilder stringBuilder = new StringBuilder();
    if (arrayOfType1.length > 0) {
      stringBuilder.append("? super ");
    } else {
      Type[] arrayOfType = getUpperBounds();
      if (arrayOfType.length > 0 && !arrayOfType[0].equals(Object.class)) {
        arrayOfType2 = arrayOfType;
        stringBuilder.append("? extends ");
      } else {
        return "?";
      } 
    } 
    assert arrayOfType2.length > 0;
    boolean bool = true;
    for (Type type : arrayOfType2) {
      if (!bool)
        stringBuilder.append(" & "); 
      bool = false;
      stringBuilder.append(type.getTypeName());
    } 
    return stringBuilder.toString();
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof WildcardType) {
      WildcardType wildcardType = (WildcardType)paramObject;
      return (Arrays.equals(getLowerBounds(), wildcardType.getLowerBounds()) && Arrays.equals(getUpperBounds(), wildcardType.getUpperBounds()));
    } 
    return false;
  }
  
  public int hashCode() {
    Type[] arrayOfType1 = getLowerBounds();
    Type[] arrayOfType2 = getUpperBounds();
    return Arrays.hashCode(arrayOfType1) ^ Arrays.hashCode(arrayOfType2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\generics\reflectiveObjects\WildcardTypeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */