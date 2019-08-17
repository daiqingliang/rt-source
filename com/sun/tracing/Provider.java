package com.sun.tracing;

import java.lang.reflect.Method;

public interface Provider {
  Probe getProbe(Method paramMethod);
  
  void dispose();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\tracing\Provider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */