package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.Util;
import com.sun.xml.internal.bind.v2.bytecode.ClassTailor;
import com.sun.xml.internal.bind.v2.model.core.TypeInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeClassInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.TransducedAccessor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class OptimizedTransducedAccessorFactory {
  private static final Logger logger = Util.getClassLogger();
  
  private static final String fieldTemplateName;
  
  private static final String methodTemplateName;
  
  private static final Map<Class, String> suffixMap;
  
  public static final TransducedAccessor get(RuntimePropertyInfo paramRuntimePropertyInfo) {
    Accessor accessor = paramRuntimePropertyInfo.getAccessor();
    Class clazz1 = null;
    TypeInfo typeInfo = paramRuntimePropertyInfo.parent();
    if (!(typeInfo instanceof RuntimeClassInfo))
      return null; 
    Class clazz2 = (Class)((RuntimeClassInfo)typeInfo).getClazz();
    String str = ClassTailor.toVMClassName(clazz2) + "_JaxbXducedAccessor_" + paramRuntimePropertyInfo.getName();
    if (accessor instanceof Accessor.FieldReflection) {
      Accessor.FieldReflection fieldReflection = (Accessor.FieldReflection)accessor;
      Field field = fieldReflection.f;
      int i = field.getModifiers();
      if (Modifier.isPrivate(i) || Modifier.isFinal(i))
        return null; 
      Class clazz = field.getType();
      if (clazz.isPrimitive())
        clazz1 = AccessorInjector.prepare(clazz2, fieldTemplateName + (String)suffixMap.get(clazz), str, new String[] { ClassTailor.toVMClassName(Bean.class), ClassTailor.toVMClassName(clazz2), "f_" + clazz.getName(), field.getName() }); 
    } 
    if (accessor.getClass() == Accessor.GetterSetterReflection.class) {
      Accessor.GetterSetterReflection getterSetterReflection = (Accessor.GetterSetterReflection)accessor;
      if (getterSetterReflection.getter == null || getterSetterReflection.setter == null)
        return null; 
      Class clazz = getterSetterReflection.getter.getReturnType();
      if (Modifier.isPrivate(getterSetterReflection.getter.getModifiers()) || Modifier.isPrivate(getterSetterReflection.setter.getModifiers()))
        return null; 
      if (clazz.isPrimitive())
        clazz1 = AccessorInjector.prepare(clazz2, methodTemplateName + (String)suffixMap.get(clazz), str, new String[] { ClassTailor.toVMClassName(Bean.class), ClassTailor.toVMClassName(clazz2), "get_" + clazz.getName(), getterSetterReflection.getter.getName(), "set_" + clazz.getName(), getterSetterReflection.setter.getName() }); 
    } 
    if (clazz1 == null)
      return null; 
    logger.log(Level.FINE, "Using optimized TransducedAccessor for " + paramRuntimePropertyInfo.displayName());
    try {
      return (TransducedAccessor)clazz1.newInstance();
    } catch (InstantiationException instantiationException) {
      logger.log(Level.INFO, "failed to load an optimized TransducedAccessor", instantiationException);
    } catch (IllegalAccessException illegalAccessException) {
      logger.log(Level.INFO, "failed to load an optimized TransducedAccessor", illegalAccessException);
    } catch (SecurityException securityException) {
      logger.log(Level.INFO, "failed to load an optimized TransducedAccessor", securityException);
    } 
    return null;
  }
  
  static  {
    String str = TransducedAccessor_field_Byte.class.getName();
    fieldTemplateName = str.substring(0, str.length() - "Byte".length()).replace('.', '/');
    str = TransducedAccessor_method_Byte.class.getName();
    methodTemplateName = str.substring(0, str.length() - "Byte".length()).replace('.', '/');
    suffixMap = new HashMap();
    suffixMap.put(byte.class, "Byte");
    suffixMap.put(short.class, "Short");
    suffixMap.put(int.class, "Integer");
    suffixMap.put(long.class, "Long");
    suffixMap.put(boolean.class, "Boolean");
    suffixMap.put(float.class, "Float");
    suffixMap.put(double.class, "Double");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\opt\OptimizedTransducedAccessorFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */