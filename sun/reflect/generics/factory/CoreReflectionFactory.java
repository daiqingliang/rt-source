package sun.reflect.generics.factory;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import sun.reflect.generics.reflectiveObjects.GenericArrayTypeImpl;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;
import sun.reflect.generics.reflectiveObjects.TypeVariableImpl;
import sun.reflect.generics.reflectiveObjects.WildcardTypeImpl;
import sun.reflect.generics.scope.Scope;
import sun.reflect.generics.tree.FieldTypeSignature;

public class CoreReflectionFactory implements GenericsFactory {
  private final GenericDeclaration decl;
  
  private final Scope scope;
  
  private CoreReflectionFactory(GenericDeclaration paramGenericDeclaration, Scope paramScope) {
    this.decl = paramGenericDeclaration;
    this.scope = paramScope;
  }
  
  private GenericDeclaration getDecl() { return this.decl; }
  
  private Scope getScope() { return this.scope; }
  
  private ClassLoader getDeclsLoader() {
    if (this.decl instanceof Class)
      return ((Class)this.decl).getClassLoader(); 
    if (this.decl instanceof Method)
      return ((Method)this.decl).getDeclaringClass().getClassLoader(); 
    assert this.decl instanceof Constructor : "Constructor expected";
    return ((Constructor)this.decl).getDeclaringClass().getClassLoader();
  }
  
  public static CoreReflectionFactory make(GenericDeclaration paramGenericDeclaration, Scope paramScope) { return new CoreReflectionFactory(paramGenericDeclaration, paramScope); }
  
  public TypeVariable<?> makeTypeVariable(String paramString, FieldTypeSignature[] paramArrayOfFieldTypeSignature) { return TypeVariableImpl.make(getDecl(), paramString, paramArrayOfFieldTypeSignature, this); }
  
  public WildcardType makeWildcard(FieldTypeSignature[] paramArrayOfFieldTypeSignature1, FieldTypeSignature[] paramArrayOfFieldTypeSignature2) { return WildcardTypeImpl.make(paramArrayOfFieldTypeSignature1, paramArrayOfFieldTypeSignature2, this); }
  
  public ParameterizedType makeParameterizedType(Type paramType1, Type[] paramArrayOfType, Type paramType2) { return ParameterizedTypeImpl.make((Class)paramType1, paramArrayOfType, paramType2); }
  
  public TypeVariable<?> findTypeVariable(String paramString) { return getScope().lookup(paramString); }
  
  public Type makeNamedType(String paramString) {
    try {
      return Class.forName(paramString, false, getDeclsLoader());
    } catch (ClassNotFoundException classNotFoundException) {
      throw new TypeNotPresentException(paramString, classNotFoundException);
    } 
  }
  
  public Type makeArrayType(Type paramType) { return (paramType instanceof Class) ? Array.newInstance((Class)paramType, 0).getClass() : GenericArrayTypeImpl.make(paramType); }
  
  public Type makeByte() { return byte.class; }
  
  public Type makeBool() { return boolean.class; }
  
  public Type makeShort() { return short.class; }
  
  public Type makeChar() { return char.class; }
  
  public Type makeInt() { return int.class; }
  
  public Type makeLong() { return long.class; }
  
  public Type makeFloat() { return float.class; }
  
  public Type makeDouble() { return double.class; }
  
  public Type makeVoid() { return void.class; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\generics\factory\CoreReflectionFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */