package javax.swing;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Objects;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.plaf.ToolTipUI;

public class JToolTip extends JComponent implements Accessible {
  private static final String uiClassID = "ToolTipUI";
  
  String tipText;
  
  JComponent component;
  
  public JToolTip() {
    setOpaque(true);
    updateUI();
  }
  
  public ToolTipUI getUI() { return (ToolTipUI)this.ui; }
  
  public void updateUI() { setUI((ToolTipUI)UIManager.getUI(this)); }
  
  public String getUIClassID() { return "ToolTipUI"; }
  
  public void setTipText(String paramString) {
    String str = this.tipText;
    this.tipText = paramString;
    firePropertyChange("tiptext", str, paramString);
    if (!Objects.equals(str, paramString)) {
      revalidate();
      repaint();
    } 
  }
  
  public String getTipText() { return this.tipText; }
  
  public void setComponent(JComponent paramJComponent) {
    JComponent jComponent = this.component;
    this.component = paramJComponent;
    firePropertyChange("component", jComponent, paramJComponent);
  }
  
  public JComponent getComponent() { return this.component; }
  
  boolean alwaysOnTop() { return true; }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("ToolTipUI")) {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if (b == 0 && this.ui != null)
        this.ui.installUI(this); 
    } 
  }
  
  protected String paramString() {
    String str = (this.tipText != null) ? this.tipText : "";
    return super.paramString() + ",tipText=" + str;
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleJToolTip(); 
    return this.accessibleContext;
  }
  
  protected class AccessibleJToolTip extends JComponent.AccessibleJComponent {
    protected AccessibleJToolTip() { super(JToolTip.this); }
    
    public String getAccessibleDescription() {
      String str = this.accessibleDescription;
      if (str == null)
        str = (String)JToolTip.this.getClientProperty("AccessibleDescription"); 
      if (str == null)
        str = JToolTip.this.getTipText(); 
      return str;
    }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.TOOL_TIP; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\JToolTip.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */