package javax.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.IllegalComponentStateException;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.security.AccessController;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.plaf.RootPaneUI;
import sun.awt.AWTAccessor;
import sun.security.action.GetBooleanAction;

public class JRootPane extends JComponent implements Accessible {
  private static final String uiClassID = "RootPaneUI";
  
  private static final boolean LOG_DISABLE_TRUE_DOUBLE_BUFFERING = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("swing.logDoubleBufferingDisable"))).booleanValue();
  
  private static final boolean IGNORE_DISABLE_TRUE_DOUBLE_BUFFERING = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("swing.ignoreDoubleBufferingDisable"))).booleanValue();
  
  public static final int NONE = 0;
  
  public static final int FRAME = 1;
  
  public static final int PLAIN_DIALOG = 2;
  
  public static final int INFORMATION_DIALOG = 3;
  
  public static final int ERROR_DIALOG = 4;
  
  public static final int COLOR_CHOOSER_DIALOG = 5;
  
  public static final int FILE_CHOOSER_DIALOG = 6;
  
  public static final int QUESTION_DIALOG = 7;
  
  public static final int WARNING_DIALOG = 8;
  
  private int windowDecorationStyle;
  
  protected JMenuBar menuBar;
  
  protected Container contentPane;
  
  protected JLayeredPane layeredPane;
  
  protected Component glassPane;
  
  protected JButton defaultButton;
  
  @Deprecated
  protected DefaultAction defaultPressAction;
  
  @Deprecated
  protected DefaultAction defaultReleaseAction;
  
  boolean useTrueDoubleBuffering = true;
  
  public JRootPane() {
    setGlassPane(createGlassPane());
    setLayeredPane(createLayeredPane());
    setContentPane(createContentPane());
    setLayout(createRootLayout());
    setDoubleBuffered(true);
    updateUI();
  }
  
  public void setDoubleBuffered(boolean paramBoolean) {
    if (isDoubleBuffered() != paramBoolean) {
      super.setDoubleBuffered(paramBoolean);
      RepaintManager.currentManager(this).doubleBufferingChanged(this);
    } 
  }
  
  public int getWindowDecorationStyle() { return this.windowDecorationStyle; }
  
  public void setWindowDecorationStyle(int paramInt) {
    if (paramInt < 0 || paramInt > 8)
      throw new IllegalArgumentException("Invalid decoration style"); 
    int i = getWindowDecorationStyle();
    this.windowDecorationStyle = paramInt;
    firePropertyChange("windowDecorationStyle", i, paramInt);
  }
  
  public RootPaneUI getUI() { return (RootPaneUI)this.ui; }
  
  public void setUI(RootPaneUI paramRootPaneUI) { setUI(paramRootPaneUI); }
  
  public void updateUI() { setUI((RootPaneUI)UIManager.getUI(this)); }
  
  public String getUIClassID() { return "RootPaneUI"; }
  
  protected JLayeredPane createLayeredPane() {
    JLayeredPane jLayeredPane = new JLayeredPane();
    jLayeredPane.setName(getName() + ".layeredPane");
    return jLayeredPane;
  }
  
  protected Container createContentPane() {
    JPanel jPanel = new JPanel();
    jPanel.setName(getName() + ".contentPane");
    jPanel.setLayout(new BorderLayout() {
          public void addLayoutComponent(Component param1Component, Object param1Object) {
            if (param1Object == null)
              param1Object = "Center"; 
            super.addLayoutComponent(param1Component, param1Object);
          }
        });
    return jPanel;
  }
  
  protected Component createGlassPane() {
    JPanel jPanel = new JPanel();
    jPanel.setName(getName() + ".glassPane");
    jPanel.setVisible(false);
    ((JPanel)jPanel).setOpaque(false);
    return jPanel;
  }
  
  protected LayoutManager createRootLayout() { return new RootLayout(); }
  
  public void setJMenuBar(JMenuBar paramJMenuBar) {
    if (this.menuBar != null && this.menuBar.getParent() == this.layeredPane)
      this.layeredPane.remove(this.menuBar); 
    this.menuBar = paramJMenuBar;
    if (this.menuBar != null)
      this.layeredPane.add(this.menuBar, JLayeredPane.FRAME_CONTENT_LAYER); 
  }
  
  @Deprecated
  public void setMenuBar(JMenuBar paramJMenuBar) {
    if (this.menuBar != null && this.menuBar.getParent() == this.layeredPane)
      this.layeredPane.remove(this.menuBar); 
    this.menuBar = paramJMenuBar;
    if (this.menuBar != null)
      this.layeredPane.add(this.menuBar, JLayeredPane.FRAME_CONTENT_LAYER); 
  }
  
  public JMenuBar getJMenuBar() { return this.menuBar; }
  
  @Deprecated
  public JMenuBar getMenuBar() { return this.menuBar; }
  
  public void setContentPane(Container paramContainer) {
    if (paramContainer == null)
      throw new IllegalComponentStateException("contentPane cannot be set to null."); 
    if (this.contentPane != null && this.contentPane.getParent() == this.layeredPane)
      this.layeredPane.remove(this.contentPane); 
    this.contentPane = paramContainer;
    this.layeredPane.add(this.contentPane, JLayeredPane.FRAME_CONTENT_LAYER);
  }
  
  public Container getContentPane() { return this.contentPane; }
  
  public void setLayeredPane(JLayeredPane paramJLayeredPane) {
    if (paramJLayeredPane == null)
      throw new IllegalComponentStateException("layeredPane cannot be set to null."); 
    if (this.layeredPane != null && this.layeredPane.getParent() == this)
      remove(this.layeredPane); 
    this.layeredPane = paramJLayeredPane;
    add(this.layeredPane, -1);
  }
  
  public JLayeredPane getLayeredPane() { return this.layeredPane; }
  
  public void setGlassPane(Component paramComponent) {
    if (paramComponent == null)
      throw new NullPointerException("glassPane cannot be set to null."); 
    AWTAccessor.getComponentAccessor().setMixingCutoutShape(paramComponent, new Rectangle());
    boolean bool = false;
    if (this.glassPane != null && this.glassPane.getParent() == this) {
      remove(this.glassPane);
      bool = this.glassPane.isVisible();
    } 
    paramComponent.setVisible(bool);
    this.glassPane = paramComponent;
    add(this.glassPane, 0);
    if (bool)
      repaint(); 
  }
  
  public Component getGlassPane() { return this.glassPane; }
  
  public boolean isValidateRoot() { return true; }
  
  public boolean isOptimizedDrawingEnabled() { return !this.glassPane.isVisible(); }
  
  public void addNotify() {
    super.addNotify();
    enableEvents(8L);
  }
  
  public void removeNotify() { super.removeNotify(); }
  
  public void setDefaultButton(JButton paramJButton) {
    JButton jButton = this.defaultButton;
    if (jButton != paramJButton) {
      this.defaultButton = paramJButton;
      if (jButton != null)
        jButton.repaint(); 
      if (paramJButton != null)
        paramJButton.repaint(); 
    } 
    firePropertyChange("defaultButton", jButton, paramJButton);
  }
  
  public JButton getDefaultButton() { return this.defaultButton; }
  
  final void setUseTrueDoubleBuffering(boolean paramBoolean) { this.useTrueDoubleBuffering = paramBoolean; }
  
  final boolean getUseTrueDoubleBuffering() { return this.useTrueDoubleBuffering; }
  
  final void disableTrueDoubleBuffering() {
    if (this.useTrueDoubleBuffering && !IGNORE_DISABLE_TRUE_DOUBLE_BUFFERING) {
      if (LOG_DISABLE_TRUE_DOUBLE_BUFFERING) {
        System.out.println("Disabling true double buffering for " + this);
        Thread.dumpStack();
      } 
      this.useTrueDoubleBuffering = false;
      RepaintManager.currentManager(this).doubleBufferingChanged(this);
    } 
  }
  
  protected void addImpl(Component paramComponent, Object paramObject, int paramInt) {
    super.addImpl(paramComponent, paramObject, paramInt);
    if (this.glassPane != null && this.glassPane.getParent() == this && getComponent(false) != this.glassPane)
      add(this.glassPane, 0); 
  }
  
  protected String paramString() { return super.paramString(); }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleJRootPane(); 
    return this.accessibleContext;
  }
  
  protected class AccessibleJRootPane extends JComponent.AccessibleJComponent {
    protected AccessibleJRootPane() { super(JRootPane.this); }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.ROOT_PANE; }
    
    public int getAccessibleChildrenCount() { return super.getAccessibleChildrenCount(); }
    
    public Accessible getAccessibleChild(int param1Int) { return super.getAccessibleChild(param1Int); }
  }
  
  static class DefaultAction extends AbstractAction {
    JButton owner;
    
    JRootPane root;
    
    boolean press;
    
    DefaultAction(JRootPane param1JRootPane, boolean param1Boolean) {
      this.root = param1JRootPane;
      this.press = param1Boolean;
    }
    
    public void setOwner(JButton param1JButton) { this.owner = param1JButton; }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      if (this.owner != null && SwingUtilities.getRootPane(this.owner) == this.root) {
        ButtonModel buttonModel = this.owner.getModel();
        if (this.press) {
          buttonModel.setArmed(true);
          buttonModel.setPressed(true);
        } else {
          buttonModel.setPressed(false);
        } 
      } 
    }
    
    public boolean isEnabled() { return this.owner.getModel().isEnabled(); }
  }
  
  protected class RootLayout implements LayoutManager2, Serializable {
    public Dimension preferredLayoutSize(Container param1Container) {
      Dimension dimension2;
      Dimension dimension1;
      Insets insets = JRootPane.this.getInsets();
      if (JRootPane.this.contentPane != null) {
        dimension1 = JRootPane.this.contentPane.getPreferredSize();
      } else {
        dimension1 = param1Container.getSize();
      } 
      if (JRootPane.this.menuBar != null && JRootPane.this.menuBar.isVisible()) {
        dimension2 = JRootPane.this.menuBar.getPreferredSize();
      } else {
        dimension2 = new Dimension(0, 0);
      } 
      return new Dimension(Math.max(dimension1.width, dimension2.width) + insets.left + insets.right, dimension1.height + dimension2.height + insets.top + insets.bottom);
    }
    
    public Dimension minimumLayoutSize(Container param1Container) {
      Dimension dimension2;
      Dimension dimension1;
      Insets insets = JRootPane.this.getInsets();
      if (JRootPane.this.contentPane != null) {
        dimension1 = JRootPane.this.contentPane.getMinimumSize();
      } else {
        dimension1 = param1Container.getSize();
      } 
      if (JRootPane.this.menuBar != null && JRootPane.this.menuBar.isVisible()) {
        dimension2 = JRootPane.this.menuBar.getMinimumSize();
      } else {
        dimension2 = new Dimension(0, 0);
      } 
      return new Dimension(Math.max(dimension1.width, dimension2.width) + insets.left + insets.right, dimension1.height + dimension2.height + insets.top + insets.bottom);
    }
    
    public Dimension maximumLayoutSize(Container param1Container) {
      Dimension dimension2;
      Dimension dimension1;
      Insets insets = JRootPane.this.getInsets();
      if (JRootPane.this.menuBar != null && JRootPane.this.menuBar.isVisible()) {
        dimension2 = JRootPane.this.menuBar.getMaximumSize();
      } else {
        dimension2 = new Dimension(0, 0);
      } 
      if (JRootPane.this.contentPane != null) {
        dimension1 = JRootPane.this.contentPane.getMaximumSize();
      } else {
        dimension1 = new Dimension(2147483647, Integer.MAX_VALUE - insets.top - insets.bottom - dimension2.height - 1);
      } 
      return new Dimension(Math.min(dimension1.width, dimension2.width) + insets.left + insets.right, dimension1.height + dimension2.height + insets.top + insets.bottom);
    }
    
    public void layoutContainer(Container param1Container) {
      Rectangle rectangle = param1Container.getBounds();
      Insets insets = JRootPane.this.getInsets();
      int i = 0;
      int j = rectangle.width - insets.right - insets.left;
      int k = rectangle.height - insets.top - insets.bottom;
      if (JRootPane.this.layeredPane != null)
        JRootPane.this.layeredPane.setBounds(insets.left, insets.top, j, k); 
      if (JRootPane.this.glassPane != null)
        JRootPane.this.glassPane.setBounds(insets.left, insets.top, j, k); 
      if (JRootPane.this.menuBar != null && JRootPane.this.menuBar.isVisible()) {
        Dimension dimension = JRootPane.this.menuBar.getPreferredSize();
        JRootPane.this.menuBar.setBounds(0, 0, j, dimension.height);
        i += dimension.height;
      } 
      if (JRootPane.this.contentPane != null)
        JRootPane.this.contentPane.setBounds(0, i, j, k - i); 
    }
    
    public void addLayoutComponent(String param1String, Component param1Component) {}
    
    public void removeLayoutComponent(Component param1Component) {}
    
    public void addLayoutComponent(Component param1Component, Object param1Object) {}
    
    public float getLayoutAlignmentX(Container param1Container) { return 0.0F; }
    
    public float getLayoutAlignmentY(Container param1Container) { return 0.0F; }
    
    public void invalidateLayout(Container param1Container) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\JRootPane.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */