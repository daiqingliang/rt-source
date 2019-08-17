package javax.swing;

import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.plaf.PanelUI;

public class JPanel extends JComponent implements Accessible {
  private static final String uiClassID = "PanelUI";
  
  public JPanel(LayoutManager paramLayoutManager, boolean paramBoolean) {
    setLayout(paramLayoutManager);
    setDoubleBuffered(paramBoolean);
    setUIProperty("opaque", Boolean.TRUE);
    updateUI();
  }
  
  public JPanel(LayoutManager paramLayoutManager) { this(paramLayoutManager, true); }
  
  public JPanel(boolean paramBoolean) { this(new FlowLayout(), paramBoolean); }
  
  public JPanel() { this(true); }
  
  public void updateUI() { setUI((PanelUI)UIManager.getUI(this)); }
  
  public PanelUI getUI() { return (PanelUI)this.ui; }
  
  public void setUI(PanelUI paramPanelUI) { setUI(paramPanelUI); }
  
  public String getUIClassID() { return "PanelUI"; }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("PanelUI")) {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if (b == 0 && this.ui != null)
        this.ui.installUI(this); 
    } 
  }
  
  protected String paramString() { return super.paramString(); }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleJPanel(); 
    return this.accessibleContext;
  }
  
  protected class AccessibleJPanel extends JComponent.AccessibleJComponent {
    protected AccessibleJPanel() { super(JPanel.this); }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.PANEL; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\JPanel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */