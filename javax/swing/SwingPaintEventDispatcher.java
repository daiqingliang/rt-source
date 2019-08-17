package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.PaintEvent;
import java.security.AccessController;
import sun.awt.AppContext;
import sun.awt.PaintEventDispatcher;
import sun.awt.SunToolkit;
import sun.awt.event.IgnorePaintEvent;
import sun.security.action.GetBooleanAction;
import sun.security.action.GetPropertyAction;

class SwingPaintEventDispatcher extends PaintEventDispatcher {
  private static final boolean SHOW_FROM_DOUBLE_BUFFER = "true".equals(AccessController.doPrivileged(new GetPropertyAction("swing.showFromDoubleBuffer", "true")));
  
  private static final boolean ERASE_BACKGROUND = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("swing.nativeErase"))).booleanValue();
  
  public PaintEvent createPaintEvent(Component paramComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (paramComponent instanceof RootPaneContainer) {
      AppContext appContext = SunToolkit.targetToAppContext(paramComponent);
      RepaintManager repaintManager = RepaintManager.currentManager(appContext);
      if (!SHOW_FROM_DOUBLE_BUFFER || !repaintManager.show((Container)paramComponent, paramInt1, paramInt2, paramInt3, paramInt4))
        repaintManager.nativeAddDirtyRegion(appContext, (Container)paramComponent, paramInt1, paramInt2, paramInt3, paramInt4); 
      return new IgnorePaintEvent(paramComponent, 800, new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4));
    } 
    if (paramComponent instanceof SwingHeavyWeight) {
      AppContext appContext = SunToolkit.targetToAppContext(paramComponent);
      RepaintManager repaintManager = RepaintManager.currentManager(appContext);
      repaintManager.nativeAddDirtyRegion(appContext, (Container)paramComponent, paramInt1, paramInt2, paramInt3, paramInt4);
      return new IgnorePaintEvent(paramComponent, 800, new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4));
    } 
    return super.createPaintEvent(paramComponent, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public boolean shouldDoNativeBackgroundErase(Component paramComponent) { return (ERASE_BACKGROUND || !(paramComponent instanceof RootPaneContainer)); }
  
  public boolean queueSurfaceDataReplacing(Component paramComponent, Runnable paramRunnable) {
    if (paramComponent instanceof RootPaneContainer) {
      AppContext appContext = SunToolkit.targetToAppContext(paramComponent);
      RepaintManager.currentManager(appContext).nativeQueueSurfaceDataRunnable(appContext, paramComponent, paramRunnable);
      return true;
    } 
    return super.queueSurfaceDataReplacing(paramComponent, paramRunnable);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\SwingPaintEventDispatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */