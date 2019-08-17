package sun.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import sun.reflect.misc.ReflectUtil;

class NativeMethodAccessorImpl extends MethodAccessorImpl {
  private final Method method;
  
  private DelegatingMethodAccessorImpl parent;
  
  private int numInvocations;
  
  NativeMethodAccessorImpl(Method paramMethod) { this.method = paramMethod; }
  
  public Object invoke(Object paramObject, Object[] paramArrayOfObject) throws IllegalArgumentException, InvocationTargetException {
    if (++this.numInvocations > ReflectionFactory.inflationThreshold() && !ReflectUtil.isVMAnonymousClass(this.method.getDeclaringClass())) {
      MethodAccessorImpl methodAccessorImpl = (MethodAccessorImpl)(new MethodAccessorGenerator()).generateMethod(this.method.getDeclaringClass(), this.method.getName(), this.method.getParameterTypes(), this.method.getReturnType(), this.method.getExceptionTypes(), this.method.getModifiers());
      this.parent.setDelegate(methodAccessorImpl);
    } 
    return invoke0(this.method, paramObject, paramArrayOfObject);
  }
  
  void setParent(DelegatingMethodAccessorImpl paramDelegatingMethodAccessorImpl) { this.parent = paramDelegatingMethodAccessorImpl; }
  
  private static native Object invoke0(Method paramMethod, Object paramObject, Object[] paramArrayOfObject);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\NativeMethodAccessorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */