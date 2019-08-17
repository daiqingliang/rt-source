package sun.instrument;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.jar.JarFile;

public class InstrumentationImpl implements Instrumentation {
  private final TransformerManager mTransformerManager = new TransformerManager(false);
  
  private TransformerManager mRetransfomableTransformerManager = null;
  
  private final long mNativeAgent;
  
  private final boolean mEnvironmentSupportsRedefineClasses;
  
  private final boolean mEnvironmentSupportsNativeMethodPrefix;
  
  private InstrumentationImpl(long paramLong, boolean paramBoolean1, boolean paramBoolean2) {
    this.mNativeAgent = paramLong;
    this.mEnvironmentSupportsRedefineClasses = paramBoolean1;
    this.mEnvironmentSupportsRetransformClassesKnown = false;
    this.mEnvironmentSupportsRetransformClasses = false;
    this.mEnvironmentSupportsNativeMethodPrefix = paramBoolean2;
  }
  
  public void addTransformer(ClassFileTransformer paramClassFileTransformer) { addTransformer(paramClassFileTransformer, false); }
  
  public void addTransformer(ClassFileTransformer paramClassFileTransformer, boolean paramBoolean) {
    if (paramClassFileTransformer == null)
      throw new NullPointerException("null passed as 'transformer' in addTransformer"); 
    if (paramBoolean) {
      if (!isRetransformClassesSupported())
        throw new UnsupportedOperationException("adding retransformable transformers is not supported in this environment"); 
      if (this.mRetransfomableTransformerManager == null)
        this.mRetransfomableTransformerManager = new TransformerManager(true); 
      this.mRetransfomableTransformerManager.addTransformer(paramClassFileTransformer);
      if (this.mRetransfomableTransformerManager.getTransformerCount() == 1)
        setHasRetransformableTransformers(this.mNativeAgent, true); 
    } else {
      this.mTransformerManager.addTransformer(paramClassFileTransformer);
    } 
  }
  
  public boolean removeTransformer(ClassFileTransformer paramClassFileTransformer) {
    if (paramClassFileTransformer == null)
      throw new NullPointerException("null passed as 'transformer' in removeTransformer"); 
    TransformerManager transformerManager = findTransformerManager(paramClassFileTransformer);
    if (transformerManager != null) {
      transformerManager.removeTransformer(paramClassFileTransformer);
      if (transformerManager.isRetransformable() && transformerManager.getTransformerCount() == 0)
        setHasRetransformableTransformers(this.mNativeAgent, false); 
      return true;
    } 
    return false;
  }
  
  public boolean isModifiableClass(Class<?> paramClass) {
    if (paramClass == null)
      throw new NullPointerException("null passed as 'theClass' in isModifiableClass"); 
    return isModifiableClass0(this.mNativeAgent, paramClass);
  }
  
  public boolean isRetransformClassesSupported() {
    if (!this.mEnvironmentSupportsRetransformClassesKnown) {
      this.mEnvironmentSupportsRetransformClasses = isRetransformClassesSupported0(this.mNativeAgent);
      this.mEnvironmentSupportsRetransformClassesKnown = true;
    } 
    return this.mEnvironmentSupportsRetransformClasses;
  }
  
  public void retransformClasses(Class<?>... paramVarArgs) {
    if (!isRetransformClassesSupported())
      throw new UnsupportedOperationException("retransformClasses is not supported in this environment"); 
    retransformClasses0(this.mNativeAgent, paramVarArgs);
  }
  
  public boolean isRedefineClassesSupported() { return this.mEnvironmentSupportsRedefineClasses; }
  
  public void redefineClasses(ClassDefinition... paramVarArgs) throws ClassNotFoundException {
    if (!isRedefineClassesSupported())
      throw new UnsupportedOperationException("redefineClasses is not supported in this environment"); 
    if (paramVarArgs == null)
      throw new NullPointerException("null passed as 'definitions' in redefineClasses"); 
    for (byte b = 0; b < paramVarArgs.length; b++) {
      if (paramVarArgs[b] == null)
        throw new NullPointerException("element of 'definitions' is null in redefineClasses"); 
    } 
    if (paramVarArgs.length == 0)
      return; 
    redefineClasses0(this.mNativeAgent, paramVarArgs);
  }
  
  public Class[] getAllLoadedClasses() { return getAllLoadedClasses0(this.mNativeAgent); }
  
  public Class[] getInitiatedClasses(ClassLoader paramClassLoader) { return getInitiatedClasses0(this.mNativeAgent, paramClassLoader); }
  
  public long getObjectSize(Object paramObject) {
    if (paramObject == null)
      throw new NullPointerException("null passed as 'objectToSize' in getObjectSize"); 
    return getObjectSize0(this.mNativeAgent, paramObject);
  }
  
  public void appendToBootstrapClassLoaderSearch(JarFile paramJarFile) { appendToClassLoaderSearch0(this.mNativeAgent, paramJarFile.getName(), true); }
  
  public void appendToSystemClassLoaderSearch(JarFile paramJarFile) { appendToClassLoaderSearch0(this.mNativeAgent, paramJarFile.getName(), false); }
  
  public boolean isNativeMethodPrefixSupported() { return this.mEnvironmentSupportsNativeMethodPrefix; }
  
  public void setNativeMethodPrefix(ClassFileTransformer paramClassFileTransformer, String paramString) {
    if (!isNativeMethodPrefixSupported())
      throw new UnsupportedOperationException("setNativeMethodPrefix is not supported in this environment"); 
    if (paramClassFileTransformer == null)
      throw new NullPointerException("null passed as 'transformer' in setNativeMethodPrefix"); 
    TransformerManager transformerManager = findTransformerManager(paramClassFileTransformer);
    if (transformerManager == null)
      throw new IllegalArgumentException("transformer not registered in setNativeMethodPrefix"); 
    transformerManager.setNativeMethodPrefix(paramClassFileTransformer, paramString);
    String[] arrayOfString = transformerManager.getNativeMethodPrefixes();
    setNativeMethodPrefixes(this.mNativeAgent, arrayOfString, transformerManager.isRetransformable());
  }
  
  private TransformerManager findTransformerManager(ClassFileTransformer paramClassFileTransformer) { return this.mTransformerManager.includesTransformer(paramClassFileTransformer) ? this.mTransformerManager : ((this.mRetransfomableTransformerManager != null && this.mRetransfomableTransformerManager.includesTransformer(paramClassFileTransformer)) ? this.mRetransfomableTransformerManager : null); }
  
  private native boolean isModifiableClass0(long paramLong, Class<?> paramClass);
  
  private native boolean isRetransformClassesSupported0(long paramLong);
  
  private native void setHasRetransformableTransformers(long paramLong, boolean paramBoolean);
  
  private native void retransformClasses0(long paramLong, Class<?>[] paramArrayOfClass);
  
  private native void redefineClasses0(long paramLong, ClassDefinition[] paramArrayOfClassDefinition) throws ClassNotFoundException;
  
  private native Class[] getAllLoadedClasses0(long paramLong);
  
  private native Class[] getInitiatedClasses0(long paramLong, ClassLoader paramClassLoader);
  
  private native long getObjectSize0(long paramLong, Object paramObject);
  
  private native void appendToClassLoaderSearch0(long paramLong, String paramString, boolean paramBoolean);
  
  private native void setNativeMethodPrefixes(long paramLong, String[] paramArrayOfString, boolean paramBoolean);
  
  private static void setAccessible(final AccessibleObject ao, final boolean accessible) { AccessController.doPrivileged(new PrivilegedAction<Object>() {
          public Object run() {
            ao.setAccessible(accessible);
            return null;
          }
        }); }
  
  private void loadClassAndStartAgent(String paramString1, String paramString2, String paramString3) throws Throwable {
    ClassLoader classLoader = ClassLoader.getSystemClassLoader();
    Class clazz = classLoader.loadClass(paramString1);
    Method method = null;
    NoSuchMethodException noSuchMethodException = null;
    boolean bool = false;
    try {
      method = clazz.getDeclaredMethod(paramString2, new Class[] { String.class, Instrumentation.class });
      bool = true;
    } catch (NoSuchMethodException noSuchMethodException1) {
      noSuchMethodException = noSuchMethodException1;
    } 
    if (method == null)
      try {
        method = clazz.getDeclaredMethod(paramString2, new Class[] { String.class });
      } catch (NoSuchMethodException noSuchMethodException1) {} 
    if (method == null)
      try {
        method = clazz.getMethod(paramString2, new Class[] { String.class, Instrumentation.class });
        bool = true;
      } catch (NoSuchMethodException noSuchMethodException1) {} 
    if (method == null)
      try {
        method = clazz.getMethod(paramString2, new Class[] { String.class });
      } catch (NoSuchMethodException noSuchMethodException1) {
        throw noSuchMethodException;
      }  
    setAccessible(method, true);
    if (bool) {
      method.invoke(null, new Object[] { paramString3, this });
    } else {
      method.invoke(null, new Object[] { paramString3 });
    } 
    setAccessible(method, false);
  }
  
  private void loadClassAndCallPremain(String paramString1, String paramString2) throws Throwable { loadClassAndStartAgent(paramString1, "premain", paramString2); }
  
  private void loadClassAndCallAgentmain(String paramString1, String paramString2) throws Throwable { loadClassAndStartAgent(paramString1, "agentmain", paramString2); }
  
  private byte[] transform(ClassLoader paramClassLoader, String paramString, Class<?> paramClass, ProtectionDomain paramProtectionDomain, byte[] paramArrayOfByte, boolean paramBoolean) {
    TransformerManager transformerManager = paramBoolean ? this.mRetransfomableTransformerManager : this.mTransformerManager;
    return (transformerManager == null) ? null : transformerManager.transform(paramClassLoader, paramString, paramClass, paramProtectionDomain, paramArrayOfByte);
  }
  
  static  {
    System.loadLibrary("instrument");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\instrument\InstrumentationImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */