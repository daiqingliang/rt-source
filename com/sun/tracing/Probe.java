package com.sun.tracing;

public interface Probe {
  boolean isEnabled();
  
  void trigger(Object... paramVarArgs);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\tracing\Probe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */