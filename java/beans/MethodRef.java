package java.beans;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import sun.reflect.misc.ReflectUtil;

final class MethodRef {
  private String signature;
  
  private SoftReference<Method> methodRef;
  
  private WeakReference<Class<?>> typeRef;
  
  void set(Method paramMethod) {
    if (paramMethod == null) {
      this.signature = null;
      this.methodRef = null;
      this.typeRef = null;
    } else {
      this.signature = paramMethod.toGenericString();
      this.methodRef = new SoftReference(paramMethod);
      this.typeRef = new WeakReference(paramMethod.getDeclaringClass());
    } 
  }
  
  boolean isSet() { return (this.methodRef != null); }
  
  Method get() {
    if (this.methodRef == null)
      return null; 
    Method method = (Method)this.methodRef.get();
    if (method == null) {
      method = find((Class)this.typeRef.get(), this.signature);
      if (method == null) {
        this.signature = null;
        this.methodRef = null;
        this.typeRef = null;
        return null;
      } 
      this.methodRef = new SoftReference(method);
    } 
    return ReflectUtil.isPackageAccessible(method.getDeclaringClass()) ? method : null;
  }
  
  private static Method find(Class<?> paramClass, String paramString) {
    if (paramClass != null)
      for (Method method : paramClass.getMethods()) {
        if (paramClass.equals(method.getDeclaringClass()) && method.toGenericString().equals(paramString))
          return method; 
      }  
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\MethodRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */