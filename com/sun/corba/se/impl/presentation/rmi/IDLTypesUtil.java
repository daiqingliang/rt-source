package com.sun.corba.se.impl.presentation.rmi;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashSet;

public final class IDLTypesUtil {
  private static final String GET_PROPERTY_PREFIX = "get";
  
  private static final String SET_PROPERTY_PREFIX = "set";
  
  private static final String IS_PROPERTY_PREFIX = "is";
  
  public static final int VALID_TYPE = 0;
  
  public static final int INVALID_TYPE = 1;
  
  public static final boolean FOLLOW_RMIC = true;
  
  public void validateRemoteInterface(Class paramClass) throws IDLTypeException {
    if (paramClass == null)
      throw new IllegalArgumentException(); 
    if (!paramClass.isInterface()) {
      String str = "Class " + paramClass + " must be a java interface.";
      throw new IDLTypeException(str);
    } 
    if (!java.rmi.Remote.class.isAssignableFrom(paramClass)) {
      String str = "Class " + paramClass + " must extend java.rmi.Remote, either directly or indirectly.";
      throw new IDLTypeException(str);
    } 
    Method[] arrayOfMethod = paramClass.getMethods();
    for (byte b = 0; b < arrayOfMethod.length; b++) {
      Method method = arrayOfMethod[b];
      validateExceptions(method);
    } 
    validateConstants(paramClass);
  }
  
  public boolean isRemoteInterface(Class paramClass) {
    boolean bool = true;
    try {
      validateRemoteInterface(paramClass);
    } catch (IDLTypeException iDLTypeException) {
      bool = false;
    } 
    return bool;
  }
  
  public boolean isPrimitive(Class paramClass) {
    if (paramClass == null)
      throw new IllegalArgumentException(); 
    return paramClass.isPrimitive();
  }
  
  public boolean isValue(Class paramClass) {
    if (paramClass == null)
      throw new IllegalArgumentException(); 
    return (!paramClass.isInterface() && java.io.Serializable.class.isAssignableFrom(paramClass) && !java.rmi.Remote.class.isAssignableFrom(paramClass));
  }
  
  public boolean isArray(Class paramClass) {
    boolean bool = false;
    if (paramClass == null)
      throw new IllegalArgumentException(); 
    if (paramClass.isArray()) {
      Class clazz = paramClass.getComponentType();
      bool = (isPrimitive(clazz) || isRemoteInterface(clazz) || isEntity(clazz) || isException(clazz) || isValue(clazz) || isObjectReference(clazz));
    } 
    return bool;
  }
  
  public boolean isException(Class paramClass) {
    if (paramClass == null)
      throw new IllegalArgumentException(); 
    return (isCheckedException(paramClass) && !isRemoteException(paramClass) && isValue(paramClass));
  }
  
  public boolean isRemoteException(Class paramClass) {
    if (paramClass == null)
      throw new IllegalArgumentException(); 
    return java.rmi.RemoteException.class.isAssignableFrom(paramClass);
  }
  
  public boolean isCheckedException(Class paramClass) {
    if (paramClass == null)
      throw new IllegalArgumentException(); 
    return (Throwable.class.isAssignableFrom(paramClass) && !RuntimeException.class.isAssignableFrom(paramClass) && !Error.class.isAssignableFrom(paramClass));
  }
  
  public boolean isObjectReference(Class paramClass) {
    if (paramClass == null)
      throw new IllegalArgumentException(); 
    return (paramClass.isInterface() && org.omg.CORBA.Object.class.isAssignableFrom(paramClass));
  }
  
  public boolean isEntity(Class paramClass) {
    if (paramClass == null)
      throw new IllegalArgumentException(); 
    Class clazz = paramClass.getSuperclass();
    return (!paramClass.isInterface() && clazz != null && org.omg.CORBA.portable.IDLEntity.class.isAssignableFrom(paramClass));
  }
  
  public boolean isPropertyAccessorMethod(Method paramMethod, Class paramClass) {
    String str1 = paramMethod.getName();
    Class clazz = paramMethod.getReturnType();
    Class[] arrayOfClass1 = paramMethod.getParameterTypes();
    Class[] arrayOfClass2 = paramMethod.getExceptionTypes();
    String str2 = null;
    if (str1.startsWith("get")) {
      if (arrayOfClass1.length == 0 && clazz != void.class && !readHasCorrespondingIsProperty(paramMethod, paramClass))
        str2 = "get"; 
    } else if (str1.startsWith("set")) {
      if (clazz == void.class && arrayOfClass1.length == 1 && (hasCorrespondingReadProperty(paramMethod, paramClass, "get") || hasCorrespondingReadProperty(paramMethod, paramClass, "is")))
        str2 = "set"; 
    } else if (str1.startsWith("is") && arrayOfClass1.length == 0 && clazz == boolean.class && !isHasCorrespondingReadProperty(paramMethod, paramClass)) {
      str2 = "is";
    } 
    if (str2 != null && (!validPropertyExceptions(paramMethod) || str1.length() <= str2.length()))
      str2 = null; 
    return (str2 != null);
  }
  
  private boolean hasCorrespondingReadProperty(Method paramMethod, Class paramClass, String paramString) {
    String str = paramMethod.getName();
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    boolean bool = false;
    try {
      String str1 = str.replaceFirst("set", paramString);
      Method method = paramClass.getMethod(str1, new Class[0]);
      bool = (isPropertyAccessorMethod(method, paramClass) && method.getReturnType() == arrayOfClass[false]);
    } catch (Exception exception) {}
    return bool;
  }
  
  private boolean readHasCorrespondingIsProperty(Method paramMethod, Class paramClass) { return false; }
  
  private boolean isHasCorrespondingReadProperty(Method paramMethod, Class paramClass) {
    String str = paramMethod.getName();
    boolean bool = false;
    try {
      String str1 = str.replaceFirst("is", "get");
      Method method = paramClass.getMethod(str1, new Class[0]);
      bool = isPropertyAccessorMethod(method, paramClass);
    } catch (Exception exception) {}
    return bool;
  }
  
  public String getAttributeNameForProperty(String paramString) {
    String str1 = null;
    String str2 = null;
    if (paramString.startsWith("get")) {
      str2 = "get";
    } else if (paramString.startsWith("set")) {
      str2 = "set";
    } else if (paramString.startsWith("is")) {
      str2 = "is";
    } 
    if (str2 != null && str2.length() < paramString.length()) {
      String str = paramString.substring(str2.length());
      if (str.length() >= 2 && Character.isUpperCase(str.charAt(0)) && Character.isUpperCase(str.charAt(1))) {
        str1 = str;
      } else {
        str1 = Character.toLowerCase(str.charAt(0)) + str.substring(1);
      } 
    } 
    return str1;
  }
  
  public IDLType getPrimitiveIDLTypeMapping(Class paramClass) {
    if (paramClass == null)
      throw new IllegalArgumentException(); 
    if (paramClass.isPrimitive()) {
      if (paramClass == void.class)
        return new IDLType(paramClass, "void"); 
      if (paramClass == boolean.class)
        return new IDLType(paramClass, "boolean"); 
      if (paramClass == char.class)
        return new IDLType(paramClass, "wchar"); 
      if (paramClass == byte.class)
        return new IDLType(paramClass, "octet"); 
      if (paramClass == short.class)
        return new IDLType(paramClass, "short"); 
      if (paramClass == int.class)
        return new IDLType(paramClass, "long"); 
      if (paramClass == long.class)
        return new IDLType(paramClass, "long_long"); 
      if (paramClass == float.class)
        return new IDLType(paramClass, "float"); 
      if (paramClass == double.class)
        return new IDLType(paramClass, "double"); 
    } 
    return null;
  }
  
  public IDLType getSpecialCaseIDLTypeMapping(Class paramClass) {
    if (paramClass == null)
      throw new IllegalArgumentException(); 
    return (paramClass == Object.class) ? new IDLType(paramClass, new String[] { "java", "lang" }, "Object") : ((paramClass == String.class) ? new IDLType(paramClass, new String[] { "CORBA" }, "WStringValue") : ((paramClass == Class.class) ? new IDLType(paramClass, new String[] { "javax", "rmi", "CORBA" }, "ClassDesc") : ((paramClass == java.io.Serializable.class) ? new IDLType(paramClass, new String[] { "java", "io" }, "Serializable") : ((paramClass == java.io.Externalizable.class) ? new IDLType(paramClass, new String[] { "java", "io" }, "Externalizable") : ((paramClass == java.rmi.Remote.class) ? new IDLType(paramClass, new String[] { "java", "rmi" }, "Remote") : ((paramClass == org.omg.CORBA.Object.class) ? new IDLType(paramClass, "Object") : null))))));
  }
  
  private void validateExceptions(Method paramMethod) throws IDLTypeException {
    Class[] arrayOfClass = paramMethod.getExceptionTypes();
    boolean bool = false;
    byte b;
    for (b = 0; b < arrayOfClass.length; b++) {
      Class clazz = arrayOfClass[b];
      if (isRemoteExceptionOrSuperClass(clazz)) {
        bool = true;
        break;
      } 
    } 
    if (!bool) {
      String str = "Method '" + paramMethod + "' must throw at least one exception of type java.rmi.RemoteException or one of its super-classes";
      throw new IDLTypeException(str);
    } 
    for (b = 0; b < arrayOfClass.length; b++) {
      Class clazz = arrayOfClass[b];
      if (isCheckedException(clazz) && !isValue(clazz) && !isRemoteException(clazz)) {
        String str = "Exception '" + clazz + "' on method '" + paramMethod + "' is not a allowed RMI/IIOP exception type";
        throw new IDLTypeException(str);
      } 
    } 
  }
  
  private boolean validPropertyExceptions(Method paramMethod) {
    Class[] arrayOfClass = paramMethod.getExceptionTypes();
    for (byte b = 0; b < arrayOfClass.length; b++) {
      Class clazz = arrayOfClass[b];
      if (isCheckedException(clazz) && !isRemoteException(clazz))
        return false; 
    } 
    return true;
  }
  
  private boolean isRemoteExceptionOrSuperClass(Class paramClass) { return (paramClass == java.rmi.RemoteException.class || paramClass == java.io.IOException.class || paramClass == Exception.class || paramClass == Throwable.class); }
  
  private void validateDirectInterfaces(Class paramClass) throws IDLTypeException {
    Class[] arrayOfClass = paramClass.getInterfaces();
    if (arrayOfClass.length < 2)
      return; 
    HashSet hashSet1 = new HashSet();
    HashSet hashSet2 = new HashSet();
    for (byte b = 0; b < arrayOfClass.length; b++) {
      Class clazz = arrayOfClass[b];
      Method[] arrayOfMethod = clazz.getMethods();
      hashSet2.clear();
      for (byte b1 = 0; b1 < arrayOfMethod.length; b1++)
        hashSet2.add(arrayOfMethod[b1].getName()); 
      for (String str : hashSet2) {
        if (hashSet1.contains(str)) {
          String str1 = "Class " + paramClass + " inherits method " + str + " from multiple direct interfaces.";
          throw new IDLTypeException(str1);
        } 
        hashSet1.add(str);
      } 
    } 
  }
  
  private void validateConstants(final Class c) throws IDLTypeException {
    Field[] arrayOfField = null;
    try {
      arrayOfField = (Field[])AccessController.doPrivileged(new PrivilegedExceptionAction() {
            public Object run() throws Exception { return c.getFields(); }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      IDLTypeException iDLTypeException = new IDLTypeException();
      iDLTypeException.initCause(privilegedActionException);
      throw iDLTypeException;
    } 
    for (byte b = 0; b < arrayOfField.length; b++) {
      Field field = arrayOfField[b];
      Class clazz = field.getType();
      if (clazz != String.class && !isPrimitive(clazz)) {
        String str = "Constant field '" + field.getName() + "' in class '" + field.getDeclaringClass().getName() + "' has invalid type' " + field.getType() + "'. Constants in RMI/IIOP interfaces can only have primitive types and java.lang.String types.";
        throw new IDLTypeException(str);
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\presentation\rmi\IDLTypesUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */