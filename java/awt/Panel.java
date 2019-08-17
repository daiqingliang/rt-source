package java.awt;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;

public class Panel extends Container implements Accessible {
  private static final String base = "panel";
  
  private static int nameCounter = 0;
  
  private static final long serialVersionUID = -2728009084054400034L;
  
  public Panel() { this(new FlowLayout()); }
  
  public Panel(LayoutManager paramLayoutManager) { setLayout(paramLayoutManager); }
  
  String constructComponentName() {
    synchronized (Panel.class) {
      return "panel" + nameCounter++;
    } 
  }
  
  public void addNotify() {
    synchronized (getTreeLock()) {
      if (this.peer == null)
        this.peer = getToolkit().createPanel(this); 
      super.addNotify();
    } 
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleAWTPanel(); 
    return this.accessibleContext;
  }
  
  protected class AccessibleAWTPanel extends Container.AccessibleAWTContainer {
    private static final long serialVersionUID = -6409552226660031050L;
    
    protected AccessibleAWTPanel() { super(Panel.this); }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.PANEL; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\Panel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */