package com.sun.org.apache.bcel.internal;

import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import com.sun.org.apache.bcel.internal.util.ClassPath;
import com.sun.org.apache.bcel.internal.util.Repository;
import com.sun.org.apache.bcel.internal.util.SyntheticRepository;
import java.io.IOException;

public abstract class Repository {
  private static Repository _repository = SyntheticRepository.getInstance();
  
  public static Repository getRepository() { return _repository; }
  
  public static void setRepository(Repository paramRepository) { _repository = paramRepository; }
  
  public static JavaClass lookupClass(String paramString) {
    try {
      JavaClass javaClass = _repository.findClass(paramString);
      return (javaClass == null) ? _repository.loadClass(paramString) : javaClass;
    } catch (ClassNotFoundException classNotFoundException) {
      return null;
    } 
  }
  
  public static JavaClass lookupClass(Class paramClass) {
    try {
      return _repository.loadClass(paramClass);
    } catch (ClassNotFoundException classNotFoundException) {
      return null;
    } 
  }
  
  public static ClassPath.ClassFile lookupClassFile(String paramString) {
    try {
      return ClassPath.SYSTEM_CLASS_PATH.getClassFile(paramString);
    } catch (IOException iOException) {
      return null;
    } 
  }
  
  public static void clearCache() { _repository.clear(); }
  
  public static JavaClass addClass(JavaClass paramJavaClass) {
    JavaClass javaClass = _repository.findClass(paramJavaClass.getClassName());
    _repository.storeClass(paramJavaClass);
    return javaClass;
  }
  
  public static void removeClass(String paramString) { _repository.removeClass(_repository.findClass(paramString)); }
  
  public static void removeClass(JavaClass paramJavaClass) { _repository.removeClass(paramJavaClass); }
  
  public static JavaClass[] getSuperClasses(JavaClass paramJavaClass) { return paramJavaClass.getSuperClasses(); }
  
  public static JavaClass[] getSuperClasses(String paramString) {
    JavaClass javaClass = lookupClass(paramString);
    return (javaClass == null) ? null : getSuperClasses(javaClass);
  }
  
  public static JavaClass[] getInterfaces(JavaClass paramJavaClass) { return paramJavaClass.getAllInterfaces(); }
  
  public static JavaClass[] getInterfaces(String paramString) { return getInterfaces(lookupClass(paramString)); }
  
  public static boolean instanceOf(JavaClass paramJavaClass1, JavaClass paramJavaClass2) { return paramJavaClass1.instanceOf(paramJavaClass2); }
  
  public static boolean instanceOf(String paramString1, String paramString2) { return instanceOf(lookupClass(paramString1), lookupClass(paramString2)); }
  
  public static boolean instanceOf(JavaClass paramJavaClass, String paramString) { return instanceOf(paramJavaClass, lookupClass(paramString)); }
  
  public static boolean instanceOf(String paramString, JavaClass paramJavaClass) { return instanceOf(lookupClass(paramString), paramJavaClass); }
  
  public static boolean implementationOf(JavaClass paramJavaClass1, JavaClass paramJavaClass2) { return paramJavaClass1.implementationOf(paramJavaClass2); }
  
  public static boolean implementationOf(String paramString1, String paramString2) { return implementationOf(lookupClass(paramString1), lookupClass(paramString2)); }
  
  public static boolean implementationOf(JavaClass paramJavaClass, String paramString) { return implementationOf(paramJavaClass, lookupClass(paramString)); }
  
  public static boolean implementationOf(String paramString, JavaClass paramJavaClass) { return implementationOf(lookupClass(paramString), paramJavaClass); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\Repository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */