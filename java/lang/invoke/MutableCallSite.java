package java.lang.invoke;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.util.concurrent.atomic.AtomicInteger;

public class MutableCallSite extends CallSite {
  private static final AtomicInteger STORE_BARRIER = new AtomicInteger();
  
  public MutableCallSite(MethodType paramMethodType) { super(paramMethodType); }
  
  public MutableCallSite(MethodHandle paramMethodHandle) { super(paramMethodHandle); }
  
  public final MethodHandle getTarget() { return this.target; }
  
  public void setTarget(MethodHandle paramMethodHandle) {
    checkTargetChange(this.target, paramMethodHandle);
    setTargetNormal(paramMethodHandle);
  }
  
  public final MethodHandle dynamicInvoker() { return makeDynamicInvoker(); }
  
  public static void syncAll(MutableCallSite[] paramArrayOfMutableCallSite) {
    if (paramArrayOfMutableCallSite.length == 0)
      return; 
    STORE_BARRIER.lazySet(0);
    for (byte b = 0; b < paramArrayOfMutableCallSite.length; b++)
      paramArrayOfMutableCallSite[b].getClass(); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\invoke\MutableCallSite.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */