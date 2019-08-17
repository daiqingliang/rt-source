package sun.swing;

import java.awt.Point;
import javax.swing.RepaintManager;
import javax.swing.TransferHandler;
import javax.swing.text.JTextComponent;
import sun.misc.Unsafe;

public final class SwingAccessor {
  private static final Unsafe unsafe = Unsafe.getUnsafe();
  
  private static JTextComponentAccessor jtextComponentAccessor;
  
  private static JLightweightFrameAccessor jLightweightFrameAccessor;
  
  private static RepaintManagerAccessor repaintManagerAccessor;
  
  public static void setJTextComponentAccessor(JTextComponentAccessor paramJTextComponentAccessor) { jtextComponentAccessor = paramJTextComponentAccessor; }
  
  public static JTextComponentAccessor getJTextComponentAccessor() {
    if (jtextComponentAccessor == null)
      unsafe.ensureClassInitialized(JTextComponent.class); 
    return jtextComponentAccessor;
  }
  
  public static void setJLightweightFrameAccessor(JLightweightFrameAccessor paramJLightweightFrameAccessor) { jLightweightFrameAccessor = paramJLightweightFrameAccessor; }
  
  public static JLightweightFrameAccessor getJLightweightFrameAccessor() {
    if (jLightweightFrameAccessor == null)
      unsafe.ensureClassInitialized(JLightweightFrame.class); 
    return jLightweightFrameAccessor;
  }
  
  public static void setRepaintManagerAccessor(RepaintManagerAccessor paramRepaintManagerAccessor) { repaintManagerAccessor = paramRepaintManagerAccessor; }
  
  public static RepaintManagerAccessor getRepaintManagerAccessor() {
    if (repaintManagerAccessor == null)
      unsafe.ensureClassInitialized(RepaintManager.class); 
    return repaintManagerAccessor;
  }
  
  public static interface JLightweightFrameAccessor {
    void updateCursor(JLightweightFrame param1JLightweightFrame);
  }
  
  public static interface JTextComponentAccessor {
    TransferHandler.DropLocation dropLocationForPoint(JTextComponent param1JTextComponent, Point param1Point);
    
    Object setDropLocation(JTextComponent param1JTextComponent, TransferHandler.DropLocation param1DropLocation, Object param1Object, boolean param1Boolean);
  }
  
  public static interface RepaintManagerAccessor {
    void addRepaintListener(RepaintManager param1RepaintManager, SwingUtilities2.RepaintListener param1RepaintListener);
    
    void removeRepaintListener(RepaintManager param1RepaintManager, SwingUtilities2.RepaintListener param1RepaintListener);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\swing\SwingAccessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */