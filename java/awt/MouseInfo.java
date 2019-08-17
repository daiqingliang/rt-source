package java.awt;

import sun.security.util.SecurityConstants;

public class MouseInfo {
  public static PointerInfo getPointerInfo() throws HeadlessException {
    if (GraphicsEnvironment.isHeadless())
      throw new HeadlessException(); 
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(SecurityConstants.AWT.WATCH_MOUSE_PERMISSION); 
    Point point = new Point(0, 0);
    int i = Toolkit.getDefaultToolkit().getMouseInfoPeer().fillPointWithCoords(point);
    GraphicsDevice[] arrayOfGraphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
    PointerInfo pointerInfo = null;
    if (areScreenDevicesIndependent(arrayOfGraphicsDevice)) {
      pointerInfo = new PointerInfo(arrayOfGraphicsDevice[i], point);
    } else {
      for (byte b = 0; b < arrayOfGraphicsDevice.length; b++) {
        GraphicsConfiguration graphicsConfiguration = arrayOfGraphicsDevice[b].getDefaultConfiguration();
        Rectangle rectangle = graphicsConfiguration.getBounds();
        if (rectangle.contains(point))
          pointerInfo = new PointerInfo(arrayOfGraphicsDevice[b], point); 
      } 
    } 
    return pointerInfo;
  }
  
  private static boolean areScreenDevicesIndependent(GraphicsDevice[] paramArrayOfGraphicsDevice) {
    for (byte b = 0; b < paramArrayOfGraphicsDevice.length; b++) {
      Rectangle rectangle = paramArrayOfGraphicsDevice[b].getDefaultConfiguration().getBounds();
      if (rectangle.x != 0 || rectangle.y != 0)
        return false; 
    } 
    return true;
  }
  
  public static int getNumberOfButtons() throws HeadlessException {
    if (GraphicsEnvironment.isHeadless())
      throw new HeadlessException(); 
    Object object = Toolkit.getDefaultToolkit().getDesktopProperty("awt.mouse.numButtons");
    if (object instanceof Integer)
      return ((Integer)object).intValue(); 
    assert false : "awt.mouse.numButtons is not an integer property";
    return 0;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\MouseInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */