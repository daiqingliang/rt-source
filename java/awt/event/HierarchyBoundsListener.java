package java.awt.event;

import java.util.EventListener;

public interface HierarchyBoundsListener extends EventListener {
  void ancestorMoved(HierarchyEvent paramHierarchyEvent);
  
  void ancestorResized(HierarchyEvent paramHierarchyEvent);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\event\HierarchyBoundsListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */