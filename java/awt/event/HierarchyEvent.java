package java.awt.event;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;

public class HierarchyEvent extends AWTEvent {
  private static final long serialVersionUID = -5337576970038043990L;
  
  public static final int HIERARCHY_FIRST = 1400;
  
  public static final int HIERARCHY_CHANGED = 1400;
  
  public static final int ANCESTOR_MOVED = 1401;
  
  public static final int ANCESTOR_RESIZED = 1402;
  
  public static final int HIERARCHY_LAST = 1402;
  
  public static final int PARENT_CHANGED = 1;
  
  public static final int DISPLAYABILITY_CHANGED = 2;
  
  public static final int SHOWING_CHANGED = 4;
  
  Component changed;
  
  Container changedParent;
  
  long changeFlags;
  
  public HierarchyEvent(Component paramComponent1, int paramInt, Component paramComponent2, Container paramContainer) {
    super(paramComponent1, paramInt);
    this.changed = paramComponent2;
    this.changedParent = paramContainer;
  }
  
  public HierarchyEvent(Component paramComponent1, int paramInt, Component paramComponent2, Container paramContainer, long paramLong) {
    super(paramComponent1, paramInt);
    this.changed = paramComponent2;
    this.changedParent = paramContainer;
    this.changeFlags = paramLong;
  }
  
  public Component getComponent() { return (this.source instanceof Component) ? (Component)this.source : null; }
  
  public Component getChanged() { return this.changed; }
  
  public Container getChangedParent() { return this.changedParent; }
  
  public long getChangeFlags() { return this.changeFlags; }
  
  public String paramString() {
    boolean bool;
    switch (this.id) {
      case 1401:
        return "ANCESTOR_MOVED (" + this.changed + "," + this.changedParent + ")";
      case 1402:
        return "ANCESTOR_RESIZED (" + this.changed + "," + this.changedParent + ")";
      case 1400:
        null = "HIERARCHY_CHANGED (";
        bool = true;
        if ((this.changeFlags & 0x1L) != 0L) {
          bool = false;
          null = null + "PARENT_CHANGED";
        } 
        if ((this.changeFlags & 0x2L) != 0L) {
          if (bool) {
            bool = false;
          } else {
            null = null + ",";
          } 
          null = null + "DISPLAYABILITY_CHANGED";
        } 
        if ((this.changeFlags & 0x4L) != 0L) {
          if (bool) {
            bool = false;
          } else {
            null = null + ",";
          } 
          null = null + "SHOWING_CHANGED";
        } 
        if (!bool)
          null = null + ","; 
        return null + this.changed + "," + this.changedParent + ")";
    } 
    return "unknown type";
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\event\HierarchyEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */