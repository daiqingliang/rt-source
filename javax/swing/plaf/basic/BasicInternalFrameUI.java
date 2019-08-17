package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.IllegalComponentStateException;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import javax.swing.DefaultDesktopManager;
import javax.swing.DesktopManager;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.RootPaneContainer;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.InternalFrameUI;
import sun.swing.DefaultLookup;
import sun.swing.UIAction;

public class BasicInternalFrameUI extends InternalFrameUI {
  protected JInternalFrame frame;
  
  private Handler handler;
  
  protected MouseInputAdapter borderListener;
  
  protected PropertyChangeListener propertyChangeListener;
  
  protected LayoutManager internalFrameLayout;
  
  protected ComponentListener componentListener;
  
  protected MouseInputListener glassPaneDispatcher;
  
  private InternalFrameListener internalFrameListener;
  
  protected JComponent northPane;
  
  protected JComponent southPane;
  
  protected JComponent westPane;
  
  protected JComponent eastPane;
  
  protected BasicInternalFrameTitlePane titlePane;
  
  private static DesktopManager sharedDesktopManager;
  
  private boolean componentListenerAdded = false;
  
  private Rectangle parentBounds;
  
  private boolean dragging = false;
  
  private boolean resizing = false;
  
  @Deprecated
  protected KeyStroke openMenuKey;
  
  private boolean keyBindingRegistered = false;
  
  private boolean keyBindingActive = false;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new BasicInternalFrameUI((JInternalFrame)paramJComponent); }
  
  public BasicInternalFrameUI(JInternalFrame paramJInternalFrame) {
    LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
    if (lookAndFeel instanceof BasicLookAndFeel)
      ((BasicLookAndFeel)lookAndFeel).installAWTEventListener(); 
  }
  
  public void installUI(JComponent paramJComponent) {
    this.frame = (JInternalFrame)paramJComponent;
    installDefaults();
    installListeners();
    installComponents();
    installKeyboardActions();
    LookAndFeel.installProperty(this.frame, "opaque", Boolean.TRUE);
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    if (paramJComponent != this.frame)
      throw new IllegalComponentStateException(this + " was asked to deinstall() " + paramJComponent + " when it only knows about " + this.frame + "."); 
    uninstallKeyboardActions();
    uninstallComponents();
    uninstallListeners();
    uninstallDefaults();
    updateFrameCursor();
    this.handler = null;
    this.frame = null;
  }
  
  protected void installDefaults() {
    Icon icon = this.frame.getFrameIcon();
    if (icon == null || icon instanceof javax.swing.plaf.UIResource)
      this.frame.setFrameIcon(UIManager.getIcon("InternalFrame.icon")); 
    Container container = this.frame.getContentPane();
    if (container != null) {
      Color color = container.getBackground();
      if (color instanceof javax.swing.plaf.UIResource)
        container.setBackground(null); 
    } 
    this.frame.setLayout(this.internalFrameLayout = createLayoutManager());
    this.frame.setBackground(UIManager.getLookAndFeelDefaults().getColor("control"));
    LookAndFeel.installBorder(this.frame, "InternalFrame.border");
  }
  
  protected void installKeyboardActions() {
    createInternalFrameListener();
    if (this.internalFrameListener != null)
      this.frame.addInternalFrameListener(this.internalFrameListener); 
    LazyActionMap.installLazyActionMap(this.frame, BasicInternalFrameUI.class, "InternalFrame.actionMap");
  }
  
  static void loadActionMap(LazyActionMap paramLazyActionMap) {
    paramLazyActionMap.put(new UIAction("showSystemMenu") {
          public void actionPerformed(ActionEvent param1ActionEvent) {
            JInternalFrame jInternalFrame = (JInternalFrame)param1ActionEvent.getSource();
            if (jInternalFrame.getUI() instanceof BasicInternalFrameUI) {
              JComponent jComponent = ((BasicInternalFrameUI)jInternalFrame.getUI()).getNorthPane();
              if (jComponent instanceof BasicInternalFrameTitlePane)
                ((BasicInternalFrameTitlePane)jComponent).showSystemMenu(); 
            } 
          }
          
          public boolean isEnabled(Object param1Object) {
            if (param1Object instanceof JInternalFrame) {
              JInternalFrame jInternalFrame = (JInternalFrame)param1Object;
              if (jInternalFrame.getUI() instanceof BasicInternalFrameUI)
                return ((BasicInternalFrameUI)jInternalFrame.getUI()).isKeyBindingActive(); 
            } 
            return false;
          }
        });
    BasicLookAndFeel.installAudioActionMap(paramLazyActionMap);
  }
  
  protected void installComponents() {
    setNorthPane(createNorthPane(this.frame));
    setSouthPane(createSouthPane(this.frame));
    setEastPane(createEastPane(this.frame));
    setWestPane(createWestPane(this.frame));
  }
  
  protected void installListeners() {
    this.borderListener = createBorderListener(this.frame);
    this.propertyChangeListener = createPropertyChangeListener();
    this.frame.addPropertyChangeListener(this.propertyChangeListener);
    installMouseHandlers(this.frame);
    this.glassPaneDispatcher = createGlassPaneDispatcher();
    if (this.glassPaneDispatcher != null) {
      this.frame.getGlassPane().addMouseListener(this.glassPaneDispatcher);
      this.frame.getGlassPane().addMouseMotionListener(this.glassPaneDispatcher);
    } 
    this.componentListener = createComponentListener();
    if (this.frame.getParent() != null)
      this.parentBounds = this.frame.getParent().getBounds(); 
    if (this.frame.getParent() != null && !this.componentListenerAdded) {
      this.frame.getParent().addComponentListener(this.componentListener);
      this.componentListenerAdded = true;
    } 
  }
  
  private WindowFocusListener getWindowFocusListener() { return getHandler(); }
  
  private void cancelResize() {
    if (this.resizing && this.borderListener instanceof BorderListener)
      ((BorderListener)this.borderListener).finishMouseReleased(); 
  }
  
  private Handler getHandler() {
    if (this.handler == null)
      this.handler = new Handler(null); 
    return this.handler;
  }
  
  InputMap getInputMap(int paramInt) { return (paramInt == 2) ? createInputMap(paramInt) : null; }
  
  InputMap createInputMap(int paramInt) {
    if (paramInt == 2) {
      Object[] arrayOfObject = (Object[])DefaultLookup.get(this.frame, this, "InternalFrame.windowBindings");
      if (arrayOfObject != null)
        return LookAndFeel.makeComponentInputMap(this.frame, arrayOfObject); 
    } 
    return null;
  }
  
  protected void uninstallDefaults() {
    Icon icon = this.frame.getFrameIcon();
    if (icon instanceof javax.swing.plaf.UIResource)
      this.frame.setFrameIcon(null); 
    this.internalFrameLayout = null;
    this.frame.setLayout(null);
    LookAndFeel.uninstallBorder(this.frame);
  }
  
  protected void uninstallComponents() {
    setNorthPane(null);
    setSouthPane(null);
    setEastPane(null);
    setWestPane(null);
    if (this.titlePane != null)
      this.titlePane.uninstallDefaults(); 
    this.titlePane = null;
  }
  
  protected void uninstallListeners() {
    if (this.frame.getParent() != null && this.componentListenerAdded) {
      this.frame.getParent().removeComponentListener(this.componentListener);
      this.componentListenerAdded = false;
    } 
    this.componentListener = null;
    if (this.glassPaneDispatcher != null) {
      this.frame.getGlassPane().removeMouseListener(this.glassPaneDispatcher);
      this.frame.getGlassPane().removeMouseMotionListener(this.glassPaneDispatcher);
      this.glassPaneDispatcher = null;
    } 
    deinstallMouseHandlers(this.frame);
    this.frame.removePropertyChangeListener(this.propertyChangeListener);
    this.propertyChangeListener = null;
    this.borderListener = null;
  }
  
  protected void uninstallKeyboardActions() {
    if (this.internalFrameListener != null)
      this.frame.removeInternalFrameListener(this.internalFrameListener); 
    this.internalFrameListener = null;
    SwingUtilities.replaceUIInputMap(this.frame, 2, null);
    SwingUtilities.replaceUIActionMap(this.frame, null);
  }
  
  void updateFrameCursor() {
    if (this.resizing)
      return; 
    Cursor cursor = this.frame.getLastCursor();
    if (cursor == null)
      cursor = Cursor.getPredefinedCursor(0); 
    this.frame.setCursor(cursor);
  }
  
  protected LayoutManager createLayoutManager() { return getHandler(); }
  
  protected PropertyChangeListener createPropertyChangeListener() { return getHandler(); }
  
  public Dimension getPreferredSize(JComponent paramJComponent) { return (this.frame == paramJComponent) ? this.frame.getLayout().preferredLayoutSize(paramJComponent) : new Dimension(100, 100); }
  
  public Dimension getMinimumSize(JComponent paramJComponent) { return (this.frame == paramJComponent) ? this.frame.getLayout().minimumLayoutSize(paramJComponent) : new Dimension(0, 0); }
  
  public Dimension getMaximumSize(JComponent paramJComponent) { return new Dimension(2147483647, 2147483647); }
  
  protected void replacePane(JComponent paramJComponent1, JComponent paramJComponent2) {
    if (paramJComponent1 != null) {
      deinstallMouseHandlers(paramJComponent1);
      this.frame.remove(paramJComponent1);
    } 
    if (paramJComponent2 != null) {
      this.frame.add(paramJComponent2);
      installMouseHandlers(paramJComponent2);
    } 
  }
  
  protected void deinstallMouseHandlers(JComponent paramJComponent) {
    paramJComponent.removeMouseListener(this.borderListener);
    paramJComponent.removeMouseMotionListener(this.borderListener);
  }
  
  protected void installMouseHandlers(JComponent paramJComponent) {
    paramJComponent.addMouseListener(this.borderListener);
    paramJComponent.addMouseMotionListener(this.borderListener);
  }
  
  protected JComponent createNorthPane(JInternalFrame paramJInternalFrame) {
    this.titlePane = new BasicInternalFrameTitlePane(paramJInternalFrame);
    return this.titlePane;
  }
  
  protected JComponent createSouthPane(JInternalFrame paramJInternalFrame) { return null; }
  
  protected JComponent createWestPane(JInternalFrame paramJInternalFrame) { return null; }
  
  protected JComponent createEastPane(JInternalFrame paramJInternalFrame) { return null; }
  
  protected MouseInputAdapter createBorderListener(JInternalFrame paramJInternalFrame) { return new BorderListener(); }
  
  protected void createInternalFrameListener() { this.internalFrameListener = getHandler(); }
  
  protected final boolean isKeyBindingRegistered() { return this.keyBindingRegistered; }
  
  protected final void setKeyBindingRegistered(boolean paramBoolean) { this.keyBindingRegistered = paramBoolean; }
  
  public final boolean isKeyBindingActive() { return this.keyBindingActive; }
  
  protected final void setKeyBindingActive(boolean paramBoolean) { this.keyBindingActive = paramBoolean; }
  
  protected void setupMenuOpenKey() {
    InputMap inputMap = getInputMap(2);
    SwingUtilities.replaceUIInputMap(this.frame, 2, inputMap);
  }
  
  protected void setupMenuCloseKey() {}
  
  public JComponent getNorthPane() { return this.northPane; }
  
  public void setNorthPane(JComponent paramJComponent) {
    if (this.northPane != null && this.northPane instanceof BasicInternalFrameTitlePane)
      ((BasicInternalFrameTitlePane)this.northPane).uninstallListeners(); 
    replacePane(this.northPane, paramJComponent);
    this.northPane = paramJComponent;
    if (paramJComponent instanceof BasicInternalFrameTitlePane)
      this.titlePane = (BasicInternalFrameTitlePane)paramJComponent; 
  }
  
  public JComponent getSouthPane() { return this.southPane; }
  
  public void setSouthPane(JComponent paramJComponent) { this.southPane = paramJComponent; }
  
  public JComponent getWestPane() { return this.westPane; }
  
  public void setWestPane(JComponent paramJComponent) { this.westPane = paramJComponent; }
  
  public JComponent getEastPane() { return this.eastPane; }
  
  public void setEastPane(JComponent paramJComponent) { this.eastPane = paramJComponent; }
  
  protected DesktopManager getDesktopManager() {
    if (this.frame.getDesktopPane() != null && this.frame.getDesktopPane().getDesktopManager() != null)
      return this.frame.getDesktopPane().getDesktopManager(); 
    if (sharedDesktopManager == null)
      sharedDesktopManager = createDesktopManager(); 
    return sharedDesktopManager;
  }
  
  protected DesktopManager createDesktopManager() { return new DefaultDesktopManager(); }
  
  protected void closeFrame(JInternalFrame paramJInternalFrame) {
    BasicLookAndFeel.playSound(this.frame, "InternalFrame.closeSound");
    getDesktopManager().closeFrame(paramJInternalFrame);
  }
  
  protected void maximizeFrame(JInternalFrame paramJInternalFrame) {
    BasicLookAndFeel.playSound(this.frame, "InternalFrame.maximizeSound");
    getDesktopManager().maximizeFrame(paramJInternalFrame);
  }
  
  protected void minimizeFrame(JInternalFrame paramJInternalFrame) {
    if (!paramJInternalFrame.isIcon())
      BasicLookAndFeel.playSound(this.frame, "InternalFrame.restoreDownSound"); 
    getDesktopManager().minimizeFrame(paramJInternalFrame);
  }
  
  protected void iconifyFrame(JInternalFrame paramJInternalFrame) {
    BasicLookAndFeel.playSound(this.frame, "InternalFrame.minimizeSound");
    getDesktopManager().iconifyFrame(paramJInternalFrame);
  }
  
  protected void deiconifyFrame(JInternalFrame paramJInternalFrame) {
    if (!paramJInternalFrame.isMaximum())
      BasicLookAndFeel.playSound(this.frame, "InternalFrame.restoreUpSound"); 
    getDesktopManager().deiconifyFrame(paramJInternalFrame);
  }
  
  protected void activateFrame(JInternalFrame paramJInternalFrame) { getDesktopManager().activateFrame(paramJInternalFrame); }
  
  protected void deactivateFrame(JInternalFrame paramJInternalFrame) { getDesktopManager().deactivateFrame(paramJInternalFrame); }
  
  protected ComponentListener createComponentListener() { return getHandler(); }
  
  protected MouseInputListener createGlassPaneDispatcher() { return null; }
  
  protected class BasicInternalFrameListener implements InternalFrameListener {
    public void internalFrameClosing(InternalFrameEvent param1InternalFrameEvent) { BasicInternalFrameUI.this.getHandler().internalFrameClosing(param1InternalFrameEvent); }
    
    public void internalFrameClosed(InternalFrameEvent param1InternalFrameEvent) { BasicInternalFrameUI.this.getHandler().internalFrameClosed(param1InternalFrameEvent); }
    
    public void internalFrameOpened(InternalFrameEvent param1InternalFrameEvent) { BasicInternalFrameUI.this.getHandler().internalFrameOpened(param1InternalFrameEvent); }
    
    public void internalFrameIconified(InternalFrameEvent param1InternalFrameEvent) { BasicInternalFrameUI.this.getHandler().internalFrameIconified(param1InternalFrameEvent); }
    
    public void internalFrameDeiconified(InternalFrameEvent param1InternalFrameEvent) { BasicInternalFrameUI.this.getHandler().internalFrameDeiconified(param1InternalFrameEvent); }
    
    public void internalFrameActivated(InternalFrameEvent param1InternalFrameEvent) { BasicInternalFrameUI.this.getHandler().internalFrameActivated(param1InternalFrameEvent); }
    
    public void internalFrameDeactivated(InternalFrameEvent param1InternalFrameEvent) { BasicInternalFrameUI.this.getHandler().internalFrameDeactivated(param1InternalFrameEvent); }
  }
  
  protected class BorderListener extends MouseInputAdapter implements SwingConstants {
    int _x;
    
    int _y;
    
    int __x;
    
    int __y;
    
    Rectangle startingBounds;
    
    int resizeDir;
    
    protected final int RESIZE_NONE = 0;
    
    private boolean discardRelease = false;
    
    int resizeCornerSize = 16;
    
    public void mouseClicked(MouseEvent param1MouseEvent) {
      if (param1MouseEvent.getClickCount() > 1 && param1MouseEvent.getSource() == BasicInternalFrameUI.this.getNorthPane())
        if (BasicInternalFrameUI.this.frame.isIconifiable() && BasicInternalFrameUI.this.frame.isIcon()) {
          try {
            BasicInternalFrameUI.this.frame.setIcon(false);
          } catch (PropertyVetoException propertyVetoException) {}
        } else if (BasicInternalFrameUI.this.frame.isMaximizable()) {
          if (!BasicInternalFrameUI.this.frame.isMaximum()) {
            try {
              BasicInternalFrameUI.this.frame.setMaximum(true);
            } catch (PropertyVetoException propertyVetoException) {}
          } else {
            try {
              BasicInternalFrameUI.this.frame.setMaximum(false);
            } catch (PropertyVetoException propertyVetoException) {}
          } 
        }  
    }
    
    void finishMouseReleased() {
      if (this.discardRelease) {
        this.discardRelease = false;
        return;
      } 
      if (this.resizeDir == 0) {
        BasicInternalFrameUI.this.getDesktopManager().endDraggingFrame(BasicInternalFrameUI.this.frame);
        BasicInternalFrameUI.this.dragging = false;
      } else {
        Window window = SwingUtilities.getWindowAncestor(BasicInternalFrameUI.this.frame);
        if (window != null)
          window.removeWindowFocusListener(BasicInternalFrameUI.this.getWindowFocusListener()); 
        Container container = BasicInternalFrameUI.this.frame.getTopLevelAncestor();
        if (container instanceof RootPaneContainer) {
          Component component = ((RootPaneContainer)container).getGlassPane();
          component.setCursor(Cursor.getPredefinedCursor(0));
          component.setVisible(false);
        } 
        BasicInternalFrameUI.this.getDesktopManager().endResizingFrame(BasicInternalFrameUI.this.frame);
        BasicInternalFrameUI.this.resizing = false;
        BasicInternalFrameUI.this.updateFrameCursor();
      } 
      this._x = 0;
      this._y = 0;
      this.__x = 0;
      this.__y = 0;
      this.startingBounds = null;
      this.resizeDir = 0;
      this.discardRelease = true;
    }
    
    public void mouseReleased(MouseEvent param1MouseEvent) { finishMouseReleased(); }
    
    public void mousePressed(MouseEvent param1MouseEvent) {
      Point point1 = SwingUtilities.convertPoint((Component)param1MouseEvent.getSource(), param1MouseEvent.getX(), param1MouseEvent.getY(), null);
      this.__x = param1MouseEvent.getX();
      this.__y = param1MouseEvent.getY();
      this._x = point1.x;
      this._y = point1.y;
      this.startingBounds = BasicInternalFrameUI.this.frame.getBounds();
      this.resizeDir = 0;
      this.discardRelease = false;
      try {
        BasicInternalFrameUI.this.frame.setSelected(true);
      } catch (PropertyVetoException propertyVetoException) {}
      Insets insets = BasicInternalFrameUI.this.frame.getInsets();
      Point point2 = new Point(this.__x, this.__y);
      if (param1MouseEvent.getSource() == BasicInternalFrameUI.this.getNorthPane()) {
        Point point = BasicInternalFrameUI.this.getNorthPane().getLocation();
        point2.x += point.x;
        point2.y += point.y;
      } 
      if (param1MouseEvent.getSource() == BasicInternalFrameUI.this.getNorthPane() && point2.x > insets.left && point2.y > insets.top && point2.x < BasicInternalFrameUI.this.frame.getWidth() - insets.right) {
        BasicInternalFrameUI.this.getDesktopManager().beginDraggingFrame(BasicInternalFrameUI.this.frame);
        BasicInternalFrameUI.this.dragging = true;
        return;
      } 
      if (!BasicInternalFrameUI.this.frame.isResizable())
        return; 
      if (param1MouseEvent.getSource() == BasicInternalFrameUI.this.frame || param1MouseEvent.getSource() == BasicInternalFrameUI.this.getNorthPane()) {
        if (point2.x <= insets.left) {
          if (point2.y < this.resizeCornerSize + insets.top) {
            this.resizeDir = 8;
          } else if (point2.y > BasicInternalFrameUI.this.frame.getHeight() - this.resizeCornerSize - insets.bottom) {
            this.resizeDir = 6;
          } else {
            this.resizeDir = 7;
          } 
        } else if (point2.x >= BasicInternalFrameUI.this.frame.getWidth() - insets.right) {
          if (point2.y < this.resizeCornerSize + insets.top) {
            this.resizeDir = 2;
          } else if (point2.y > BasicInternalFrameUI.this.frame.getHeight() - this.resizeCornerSize - insets.bottom) {
            this.resizeDir = 4;
          } else {
            this.resizeDir = 3;
          } 
        } else if (point2.y <= insets.top) {
          if (point2.x < this.resizeCornerSize + insets.left) {
            this.resizeDir = 8;
          } else if (point2.x > BasicInternalFrameUI.this.frame.getWidth() - this.resizeCornerSize - insets.right) {
            this.resizeDir = 2;
          } else {
            this.resizeDir = 1;
          } 
        } else if (point2.y >= BasicInternalFrameUI.this.frame.getHeight() - insets.bottom) {
          if (point2.x < this.resizeCornerSize + insets.left) {
            this.resizeDir = 6;
          } else if (point2.x > BasicInternalFrameUI.this.frame.getWidth() - this.resizeCornerSize - insets.right) {
            this.resizeDir = 4;
          } else {
            this.resizeDir = 5;
          } 
        } else {
          this.discardRelease = true;
          return;
        } 
        Cursor cursor = Cursor.getPredefinedCursor(0);
        switch (this.resizeDir) {
          case 5:
            cursor = Cursor.getPredefinedCursor(9);
            break;
          case 1:
            cursor = Cursor.getPredefinedCursor(8);
            break;
          case 7:
            cursor = Cursor.getPredefinedCursor(10);
            break;
          case 3:
            cursor = Cursor.getPredefinedCursor(11);
            break;
          case 4:
            cursor = Cursor.getPredefinedCursor(5);
            break;
          case 6:
            cursor = Cursor.getPredefinedCursor(4);
            break;
          case 8:
            cursor = Cursor.getPredefinedCursor(6);
            break;
          case 2:
            cursor = Cursor.getPredefinedCursor(7);
            break;
        } 
        Container container = BasicInternalFrameUI.this.frame.getTopLevelAncestor();
        if (container instanceof RootPaneContainer) {
          Component component = ((RootPaneContainer)container).getGlassPane();
          component.setVisible(true);
          component.setCursor(cursor);
        } 
        BasicInternalFrameUI.this.getDesktopManager().beginResizingFrame(BasicInternalFrameUI.this.frame, this.resizeDir);
        BasicInternalFrameUI.this.resizing = true;
        Window window = SwingUtilities.getWindowAncestor(BasicInternalFrameUI.this.frame);
        if (window != null)
          window.addWindowFocusListener(BasicInternalFrameUI.this.getWindowFocusListener()); 
        return;
      } 
    }
    
    public void mouseDragged(MouseEvent param1MouseEvent) {
      if (this.startingBounds == null)
        return; 
      Point point = SwingUtilities.convertPoint((Component)param1MouseEvent.getSource(), param1MouseEvent.getX(), param1MouseEvent.getY(), null);
      int i = this._x - point.x;
      int j = this._y - point.y;
      Dimension dimension1 = BasicInternalFrameUI.this.frame.getMinimumSize();
      Dimension dimension2 = BasicInternalFrameUI.this.frame.getMaximumSize();
      Insets insets = BasicInternalFrameUI.this.frame.getInsets();
      if (BasicInternalFrameUI.this.dragging) {
        if (BasicInternalFrameUI.this.frame.isMaximum() || (param1MouseEvent.getModifiers() & 0x10) != 16)
          return; 
        Dimension dimension = BasicInternalFrameUI.this.frame.getParent().getSize();
        int i4 = dimension.width;
        int i5 = dimension.height;
        int i2 = this.startingBounds.x - i;
        int i3 = this.startingBounds.y - j;
        if (i2 + insets.left <= -this.__x)
          i2 = -this.__x - insets.left + 1; 
        if (i3 + insets.top <= -this.__y)
          i3 = -this.__y - insets.top + 1; 
        if (i2 + this.__x + insets.right >= i4)
          i2 = i4 - this.__x - insets.right - 1; 
        if (i3 + this.__y + insets.bottom >= i5)
          i3 = i5 - this.__y - insets.bottom - 1; 
        BasicInternalFrameUI.this.getDesktopManager().dragFrame(BasicInternalFrameUI.this.frame, i2, i3);
        return;
      } 
      if (!BasicInternalFrameUI.this.frame.isResizable())
        return; 
      int k = BasicInternalFrameUI.this.frame.getX();
      int m = BasicInternalFrameUI.this.frame.getY();
      int n = BasicInternalFrameUI.this.frame.getWidth();
      int i1 = BasicInternalFrameUI.this.frame.getHeight();
      BasicInternalFrameUI.this.parentBounds = BasicInternalFrameUI.this.frame.getParent().getBounds();
      switch (this.resizeDir) {
        case 0:
          return;
        case 1:
          if (this.startingBounds.height + j < dimension1.height) {
            j = -(this.startingBounds.height - dimension1.height);
          } else if (this.startingBounds.height + j > dimension2.height) {
            j = dimension2.height - this.startingBounds.height;
          } 
          if (this.startingBounds.y - j < 0)
            j = this.startingBounds.y; 
          k = this.startingBounds.x;
          m = this.startingBounds.y - j;
          n = this.startingBounds.width;
          i1 = this.startingBounds.height + j;
          break;
        case 2:
          if (this.startingBounds.height + j < dimension1.height) {
            j = -(this.startingBounds.height - dimension1.height);
          } else if (this.startingBounds.height + j > dimension2.height) {
            j = dimension2.height - this.startingBounds.height;
          } 
          if (this.startingBounds.y - j < 0)
            j = this.startingBounds.y; 
          if (this.startingBounds.width - i < dimension1.width) {
            i = this.startingBounds.width - dimension1.width;
          } else if (this.startingBounds.width - i > dimension2.width) {
            i = -(dimension2.width - this.startingBounds.width);
          } 
          if (this.startingBounds.x + this.startingBounds.width - i > this.this$0.parentBounds.width)
            i = this.startingBounds.x + this.startingBounds.width - this.this$0.parentBounds.width; 
          k = this.startingBounds.x;
          m = this.startingBounds.y - j;
          n = this.startingBounds.width - i;
          i1 = this.startingBounds.height + j;
          break;
        case 3:
          if (this.startingBounds.width - i < dimension1.width) {
            i = this.startingBounds.width - dimension1.width;
          } else if (this.startingBounds.width - i > dimension2.width) {
            i = -(dimension2.width - this.startingBounds.width);
          } 
          if (this.startingBounds.x + this.startingBounds.width - i > this.this$0.parentBounds.width)
            i = this.startingBounds.x + this.startingBounds.width - this.this$0.parentBounds.width; 
          n = this.startingBounds.width - i;
          i1 = this.startingBounds.height;
          break;
        case 4:
          if (this.startingBounds.width - i < dimension1.width) {
            i = this.startingBounds.width - dimension1.width;
          } else if (this.startingBounds.width - i > dimension2.width) {
            i = -(dimension2.width - this.startingBounds.width);
          } 
          if (this.startingBounds.x + this.startingBounds.width - i > this.this$0.parentBounds.width)
            i = this.startingBounds.x + this.startingBounds.width - this.this$0.parentBounds.width; 
          if (this.startingBounds.height - j < dimension1.height) {
            j = this.startingBounds.height - dimension1.height;
          } else if (this.startingBounds.height - j > dimension2.height) {
            j = -(dimension2.height - this.startingBounds.height);
          } 
          if (this.startingBounds.y + this.startingBounds.height - j > this.this$0.parentBounds.height)
            j = this.startingBounds.y + this.startingBounds.height - this.this$0.parentBounds.height; 
          n = this.startingBounds.width - i;
          i1 = this.startingBounds.height - j;
          break;
        case 5:
          if (this.startingBounds.height - j < dimension1.height) {
            j = this.startingBounds.height - dimension1.height;
          } else if (this.startingBounds.height - j > dimension2.height) {
            j = -(dimension2.height - this.startingBounds.height);
          } 
          if (this.startingBounds.y + this.startingBounds.height - j > this.this$0.parentBounds.height)
            j = this.startingBounds.y + this.startingBounds.height - this.this$0.parentBounds.height; 
          n = this.startingBounds.width;
          i1 = this.startingBounds.height - j;
          break;
        case 6:
          if (this.startingBounds.height - j < dimension1.height) {
            j = this.startingBounds.height - dimension1.height;
          } else if (this.startingBounds.height - j > dimension2.height) {
            j = -(dimension2.height - this.startingBounds.height);
          } 
          if (this.startingBounds.y + this.startingBounds.height - j > this.this$0.parentBounds.height)
            j = this.startingBounds.y + this.startingBounds.height - this.this$0.parentBounds.height; 
          if (this.startingBounds.width + i < dimension1.width) {
            i = -(this.startingBounds.width - dimension1.width);
          } else if (this.startingBounds.width + i > dimension2.width) {
            i = dimension2.width - this.startingBounds.width;
          } 
          if (this.startingBounds.x - i < 0)
            i = this.startingBounds.x; 
          k = this.startingBounds.x - i;
          m = this.startingBounds.y;
          n = this.startingBounds.width + i;
          i1 = this.startingBounds.height - j;
          break;
        case 7:
          if (this.startingBounds.width + i < dimension1.width) {
            i = -(this.startingBounds.width - dimension1.width);
          } else if (this.startingBounds.width + i > dimension2.width) {
            i = dimension2.width - this.startingBounds.width;
          } 
          if (this.startingBounds.x - i < 0)
            i = this.startingBounds.x; 
          k = this.startingBounds.x - i;
          m = this.startingBounds.y;
          n = this.startingBounds.width + i;
          i1 = this.startingBounds.height;
          break;
        case 8:
          if (this.startingBounds.width + i < dimension1.width) {
            i = -(this.startingBounds.width - dimension1.width);
          } else if (this.startingBounds.width + i > dimension2.width) {
            i = dimension2.width - this.startingBounds.width;
          } 
          if (this.startingBounds.x - i < 0)
            i = this.startingBounds.x; 
          if (this.startingBounds.height + j < dimension1.height) {
            j = -(this.startingBounds.height - dimension1.height);
          } else if (this.startingBounds.height + j > dimension2.height) {
            j = dimension2.height - this.startingBounds.height;
          } 
          if (this.startingBounds.y - j < 0)
            j = this.startingBounds.y; 
          k = this.startingBounds.x - i;
          m = this.startingBounds.y - j;
          n = this.startingBounds.width + i;
          i1 = this.startingBounds.height + j;
          break;
        default:
          return;
      } 
      BasicInternalFrameUI.this.getDesktopManager().resizeFrame(BasicInternalFrameUI.this.frame, k, m, n, i1);
    }
    
    public void mouseMoved(MouseEvent param1MouseEvent) {
      if (!BasicInternalFrameUI.this.frame.isResizable())
        return; 
      if (param1MouseEvent.getSource() == BasicInternalFrameUI.this.frame || param1MouseEvent.getSource() == BasicInternalFrameUI.this.getNorthPane()) {
        Insets insets = BasicInternalFrameUI.this.frame.getInsets();
        Point point = new Point(param1MouseEvent.getX(), param1MouseEvent.getY());
        if (param1MouseEvent.getSource() == BasicInternalFrameUI.this.getNorthPane()) {
          Point point1 = BasicInternalFrameUI.this.getNorthPane().getLocation();
          point.x += point1.x;
          point.y += point1.y;
        } 
        if (point.x <= insets.left) {
          if (point.y < this.resizeCornerSize + insets.top) {
            BasicInternalFrameUI.this.frame.setCursor(Cursor.getPredefinedCursor(6));
          } else if (point.y > BasicInternalFrameUI.this.frame.getHeight() - this.resizeCornerSize - insets.bottom) {
            BasicInternalFrameUI.this.frame.setCursor(Cursor.getPredefinedCursor(4));
          } else {
            BasicInternalFrameUI.this.frame.setCursor(Cursor.getPredefinedCursor(10));
          } 
        } else if (point.x >= BasicInternalFrameUI.this.frame.getWidth() - insets.right) {
          if (param1MouseEvent.getY() < this.resizeCornerSize + insets.top) {
            BasicInternalFrameUI.this.frame.setCursor(Cursor.getPredefinedCursor(7));
          } else if (point.y > BasicInternalFrameUI.this.frame.getHeight() - this.resizeCornerSize - insets.bottom) {
            BasicInternalFrameUI.this.frame.setCursor(Cursor.getPredefinedCursor(5));
          } else {
            BasicInternalFrameUI.this.frame.setCursor(Cursor.getPredefinedCursor(11));
          } 
        } else if (point.y <= insets.top) {
          if (point.x < this.resizeCornerSize + insets.left) {
            BasicInternalFrameUI.this.frame.setCursor(Cursor.getPredefinedCursor(6));
          } else if (point.x > BasicInternalFrameUI.this.frame.getWidth() - this.resizeCornerSize - insets.right) {
            BasicInternalFrameUI.this.frame.setCursor(Cursor.getPredefinedCursor(7));
          } else {
            BasicInternalFrameUI.this.frame.setCursor(Cursor.getPredefinedCursor(8));
          } 
        } else if (point.y >= BasicInternalFrameUI.this.frame.getHeight() - insets.bottom) {
          if (point.x < this.resizeCornerSize + insets.left) {
            BasicInternalFrameUI.this.frame.setCursor(Cursor.getPredefinedCursor(4));
          } else if (point.x > BasicInternalFrameUI.this.frame.getWidth() - this.resizeCornerSize - insets.right) {
            BasicInternalFrameUI.this.frame.setCursor(Cursor.getPredefinedCursor(5));
          } else {
            BasicInternalFrameUI.this.frame.setCursor(Cursor.getPredefinedCursor(9));
          } 
        } else {
          BasicInternalFrameUI.this.updateFrameCursor();
        } 
        return;
      } 
      BasicInternalFrameUI.this.updateFrameCursor();
    }
    
    public void mouseEntered(MouseEvent param1MouseEvent) { BasicInternalFrameUI.this.updateFrameCursor(); }
    
    public void mouseExited(MouseEvent param1MouseEvent) { BasicInternalFrameUI.this.updateFrameCursor(); }
  }
  
  protected class ComponentHandler implements ComponentListener {
    public void componentResized(ComponentEvent param1ComponentEvent) { BasicInternalFrameUI.this.getHandler().componentResized(param1ComponentEvent); }
    
    public void componentMoved(ComponentEvent param1ComponentEvent) { BasicInternalFrameUI.this.getHandler().componentMoved(param1ComponentEvent); }
    
    public void componentShown(ComponentEvent param1ComponentEvent) { BasicInternalFrameUI.this.getHandler().componentShown(param1ComponentEvent); }
    
    public void componentHidden(ComponentEvent param1ComponentEvent) { BasicInternalFrameUI.this.getHandler().componentHidden(param1ComponentEvent); }
  }
  
  protected class GlassPaneDispatcher implements MouseInputListener {
    public void mousePressed(MouseEvent param1MouseEvent) { BasicInternalFrameUI.this.getHandler().mousePressed(param1MouseEvent); }
    
    public void mouseEntered(MouseEvent param1MouseEvent) { BasicInternalFrameUI.this.getHandler().mouseEntered(param1MouseEvent); }
    
    public void mouseMoved(MouseEvent param1MouseEvent) { BasicInternalFrameUI.this.getHandler().mouseMoved(param1MouseEvent); }
    
    public void mouseExited(MouseEvent param1MouseEvent) { BasicInternalFrameUI.this.getHandler().mouseExited(param1MouseEvent); }
    
    public void mouseClicked(MouseEvent param1MouseEvent) { BasicInternalFrameUI.this.getHandler().mouseClicked(param1MouseEvent); }
    
    public void mouseReleased(MouseEvent param1MouseEvent) { BasicInternalFrameUI.this.getHandler().mouseReleased(param1MouseEvent); }
    
    public void mouseDragged(MouseEvent param1MouseEvent) { BasicInternalFrameUI.this.getHandler().mouseDragged(param1MouseEvent); }
  }
  
  private class Handler implements ComponentListener, InternalFrameListener, LayoutManager, MouseInputListener, PropertyChangeListener, WindowFocusListener, SwingConstants {
    private Handler() {}
    
    public void windowGainedFocus(WindowEvent param1WindowEvent) {}
    
    public void windowLostFocus(WindowEvent param1WindowEvent) { BasicInternalFrameUI.this.cancelResize(); }
    
    public void componentResized(ComponentEvent param1ComponentEvent) {
      Rectangle rectangle = ((Component)param1ComponentEvent.getSource()).getBounds();
      JInternalFrame.JDesktopIcon jDesktopIcon = null;
      if (BasicInternalFrameUI.this.frame != null) {
        jDesktopIcon = BasicInternalFrameUI.this.frame.getDesktopIcon();
        if (BasicInternalFrameUI.this.frame.isMaximum())
          BasicInternalFrameUI.this.frame.setBounds(0, 0, rectangle.width, rectangle.height); 
      } 
      if (jDesktopIcon != null) {
        Rectangle rectangle1 = jDesktopIcon.getBounds();
        int i = rectangle1.y + rectangle.height - this.this$0.parentBounds.height;
        jDesktopIcon.setBounds(rectangle1.x, i, rectangle1.width, rectangle1.height);
      } 
      if (!BasicInternalFrameUI.this.parentBounds.equals(rectangle))
        BasicInternalFrameUI.this.parentBounds = rectangle; 
      if (BasicInternalFrameUI.this.frame != null)
        BasicInternalFrameUI.this.frame.validate(); 
    }
    
    public void componentMoved(ComponentEvent param1ComponentEvent) {}
    
    public void componentShown(ComponentEvent param1ComponentEvent) {}
    
    public void componentHidden(ComponentEvent param1ComponentEvent) {}
    
    public void internalFrameClosed(InternalFrameEvent param1InternalFrameEvent) { BasicInternalFrameUI.this.frame.removeInternalFrameListener(BasicInternalFrameUI.this.getHandler()); }
    
    public void internalFrameActivated(InternalFrameEvent param1InternalFrameEvent) {
      if (!BasicInternalFrameUI.this.isKeyBindingRegistered()) {
        BasicInternalFrameUI.this.setKeyBindingRegistered(true);
        BasicInternalFrameUI.this.setupMenuOpenKey();
        BasicInternalFrameUI.this.setupMenuCloseKey();
      } 
      if (BasicInternalFrameUI.this.isKeyBindingRegistered())
        BasicInternalFrameUI.this.setKeyBindingActive(true); 
    }
    
    public void internalFrameDeactivated(InternalFrameEvent param1InternalFrameEvent) { BasicInternalFrameUI.this.setKeyBindingActive(false); }
    
    public void internalFrameClosing(InternalFrameEvent param1InternalFrameEvent) {}
    
    public void internalFrameOpened(InternalFrameEvent param1InternalFrameEvent) {}
    
    public void internalFrameIconified(InternalFrameEvent param1InternalFrameEvent) {}
    
    public void internalFrameDeiconified(InternalFrameEvent param1InternalFrameEvent) {}
    
    public void addLayoutComponent(String param1String, Component param1Component) {}
    
    public void removeLayoutComponent(Component param1Component) {}
    
    public Dimension preferredLayoutSize(Container param1Container) {
      Insets insets = BasicInternalFrameUI.this.frame.getInsets();
      Dimension dimension = new Dimension(BasicInternalFrameUI.this.frame.getRootPane().getPreferredSize());
      dimension.width += insets.left + insets.right;
      dimension.height += insets.top + insets.bottom;
      if (BasicInternalFrameUI.this.getNorthPane() != null) {
        Dimension dimension1 = BasicInternalFrameUI.this.getNorthPane().getPreferredSize();
        dimension.width = Math.max(dimension1.width, dimension.width);
        dimension.height += dimension1.height;
      } 
      if (BasicInternalFrameUI.this.getSouthPane() != null) {
        Dimension dimension1 = BasicInternalFrameUI.this.getSouthPane().getPreferredSize();
        dimension.width = Math.max(dimension1.width, dimension.width);
        dimension.height += dimension1.height;
      } 
      if (BasicInternalFrameUI.this.getEastPane() != null) {
        Dimension dimension1 = BasicInternalFrameUI.this.getEastPane().getPreferredSize();
        dimension.width += dimension1.width;
        dimension.height = Math.max(dimension1.height, dimension.height);
      } 
      if (BasicInternalFrameUI.this.getWestPane() != null) {
        Dimension dimension1 = BasicInternalFrameUI.this.getWestPane().getPreferredSize();
        dimension.width += dimension1.width;
        dimension.height = Math.max(dimension1.height, dimension.height);
      } 
      return dimension;
    }
    
    public Dimension minimumLayoutSize(Container param1Container) {
      Dimension dimension = new Dimension();
      if (BasicInternalFrameUI.this.getNorthPane() != null && BasicInternalFrameUI.this.getNorthPane() instanceof BasicInternalFrameTitlePane)
        dimension = new Dimension(BasicInternalFrameUI.this.getNorthPane().getMinimumSize()); 
      Insets insets = BasicInternalFrameUI.this.frame.getInsets();
      dimension.width += insets.left + insets.right;
      dimension.height += insets.top + insets.bottom;
      return dimension;
    }
    
    public void layoutContainer(Container param1Container) {
      Insets insets = BasicInternalFrameUI.this.frame.getInsets();
      int i = insets.left;
      int j = insets.top;
      int k = BasicInternalFrameUI.this.frame.getWidth() - insets.left - insets.right;
      int m = BasicInternalFrameUI.this.frame.getHeight() - insets.top - insets.bottom;
      if (BasicInternalFrameUI.this.getNorthPane() != null) {
        Dimension dimension = BasicInternalFrameUI.this.getNorthPane().getPreferredSize();
        if (DefaultLookup.getBoolean(BasicInternalFrameUI.this.frame, BasicInternalFrameUI.this, "InternalFrame.layoutTitlePaneAtOrigin", false)) {
          j = 0;
          m += insets.top;
          BasicInternalFrameUI.this.getNorthPane().setBounds(0, 0, BasicInternalFrameUI.this.frame.getWidth(), dimension.height);
        } else {
          BasicInternalFrameUI.this.getNorthPane().setBounds(i, j, k, dimension.height);
        } 
        j += dimension.height;
        m -= dimension.height;
      } 
      if (BasicInternalFrameUI.this.getSouthPane() != null) {
        Dimension dimension = BasicInternalFrameUI.this.getSouthPane().getPreferredSize();
        BasicInternalFrameUI.this.getSouthPane().setBounds(i, BasicInternalFrameUI.this.frame.getHeight() - insets.bottom - dimension.height, k, dimension.height);
        m -= dimension.height;
      } 
      if (BasicInternalFrameUI.this.getWestPane() != null) {
        Dimension dimension = BasicInternalFrameUI.this.getWestPane().getPreferredSize();
        BasicInternalFrameUI.this.getWestPane().setBounds(i, j, dimension.width, m);
        k -= dimension.width;
        i += dimension.width;
      } 
      if (BasicInternalFrameUI.this.getEastPane() != null) {
        Dimension dimension = BasicInternalFrameUI.this.getEastPane().getPreferredSize();
        BasicInternalFrameUI.this.getEastPane().setBounds(k - dimension.width, j, dimension.width, m);
        k -= dimension.width;
      } 
      if (BasicInternalFrameUI.this.frame.getRootPane() != null)
        BasicInternalFrameUI.this.frame.getRootPane().setBounds(i, j, k, m); 
    }
    
    public void mousePressed(MouseEvent param1MouseEvent) {}
    
    public void mouseEntered(MouseEvent param1MouseEvent) {}
    
    public void mouseMoved(MouseEvent param1MouseEvent) {}
    
    public void mouseExited(MouseEvent param1MouseEvent) {}
    
    public void mouseClicked(MouseEvent param1MouseEvent) {}
    
    public void mouseReleased(MouseEvent param1MouseEvent) {}
    
    public void mouseDragged(MouseEvent param1MouseEvent) {}
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      String str = param1PropertyChangeEvent.getPropertyName();
      JInternalFrame jInternalFrame = (JInternalFrame)param1PropertyChangeEvent.getSource();
      Object object1 = param1PropertyChangeEvent.getNewValue();
      Object object2 = param1PropertyChangeEvent.getOldValue();
      if ("closed" == str) {
        if (object1 == Boolean.TRUE) {
          BasicInternalFrameUI.this.cancelResize();
          if (BasicInternalFrameUI.this.frame.getParent() != null && BasicInternalFrameUI.this.componentListenerAdded)
            BasicInternalFrameUI.this.frame.getParent().removeComponentListener(BasicInternalFrameUI.this.componentListener); 
          BasicInternalFrameUI.this.closeFrame(jInternalFrame);
        } 
      } else if ("maximum" == str) {
        if (object1 == Boolean.TRUE) {
          BasicInternalFrameUI.this.maximizeFrame(jInternalFrame);
        } else {
          BasicInternalFrameUI.this.minimizeFrame(jInternalFrame);
        } 
      } else if ("icon" == str) {
        if (object1 == Boolean.TRUE) {
          BasicInternalFrameUI.this.iconifyFrame(jInternalFrame);
        } else {
          BasicInternalFrameUI.this.deiconifyFrame(jInternalFrame);
        } 
      } else if ("selected" == str) {
        if (object1 == Boolean.TRUE && object2 == Boolean.FALSE) {
          BasicInternalFrameUI.this.activateFrame(jInternalFrame);
        } else if (object1 == Boolean.FALSE && object2 == Boolean.TRUE) {
          BasicInternalFrameUI.this.deactivateFrame(jInternalFrame);
        } 
      } else if (str == "ancestor") {
        if (object1 == null)
          BasicInternalFrameUI.this.cancelResize(); 
        if (BasicInternalFrameUI.this.frame.getParent() != null) {
          BasicInternalFrameUI.this.parentBounds = jInternalFrame.getParent().getBounds();
        } else {
          BasicInternalFrameUI.this.parentBounds = null;
        } 
        if (BasicInternalFrameUI.this.frame.getParent() != null && !BasicInternalFrameUI.this.componentListenerAdded) {
          jInternalFrame.getParent().addComponentListener(BasicInternalFrameUI.this.componentListener);
          BasicInternalFrameUI.this.componentListenerAdded = true;
        } 
      } else if ("title" == str || str == "closable" || str == "iconable" || str == "maximizable") {
        Dimension dimension1 = BasicInternalFrameUI.this.frame.getMinimumSize();
        Dimension dimension2 = BasicInternalFrameUI.this.frame.getSize();
        if (dimension1.width > dimension2.width)
          BasicInternalFrameUI.this.frame.setSize(dimension1.width, dimension2.height); 
      } 
    }
  }
  
  public class InternalFrameLayout implements LayoutManager {
    public void addLayoutComponent(String param1String, Component param1Component) { BasicInternalFrameUI.this.getHandler().addLayoutComponent(param1String, param1Component); }
    
    public void removeLayoutComponent(Component param1Component) { BasicInternalFrameUI.this.getHandler().removeLayoutComponent(param1Component); }
    
    public Dimension preferredLayoutSize(Container param1Container) { return BasicInternalFrameUI.this.getHandler().preferredLayoutSize(param1Container); }
    
    public Dimension minimumLayoutSize(Container param1Container) { return BasicInternalFrameUI.this.getHandler().minimumLayoutSize(param1Container); }
    
    public void layoutContainer(Container param1Container) { BasicInternalFrameUI.this.getHandler().layoutContainer(param1Container); }
  }
  
  public class InternalFramePropertyChangeListener implements PropertyChangeListener {
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) { BasicInternalFrameUI.this.getHandler().propertyChange(param1PropertyChangeEvent); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicInternalFrameUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */