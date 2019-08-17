package java.lang.invoke;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.SwitchPoint;

public class SwitchPoint {
  private static final MethodHandle K_true = MethodHandles.constant(boolean.class, Boolean.valueOf(true));
  
  private static final MethodHandle K_false = MethodHandles.constant(boolean.class, Boolean.valueOf(false));
  
  private final MutableCallSite mcs = new MutableCallSite(K_true);
  
  private final MethodHandle mcsInvoker = this.mcs.dynamicInvoker();
  
  public boolean hasBeenInvalidated() { return (this.mcs.getTarget() != K_true); }
  
  public MethodHandle guardWithTest(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2) { return (this.mcs.getTarget() == K_false) ? paramMethodHandle2 : MethodHandles.guardWithTest(this.mcsInvoker, paramMethodHandle1, paramMethodHandle2); }
  
  public static void invalidateAll(SwitchPoint[] paramArrayOfSwitchPoint) {
    if (paramArrayOfSwitchPoint.length == 0)
      return; 
    MutableCallSite[] arrayOfMutableCallSite = new MutableCallSite[paramArrayOfSwitchPoint.length];
    for (byte b = 0; b < paramArrayOfSwitchPoint.length; b++) {
      SwitchPoint switchPoint = paramArrayOfSwitchPoint[b];
      if (switchPoint == null)
        break; 
      arrayOfMutableCallSite[b] = switchPoint.mcs;
      switchPoint.mcs.setTarget(K_false);
    } 
    MutableCallSite.syncAll(arrayOfMutableCallSite);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\invoke\SwitchPoint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */