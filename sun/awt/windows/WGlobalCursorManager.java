package sun.awt.windows;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import sun.awt.GlobalCursorManager;

final class WGlobalCursorManager extends GlobalCursorManager {
  private static WGlobalCursorManager manager;
  
  public static GlobalCursorManager getCursorManager() {
    if (manager == null)
      manager = new WGlobalCursorManager(); 
    return manager;
  }
  
  public static void nativeUpdateCursor(Component paramComponent) { getCursorManager().updateCursorLater(paramComponent); }
  
  protected native void setCursor(Component paramComponent, Cursor paramCursor, boolean paramBoolean);
  
  protected native void getCursorPos(Point paramPoint);
  
  protected native Component findHeavyweightUnderCursor(boolean paramBoolean);
  
  protected native Point getLocationOnScreen(Component paramComponent);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WGlobalCursorManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */