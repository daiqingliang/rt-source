package sun.reflect.generics.reflectiveObjects;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.Objects;

public class GenericArrayTypeImpl implements GenericArrayType {
  private final Type genericComponentType;
  
  private GenericArrayTypeImpl(Type paramType) { this.genericComponentType = paramType; }
  
  public static GenericArrayTypeImpl make(Type paramType) { return new GenericArrayTypeImpl(paramType); }
  
  public Type getGenericComponentType() { return this.genericComponentType; }
  
  public String toString() {
    Type type = getGenericComponentType();
    StringBuilder stringBuilder = new StringBuilder();
    if (type instanceof Class) {
      stringBuilder.append(((Class)type).getName());
    } else {
      stringBuilder.append(type.toString());
    } 
    stringBuilder.append("[]");
    return stringBuilder.toString();
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof GenericArrayType) {
      GenericArrayType genericArrayType = (GenericArrayType)paramObject;
      return Objects.equals(this.genericComponentType, genericArrayType.getGenericComponentType());
    } 
    return false;
  }
  
  public int hashCode() { return Objects.hashCode(this.genericComponentType); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\generics\reflectiveObjects\GenericArrayTypeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */