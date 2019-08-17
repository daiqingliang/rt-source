package sun.awt.windows;

import java.awt.Component;
import java.awt.Image;

public final class WBufferStrategy {
  private static native void initIDs(Class<?> paramClass);
  
  public static native Image getDrawBuffer(Component paramComponent);
  
  static  {
    initIDs(Component.class);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WBufferStrategy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */