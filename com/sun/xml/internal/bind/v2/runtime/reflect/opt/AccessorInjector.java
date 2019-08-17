package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.Util;
import com.sun.xml.internal.bind.v2.bytecode.ClassTailor;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

class AccessorInjector {
  private static final Logger logger = Util.getClassLogger();
  
  protected static final boolean noOptimize = (Util.getSystemProperty(ClassTailor.class.getName() + ".noOptimize") != null);
  
  private static final ClassLoader CLASS_LOADER;
  
  public static Class<?> prepare(Class paramClass, String paramString1, String paramString2, String... paramVarArgs) {
    if (noOptimize)
      return null; 
    try {
      ClassLoader classLoader = SecureLoader.getClassClassLoader(paramClass);
      if (classLoader == null)
        return null; 
      Class clazz = Injector.find(classLoader, paramString2);
      if (clazz == null) {
        byte[] arrayOfByte = tailor(paramString1, paramString2, paramVarArgs);
        if (arrayOfByte == null)
          return null; 
        clazz = Injector.inject(classLoader, paramString2, arrayOfByte);
        if (clazz == null)
          Injector.find(classLoader, paramString2); 
      } 
      return clazz;
    } catch (SecurityException securityException) {
      logger.log(Level.INFO, "Unable to create an optimized TransducedAccessor ", securityException);
      return null;
    } 
  }
  
  private static byte[] tailor(String paramString1, String paramString2, String... paramVarArgs) {
    InputStream inputStream;
    if (CLASS_LOADER != null) {
      inputStream = CLASS_LOADER.getResourceAsStream(paramString1 + ".class");
    } else {
      inputStream = ClassLoader.getSystemResourceAsStream(paramString1 + ".class");
    } 
    return (inputStream == null) ? null : ClassTailor.tailor(inputStream, paramString1, paramString2, paramVarArgs);
  }
  
  static  {
    if (noOptimize)
      logger.info("The optimized code generation is disabled"); 
    CLASS_LOADER = SecureLoader.getClassClassLoader(AccessorInjector.class);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\opt\AccessorInjector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */