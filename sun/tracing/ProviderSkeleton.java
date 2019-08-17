package sun.tracing;

import com.sun.tracing.Probe;
import com.sun.tracing.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;

public abstract class ProviderSkeleton implements InvocationHandler, Provider {
  protected boolean active = false;
  
  protected Class<? extends Provider> providerType;
  
  protected HashMap<Method, ProbeSkeleton> probes;
  
  protected abstract ProbeSkeleton createProbe(Method paramMethod);
  
  protected ProviderSkeleton(Class<? extends Provider> paramClass) {
    this.providerType = paramClass;
    this.probes = new HashMap();
  }
  
  public void init() {
    Method[] arrayOfMethod = (Method[])AccessController.doPrivileged(new PrivilegedAction<Method[]>() {
          public Method[] run() { return ProviderSkeleton.this.providerType.getDeclaredMethods(); }
        });
    for (Method method : arrayOfMethod) {
      if (method.getReturnType() != void.class)
        throw new IllegalArgumentException("Return value of method is not void"); 
      this.probes.put(method, createProbe(method));
    } 
    this.active = true;
  }
  
  public <T extends Provider> T newProxyInstance() {
    final ProviderSkeleton ih = this;
    return (T)(Provider)AccessController.doPrivileged(new PrivilegedAction<T>() {
          public T run() { return (T)(Provider)Proxy.newProxyInstance(ProviderSkeleton.this.providerType.getClassLoader(), new Class[] { ProviderSkeleton.this.providerType }, ih); }
        });
  }
  
  public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject) {
    Class clazz = paramMethod.getDeclaringClass();
    if (clazz != this.providerType) {
      try {
        if (clazz == Provider.class || clazz == Object.class)
          return paramMethod.invoke(this, paramArrayOfObject); 
        throw new SecurityException();
      } catch (IllegalAccessException illegalAccessException) {
        assert false;
      } catch (InvocationTargetException invocationTargetException) {
        assert false;
      } 
    } else {
      triggerProbe(paramMethod, paramArrayOfObject);
    } 
    return null;
  }
  
  public Probe getProbe(Method paramMethod) { return this.active ? (Probe)this.probes.get(paramMethod) : null; }
  
  public void dispose() {
    this.active = false;
    this.probes.clear();
  }
  
  protected String getProviderName() { return getAnnotationString(this.providerType, com.sun.tracing.ProviderName.class, this.providerType.getSimpleName()); }
  
  protected static String getAnnotationString(AnnotatedElement paramAnnotatedElement, Class<? extends Annotation> paramClass, String paramString) {
    String str = (String)getAnnotationValue(paramAnnotatedElement, paramClass, "value", paramString);
    return str.isEmpty() ? paramString : str;
  }
  
  protected static Object getAnnotationValue(AnnotatedElement paramAnnotatedElement, Class<? extends Annotation> paramClass, String paramString, Object paramObject) {
    Object object = paramObject;
    try {
      Method method = paramClass.getMethod(paramString, new Class[0]);
      Annotation annotation = paramAnnotatedElement.getAnnotation(paramClass);
      object = method.invoke(annotation, new Object[0]);
    } catch (NoSuchMethodException noSuchMethodException) {
      assert false;
    } catch (IllegalAccessException illegalAccessException) {
      assert false;
    } catch (InvocationTargetException invocationTargetException) {
      assert false;
    } catch (NullPointerException nullPointerException) {
      assert false;
    } 
    return object;
  }
  
  protected void triggerProbe(Method paramMethod, Object[] paramArrayOfObject) {
    if (this.active) {
      ProbeSkeleton probeSkeleton = (ProbeSkeleton)this.probes.get(paramMethod);
      if (probeSkeleton != null)
        probeSkeleton.uncheckedTrigger(paramArrayOfObject); 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\tracing\ProviderSkeleton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */