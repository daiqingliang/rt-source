package javax.swing.plaf.basic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.IllegalComponentStateException;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Hashtable;
import javax.swing.AbstractButton;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.RootPaneContainer;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ToolBarUI;
import sun.swing.DefaultLookup;
import sun.swing.UIAction;

public class BasicToolBarUI extends ToolBarUI implements SwingConstants {
  protected JToolBar toolBar;
  
  private boolean floating;
  
  private int floatingX;
  
  private int floatingY;
  
  private JFrame floatingFrame;
  
  private RootPaneContainer floatingToolBar;
  
  protected DragWindow dragWindow;
  
  private Container dockingSource;
  
  private int dockingSensitivity = 0;
  
  protected int focusedCompIndex = -1;
  
  protected Color dockingColor = null;
  
  protected Color floatingColor = null;
  
  protected Color dockingBorderColor = null;
  
  protected Color floatingBorderColor = null;
  
  protected MouseInputListener dockingListener;
  
  protected PropertyChangeListener propertyListener;
  
  protected ContainerListener toolBarContListener;
  
  protected FocusListener toolBarFocusListener;
  
  private Handler handler;
  
  protected String constraintBeforeFloating = "North";
  
  private static String IS_ROLLOVER = "JToolBar.isRollover";
  
  private static Border rolloverBorder;
  
  private static Border nonRolloverBorder;
  
  private static Border nonRolloverToggleBorder;
  
  private boolean rolloverBorders = false;
  
  private HashMap<AbstractButton, Border> borderTable = new HashMap();
  
  private Hashtable<AbstractButton, Boolean> rolloverTable = new Hashtable();
  
  @Deprecated
  protected KeyStroke upKey;
  
  @Deprecated
  protected KeyStroke downKey;
  
  @Deprecated
  protected KeyStroke leftKey;
  
  @Deprecated
  protected KeyStroke rightKey;
  
  private static String FOCUSED_COMP_INDEX = "JToolBar.focusedCompIndex";
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new BasicToolBarUI(); }
  
  public void installUI(JComponent paramJComponent) {
    this.toolBar = (JToolBar)paramJComponent;
    installDefaults();
    installComponents();
    installListeners();
    installKeyboardActions();
    this.dockingSensitivity = 0;
    this.floating = false;
    this.floatingX = this.floatingY = 0;
    this.floatingToolBar = null;
    setOrientation(this.toolBar.getOrientation());
    LookAndFeel.installProperty(paramJComponent, "opaque", Boolean.TRUE);
    if (paramJComponent.getClientProperty(FOCUSED_COMP_INDEX) != null)
      this.focusedCompIndex = ((Integer)paramJComponent.getClientProperty(FOCUSED_COMP_INDEX)).intValue(); 
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    uninstallDefaults();
    uninstallComponents();
    uninstallListeners();
    uninstallKeyboardActions();
    if (isFloating())
      setFloating(false, null); 
    this.floatingToolBar = null;
    this.dragWindow = null;
    this.dockingSource = null;
    paramJComponent.putClientProperty(FOCUSED_COMP_INDEX, Integer.valueOf(this.focusedCompIndex));
  }
  
  protected void installDefaults() {
    LookAndFeel.installBorder(this.toolBar, "ToolBar.border");
    LookAndFeel.installColorsAndFont(this.toolBar, "ToolBar.background", "ToolBar.foreground", "ToolBar.font");
    if (this.dockingColor == null || this.dockingColor instanceof javax.swing.plaf.UIResource)
      this.dockingColor = UIManager.getColor("ToolBar.dockingBackground"); 
    if (this.floatingColor == null || this.floatingColor instanceof javax.swing.plaf.UIResource)
      this.floatingColor = UIManager.getColor("ToolBar.floatingBackground"); 
    if (this.dockingBorderColor == null || this.dockingBorderColor instanceof javax.swing.plaf.UIResource)
      this.dockingBorderColor = UIManager.getColor("ToolBar.dockingForeground"); 
    if (this.floatingBorderColor == null || this.floatingBorderColor instanceof javax.swing.plaf.UIResource)
      this.floatingBorderColor = UIManager.getColor("ToolBar.floatingForeground"); 
    Object object = this.toolBar.getClientProperty(IS_ROLLOVER);
    if (object == null)
      object = UIManager.get("ToolBar.isRollover"); 
    if (object != null)
      this.rolloverBorders = ((Boolean)object).booleanValue(); 
    if (rolloverBorder == null)
      rolloverBorder = createRolloverBorder(); 
    if (nonRolloverBorder == null)
      nonRolloverBorder = createNonRolloverBorder(); 
    if (nonRolloverToggleBorder == null)
      nonRolloverToggleBorder = createNonRolloverToggleBorder(); 
    setRolloverBorders(isRolloverBorders());
  }
  
  protected void uninstallDefaults() {
    LookAndFeel.uninstallBorder(this.toolBar);
    this.dockingColor = null;
    this.floatingColor = null;
    this.dockingBorderColor = null;
    this.floatingBorderColor = null;
    installNormalBorders(this.toolBar);
    rolloverBorder = null;
    nonRolloverBorder = null;
    nonRolloverToggleBorder = null;
  }
  
  protected void installComponents() {}
  
  protected void uninstallComponents() {}
  
  protected void installListeners() {
    this.dockingListener = createDockingListener();
    if (this.dockingListener != null) {
      this.toolBar.addMouseMotionListener(this.dockingListener);
      this.toolBar.addMouseListener(this.dockingListener);
    } 
    this.propertyListener = createPropertyListener();
    if (this.propertyListener != null)
      this.toolBar.addPropertyChangeListener(this.propertyListener); 
    this.toolBarContListener = createToolBarContListener();
    if (this.toolBarContListener != null)
      this.toolBar.addContainerListener(this.toolBarContListener); 
    this.toolBarFocusListener = createToolBarFocusListener();
    if (this.toolBarFocusListener != null) {
      Component[] arrayOfComponent = this.toolBar.getComponents();
      for (Component component : arrayOfComponent)
        component.addFocusListener(this.toolBarFocusListener); 
    } 
  }
  
  protected void uninstallListeners() {
    if (this.dockingListener != null) {
      this.toolBar.removeMouseMotionListener(this.dockingListener);
      this.toolBar.removeMouseListener(this.dockingListener);
      this.dockingListener = null;
    } 
    if (this.propertyListener != null) {
      this.toolBar.removePropertyChangeListener(this.propertyListener);
      this.propertyListener = null;
    } 
    if (this.toolBarContListener != null) {
      this.toolBar.removeContainerListener(this.toolBarContListener);
      this.toolBarContListener = null;
    } 
    if (this.toolBarFocusListener != null) {
      Component[] arrayOfComponent = this.toolBar.getComponents();
      for (Component component : arrayOfComponent)
        component.removeFocusListener(this.toolBarFocusListener); 
      this.toolBarFocusListener = null;
    } 
    this.handler = null;
  }
  
  protected void installKeyboardActions() {
    InputMap inputMap = getInputMap(1);
    SwingUtilities.replaceUIInputMap(this.toolBar, 1, inputMap);
    LazyActionMap.installLazyActionMap(this.toolBar, BasicToolBarUI.class, "ToolBar.actionMap");
  }
  
  InputMap getInputMap(int paramInt) { return (paramInt == 1) ? (InputMap)DefaultLookup.get(this.toolBar, this, "ToolBar.ancestorInputMap") : null; }
  
  static void loadActionMap(LazyActionMap paramLazyActionMap) {
    paramLazyActionMap.put(new Actions("navigateRight"));
    paramLazyActionMap.put(new Actions("navigateLeft"));
    paramLazyActionMap.put(new Actions("navigateUp"));
    paramLazyActionMap.put(new Actions("navigateDown"));
  }
  
  protected void uninstallKeyboardActions() {
    SwingUtilities.replaceUIActionMap(this.toolBar, null);
    SwingUtilities.replaceUIInputMap(this.toolBar, 1, null);
  }
  
  protected void navigateFocusedComp(int paramInt) {
    int j;
    int i = this.toolBar.getComponentCount();
    switch (paramInt) {
      case 3:
      case 5:
        if (this.focusedCompIndex < 0 || this.focusedCompIndex >= i)
          break; 
        j = this.focusedCompIndex + 1;
        while (j != this.focusedCompIndex) {
          if (j >= i)
            j = 0; 
          Component component = this.toolBar.getComponentAtIndex(j++);
          if (component != null && component.isFocusTraversable() && component.isEnabled()) {
            component.requestFocus();
            break;
          } 
        } 
        break;
      case 1:
      case 7:
        if (this.focusedCompIndex < 0 || this.focusedCompIndex >= i)
          break; 
        j = this.focusedCompIndex - 1;
        while (j != this.focusedCompIndex) {
          if (j < 0)
            j = i - 1; 
          Component component = this.toolBar.getComponentAtIndex(j--);
          if (component != null && component.isFocusTraversable() && component.isEnabled()) {
            component.requestFocus();
            break;
          } 
        } 
        break;
    } 
  }
  
  protected Border createRolloverBorder() {
    Object object = UIManager.get("ToolBar.rolloverBorder");
    if (object != null)
      return (Border)object; 
    UIDefaults uIDefaults = UIManager.getLookAndFeelDefaults();
    return new CompoundBorder(new BasicBorders.RolloverButtonBorder(uIDefaults.getColor("controlShadow"), uIDefaults.getColor("controlDkShadow"), uIDefaults.getColor("controlHighlight"), uIDefaults.getColor("controlLtHighlight")), new BasicBorders.RolloverMarginBorder());
  }
  
  protected Border createNonRolloverBorder() {
    Object object = UIManager.get("ToolBar.nonrolloverBorder");
    if (object != null)
      return (Border)object; 
    UIDefaults uIDefaults = UIManager.getLookAndFeelDefaults();
    return new CompoundBorder(new BasicBorders.ButtonBorder(uIDefaults.getColor("Button.shadow"), uIDefaults.getColor("Button.darkShadow"), uIDefaults.getColor("Button.light"), uIDefaults.getColor("Button.highlight")), new BasicBorders.RolloverMarginBorder());
  }
  
  private Border createNonRolloverToggleBorder() {
    UIDefaults uIDefaults = UIManager.getLookAndFeelDefaults();
    return new CompoundBorder(new BasicBorders.RadioButtonBorder(uIDefaults.getColor("ToggleButton.shadow"), uIDefaults.getColor("ToggleButton.darkShadow"), uIDefaults.getColor("ToggleButton.light"), uIDefaults.getColor("ToggleButton.highlight")), new BasicBorders.RolloverMarginBorder());
  }
  
  protected JFrame createFloatingFrame(JToolBar paramJToolBar) {
    Window window = SwingUtilities.getWindowAncestor(paramJToolBar);
    JFrame jFrame = new JFrame(paramJToolBar.getName(), (window != null) ? window.getGraphicsConfiguration() : null) {
        protected JRootPane createRootPane() {
          JRootPane jRootPane = new JRootPane() {
              private boolean packing = false;
              
              public void validate() {
                super.validate();
                if (!this.packing) {
                  this.packing = true;
                  BasicToolBarUI.null.this.pack();
                  this.packing = false;
                } 
              }
            };
          jRootPane.setOpaque(true);
          return jRootPane;
        }
      };
    jFrame.getRootPane().setName("ToolBar.FloatingFrame");
    jFrame.setResizable(false);
    WindowListener windowListener = createFrameListener();
    jFrame.addWindowListener(windowListener);
    return jFrame;
  }
  
  protected RootPaneContainer createFloatingWindow(JToolBar paramJToolBar) {
    ToolBarDialog toolBarDialog;
    Window window = SwingUtilities.getWindowAncestor(paramJToolBar);
    if (window instanceof Frame) {
      class ToolBarDialog extends JDialog {
        public ToolBarDialog(Frame param1Frame, String param1String, boolean param1Boolean) { super(param1Frame, param1String, param1Boolean); }
        
        public ToolBarDialog(Dialog param1Dialog, String param1String, boolean param1Boolean) { super(param1Dialog, param1String, param1Boolean); }
        
        protected JRootPane createRootPane() {
          JRootPane jRootPane = new JRootPane() {
              private boolean packing = false;
              
              public void validate() {
                super.validate();
                if (!this.packing) {
                  this.packing = true;
                  BasicToolBarUI.ToolBarDialog.this.pack();
                  this.packing = false;
                } 
              }
            };
          jRootPane.setOpaque(true);
          return jRootPane;
        }
      };
      toolBarDialog = new ToolBarDialog((Frame)window, paramJToolBar.getName(), false);
    } else if (window instanceof Dialog) {
      toolBarDialog = new ToolBarDialog(this, (Dialog)window, paramJToolBar.getName(), false);
    } else {
      toolBarDialog = new ToolBarDialog(this, (Frame)null, paramJToolBar.getName(), false);
    } 
    toolBarDialog.getRootPane().setName("ToolBar.FloatingWindow");
    toolBarDialog.setTitle(paramJToolBar.getName());
    toolBarDialog.setResizable(false);
    WindowListener windowListener = createFrameListener();
    toolBarDialog.addWindowListener(windowListener);
    return toolBarDialog;
  }
  
  protected DragWindow createDragWindow(JToolBar paramJToolBar) {
    Window window = null;
    if (this.toolBar != null) {
      Container container;
      for (container = this.toolBar.getParent(); container != null && !(container instanceof Window); container = container.getParent());
      if (container != null && container instanceof Window)
        window = (Window)container; 
    } 
    if (this.floatingToolBar == null)
      this.floatingToolBar = createFloatingWindow(this.toolBar); 
    if (this.floatingToolBar instanceof Window)
      window = (Window)this.floatingToolBar; 
    return new DragWindow(window);
  }
  
  public boolean isRolloverBorders() { return this.rolloverBorders; }
  
  public void setRolloverBorders(boolean paramBoolean) {
    this.rolloverBorders = paramBoolean;
    if (this.rolloverBorders) {
      installRolloverBorders(this.toolBar);
    } else {
      installNonRolloverBorders(this.toolBar);
    } 
  }
  
  protected void installRolloverBorders(JComponent paramJComponent) {
    Component[] arrayOfComponent = paramJComponent.getComponents();
    for (Component component : arrayOfComponent) {
      if (component instanceof JComponent) {
        ((JComponent)component).updateUI();
        setBorderToRollover(component);
      } 
    } 
  }
  
  protected void installNonRolloverBorders(JComponent paramJComponent) {
    Component[] arrayOfComponent = paramJComponent.getComponents();
    for (Component component : arrayOfComponent) {
      if (component instanceof JComponent) {
        ((JComponent)component).updateUI();
        setBorderToNonRollover(component);
      } 
    } 
  }
  
  protected void installNormalBorders(JComponent paramJComponent) {
    Component[] arrayOfComponent = paramJComponent.getComponents();
    for (Component component : arrayOfComponent)
      setBorderToNormal(component); 
  }
  
  protected void setBorderToRollover(Component paramComponent) {
    if (paramComponent instanceof AbstractButton) {
      AbstractButton abstractButton = (AbstractButton)paramComponent;
      Border border = (Border)this.borderTable.get(abstractButton);
      if (border == null || border instanceof javax.swing.plaf.UIResource)
        this.borderTable.put(abstractButton, abstractButton.getBorder()); 
      if (abstractButton.getBorder() instanceof javax.swing.plaf.UIResource)
        abstractButton.setBorder(getRolloverBorder(abstractButton)); 
      this.rolloverTable.put(abstractButton, abstractButton.isRolloverEnabled() ? Boolean.TRUE : Boolean.FALSE);
      abstractButton.setRolloverEnabled(true);
    } 
  }
  
  protected Border getRolloverBorder(AbstractButton paramAbstractButton) { return rolloverBorder; }
  
  protected void setBorderToNonRollover(Component paramComponent) {
    if (paramComponent instanceof AbstractButton) {
      AbstractButton abstractButton = (AbstractButton)paramComponent;
      Border border = (Border)this.borderTable.get(abstractButton);
      if (border == null || border instanceof javax.swing.plaf.UIResource)
        this.borderTable.put(abstractButton, abstractButton.getBorder()); 
      if (abstractButton.getBorder() instanceof javax.swing.plaf.UIResource)
        abstractButton.setBorder(getNonRolloverBorder(abstractButton)); 
      this.rolloverTable.put(abstractButton, abstractButton.isRolloverEnabled() ? Boolean.TRUE : Boolean.FALSE);
      abstractButton.setRolloverEnabled(false);
    } 
  }
  
  protected Border getNonRolloverBorder(AbstractButton paramAbstractButton) { return (paramAbstractButton instanceof javax.swing.JToggleButton) ? nonRolloverToggleBorder : nonRolloverBorder; }
  
  protected void setBorderToNormal(Component paramComponent) {
    if (paramComponent instanceof AbstractButton) {
      AbstractButton abstractButton = (AbstractButton)paramComponent;
      Border border = (Border)this.borderTable.remove(abstractButton);
      abstractButton.setBorder(border);
      Boolean bool = (Boolean)this.rolloverTable.remove(abstractButton);
      if (bool != null)
        abstractButton.setRolloverEnabled(bool.booleanValue()); 
    } 
  }
  
  public void setFloatingLocation(int paramInt1, int paramInt2) {
    this.floatingX = paramInt1;
    this.floatingY = paramInt2;
  }
  
  public boolean isFloating() { return this.floating; }
  
  public void setFloating(boolean paramBoolean, Point paramPoint) {
    if (this.toolBar.isFloatable()) {
      boolean bool = false;
      Window window = SwingUtilities.getWindowAncestor(this.toolBar);
      if (window != null)
        bool = window.isVisible(); 
      if (this.dragWindow != null)
        this.dragWindow.setVisible(false); 
      this.floating = paramBoolean;
      if (this.floatingToolBar == null)
        this.floatingToolBar = createFloatingWindow(this.toolBar); 
      if (paramBoolean == true) {
        if (this.dockingSource == null) {
          this.dockingSource = this.toolBar.getParent();
          this.dockingSource.remove(this.toolBar);
        } 
        this.constraintBeforeFloating = calculateConstraint();
        if (this.propertyListener != null)
          UIManager.addPropertyChangeListener(this.propertyListener); 
        this.floatingToolBar.getContentPane().add(this.toolBar, "Center");
        if (this.floatingToolBar instanceof Window) {
          ((Window)this.floatingToolBar).pack();
          ((Window)this.floatingToolBar).setLocation(this.floatingX, this.floatingY);
          if (bool) {
            ((Window)this.floatingToolBar).show();
          } else {
            window.addWindowListener(new WindowAdapter() {
                  public void windowOpened(WindowEvent param1WindowEvent) { ((Window)BasicToolBarUI.this.floatingToolBar).show(); }
                });
          } 
        } 
      } else {
        if (this.floatingToolBar == null)
          this.floatingToolBar = createFloatingWindow(this.toolBar); 
        if (this.floatingToolBar instanceof Window)
          ((Window)this.floatingToolBar).setVisible(false); 
        this.floatingToolBar.getContentPane().remove(this.toolBar);
        String str = getDockingConstraint(this.dockingSource, paramPoint);
        if (str == null)
          str = "North"; 
        int i = mapConstraintToOrientation(str);
        setOrientation(i);
        if (this.dockingSource == null)
          this.dockingSource = this.toolBar.getParent(); 
        if (this.propertyListener != null)
          UIManager.removePropertyChangeListener(this.propertyListener); 
        this.dockingSource.add(str, this.toolBar);
      } 
      this.dockingSource.invalidate();
      Container container = this.dockingSource.getParent();
      if (container != null)
        container.validate(); 
      this.dockingSource.repaint();
    } 
  }
  
  private int mapConstraintToOrientation(String paramString) {
    int i = this.toolBar.getOrientation();
    if (paramString != null)
      if (paramString.equals("East") || paramString.equals("West")) {
        i = 1;
      } else if (paramString.equals("North") || paramString.equals("South")) {
        i = 0;
      }  
    return i;
  }
  
  public void setOrientation(int paramInt) {
    this.toolBar.setOrientation(paramInt);
    if (this.dragWindow != null)
      this.dragWindow.setOrientation(paramInt); 
  }
  
  public Color getDockingColor() { return this.dockingColor; }
  
  public void setDockingColor(Color paramColor) { this.dockingColor = paramColor; }
  
  public Color getFloatingColor() { return this.floatingColor; }
  
  public void setFloatingColor(Color paramColor) { this.floatingColor = paramColor; }
  
  private boolean isBlocked(Component paramComponent, Object paramObject) {
    if (paramComponent instanceof Container) {
      Container container = (Container)paramComponent;
      LayoutManager layoutManager = container.getLayout();
      if (layoutManager instanceof BorderLayout) {
        BorderLayout borderLayout = (BorderLayout)layoutManager;
        Component component = borderLayout.getLayoutComponent(container, paramObject);
        return (component != null && component != this.toolBar);
      } 
    } 
    return false;
  }
  
  public boolean canDock(Component paramComponent, Point paramPoint) { return (paramPoint != null && getDockingConstraint(paramComponent, paramPoint) != null); }
  
  private String calculateConstraint() {
    String str = null;
    LayoutManager layoutManager = this.dockingSource.getLayout();
    if (layoutManager instanceof BorderLayout)
      str = (String)((BorderLayout)layoutManager).getConstraints(this.toolBar); 
    return (str != null) ? str : this.constraintBeforeFloating;
  }
  
  private String getDockingConstraint(Component paramComponent, Point paramPoint) {
    if (paramPoint == null)
      return this.constraintBeforeFloating; 
    if (paramComponent.contains(paramPoint)) {
      this.dockingSensitivity = (this.toolBar.getOrientation() == 0) ? (this.toolBar.getSize()).height : (this.toolBar.getSize()).width;
      if (paramPoint.y < this.dockingSensitivity && !isBlocked(paramComponent, "North"))
        return "North"; 
      if (paramPoint.x >= paramComponent.getWidth() - this.dockingSensitivity && !isBlocked(paramComponent, "East"))
        return "East"; 
      if (paramPoint.x < this.dockingSensitivity && !isBlocked(paramComponent, "West"))
        return "West"; 
      if (paramPoint.y >= paramComponent.getHeight() - this.dockingSensitivity && !isBlocked(paramComponent, "South"))
        return "South"; 
    } 
    return null;
  }
  
  protected void dragTo(Point paramPoint1, Point paramPoint2) {
    if (this.toolBar.isFloatable())
      try {
        if (this.dragWindow == null)
          this.dragWindow = createDragWindow(this.toolBar); 
        Point point1 = this.dragWindow.getOffset();
        if (point1 == null) {
          Dimension dimension = this.toolBar.getPreferredSize();
          point1 = new Point(dimension.width / 2, dimension.height / 2);
          this.dragWindow.setOffset(point1);
        } 
        Point point2 = new Point(paramPoint2.x + paramPoint1.x, paramPoint2.y + paramPoint1.y);
        Point point3 = new Point(point2.x - point1.x, point2.y - point1.y);
        if (this.dockingSource == null)
          this.dockingSource = this.toolBar.getParent(); 
        this.constraintBeforeFloating = calculateConstraint();
        Point point4 = this.dockingSource.getLocationOnScreen();
        Point point5 = new Point(point2.x - point4.x, point2.y - point4.y);
        if (canDock(this.dockingSource, point5)) {
          this.dragWindow.setBackground(getDockingColor());
          String str = getDockingConstraint(this.dockingSource, point5);
          int i = mapConstraintToOrientation(str);
          this.dragWindow.setOrientation(i);
          this.dragWindow.setBorderColor(this.dockingBorderColor);
        } else {
          this.dragWindow.setBackground(getFloatingColor());
          this.dragWindow.setBorderColor(this.floatingBorderColor);
          this.dragWindow.setOrientation(this.toolBar.getOrientation());
        } 
        this.dragWindow.setLocation(point3.x, point3.y);
        if (!this.dragWindow.isVisible()) {
          Dimension dimension = this.toolBar.getPreferredSize();
          this.dragWindow.setSize(dimension.width, dimension.height);
          this.dragWindow.show();
        } 
      } catch (IllegalComponentStateException illegalComponentStateException) {} 
  }
  
  protected void floatAt(Point paramPoint1, Point paramPoint2) {
    if (this.toolBar.isFloatable())
      try {
        Point point1 = this.dragWindow.getOffset();
        if (point1 == null) {
          point1 = paramPoint1;
          this.dragWindow.setOffset(point1);
        } 
        Point point2 = new Point(paramPoint2.x + paramPoint1.x, paramPoint2.y + paramPoint1.y);
        setFloatingLocation(point2.x - point1.x, point2.y - point1.y);
        if (this.dockingSource != null) {
          Point point3 = this.dockingSource.getLocationOnScreen();
          Point point4 = new Point(point2.x - point3.x, point2.y - point3.y);
          if (canDock(this.dockingSource, point4)) {
            setFloating(false, point4);
          } else {
            setFloating(true, null);
          } 
        } else {
          setFloating(true, null);
        } 
        this.dragWindow.setOffset(null);
      } catch (IllegalComponentStateException illegalComponentStateException) {} 
  }
  
  private Handler getHandler() {
    if (this.handler == null)
      this.handler = new Handler(null); 
    return this.handler;
  }
  
  protected ContainerListener createToolBarContListener() { return getHandler(); }
  
  protected FocusListener createToolBarFocusListener() { return getHandler(); }
  
  protected PropertyChangeListener createPropertyListener() { return getHandler(); }
  
  protected MouseInputListener createDockingListener() {
    (getHandler()).tb = this.toolBar;
    return getHandler();
  }
  
  protected WindowListener createFrameListener() { return new FrameListener(); }
  
  protected void paintDragWindow(Graphics paramGraphics) {
    paramGraphics.setColor(this.dragWindow.getBackground());
    int i = this.dragWindow.getWidth();
    int j = this.dragWindow.getHeight();
    paramGraphics.fillRect(0, 0, i, j);
    paramGraphics.setColor(this.dragWindow.getBorderColor());
    paramGraphics.drawRect(0, 0, i - 1, j - 1);
  }
  
  private static class Actions extends UIAction {
    private static final String NAVIGATE_RIGHT = "navigateRight";
    
    private static final String NAVIGATE_LEFT = "navigateLeft";
    
    private static final String NAVIGATE_UP = "navigateUp";
    
    private static final String NAVIGATE_DOWN = "navigateDown";
    
    public Actions(String param1String) { super(param1String); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      String str = getName();
      JToolBar jToolBar = (JToolBar)param1ActionEvent.getSource();
      BasicToolBarUI basicToolBarUI = (BasicToolBarUI)BasicLookAndFeel.getUIOfType(jToolBar.getUI(), BasicToolBarUI.class);
      if ("navigateRight" == str) {
        basicToolBarUI.navigateFocusedComp(3);
      } else if ("navigateLeft" == str) {
        basicToolBarUI.navigateFocusedComp(7);
      } else if ("navigateUp" == str) {
        basicToolBarUI.navigateFocusedComp(1);
      } else if ("navigateDown" == str) {
        basicToolBarUI.navigateFocusedComp(5);
      } 
    }
  }
  
  public class DockingListener implements MouseInputListener {
    protected JToolBar toolBar;
    
    protected boolean isDragging = false;
    
    protected Point origin = null;
    
    public DockingListener(JToolBar param1JToolBar) {
      this.toolBar = param1JToolBar;
      (this$0.getHandler()).tb = param1JToolBar;
    }
    
    public void mouseClicked(MouseEvent param1MouseEvent) { BasicToolBarUI.this.getHandler().mouseClicked(param1MouseEvent); }
    
    public void mousePressed(MouseEvent param1MouseEvent) {
      (this.this$0.getHandler()).tb = this.toolBar;
      BasicToolBarUI.this.getHandler().mousePressed(param1MouseEvent);
      this.isDragging = (this.this$0.getHandler()).isDragging;
    }
    
    public void mouseReleased(MouseEvent param1MouseEvent) {
      (this.this$0.getHandler()).tb = this.toolBar;
      (this.this$0.getHandler()).isDragging = this.isDragging;
      (this.this$0.getHandler()).origin = this.origin;
      BasicToolBarUI.this.getHandler().mouseReleased(param1MouseEvent);
      this.isDragging = (this.this$0.getHandler()).isDragging;
      this.origin = (this.this$0.getHandler()).origin;
    }
    
    public void mouseEntered(MouseEvent param1MouseEvent) { BasicToolBarUI.this.getHandler().mouseEntered(param1MouseEvent); }
    
    public void mouseExited(MouseEvent param1MouseEvent) { BasicToolBarUI.this.getHandler().mouseExited(param1MouseEvent); }
    
    public void mouseDragged(MouseEvent param1MouseEvent) {
      (this.this$0.getHandler()).tb = this.toolBar;
      (this.this$0.getHandler()).origin = this.origin;
      BasicToolBarUI.this.getHandler().mouseDragged(param1MouseEvent);
      this.isDragging = (this.this$0.getHandler()).isDragging;
      this.origin = (this.this$0.getHandler()).origin;
    }
    
    public void mouseMoved(MouseEvent param1MouseEvent) { BasicToolBarUI.this.getHandler().mouseMoved(param1MouseEvent); }
  }
  
  protected class DragWindow extends Window {
    Color borderColor = Color.gray;
    
    int orientation = BasicToolBarUI.this.toolBar.getOrientation();
    
    Point offset;
    
    DragWindow(Window param1Window) { super(param1Window); }
    
    public int getOrientation() { return this.orientation; }
    
    public void setOrientation(int param1Int) {
      if (isShowing()) {
        if (param1Int == this.orientation)
          return; 
        this.orientation = param1Int;
        Dimension dimension = getSize();
        setSize(new Dimension(dimension.height, dimension.width));
        if (this.offset != null)
          if (BasicGraphicsUtils.isLeftToRight(BasicToolBarUI.this.toolBar)) {
            setOffset(new Point(this.offset.y, this.offset.x));
          } else if (param1Int == 0) {
            setOffset(new Point(dimension.height - this.offset.y, this.offset.x));
          } else {
            setOffset(new Point(this.offset.y, dimension.width - this.offset.x));
          }  
        repaint();
      } 
    }
    
    public Point getOffset() { return this.offset; }
    
    public void setOffset(Point param1Point) { this.offset = param1Point; }
    
    public void setBorderColor(Color param1Color) {
      if (this.borderColor == param1Color)
        return; 
      this.borderColor = param1Color;
      repaint();
    }
    
    public Color getBorderColor() { return this.borderColor; }
    
    public void paint(Graphics param1Graphics) {
      BasicToolBarUI.this.paintDragWindow(param1Graphics);
      super.paint(param1Graphics);
    }
    
    public Insets getInsets() { return new Insets(1, 1, 1, 1); }
  }
  
  protected class FrameListener extends WindowAdapter {
    public void windowClosing(WindowEvent param1WindowEvent) {
      if (BasicToolBarUI.this.toolBar.isFloatable()) {
        if (BasicToolBarUI.this.dragWindow != null)
          BasicToolBarUI.this.dragWindow.setVisible(false); 
        BasicToolBarUI.this.floating = false;
        if (BasicToolBarUI.this.floatingToolBar == null)
          BasicToolBarUI.this.floatingToolBar = BasicToolBarUI.this.createFloatingWindow(BasicToolBarUI.this.toolBar); 
        if (BasicToolBarUI.this.floatingToolBar instanceof Window)
          ((Window)BasicToolBarUI.this.floatingToolBar).setVisible(false); 
        BasicToolBarUI.this.floatingToolBar.getContentPane().remove(BasicToolBarUI.this.toolBar);
        String str = BasicToolBarUI.this.constraintBeforeFloating;
        if (BasicToolBarUI.this.toolBar.getOrientation() == 0) {
          if (str == "West" || str == "East")
            str = "North"; 
        } else if (str == "North" || str == "South") {
          str = "West";
        } 
        if (BasicToolBarUI.this.dockingSource == null)
          BasicToolBarUI.this.dockingSource = BasicToolBarUI.this.toolBar.getParent(); 
        if (BasicToolBarUI.this.propertyListener != null)
          UIManager.removePropertyChangeListener(BasicToolBarUI.this.propertyListener); 
        BasicToolBarUI.this.dockingSource.add(BasicToolBarUI.this.toolBar, str);
        BasicToolBarUI.this.dockingSource.invalidate();
        Container container = BasicToolBarUI.this.dockingSource.getParent();
        if (container != null)
          container.validate(); 
        BasicToolBarUI.this.dockingSource.repaint();
      } 
    }
  }
  
  private class Handler implements ContainerListener, FocusListener, MouseInputListener, PropertyChangeListener {
    JToolBar tb;
    
    boolean isDragging = false;
    
    Point origin = null;
    
    private Handler() {}
    
    public void componentAdded(ContainerEvent param1ContainerEvent) {
      Component component = param1ContainerEvent.getChild();
      if (BasicToolBarUI.this.toolBarFocusListener != null)
        component.addFocusListener(BasicToolBarUI.this.toolBarFocusListener); 
      if (BasicToolBarUI.this.isRolloverBorders()) {
        BasicToolBarUI.this.setBorderToRollover(component);
      } else {
        BasicToolBarUI.this.setBorderToNonRollover(component);
      } 
    }
    
    public void componentRemoved(ContainerEvent param1ContainerEvent) {
      Component component = param1ContainerEvent.getChild();
      if (BasicToolBarUI.this.toolBarFocusListener != null)
        component.removeFocusListener(BasicToolBarUI.this.toolBarFocusListener); 
      BasicToolBarUI.this.setBorderToNormal(component);
    }
    
    public void focusGained(FocusEvent param1FocusEvent) {
      Component component = param1FocusEvent.getComponent();
      BasicToolBarUI.this.focusedCompIndex = BasicToolBarUI.this.toolBar.getComponentIndex(component);
    }
    
    public void focusLost(FocusEvent param1FocusEvent) {}
    
    public void mousePressed(MouseEvent param1MouseEvent) {
      if (!this.tb.isEnabled())
        return; 
      this.isDragging = false;
    }
    
    public void mouseReleased(MouseEvent param1MouseEvent) {
      if (!this.tb.isEnabled())
        return; 
      if (this.isDragging) {
        Point point = param1MouseEvent.getPoint();
        if (this.origin == null)
          this.origin = param1MouseEvent.getComponent().getLocationOnScreen(); 
        BasicToolBarUI.this.floatAt(point, this.origin);
      } 
      this.origin = null;
      this.isDragging = false;
    }
    
    public void mouseDragged(MouseEvent param1MouseEvent) {
      if (!this.tb.isEnabled())
        return; 
      this.isDragging = true;
      Point point = param1MouseEvent.getPoint();
      if (this.origin == null)
        this.origin = param1MouseEvent.getComponent().getLocationOnScreen(); 
      BasicToolBarUI.this.dragTo(point, this.origin);
    }
    
    public void mouseClicked(MouseEvent param1MouseEvent) {}
    
    public void mouseEntered(MouseEvent param1MouseEvent) {}
    
    public void mouseExited(MouseEvent param1MouseEvent) {}
    
    public void mouseMoved(MouseEvent param1MouseEvent) {}
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      String str = param1PropertyChangeEvent.getPropertyName();
      if (str == "lookAndFeel") {
        BasicToolBarUI.this.toolBar.updateUI();
      } else if (str == "orientation") {
        Component[] arrayOfComponent = BasicToolBarUI.this.toolBar.getComponents();
        int i = ((Integer)param1PropertyChangeEvent.getNewValue()).intValue();
        for (byte b = 0; b < arrayOfComponent.length; b++) {
          if (arrayOfComponent[b] instanceof JToolBar.Separator) {
            JToolBar.Separator separator = (JToolBar.Separator)arrayOfComponent[b];
            if (i == 0) {
              separator.setOrientation(1);
            } else {
              separator.setOrientation(0);
            } 
            Dimension dimension = separator.getSeparatorSize();
            if (dimension != null && dimension.width != dimension.height) {
              Dimension dimension1 = new Dimension(dimension.height, dimension.width);
              separator.setSeparatorSize(dimension1);
            } 
          } 
        } 
      } else if (str == IS_ROLLOVER) {
        BasicToolBarUI.this.installNormalBorders(BasicToolBarUI.this.toolBar);
        BasicToolBarUI.this.setRolloverBorders(((Boolean)param1PropertyChangeEvent.getNewValue()).booleanValue());
      } 
    }
  }
  
  protected class PropertyListener implements PropertyChangeListener {
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) { BasicToolBarUI.this.getHandler().propertyChange(param1PropertyChangeEvent); }
  }
  
  protected class ToolBarContListener implements ContainerListener {
    public void componentAdded(ContainerEvent param1ContainerEvent) { BasicToolBarUI.this.getHandler().componentAdded(param1ContainerEvent); }
    
    public void componentRemoved(ContainerEvent param1ContainerEvent) { BasicToolBarUI.this.getHandler().componentRemoved(param1ContainerEvent); }
  }
  
  protected class ToolBarFocusListener implements FocusListener {
    public void focusGained(FocusEvent param1FocusEvent) { BasicToolBarUI.this.getHandler().focusGained(param1FocusEvent); }
    
    public void focusLost(FocusEvent param1FocusEvent) { BasicToolBarUI.this.getHandler().focusLost(param1FocusEvent); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicToolBarUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */