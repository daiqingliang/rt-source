package java.lang.invoke;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.invoke.VolatileCallSite;

public class VolatileCallSite extends CallSite {
  public VolatileCallSite(MethodType paramMethodType) { super(paramMethodType); }
  
  public VolatileCallSite(MethodHandle paramMethodHandle) { super(paramMethodHandle); }
  
  public final MethodHandle getTarget() { return getTargetVolatile(); }
  
  public void setTarget(MethodHandle paramMethodHandle) {
    checkTargetChange(getTargetVolatile(), paramMethodHandle);
    setTargetVolatile(paramMethodHandle);
  }
  
  public final MethodHandle dynamicInvoker() { return makeDynamicInvoker(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\invoke\VolatileCallSite.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */