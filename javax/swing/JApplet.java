package javax.swing;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.LayoutManager;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import sun.awt.SunToolkit;

public class JApplet extends Applet implements Accessible, RootPaneContainer, TransferHandler.HasGetTransferHandler {
  protected JRootPane rootPane;
  
  protected boolean rootPaneCheckingEnabled = false;
  
  private TransferHandler transferHandler;
  
  protected AccessibleContext accessibleContext = null;
  
  public JApplet() throws HeadlessException {
    TimerQueue timerQueue = TimerQueue.sharedInstance();
    if (timerQueue != null)
      timerQueue.startIfNeeded(); 
    setForeground(Color.black);
    setBackground(Color.white);
    setLocale(JComponent.getDefaultLocale());
    setLayout(new BorderLayout());
    setRootPane(createRootPane());
    setRootPaneCheckingEnabled(true);
    setFocusTraversalPolicyProvider(true);
    SunToolkit.checkAndSetPolicy(this);
    enableEvents(8L);
  }
  
  protected JRootPane createRootPane() {
    JRootPane jRootPane = new JRootPane();
    jRootPane.setOpaque(true);
    return jRootPane;
  }
  
  public void setTransferHandler(TransferHandler paramTransferHandler) {
    TransferHandler transferHandler1 = this.transferHandler;
    this.transferHandler = paramTransferHandler;
    SwingUtilities.installSwingDropTargetAsNecessary(this, this.transferHandler);
    firePropertyChange("transferHandler", transferHandler1, paramTransferHandler);
  }
  
  public TransferHandler getTransferHandler() { return this.transferHandler; }
  
  public void update(Graphics paramGraphics) { paint(paramGraphics); }
  
  public void setJMenuBar(JMenuBar paramJMenuBar) { getRootPane().setMenuBar(paramJMenuBar); }
  
  public JMenuBar getJMenuBar() { return getRootPane().getMenuBar(); }
  
  protected boolean isRootPaneCheckingEnabled() { return this.rootPaneCheckingEnabled; }
  
  protected void setRootPaneCheckingEnabled(boolean paramBoolean) { this.rootPaneCheckingEnabled = paramBoolean; }
  
  protected void addImpl(Component paramComponent, Object paramObject, int paramInt) {
    if (isRootPaneCheckingEnabled()) {
      getContentPane().add(paramComponent, paramObject, paramInt);
    } else {
      super.addImpl(paramComponent, paramObject, paramInt);
    } 
  }
  
  public void remove(Component paramComponent) {
    if (paramComponent == this.rootPane) {
      super.remove(paramComponent);
    } else {
      getContentPane().remove(paramComponent);
    } 
  }
  
  public void setLayout(LayoutManager paramLayoutManager) {
    if (isRootPaneCheckingEnabled()) {
      getContentPane().setLayout(paramLayoutManager);
    } else {
      super.setLayout(paramLayoutManager);
    } 
  }
  
  public JRootPane getRootPane() { return this.rootPane; }
  
  protected void setRootPane(JRootPane paramJRootPane) {
    if (this.rootPane != null)
      remove(this.rootPane); 
    this.rootPane = paramJRootPane;
    if (this.rootPane != null) {
      bool = isRootPaneCheckingEnabled();
      try {
        setRootPaneCheckingEnabled(false);
        add(this.rootPane, "Center");
      } finally {
        setRootPaneCheckingEnabled(bool);
      } 
    } 
  }
  
  public Container getContentPane() { return getRootPane().getContentPane(); }
  
  public void setContentPane(Container paramContainer) { getRootPane().setContentPane(paramContainer); }
  
  public JLayeredPane getLayeredPane() { return getRootPane().getLayeredPane(); }
  
  public void setLayeredPane(JLayeredPane paramJLayeredPane) { getRootPane().setLayeredPane(paramJLayeredPane); }
  
  public Component getGlassPane() { return getRootPane().getGlassPane(); }
  
  public void setGlassPane(Component paramComponent) { getRootPane().setGlassPane(paramComponent); }
  
  public Graphics getGraphics() {
    JComponent.getGraphicsInvoked(this);
    return super.getGraphics();
  }
  
  public void repaint(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (RepaintManager.HANDLE_TOP_LEVEL_PAINT) {
      RepaintManager.currentManager(this).addDirtyRegion(this, paramInt1, paramInt2, paramInt3, paramInt4);
    } else {
      super.repaint(paramLong, paramInt1, paramInt2, paramInt3, paramInt4);
    } 
  }
  
  protected String paramString() {
    String str1 = (this.rootPane != null) ? this.rootPane.toString() : "";
    String str2 = this.rootPaneCheckingEnabled ? "true" : "false";
    return super.paramString() + ",rootPane=" + str1 + ",rootPaneCheckingEnabled=" + str2;
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleJApplet(); 
    return this.accessibleContext;
  }
  
  protected class AccessibleJApplet extends Applet.AccessibleApplet {
    protected AccessibleJApplet() { super(JApplet.this); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\JApplet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */