package com.sun.jmx.mbeanserver;

import java.lang.annotation.Annotation;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import javax.management.Descriptor;
import javax.management.ImmutableDescriptor;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.openmbean.OpenMBeanAttributeInfoSupport;
import javax.management.openmbean.OpenMBeanOperationInfoSupport;
import javax.management.openmbean.OpenMBeanParameterInfo;
import javax.management.openmbean.OpenMBeanParameterInfoSupport;
import javax.management.openmbean.OpenType;

class MXBeanIntrospector extends MBeanIntrospector<ConvertingMethod> {
  private static final MXBeanIntrospector instance = new MXBeanIntrospector();
  
  private final MBeanIntrospector.PerInterfaceMap<ConvertingMethod> perInterfaceMap = new MBeanIntrospector.PerInterfaceMap();
  
  private static final MBeanIntrospector.MBeanInfoMap mbeanInfoMap = new MBeanIntrospector.MBeanInfoMap();
  
  static MXBeanIntrospector getInstance() { return instance; }
  
  MBeanIntrospector.PerInterfaceMap<ConvertingMethod> getPerInterfaceMap() { return this.perInterfaceMap; }
  
  MBeanIntrospector.MBeanInfoMap getMBeanInfoMap() { return mbeanInfoMap; }
  
  MBeanAnalyzer<ConvertingMethod> getAnalyzer(Class<?> paramClass) throws NotCompliantMBeanException { return MBeanAnalyzer.analyzer(paramClass, this); }
  
  boolean isMXBean() { return true; }
  
  ConvertingMethod mFrom(Method paramMethod) { return ConvertingMethod.from(paramMethod); }
  
  String getName(ConvertingMethod paramConvertingMethod) { return paramConvertingMethod.getName(); }
  
  Type getGenericReturnType(ConvertingMethod paramConvertingMethod) { return paramConvertingMethod.getGenericReturnType(); }
  
  Type[] getGenericParameterTypes(ConvertingMethod paramConvertingMethod) { return paramConvertingMethod.getGenericParameterTypes(); }
  
  String[] getSignature(ConvertingMethod paramConvertingMethod) { return paramConvertingMethod.getOpenSignature(); }
  
  void checkMethod(ConvertingMethod paramConvertingMethod) { paramConvertingMethod.checkCallFromOpen(); }
  
  Object invokeM2(ConvertingMethod paramConvertingMethod, Object paramObject1, Object[] paramArrayOfObject, Object paramObject2) throws InvocationTargetException, IllegalAccessException, MBeanException { return paramConvertingMethod.invokeWithOpenReturn((MXBeanLookup)paramObject2, paramObject1, paramArrayOfObject); }
  
  boolean validParameter(ConvertingMethod paramConvertingMethod, Object paramObject1, int paramInt, Object paramObject2) {
    Object object;
    if (paramObject1 == null) {
      object = paramConvertingMethod.getGenericParameterTypes()[paramInt];
      return (!(object instanceof Class) || !((Class)object).isPrimitive());
    } 
    try {
      object = paramConvertingMethod.fromOpenParameter((MXBeanLookup)paramObject2, paramObject1, paramInt);
    } catch (Exception exception) {
      return true;
    } 
    return isValidParameter(paramConvertingMethod.getMethod(), object, paramInt);
  }
  
  MBeanAttributeInfo getMBeanAttributeInfo(String paramString, ConvertingMethod paramConvertingMethod1, ConvertingMethod paramConvertingMethod2) {
    MBeanAttributeInfo mBeanAttributeInfo;
    Type type;
    OpenType openType;
    boolean bool1 = (paramConvertingMethod1 != null);
    boolean bool2 = (paramConvertingMethod2 != null);
    boolean bool3 = (bool1 && getName(paramConvertingMethod1).startsWith("is"));
    String str = paramString;
    if (bool1) {
      openType = paramConvertingMethod1.getOpenReturnType();
      type = paramConvertingMethod1.getGenericReturnType();
    } else {
      openType = paramConvertingMethod2.getOpenParameterTypes()[0];
      type = paramConvertingMethod2.getGenericParameterTypes()[0];
    } 
    Descriptor descriptor = typeDescriptor(openType, type);
    if (bool1)
      descriptor = ImmutableDescriptor.union(new Descriptor[] { descriptor, paramConvertingMethod1.getDescriptor() }); 
    if (bool2)
      descriptor = ImmutableDescriptor.union(new Descriptor[] { descriptor, paramConvertingMethod2.getDescriptor() }); 
    if (canUseOpenInfo(type)) {
      mBeanAttributeInfo = new OpenMBeanAttributeInfoSupport(paramString, str, openType, bool1, bool2, bool3, descriptor);
    } else {
      mBeanAttributeInfo = new MBeanAttributeInfo(paramString, originalTypeString(type), str, bool1, bool2, bool3, descriptor);
    } 
    return mBeanAttributeInfo;
  }
  
  MBeanOperationInfo getMBeanOperationInfo(String paramString, ConvertingMethod paramConvertingMethod) {
    MBeanOperationInfo mBeanOperationInfo;
    Method method = paramConvertingMethod.getMethod();
    String str = paramString;
    OpenType openType = paramConvertingMethod.getOpenReturnType();
    Type type = paramConvertingMethod.getGenericReturnType();
    OpenType[] arrayOfOpenType = paramConvertingMethod.getOpenParameterTypes();
    Type[] arrayOfType = paramConvertingMethod.getGenericParameterTypes();
    MBeanParameterInfo[] arrayOfMBeanParameterInfo = new MBeanParameterInfo[arrayOfOpenType.length];
    boolean bool = canUseOpenInfo(type);
    boolean bool1 = true;
    Annotation[][] arrayOfAnnotation = method.getParameterAnnotations();
    for (byte b = 0; b < arrayOfOpenType.length; b++) {
      MBeanParameterInfo mBeanParameterInfo;
      mBeanOperationInfo = "p" + b;
      String str1 = mBeanOperationInfo;
      OpenType openType1 = arrayOfOpenType[b];
      Type type1 = arrayOfType[b];
      Descriptor descriptor1 = typeDescriptor(openType1, type1);
      descriptor1 = ImmutableDescriptor.union(new Descriptor[] { descriptor1, Introspector.descriptorForAnnotations(arrayOfAnnotation[b]) });
      if (canUseOpenInfo(type1)) {
        mBeanParameterInfo = new OpenMBeanParameterInfoSupport(mBeanOperationInfo, str1, openType1, descriptor1);
      } else {
        bool1 = false;
        mBeanParameterInfo = new MBeanParameterInfo(mBeanOperationInfo, originalTypeString(type1), str1, descriptor1);
      } 
      arrayOfMBeanParameterInfo[b] = mBeanParameterInfo;
    } 
    Descriptor descriptor = typeDescriptor(openType, type);
    descriptor = ImmutableDescriptor.union(new Descriptor[] { descriptor, Introspector.descriptorForElement(method) });
    if (bool && bool1) {
      OpenMBeanParameterInfo[] arrayOfOpenMBeanParameterInfo = new OpenMBeanParameterInfo[arrayOfMBeanParameterInfo.length];
      System.arraycopy(arrayOfMBeanParameterInfo, 0, arrayOfOpenMBeanParameterInfo, 0, arrayOfMBeanParameterInfo.length);
      mBeanOperationInfo = new OpenMBeanOperationInfoSupport(paramString, str, arrayOfOpenMBeanParameterInfo, openType, 3, descriptor);
    } else {
      mBeanOperationInfo = new MBeanOperationInfo(paramString, str, arrayOfMBeanParameterInfo, bool ? openType.getClassName() : originalTypeString(type), 3, descriptor);
    } 
    return mBeanOperationInfo;
  }
  
  Descriptor getBasicMBeanDescriptor() { return new ImmutableDescriptor(new String[] { "mxbean=true", "immutableInfo=true" }); }
  
  Descriptor getMBeanDescriptor(Class<?> paramClass) { return ImmutableDescriptor.EMPTY_DESCRIPTOR; }
  
  private static Descriptor typeDescriptor(OpenType<?> paramOpenType, Type paramType) { return new ImmutableDescriptor(new String[] { "openType", "originalType" }, new Object[] { paramOpenType, originalTypeString(paramType) }); }
  
  private static boolean canUseOpenInfo(Type paramType) { return (paramType instanceof GenericArrayType) ? canUseOpenInfo(((GenericArrayType)paramType).getGenericComponentType()) : ((paramType instanceof Class && ((Class)paramType).isArray()) ? canUseOpenInfo(((Class)paramType).getComponentType()) : ((!(paramType instanceof Class) || !((Class)paramType).isPrimitive()) ? 1 : 0)); }
  
  private static String originalTypeString(Type paramType) { return (paramType instanceof Class) ? ((Class)paramType).getName() : typeName(paramType); }
  
  static String typeName(Type paramType) {
    if (paramType instanceof Class) {
      Class clazz = (Class)paramType;
      return clazz.isArray() ? (typeName(clazz.getComponentType()) + "[]") : clazz.getName();
    } 
    if (paramType instanceof GenericArrayType) {
      GenericArrayType genericArrayType = (GenericArrayType)paramType;
      return typeName(genericArrayType.getGenericComponentType()) + "[]";
    } 
    if (paramType instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType)paramType;
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(typeName(parameterizedType.getRawType())).append("<");
      String str = "";
      for (Type type : parameterizedType.getActualTypeArguments()) {
        stringBuilder.append(str).append(typeName(type));
        str = ", ";
      } 
      return stringBuilder.append(">").toString();
    } 
    return "???";
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\mbeanserver\MXBeanIntrospector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */