package java.awt.event;

import java.util.EventListener;

public interface ContainerListener extends EventListener {
  void componentAdded(ContainerEvent paramContainerEvent);
  
  void componentRemoved(ContainerEvent paramContainerEvent);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\event\ContainerListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */