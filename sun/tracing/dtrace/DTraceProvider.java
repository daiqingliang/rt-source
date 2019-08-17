package sun.tracing.dtrace;

import com.sun.tracing.Provider;
import com.sun.tracing.dtrace.Attributes;
import com.sun.tracing.dtrace.DependencyClass;
import com.sun.tracing.dtrace.StabilityLevel;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import sun.misc.ProxyGenerator;
import sun.tracing.ProbeSkeleton;
import sun.tracing.ProviderSkeleton;

class DTraceProvider extends ProviderSkeleton {
  private Activation activation;
  
  private Object proxy;
  
  private static final Class[] constructorParams = { java.lang.reflect.InvocationHandler.class };
  
  private final String proxyClassNamePrefix = "$DTraceTracingProxy";
  
  static final String DEFAULT_MODULE = "java_tracing";
  
  static final String DEFAULT_FUNCTION = "unspecified";
  
  private static long nextUniqueNumber = 0L;
  
  private static long getUniqueNumber() { return nextUniqueNumber++; }
  
  protected ProbeSkeleton createProbe(Method paramMethod) { return new DTraceProbe(this.proxy, paramMethod); }
  
  DTraceProvider(Class<? extends Provider> paramClass) { super(paramClass); }
  
  void setProxy(Object paramObject) { this.proxy = paramObject; }
  
  void setActivation(Activation paramActivation) { this.activation = paramActivation; }
  
  public void dispose() {
    if (this.activation != null) {
      this.activation.disposeProvider(this);
      this.activation = null;
    } 
    super.dispose();
  }
  
  public <T extends Provider> T newProxyInstance() {
    long l = getUniqueNumber();
    String str1 = "";
    if (!Modifier.isPublic(this.providerType.getModifiers())) {
      String str = this.providerType.getName();
      int i = str.lastIndexOf('.');
      str1 = (i == -1) ? "" : str.substring(0, i + 1);
    } 
    String str2 = str1 + "$DTraceTracingProxy" + l;
    Class clazz = null;
    byte[] arrayOfByte = ProxyGenerator.generateProxyClass(str2, new Class[] { this.providerType });
    try {
      clazz = JVM.defineClass(this.providerType.getClassLoader(), str2, arrayOfByte, 0, arrayOfByte.length);
    } catch (ClassFormatError classFormatError) {
      throw new IllegalArgumentException(classFormatError.toString());
    } 
    try {
      Constructor constructor = clazz.getConstructor(constructorParams);
      return (T)(Provider)constructor.newInstance(new Object[] { this });
    } catch (ReflectiveOperationException reflectiveOperationException) {
      throw new InternalError(reflectiveOperationException.toString(), reflectiveOperationException);
    } 
  }
  
  protected void triggerProbe(Method paramMethod, Object[] paramArrayOfObject) { assert false : "This method should have been overridden by the JVM"; }
  
  public String getProviderName() { return super.getProviderName(); }
  
  String getModuleName() { return getAnnotationString(this.providerType, com.sun.tracing.dtrace.ModuleName.class, "java_tracing"); }
  
  static String getProbeName(Method paramMethod) { return getAnnotationString(paramMethod, com.sun.tracing.ProbeName.class, paramMethod.getName()); }
  
  static String getFunctionName(Method paramMethod) { return getAnnotationString(paramMethod, com.sun.tracing.dtrace.FunctionName.class, "unspecified"); }
  
  DTraceProbe[] getProbes() { return (DTraceProbe[])this.probes.values().toArray(new DTraceProbe[0]); }
  
  StabilityLevel getNameStabilityFor(Class<? extends Annotation> paramClass) {
    Attributes attributes = (Attributes)getAnnotationValue(this.providerType, paramClass, "value", null);
    return (attributes == null) ? StabilityLevel.PRIVATE : attributes.name();
  }
  
  StabilityLevel getDataStabilityFor(Class<? extends Annotation> paramClass) {
    Attributes attributes = (Attributes)getAnnotationValue(this.providerType, paramClass, "value", null);
    return (attributes == null) ? StabilityLevel.PRIVATE : attributes.data();
  }
  
  DependencyClass getDependencyClassFor(Class<? extends Annotation> paramClass) {
    Attributes attributes = (Attributes)getAnnotationValue(this.providerType, paramClass, "value", null);
    return (attributes == null) ? DependencyClass.UNKNOWN : attributes.dependency();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\tracing\dtrace\DTraceProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */