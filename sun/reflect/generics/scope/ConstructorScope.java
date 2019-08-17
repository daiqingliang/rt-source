package sun.reflect.generics.scope;

import java.lang.reflect.Constructor;

public class ConstructorScope extends AbstractScope<Constructor<?>> {
  private ConstructorScope(Constructor<?> paramConstructor) { super(paramConstructor); }
  
  private Class<?> getEnclosingClass() { return ((Constructor)getRecvr()).getDeclaringClass(); }
  
  protected Scope computeEnclosingScope() { return ClassScope.make(getEnclosingClass()); }
  
  public static ConstructorScope make(Constructor<?> paramConstructor) { return new ConstructorScope(paramConstructor); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\generics\scope\ConstructorScope.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */