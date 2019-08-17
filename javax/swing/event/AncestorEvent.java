package javax.swing.event;

import java.awt.AWTEvent;
import java.awt.Container;
import javax.swing.JComponent;

public class AncestorEvent extends AWTEvent {
  public static final int ANCESTOR_ADDED = 1;
  
  public static final int ANCESTOR_REMOVED = 2;
  
  public static final int ANCESTOR_MOVED = 3;
  
  Container ancestor;
  
  Container ancestorParent;
  
  public AncestorEvent(JComponent paramJComponent, int paramInt, Container paramContainer1, Container paramContainer2) {
    super(paramJComponent, paramInt);
    this.ancestor = paramContainer1;
    this.ancestorParent = paramContainer2;
  }
  
  public Container getAncestor() { return this.ancestor; }
  
  public Container getAncestorParent() { return this.ancestorParent; }
  
  public JComponent getComponent() { return (JComponent)getSource(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\event\AncestorEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */