package sun.java2d.pipe.hw;

import java.awt.image.VolatileImage;

public interface AccelGraphicsConfig extends BufferedContextProvider {
  VolatileImage createCompatibleVolatileImage(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  ContextCapabilities getContextCapabilities();
  
  void addDeviceEventListener(AccelDeviceEventListener paramAccelDeviceEventListener);
  
  void removeDeviceEventListener(AccelDeviceEventListener paramAccelDeviceEventListener);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\pipe\hw\AccelGraphicsConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */