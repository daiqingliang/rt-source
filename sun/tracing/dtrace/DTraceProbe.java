package sun.tracing.dtrace;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import sun.tracing.ProbeSkeleton;

class DTraceProbe extends ProbeSkeleton {
  private Object proxy;
  
  private Method declared_method;
  
  private Method implementing_method;
  
  DTraceProbe(Object paramObject, Method paramMethod) {
    super(paramMethod.getParameterTypes());
    this.proxy = paramObject;
    this.declared_method = paramMethod;
    try {
      this.implementing_method = paramObject.getClass().getMethod(paramMethod.getName(), paramMethod.getParameterTypes());
    } catch (NoSuchMethodException noSuchMethodException) {
      throw new RuntimeException("Internal error, wrong proxy class");
    } 
  }
  
  public boolean isEnabled() { return JVM.isEnabled(this.implementing_method); }
  
  public void uncheckedTrigger(Object[] paramArrayOfObject) {
    try {
      this.implementing_method.invoke(this.proxy, paramArrayOfObject);
    } catch (IllegalAccessException illegalAccessException) {
      assert false;
    } catch (InvocationTargetException invocationTargetException) {
      assert false;
    } 
  }
  
  String getProbeName() { return DTraceProvider.getProbeName(this.declared_method); }
  
  String getFunctionName() { return DTraceProvider.getFunctionName(this.declared_method); }
  
  Method getMethod() { return this.implementing_method; }
  
  Class<?>[] getParameterTypes() { return this.parameters; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\tracing\dtrace\DTraceProbe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */