package jdk.management.resource.internal.inst;

import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;

@InstrumentationTarget("jdk.management.resource.internal.WrapInstrumentation")
public class WrapInstrumentationRMHooks {
  @InstrumentationMethod
  public boolean wrapComplete() {
    wrapComplete();
    return true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\internal\inst\WrapInstrumentationRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */