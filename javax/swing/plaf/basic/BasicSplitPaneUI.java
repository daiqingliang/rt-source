package javax.swing.plaf.basic;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.peer.ComponentPeer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.SplitPaneUI;
import sun.swing.DefaultLookup;
import sun.swing.SwingUtilities2;
import sun.swing.UIAction;

public class BasicSplitPaneUI extends SplitPaneUI {
  protected static final String NON_CONTINUOUS_DIVIDER = "nonContinuousDivider";
  
  protected static int KEYBOARD_DIVIDER_MOVE_OFFSET = 3;
  
  protected JSplitPane splitPane;
  
  protected BasicHorizontalLayoutManager layoutManager;
  
  protected BasicSplitPaneDivider divider;
  
  protected PropertyChangeListener propertyChangeListener;
  
  protected FocusListener focusListener;
  
  private Handler handler;
  
  private Set<KeyStroke> managingFocusForwardTraversalKeys;
  
  private Set<KeyStroke> managingFocusBackwardTraversalKeys;
  
  protected int dividerSize;
  
  protected Component nonContinuousLayoutDivider;
  
  protected boolean draggingHW;
  
  protected int beginDragDividerLocation;
  
  @Deprecated
  protected KeyStroke upKey;
  
  @Deprecated
  protected KeyStroke downKey;
  
  @Deprecated
  protected KeyStroke leftKey;
  
  @Deprecated
  protected KeyStroke rightKey;
  
  @Deprecated
  protected KeyStroke homeKey;
  
  @Deprecated
  protected KeyStroke endKey;
  
  @Deprecated
  protected KeyStroke dividerResizeToggleKey;
  
  @Deprecated
  protected ActionListener keyboardUpLeftListener;
  
  @Deprecated
  protected ActionListener keyboardDownRightListener;
  
  @Deprecated
  protected ActionListener keyboardHomeListener;
  
  @Deprecated
  protected ActionListener keyboardEndListener;
  
  @Deprecated
  protected ActionListener keyboardResizeToggleListener;
  
  private int orientation;
  
  private int lastDragLocation;
  
  private boolean continuousLayout;
  
  private boolean dividerKeyboardResize;
  
  private boolean dividerLocationIsSet;
  
  private Color dividerDraggingColor;
  
  private boolean rememberPaneSizes;
  
  private boolean keepHidden = false;
  
  boolean painted;
  
  boolean ignoreDividerLocationChange;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new BasicSplitPaneUI(); }
  
  static void loadActionMap(LazyActionMap paramLazyActionMap) {
    paramLazyActionMap.put(new Actions("negativeIncrement"));
    paramLazyActionMap.put(new Actions("positiveIncrement"));
    paramLazyActionMap.put(new Actions("selectMin"));
    paramLazyActionMap.put(new Actions("selectMax"));
    paramLazyActionMap.put(new Actions("startResize"));
    paramLazyActionMap.put(new Actions("toggleFocus"));
    paramLazyActionMap.put(new Actions("focusOutForward"));
    paramLazyActionMap.put(new Actions("focusOutBackward"));
  }
  
  public void installUI(JComponent paramJComponent) {
    this.splitPane = (JSplitPane)paramJComponent;
    this.dividerLocationIsSet = false;
    this.dividerKeyboardResize = false;
    this.keepHidden = false;
    installDefaults();
    installListeners();
    installKeyboardActions();
    setLastDragLocation(-1);
  }
  
  protected void installDefaults() {
    LookAndFeel.installBorder(this.splitPane, "SplitPane.border");
    LookAndFeel.installColors(this.splitPane, "SplitPane.background", "SplitPane.foreground");
    LookAndFeel.installProperty(this.splitPane, "opaque", Boolean.TRUE);
    if (this.divider == null)
      this.divider = createDefaultDivider(); 
    this.divider.setBasicSplitPaneUI(this);
    Border border = this.divider.getBorder();
    if (border == null || !(border instanceof javax.swing.plaf.UIResource))
      this.divider.setBorder(UIManager.getBorder("SplitPaneDivider.border")); 
    this.dividerDraggingColor = UIManager.getColor("SplitPaneDivider.draggingColor");
    setOrientation(this.splitPane.getOrientation());
    Integer integer;
    LookAndFeel.installProperty(this.splitPane, "dividerSize", (integer = (Integer)UIManager.get("SplitPane.dividerSize")).valueOf((integer == null) ? 10 : integer.intValue()));
    this.divider.setDividerSize(this.splitPane.getDividerSize());
    this.dividerSize = this.divider.getDividerSize();
    this.splitPane.add(this.divider, "divider");
    setContinuousLayout(this.splitPane.isContinuousLayout());
    resetLayoutManager();
    if (this.nonContinuousLayoutDivider == null) {
      setNonContinuousLayoutDivider(createDefaultNonContinuousLayoutDivider(), true);
    } else {
      setNonContinuousLayoutDivider(this.nonContinuousLayoutDivider, true);
    } 
    if (this.managingFocusForwardTraversalKeys == null) {
      this.managingFocusForwardTraversalKeys = new HashSet();
      this.managingFocusForwardTraversalKeys.add(KeyStroke.getKeyStroke(9, 0));
    } 
    this.splitPane.setFocusTraversalKeys(0, this.managingFocusForwardTraversalKeys);
    if (this.managingFocusBackwardTraversalKeys == null) {
      this.managingFocusBackwardTraversalKeys = new HashSet();
      this.managingFocusBackwardTraversalKeys.add(KeyStroke.getKeyStroke(9, 1));
    } 
    this.splitPane.setFocusTraversalKeys(1, this.managingFocusBackwardTraversalKeys);
  }
  
  protected void installListeners() {
    if ((this.propertyChangeListener = createPropertyChangeListener()) != null)
      this.splitPane.addPropertyChangeListener(this.propertyChangeListener); 
    if ((this.focusListener = createFocusListener()) != null)
      this.splitPane.addFocusListener(this.focusListener); 
  }
  
  protected void installKeyboardActions() {
    InputMap inputMap = getInputMap(1);
    SwingUtilities.replaceUIInputMap(this.splitPane, 1, inputMap);
    LazyActionMap.installLazyActionMap(this.splitPane, BasicSplitPaneUI.class, "SplitPane.actionMap");
  }
  
  InputMap getInputMap(int paramInt) { return (paramInt == 1) ? (InputMap)DefaultLookup.get(this.splitPane, this, "SplitPane.ancestorInputMap") : null; }
  
  public void uninstallUI(JComponent paramJComponent) {
    uninstallKeyboardActions();
    uninstallListeners();
    uninstallDefaults();
    this.dividerLocationIsSet = false;
    this.dividerKeyboardResize = false;
    this.splitPane = null;
  }
  
  protected void uninstallDefaults() {
    if (this.splitPane.getLayout() == this.layoutManager)
      this.splitPane.setLayout(null); 
    if (this.nonContinuousLayoutDivider != null)
      this.splitPane.remove(this.nonContinuousLayoutDivider); 
    LookAndFeel.uninstallBorder(this.splitPane);
    Border border = this.divider.getBorder();
    if (border instanceof javax.swing.plaf.UIResource)
      this.divider.setBorder(null); 
    this.splitPane.remove(this.divider);
    this.divider.setBasicSplitPaneUI(null);
    this.layoutManager = null;
    this.divider = null;
    this.nonContinuousLayoutDivider = null;
    setNonContinuousLayoutDivider(null);
    this.splitPane.setFocusTraversalKeys(0, null);
    this.splitPane.setFocusTraversalKeys(1, null);
  }
  
  protected void uninstallListeners() {
    if (this.propertyChangeListener != null) {
      this.splitPane.removePropertyChangeListener(this.propertyChangeListener);
      this.propertyChangeListener = null;
    } 
    if (this.focusListener != null) {
      this.splitPane.removeFocusListener(this.focusListener);
      this.focusListener = null;
    } 
    this.keyboardUpLeftListener = null;
    this.keyboardDownRightListener = null;
    this.keyboardHomeListener = null;
    this.keyboardEndListener = null;
    this.keyboardResizeToggleListener = null;
    this.handler = null;
  }
  
  protected void uninstallKeyboardActions() {
    SwingUtilities.replaceUIActionMap(this.splitPane, null);
    SwingUtilities.replaceUIInputMap(this.splitPane, 1, null);
  }
  
  protected PropertyChangeListener createPropertyChangeListener() { return getHandler(); }
  
  private Handler getHandler() {
    if (this.handler == null)
      this.handler = new Handler(null); 
    return this.handler;
  }
  
  protected FocusListener createFocusListener() { return getHandler(); }
  
  @Deprecated
  protected ActionListener createKeyboardUpLeftListener() { return new KeyboardUpLeftHandler(); }
  
  @Deprecated
  protected ActionListener createKeyboardDownRightListener() { return new KeyboardDownRightHandler(); }
  
  @Deprecated
  protected ActionListener createKeyboardHomeListener() { return new KeyboardHomeHandler(); }
  
  @Deprecated
  protected ActionListener createKeyboardEndListener() { return new KeyboardEndHandler(); }
  
  @Deprecated
  protected ActionListener createKeyboardResizeToggleListener() { return new KeyboardResizeToggleHandler(); }
  
  public int getOrientation() { return this.orientation; }
  
  public void setOrientation(int paramInt) { this.orientation = paramInt; }
  
  public boolean isContinuousLayout() { return this.continuousLayout; }
  
  public void setContinuousLayout(boolean paramBoolean) { this.continuousLayout = paramBoolean; }
  
  public int getLastDragLocation() { return this.lastDragLocation; }
  
  public void setLastDragLocation(int paramInt) { this.lastDragLocation = paramInt; }
  
  int getKeyboardMoveIncrement() { return 3; }
  
  public BasicSplitPaneDivider getDivider() { return this.divider; }
  
  protected Component createDefaultNonContinuousLayoutDivider() { return new Canvas() {
        public void paint(Graphics param1Graphics) {
          if (!BasicSplitPaneUI.this.isContinuousLayout() && BasicSplitPaneUI.this.getLastDragLocation() != -1) {
            Dimension dimension = BasicSplitPaneUI.this.splitPane.getSize();
            param1Graphics.setColor(BasicSplitPaneUI.this.dividerDraggingColor);
            if (BasicSplitPaneUI.this.orientation == 1) {
              param1Graphics.fillRect(0, 0, BasicSplitPaneUI.this.dividerSize - 1, dimension.height - 1);
            } else {
              param1Graphics.fillRect(0, 0, dimension.width - 1, BasicSplitPaneUI.this.dividerSize - 1);
            } 
          } 
        }
      }; }
  
  protected void setNonContinuousLayoutDivider(Component paramComponent) { setNonContinuousLayoutDivider(paramComponent, true); }
  
  protected void setNonContinuousLayoutDivider(Component paramComponent, boolean paramBoolean) {
    this.rememberPaneSizes = paramBoolean;
    if (this.nonContinuousLayoutDivider != null && this.splitPane != null)
      this.splitPane.remove(this.nonContinuousLayoutDivider); 
    this.nonContinuousLayoutDivider = paramComponent;
  }
  
  private void addHeavyweightDivider() {
    if (this.nonContinuousLayoutDivider != null && this.splitPane != null) {
      Component component1 = this.splitPane.getLeftComponent();
      Component component2 = this.splitPane.getRightComponent();
      int i = this.splitPane.getDividerLocation();
      if (component1 != null)
        this.splitPane.setLeftComponent(null); 
      if (component2 != null)
        this.splitPane.setRightComponent(null); 
      this.splitPane.remove(this.divider);
      this.splitPane.add(this.nonContinuousLayoutDivider, "nonContinuousDivider", this.splitPane.getComponentCount());
      this.splitPane.setLeftComponent(component1);
      this.splitPane.setRightComponent(component2);
      this.splitPane.add(this.divider, "divider");
      if (this.rememberPaneSizes)
        this.splitPane.setDividerLocation(i); 
    } 
  }
  
  public Component getNonContinuousLayoutDivider() { return this.nonContinuousLayoutDivider; }
  
  public JSplitPane getSplitPane() { return this.splitPane; }
  
  public BasicSplitPaneDivider createDefaultDivider() { return new BasicSplitPaneDivider(this); }
  
  public void resetToPreferredSizes(JSplitPane paramJSplitPane) {
    if (this.splitPane != null) {
      this.layoutManager.resetToPreferredSizes();
      this.splitPane.revalidate();
      this.splitPane.repaint();
    } 
  }
  
  public void setDividerLocation(JSplitPane paramJSplitPane, int paramInt) {
    if (!this.ignoreDividerLocationChange) {
      this.dividerLocationIsSet = true;
      this.splitPane.revalidate();
      this.splitPane.repaint();
      if (this.keepHidden) {
        Insets insets = this.splitPane.getInsets();
        int i = this.splitPane.getOrientation();
        if ((i == 0 && paramInt != insets.top && paramInt != this.splitPane.getHeight() - this.divider.getHeight() - insets.top) || (i == 1 && paramInt != insets.left && paramInt != this.splitPane.getWidth() - this.divider.getWidth() - insets.left))
          setKeepHidden(false); 
      } 
    } else {
      this.ignoreDividerLocationChange = false;
    } 
  }
  
  public int getDividerLocation(JSplitPane paramJSplitPane) { return (this.orientation == 1) ? (this.divider.getLocation()).x : (this.divider.getLocation()).y; }
  
  public int getMinimumDividerLocation(JSplitPane paramJSplitPane) {
    int i = 0;
    Component component = this.splitPane.getLeftComponent();
    if (component != null && component.isVisible()) {
      Insets insets = this.splitPane.getInsets();
      Dimension dimension = component.getMinimumSize();
      if (this.orientation == 1) {
        i = dimension.width;
      } else {
        i = dimension.height;
      } 
      if (insets != null)
        if (this.orientation == 1) {
          i += insets.left;
        } else {
          i += insets.top;
        }  
    } 
    return i;
  }
  
  public int getMaximumDividerLocation(JSplitPane paramJSplitPane) {
    Dimension dimension = this.splitPane.getSize();
    int i = 0;
    Component component = this.splitPane.getRightComponent();
    if (component != null) {
      Insets insets = this.splitPane.getInsets();
      Dimension dimension1 = new Dimension(0, 0);
      if (component.isVisible())
        dimension1 = component.getMinimumSize(); 
      if (this.orientation == 1) {
        i = dimension.width - dimension1.width;
      } else {
        i = dimension.height - dimension1.height;
      } 
      i -= this.dividerSize;
      if (insets != null)
        if (this.orientation == 1) {
          i -= insets.right;
        } else {
          i -= insets.top;
        }  
    } 
    return Math.max(getMinimumDividerLocation(this.splitPane), i);
  }
  
  public void finishedPaintingChildren(JSplitPane paramJSplitPane, Graphics paramGraphics) {
    if (paramJSplitPane == this.splitPane && getLastDragLocation() != -1 && !isContinuousLayout() && !this.draggingHW) {
      Dimension dimension = this.splitPane.getSize();
      paramGraphics.setColor(this.dividerDraggingColor);
      if (this.orientation == 1) {
        paramGraphics.fillRect(getLastDragLocation(), 0, this.dividerSize - 1, dimension.height - 1);
      } else {
        paramGraphics.fillRect(0, this.lastDragLocation, dimension.width - 1, this.dividerSize - 1);
      } 
    } 
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    if (!this.painted && this.splitPane.getDividerLocation() < 0) {
      this.ignoreDividerLocationChange = true;
      this.splitPane.setDividerLocation(getDividerLocation(this.splitPane));
    } 
    this.painted = true;
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent) { return (this.splitPane != null) ? this.layoutManager.preferredLayoutSize(this.splitPane) : new Dimension(0, 0); }
  
  public Dimension getMinimumSize(JComponent paramJComponent) { return (this.splitPane != null) ? this.layoutManager.minimumLayoutSize(this.splitPane) : new Dimension(0, 0); }
  
  public Dimension getMaximumSize(JComponent paramJComponent) { return (this.splitPane != null) ? this.layoutManager.maximumLayoutSize(this.splitPane) : new Dimension(0, 0); }
  
  public Insets getInsets(JComponent paramJComponent) { return null; }
  
  protected void resetLayoutManager() {
    if (this.orientation == 1) {
      this.layoutManager = new BasicHorizontalLayoutManager(0);
    } else {
      this.layoutManager = new BasicHorizontalLayoutManager(1);
    } 
    this.splitPane.setLayout(this.layoutManager);
    this.layoutManager.updateComponents();
    this.splitPane.revalidate();
    this.splitPane.repaint();
  }
  
  void setKeepHidden(boolean paramBoolean) { this.keepHidden = paramBoolean; }
  
  private boolean getKeepHidden() { return this.keepHidden; }
  
  protected void startDragging() {
    Component component1 = this.splitPane.getLeftComponent();
    Component component2 = this.splitPane.getRightComponent();
    this.beginDragDividerLocation = getDividerLocation(this.splitPane);
    this.draggingHW = false;
    ComponentPeer componentPeer;
    if (component1 != null && (componentPeer = component1.getPeer()) != null && !(componentPeer instanceof java.awt.peer.LightweightPeer)) {
      this.draggingHW = true;
    } else if (component2 != null && (componentPeer = component2.getPeer()) != null && !(componentPeer instanceof java.awt.peer.LightweightPeer)) {
      this.draggingHW = true;
    } 
    if (this.orientation == 1) {
      setLastDragLocation((this.divider.getBounds()).x);
      this.dividerSize = (this.divider.getSize()).width;
      if (!isContinuousLayout() && this.draggingHW) {
        this.nonContinuousLayoutDivider.setBounds(getLastDragLocation(), 0, this.dividerSize, this.splitPane.getHeight());
        addHeavyweightDivider();
      } 
    } else {
      setLastDragLocation((this.divider.getBounds()).y);
      this.dividerSize = (this.divider.getSize()).height;
      if (!isContinuousLayout() && this.draggingHW) {
        this.nonContinuousLayoutDivider.setBounds(0, getLastDragLocation(), this.splitPane.getWidth(), this.dividerSize);
        addHeavyweightDivider();
      } 
    } 
  }
  
  protected void dragDividerTo(int paramInt) {
    if (getLastDragLocation() != paramInt)
      if (isContinuousLayout()) {
        this.splitPane.setDividerLocation(paramInt);
        setLastDragLocation(paramInt);
      } else {
        int i = getLastDragLocation();
        setLastDragLocation(paramInt);
        if (this.orientation == 1) {
          if (this.draggingHW) {
            this.nonContinuousLayoutDivider.setLocation(getLastDragLocation(), 0);
          } else {
            int j = this.splitPane.getHeight();
            this.splitPane.repaint(i, 0, this.dividerSize, j);
            this.splitPane.repaint(paramInt, 0, this.dividerSize, j);
          } 
        } else if (this.draggingHW) {
          this.nonContinuousLayoutDivider.setLocation(0, getLastDragLocation());
        } else {
          int j = this.splitPane.getWidth();
          this.splitPane.repaint(0, i, j, this.dividerSize);
          this.splitPane.repaint(0, paramInt, j, this.dividerSize);
        } 
      }  
  }
  
  protected void finishDraggingTo(int paramInt) {
    dragDividerTo(paramInt);
    setLastDragLocation(-1);
    if (!isContinuousLayout()) {
      Component component = this.splitPane.getLeftComponent();
      Rectangle rectangle = component.getBounds();
      if (this.draggingHW) {
        if (this.orientation == 1) {
          this.nonContinuousLayoutDivider.setLocation(-this.dividerSize, 0);
        } else {
          this.nonContinuousLayoutDivider.setLocation(0, -this.dividerSize);
        } 
        this.splitPane.remove(this.nonContinuousLayoutDivider);
      } 
      this.splitPane.setDividerLocation(paramInt);
    } 
  }
  
  @Deprecated
  protected int getDividerBorderSize() { return 1; }
  
  private static class Actions extends UIAction {
    private static final String NEGATIVE_INCREMENT = "negativeIncrement";
    
    private static final String POSITIVE_INCREMENT = "positiveIncrement";
    
    private static final String SELECT_MIN = "selectMin";
    
    private static final String SELECT_MAX = "selectMax";
    
    private static final String START_RESIZE = "startResize";
    
    private static final String TOGGLE_FOCUS = "toggleFocus";
    
    private static final String FOCUS_OUT_FORWARD = "focusOutForward";
    
    private static final String FOCUS_OUT_BACKWARD = "focusOutBackward";
    
    Actions(String param1String) { super(param1String); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JSplitPane jSplitPane = (JSplitPane)param1ActionEvent.getSource();
      BasicSplitPaneUI basicSplitPaneUI = (BasicSplitPaneUI)BasicLookAndFeel.getUIOfType(jSplitPane.getUI(), BasicSplitPaneUI.class);
      if (basicSplitPaneUI == null)
        return; 
      String str = getName();
      if (str == "negativeIncrement") {
        if (basicSplitPaneUI.dividerKeyboardResize)
          jSplitPane.setDividerLocation(Math.max(0, basicSplitPaneUI.getDividerLocation(jSplitPane) - basicSplitPaneUI.getKeyboardMoveIncrement())); 
      } else if (str == "positiveIncrement") {
        if (basicSplitPaneUI.dividerKeyboardResize)
          jSplitPane.setDividerLocation(basicSplitPaneUI.getDividerLocation(jSplitPane) + basicSplitPaneUI.getKeyboardMoveIncrement()); 
      } else if (str == "selectMin") {
        if (basicSplitPaneUI.dividerKeyboardResize)
          jSplitPane.setDividerLocation(0); 
      } else if (str == "selectMax") {
        if (basicSplitPaneUI.dividerKeyboardResize) {
          Insets insets = jSplitPane.getInsets();
          int i = (insets != null) ? insets.bottom : 0;
          int j = (insets != null) ? insets.right : 0;
          if (basicSplitPaneUI.orientation == 0) {
            jSplitPane.setDividerLocation(jSplitPane.getHeight() - i);
          } else {
            jSplitPane.setDividerLocation(jSplitPane.getWidth() - j);
          } 
        } 
      } else if (str == "startResize") {
        if (!basicSplitPaneUI.dividerKeyboardResize) {
          jSplitPane.requestFocus();
        } else {
          JSplitPane jSplitPane1 = (JSplitPane)SwingUtilities.getAncestorOfClass(JSplitPane.class, jSplitPane);
          if (jSplitPane1 != null)
            jSplitPane1.requestFocus(); 
        } 
      } else if (str == "toggleFocus") {
        toggleFocus(jSplitPane);
      } else if (str == "focusOutForward") {
        moveFocus(jSplitPane, 1);
      } else if (str == "focusOutBackward") {
        moveFocus(jSplitPane, -1);
      } 
    }
    
    private void moveFocus(JSplitPane param1JSplitPane, int param1Int) {
      Container container = param1JSplitPane.getFocusCycleRootAncestor();
      FocusTraversalPolicy focusTraversalPolicy = container.getFocusTraversalPolicy();
      Component component = (param1Int > 0) ? focusTraversalPolicy.getComponentAfter(container, param1JSplitPane) : focusTraversalPolicy.getComponentBefore(container, param1JSplitPane);
      HashSet hashSet = new HashSet();
      if (param1JSplitPane.isAncestorOf(component))
        do {
          hashSet.add(component);
          container = component.getFocusCycleRootAncestor();
          focusTraversalPolicy = container.getFocusTraversalPolicy();
          component = (param1Int > 0) ? focusTraversalPolicy.getComponentAfter(container, component) : focusTraversalPolicy.getComponentBefore(container, component);
        } while (param1JSplitPane.isAncestorOf(component) && !hashSet.contains(component)); 
      if (component != null && !param1JSplitPane.isAncestorOf(component))
        component.requestFocus(); 
    }
    
    private void toggleFocus(JSplitPane param1JSplitPane) {
      Component component1 = param1JSplitPane.getLeftComponent();
      Component component2 = param1JSplitPane.getRightComponent();
      KeyboardFocusManager keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
      Component component3 = keyboardFocusManager.getFocusOwner();
      Component component4 = getNextSide(param1JSplitPane, component3);
      if (component4 != null) {
        if (component3 != null && ((SwingUtilities.isDescendingFrom(component3, component1) && SwingUtilities.isDescendingFrom(component4, component1)) || (SwingUtilities.isDescendingFrom(component3, component2) && SwingUtilities.isDescendingFrom(component4, component2))))
          return; 
        SwingUtilities2.compositeRequestFocus(component4);
      } 
    }
    
    private Component getNextSide(JSplitPane param1JSplitPane, Component param1Component) {
      Component component3;
      Component component1 = param1JSplitPane.getLeftComponent();
      Component component2 = param1JSplitPane.getRightComponent();
      if (param1Component != null && SwingUtilities.isDescendingFrom(param1Component, component1) && component2 != null) {
        component3 = getFirstAvailableComponent(component2);
        if (component3 != null)
          return component3; 
      } 
      JSplitPane jSplitPane = (JSplitPane)SwingUtilities.getAncestorOfClass(JSplitPane.class, param1JSplitPane);
      if (jSplitPane != null) {
        component3 = getNextSide(jSplitPane, param1Component);
      } else {
        component3 = getFirstAvailableComponent(component1);
        if (component3 == null)
          component3 = getFirstAvailableComponent(component2); 
      } 
      return component3;
    }
    
    private Component getFirstAvailableComponent(Component param1Component) {
      if (param1Component != null && param1Component instanceof JSplitPane) {
        JSplitPane jSplitPane = (JSplitPane)param1Component;
        Component component = getFirstAvailableComponent(jSplitPane.getLeftComponent());
        if (component != null) {
          param1Component = component;
        } else {
          param1Component = getFirstAvailableComponent(jSplitPane.getRightComponent());
        } 
      } 
      return param1Component;
    }
  }
  
  public class BasicHorizontalLayoutManager implements LayoutManager2 {
    protected int[] sizes;
    
    protected Component[] components;
    
    private int lastSplitPaneSize;
    
    private boolean doReset;
    
    private int axis;
    
    BasicHorizontalLayoutManager(BasicSplitPaneUI this$0) { this(0); }
    
    BasicHorizontalLayoutManager(int param1Int) {
      this.axis = param1Int;
      this.components = new Component[3];
      this.components[2] = null;
      this.components[1] = null;
      this.components[0] = null;
      this.sizes = new int[3];
    }
    
    public void layoutContainer(Container param1Container) {
      Dimension dimension1 = param1Container.getSize();
      if (dimension1.height <= 0 || dimension1.width <= 0) {
        this.lastSplitPaneSize = 0;
        return;
      } 
      int i = BasicSplitPaneUI.this.splitPane.getDividerLocation();
      Insets insets = BasicSplitPaneUI.this.splitPane.getInsets();
      int j = getAvailableSize(dimension1, insets);
      int k = getSizeForPrimaryAxis(dimension1);
      int m = BasicSplitPaneUI.this.getDividerLocation(BasicSplitPaneUI.this.splitPane);
      int n = getSizeForPrimaryAxis(insets, true);
      Dimension dimension2 = (this.components[2] == null) ? null : this.components[2].getPreferredSize();
      if ((this.doReset && !BasicSplitPaneUI.this.dividerLocationIsSet) || i < 0) {
        resetToPreferredSizes(j);
      } else if (this.lastSplitPaneSize <= 0 || j == this.lastSplitPaneSize || !BasicSplitPaneUI.this.painted || (dimension2 != null && getSizeForPrimaryAxis(dimension2) != this.sizes[2])) {
        if (dimension2 != null) {
          this.sizes[2] = getSizeForPrimaryAxis(dimension2);
        } else {
          this.sizes[2] = 0;
        } 
        setDividerLocation(i - n, j);
        BasicSplitPaneUI.this.dividerLocationIsSet = false;
      } else if (j != this.lastSplitPaneSize) {
        distributeSpace(j - this.lastSplitPaneSize, BasicSplitPaneUI.this.getKeepHidden());
      } 
      this.doReset = false;
      BasicSplitPaneUI.this.dividerLocationIsSet = false;
      this.lastSplitPaneSize = j;
      int i1 = getInitialLocation(insets);
      byte b = 0;
      while (b < 3) {
        if (this.components[b] != null && this.components[b].isVisible()) {
          setComponentToSize(this.components[b], this.sizes[b], i1, insets, dimension1);
          i1 += this.sizes[b];
        } 
        switch (b) {
          case false:
            b = 2;
          case true:
            b = 1;
          case true:
            b = 3;
        } 
      } 
      if (BasicSplitPaneUI.this.painted) {
        int i2 = BasicSplitPaneUI.this.getDividerLocation(BasicSplitPaneUI.this.splitPane);
        if (i2 != i - n) {
          int i3 = BasicSplitPaneUI.this.splitPane.getLastDividerLocation();
          BasicSplitPaneUI.this.ignoreDividerLocationChange = true;
          try {
            BasicSplitPaneUI.this.splitPane.setDividerLocation(i2);
            BasicSplitPaneUI.this.splitPane.setLastDividerLocation(i3);
          } finally {
            BasicSplitPaneUI.this.ignoreDividerLocationChange = false;
          } 
        } 
      } 
    }
    
    public void addLayoutComponent(String param1String, Component param1Component) {
      boolean bool = true;
      if (param1String != null) {
        if (param1String.equals("divider")) {
          this.components[2] = param1Component;
          this.sizes[2] = getSizeForPrimaryAxis(param1Component.getPreferredSize());
        } else if (param1String.equals("left") || param1String.equals("top")) {
          this.components[0] = param1Component;
          this.sizes[0] = 0;
        } else if (param1String.equals("right") || param1String.equals("bottom")) {
          this.components[1] = param1Component;
          this.sizes[1] = 0;
        } else if (!param1String.equals("nonContinuousDivider")) {
          bool = false;
        } 
      } else {
        bool = false;
      } 
      if (!bool)
        throw new IllegalArgumentException("cannot add to layout: unknown constraint: " + param1String); 
      this.doReset = true;
    }
    
    public Dimension minimumLayoutSize(Container param1Container) {
      int i = 0;
      int j = 0;
      Insets insets = BasicSplitPaneUI.this.splitPane.getInsets();
      for (byte b = 0; b < 3; b++) {
        if (this.components[b] != null) {
          Dimension dimension = this.components[b].getMinimumSize();
          int k = getSizeForSecondaryAxis(dimension);
          i += getSizeForPrimaryAxis(dimension);
          if (k > j)
            j = k; 
        } 
      } 
      if (insets != null) {
        i += getSizeForPrimaryAxis(insets, true) + getSizeForPrimaryAxis(insets, false);
        j += getSizeForSecondaryAxis(insets, true) + getSizeForSecondaryAxis(insets, false);
      } 
      return (this.axis == 0) ? new Dimension(i, j) : new Dimension(j, i);
    }
    
    public Dimension preferredLayoutSize(Container param1Container) {
      int i = 0;
      int j = 0;
      Insets insets = BasicSplitPaneUI.this.splitPane.getInsets();
      for (byte b = 0; b < 3; b++) {
        if (this.components[b] != null) {
          Dimension dimension = this.components[b].getPreferredSize();
          int k = getSizeForSecondaryAxis(dimension);
          i += getSizeForPrimaryAxis(dimension);
          if (k > j)
            j = k; 
        } 
      } 
      if (insets != null) {
        i += getSizeForPrimaryAxis(insets, true) + getSizeForPrimaryAxis(insets, false);
        j += getSizeForSecondaryAxis(insets, true) + getSizeForSecondaryAxis(insets, false);
      } 
      return (this.axis == 0) ? new Dimension(i, j) : new Dimension(j, i);
    }
    
    public void removeLayoutComponent(Component param1Component) {
      for (byte b = 0; b < 3; b++) {
        if (this.components[b] == param1Component) {
          this.components[b] = null;
          this.sizes[b] = 0;
          this.doReset = true;
        } 
      } 
    }
    
    public void addLayoutComponent(Component param1Component, Object param1Object) {
      if (param1Object == null || param1Object instanceof String) {
        addLayoutComponent((String)param1Object, param1Component);
      } else {
        throw new IllegalArgumentException("cannot add to layout: constraint must be a string (or null)");
      } 
    }
    
    public float getLayoutAlignmentX(Container param1Container) { return 0.0F; }
    
    public float getLayoutAlignmentY(Container param1Container) { return 0.0F; }
    
    public void invalidateLayout(Container param1Container) {}
    
    public Dimension maximumLayoutSize(Container param1Container) { return new Dimension(2147483647, 2147483647); }
    
    public void resetToPreferredSizes() { this.doReset = true; }
    
    protected void resetSizeAt(int param1Int) {
      this.sizes[param1Int] = 0;
      this.doReset = true;
    }
    
    protected void setSizes(int[] param1ArrayOfInt) { System.arraycopy(param1ArrayOfInt, 0, this.sizes, 0, 3); }
    
    protected int[] getSizes() {
      int[] arrayOfInt = new int[3];
      System.arraycopy(this.sizes, 0, arrayOfInt, 0, 3);
      return arrayOfInt;
    }
    
    protected int getPreferredSizeOfComponent(Component param1Component) { return getSizeForPrimaryAxis(param1Component.getPreferredSize()); }
    
    int getMinimumSizeOfComponent(Component param1Component) { return getSizeForPrimaryAxis(param1Component.getMinimumSize()); }
    
    protected int getSizeOfComponent(Component param1Component) { return getSizeForPrimaryAxis(param1Component.getSize()); }
    
    protected int getAvailableSize(Dimension param1Dimension, Insets param1Insets) { return (param1Insets == null) ? getSizeForPrimaryAxis(param1Dimension) : (getSizeForPrimaryAxis(param1Dimension) - getSizeForPrimaryAxis(param1Insets, true) + getSizeForPrimaryAxis(param1Insets, false)); }
    
    protected int getInitialLocation(Insets param1Insets) { return (param1Insets != null) ? getSizeForPrimaryAxis(param1Insets, true) : 0; }
    
    protected void setComponentToSize(Component param1Component, int param1Int1, int param1Int2, Insets param1Insets, Dimension param1Dimension) {
      if (param1Insets != null) {
        if (this.axis == 0) {
          param1Component.setBounds(param1Int2, param1Insets.top, param1Int1, param1Dimension.height - param1Insets.top + param1Insets.bottom);
        } else {
          param1Component.setBounds(param1Insets.left, param1Int2, param1Dimension.width - param1Insets.left + param1Insets.right, param1Int1);
        } 
      } else if (this.axis == 0) {
        param1Component.setBounds(param1Int2, 0, param1Int1, param1Dimension.height);
      } else {
        param1Component.setBounds(0, param1Int2, param1Dimension.width, param1Int1);
      } 
    }
    
    int getSizeForPrimaryAxis(Dimension param1Dimension) { return (this.axis == 0) ? param1Dimension.width : param1Dimension.height; }
    
    int getSizeForSecondaryAxis(Dimension param1Dimension) { return (this.axis == 0) ? param1Dimension.height : param1Dimension.width; }
    
    int getSizeForPrimaryAxis(Insets param1Insets, boolean param1Boolean) { return (this.axis == 0) ? (param1Boolean ? param1Insets.left : param1Insets.right) : (param1Boolean ? param1Insets.top : param1Insets.bottom); }
    
    int getSizeForSecondaryAxis(Insets param1Insets, boolean param1Boolean) { return (this.axis == 0) ? (param1Boolean ? param1Insets.top : param1Insets.bottom) : (param1Boolean ? param1Insets.left : param1Insets.right); }
    
    protected void updateComponents() {
      Component component1 = BasicSplitPaneUI.this.splitPane.getLeftComponent();
      if (this.components[false] != component1) {
        this.components[0] = component1;
        if (component1 == null) {
          this.sizes[0] = 0;
        } else {
          this.sizes[0] = -1;
        } 
      } 
      component1 = BasicSplitPaneUI.this.splitPane.getRightComponent();
      if (this.components[true] != component1) {
        this.components[1] = component1;
        if (component1 == null) {
          this.sizes[1] = 0;
        } else {
          this.sizes[1] = -1;
        } 
      } 
      Component[] arrayOfComponent = BasicSplitPaneUI.this.splitPane.getComponents();
      Component component2 = this.components[2];
      this.components[2] = null;
      for (int i = arrayOfComponent.length - 1; i >= 0; i--) {
        if (arrayOfComponent[i] != this.components[false] && arrayOfComponent[i] != this.components[true] && arrayOfComponent[i] != BasicSplitPaneUI.this.nonContinuousLayoutDivider) {
          if (component2 != arrayOfComponent[i]) {
            this.components[2] = arrayOfComponent[i];
            break;
          } 
          this.components[2] = component2;
          break;
        } 
      } 
      if (this.components[2] == null) {
        this.sizes[2] = 0;
      } else {
        this.sizes[2] = getSizeForPrimaryAxis(this.components[2].getPreferredSize());
      } 
    }
    
    void setDividerLocation(int param1Int1, int param1Int2) {
      boolean bool1 = (this.components[false] != null && this.components[0].isVisible()) ? 1 : 0;
      boolean bool2 = (this.components[true] != null && this.components[1].isVisible()) ? 1 : 0;
      boolean bool3 = (this.components[2] != null && this.components[2].isVisible()) ? 1 : 0;
      int i = param1Int2;
      if (bool3)
        i -= this.sizes[2]; 
      param1Int1 = Math.max(0, Math.min(param1Int1, i));
      if (bool1) {
        if (bool2) {
          this.sizes[0] = param1Int1;
          this.sizes[1] = i - param1Int1;
        } else {
          this.sizes[0] = i;
          this.sizes[1] = 0;
        } 
      } else if (bool2) {
        this.sizes[1] = i;
        this.sizes[0] = 0;
      } 
    }
    
    int[] getPreferredSizes() {
      int[] arrayOfInt = new int[3];
      for (byte b = 0; b < 3; b++) {
        if (this.components[b] != null && this.components[b].isVisible()) {
          arrayOfInt[b] = getPreferredSizeOfComponent(this.components[b]);
        } else {
          arrayOfInt[b] = -1;
        } 
      } 
      return arrayOfInt;
    }
    
    int[] getMinimumSizes() {
      int[] arrayOfInt = new int[3];
      for (byte b = 0; b < 2; b++) {
        if (this.components[b] != null && this.components[b].isVisible()) {
          arrayOfInt[b] = getMinimumSizeOfComponent(this.components[b]);
        } else {
          arrayOfInt[b] = -1;
        } 
      } 
      arrayOfInt[2] = (this.components[2] != null) ? getMinimumSizeOfComponent(this.components[2]) : -1;
      return arrayOfInt;
    }
    
    void resetToPreferredSizes(int param1Int) {
      int[] arrayOfInt = getPreferredSizes();
      int i = 0;
      byte b;
      for (b = 0; b < 3; b++) {
        if (arrayOfInt[b] != -1)
          i += arrayOfInt[b]; 
      } 
      if (i > param1Int) {
        arrayOfInt = getMinimumSizes();
        i = 0;
        for (b = 0; b < 3; b++) {
          if (arrayOfInt[b] != -1)
            i += arrayOfInt[b]; 
        } 
      } 
      setSizes(arrayOfInt);
      distributeSpace(param1Int - i, false);
    }
    
    void distributeSpace(int param1Int, boolean param1Boolean) {
      boolean bool1 = (this.components[false] != null && this.components[0].isVisible()) ? 1 : 0;
      boolean bool2 = (this.components[true] != null && this.components[1].isVisible()) ? 1 : 0;
      if (param1Boolean)
        if (bool1 && getSizeForPrimaryAxis(this.components[0].getSize()) == 0) {
          bool1 = false;
          if (bool2 && getSizeForPrimaryAxis(this.components[1].getSize()) == 0)
            bool1 = true; 
        } else if (bool2 && getSizeForPrimaryAxis(this.components[1].getSize()) == 0) {
          bool2 = false;
        }  
      if (bool1 && bool2) {
        double d = BasicSplitPaneUI.this.splitPane.getResizeWeight();
        int i = (int)(d * param1Int);
        int j = param1Int - i;
        this.sizes[0] = this.sizes[0] + i;
        this.sizes[1] = this.sizes[1] + j;
        int k = getMinimumSizeOfComponent(this.components[0]);
        int m = getMinimumSizeOfComponent(this.components[1]);
        boolean bool3 = (this.sizes[0] >= k) ? 1 : 0;
        boolean bool4 = (this.sizes[1] >= m) ? 1 : 0;
        if (!bool3 && !bool4) {
          if (this.sizes[0] < 0) {
            this.sizes[1] = this.sizes[1] + this.sizes[0];
            this.sizes[0] = 0;
          } else if (this.sizes[1] < 0) {
            this.sizes[0] = this.sizes[0] + this.sizes[1];
            this.sizes[1] = 0;
          } 
        } else if (!bool3) {
          if (this.sizes[1] - k - this.sizes[0] < m) {
            if (this.sizes[0] < 0) {
              this.sizes[1] = this.sizes[1] + this.sizes[0];
              this.sizes[0] = 0;
            } 
          } else {
            this.sizes[1] = this.sizes[1] - k - this.sizes[0];
            this.sizes[0] = k;
          } 
        } else if (!bool4) {
          if (this.sizes[0] - m - this.sizes[1] < k) {
            if (this.sizes[1] < 0) {
              this.sizes[0] = this.sizes[0] + this.sizes[1];
              this.sizes[1] = 0;
            } 
          } else {
            this.sizes[0] = this.sizes[0] - m - this.sizes[1];
            this.sizes[1] = m;
          } 
        } 
        if (this.sizes[0] < 0)
          this.sizes[0] = 0; 
        if (this.sizes[1] < 0)
          this.sizes[1] = 0; 
      } else if (bool1) {
        this.sizes[0] = Math.max(0, this.sizes[0] + param1Int);
      } else if (bool2) {
        this.sizes[1] = Math.max(0, this.sizes[1] + param1Int);
      } 
    }
  }
  
  public class BasicVerticalLayoutManager extends BasicHorizontalLayoutManager {
    public BasicVerticalLayoutManager() { super(BasicSplitPaneUI.this, 1); }
  }
  
  public class FocusHandler extends FocusAdapter {
    public void focusGained(FocusEvent param1FocusEvent) { BasicSplitPaneUI.this.getHandler().focusGained(param1FocusEvent); }
    
    public void focusLost(FocusEvent param1FocusEvent) { BasicSplitPaneUI.this.getHandler().focusLost(param1FocusEvent); }
  }
  
  private class Handler implements FocusListener, PropertyChangeListener {
    private Handler() {}
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      if (param1PropertyChangeEvent.getSource() == BasicSplitPaneUI.this.splitPane) {
        String str = param1PropertyChangeEvent.getPropertyName();
        if (str == "orientation") {
          BasicSplitPaneUI.this.orientation = BasicSplitPaneUI.this.splitPane.getOrientation();
          BasicSplitPaneUI.this.resetLayoutManager();
        } else if (str == "continuousLayout") {
          BasicSplitPaneUI.this.setContinuousLayout(BasicSplitPaneUI.this.splitPane.isContinuousLayout());
          if (!BasicSplitPaneUI.this.isContinuousLayout())
            if (BasicSplitPaneUI.this.nonContinuousLayoutDivider == null) {
              BasicSplitPaneUI.this.setNonContinuousLayoutDivider(BasicSplitPaneUI.this.createDefaultNonContinuousLayoutDivider(), true);
            } else if (BasicSplitPaneUI.this.nonContinuousLayoutDivider.getParent() == null) {
              BasicSplitPaneUI.this.setNonContinuousLayoutDivider(BasicSplitPaneUI.this.nonContinuousLayoutDivider, true);
            }  
        } else if (str == "dividerSize") {
          BasicSplitPaneUI.this.divider.setDividerSize(BasicSplitPaneUI.this.splitPane.getDividerSize());
          BasicSplitPaneUI.this.dividerSize = BasicSplitPaneUI.this.divider.getDividerSize();
          BasicSplitPaneUI.this.splitPane.revalidate();
          BasicSplitPaneUI.this.splitPane.repaint();
        } 
      } 
    }
    
    public void focusGained(FocusEvent param1FocusEvent) {
      BasicSplitPaneUI.this.dividerKeyboardResize = true;
      BasicSplitPaneUI.this.splitPane.repaint();
    }
    
    public void focusLost(FocusEvent param1FocusEvent) {
      BasicSplitPaneUI.this.dividerKeyboardResize = false;
      BasicSplitPaneUI.this.splitPane.repaint();
    }
  }
  
  public class KeyboardDownRightHandler implements ActionListener {
    public void actionPerformed(ActionEvent param1ActionEvent) {
      if (BasicSplitPaneUI.this.dividerKeyboardResize)
        BasicSplitPaneUI.this.splitPane.setDividerLocation(BasicSplitPaneUI.this.getDividerLocation(BasicSplitPaneUI.this.splitPane) + BasicSplitPaneUI.this.getKeyboardMoveIncrement()); 
    }
  }
  
  public class KeyboardEndHandler implements ActionListener {
    public void actionPerformed(ActionEvent param1ActionEvent) {
      if (BasicSplitPaneUI.this.dividerKeyboardResize) {
        Insets insets = BasicSplitPaneUI.this.splitPane.getInsets();
        int i = (insets != null) ? insets.bottom : 0;
        int j = (insets != null) ? insets.right : 0;
        if (BasicSplitPaneUI.this.orientation == 0) {
          BasicSplitPaneUI.this.splitPane.setDividerLocation(BasicSplitPaneUI.this.splitPane.getHeight() - i);
        } else {
          BasicSplitPaneUI.this.splitPane.setDividerLocation(BasicSplitPaneUI.this.splitPane.getWidth() - j);
        } 
      } 
    }
  }
  
  public class KeyboardHomeHandler implements ActionListener {
    public void actionPerformed(ActionEvent param1ActionEvent) {
      if (BasicSplitPaneUI.this.dividerKeyboardResize)
        BasicSplitPaneUI.this.splitPane.setDividerLocation(0); 
    }
  }
  
  public class KeyboardResizeToggleHandler implements ActionListener {
    public void actionPerformed(ActionEvent param1ActionEvent) {
      if (!BasicSplitPaneUI.this.dividerKeyboardResize)
        BasicSplitPaneUI.this.splitPane.requestFocus(); 
    }
  }
  
  public class KeyboardUpLeftHandler implements ActionListener {
    public void actionPerformed(ActionEvent param1ActionEvent) {
      if (BasicSplitPaneUI.this.dividerKeyboardResize)
        BasicSplitPaneUI.this.splitPane.setDividerLocation(Math.max(0, BasicSplitPaneUI.this.getDividerLocation(BasicSplitPaneUI.this.splitPane) - BasicSplitPaneUI.this.getKeyboardMoveIncrement())); 
    }
  }
  
  public class PropertyHandler implements PropertyChangeListener {
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) { BasicSplitPaneUI.this.getHandler().propertyChange(param1PropertyChangeEvent); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicSplitPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */