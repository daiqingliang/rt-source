package java.awt.event;

import java.awt.Component;
import java.awt.Container;

public class ContainerEvent extends ComponentEvent {
  public static final int CONTAINER_FIRST = 300;
  
  public static final int CONTAINER_LAST = 301;
  
  public static final int COMPONENT_ADDED = 300;
  
  public static final int COMPONENT_REMOVED = 301;
  
  Component child;
  
  private static final long serialVersionUID = -4114942250539772041L;
  
  public ContainerEvent(Component paramComponent1, int paramInt, Component paramComponent2) {
    super(paramComponent1, paramInt);
    this.child = paramComponent2;
  }
  
  public Container getContainer() { return (this.source instanceof Container) ? (Container)this.source : null; }
  
  public Component getChild() { return this.child; }
  
  public String paramString() {
    switch (this.id) {
      case 300:
        str = "COMPONENT_ADDED";
        return str + ",child=" + this.child.getName();
      case 301:
        str = "COMPONENT_REMOVED";
        return str + ",child=" + this.child.getName();
    } 
    String str = "unknown type";
    return str + ",child=" + this.child.getName();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\event\ContainerEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */