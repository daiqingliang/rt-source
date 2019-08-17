package sun.net;

import java.net.URL;

class DefaultProgressMeteringPolicy implements ProgressMeteringPolicy {
  public boolean shouldMeterInput(URL paramURL, String paramString) { return false; }
  
  public int getProgressUpdateThreshold() { return 8192; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\DefaultProgressMeteringPolicy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */