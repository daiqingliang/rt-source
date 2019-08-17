package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.Util;
import com.sun.xml.internal.bind.v2.bytecode.ClassTailor;
import com.sun.xml.internal.bind.v2.runtime.RuntimeUtil;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class OptimizedAccessorFactory {
  private static final Logger logger = Util.getClassLogger();
  
  private static final String fieldTemplateName;
  
  private static final String methodTemplateName;
  
  public static final <B, V> Accessor<B, V> get(Method paramMethod1, Method paramMethod2) {
    Class clazz2;
    if (paramMethod1.getParameterTypes().length != 0)
      return null; 
    Class[] arrayOfClass = paramMethod2.getParameterTypes();
    if (arrayOfClass.length != 1)
      return null; 
    if (arrayOfClass[false] != paramMethod1.getReturnType())
      return null; 
    if (paramMethod2.getReturnType() != void.class)
      return null; 
    if (paramMethod1.getDeclaringClass() != paramMethod2.getDeclaringClass())
      return null; 
    if (Modifier.isPrivate(paramMethod1.getModifiers()) || Modifier.isPrivate(paramMethod2.getModifiers()))
      return null; 
    Class clazz1 = arrayOfClass[0];
    String str1 = clazz1.getName().replace('.', '_');
    if (clazz1.isArray()) {
      str1 = "AOf_";
      String str = clazz1.getComponentType().getName().replace('.', '_');
      while (str.startsWith("[L")) {
        str = str.substring(2);
        str1 = str1 + "AOf_";
      } 
      str1 = str1 + str;
    } 
    String str2 = ClassTailor.toVMClassName(paramMethod1.getDeclaringClass()) + "$JaxbAccessorM_" + paramMethod1.getName() + '_' + paramMethod2.getName() + '_' + str1;
    if (clazz1.isPrimitive()) {
      clazz2 = AccessorInjector.prepare(paramMethod1.getDeclaringClass(), methodTemplateName + ((Class)RuntimeUtil.primitiveToBox.get(clazz1)).getSimpleName(), str2, new String[] { ClassTailor.toVMClassName(Bean.class), ClassTailor.toVMClassName(paramMethod1.getDeclaringClass()), "get_" + clazz1.getName(), paramMethod1.getName(), "set_" + clazz1.getName(), paramMethod2.getName() });
    } else {
      clazz2 = AccessorInjector.prepare(paramMethod1.getDeclaringClass(), methodTemplateName + "Ref", str2, new String[] { 
            ClassTailor.toVMClassName(Bean.class), ClassTailor.toVMClassName(paramMethod1.getDeclaringClass()), ClassTailor.toVMClassName(Ref.class), ClassTailor.toVMClassName(clazz1), "()" + ClassTailor.toVMTypeName(Ref.class), "()" + ClassTailor.toVMTypeName(clazz1), '(' + ClassTailor.toVMTypeName(Ref.class) + ")V", '(' + ClassTailor.toVMTypeName(clazz1) + ")V", "get_ref", paramMethod1.getName(), 
            "set_ref", paramMethod2.getName() });
    } 
    if (clazz2 == null)
      return null; 
    Accessor accessor = instanciate(clazz2);
    if (accessor != null && logger.isLoggable(Level.FINE))
      logger.log(Level.FINE, "Using optimized Accessor for {0} and {1}", new Object[] { paramMethod1, paramMethod2 }); 
    return accessor;
  }
  
  public static final <B, V> Accessor<B, V> get(Field paramField) {
    Class clazz;
    int i = paramField.getModifiers();
    if (Modifier.isPrivate(i) || Modifier.isFinal(i))
      return null; 
    String str = ClassTailor.toVMClassName(paramField.getDeclaringClass()) + "$JaxbAccessorF_" + paramField.getName();
    if (paramField.getType().isPrimitive()) {
      clazz = AccessorInjector.prepare(paramField.getDeclaringClass(), fieldTemplateName + ((Class)RuntimeUtil.primitiveToBox.get(paramField.getType())).getSimpleName(), str, new String[] { ClassTailor.toVMClassName(Bean.class), ClassTailor.toVMClassName(paramField.getDeclaringClass()), "f_" + paramField.getType().getName(), paramField.getName() });
    } else {
      clazz = AccessorInjector.prepare(paramField.getDeclaringClass(), fieldTemplateName + "Ref", str, new String[] { ClassTailor.toVMClassName(Bean.class), ClassTailor.toVMClassName(paramField.getDeclaringClass()), ClassTailor.toVMClassName(Ref.class), ClassTailor.toVMClassName(paramField.getType()), ClassTailor.toVMTypeName(Ref.class), ClassTailor.toVMTypeName(paramField.getType()), "f_ref", paramField.getName() });
    } 
    if (clazz == null)
      return null; 
    Accessor accessor = instanciate(clazz);
    if (accessor != null && logger.isLoggable(Level.FINE))
      logger.log(Level.FINE, "Using optimized Accessor for {0}", paramField); 
    return accessor;
  }
  
  private static <B, V> Accessor<B, V> instanciate(Class paramClass) {
    try {
      return (Accessor)paramClass.newInstance();
    } catch (InstantiationException instantiationException) {
      logger.log(Level.INFO, "failed to load an optimized Accessor", instantiationException);
    } catch (IllegalAccessException illegalAccessException) {
      logger.log(Level.INFO, "failed to load an optimized Accessor", illegalAccessException);
    } catch (SecurityException securityException) {
      logger.log(Level.INFO, "failed to load an optimized Accessor", securityException);
    } 
    return null;
  }
  
  static  {
    String str = FieldAccessor_Byte.class.getName();
    fieldTemplateName = str.substring(0, str.length() - "Byte".length()).replace('.', '/');
    str = MethodAccessor_Byte.class.getName();
    methodTemplateName = str.substring(0, str.length() - "Byte".length()).replace('.', '/');
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\opt\OptimizedAccessorFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */