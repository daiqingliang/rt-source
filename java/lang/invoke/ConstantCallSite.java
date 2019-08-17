package java.lang.invoke;

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

public class ConstantCallSite extends CallSite {
  private final boolean isFrozen = true;
  
  public ConstantCallSite(MethodHandle paramMethodHandle) { super(paramMethodHandle); }
  
  protected ConstantCallSite(MethodType paramMethodType, MethodHandle paramMethodHandle) throws Throwable { super(paramMethodType, paramMethodHandle); }
  
  public final MethodHandle getTarget() {
    if (!this.isFrozen)
      throw new IllegalStateException(); 
    return this.target;
  }
  
  public final void setTarget(MethodHandle paramMethodHandle) { throw new UnsupportedOperationException(); }
  
  public final MethodHandle dynamicInvoker() { return getTarget(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\invoke\ConstantCallSite.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */