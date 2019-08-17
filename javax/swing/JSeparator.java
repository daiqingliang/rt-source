package javax.swing;

import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.plaf.SeparatorUI;

public class JSeparator extends JComponent implements SwingConstants, Accessible {
  private static final String uiClassID = "SeparatorUI";
  
  private int orientation = 0;
  
  public JSeparator() { this(0); }
  
  public JSeparator(int paramInt) {
    checkOrientation(paramInt);
    this.orientation = paramInt;
    setFocusable(false);
    updateUI();
  }
  
  public SeparatorUI getUI() { return (SeparatorUI)this.ui; }
  
  public void setUI(SeparatorUI paramSeparatorUI) { setUI(paramSeparatorUI); }
  
  public void updateUI() { setUI((SeparatorUI)UIManager.getUI(this)); }
  
  public String getUIClassID() { return "SeparatorUI"; }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("SeparatorUI")) {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if (b == 0 && this.ui != null)
        this.ui.installUI(this); 
    } 
  }
  
  public int getOrientation() { return this.orientation; }
  
  public void setOrientation(int paramInt) {
    if (this.orientation == paramInt)
      return; 
    int i = this.orientation;
    checkOrientation(paramInt);
    this.orientation = paramInt;
    firePropertyChange("orientation", i, paramInt);
    revalidate();
    repaint();
  }
  
  private void checkOrientation(int paramInt) {
    switch (paramInt) {
      case 0:
      case 1:
        return;
    } 
    throw new IllegalArgumentException("orientation must be one of: VERTICAL, HORIZONTAL");
  }
  
  protected String paramString() {
    String str = (this.orientation == 0) ? "HORIZONTAL" : "VERTICAL";
    return super.paramString() + ",orientation=" + str;
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleJSeparator(); 
    return this.accessibleContext;
  }
  
  protected class AccessibleJSeparator extends JComponent.AccessibleJComponent {
    protected AccessibleJSeparator() { super(JSeparator.this); }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.SEPARATOR; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\JSeparator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */