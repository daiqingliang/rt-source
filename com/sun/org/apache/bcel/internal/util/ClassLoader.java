package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.classfile.ClassParser;
import com.sun.org.apache.bcel.internal.classfile.ConstantClass;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import com.sun.org.apache.bcel.internal.classfile.ConstantUtf8;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import com.sun.org.apache.bcel.internal.classfile.Utility;
import java.io.ByteArrayInputStream;
import java.util.Hashtable;

public class ClassLoader extends java.lang.ClassLoader {
  private Hashtable classes = new Hashtable();
  
  private String[] ignored_packages = { "java.", "javax.", "sun." };
  
  private Repository repository = SyntheticRepository.getInstance();
  
  private java.lang.ClassLoader deferTo = getSystemClassLoader();
  
  public ClassLoader() {}
  
  public ClassLoader(java.lang.ClassLoader paramClassLoader) {
    this.deferTo = paramClassLoader;
    this.repository = new ClassLoaderRepository(paramClassLoader);
  }
  
  public ClassLoader(String[] paramArrayOfString) { addIgnoredPkgs(paramArrayOfString); }
  
  public ClassLoader(java.lang.ClassLoader paramClassLoader, String[] paramArrayOfString) {
    this.deferTo = paramClassLoader;
    this.repository = new ClassLoaderRepository(paramClassLoader);
    addIgnoredPkgs(paramArrayOfString);
  }
  
  private void addIgnoredPkgs(String[] paramArrayOfString) {
    String[] arrayOfString = new String[paramArrayOfString.length + this.ignored_packages.length];
    System.arraycopy(this.ignored_packages, 0, arrayOfString, 0, this.ignored_packages.length);
    System.arraycopy(paramArrayOfString, 0, arrayOfString, this.ignored_packages.length, paramArrayOfString.length);
    this.ignored_packages = arrayOfString;
  }
  
  protected Class loadClass(String paramString, boolean paramBoolean) throws ClassNotFoundException {
    Class clazz = null;
    if ((clazz = (Class)this.classes.get(paramString)) == null) {
      for (byte b = 0; b < this.ignored_packages.length; b++) {
        if (paramString.startsWith(this.ignored_packages[b])) {
          clazz = this.deferTo.loadClass(paramString);
          break;
        } 
      } 
      if (clazz == null) {
        JavaClass javaClass = null;
        if (paramString.indexOf("$$BCEL$$") >= 0) {
          javaClass = createClass(paramString);
        } else if ((javaClass = this.repository.loadClass(paramString)) != null) {
          javaClass = modifyClass(javaClass);
        } else {
          throw new ClassNotFoundException(paramString);
        } 
        if (javaClass != null) {
          byte[] arrayOfByte = javaClass.getBytes();
          clazz = defineClass(paramString, arrayOfByte, 0, arrayOfByte.length);
        } else {
          clazz = Class.forName(paramString);
        } 
      } 
      if (paramBoolean)
        resolveClass(clazz); 
    } 
    this.classes.put(paramString, clazz);
    return clazz;
  }
  
  protected JavaClass modifyClass(JavaClass paramJavaClass) { return paramJavaClass; }
  
  protected JavaClass createClass(String paramString) {
    int i = paramString.indexOf("$$BCEL$$");
    String str = paramString.substring(i + 8);
    JavaClass javaClass = null;
    try {
      byte[] arrayOfByte = Utility.decode(str, true);
      ClassParser classParser = new ClassParser(new ByteArrayInputStream(arrayOfByte), "foo");
      javaClass = classParser.parse();
    } catch (Throwable throwable) {
      throwable.printStackTrace();
      return null;
    } 
    ConstantPool constantPool = javaClass.getConstantPool();
    ConstantClass constantClass = (ConstantClass)constantPool.getConstant(javaClass.getClassNameIndex(), (byte)7);
    ConstantUtf8 constantUtf8 = (ConstantUtf8)constantPool.getConstant(constantClass.getNameIndex(), (byte)1);
    constantUtf8.setBytes(paramString.replace('.', '/'));
    return javaClass;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\interna\\util\ClassLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */