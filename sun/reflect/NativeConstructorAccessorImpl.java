package sun.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import sun.reflect.misc.ReflectUtil;

class NativeConstructorAccessorImpl extends ConstructorAccessorImpl {
  private final Constructor<?> c;
  
  private DelegatingConstructorAccessorImpl parent;
  
  private int numInvocations;
  
  NativeConstructorAccessorImpl(Constructor<?> paramConstructor) { this.c = paramConstructor; }
  
  public Object newInstance(Object[] paramArrayOfObject) throws InstantiationException, IllegalArgumentException, InvocationTargetException {
    if (++this.numInvocations > ReflectionFactory.inflationThreshold() && !ReflectUtil.isVMAnonymousClass(this.c.getDeclaringClass())) {
      ConstructorAccessorImpl constructorAccessorImpl = (ConstructorAccessorImpl)(new MethodAccessorGenerator()).generateConstructor(this.c.getDeclaringClass(), this.c.getParameterTypes(), this.c.getExceptionTypes(), this.c.getModifiers());
      this.parent.setDelegate(constructorAccessorImpl);
    } 
    return newInstance0(this.c, paramArrayOfObject);
  }
  
  void setParent(DelegatingConstructorAccessorImpl paramDelegatingConstructorAccessorImpl) { this.parent = paramDelegatingConstructorAccessorImpl; }
  
  private static native Object newInstance0(Constructor<?> paramConstructor, Object[] paramArrayOfObject) throws InstantiationException, IllegalArgumentException, InvocationTargetException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\NativeConstructorAccessorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */