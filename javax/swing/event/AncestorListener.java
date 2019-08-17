package javax.swing.event;

import java.util.EventListener;

public interface AncestorListener extends EventListener {
  void ancestorAdded(AncestorEvent paramAncestorEvent);
  
  void ancestorRemoved(AncestorEvent paramAncestorEvent);
  
  void ancestorMoved(AncestorEvent paramAncestorEvent);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\event\AncestorListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */