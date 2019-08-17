package com.sun.xml.internal.bind.v2.model.nav;

import com.sun.xml.internal.bind.v2.runtime.Location;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collection;

final class ReflectionNavigator extends Object implements Navigator<Type, Class, Field, Method> {
  private static final ReflectionNavigator INSTANCE = new ReflectionNavigator();
  
  private static final TypeVisitor<Type, Class> baseClassFinder = new TypeVisitor<Type, Class>() {
      public Type onClass(Class param1Class1, Class param1Class2) {
        if (param1Class2 == param1Class1)
          return param1Class2; 
        Type type = param1Class1.getGenericSuperclass();
        if (type != null) {
          Type type1 = (Type)visit(type, param1Class2);
          if (type1 != null)
            return type1; 
        } 
        for (Type type2 : param1Class1.getGenericInterfaces()) {
          Type type1 = (Type)visit(type2, param1Class2);
          if (type1 != null)
            return type1; 
        } 
        return null;
      }
      
      public Type onParameterizdType(ParameterizedType param1ParameterizedType, Class param1Class) {
        Class clazz = (Class)param1ParameterizedType.getRawType();
        if (clazz == param1Class)
          return param1ParameterizedType; 
        Type type = clazz.getGenericSuperclass();
        if (type != null)
          type = (Type)visit(bind(type, clazz, param1ParameterizedType), param1Class); 
        if (type != null)
          return type; 
        for (Type type1 : clazz.getGenericInterfaces()) {
          type = (Type)visit(bind(type1, clazz, param1ParameterizedType), param1Class);
          if (type != null)
            return type; 
        } 
        return null;
      }
      
      public Type onGenericArray(GenericArrayType param1GenericArrayType, Class param1Class) { return null; }
      
      public Type onVariable(TypeVariable param1TypeVariable, Class param1Class) { return (Type)visit(param1TypeVariable.getBounds()[0], param1Class); }
      
      public Type onWildcard(WildcardType param1WildcardType, Class param1Class) { return null; }
      
      private Type bind(Type param1Type, GenericDeclaration param1GenericDeclaration, ParameterizedType param1ParameterizedType) { return (Type)binder.visit(param1Type, new ReflectionNavigator.BinderArg(param1GenericDeclaration, param1ParameterizedType.getActualTypeArguments())); }
    };
  
  private static final TypeVisitor<Type, BinderArg> binder = new TypeVisitor<Type, BinderArg>() {
      public Type onClass(Class param1Class, ReflectionNavigator.BinderArg param1BinderArg) { return param1Class; }
      
      public Type onParameterizdType(ParameterizedType param1ParameterizedType, ReflectionNavigator.BinderArg param1BinderArg) {
        Type[] arrayOfType = param1ParameterizedType.getActualTypeArguments();
        boolean bool = false;
        for (byte b = 0; b < arrayOfType.length; b++) {
          Type type1 = arrayOfType[b];
          arrayOfType[b] = (Type)visit(type1, param1BinderArg);
          bool |= ((type1 != arrayOfType[b]) ? 1 : 0);
        } 
        Type type = param1ParameterizedType.getOwnerType();
        if (type != null)
          type = (Type)visit(type, param1BinderArg); 
        bool |= ((param1ParameterizedType.getOwnerType() != type) ? 1 : 0);
        return !bool ? param1ParameterizedType : new ParameterizedTypeImpl((Class)param1ParameterizedType.getRawType(), arrayOfType, type);
      }
      
      public Type onGenericArray(GenericArrayType param1GenericArrayType, ReflectionNavigator.BinderArg param1BinderArg) {
        Type type = (Type)visit(param1GenericArrayType.getGenericComponentType(), param1BinderArg);
        return (type == param1GenericArrayType.getGenericComponentType()) ? param1GenericArrayType : new GenericArrayTypeImpl(type);
      }
      
      public Type onVariable(TypeVariable param1TypeVariable, ReflectionNavigator.BinderArg param1BinderArg) { return param1BinderArg.replace(param1TypeVariable); }
      
      public Type onWildcard(WildcardType param1WildcardType, ReflectionNavigator.BinderArg param1BinderArg) {
        Type[] arrayOfType1 = param1WildcardType.getLowerBounds();
        Type[] arrayOfType2 = param1WildcardType.getUpperBounds();
        boolean bool = false;
        byte b;
        for (b = 0; b < arrayOfType1.length; b++) {
          Type type = arrayOfType1[b];
          arrayOfType1[b] = (Type)visit(type, param1BinderArg);
          bool |= ((type != arrayOfType1[b]) ? 1 : 0);
        } 
        for (b = 0; b < arrayOfType2.length; b++) {
          Type type = arrayOfType2[b];
          arrayOfType2[b] = (Type)visit(type, param1BinderArg);
          bool |= ((type != arrayOfType2[b]) ? 1 : 0);
        } 
        return !bool ? param1WildcardType : new WildcardTypeImpl(arrayOfType1, arrayOfType2);
      }
    };
  
  private static final TypeVisitor<Class, Void> eraser = new TypeVisitor<Class, Void>() {
      public Class onClass(Class param1Class, Void param1Void) { return param1Class; }
      
      public Class onParameterizdType(ParameterizedType param1ParameterizedType, Void param1Void) { return (Class)visit(param1ParameterizedType.getRawType(), null); }
      
      public Class onGenericArray(GenericArrayType param1GenericArrayType, Void param1Void) { return Array.newInstance((Class)visit(param1GenericArrayType.getGenericComponentType(), null), 0).getClass(); }
      
      public Class onVariable(TypeVariable param1TypeVariable, Void param1Void) { return (Class)visit(param1TypeVariable.getBounds()[0], null); }
      
      public Class onWildcard(WildcardType param1WildcardType, Void param1Void) { return (Class)visit(param1WildcardType.getUpperBounds()[0], null); }
    };
  
  static ReflectionNavigator getInstance() { return INSTANCE; }
  
  public Class getSuperClass(Class paramClass) {
    if (paramClass == Object.class)
      return null; 
    Class clazz = paramClass.getSuperclass();
    if (clazz == null)
      clazz = Object.class; 
    return clazz;
  }
  
  public Type getBaseClass(Type paramType, Class paramClass) { return (Type)baseClassFinder.visit(paramType, paramClass); }
  
  public String getClassName(Class paramClass) { return paramClass.getName(); }
  
  public String getTypeName(Type paramType) {
    if (paramType instanceof Class) {
      Class clazz = (Class)paramType;
      return clazz.isArray() ? (getTypeName(clazz.getComponentType()) + "[]") : clazz.getName();
    } 
    return paramType.toString();
  }
  
  public String getClassShortName(Class paramClass) { return paramClass.getSimpleName(); }
  
  public Collection<? extends Field> getDeclaredFields(Class paramClass) { return Arrays.asList(paramClass.getDeclaredFields()); }
  
  public Field getDeclaredField(Class paramClass, String paramString) {
    try {
      return paramClass.getDeclaredField(paramString);
    } catch (NoSuchFieldException noSuchFieldException) {
      return null;
    } 
  }
  
  public Collection<? extends Method> getDeclaredMethods(Class paramClass) { return Arrays.asList(paramClass.getDeclaredMethods()); }
  
  public Class getDeclaringClassForField(Field paramField) { return paramField.getDeclaringClass(); }
  
  public Class getDeclaringClassForMethod(Method paramMethod) { return paramMethod.getDeclaringClass(); }
  
  public Type getFieldType(Field paramField) {
    if (paramField.getType().isArray()) {
      Class clazz = paramField.getType().getComponentType();
      if (clazz.isPrimitive())
        return Array.newInstance(clazz, 0).getClass(); 
    } 
    return fix(paramField.getGenericType());
  }
  
  public String getFieldName(Field paramField) { return paramField.getName(); }
  
  public String getMethodName(Method paramMethod) { return paramMethod.getName(); }
  
  public Type getReturnType(Method paramMethod) { return fix(paramMethod.getGenericReturnType()); }
  
  public Type[] getMethodParameters(Method paramMethod) { return paramMethod.getGenericParameterTypes(); }
  
  public boolean isStaticMethod(Method paramMethod) { return Modifier.isStatic(paramMethod.getModifiers()); }
  
  public boolean isFinalMethod(Method paramMethod) { return Modifier.isFinal(paramMethod.getModifiers()); }
  
  public boolean isSubClassOf(Type paramType1, Type paramType2) { return erasure(paramType2).isAssignableFrom(erasure(paramType1)); }
  
  public Class ref(Class paramClass) { return paramClass; }
  
  public Class use(Class paramClass) { return paramClass; }
  
  public Class asDecl(Type paramType) { return erasure(paramType); }
  
  public Class asDecl(Class paramClass) { return paramClass; }
  
  public <T> Class<T> erasure(Type paramType) { return (Class)eraser.visit(paramType, null); }
  
  public boolean isAbstract(Class paramClass) { return Modifier.isAbstract(paramClass.getModifiers()); }
  
  public boolean isFinal(Class paramClass) { return Modifier.isFinal(paramClass.getModifiers()); }
  
  public Type createParameterizedType(Class paramClass, Type... paramVarArgs) { return new ParameterizedTypeImpl(paramClass, paramVarArgs, null); }
  
  public boolean isArray(Type paramType) {
    if (paramType instanceof Class) {
      Class clazz = (Class)paramType;
      return clazz.isArray();
    } 
    return (paramType instanceof GenericArrayType);
  }
  
  public boolean isArrayButNotByteArray(Type paramType) {
    if (paramType instanceof Class) {
      Class clazz = (Class)paramType;
      return (clazz.isArray() && clazz != byte[].class);
    } 
    if (paramType instanceof GenericArrayType) {
      paramType = ((GenericArrayType)paramType).getGenericComponentType();
      return (paramType != byte.class);
    } 
    return false;
  }
  
  public Type getComponentType(Type paramType) {
    if (paramType instanceof Class) {
      Class clazz = (Class)paramType;
      return clazz.getComponentType();
    } 
    if (paramType instanceof GenericArrayType)
      return ((GenericArrayType)paramType).getGenericComponentType(); 
    throw new IllegalArgumentException();
  }
  
  public Type getTypeArgument(Type paramType, int paramInt) {
    if (paramType instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType)paramType;
      return fix(parameterizedType.getActualTypeArguments()[paramInt]);
    } 
    throw new IllegalArgumentException();
  }
  
  public boolean isParameterizedType(Type paramType) { return paramType instanceof ParameterizedType; }
  
  public boolean isPrimitive(Type paramType) {
    if (paramType instanceof Class) {
      Class clazz = (Class)paramType;
      return clazz.isPrimitive();
    } 
    return false;
  }
  
  public Type getPrimitive(Class paramClass) {
    assert paramClass.isPrimitive();
    return paramClass;
  }
  
  public Location getClassLocation(final Class clazz) { return new Location() {
        public String toString() { return clazz.getName(); }
      }; }
  
  public Location getFieldLocation(final Field field) { return new Location() {
        public String toString() { return field.toString(); }
      }; }
  
  public Location getMethodLocation(final Method method) { return new Location() {
        public String toString() { return method.toString(); }
      }; }
  
  public boolean hasDefaultConstructor(Class paramClass) {
    try {
      paramClass.getDeclaredConstructor(new Class[0]);
      return true;
    } catch (NoSuchMethodException noSuchMethodException) {
      return false;
    } 
  }
  
  public boolean isStaticField(Field paramField) { return Modifier.isStatic(paramField.getModifiers()); }
  
  public boolean isPublicMethod(Method paramMethod) { return Modifier.isPublic(paramMethod.getModifiers()); }
  
  public boolean isPublicField(Field paramField) { return Modifier.isPublic(paramField.getModifiers()); }
  
  public boolean isEnum(Class paramClass) { return Enum.class.isAssignableFrom(paramClass); }
  
  public Field[] getEnumConstants(Class paramClass) {
    try {
      Object[] arrayOfObject = paramClass.getEnumConstants();
      Field[] arrayOfField = new Field[arrayOfObject.length];
      for (byte b = 0; b < arrayOfObject.length; b++)
        arrayOfField[b] = paramClass.getField(((Enum)arrayOfObject[b]).name()); 
      return arrayOfField;
    } catch (NoSuchFieldException noSuchFieldException) {
      throw new NoSuchFieldError(noSuchFieldException.getMessage());
    } 
  }
  
  public Type getVoidType() { return Void.class; }
  
  public String getPackageName(Class paramClass) {
    String str = paramClass.getName();
    int i = str.lastIndexOf('.');
    return (i < 0) ? "" : str.substring(0, i);
  }
  
  public Class loadObjectFactory(Class paramClass, String paramString) {
    ClassLoader classLoader = SecureLoader.getClassClassLoader(paramClass);
    if (classLoader == null)
      classLoader = SecureLoader.getSystemClassLoader(); 
    try {
      return classLoader.loadClass(paramString + ".ObjectFactory");
    } catch (ClassNotFoundException classNotFoundException) {
      return null;
    } 
  }
  
  public boolean isBridgeMethod(Method paramMethod) { return paramMethod.isBridge(); }
  
  public boolean isOverriding(Method paramMethod, Class paramClass) {
    String str = paramMethod.getName();
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    while (paramClass != null) {
      try {
        if (paramClass.getDeclaredMethod(str, arrayOfClass) != null)
          return true; 
      } catch (NoSuchMethodException noSuchMethodException) {}
      paramClass = paramClass.getSuperclass();
    } 
    return false;
  }
  
  public boolean isInterface(Class paramClass) { return paramClass.isInterface(); }
  
  public boolean isTransient(Field paramField) { return Modifier.isTransient(paramField.getModifiers()); }
  
  public boolean isInnerClass(Class paramClass) { return (paramClass.getEnclosingClass() != null && !Modifier.isStatic(paramClass.getModifiers())); }
  
  public boolean isSameType(Type paramType1, Type paramType2) { return paramType1.equals(paramType2); }
  
  private Type fix(Type paramType) {
    if (!(paramType instanceof GenericArrayType))
      return paramType; 
    GenericArrayType genericArrayType = (GenericArrayType)paramType;
    if (genericArrayType.getGenericComponentType() instanceof Class) {
      Class clazz = (Class)genericArrayType.getGenericComponentType();
      return Array.newInstance(clazz, 0).getClass();
    } 
    return paramType;
  }
  
  private static class BinderArg {
    final TypeVariable[] params;
    
    final Type[] args;
    
    BinderArg(TypeVariable[] param1ArrayOfTypeVariable, Type[] param1ArrayOfType) {
      this.params = param1ArrayOfTypeVariable;
      this.args = param1ArrayOfType;
      assert param1ArrayOfTypeVariable.length == param1ArrayOfType.length;
    }
    
    public BinderArg(GenericDeclaration param1GenericDeclaration, Type[] param1ArrayOfType) { this(param1GenericDeclaration.getTypeParameters(), param1ArrayOfType); }
    
    Type replace(TypeVariable param1TypeVariable) {
      for (byte b = 0; b < this.params.length; b++) {
        if (this.params[b].equals(param1TypeVariable))
          return this.args[b]; 
      } 
      return param1TypeVariable;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\nav\ReflectionNavigator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */