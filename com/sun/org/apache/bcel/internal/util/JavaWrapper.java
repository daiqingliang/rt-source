package com.sun.org.apache.bcel.internal.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class JavaWrapper {
  private java.lang.ClassLoader loader;
  
  private static java.lang.ClassLoader getClassLoader() {
    String str = SecuritySupport.getSystemProperty("bcel.classloader");
    if (str == null || "".equals(str))
      str = "com.sun.org.apache.bcel.internal.util.ClassLoader"; 
    try {
      return (java.lang.ClassLoader)Class.forName(str).newInstance();
    } catch (Exception exception) {
      throw new RuntimeException(exception.toString());
    } 
  }
  
  public JavaWrapper(java.lang.ClassLoader paramClassLoader) { this.loader = paramClassLoader; }
  
  public JavaWrapper() { this(getClassLoader()); }
  
  public void runMain(String paramString, String[] paramArrayOfString) throws ClassNotFoundException {
    Class clazz = this.loader.loadClass(paramString);
    Method method = null;
    try {
      method = clazz.getMethod("_main", new Class[] { paramArrayOfString.getClass() });
      int i = method.getModifiers();
      Class clazz1 = method.getReturnType();
      if (!Modifier.isPublic(i) || !Modifier.isStatic(i) || Modifier.isAbstract(i) || clazz1 != void.class)
        throw new NoSuchMethodException(); 
    } catch (NoSuchMethodException noSuchMethodException) {
      System.out.println("In class " + paramString + ": public static void _main(String[] argv) is not defined");
      return;
    } 
    try {
      method.invoke(null, new Object[] { paramArrayOfString });
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  public static void _main(String[] paramArrayOfString) throws Exception {
    if (paramArrayOfString.length == 0) {
      System.out.println("Missing class name.");
      return;
    } 
    String str = paramArrayOfString[0];
    String[] arrayOfString = new String[paramArrayOfString.length - 1];
    System.arraycopy(paramArrayOfString, 1, arrayOfString, 0, arrayOfString.length);
    JavaWrapper javaWrapper = new JavaWrapper();
    javaWrapper.runMain(str, arrayOfString);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\interna\\util\JavaWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */