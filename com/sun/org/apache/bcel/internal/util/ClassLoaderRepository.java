package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.classfile.ClassParser;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class ClassLoaderRepository implements Repository {
  private java.lang.ClassLoader loader;
  
  private HashMap loadedClasses = new HashMap();
  
  public ClassLoaderRepository(java.lang.ClassLoader paramClassLoader) { this.loader = paramClassLoader; }
  
  public void storeClass(JavaClass paramJavaClass) {
    this.loadedClasses.put(paramJavaClass.getClassName(), paramJavaClass);
    paramJavaClass.setRepository(this);
  }
  
  public void removeClass(JavaClass paramJavaClass) { this.loadedClasses.remove(paramJavaClass.getClassName()); }
  
  public JavaClass findClass(String paramString) { return this.loadedClasses.containsKey(paramString) ? (JavaClass)this.loadedClasses.get(paramString) : null; }
  
  public JavaClass loadClass(String paramString) {
    String str = paramString.replace('.', '/');
    JavaClass javaClass = findClass(paramString);
    if (javaClass != null)
      return javaClass; 
    try {
      InputStream inputStream = this.loader.getResourceAsStream(str + ".class");
      if (inputStream == null)
        throw new ClassNotFoundException(paramString + " not found."); 
      ClassParser classParser = new ClassParser(inputStream, paramString);
      javaClass = classParser.parse();
      storeClass(javaClass);
      return javaClass;
    } catch (IOException iOException) {
      throw new ClassNotFoundException(iOException.toString());
    } 
  }
  
  public JavaClass loadClass(Class paramClass) throws ClassNotFoundException { return loadClass(paramClass.getName()); }
  
  public void clear() { this.loadedClasses.clear(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\interna\\util\ClassLoaderRepository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */