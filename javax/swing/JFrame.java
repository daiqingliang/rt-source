package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.event.WindowEvent;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import sun.awt.SunToolkit;

public class JFrame extends Frame implements WindowConstants, Accessible, RootPaneContainer, TransferHandler.HasGetTransferHandler {
  public static final int EXIT_ON_CLOSE = 3;
  
  private static final Object defaultLookAndFeelDecoratedKey = new StringBuffer("JFrame.defaultLookAndFeelDecorated");
  
  private int defaultCloseOperation = 1;
  
  private TransferHandler transferHandler;
  
  protected JRootPane rootPane;
  
  protected boolean rootPaneCheckingEnabled = false;
  
  protected AccessibleContext accessibleContext = null;
  
  public JFrame() throws HeadlessException { frameInit(); }
  
  public JFrame(GraphicsConfiguration paramGraphicsConfiguration) {
    super(paramGraphicsConfiguration);
    frameInit();
  }
  
  public JFrame(String paramString) throws HeadlessException {
    super(paramString);
    frameInit();
  }
  
  public JFrame(String paramString, GraphicsConfiguration paramGraphicsConfiguration) {
    super(paramString, paramGraphicsConfiguration);
    frameInit();
  }
  
  protected void frameInit() throws HeadlessException {
    enableEvents(72L);
    setLocale(JComponent.getDefaultLocale());
    setRootPane(createRootPane());
    setBackground(UIManager.getColor("control"));
    setRootPaneCheckingEnabled(true);
    if (isDefaultLookAndFeelDecorated()) {
      boolean bool = UIManager.getLookAndFeel().getSupportsWindowDecorations();
      if (bool) {
        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(1);
      } 
    } 
    SunToolkit.checkAndSetPolicy(this);
  }
  
  protected JRootPane createRootPane() {
    JRootPane jRootPane = new JRootPane();
    jRootPane.setOpaque(true);
    return jRootPane;
  }
  
  protected void processWindowEvent(WindowEvent paramWindowEvent) {
    super.processWindowEvent(paramWindowEvent);
    if (paramWindowEvent.getID() == 201)
      switch (this.defaultCloseOperation) {
        case 1:
          setVisible(false);
          break;
        case 2:
          dispose();
          break;
        case 3:
          System.exit(0);
          break;
      }  
  }
  
  public void setDefaultCloseOperation(int paramInt) {
    if (paramInt != 0 && paramInt != 1 && paramInt != 2 && paramInt != 3)
      throw new IllegalArgumentException("defaultCloseOperation must be one of: DO_NOTHING_ON_CLOSE, HIDE_ON_CLOSE, DISPOSE_ON_CLOSE, or EXIT_ON_CLOSE"); 
    if (paramInt == 3) {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null)
        securityManager.checkExit(0); 
    } 
    if (this.defaultCloseOperation != paramInt) {
      int i = this.defaultCloseOperation;
      this.defaultCloseOperation = paramInt;
      firePropertyChange("defaultCloseOperation", i, paramInt);
    } 
  }
  
  public int getDefaultCloseOperation() { return this.defaultCloseOperation; }
  
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
  
  public void setIconImage(Image paramImage) { super.setIconImage(paramImage); }
  
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
  
  public static void setDefaultLookAndFeelDecorated(boolean paramBoolean) {
    if (paramBoolean) {
      SwingUtilities.appContextPut(defaultLookAndFeelDecoratedKey, Boolean.TRUE);
    } else {
      SwingUtilities.appContextPut(defaultLookAndFeelDecoratedKey, Boolean.FALSE);
    } 
  }
  
  public static boolean isDefaultLookAndFeelDecorated() {
    Boolean bool = (Boolean)SwingUtilities.appContextGet(defaultLookAndFeelDecoratedKey);
    if (bool == null)
      bool = Boolean.FALSE; 
    return bool.booleanValue();
  }
  
  protected String paramString() {
    String str1;
    if (this.defaultCloseOperation == 1) {
      str1 = "HIDE_ON_CLOSE";
    } else if (this.defaultCloseOperation == 2) {
      str1 = "DISPOSE_ON_CLOSE";
    } else if (this.defaultCloseOperation == 0) {
      str1 = "DO_NOTHING_ON_CLOSE";
    } else if (this.defaultCloseOperation == 3) {
      str1 = "EXIT_ON_CLOSE";
    } else {
      str1 = "";
    } 
    String str2 = (this.rootPane != null) ? this.rootPane.toString() : "";
    String str3 = this.rootPaneCheckingEnabled ? "true" : "false";
    return super.paramString() + ",defaultCloseOperation=" + str1 + ",rootPane=" + str2 + ",rootPaneCheckingEnabled=" + str3;
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleJFrame(); 
    return this.accessibleContext;
  }
  
  protected class AccessibleJFrame extends Frame.AccessibleAWTFrame {
    protected AccessibleJFrame() { super(JFrame.this); }
    
    public String getAccessibleName() { return (this.accessibleName != null) ? this.accessibleName : ((JFrame.this.getTitle() == null) ? super.getAccessibleName() : JFrame.this.getTitle()); }
    
    public AccessibleStateSet getAccessibleStateSet() {
      AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
      if (JFrame.this.isResizable())
        accessibleStateSet.add(AccessibleState.RESIZABLE); 
      if (JFrame.this.getFocusOwner() != null)
        accessibleStateSet.add(AccessibleState.ACTIVE); 
      return accessibleStateSet;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\JFrame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */