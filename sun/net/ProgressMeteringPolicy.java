package sun.net;

import java.net.URL;

public interface ProgressMeteringPolicy {
  boolean shouldMeterInput(URL paramURL, String paramString);
  
  int getProgressUpdateThreshold();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\ProgressMeteringPolicy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */