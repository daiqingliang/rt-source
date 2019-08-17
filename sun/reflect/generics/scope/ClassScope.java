package sun.reflect.generics.scope;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class ClassScope extends AbstractScope<Class<?>> implements Scope {
  private ClassScope(Class<?> paramClass) { super(paramClass); }
  
  protected Scope computeEnclosingScope() {
    Class clazz1 = (Class)getRecvr();
    Method method = clazz1.getEnclosingMethod();
    if (method != null)
      return MethodScope.make(method); 
    Constructor constructor = clazz1.getEnclosingConstructor();
    if (constructor != null)
      return ConstructorScope.make(constructor); 
    Class clazz2 = clazz1.getEnclosingClass();
    return (clazz2 != null) ? make(clazz2) : DummyScope.make();
  }
  
  public static ClassScope make(Class<?> paramClass) { return new ClassScope(paramClass); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\generics\scope\ClassScope.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */