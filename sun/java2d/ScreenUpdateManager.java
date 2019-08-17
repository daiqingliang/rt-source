package sun.java2d;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import sun.awt.Win32GraphicsConfig;
import sun.awt.windows.WComponentPeer;
import sun.java2d.d3d.D3DScreenUpdateManager;
import sun.java2d.windows.WindowsFlags;

public class ScreenUpdateManager {
  private static ScreenUpdateManager theInstance;
  
  public Graphics2D createGraphics(SurfaceData paramSurfaceData, WComponentPeer paramWComponentPeer, Color paramColor1, Color paramColor2, Font paramFont) { return new SunGraphics2D(paramSurfaceData, paramColor1, paramColor2, paramFont); }
  
  public SurfaceData createScreenSurface(Win32GraphicsConfig paramWin32GraphicsConfig, WComponentPeer paramWComponentPeer, int paramInt, boolean paramBoolean) { return paramWin32GraphicsConfig.createSurfaceData(paramWComponentPeer, paramInt); }
  
  public void dropScreenSurface(SurfaceData paramSurfaceData) {}
  
  public SurfaceData getReplacementScreenSurface(WComponentPeer paramWComponentPeer, SurfaceData paramSurfaceData) {
    SurfaceData surfaceData = paramWComponentPeer.getSurfaceData();
    if (surfaceData == null || surfaceData.isValid())
      return surfaceData; 
    paramWComponentPeer.replaceSurfaceData();
    return paramWComponentPeer.getSurfaceData();
  }
  
  public static ScreenUpdateManager getInstance() {
    if (theInstance == null)
      if (WindowsFlags.isD3DEnabled()) {
        theInstance = new D3DScreenUpdateManager();
      } else {
        theInstance = new ScreenUpdateManager();
      }  
    return theInstance;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\ScreenUpdateManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */