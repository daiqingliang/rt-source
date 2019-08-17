package java.lang.instrument;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.UnmodifiableClassException;
import java.util.jar.JarFile;

public interface Instrumentation {
  void addTransformer(ClassFileTransformer paramClassFileTransformer, boolean paramBoolean);
  
  void addTransformer(ClassFileTransformer paramClassFileTransformer);
  
  boolean removeTransformer(ClassFileTransformer paramClassFileTransformer);
  
  boolean isRetransformClassesSupported();
  
  void retransformClasses(Class<?>... paramVarArgs) throws UnmodifiableClassException;
  
  boolean isRedefineClassesSupported();
  
  void redefineClasses(ClassDefinition... paramVarArgs) throws ClassNotFoundException, UnmodifiableClassException;
  
  boolean isModifiableClass(Class<?> paramClass);
  
  Class[] getAllLoadedClasses();
  
  Class[] getInitiatedClasses(ClassLoader paramClassLoader);
  
  long getObjectSize(Object paramObject);
  
  void appendToBootstrapClassLoaderSearch(JarFile paramJarFile);
  
  void appendToSystemClassLoaderSearch(JarFile paramJarFile);
  
  boolean isNativeMethodPrefixSupported();
  
  void setNativeMethodPrefix(ClassFileTransformer paramClassFileTransformer, String paramString);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\instrument\Instrumentation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */