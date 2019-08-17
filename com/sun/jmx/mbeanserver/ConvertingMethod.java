package com.sun.jmx.mbeanserver;

import java.io.InvalidObjectException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import javax.management.Descriptor;
import javax.management.MBeanException;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import sun.reflect.misc.MethodUtil;

final class ConvertingMethod {
  private static final String[] noStrings = new String[0];
  
  private final Method method;
  
  private final MXBeanMapping returnMapping;
  
  private final MXBeanMapping[] paramMappings;
  
  private final boolean paramConversionIsIdentity;
  
  static ConvertingMethod from(Method paramMethod) {
    try {
      return new ConvertingMethod(paramMethod);
    } catch (OpenDataException openDataException) {
      String str = "Method " + paramMethod.getDeclaringClass().getName() + "." + paramMethod.getName() + " has parameter or return type that cannot be translated into an open type";
      throw new IllegalArgumentException(str, openDataException);
    } 
  }
  
  Method getMethod() { return this.method; }
  
  Descriptor getDescriptor() { return Introspector.descriptorForElement(this.method); }
  
  Type getGenericReturnType() { return this.method.getGenericReturnType(); }
  
  Type[] getGenericParameterTypes() { return this.method.getGenericParameterTypes(); }
  
  String getName() { return this.method.getName(); }
  
  OpenType<?> getOpenReturnType() { return this.returnMapping.getOpenType(); }
  
  OpenType<?>[] getOpenParameterTypes() {
    OpenType[] arrayOfOpenType = new OpenType[this.paramMappings.length];
    for (byte b = 0; b < this.paramMappings.length; b++)
      arrayOfOpenType[b] = this.paramMappings[b].getOpenType(); 
    return arrayOfOpenType;
  }
  
  void checkCallFromOpen() {
    try {
      for (MXBeanMapping mXBeanMapping : this.paramMappings)
        mXBeanMapping.checkReconstructible(); 
    } catch (InvalidObjectException invalidObjectException) {
      throw new IllegalArgumentException(invalidObjectException);
    } 
  }
  
  void checkCallToOpen() {
    try {
      this.returnMapping.checkReconstructible();
    } catch (InvalidObjectException invalidObjectException) {
      throw new IllegalArgumentException(invalidObjectException);
    } 
  }
  
  String[] getOpenSignature() {
    if (this.paramMappings.length == 0)
      return noStrings; 
    String[] arrayOfString = new String[this.paramMappings.length];
    for (byte b = 0; b < this.paramMappings.length; b++)
      arrayOfString[b] = this.paramMappings[b].getOpenClass().getName(); 
    return arrayOfString;
  }
  
  final Object toOpenReturnValue(MXBeanLookup paramMXBeanLookup, Object paramObject) throws OpenDataException { return this.returnMapping.toOpenValue(paramObject); }
  
  final Object fromOpenReturnValue(MXBeanLookup paramMXBeanLookup, Object paramObject) throws OpenDataException { return this.returnMapping.fromOpenValue(paramObject); }
  
  final Object[] toOpenParameters(MXBeanLookup paramMXBeanLookup, Object[] paramArrayOfObject) throws OpenDataException {
    if (this.paramConversionIsIdentity || paramArrayOfObject == null)
      return paramArrayOfObject; 
    Object[] arrayOfObject = new Object[paramArrayOfObject.length];
    for (byte b = 0; b < paramArrayOfObject.length; b++)
      arrayOfObject[b] = this.paramMappings[b].toOpenValue(paramArrayOfObject[b]); 
    return arrayOfObject;
  }
  
  final Object[] fromOpenParameters(Object[] paramArrayOfObject) throws InvalidObjectException {
    if (this.paramConversionIsIdentity || paramArrayOfObject == null)
      return paramArrayOfObject; 
    Object[] arrayOfObject = new Object[paramArrayOfObject.length];
    for (byte b = 0; b < paramArrayOfObject.length; b++)
      arrayOfObject[b] = this.paramMappings[b].fromOpenValue(paramArrayOfObject[b]); 
    return arrayOfObject;
  }
  
  final Object toOpenParameter(MXBeanLookup paramMXBeanLookup, Object paramObject, int paramInt) throws OpenDataException { return this.paramMappings[paramInt].toOpenValue(paramObject); }
  
  final Object fromOpenParameter(MXBeanLookup paramMXBeanLookup, Object paramObject, int paramInt) throws OpenDataException { return this.paramMappings[paramInt].fromOpenValue(paramObject); }
  
  Object invokeWithOpenReturn(MXBeanLookup paramMXBeanLookup, Object paramObject, Object[] paramArrayOfObject) throws MBeanException, IllegalAccessException, InvocationTargetException {
    mXBeanLookup = MXBeanLookup.getLookup();
    try {
      MXBeanLookup.setLookup(paramMXBeanLookup);
      return invokeWithOpenReturn(paramObject, paramArrayOfObject);
    } finally {
      MXBeanLookup.setLookup(mXBeanLookup);
    } 
  }
  
  private Object invokeWithOpenReturn(Object paramObject, Object[] paramArrayOfObject) throws MBeanException, IllegalAccessException, InvocationTargetException {
    Object[] arrayOfObject;
    try {
      arrayOfObject = fromOpenParameters(paramArrayOfObject);
    } catch (InvalidObjectException invalidObjectException) {
      String str = methodName() + ": cannot convert parameters from open values: " + invalidObjectException;
      throw new MBeanException(invalidObjectException, str);
    } 
    Object object = MethodUtil.invoke(this.method, paramObject, arrayOfObject);
    try {
      return this.returnMapping.toOpenValue(object);
    } catch (OpenDataException openDataException) {
      String str = methodName() + ": cannot convert return value to open value: " + openDataException;
      throw new MBeanException(openDataException, str);
    } 
  }
  
  private String methodName() { return this.method.getDeclaringClass() + "." + this.method.getName(); }
  
  private ConvertingMethod(Method paramMethod) throws OpenDataException {
    this.method = paramMethod;
    MXBeanMappingFactory mXBeanMappingFactory = MXBeanMappingFactory.DEFAULT;
    this.returnMapping = mXBeanMappingFactory.mappingForType(paramMethod.getGenericReturnType(), mXBeanMappingFactory);
    Type[] arrayOfType = paramMethod.getGenericParameterTypes();
    this.paramMappings = new MXBeanMapping[arrayOfType.length];
    boolean bool = true;
    for (byte b = 0; b < arrayOfType.length; b++) {
      this.paramMappings[b] = mXBeanMappingFactory.mappingForType(arrayOfType[b], mXBeanMappingFactory);
      bool &= DefaultMXBeanMappingFactory.isIdentity(this.paramMappings[b]);
    } 
    this.paramConversionIsIdentity = bool;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\mbeanserver\ConvertingMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */