package sun.reflect.generics.scope;

import java.lang.reflect.Method;

public class MethodScope extends AbstractScope<Method> {
  private MethodScope(Method paramMethod) { super(paramMethod); }
  
  private Class<?> getEnclosingClass() { return ((Method)getRecvr()).getDeclaringClass(); }
  
  protected Scope computeEnclosingScope() { return ClassScope.make(getEnclosingClass()); }
  
  public static MethodScope make(Method paramMethod) { return new MethodScope(paramMethod); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\generics\scope\MethodScope.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */