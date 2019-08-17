package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.LayoutManager;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import sun.awt.SunToolkit;

public class JDialog extends Dialog implements WindowConstants, Accessible, RootPaneContainer, TransferHandler.HasGetTransferHandler {
  private static final Object defaultLookAndFeelDecoratedKey = new StringBuffer("JDialog.defaultLookAndFeelDecorated");
  
  private int defaultCloseOperation = 1;
  
  protected JRootPane rootPane;
  
  protected boolean rootPaneCheckingEnabled = false;
  
  private TransferHandler transferHandler;
  
  protected AccessibleContext accessibleContext = null;
  
  public JDialog() { this((Frame)null, false); }
  
  public JDialog(Frame paramFrame) { this(paramFrame, false); }
  
  public JDialog(Frame paramFrame, boolean paramBoolean) { this(paramFrame, "", paramBoolean); }
  
  public JDialog(Frame paramFrame, String paramString) { this(paramFrame, paramString, false); }
  
  public JDialog(Frame paramFrame, String paramString, boolean paramBoolean) {
    super((paramFrame == null) ? SwingUtilities.getSharedOwnerFrame() : paramFrame, paramString, paramBoolean);
    if (paramFrame == null) {
      WindowListener windowListener = SwingUtilities.getSharedOwnerFrameShutdownListener();
      addWindowListener(windowListener);
    } 
    dialogInit();
  }
  
  public JDialog(Frame paramFrame, String paramString, boolean paramBoolean, GraphicsConfiguration paramGraphicsConfiguration) {
    super((paramFrame == null) ? SwingUtilities.getSharedOwnerFrame() : paramFrame, paramString, paramBoolean, paramGraphicsConfiguration);
    if (paramFrame == null) {
      WindowListener windowListener = SwingUtilities.getSharedOwnerFrameShutdownListener();
      addWindowListener(windowListener);
    } 
    dialogInit();
  }
  
  public JDialog(Dialog paramDialog) { this(paramDialog, false); }
  
  public JDialog(Dialog paramDialog, boolean paramBoolean) { this(paramDialog, "", paramBoolean); }
  
  public JDialog(Dialog paramDialog, String paramString) { this(paramDialog, paramString, false); }
  
  public JDialog(Dialog paramDialog, String paramString, boolean paramBoolean) {
    super(paramDialog, paramString, paramBoolean);
    dialogInit();
  }
  
  public JDialog(Dialog paramDialog, String paramString, boolean paramBoolean, GraphicsConfiguration paramGraphicsConfiguration) {
    super(paramDialog, paramString, paramBoolean, paramGraphicsConfiguration);
    dialogInit();
  }
  
  public JDialog(Window paramWindow) { this(paramWindow, Dialog.ModalityType.MODELESS); }
  
  public JDialog(Window paramWindow, Dialog.ModalityType paramModalityType) { this(paramWindow, "", paramModalityType); }
  
  public JDialog(Window paramWindow, String paramString) { this(paramWindow, paramString, Dialog.ModalityType.MODELESS); }
  
  public JDialog(Window paramWindow, String paramString, Dialog.ModalityType paramModalityType) {
    super(paramWindow, paramString, paramModalityType);
    dialogInit();
  }
  
  public JDialog(Window paramWindow, String paramString, Dialog.ModalityType paramModalityType, GraphicsConfiguration paramGraphicsConfiguration) {
    super(paramWindow, paramString, paramModalityType, paramGraphicsConfiguration);
    dialogInit();
  }
  
  protected void dialogInit() {
    enableEvents(72L);
    setLocale(JComponent.getDefaultLocale());
    setRootPane(createRootPane());
    setBackground(UIManager.getColor("control"));
    setRootPaneCheckingEnabled(true);
    if (isDefaultLookAndFeelDecorated()) {
      boolean bool = UIManager.getLookAndFeel().getSupportsWindowDecorations();
      if (bool) {
        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(2);
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
      }  
  }
  
  public void setDefaultCloseOperation(int paramInt) {
    if (paramInt != 0 && paramInt != 1 && paramInt != 2)
      throw new IllegalArgumentException("defaultCloseOperation must be one of: DO_NOTHING_ON_CLOSE, HIDE_ON_CLOSE, or DISPOSE_ON_CLOSE"); 
    int i = this.defaultCloseOperation;
    this.defaultCloseOperation = paramInt;
    firePropertyChange("defaultCloseOperation", i, paramInt);
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
    } else {
      str1 = "";
    } 
    String str2 = (this.rootPane != null) ? this.rootPane.toString() : "";
    String str3 = this.rootPaneCheckingEnabled ? "true" : "false";
    return super.paramString() + ",defaultCloseOperation=" + str1 + ",rootPane=" + str2 + ",rootPaneCheckingEnabled=" + str3;
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleJDialog(); 
    return this.accessibleContext;
  }
  
  protected class AccessibleJDialog extends Dialog.AccessibleAWTDialog {
    protected AccessibleJDialog() { super(JDialog.this); }
    
    public String getAccessibleName() { return (this.accessibleName != null) ? this.accessibleName : ((JDialog.this.getTitle() == null) ? super.getAccessibleName() : JDialog.this.getTitle()); }
    
    public AccessibleStateSet getAccessibleStateSet() {
      AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
      if (JDialog.this.isResizable())
        accessibleStateSet.add(AccessibleState.RESIZABLE); 
      if (JDialog.this.getFocusOwner() != null)
        accessibleStateSet.add(AccessibleState.ACTIVE); 
      if (JDialog.this.isModal())
        accessibleStateSet.add(AccessibleState.MODAL); 
      return accessibleStateSet;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\JDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */