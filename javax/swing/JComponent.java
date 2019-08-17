package javax.swing;

import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.Transient;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleExtendedComponent;
import javax.accessibility.AccessibleKeyBinding;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.AncestorListener;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.ComponentUI;
import sun.awt.CausedFocusEvent;
import sun.awt.RequestFocusController;
import sun.awt.SunToolkit;
import sun.swing.SwingUtilities2;

public abstract class JComponent extends Container implements Serializable, TransferHandler.HasGetTransferHandler {
  private static final String uiClassID = "ComponentUI";
  
  private static final Hashtable<ObjectInputStream, ReadObjectCallback> readObjectCallbacks = new Hashtable(1);
  
  private static Set<KeyStroke> managingFocusForwardTraversalKeys;
  
  private static Set<KeyStroke> managingFocusBackwardTraversalKeys;
  
  private static final int NOT_OBSCURED = 0;
  
  private static final int PARTIALLY_OBSCURED = 1;
  
  private static final int COMPLETELY_OBSCURED = 2;
  
  static boolean DEBUG_GRAPHICS_LOADED;
  
  private static final Object INPUT_VERIFIER_SOURCE_KEY = new StringBuilder("InputVerifierSourceKey");
  
  private boolean isAlignmentXSet;
  
  private float alignmentX;
  
  private boolean isAlignmentYSet;
  
  private float alignmentY;
  
  protected ComponentUI ui;
  
  protected EventListenerList listenerList = new EventListenerList();
  
  private ArrayTable clientProperties;
  
  private VetoableChangeSupport vetoableChangeSupport;
  
  private boolean autoscrolls;
  
  private Border border;
  
  private int flags;
  
  private InputVerifier inputVerifier = null;
  
  private boolean verifyInputWhenFocusTarget = true;
  
  Component paintingChild;
  
  public static final int WHEN_FOCUSED = 0;
  
  public static final int WHEN_ANCESTOR_OF_FOCUSED_COMPONENT = 1;
  
  public static final int WHEN_IN_FOCUSED_WINDOW = 2;
  
  public static final int UNDEFINED_CONDITION = -1;
  
  private static final String KEYBOARD_BINDINGS_KEY = "_KeyboardBindings";
  
  private static final String WHEN_IN_FOCUSED_WINDOW_BINDINGS = "_WhenInFocusedWindow";
  
  public static final String TOOL_TIP_TEXT_KEY = "ToolTipText";
  
  private static final String NEXT_FOCUS = "nextFocus";
  
  private JPopupMenu popupMenu;
  
  private static final int IS_DOUBLE_BUFFERED = 0;
  
  private static final int ANCESTOR_USING_BUFFER = 1;
  
  private static final int IS_PAINTING_TILE = 2;
  
  private static final int IS_OPAQUE = 3;
  
  private static final int KEY_EVENTS_ENABLED = 4;
  
  private static final int FOCUS_INPUTMAP_CREATED = 5;
  
  private static final int ANCESTOR_INPUTMAP_CREATED = 6;
  
  private static final int WIF_INPUTMAP_CREATED = 7;
  
  private static final int ACTIONMAP_CREATED = 8;
  
  private static final int CREATED_DOUBLE_BUFFER = 9;
  
  private static final int IS_PRINTING = 11;
  
  private static final int IS_PRINTING_ALL = 12;
  
  private static final int IS_REPAINTING = 13;
  
  private static final int WRITE_OBJ_COUNTER_FIRST = 14;
  
  private static final int RESERVED_1 = 15;
  
  private static final int RESERVED_2 = 16;
  
  private static final int RESERVED_3 = 17;
  
  private static final int RESERVED_4 = 18;
  
  private static final int RESERVED_5 = 19;
  
  private static final int RESERVED_6 = 20;
  
  private static final int WRITE_OBJ_COUNTER_LAST = 21;
  
  private static final int REQUEST_FOCUS_DISABLED = 22;
  
  private static final int INHERITS_POPUP_MENU = 23;
  
  private static final int OPAQUE_SET = 24;
  
  private static final int AUTOSCROLLS_SET = 25;
  
  private static final int FOCUS_TRAVERSAL_KEYS_FORWARD_SET = 26;
  
  private static final int FOCUS_TRAVERSAL_KEYS_BACKWARD_SET = 27;
  
  private AtomicBoolean revalidateRunnableScheduled = new AtomicBoolean(false);
  
  private static List<Rectangle> tempRectangles = new ArrayList(11);
  
  private InputMap focusInputMap;
  
  private InputMap ancestorInputMap;
  
  private ComponentInputMap windowInputMap;
  
  private ActionMap actionMap;
  
  private static final String defaultLocale = "JComponent.defaultLocale";
  
  private static Component componentObtainingGraphicsFrom;
  
  private static Object componentObtainingGraphicsFromLock = new StringBuilder("componentObtainingGraphicsFrom");
  
  private Object aaTextInfo;
  
  static final RequestFocusController focusController = new RequestFocusController() {
      public boolean acceptRequestFocus(Component param1Component1, Component param1Component2, boolean param1Boolean1, boolean param1Boolean2, CausedFocusEvent.Cause param1Cause) {
        if (param1Component2 == null || !(param1Component2 instanceof JComponent))
          return true; 
        if (param1Component1 == null || !(param1Component1 instanceof JComponent))
          return true; 
        JComponent jComponent1 = (JComponent)param1Component2;
        if (!jComponent1.getVerifyInputWhenFocusTarget())
          return true; 
        JComponent jComponent2 = (JComponent)param1Component1;
        InputVerifier inputVerifier = jComponent2.getInputVerifier();
        if (inputVerifier == null)
          return true; 
        object = SwingUtilities.appContextGet(INPUT_VERIFIER_SOURCE_KEY);
        if (object == jComponent2)
          return true; 
        SwingUtilities.appContextPut(INPUT_VERIFIER_SOURCE_KEY, jComponent2);
        try {
          return inputVerifier.shouldYieldFocus(jComponent2);
        } finally {
          if (object != null) {
            SwingUtilities.appContextPut(INPUT_VERIFIER_SOURCE_KEY, object);
          } else {
            SwingUtilities.appContextRemove(INPUT_VERIFIER_SOURCE_KEY);
          } 
        } 
      }
    };
  
  static Graphics safelyGetGraphics(Component paramComponent) { return safelyGetGraphics(paramComponent, SwingUtilities.getRoot(paramComponent)); }
  
  static Graphics safelyGetGraphics(Component paramComponent1, Component paramComponent2) {
    synchronized (componentObtainingGraphicsFromLock) {
      componentObtainingGraphicsFrom = paramComponent2;
      Graphics graphics = paramComponent1.getGraphics();
      componentObtainingGraphicsFrom = null;
      return graphics;
    } 
  }
  
  static void getGraphicsInvoked(Component paramComponent) {
    if (!isComponentObtainingGraphicsFrom(paramComponent)) {
      JRootPane jRootPane = ((RootPaneContainer)paramComponent).getRootPane();
      if (jRootPane != null)
        jRootPane.disableTrueDoubleBuffering(); 
    } 
  }
  
  private static boolean isComponentObtainingGraphicsFrom(Component paramComponent) {
    synchronized (componentObtainingGraphicsFromLock) {
      return (componentObtainingGraphicsFrom == paramComponent);
    } 
  }
  
  static Set<KeyStroke> getManagingFocusForwardTraversalKeys() {
    synchronized (JComponent.class) {
      if (managingFocusForwardTraversalKeys == null) {
        managingFocusForwardTraversalKeys = new HashSet(1);
        managingFocusForwardTraversalKeys.add(KeyStroke.getKeyStroke(9, 2));
      } 
    } 
    return managingFocusForwardTraversalKeys;
  }
  
  static Set<KeyStroke> getManagingFocusBackwardTraversalKeys() {
    synchronized (JComponent.class) {
      if (managingFocusBackwardTraversalKeys == null) {
        managingFocusBackwardTraversalKeys = new HashSet(1);
        managingFocusBackwardTraversalKeys.add(KeyStroke.getKeyStroke(9, 3));
      } 
    } 
    return managingFocusBackwardTraversalKeys;
  }
  
  private static Rectangle fetchRectangle() {
    synchronized (tempRectangles) {
      Rectangle rectangle;
      int i = tempRectangles.size();
      if (i > 0) {
        rectangle = (Rectangle)tempRectangles.remove(i - 1);
      } else {
        rectangle = new Rectangle(0, 0, 0, 0);
      } 
      return rectangle;
    } 
  }
  
  private static void recycleRectangle(Rectangle paramRectangle) {
    synchronized (tempRectangles) {
      tempRectangles.add(paramRectangle);
    } 
  }
  
  public void setInheritsPopupMenu(boolean paramBoolean) {
    boolean bool = getFlag(23);
    setFlag(23, paramBoolean);
    firePropertyChange("inheritsPopupMenu", bool, paramBoolean);
  }
  
  public boolean getInheritsPopupMenu() { return getFlag(23); }
  
  public void setComponentPopupMenu(JPopupMenu paramJPopupMenu) {
    if (paramJPopupMenu != null)
      enableEvents(16L); 
    JPopupMenu jPopupMenu = this.popupMenu;
    this.popupMenu = paramJPopupMenu;
    firePropertyChange("componentPopupMenu", jPopupMenu, paramJPopupMenu);
  }
  
  public JPopupMenu getComponentPopupMenu() {
    if (!getInheritsPopupMenu())
      return this.popupMenu; 
    if (this.popupMenu == null) {
      for (Container container = getParent(); container != null; container = container.getParent()) {
        if (container instanceof JComponent)
          return ((JComponent)container).getComponentPopupMenu(); 
        if (container instanceof Window || container instanceof java.applet.Applet)
          break; 
      } 
      return null;
    } 
    return this.popupMenu;
  }
  
  public JComponent() {
    enableEvents(8L);
    if (isManagingFocus()) {
      LookAndFeel.installProperty(this, "focusTraversalKeysForward", getManagingFocusForwardTraversalKeys());
      LookAndFeel.installProperty(this, "focusTraversalKeysBackward", getManagingFocusBackwardTraversalKeys());
    } 
    setLocale(getDefaultLocale());
  }
  
  public void updateUI() {}
  
  protected void setUI(ComponentUI paramComponentUI) {
    uninstallUIAndProperties();
    this.aaTextInfo = UIManager.getDefaults().get(SwingUtilities2.AA_TEXT_PROPERTY_KEY);
    ComponentUI componentUI = this.ui;
    this.ui = paramComponentUI;
    if (this.ui != null)
      this.ui.installUI(this); 
    firePropertyChange("UI", componentUI, paramComponentUI);
    revalidate();
    repaint();
  }
  
  private void uninstallUIAndProperties() {
    if (this.ui != null) {
      this.ui.uninstallUI(this);
      if (this.clientProperties != null)
        synchronized (this.clientProperties) {
          Object[] arrayOfObject = this.clientProperties.getKeys(null);
          if (arrayOfObject != null)
            for (Object object : arrayOfObject) {
              if (object instanceof sun.swing.UIClientPropertyKey)
                putClientProperty(object, null); 
            }  
        }  
    } 
  }
  
  public String getUIClassID() { return "ComponentUI"; }
  
  protected Graphics getComponentGraphics(Graphics paramGraphics) {
    Graphics graphics = paramGraphics;
    if (this.ui != null && DEBUG_GRAPHICS_LOADED && DebugGraphics.debugComponentCount() != 0 && shouldDebugGraphics() != 0 && !(paramGraphics instanceof DebugGraphics))
      graphics = new DebugGraphics(paramGraphics, this); 
    graphics.setColor(getForeground());
    graphics.setFont(getFont());
    return graphics;
  }
  
  protected void paintComponent(Graphics paramGraphics) {
    if (this.ui != null) {
      graphics = (paramGraphics == null) ? null : paramGraphics.create();
      try {
        this.ui.update(graphics, this);
      } finally {
        graphics.dispose();
      } 
    } 
  }
  
  protected void paintChildren(Graphics paramGraphics) {
    Graphics graphics = paramGraphics;
    synchronized (getTreeLock()) {
      int i = getComponentCount() - 1;
      if (i < 0)
        return; 
      if (this.paintingChild != null && this.paintingChild instanceof JComponent && this.paintingChild.isOpaque())
        while (i >= 0 && getComponent(i) != this.paintingChild)
          i--;  
      Rectangle rectangle1 = fetchRectangle();
      boolean bool1 = (!isOptimizedDrawingEnabled() && checkIfChildObscuredBySibling()) ? 1 : 0;
      Rectangle rectangle2 = null;
      if (bool1) {
        rectangle2 = graphics.getClipBounds();
        if (rectangle2 == null)
          rectangle2 = new Rectangle(0, 0, getWidth(), getHeight()); 
      } 
      boolean bool = getFlag(11);
      Window window = SwingUtilities.getWindowAncestor(this);
      boolean bool2 = (window == null || window.isOpaque()) ? 1 : 0;
      while (i >= 0) {
        component = getComponent(i);
        if (component == null)
          continue; 
        boolean bool3 = component instanceof JComponent;
        if ((!bool2 || bool3 || isLightweightComponent(component)) && component.isVisible()) {
          Rectangle rectangle = component.getBounds(rectangle1);
          boolean bool4 = paramGraphics.hitClip(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
          if (bool4) {
            if (bool1 && i > 0) {
              int j = rectangle.x;
              int k = rectangle.y;
              int m = rectangle.width;
              int n = rectangle.height;
              SwingUtilities.computeIntersection(rectangle2.x, rectangle2.y, rectangle2.width, rectangle2.height, rectangle);
              if (getObscuredState(i, rectangle.x, rectangle.y, rectangle.width, rectangle.height) == 2)
                continue; 
              rectangle.x = j;
              rectangle.y = k;
              rectangle.width = m;
              rectangle.height = n;
            } 
            graphics1 = graphics.create(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
            graphics1.setColor(component.getForeground());
            graphics1.setFont(component.getFont());
            bool5 = false;
            try {
              if (bool3) {
                if (getFlag(1)) {
                  ((JComponent)component).setFlag(1, true);
                  bool5 = true;
                } 
                if (getFlag(2)) {
                  ((JComponent)component).setFlag(2, true);
                  bool5 = true;
                } 
                if (!bool) {
                  component.paint(graphics1);
                } else if (!getFlag(12)) {
                  component.print(graphics1);
                } else {
                  component.printAll(graphics1);
                } 
              } else if (!bool) {
                component.paint(graphics1);
              } else if (!getFlag(12)) {
                component.print(graphics1);
              } else {
                component.printAll(graphics1);
              } 
            } finally {
              graphics1.dispose();
              if (bool5) {
                ((JComponent)component).setFlag(1, false);
                ((JComponent)component).setFlag(2, false);
              } 
            } 
          } 
        } 
        continue;
        i--;
      } 
      recycleRectangle(rectangle1);
    } 
  }
  
  protected void paintBorder(Graphics paramGraphics) {
    Border border1 = getBorder();
    if (border1 != null)
      border1.paintBorder(this, paramGraphics, 0, 0, getWidth(), getHeight()); 
  }
  
  public void update(Graphics paramGraphics) { paint(paramGraphics); }
  
  public void paint(Graphics paramGraphics) {
    bool = false;
    if (getWidth() <= 0 || getHeight() <= 0)
      return; 
    Graphics graphics1 = getComponentGraphics(paramGraphics);
    graphics2 = graphics1.create();
    try {
      int m;
      int k;
      int j;
      int i;
      repaintManager = RepaintManager.currentManager(this);
      Rectangle rectangle = graphics2.getClipBounds();
      if (rectangle == null) {
        i = j = 0;
        k = getWidth();
        m = getHeight();
      } else {
        i = rectangle.x;
        j = rectangle.y;
        k = rectangle.width;
        m = rectangle.height;
      } 
      if (k > getWidth())
        k = getWidth(); 
      if (m > getHeight())
        m = getHeight(); 
      if (getParent() != null && !(getParent() instanceof JComponent)) {
        adjustPaintFlags();
        bool = true;
      } 
      boolean bool1 = getFlag(11);
      if (!bool1 && repaintManager.isDoubleBufferingEnabled() && !getFlag(1) && isDoubleBuffered() && (getFlag(13) || repaintManager.isPainting())) {
        repaintManager.beginPaint();
        try {
          repaintManager.paint(this, this, graphics2, i, j, k, m);
        } finally {
          repaintManager.endPaint();
        } 
      } else {
        if (rectangle == null)
          graphics2.setClip(i, j, k, m); 
        if (!rectangleIsObscured(i, j, k, m))
          if (!bool1) {
            paintComponent(graphics2);
            paintBorder(graphics2);
          } else {
            printComponent(graphics2);
            printBorder(graphics2);
          }  
        if (!bool1) {
          paintChildren(graphics2);
        } else {
          printChildren(graphics2);
        } 
      } 
    } finally {
      graphics2.dispose();
      if (bool) {
        setFlag(1, false);
        setFlag(2, false);
        setFlag(11, false);
        setFlag(12, false);
      } 
    } 
  }
  
  void paintForceDoubleBuffered(Graphics paramGraphics) {
    repaintManager = RepaintManager.currentManager(this);
    Rectangle rectangle = paramGraphics.getClipBounds();
    repaintManager.beginPaint();
    setFlag(13, true);
    try {
      repaintManager.paint(this, this, paramGraphics, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    } finally {
      repaintManager.endPaint();
      setFlag(13, false);
    } 
  }
  
  boolean isPainting() {
    JComponent jComponent = this;
    while (jComponent != null) {
      if (jComponent instanceof JComponent && ((JComponent)jComponent).getFlag(1))
        return true; 
      Container container = jComponent.getParent();
    } 
    return false;
  }
  
  private void adjustPaintFlags() {
    for (Container container = getParent(); container != null; container = container.getParent()) {
      if (container instanceof JComponent) {
        JComponent jComponent = (JComponent)container;
        if (jComponent.getFlag(1))
          setFlag(1, true); 
        if (jComponent.getFlag(2))
          setFlag(2, true); 
        if (jComponent.getFlag(11))
          setFlag(11, true); 
        if (jComponent.getFlag(12))
          setFlag(12, true); 
        break;
      } 
    } 
  }
  
  public void printAll(Graphics paramGraphics) {
    setFlag(12, true);
    try {
      print(paramGraphics);
    } finally {
      setFlag(12, false);
    } 
  }
  
  public void print(Graphics paramGraphics) {
    setFlag(11, true);
    firePropertyChange("paintingForPrint", false, true);
    try {
      paint(paramGraphics);
    } finally {
      setFlag(11, false);
      firePropertyChange("paintingForPrint", true, false);
    } 
  }
  
  protected void printComponent(Graphics paramGraphics) { paintComponent(paramGraphics); }
  
  protected void printChildren(Graphics paramGraphics) { paintChildren(paramGraphics); }
  
  protected void printBorder(Graphics paramGraphics) { paintBorder(paramGraphics); }
  
  public boolean isPaintingTile() { return getFlag(2); }
  
  public final boolean isPaintingForPrint() { return getFlag(11); }
  
  @Deprecated
  public boolean isManagingFocus() { return false; }
  
  private void registerNextFocusableComponent() { registerNextFocusableComponent(getNextFocusableComponent()); }
  
  private void registerNextFocusableComponent(Component paramComponent) {
    if (paramComponent == null)
      return; 
    JComponent jComponent = isFocusCycleRoot() ? this : getFocusCycleRootAncestor();
    FocusTraversalPolicy focusTraversalPolicy = jComponent.getFocusTraversalPolicy();
    if (!(focusTraversalPolicy instanceof LegacyGlueFocusTraversalPolicy)) {
      focusTraversalPolicy = new LegacyGlueFocusTraversalPolicy(focusTraversalPolicy);
      jComponent.setFocusTraversalPolicy(focusTraversalPolicy);
    } 
    ((LegacyGlueFocusTraversalPolicy)focusTraversalPolicy).setNextFocusableComponent(this, paramComponent);
  }
  
  private void deregisterNextFocusableComponent() {
    Component component = getNextFocusableComponent();
    if (component == null)
      return; 
    JComponent jComponent = isFocusCycleRoot() ? this : getFocusCycleRootAncestor();
    if (jComponent == null)
      return; 
    FocusTraversalPolicy focusTraversalPolicy = jComponent.getFocusTraversalPolicy();
    if (focusTraversalPolicy instanceof LegacyGlueFocusTraversalPolicy)
      ((LegacyGlueFocusTraversalPolicy)focusTraversalPolicy).unsetNextFocusableComponent(this, component); 
  }
  
  @Deprecated
  public void setNextFocusableComponent(Component paramComponent) {
    boolean bool = isDisplayable();
    if (bool)
      deregisterNextFocusableComponent(); 
    putClientProperty("nextFocus", paramComponent);
    if (bool)
      registerNextFocusableComponent(paramComponent); 
  }
  
  @Deprecated
  public Component getNextFocusableComponent() { return (Component)getClientProperty("nextFocus"); }
  
  public void setRequestFocusEnabled(boolean paramBoolean) { setFlag(22, !paramBoolean); }
  
  public boolean isRequestFocusEnabled() { return !getFlag(22); }
  
  public void requestFocus() { super.requestFocus(); }
  
  public boolean requestFocus(boolean paramBoolean) { return super.requestFocus(paramBoolean); }
  
  public boolean requestFocusInWindow() { return super.requestFocusInWindow(); }
  
  protected boolean requestFocusInWindow(boolean paramBoolean) { return super.requestFocusInWindow(paramBoolean); }
  
  public void grabFocus() { requestFocus(); }
  
  public void setVerifyInputWhenFocusTarget(boolean paramBoolean) {
    boolean bool = this.verifyInputWhenFocusTarget;
    this.verifyInputWhenFocusTarget = paramBoolean;
    firePropertyChange("verifyInputWhenFocusTarget", bool, paramBoolean);
  }
  
  public boolean getVerifyInputWhenFocusTarget() { return this.verifyInputWhenFocusTarget; }
  
  public FontMetrics getFontMetrics(Font paramFont) { return SwingUtilities2.getFontMetrics(this, paramFont); }
  
  public void setPreferredSize(Dimension paramDimension) { super.setPreferredSize(paramDimension); }
  
  @Transient
  public Dimension getPreferredSize() {
    if (isPreferredSizeSet())
      return super.getPreferredSize(); 
    Dimension dimension = null;
    if (this.ui != null)
      dimension = this.ui.getPreferredSize(this); 
    return (dimension != null) ? dimension : super.getPreferredSize();
  }
  
  public void setMaximumSize(Dimension paramDimension) { super.setMaximumSize(paramDimension); }
  
  @Transient
  public Dimension getMaximumSize() {
    if (isMaximumSizeSet())
      return super.getMaximumSize(); 
    Dimension dimension = null;
    if (this.ui != null)
      dimension = this.ui.getMaximumSize(this); 
    return (dimension != null) ? dimension : super.getMaximumSize();
  }
  
  public void setMinimumSize(Dimension paramDimension) { super.setMinimumSize(paramDimension); }
  
  @Transient
  public Dimension getMinimumSize() {
    if (isMinimumSizeSet())
      return super.getMinimumSize(); 
    Dimension dimension = null;
    if (this.ui != null)
      dimension = this.ui.getMinimumSize(this); 
    return (dimension != null) ? dimension : super.getMinimumSize();
  }
  
  public boolean contains(int paramInt1, int paramInt2) { return (this.ui != null) ? this.ui.contains(this, paramInt1, paramInt2) : super.contains(paramInt1, paramInt2); }
  
  public void setBorder(Border paramBorder) {
    Border border1 = this.border;
    this.border = paramBorder;
    firePropertyChange("border", border1, paramBorder);
    if (paramBorder != border1) {
      if (paramBorder == null || border1 == null || !paramBorder.getBorderInsets(this).equals(border1.getBorderInsets(this)))
        revalidate(); 
      repaint();
    } 
  }
  
  public Border getBorder() { return this.border; }
  
  public Insets getInsets() { return (this.border != null) ? this.border.getBorderInsets(this) : super.getInsets(); }
  
  public Insets getInsets(Insets paramInsets) {
    if (paramInsets == null)
      paramInsets = new Insets(0, 0, 0, 0); 
    if (this.border != null)
      return (this.border instanceof AbstractBorder) ? ((AbstractBorder)this.border).getBorderInsets(this, paramInsets) : this.border.getBorderInsets(this); 
    paramInsets.left = paramInsets.top = paramInsets.right = paramInsets.bottom = 0;
    return paramInsets;
  }
  
  public float getAlignmentY() { return this.isAlignmentYSet ? this.alignmentY : super.getAlignmentY(); }
  
  public void setAlignmentY(float paramFloat) {
    this.alignmentY = (paramFloat > 1.0F) ? 1.0F : ((paramFloat < 0.0F) ? 0.0F : paramFloat);
    this.isAlignmentYSet = true;
  }
  
  public float getAlignmentX() { return this.isAlignmentXSet ? this.alignmentX : super.getAlignmentX(); }
  
  public void setAlignmentX(float paramFloat) {
    this.alignmentX = (paramFloat > 1.0F) ? 1.0F : ((paramFloat < 0.0F) ? 0.0F : paramFloat);
    this.isAlignmentXSet = true;
  }
  
  public void setInputVerifier(InputVerifier paramInputVerifier) {
    InputVerifier inputVerifier1 = (InputVerifier)getClientProperty(ClientPropertyKey.JComponent_INPUT_VERIFIER);
    putClientProperty(ClientPropertyKey.JComponent_INPUT_VERIFIER, paramInputVerifier);
    firePropertyChange("inputVerifier", inputVerifier1, paramInputVerifier);
  }
  
  public InputVerifier getInputVerifier() { return (InputVerifier)getClientProperty(ClientPropertyKey.JComponent_INPUT_VERIFIER); }
  
  public Graphics getGraphics() { return (DEBUG_GRAPHICS_LOADED && shouldDebugGraphics() != 0) ? new DebugGraphics(super.getGraphics(), this) : super.getGraphics(); }
  
  public void setDebugGraphicsOptions(int paramInt) { DebugGraphics.setDebugOptions(this, paramInt); }
  
  public int getDebugGraphicsOptions() { return DebugGraphics.getDebugOptions(this); }
  
  int shouldDebugGraphics() { return DebugGraphics.shouldComponentDebug(this); }
  
  public void registerKeyboardAction(ActionListener paramActionListener, String paramString, KeyStroke paramKeyStroke, int paramInt) {
    InputMap inputMap = getInputMap(paramInt, true);
    if (inputMap != null) {
      ActionMap actionMap1 = getActionMap(true);
      ActionStandin actionStandin = new ActionStandin(paramActionListener, paramString);
      inputMap.put(paramKeyStroke, actionStandin);
      if (actionMap1 != null)
        actionMap1.put(actionStandin, actionStandin); 
    } 
  }
  
  private void registerWithKeyboardManager(boolean paramBoolean) {
    Object object;
    InputMap inputMap = getInputMap(2, false);
    Hashtable hashtable = (Hashtable)getClientProperty("_WhenInFocusedWindow");
    if (inputMap != null) {
      object = inputMap.allKeys();
      if (object != null)
        for (int i = object.length - 1; i >= 0; i--) {
          if (!paramBoolean || hashtable == null || hashtable.get(object[i]) == null)
            registerWithKeyboardManager(object[i]); 
          if (hashtable != null)
            hashtable.remove(object[i]); 
        }  
    } else {
      object = null;
    } 
    if (hashtable != null && hashtable.size() > 0) {
      Enumeration enumeration = hashtable.keys();
      while (enumeration.hasMoreElements()) {
        KeyStroke keyStroke = (KeyStroke)enumeration.nextElement();
        unregisterWithKeyboardManager(keyStroke);
      } 
      hashtable.clear();
    } 
    if (object != null && object.length > 0) {
      if (hashtable == null) {
        hashtable = new Hashtable(object.length);
        putClientProperty("_WhenInFocusedWindow", hashtable);
      } 
      for (int i = object.length - 1; i >= 0; i--)
        hashtable.put(object[i], object[i]); 
    } else {
      putClientProperty("_WhenInFocusedWindow", null);
    } 
  }
  
  private void unregisterWithKeyboardManager() {
    Hashtable hashtable = (Hashtable)getClientProperty("_WhenInFocusedWindow");
    if (hashtable != null && hashtable.size() > 0) {
      Enumeration enumeration = hashtable.keys();
      while (enumeration.hasMoreElements()) {
        KeyStroke keyStroke = (KeyStroke)enumeration.nextElement();
        unregisterWithKeyboardManager(keyStroke);
      } 
    } 
    putClientProperty("_WhenInFocusedWindow", null);
  }
  
  void componentInputMapChanged(ComponentInputMap paramComponentInputMap) {
    InputMap inputMap;
    for (inputMap = getInputMap(2, false); inputMap != paramComponentInputMap && inputMap != null; inputMap = inputMap.getParent());
    if (inputMap != null)
      registerWithKeyboardManager(false); 
  }
  
  private void registerWithKeyboardManager(KeyStroke paramKeyStroke) { KeyboardManager.getCurrentManager().registerKeyStroke(paramKeyStroke, this); }
  
  private void unregisterWithKeyboardManager(KeyStroke paramKeyStroke) { KeyboardManager.getCurrentManager().unregisterKeyStroke(paramKeyStroke, this); }
  
  public void registerKeyboardAction(ActionListener paramActionListener, KeyStroke paramKeyStroke, int paramInt) { registerKeyboardAction(paramActionListener, null, paramKeyStroke, paramInt); }
  
  public void unregisterKeyboardAction(KeyStroke paramKeyStroke) {
    ActionMap actionMap1 = getActionMap(false);
    for (byte b = 0; b < 3; b++) {
      InputMap inputMap = getInputMap(b, false);
      if (inputMap != null) {
        Object object = inputMap.get(paramKeyStroke);
        if (actionMap1 != null && object != null)
          actionMap1.remove(object); 
        inputMap.remove(paramKeyStroke);
      } 
    } 
  }
  
  public KeyStroke[] getRegisteredKeyStrokes() {
    int[] arrayOfInt = new int[3];
    KeyStroke[][] arrayOfKeyStroke = new KeyStroke[3][];
    for (byte b1 = 0; b1 < 3; b1++) {
      InputMap inputMap = getInputMap(b1, false);
      arrayOfKeyStroke[b1] = (inputMap != null) ? inputMap.allKeys() : null;
      arrayOfInt[b1] = (arrayOfKeyStroke[b1] != null) ? arrayOfKeyStroke[b1].length : 0;
    } 
    KeyStroke[] arrayOfKeyStroke1 = new KeyStroke[arrayOfInt[0] + arrayOfInt[1] + arrayOfInt[2]];
    byte b2 = 0;
    int i = 0;
    while (b2 < 3) {
      if (arrayOfInt[b2] > 0) {
        System.arraycopy(arrayOfKeyStroke[b2], 0, arrayOfKeyStroke1, i, arrayOfInt[b2]);
        i += arrayOfInt[b2];
      } 
      b2++;
    } 
    return arrayOfKeyStroke1;
  }
  
  public int getConditionForKeyStroke(KeyStroke paramKeyStroke) {
    for (byte b = 0; b < 3; b++) {
      InputMap inputMap = getInputMap(b, false);
      if (inputMap != null && inputMap.get(paramKeyStroke) != null)
        return b; 
    } 
    return -1;
  }
  
  public ActionListener getActionForKeyStroke(KeyStroke paramKeyStroke) {
    ActionMap actionMap1 = getActionMap(false);
    if (actionMap1 == null)
      return null; 
    for (byte b = 0; b < 3; b++) {
      InputMap inputMap = getInputMap(b, false);
      if (inputMap != null) {
        Object object = inputMap.get(paramKeyStroke);
        if (object != null) {
          Action action = actionMap1.get(object);
          return (action instanceof ActionStandin) ? ((ActionStandin)action).actionListener : action;
        } 
      } 
    } 
    return null;
  }
  
  public void resetKeyboardActions() {
    for (byte b = 0; b < 3; b++) {
      InputMap inputMap = getInputMap(b, false);
      if (inputMap != null)
        inputMap.clear(); 
    } 
    ActionMap actionMap1 = getActionMap(false);
    if (actionMap1 != null)
      actionMap1.clear(); 
  }
  
  public final void setInputMap(int paramInt, InputMap paramInputMap) {
    switch (paramInt) {
      case 2:
        if (paramInputMap != null && !(paramInputMap instanceof ComponentInputMap))
          throw new IllegalArgumentException("WHEN_IN_FOCUSED_WINDOW InputMaps must be of type ComponentInputMap"); 
        this.windowInputMap = (ComponentInputMap)paramInputMap;
        setFlag(7, true);
        registerWithKeyboardManager(false);
        return;
      case 1:
        this.ancestorInputMap = paramInputMap;
        setFlag(6, true);
        return;
      case 0:
        this.focusInputMap = paramInputMap;
        setFlag(5, true);
        return;
    } 
    throw new IllegalArgumentException("condition must be one of JComponent.WHEN_IN_FOCUSED_WINDOW, JComponent.WHEN_FOCUSED or JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT");
  }
  
  public final InputMap getInputMap(int paramInt) { return getInputMap(paramInt, true); }
  
  public final InputMap getInputMap() { return getInputMap(0, true); }
  
  public final void setActionMap(ActionMap paramActionMap) {
    this.actionMap = paramActionMap;
    setFlag(8, true);
  }
  
  public final ActionMap getActionMap() { return getActionMap(true); }
  
  final InputMap getInputMap(int paramInt, boolean paramBoolean) {
    switch (paramInt) {
      case 0:
        if (getFlag(5))
          return this.focusInputMap; 
        if (paramBoolean) {
          InputMap inputMap = new InputMap();
          setInputMap(paramInt, inputMap);
          return inputMap;
        } 
        return null;
      case 1:
        if (getFlag(6))
          return this.ancestorInputMap; 
        if (paramBoolean) {
          InputMap inputMap = new InputMap();
          setInputMap(paramInt, inputMap);
          return inputMap;
        } 
        return null;
      case 2:
        if (getFlag(7))
          return this.windowInputMap; 
        if (paramBoolean) {
          ComponentInputMap componentInputMap = new ComponentInputMap(this);
          setInputMap(paramInt, componentInputMap);
          return componentInputMap;
        } 
        return null;
    } 
    throw new IllegalArgumentException("condition must be one of JComponent.WHEN_IN_FOCUSED_WINDOW, JComponent.WHEN_FOCUSED or JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT");
  }
  
  final ActionMap getActionMap(boolean paramBoolean) {
    if (getFlag(8))
      return this.actionMap; 
    if (paramBoolean) {
      ActionMap actionMap1 = new ActionMap();
      setActionMap(actionMap1);
      return actionMap1;
    } 
    return null;
  }
  
  public int getBaseline(int paramInt1, int paramInt2) {
    super.getBaseline(paramInt1, paramInt2);
    return (this.ui != null) ? this.ui.getBaseline(this, paramInt1, paramInt2) : -1;
  }
  
  public Component.BaselineResizeBehavior getBaselineResizeBehavior() { return (this.ui != null) ? this.ui.getBaselineResizeBehavior(this) : Component.BaselineResizeBehavior.OTHER; }
  
  @Deprecated
  public boolean requestDefaultFocus() {
    JComponent jComponent = isFocusCycleRoot() ? this : getFocusCycleRootAncestor();
    if (jComponent == null)
      return false; 
    Component component = jComponent.getFocusTraversalPolicy().getDefaultComponent(jComponent);
    if (component != null) {
      component.requestFocus();
      return true;
    } 
    return false;
  }
  
  public void setVisible(boolean paramBoolean) {
    if (paramBoolean != isVisible()) {
      super.setVisible(paramBoolean);
      if (paramBoolean) {
        Container container = getParent();
        if (container != null) {
          Rectangle rectangle = getBounds();
          container.repaint(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        } 
        revalidate();
      } 
    } 
  }
  
  public void setEnabled(boolean paramBoolean) {
    boolean bool = isEnabled();
    super.setEnabled(paramBoolean);
    firePropertyChange("enabled", bool, paramBoolean);
    if (paramBoolean != bool)
      repaint(); 
  }
  
  public void setForeground(Color paramColor) {
    Color color = getForeground();
    super.setForeground(paramColor);
    if ((color != null) ? !color.equals(paramColor) : (paramColor != null && !paramColor.equals(color)))
      repaint(); 
  }
  
  public void setBackground(Color paramColor) {
    Color color = getBackground();
    super.setBackground(paramColor);
    if ((color != null) ? !color.equals(paramColor) : (paramColor != null && !paramColor.equals(color)))
      repaint(); 
  }
  
  public void setFont(Font paramFont) {
    Font font = getFont();
    super.setFont(paramFont);
    if (paramFont != font) {
      revalidate();
      repaint();
    } 
  }
  
  public static Locale getDefaultLocale() {
    Locale locale = (Locale)SwingUtilities.appContextGet("JComponent.defaultLocale");
    if (locale == null) {
      locale = Locale.getDefault();
      setDefaultLocale(locale);
    } 
    return locale;
  }
  
  public static void setDefaultLocale(Locale paramLocale) { SwingUtilities.appContextPut("JComponent.defaultLocale", paramLocale); }
  
  protected void processComponentKeyEvent(KeyEvent paramKeyEvent) {}
  
  protected void processKeyEvent(KeyEvent paramKeyEvent) {
    super.processKeyEvent(paramKeyEvent);
    if (!paramKeyEvent.isConsumed())
      processComponentKeyEvent(paramKeyEvent); 
    boolean bool = KeyboardState.shouldProcess(paramKeyEvent);
    if (paramKeyEvent.isConsumed())
      return; 
    if (bool && processKeyBindings(paramKeyEvent, (paramKeyEvent.getID() == 401)))
      paramKeyEvent.consume(); 
  }
  
  protected boolean processKeyBinding(KeyStroke paramKeyStroke, KeyEvent paramKeyEvent, int paramInt, boolean paramBoolean) {
    InputMap inputMap = getInputMap(paramInt, false);
    ActionMap actionMap1 = getActionMap(false);
    if (inputMap != null && actionMap1 != null && isEnabled()) {
      Object object = inputMap.get(paramKeyStroke);
      Action action = (object == null) ? null : actionMap1.get(object);
      if (action != null)
        return SwingUtilities.notifyAction(action, paramKeyStroke, paramKeyEvent, this, paramKeyEvent.getModifiers()); 
    } 
    return false;
  }
  
  boolean processKeyBindings(KeyEvent paramKeyEvent, boolean paramBoolean) {
    KeyStroke keyStroke1;
    if (!SwingUtilities.isValidKeyEventForKeyBindings(paramKeyEvent))
      return false; 
    KeyStroke keyStroke2 = null;
    if (paramKeyEvent.getID() == 400) {
      keyStroke1 = KeyStroke.getKeyStroke(paramKeyEvent.getKeyChar());
    } else {
      keyStroke1 = KeyStroke.getKeyStroke(paramKeyEvent.getKeyCode(), paramKeyEvent.getModifiers(), !paramBoolean);
      if (paramKeyEvent.getKeyCode() != paramKeyEvent.getExtendedKeyCode())
        keyStroke2 = KeyStroke.getKeyStroke(paramKeyEvent.getExtendedKeyCode(), paramKeyEvent.getModifiers(), !paramBoolean); 
    } 
    if (keyStroke2 != null && processKeyBinding(keyStroke2, paramKeyEvent, 0, paramBoolean))
      return true; 
    if (processKeyBinding(keyStroke1, paramKeyEvent, 0, paramBoolean))
      return true; 
    Container container = this;
    while (container != null && !(container instanceof Window) && !(container instanceof java.applet.Applet)) {
      if (container instanceof JComponent) {
        if (keyStroke2 != null && ((JComponent)container).processKeyBinding(keyStroke2, paramKeyEvent, 1, paramBoolean))
          return true; 
        if (((JComponent)container).processKeyBinding(keyStroke1, paramKeyEvent, 1, paramBoolean))
          return true; 
      } 
      if (container instanceof JInternalFrame && processKeyBindingsForAllComponents(paramKeyEvent, container, paramBoolean))
        return true; 
      container = container.getParent();
    } 
    return (container != null) ? processKeyBindingsForAllComponents(paramKeyEvent, container, paramBoolean) : 0;
  }
  
  static boolean processKeyBindingsForAllComponents(KeyEvent paramKeyEvent, Container paramContainer, boolean paramBoolean) {
    while (true) {
      if (KeyboardManager.getCurrentManager().fireKeyboardAction(paramKeyEvent, paramBoolean, paramContainer))
        return true; 
      if (paramContainer instanceof Popup.HeavyWeightWindow) {
        paramContainer = ((Window)paramContainer).getOwner();
        continue;
      } 
      break;
    } 
    return false;
  }
  
  public void setToolTipText(String paramString) {
    String str = getToolTipText();
    putClientProperty("ToolTipText", paramString);
    ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
    if (paramString != null) {
      if (str == null)
        toolTipManager.registerComponent(this); 
    } else {
      toolTipManager.unregisterComponent(this);
    } 
  }
  
  public String getToolTipText() { return (String)getClientProperty("ToolTipText"); }
  
  public String getToolTipText(MouseEvent paramMouseEvent) { return getToolTipText(); }
  
  public Point getToolTipLocation(MouseEvent paramMouseEvent) { return null; }
  
  public Point getPopupLocation(MouseEvent paramMouseEvent) { return null; }
  
  public JToolTip createToolTip() {
    JToolTip jToolTip = new JToolTip();
    jToolTip.setComponent(this);
    return jToolTip;
  }
  
  public void scrollRectToVisible(Rectangle paramRectangle) {
    int i = getX();
    int j = getY();
    Container container;
    for (container = getParent(); container != null && !(container instanceof JComponent) && !(container instanceof CellRendererPane); container = container.getParent()) {
      Rectangle rectangle = container.getBounds();
      i += rectangle.x;
      j += rectangle.y;
    } 
    if (container != null && !(container instanceof CellRendererPane)) {
      paramRectangle.x += i;
      paramRectangle.y += j;
      ((JComponent)container).scrollRectToVisible(paramRectangle);
      paramRectangle.x -= i;
      paramRectangle.y -= j;
    } 
  }
  
  public void setAutoscrolls(boolean paramBoolean) {
    setFlag(25, true);
    if (this.autoscrolls != paramBoolean) {
      this.autoscrolls = paramBoolean;
      if (paramBoolean) {
        enableEvents(16L);
        enableEvents(32L);
      } else {
        Autoscroller.stop(this);
      } 
    } 
  }
  
  public boolean getAutoscrolls() { return this.autoscrolls; }
  
  public void setTransferHandler(TransferHandler paramTransferHandler) {
    TransferHandler transferHandler = (TransferHandler)getClientProperty(ClientPropertyKey.JComponent_TRANSFER_HANDLER);
    putClientProperty(ClientPropertyKey.JComponent_TRANSFER_HANDLER, paramTransferHandler);
    SwingUtilities.installSwingDropTargetAsNecessary(this, paramTransferHandler);
    firePropertyChange("transferHandler", transferHandler, paramTransferHandler);
  }
  
  public TransferHandler getTransferHandler() { return (TransferHandler)getClientProperty(ClientPropertyKey.JComponent_TRANSFER_HANDLER); }
  
  TransferHandler.DropLocation dropLocationForPoint(Point paramPoint) { return null; }
  
  Object setDropLocation(TransferHandler.DropLocation paramDropLocation, Object paramObject, boolean paramBoolean) { return null; }
  
  void dndDone() {}
  
  protected void processMouseEvent(MouseEvent paramMouseEvent) {
    if (this.autoscrolls && paramMouseEvent.getID() == 502)
      Autoscroller.stop(this); 
    super.processMouseEvent(paramMouseEvent);
  }
  
  protected void processMouseMotionEvent(MouseEvent paramMouseEvent) {
    boolean bool = true;
    if (this.autoscrolls && paramMouseEvent.getID() == 506) {
      bool = !Autoscroller.isRunning(this) ? 1 : 0;
      Autoscroller.processMouseDragged(paramMouseEvent);
    } 
    if (bool)
      super.processMouseMotionEvent(paramMouseEvent); 
  }
  
  void superProcessMouseMotionEvent(MouseEvent paramMouseEvent) { super.processMouseMotionEvent(paramMouseEvent); }
  
  void setCreatedDoubleBuffer(boolean paramBoolean) { setFlag(9, paramBoolean); }
  
  boolean getCreatedDoubleBuffer() { return getFlag(9); }
  
  @Deprecated
  public void enable() {
    if (isEnabled() != true) {
      super.enable();
      if (this.accessibleContext != null)
        this.accessibleContext.firePropertyChange("AccessibleState", null, AccessibleState.ENABLED); 
    } 
  }
  
  @Deprecated
  public void disable() {
    if (isEnabled()) {
      super.disable();
      if (this.accessibleContext != null)
        this.accessibleContext.firePropertyChange("AccessibleState", AccessibleState.ENABLED, null); 
    } 
  }
  
  private ArrayTable getClientProperties() {
    if (this.clientProperties == null)
      this.clientProperties = new ArrayTable(); 
    return this.clientProperties;
  }
  
  public final Object getClientProperty(Object paramObject) {
    if (paramObject == SwingUtilities2.AA_TEXT_PROPERTY_KEY)
      return this.aaTextInfo; 
    if (paramObject == SwingUtilities2.COMPONENT_UI_PROPERTY_KEY)
      return this.ui; 
    if (this.clientProperties == null)
      return null; 
    synchronized (this.clientProperties) {
      return this.clientProperties.get(paramObject);
    } 
  }
  
  public final void putClientProperty(Object paramObject1, Object paramObject2) {
    Object object;
    if (paramObject1 == SwingUtilities2.AA_TEXT_PROPERTY_KEY) {
      this.aaTextInfo = paramObject2;
      return;
    } 
    if (paramObject2 == null && this.clientProperties == null)
      return; 
    ArrayTable arrayTable = getClientProperties();
    synchronized (arrayTable) {
      object = arrayTable.get(paramObject1);
      if (paramObject2 != null) {
        arrayTable.put(paramObject1, paramObject2);
      } else if (object != null) {
        arrayTable.remove(paramObject1);
      } else {
        return;
      } 
    } 
    clientPropertyChanged(paramObject1, object, paramObject2);
    firePropertyChange(paramObject1.toString(), object, paramObject2);
  }
  
  void clientPropertyChanged(Object paramObject1, Object paramObject2, Object paramObject3) {}
  
  void setUIProperty(String paramString, Object paramObject) {
    if (paramString == "opaque") {
      if (!getFlag(24)) {
        setOpaque(((Boolean)paramObject).booleanValue());
        setFlag(24, false);
      } 
    } else if (paramString == "autoscrolls") {
      if (!getFlag(25)) {
        setAutoscrolls(((Boolean)paramObject).booleanValue());
        setFlag(25, false);
      } 
    } else if (paramString == "focusTraversalKeysForward") {
      if (!getFlag(26))
        super.setFocusTraversalKeys(0, (Set)paramObject); 
    } else if (paramString == "focusTraversalKeysBackward") {
      if (!getFlag(27))
        super.setFocusTraversalKeys(1, (Set)paramObject); 
    } else {
      throw new IllegalArgumentException("property \"" + paramString + "\" cannot be set using this method");
    } 
  }
  
  public void setFocusTraversalKeys(int paramInt, Set<? extends AWTKeyStroke> paramSet) {
    if (paramInt == 0) {
      setFlag(26, true);
    } else if (paramInt == 1) {
      setFlag(27, true);
    } 
    super.setFocusTraversalKeys(paramInt, paramSet);
  }
  
  public static boolean isLightweightComponent(Component paramComponent) { return paramComponent.getPeer() instanceof java.awt.peer.LightweightPeer; }
  
  @Deprecated
  public void reshape(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { super.reshape(paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public Rectangle getBounds(Rectangle paramRectangle) {
    if (paramRectangle == null)
      return new Rectangle(getX(), getY(), getWidth(), getHeight()); 
    paramRectangle.setBounds(getX(), getY(), getWidth(), getHeight());
    return paramRectangle;
  }
  
  public Dimension getSize(Dimension paramDimension) {
    if (paramDimension == null)
      return new Dimension(getWidth(), getHeight()); 
    paramDimension.setSize(getWidth(), getHeight());
    return paramDimension;
  }
  
  public Point getLocation(Point paramPoint) {
    if (paramPoint == null)
      return new Point(getX(), getY()); 
    paramPoint.setLocation(getX(), getY());
    return paramPoint;
  }
  
  public int getX() { return super.getX(); }
  
  public int getY() { return super.getY(); }
  
  public int getWidth() { return super.getWidth(); }
  
  public int getHeight() { return super.getHeight(); }
  
  public boolean isOpaque() { return getFlag(3); }
  
  public void setOpaque(boolean paramBoolean) {
    boolean bool = getFlag(3);
    setFlag(3, paramBoolean);
    setFlag(24, true);
    firePropertyChange("opaque", bool, paramBoolean);
  }
  
  boolean rectangleIsObscured(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int i = getComponentCount();
    for (byte b = 0; b < i; b++) {
      Component component = getComponent(b);
      int j = component.getX();
      int k = component.getY();
      int m = component.getWidth();
      int n = component.getHeight();
      if (paramInt1 >= j && paramInt1 + paramInt3 <= j + m && paramInt2 >= k && paramInt2 + paramInt4 <= k + n && component.isVisible())
        return (component instanceof JComponent) ? component.isOpaque() : 0; 
    } 
    return false;
  }
  
  static final void computeVisibleRect(Component paramComponent, Rectangle paramRectangle) {
    Container container = paramComponent.getParent();
    Rectangle rectangle = paramComponent.getBounds();
    if (container == null || container instanceof Window || container instanceof java.applet.Applet) {
      paramRectangle.setBounds(0, 0, rectangle.width, rectangle.height);
    } else {
      computeVisibleRect(container, paramRectangle);
      paramRectangle.x -= rectangle.x;
      paramRectangle.y -= rectangle.y;
      SwingUtilities.computeIntersection(0, 0, rectangle.width, rectangle.height, paramRectangle);
    } 
  }
  
  public void computeVisibleRect(Rectangle paramRectangle) { computeVisibleRect(this, paramRectangle); }
  
  public Rectangle getVisibleRect() {
    Rectangle rectangle = new Rectangle();
    computeVisibleRect(rectangle);
    return rectangle;
  }
  
  public void firePropertyChange(String paramString, boolean paramBoolean1, boolean paramBoolean2) { super.firePropertyChange(paramString, paramBoolean1, paramBoolean2); }
  
  public void firePropertyChange(String paramString, int paramInt1, int paramInt2) { super.firePropertyChange(paramString, paramInt1, paramInt2); }
  
  public void firePropertyChange(String paramString, char paramChar1, char paramChar2) { super.firePropertyChange(paramString, paramChar1, paramChar2); }
  
  protected void fireVetoableChange(String paramString, Object paramObject1, Object paramObject2) throws PropertyVetoException {
    if (this.vetoableChangeSupport == null)
      return; 
    this.vetoableChangeSupport.fireVetoableChange(paramString, paramObject1, paramObject2);
  }
  
  public void addVetoableChangeListener(VetoableChangeListener paramVetoableChangeListener) {
    if (this.vetoableChangeSupport == null)
      this.vetoableChangeSupport = new VetoableChangeSupport(this); 
    this.vetoableChangeSupport.addVetoableChangeListener(paramVetoableChangeListener);
  }
  
  public void removeVetoableChangeListener(VetoableChangeListener paramVetoableChangeListener) {
    if (this.vetoableChangeSupport == null)
      return; 
    this.vetoableChangeSupport.removeVetoableChangeListener(paramVetoableChangeListener);
  }
  
  public VetoableChangeListener[] getVetoableChangeListeners() { return (this.vetoableChangeSupport == null) ? new VetoableChangeListener[0] : this.vetoableChangeSupport.getVetoableChangeListeners(); }
  
  public Container getTopLevelAncestor() {
    JComponent jComponent = this;
    while (jComponent != null) {
      if (jComponent instanceof Window || jComponent instanceof java.applet.Applet)
        return jComponent; 
      Container container = jComponent.getParent();
    } 
    return null;
  }
  
  private AncestorNotifier getAncestorNotifier() { return (AncestorNotifier)getClientProperty(ClientPropertyKey.JComponent_ANCESTOR_NOTIFIER); }
  
  public void addAncestorListener(AncestorListener paramAncestorListener) {
    AncestorNotifier ancestorNotifier = getAncestorNotifier();
    if (ancestorNotifier == null) {
      ancestorNotifier = new AncestorNotifier(this);
      putClientProperty(ClientPropertyKey.JComponent_ANCESTOR_NOTIFIER, ancestorNotifier);
    } 
    ancestorNotifier.addAncestorListener(paramAncestorListener);
  }
  
  public void removeAncestorListener(AncestorListener paramAncestorListener) {
    AncestorNotifier ancestorNotifier = getAncestorNotifier();
    if (ancestorNotifier == null)
      return; 
    ancestorNotifier.removeAncestorListener(paramAncestorListener);
    if (ancestorNotifier.listenerList.getListenerList().length == 0) {
      ancestorNotifier.removeAllListeners();
      putClientProperty(ClientPropertyKey.JComponent_ANCESTOR_NOTIFIER, null);
    } 
  }
  
  public AncestorListener[] getAncestorListeners() {
    AncestorNotifier ancestorNotifier = getAncestorNotifier();
    return (ancestorNotifier == null) ? new AncestorListener[0] : ancestorNotifier.getAncestorListeners();
  }
  
  public <T extends EventListener> T[] getListeners(Class<T> paramClass) {
    EventListener[] arrayOfEventListener;
    if (paramClass == AncestorListener.class) {
      arrayOfEventListener = (EventListener[])getAncestorListeners();
    } else if (paramClass == VetoableChangeListener.class) {
      arrayOfEventListener = (EventListener[])getVetoableChangeListeners();
    } else if (paramClass == PropertyChangeListener.class) {
      arrayOfEventListener = (EventListener[])getPropertyChangeListeners();
    } else {
      arrayOfEventListener = this.listenerList.getListeners(paramClass);
    } 
    return (arrayOfEventListener.length == 0) ? (T[])super.getListeners(paramClass) : (T[])arrayOfEventListener;
  }
  
  public void addNotify() {
    super.addNotify();
    firePropertyChange("ancestor", null, getParent());
    registerWithKeyboardManager(false);
    registerNextFocusableComponent();
  }
  
  public void removeNotify() {
    super.removeNotify();
    firePropertyChange("ancestor", getParent(), null);
    unregisterWithKeyboardManager();
    deregisterNextFocusableComponent();
    if (getCreatedDoubleBuffer()) {
      RepaintManager.currentManager(this).resetDoubleBuffer();
      setCreatedDoubleBuffer(false);
    } 
    if (this.autoscrolls)
      Autoscroller.stop(this); 
  }
  
  public void repaint(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { RepaintManager.currentManager(SunToolkit.targetToAppContext(this)).addDirtyRegion(this, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public void repaint(Rectangle paramRectangle) { repaint(0L, paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height); }
  
  public void revalidate() {
    if (getParent() == null)
      return; 
    if (SunToolkit.isDispatchThreadForAppContext(this)) {
      invalidate();
      RepaintManager.currentManager(this).addInvalidComponent(this);
    } else {
      if (this.revalidateRunnableScheduled.getAndSet(true))
        return; 
      SunToolkit.executeOnEventHandlerThread(this, () -> {
            this.revalidateRunnableScheduled.set(false);
            revalidate();
          });
    } 
  }
  
  public boolean isValidateRoot() { return false; }
  
  public boolean isOptimizedDrawingEnabled() { return true; }
  
  protected boolean isPaintingOrigin() { return false; }
  
  public void paintImmediately(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    Container container = this;
    if (!isShowing())
      return; 
    JComponent jComponent = SwingUtilities.getPaintingOrigin(this);
    if (jComponent != null) {
      Rectangle rectangle = SwingUtilities.convertRectangle(container, new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4), jComponent);
      jComponent.paintImmediately(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
      return;
    } 
    while (!container.isOpaque()) {
      Container container1 = container.getParent();
      if (container1 != null) {
        paramInt1 += container.getX();
        paramInt2 += container.getY();
        container = container1;
        if (!(container instanceof JComponent))
          break; 
      } 
    } 
    if (container instanceof JComponent) {
      ((JComponent)container)._paintImmediately(paramInt1, paramInt2, paramInt3, paramInt4);
    } else {
      container.repaint(paramInt1, paramInt2, paramInt3, paramInt4);
    } 
  }
  
  public void paintImmediately(Rectangle paramRectangle) { paintImmediately(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height); }
  
  boolean alwaysOnTop() { return false; }
  
  void setPaintingChild(Component paramComponent) { this.paintingChild = paramComponent; }
  
  void _paintImmediately(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int k = 0;
    int m = 0;
    boolean bool1 = false;
    JComponent jComponent1 = null;
    jComponent2 = this;
    RepaintManager repaintManager = RepaintManager.currentManager(this);
    arrayList = new ArrayList(7);
    b3 = -1;
    byte b4 = 0;
    int j = 0;
    int i = j;
    byte b2 = i;
    byte b1 = b2;
    Rectangle rectangle = fetchRectangle();
    rectangle.x = paramInt1;
    rectangle.y = paramInt2;
    rectangle.width = paramInt3;
    rectangle.height = paramInt4;
    boolean bool2 = (alwaysOnTop() && isOpaque()) ? 1 : 0;
    if (bool2) {
      SwingUtilities.computeIntersection(0, 0, getWidth(), getHeight(), rectangle);
      if (rectangle.width == 0) {
        recycleRectangle(rectangle);
        return;
      } 
    } 
    Container container = this;
    JComponent jComponent3 = null;
    while (container != null && !(container instanceof Window) && !(container instanceof java.applet.Applet)) {
      JComponent jComponent = (container instanceof JComponent) ? (JComponent)container : null;
      arrayList.add(container);
      if (!bool2 && jComponent != null && !jComponent.isOptimizedDrawingEnabled()) {
        boolean bool;
        if (container != this) {
          if (jComponent.isPaintingOrigin()) {
            bool = true;
          } else {
            Component[] arrayOfComponent = container.getComponents();
            byte b;
            for (b = 0; b < arrayOfComponent.length && arrayOfComponent[b] != jComponent3; b++);
            switch (jComponent.getObscuredState(b, rectangle.x, rectangle.y, rectangle.width, rectangle.height)) {
              case 0:
                bool = false;
                break;
              case 2:
                recycleRectangle(rectangle);
                return;
              default:
                bool = true;
                break;
            } 
          } 
        } else {
          bool = false;
        } 
        if (bool) {
          jComponent2 = jComponent;
          b3 = b4;
          k = m = 0;
          bool1 = false;
        } 
      } 
      b4++;
      if (repaintManager.isDoubleBufferingEnabled() && jComponent != null && jComponent.isDoubleBuffered()) {
        bool1 = true;
        jComponent1 = jComponent;
      } 
      if (!bool2) {
        int n = container.getX();
        int i1 = container.getY();
        i = container.getWidth();
        j = container.getHeight();
        SwingUtilities.computeIntersection(b1, b2, i, j, rectangle);
        rectangle.x += n;
        rectangle.y += i1;
        k += n;
        m += i1;
      } 
      jComponent3 = container;
      container = container.getParent();
    } 
    if (container == null || container.getPeer() == null || rectangle.width <= 0 || rectangle.height <= 0) {
      recycleRectangle(rectangle);
      return;
    } 
    jComponent2.setFlag(13, true);
    rectangle.x -= k;
    rectangle.y -= m;
    if (jComponent2 != this)
      for (byte b = b3; b > 0; b--) {
        Component component = (Component)arrayList.get(b);
        if (component instanceof JComponent)
          ((JComponent)component).setPaintingChild((Component)arrayList.get(b - 1)); 
      }  
    try {
      if ((graphics = safelyGetGraphics(jComponent2, container)) != null)
        try {
          if (bool1) {
            repaintManager1 = RepaintManager.currentManager(jComponent1);
            repaintManager1.beginPaint();
            try {
              repaintManager1.paint(jComponent2, jComponent1, graphics, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
            } finally {
              repaintManager1.endPaint();
            } 
          } else {
            graphics.setClip(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
            jComponent2.paint(graphics);
          } 
        } finally {
          graphics.dispose();
        }  
    } finally {
      if (jComponent2 != this)
        for (byte b = b3; b > 0; b--) {
          Component component = (Component)arrayList.get(b);
          if (component instanceof JComponent)
            ((JComponent)component).setPaintingChild(null); 
        }  
      jComponent2.setFlag(13, false);
    } 
    recycleRectangle(rectangle);
  }
  
  void paintToOffscreen(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    try {
      setFlag(1, true);
      if (paramInt2 + paramInt4 < paramInt6 || paramInt1 + paramInt3 < paramInt5)
        setFlag(2, true); 
      if (getFlag(13)) {
        paint(paramGraphics);
      } else {
        if (!rectangleIsObscured(paramInt1, paramInt2, paramInt3, paramInt4)) {
          paintComponent(paramGraphics);
          paintBorder(paramGraphics);
        } 
        paintChildren(paramGraphics);
      } 
    } finally {
      setFlag(1, false);
      setFlag(2, false);
    } 
  }
  
  private int getObscuredState(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    byte b = 0;
    Rectangle rectangle = fetchRectangle();
    for (int i = paramInt1 - 1; i >= 0; i--) {
      boolean bool;
      Component component = getComponent(i);
      if (!component.isVisible())
        continue; 
      if (component instanceof JComponent) {
        bool = component.isOpaque();
        if (!bool && b == 1)
          continue; 
      } else {
        bool = true;
      } 
      Rectangle rectangle1 = component.getBounds(rectangle);
      if (bool && paramInt2 >= rectangle1.x && paramInt2 + paramInt4 <= rectangle1.x + rectangle1.width && paramInt3 >= rectangle1.y && paramInt3 + paramInt5 <= rectangle1.y + rectangle1.height) {
        recycleRectangle(rectangle);
        return 2;
      } 
      if (!b && paramInt2 + paramInt4 > rectangle1.x && paramInt3 + paramInt5 > rectangle1.y && paramInt2 < rectangle1.x + rectangle1.width && paramInt3 < rectangle1.y + rectangle1.height)
        b = 1; 
      continue;
    } 
    recycleRectangle(rectangle);
    return b;
  }
  
  boolean checkIfChildObscuredBySibling() { return true; }
  
  private void setFlag(int paramInt, boolean paramBoolean) {
    if (paramBoolean) {
      this.flags |= 1 << paramInt;
    } else {
      this.flags &= (1 << paramInt ^ 0xFFFFFFFF);
    } 
  }
  
  private boolean getFlag(int paramInt) {
    int i = 1 << paramInt;
    return ((this.flags & i) == i);
  }
  
  static void setWriteObjCounter(JComponent paramJComponent, byte paramByte) { paramJComponent.flags = paramJComponent.flags & 0xFFC03FFF | paramByte << 14; }
  
  static byte getWriteObjCounter(JComponent paramJComponent) { return (byte)(paramJComponent.flags >> 14 & 0xFF); }
  
  public void setDoubleBuffered(boolean paramBoolean) { setFlag(0, paramBoolean); }
  
  public boolean isDoubleBuffered() { return getFlag(0); }
  
  public JRootPane getRootPane() { return SwingUtilities.getRootPane(this); }
  
  void compWriteObjectNotify() {
    byte b = getWriteObjCounter(this);
    setWriteObjCounter(this, (byte)(b + 1));
    if (b != 0)
      return; 
    uninstallUIAndProperties();
    if (getToolTipText() != null || this instanceof javax.swing.table.JTableHeader)
      ToolTipManager.sharedInstance().unregisterComponent(this); 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    ReadObjectCallback readObjectCallback = (ReadObjectCallback)readObjectCallbacks.get(paramObjectInputStream);
    if (readObjectCallback == null)
      try {
        readObjectCallbacks.put(paramObjectInputStream, readObjectCallback = new ReadObjectCallback(paramObjectInputStream));
      } catch (Exception exception) {
        throw new IOException(exception.toString());
      }  
    readObjectCallback.registerComponent(this);
    int i = paramObjectInputStream.readInt();
    if (i > 0) {
      this.clientProperties = new ArrayTable();
      for (byte b = 0; b < i; b++)
        this.clientProperties.put(paramObjectInputStream.readObject(), paramObjectInputStream.readObject()); 
    } 
    if (getToolTipText() != null)
      ToolTipManager.sharedInstance().registerComponent(this); 
    setWriteObjCounter(this, (byte)0);
    this.revalidateRunnableScheduled = new AtomicBoolean(false);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("ComponentUI")) {
      byte b = getWriteObjCounter(this);
      b = (byte)(b - 1);
      setWriteObjCounter(this, b);
      if (b == 0 && this.ui != null)
        this.ui.installUI(this); 
    } 
    ArrayTable.writeArrayTable(paramObjectOutputStream, this.clientProperties);
  }
  
  protected String paramString() {
    String str1 = isPreferredSizeSet() ? getPreferredSize().toString() : "";
    String str2 = isMinimumSizeSet() ? getMinimumSize().toString() : "";
    String str3 = isMaximumSizeSet() ? getMaximumSize().toString() : "";
    String str4 = (this.border == null) ? "" : ((this.border == this) ? "this" : this.border.toString());
    return super.paramString() + ",alignmentX=" + this.alignmentX + ",alignmentY=" + this.alignmentY + ",border=" + str4 + ",flags=" + this.flags + ",maximumSize=" + str3 + ",minimumSize=" + str2 + ",preferredSize=" + str1;
  }
  
  @Deprecated
  public void hide() {
    boolean bool = isShowing();
    super.hide();
    if (bool) {
      Container container = getParent();
      if (container != null) {
        Rectangle rectangle = getBounds();
        container.repaint(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
      } 
      revalidate();
    } 
  }
  
  public abstract class AccessibleJComponent extends Container.AccessibleAWTContainer implements AccessibleExtendedComponent {
    @Deprecated
    protected FocusListener accessibleFocusHandler = null;
    
    protected AccessibleJComponent() { super(JComponent.this); }
    
    public void addPropertyChangeListener(PropertyChangeListener param1PropertyChangeListener) { super.addPropertyChangeListener(param1PropertyChangeListener); }
    
    public void removePropertyChangeListener(PropertyChangeListener param1PropertyChangeListener) { super.removePropertyChangeListener(param1PropertyChangeListener); }
    
    protected String getBorderTitle(Border param1Border) {
      if (param1Border instanceof TitledBorder)
        return ((TitledBorder)param1Border).getTitle(); 
      if (param1Border instanceof CompoundBorder) {
        String str = getBorderTitle(((CompoundBorder)param1Border).getInsideBorder());
        if (str == null)
          str = getBorderTitle(((CompoundBorder)param1Border).getOutsideBorder()); 
        return str;
      } 
      return null;
    }
    
    public String getAccessibleName() {
      String str = this.accessibleName;
      if (str == null)
        str = (String)JComponent.this.getClientProperty("AccessibleName"); 
      if (str == null)
        str = getBorderTitle(JComponent.this.getBorder()); 
      if (str == null) {
        Object object = JComponent.this.getClientProperty("labeledBy");
        if (object instanceof Accessible) {
          AccessibleContext accessibleContext = ((Accessible)object).getAccessibleContext();
          if (accessibleContext != null)
            str = accessibleContext.getAccessibleName(); 
        } 
      } 
      return str;
    }
    
    public String getAccessibleDescription() {
      String str = this.accessibleDescription;
      if (str == null)
        str = (String)JComponent.this.getClientProperty("AccessibleDescription"); 
      if (str == null)
        try {
          str = getToolTipText();
        } catch (Exception exception) {} 
      if (str == null) {
        Object object = JComponent.this.getClientProperty("labeledBy");
        if (object instanceof Accessible) {
          AccessibleContext accessibleContext = ((Accessible)object).getAccessibleContext();
          if (accessibleContext != null)
            str = accessibleContext.getAccessibleDescription(); 
        } 
      } 
      return str;
    }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.SWING_COMPONENT; }
    
    public AccessibleStateSet getAccessibleStateSet() {
      AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
      if (JComponent.this.isOpaque())
        accessibleStateSet.add(AccessibleState.OPAQUE); 
      return accessibleStateSet;
    }
    
    public int getAccessibleChildrenCount() { return super.getAccessibleChildrenCount(); }
    
    public Accessible getAccessibleChild(int param1Int) { return super.getAccessibleChild(param1Int); }
    
    AccessibleExtendedComponent getAccessibleExtendedComponent() { return this; }
    
    public String getToolTipText() { return JComponent.this.getToolTipText(); }
    
    public String getTitledBorderText() {
      Border border = JComponent.this.getBorder();
      return (border instanceof TitledBorder) ? ((TitledBorder)border).getTitle() : null;
    }
    
    public AccessibleKeyBinding getAccessibleKeyBinding() {
      Object object = JComponent.this.getClientProperty("labeledBy");
      if (object instanceof Accessible) {
        AccessibleContext accessibleContext = ((Accessible)object).getAccessibleContext();
        if (accessibleContext != null) {
          AccessibleComponent accessibleComponent = accessibleContext.getAccessibleComponent();
          return !(accessibleComponent instanceof AccessibleExtendedComponent) ? null : ((AccessibleExtendedComponent)accessibleComponent).getAccessibleKeyBinding();
        } 
      } 
      return null;
    }
    
    protected class AccessibleContainerHandler implements ContainerListener {
      public void componentAdded(ContainerEvent param2ContainerEvent) {
        Component component = param2ContainerEvent.getChild();
        if (component != null && component instanceof Accessible)
          JComponent.AccessibleJComponent.this.firePropertyChange("AccessibleChild", null, component.getAccessibleContext()); 
      }
      
      public void componentRemoved(ContainerEvent param2ContainerEvent) {
        Component component = param2ContainerEvent.getChild();
        if (component != null && component instanceof Accessible)
          JComponent.AccessibleJComponent.this.firePropertyChange("AccessibleChild", component.getAccessibleContext(), null); 
      }
    }
    
    protected class AccessibleFocusHandler implements FocusListener {
      public void focusGained(FocusEvent param2FocusEvent) {
        if (JComponent.AccessibleJComponent.this.this$0.accessibleContext != null)
          JComponent.AccessibleJComponent.this.this$0.accessibleContext.firePropertyChange("AccessibleState", null, AccessibleState.FOCUSED); 
      }
      
      public void focusLost(FocusEvent param2FocusEvent) {
        if (JComponent.AccessibleJComponent.this.this$0.accessibleContext != null)
          JComponent.AccessibleJComponent.this.this$0.accessibleContext.firePropertyChange("AccessibleState", AccessibleState.FOCUSED, null); 
      }
    }
  }
  
  final class ActionStandin implements Action {
    private final ActionListener actionListener;
    
    private final String command;
    
    private final Action action;
    
    ActionStandin(ActionListener param1ActionListener, String param1String) {
      this.actionListener = param1ActionListener;
      if (param1ActionListener instanceof Action) {
        this.action = (Action)param1ActionListener;
      } else {
        this.action = null;
      } 
      this.command = param1String;
    }
    
    public Object getValue(String param1String) {
      if (param1String != null) {
        if (param1String.equals("ActionCommandKey"))
          return this.command; 
        if (this.action != null)
          return this.action.getValue(param1String); 
        if (param1String.equals("Name"))
          return "ActionStandin"; 
      } 
      return null;
    }
    
    public boolean isEnabled() { return (this.actionListener == null) ? false : ((this.action == null) ? true : this.action.isEnabled()); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      if (this.actionListener != null)
        this.actionListener.actionPerformed(param1ActionEvent); 
    }
    
    public void putValue(String param1String, Object param1Object) {}
    
    public void setEnabled(boolean param1Boolean) {}
    
    public void addPropertyChangeListener(PropertyChangeListener param1PropertyChangeListener) {}
    
    public void removePropertyChangeListener(PropertyChangeListener param1PropertyChangeListener) {}
  }
  
  static final class IntVector {
    int[] array = null;
    
    int count = 0;
    
    int capacity = 0;
    
    int size() { return this.count; }
    
    int elementAt(int param1Int) { return this.array[param1Int]; }
    
    void addElement(int param1Int) {
      if (this.count == this.capacity) {
        this.capacity = (this.capacity + 2) * 2;
        int[] arrayOfInt = new int[this.capacity];
        if (this.count > 0)
          System.arraycopy(this.array, 0, arrayOfInt, 0, this.count); 
        this.array = arrayOfInt;
      } 
      this.array[this.count++] = param1Int;
    }
    
    void setElementAt(int param1Int1, int param1Int2) { this.array[param1Int2] = param1Int1; }
  }
  
  static class KeyboardState implements Serializable {
    private static final Object keyCodesKey = KeyboardState.class;
    
    static JComponent.IntVector getKeyCodeArray() {
      JComponent.IntVector intVector = (JComponent.IntVector)SwingUtilities.appContextGet(keyCodesKey);
      if (intVector == null) {
        intVector = new JComponent.IntVector();
        SwingUtilities.appContextPut(keyCodesKey, intVector);
      } 
      return intVector;
    }
    
    static void registerKeyPressed(int param1Int) {
      JComponent.IntVector intVector = getKeyCodeArray();
      int i = intVector.size();
      for (byte b = 0; b < i; b++) {
        if (intVector.elementAt(b) == -1) {
          intVector.setElementAt(param1Int, b);
          return;
        } 
      } 
      intVector.addElement(param1Int);
    }
    
    static void registerKeyReleased(int param1Int) {
      JComponent.IntVector intVector = getKeyCodeArray();
      int i = intVector.size();
      for (byte b = 0; b < i; b++) {
        if (intVector.elementAt(b) == param1Int) {
          intVector.setElementAt(-1, b);
          return;
        } 
      } 
    }
    
    static boolean keyIsPressed(int param1Int) {
      JComponent.IntVector intVector = getKeyCodeArray();
      int i = intVector.size();
      for (byte b = 0; b < i; b++) {
        if (intVector.elementAt(b) == param1Int)
          return true; 
      } 
      return false;
    }
    
    static boolean shouldProcess(KeyEvent param1KeyEvent) {
      switch (param1KeyEvent.getID()) {
        case 401:
          if (!keyIsPressed(param1KeyEvent.getKeyCode()))
            registerKeyPressed(param1KeyEvent.getKeyCode()); 
          return true;
        case 402:
          if (keyIsPressed(param1KeyEvent.getKeyCode()) || param1KeyEvent.getKeyCode() == 154) {
            registerKeyReleased(param1KeyEvent.getKeyCode());
            return true;
          } 
          return false;
        case 400:
          return true;
      } 
      return false;
    }
  }
  
  private class ReadObjectCallback implements ObjectInputValidation {
    private final Vector<JComponent> roots = new Vector(1);
    
    private final ObjectInputStream inputStream;
    
    ReadObjectCallback(ObjectInputStream param1ObjectInputStream) throws Exception {
      this.inputStream = param1ObjectInputStream;
      param1ObjectInputStream.registerValidation(this, 0);
    }
    
    public void validateObject() {
      try {
        for (JComponent jComponent : this.roots)
          SwingUtilities.updateComponentTreeUI(jComponent); 
      } finally {
        readObjectCallbacks.remove(this.inputStream);
      } 
    }
    
    private void registerComponent(JComponent param1JComponent) {
      for (JComponent jComponent1 : this.roots) {
        JComponent jComponent2 = param1JComponent;
        while (jComponent2 != null) {
          if (jComponent2 == jComponent1)
            return; 
          Container container = jComponent2.getParent();
        } 
      } 
      for (byte b = 0; b < this.roots.size(); b++) {
        JComponent jComponent = (JComponent)this.roots.elementAt(b);
        for (Container container = jComponent.getParent(); container != null; container = container.getParent()) {
          if (container == param1JComponent) {
            this.roots.removeElementAt(b--);
            break;
          } 
        } 
      } 
      this.roots.addElement(param1JComponent);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\JComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */