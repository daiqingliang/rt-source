package javax.swing.event;

import java.awt.AWTEvent;
import javax.swing.JInternalFrame;

public class InternalFrameEvent extends AWTEvent {
  public static final int INTERNAL_FRAME_FIRST = 25549;
  
  public static final int INTERNAL_FRAME_LAST = 25555;
  
  public static final int INTERNAL_FRAME_OPENED = 25549;
  
  public static final int INTERNAL_FRAME_CLOSING = 25550;
  
  public static final int INTERNAL_FRAME_CLOSED = 25551;
  
  public static final int INTERNAL_FRAME_ICONIFIED = 25552;
  
  public static final int INTERNAL_FRAME_DEICONIFIED = 25553;
  
  public static final int INTERNAL_FRAME_ACTIVATED = 25554;
  
  public static final int INTERNAL_FRAME_DEACTIVATED = 25555;
  
  public InternalFrameEvent(JInternalFrame paramJInternalFrame, int paramInt) { super(paramJInternalFrame, paramInt); }
  
  public String paramString() {
    switch (this.id) {
      case 25549:
        return "INTERNAL_FRAME_OPENED";
      case 25550:
        return "INTERNAL_FRAME_CLOSING";
      case 25551:
        return "INTERNAL_FRAME_CLOSED";
      case 25552:
        return "INTERNAL_FRAME_ICONIFIED";
      case 25553:
        return "INTERNAL_FRAME_DEICONIFIED";
      case 25554:
        return "INTERNAL_FRAME_ACTIVATED";
      case 25555:
        return "INTERNAL_FRAME_DEACTIVATED";
    } 
    return "unknown type";
  }
  
  public JInternalFrame getInternalFrame() { return (this.source instanceof JInternalFrame) ? (JInternalFrame)this.source : null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\event\InternalFrameEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */