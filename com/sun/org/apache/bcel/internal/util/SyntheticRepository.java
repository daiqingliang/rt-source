package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.classfile.ClassParser;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class SyntheticRepository implements Repository {
  private static final String DEFAULT_PATH = ClassPath.getClassPath();
  
  private static HashMap _instances = new HashMap();
  
  private ClassPath _path = null;
  
  private HashMap _loadedClasses = new HashMap();
  
  private SyntheticRepository(ClassPath paramClassPath) { this._path = paramClassPath; }
  
  public static SyntheticRepository getInstance() { return getInstance(ClassPath.SYSTEM_CLASS_PATH); }
  
  public static SyntheticRepository getInstance(ClassPath paramClassPath) {
    SyntheticRepository syntheticRepository = (SyntheticRepository)_instances.get(paramClassPath);
    if (syntheticRepository == null) {
      syntheticRepository = new SyntheticRepository(paramClassPath);
      _instances.put(paramClassPath, syntheticRepository);
    } 
    return syntheticRepository;
  }
  
  public void storeClass(JavaClass paramJavaClass) {
    this._loadedClasses.put(paramJavaClass.getClassName(), paramJavaClass);
    paramJavaClass.setRepository(this);
  }
  
  public void removeClass(JavaClass paramJavaClass) { this._loadedClasses.remove(paramJavaClass.getClassName()); }
  
  public JavaClass findClass(String paramString) { return (JavaClass)this._loadedClasses.get(paramString); }
  
  public JavaClass loadClass(String paramString) {
    if (paramString == null || paramString.equals(""))
      throw new IllegalArgumentException("Invalid class name " + paramString); 
    paramString = paramString.replace('/', '.');
    try {
      return loadClass(this._path.getInputStream(paramString), paramString);
    } catch (IOException iOException) {
      throw new ClassNotFoundException("Exception while looking for class " + paramString + ": " + iOException.toString());
    } 
  }
  
  public JavaClass loadClass(Class paramClass) throws ClassNotFoundException {
    String str1 = paramClass.getName();
    String str2 = str1;
    int i = str2.lastIndexOf('.');
    if (i > 0)
      str2 = str2.substring(i + 1); 
    return loadClass(paramClass.getResourceAsStream(str2 + ".class"), str1);
  }
  
  private JavaClass loadClass(InputStream paramInputStream, String paramString) throws ClassNotFoundException {
    JavaClass javaClass = findClass(paramString);
    if (javaClass != null)
      return javaClass; 
    try {
      if (paramInputStream != null) {
        ClassParser classParser = new ClassParser(paramInputStream, paramString);
        javaClass = classParser.parse();
        storeClass(javaClass);
        return javaClass;
      } 
    } catch (IOException iOException) {
      throw new ClassNotFoundException("Exception while looking for class " + paramString + ": " + iOException.toString());
    } 
    throw new ClassNotFoundException("SyntheticRepository could not load " + paramString);
  }
  
  public void clear() { this._loadedClasses.clear(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\interna\\util\SyntheticRepository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */