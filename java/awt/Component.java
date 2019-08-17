package java.awt;

import java.applet.Applet;
import java.awt.dnd.DropTarget;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.PaintEvent;
import java.awt.event.WindowEvent;
import java.awt.im.InputContext;
import java.awt.im.InputMethodRequests;
import java.awt.image.BufferStrategy;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.VolatileImage;
import java.awt.peer.ComponentPeer;
import java.awt.peer.ContainerPeer;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.Transient;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Vector;
import java.util.WeakHashMap;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.swing.JComponent;
import sun.awt.AWTAccessor;
import sun.awt.AppContext;
import sun.awt.CausedFocusEvent;
import sun.awt.ConstrainableGraphics;
import sun.awt.EmbeddedFrame;
import sun.awt.EventQueueItem;
import sun.awt.RequestFocusController;
import sun.awt.SubRegionShowable;
import sun.awt.SunToolkit;
import sun.awt.WindowClosingListener;
import sun.awt.dnd.SunDropTargetEvent;
import sun.awt.im.InputContext;
import sun.awt.image.VSyncedBSManager;
import sun.font.FontDesignMetrics;
import sun.font.FontManager;
import sun.font.FontManagerFactory;
import sun.font.SunFontManager;
import sun.java2d.SunGraphics2D;
import sun.java2d.SunGraphicsEnvironment;
import sun.java2d.pipe.Region;
import sun.java2d.pipe.hw.ExtendedBufferCapabilities;
import sun.security.action.GetPropertyAction;
import sun.util.logging.PlatformLogger;

public abstract class Component implements ImageObserver, MenuContainer, Serializable {
  private static final PlatformLogger log;
  
  private static final PlatformLogger eventLog;
  
  private static final PlatformLogger focusLog;
  
  private static final PlatformLogger mixingLog = (focusLog = (eventLog = (log = PlatformLogger.getLogger("java.awt.Component")).getLogger("java.awt.event.Component")).getLogger("java.awt.focus.Component")).getLogger("java.awt.mixing.Component");
  
  ComponentPeer peer;
  
  Container parent;
  
  AppContext appContext = AppContext.getAppContext();
  
  int x;
  
  int y;
  
  int width;
  
  int height;
  
  Color foreground;
  
  Color background;
  
  Font peerFont;
  
  Cursor cursor;
  
  Locale locale;
  
  BufferStrategy bufferStrategy = null;
  
  boolean ignoreRepaint = false;
  
  boolean visible = true;
  
  boolean enabled = true;
  
  DropTarget dropTarget;
  
  Vector<PopupMenu> popups;
  
  private String name;
  
  private boolean nameExplicitlySet = false;
  
  private boolean focusable = true;
  
  private static final int FOCUS_TRAVERSABLE_UNKNOWN = 0;
  
  private static final int FOCUS_TRAVERSABLE_DEFAULT = 1;
  
  private static final int FOCUS_TRAVERSABLE_SET = 2;
  
  private int isFocusTraversableOverridden = 0;
  
  Set<AWTKeyStroke>[] focusTraversalKeys;
  
  private static final String[] focusTraversalKeyPropertyNames = { "forwardFocusTraversalKeys", "backwardFocusTraversalKeys", "upCycleFocusTraversalKeys", "downCycleFocusTraversalKeys" };
  
  private boolean focusTraversalKeysEnabled = true;
  
  static final Object LOCK = new AWTTreeLock();
  
  Dimension minSize;
  
  boolean minSizeSet;
  
  Dimension prefSize;
  
  boolean prefSizeSet;
  
  Dimension maxSize;
  
  boolean maxSizeSet;
  
  ComponentOrientation componentOrientation = ComponentOrientation.UNKNOWN;
  
  boolean newEventsOnly = false;
  
  ComponentListener componentListener;
  
  FocusListener focusListener;
  
  HierarchyListener hierarchyListener;
  
  HierarchyBoundsListener hierarchyBoundsListener;
  
  KeyListener keyListener;
  
  MouseListener mouseListener;
  
  MouseMotionListener mouseMotionListener;
  
  MouseWheelListener mouseWheelListener;
  
  InputMethodListener inputMethodListener;
  
  RuntimeException windowClosingException = null;
  
  static final String actionListenerK = "actionL";
  
  static final String adjustmentListenerK = "adjustmentL";
  
  static final String componentListenerK = "componentL";
  
  static final String containerListenerK = "containerL";
  
  static final String focusListenerK = "focusL";
  
  static final String itemListenerK = "itemL";
  
  static final String keyListenerK = "keyL";
  
  static final String mouseListenerK = "mouseL";
  
  static final String mouseMotionListenerK = "mouseMotionL";
  
  static final String mouseWheelListenerK = "mouseWheelL";
  
  static final String textListenerK = "textL";
  
  static final String ownedWindowK = "ownedL";
  
  static final String windowListenerK = "windowL";
  
  static final String inputMethodListenerK = "inputMethodL";
  
  static final String hierarchyListenerK = "hierarchyL";
  
  static final String hierarchyBoundsListenerK = "hierarchyBoundsL";
  
  static final String windowStateListenerK = "windowStateL";
  
  static final String windowFocusListenerK = "windowFocusL";
  
  long eventMask = 4096L;
  
  static boolean isInc;
  
  static int incRate;
  
  public static final float TOP_ALIGNMENT = 0.0F;
  
  public static final float CENTER_ALIGNMENT = 0.5F;
  
  public static final float BOTTOM_ALIGNMENT = 1.0F;
  
  public static final float LEFT_ALIGNMENT = 0.0F;
  
  public static final float RIGHT_ALIGNMENT = 1.0F;
  
  private static final long serialVersionUID = -7644114512714619750L;
  
  private PropertyChangeSupport changeSupport;
  
  private Object objectLock = new Object();
  
  boolean isPacked = false;
  
  private int boundsOp = 3;
  
  private Region compoundShape = null;
  
  private Region mixingCutoutRegion = null;
  
  private boolean isAddNotifyComplete = false;
  
  boolean backgroundEraseDisabled;
  
  EventQueueItem[] eventCache;
  
  private boolean coalescingEnabled = checkCoalescing();
  
  private static final Map<Class<?>, Boolean> coalesceMap;
  
  private static final Class[] coalesceEventsParams;
  
  private static RequestFocusController requestFocusController;
  
  private boolean autoFocusTransferOnDisposal = true;
  
  private int componentSerializedDataVersion = 4;
  
  protected AccessibleContext accessibleContext = null;
  
  Object getObjectLock() { return this.objectLock; }
  
  final AccessControlContext getAccessControlContext() {
    if (this.acc == null)
      throw new SecurityException("Component is missing AccessControlContext"); 
    return this.acc;
  }
  
  int getBoundsOp() {
    assert Thread.holdsLock(getTreeLock());
    return this.boundsOp;
  }
  
  void setBoundsOp(int paramInt) {
    assert Thread.holdsLock(getTreeLock());
    if (paramInt == 5) {
      this.boundsOp = 3;
    } else if (this.boundsOp == 3) {
      this.boundsOp = paramInt;
    } 
  }
  
  void initializeFocusTraversalKeys() { this.focusTraversalKeys = new Set[3]; }
  
  String constructComponentName() { return null; }
  
  public String getName() {
    if (this.name == null && !this.nameExplicitlySet)
      synchronized (getObjectLock()) {
        if (this.name == null && !this.nameExplicitlySet)
          this.name = constructComponentName(); 
      }  
    return this.name;
  }
  
  public void setName(String paramString) {
    String str;
    synchronized (getObjectLock()) {
      str = this.name;
      this.name = paramString;
      this.nameExplicitlySet = true;
    } 
    firePropertyChange("name", str, paramString);
  }
  
  public Container getParent() { return getParent_NoClientCode(); }
  
  final Container getParent_NoClientCode() { return this.parent; }
  
  Container getContainer() { return getParent_NoClientCode(); }
  
  @Deprecated
  public ComponentPeer getPeer() { return this.peer; }
  
  public void setDropTarget(DropTarget paramDropTarget) {
    if (paramDropTarget == this.dropTarget || (this.dropTarget != null && this.dropTarget.equals(paramDropTarget)))
      return; 
    DropTarget dropTarget1;
    if ((dropTarget1 = this.dropTarget) != null) {
      if (this.peer != null)
        this.dropTarget.removeNotify(this.peer); 
      DropTarget dropTarget2 = this.dropTarget;
      this.dropTarget = null;
      try {
        dropTarget2.setComponent(null);
      } catch (IllegalArgumentException illegalArgumentException) {}
    } 
    if ((this.dropTarget = paramDropTarget) != null)
      try {
        this.dropTarget.setComponent(this);
        if (this.peer != null)
          this.dropTarget.addNotify(this.peer); 
      } catch (IllegalArgumentException illegalArgumentException) {
        if (dropTarget1 != null)
          try {
            dropTarget1.setComponent(this);
            if (this.peer != null)
              this.dropTarget.addNotify(this.peer); 
          } catch (IllegalArgumentException illegalArgumentException1) {} 
      }  
  }
  
  public DropTarget getDropTarget() { return this.dropTarget; }
  
  public GraphicsConfiguration getGraphicsConfiguration() { return getGraphicsConfiguration_NoClientCode(); }
  
  final GraphicsConfiguration getGraphicsConfiguration_NoClientCode() { return this.graphicsConfig; }
  
  void setGraphicsConfiguration(GraphicsConfiguration paramGraphicsConfiguration) {
    synchronized (getTreeLock()) {
      if (updateGraphicsData(paramGraphicsConfiguration)) {
        removeNotify();
        addNotify();
      } 
    } 
  }
  
  boolean updateGraphicsData(GraphicsConfiguration paramGraphicsConfiguration) {
    checkTreeLock();
    if (this.graphicsConfig == paramGraphicsConfiguration)
      return false; 
    this.graphicsConfig = paramGraphicsConfiguration;
    ComponentPeer componentPeer = getPeer();
    return (componentPeer != null) ? componentPeer.updateGraphicsData(paramGraphicsConfiguration) : 0;
  }
  
  void checkGD(String paramString) {
    if (this.graphicsConfig != null && !this.graphicsConfig.getDevice().getIDstring().equals(paramString))
      throw new IllegalArgumentException("adding a container to a container on a different GraphicsDevice"); 
  }
  
  public final Object getTreeLock() { return LOCK; }
  
  final void checkTreeLock() {
    if (!Thread.holdsLock(getTreeLock()))
      throw new IllegalStateException("This function should be called while holding treeLock"); 
  }
  
  public Toolkit getToolkit() { return getToolkitImpl(); }
  
  final Toolkit getToolkitImpl() {
    Container container = this.parent;
    return (container != null) ? container.getToolkitImpl() : Toolkit.getDefaultToolkit();
  }
  
  public boolean isValid() { return (this.peer != null && this.valid); }
  
  public boolean isDisplayable() { return (getPeer() != null); }
  
  @Transient
  public boolean isVisible() { return isVisible_NoClientCode(); }
  
  final boolean isVisible_NoClientCode() { return this.visible; }
  
  boolean isRecursivelyVisible() { return (this.visible && (this.parent == null || this.parent.isRecursivelyVisible())); }
  
  private Rectangle getRecursivelyVisibleBounds() {
    Container container = getContainer();
    Rectangle rectangle1 = getBounds();
    if (container == null)
      return rectangle1; 
    Rectangle rectangle2 = container.getRecursivelyVisibleBounds();
    rectangle2.setLocation(0, 0);
    return rectangle2.intersection(rectangle1);
  }
  
  Point pointRelativeToComponent(Point paramPoint) {
    Point point = getLocationOnScreen();
    return new Point(paramPoint.x - point.x, paramPoint.y - point.y);
  }
  
  Component findUnderMouseInWindow(PointerInfo paramPointerInfo) {
    if (!isShowing())
      return null; 
    Window window = getContainingWindow();
    if (!Toolkit.getDefaultToolkit().getMouseInfoPeer().isWindowUnderMouse(window))
      return null; 
    Point point = window.pointRelativeToComponent(paramPointerInfo.getLocation());
    return window.findComponentAt(point.x, point.y, true);
  }
  
  public Point getMousePosition() throws HeadlessException {
    if (GraphicsEnvironment.isHeadless())
      throw new HeadlessException(); 
    PointerInfo pointerInfo = (PointerInfo)AccessController.doPrivileged(new PrivilegedAction<PointerInfo>() {
          public PointerInfo run() { return MouseInfo.getPointerInfo(); }
        });
    synchronized (getTreeLock()) {
      Component component = findUnderMouseInWindow(pointerInfo);
      if (!isSameOrAncestorOf(component, true))
        return null; 
      return pointRelativeToComponent(pointerInfo.getLocation());
    } 
  }
  
  boolean isSameOrAncestorOf(Component paramComponent, boolean paramBoolean) { return (paramComponent == this); }
  
  public boolean isShowing() {
    if (this.visible && this.peer != null) {
      Container container = this.parent;
      return (container == null || container.isShowing());
    } 
    return false;
  }
  
  public boolean isEnabled() { return isEnabledImpl(); }
  
  final boolean isEnabledImpl() { return this.enabled; }
  
  public void setEnabled(boolean paramBoolean) { enable(paramBoolean); }
  
  @Deprecated
  public void enable() {
    if (!this.enabled) {
      synchronized (getTreeLock()) {
        this.enabled = true;
        ComponentPeer componentPeer = this.peer;
        if (componentPeer != null) {
          componentPeer.setEnabled(true);
          if (this.visible && !getRecursivelyVisibleBounds().isEmpty())
            updateCursorImmediately(); 
        } 
      } 
      if (this.accessibleContext != null)
        this.accessibleContext.firePropertyChange("AccessibleState", null, AccessibleState.ENABLED); 
    } 
  }
  
  @Deprecated
  public void enable(boolean paramBoolean) {
    if (paramBoolean) {
      enable();
    } else {
      disable();
    } 
  }
  
  @Deprecated
  public void disable() {
    if (this.enabled) {
      KeyboardFocusManager.clearMostRecentFocusOwner(this);
      synchronized (getTreeLock()) {
        this.enabled = false;
        if ((isFocusOwner() || (containsFocus() && !isLightweight())) && KeyboardFocusManager.isAutoFocusTransferEnabled())
          transferFocus(false); 
        ComponentPeer componentPeer = this.peer;
        if (componentPeer != null) {
          componentPeer.setEnabled(false);
          if (this.visible && !getRecursivelyVisibleBounds().isEmpty())
            updateCursorImmediately(); 
        } 
      } 
      if (this.accessibleContext != null)
        this.accessibleContext.firePropertyChange("AccessibleState", null, AccessibleState.ENABLED); 
    } 
  }
  
  public boolean isDoubleBuffered() { return false; }
  
  public void enableInputMethods(boolean paramBoolean) {
    if (paramBoolean) {
      if ((this.eventMask & 0x1000L) != 0L)
        return; 
      if (isFocusOwner()) {
        InputContext inputContext = getInputContext();
        if (inputContext != null) {
          FocusEvent focusEvent = new FocusEvent(this, 1004);
          inputContext.dispatchEvent(focusEvent);
        } 
      } 
      this.eventMask |= 0x1000L;
    } else {
      if ((this.eventMask & 0x1000L) != 0L) {
        InputContext inputContext = getInputContext();
        if (inputContext != null) {
          inputContext.endComposition();
          inputContext.removeNotify(this);
        } 
      } 
      this.eventMask &= 0xFFFFFFFFFFFFEFFFL;
    } 
  }
  
  public void setVisible(boolean paramBoolean) { show(paramBoolean); }
  
  @Deprecated
  public void show() {
    if (!this.visible) {
      synchronized (getTreeLock()) {
        this.visible = true;
        mixOnShowing();
        ComponentPeer componentPeer = this.peer;
        if (componentPeer != null) {
          componentPeer.setVisible(true);
          createHierarchyEvents(1400, this, this.parent, 4L, Toolkit.enabledOnToolkit(32768L));
          if (componentPeer instanceof java.awt.peer.LightweightPeer)
            repaint(); 
          updateCursorImmediately();
        } 
        if (this.componentListener != null || (this.eventMask & 0x1L) != 0L || Toolkit.enabledOnToolkit(1L)) {
          ComponentEvent componentEvent = new ComponentEvent(this, 102);
          Toolkit.getEventQueue().postEvent(componentEvent);
        } 
      } 
      Container container = this.parent;
      if (container != null)
        container.invalidate(); 
    } 
  }
  
  @Deprecated
  public void show(boolean paramBoolean) {
    if (paramBoolean) {
      show();
    } else {
      hide();
    } 
  }
  
  boolean containsFocus() { return isFocusOwner(); }
  
  void clearMostRecentFocusOwnerOnHide() { KeyboardFocusManager.clearMostRecentFocusOwner(this); }
  
  void clearCurrentFocusCycleRootOnHide() {}
  
  @Deprecated
  public void hide() {
    this.isPacked = false;
    if (this.visible) {
      clearCurrentFocusCycleRootOnHide();
      clearMostRecentFocusOwnerOnHide();
      synchronized (getTreeLock()) {
        this.visible = false;
        mixOnHiding(isLightweight());
        if (containsFocus() && KeyboardFocusManager.isAutoFocusTransferEnabled())
          transferFocus(true); 
        ComponentPeer componentPeer = this.peer;
        if (componentPeer != null) {
          componentPeer.setVisible(false);
          createHierarchyEvents(1400, this, this.parent, 4L, Toolkit.enabledOnToolkit(32768L));
          if (componentPeer instanceof java.awt.peer.LightweightPeer)
            repaint(); 
          updateCursorImmediately();
        } 
        if (this.componentListener != null || (this.eventMask & 0x1L) != 0L || Toolkit.enabledOnToolkit(1L)) {
          ComponentEvent componentEvent = new ComponentEvent(this, 103);
          Toolkit.getEventQueue().postEvent(componentEvent);
        } 
      } 
      Container container = this.parent;
      if (container != null)
        container.invalidate(); 
    } 
  }
  
  @Transient
  public Color getForeground() {
    Color color = this.foreground;
    if (color != null)
      return color; 
    Container container = this.parent;
    return (container != null) ? container.getForeground() : null;
  }
  
  public void setForeground(Color paramColor) {
    Color color = this.foreground;
    ComponentPeer componentPeer = this.peer;
    this.foreground = paramColor;
    if (componentPeer != null) {
      paramColor = getForeground();
      if (paramColor != null)
        componentPeer.setForeground(paramColor); 
    } 
    firePropertyChange("foreground", color, paramColor);
  }
  
  public boolean isForegroundSet() { return (this.foreground != null); }
  
  @Transient
  public Color getBackground() {
    Color color = this.background;
    if (color != null)
      return color; 
    Container container = this.parent;
    return (container != null) ? container.getBackground() : null;
  }
  
  public void setBackground(Color paramColor) {
    Color color = this.background;
    ComponentPeer componentPeer = this.peer;
    this.background = paramColor;
    if (componentPeer != null) {
      paramColor = getBackground();
      if (paramColor != null)
        componentPeer.setBackground(paramColor); 
    } 
    firePropertyChange("background", color, paramColor);
  }
  
  public boolean isBackgroundSet() { return (this.background != null); }
  
  @Transient
  public Font getFont() { return getFont_NoClientCode(); }
  
  final Font getFont_NoClientCode() {
    Font font1 = this.font;
    if (font1 != null)
      return font1; 
    Container container = this.parent;
    return (container != null) ? container.getFont_NoClientCode() : null;
  }
  
  public void setFont(Font paramFont) {
    Font font2;
    Font font1;
    synchronized (getTreeLock()) {
      font1 = this.font;
      font2 = this.font = paramFont;
      ComponentPeer componentPeer = this.peer;
      if (componentPeer != null) {
        paramFont = getFont();
        if (paramFont != null) {
          componentPeer.setFont(paramFont);
          this.peerFont = paramFont;
        } 
      } 
    } 
    firePropertyChange("font", font1, font2);
    if (paramFont != font1 && (font1 == null || !font1.equals(paramFont)))
      invalidateIfValid(); 
  }
  
  public boolean isFontSet() { return (this.font != null); }
  
  public Locale getLocale() {
    Locale locale1 = this.locale;
    if (locale1 != null)
      return locale1; 
    Container container = this.parent;
    if (container == null)
      throw new IllegalComponentStateException("This component must have a parent in order to determine its locale"); 
    return container.getLocale();
  }
  
  public void setLocale(Locale paramLocale) {
    Locale locale1 = this.locale;
    this.locale = paramLocale;
    firePropertyChange("locale", locale1, paramLocale);
    invalidateIfValid();
  }
  
  public ColorModel getColorModel() {
    ComponentPeer componentPeer = this.peer;
    return (componentPeer != null && !(componentPeer instanceof java.awt.peer.LightweightPeer)) ? componentPeer.getColorModel() : (GraphicsEnvironment.isHeadless() ? ColorModel.getRGBdefault() : getToolkit().getColorModel());
  }
  
  public Point getLocation() throws HeadlessException { return location(); }
  
  public Point getLocationOnScreen() throws HeadlessException {
    synchronized (getTreeLock()) {
      return getLocationOnScreen_NoTreeLock();
    } 
  }
  
  final Point getLocationOnScreen_NoTreeLock() throws HeadlessException {
    if (this.peer != null && isShowing()) {
      if (this.peer instanceof java.awt.peer.LightweightPeer) {
        Container container = getNativeContainer();
        Point point = container.peer.getLocationOnScreen();
        for (Component component = this; component != container; component = component.getParent()) {
          point.x += component.x;
          point.y += component.y;
        } 
        return point;
      } 
      return this.peer.getLocationOnScreen();
    } 
    throw new IllegalComponentStateException("component must be showing on the screen to determine its location");
  }
  
  @Deprecated
  public Point location() throws HeadlessException { return location_NoClientCode(); }
  
  private Point location_NoClientCode() throws HeadlessException { return new Point(this.x, this.y); }
  
  public void setLocation(int paramInt1, int paramInt2) { move(paramInt1, paramInt2); }
  
  @Deprecated
  public void move(int paramInt1, int paramInt2) {
    synchronized (getTreeLock()) {
      setBoundsOp(1);
      setBounds(paramInt1, paramInt2, this.width, this.height);
    } 
  }
  
  public void setLocation(Point paramPoint) { setLocation(paramPoint.x, paramPoint.y); }
  
  public Dimension getSize() { return size(); }
  
  @Deprecated
  public Dimension size() { return new Dimension(this.width, this.height); }
  
  public void setSize(int paramInt1, int paramInt2) { resize(paramInt1, paramInt2); }
  
  @Deprecated
  public void resize(int paramInt1, int paramInt2) {
    synchronized (getTreeLock()) {
      setBoundsOp(2);
      setBounds(this.x, this.y, paramInt1, paramInt2);
    } 
  }
  
  public void setSize(Dimension paramDimension) { resize(paramDimension); }
  
  @Deprecated
  public void resize(Dimension paramDimension) { setSize(paramDimension.width, paramDimension.height); }
  
  public Rectangle getBounds() { return bounds(); }
  
  @Deprecated
  public Rectangle bounds() { return new Rectangle(this.x, this.y, this.width, this.height); }
  
  public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { reshape(paramInt1, paramInt2, paramInt3, paramInt4); }
  
  @Deprecated
  public void reshape(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    synchronized (getTreeLock()) {
      try {
        setBoundsOp(3);
        boolean bool1 = (this.width != paramInt3 || this.height != paramInt4);
        boolean bool2 = (this.x != paramInt1 || this.y != paramInt2);
        if (!bool1 && !bool2)
          return; 
        int i = this.x;
        int j = this.y;
        int k = this.width;
        int m = this.height;
        this.x = paramInt1;
        this.y = paramInt2;
        this.width = paramInt3;
        this.height = paramInt4;
        if (bool1)
          this.isPacked = false; 
        boolean bool = true;
        mixOnReshaping();
        if (this.peer != null) {
          if (!(this.peer instanceof java.awt.peer.LightweightPeer)) {
            reshapeNativePeer(paramInt1, paramInt2, paramInt3, paramInt4, getBoundsOp());
            bool1 = (k != this.width || m != this.height);
            bool2 = (i != this.x || j != this.y);
            if (this instanceof Window)
              bool = false; 
          } 
          if (bool1)
            invalidate(); 
          if (this.parent != null)
            this.parent.invalidateIfValid(); 
        } 
        if (bool)
          notifyNewBounds(bool1, bool2); 
        repaintParentIfNeeded(i, j, k, m);
      } finally {
        setBoundsOp(5);
      } 
    } 
  }
  
  private void repaintParentIfNeeded(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (this.parent != null && this.peer instanceof java.awt.peer.LightweightPeer && isShowing()) {
      this.parent.repaint(paramInt1, paramInt2, paramInt3, paramInt4);
      repaint();
    } 
  }
  
  private void reshapeNativePeer(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    int i = paramInt1;
    int j = paramInt2;
    for (Container container = this.parent; container != null && container.peer instanceof java.awt.peer.LightweightPeer; container = container.parent) {
      i += container.x;
      j += container.y;
    } 
    this.peer.setBounds(i, j, paramInt3, paramInt4, paramInt5);
  }
  
  private void notifyNewBounds(boolean paramBoolean1, boolean paramBoolean2) {
    if (this.componentListener != null || (this.eventMask & 0x1L) != 0L || Toolkit.enabledOnToolkit(1L)) {
      if (paramBoolean1) {
        ComponentEvent componentEvent = new ComponentEvent(this, 101);
        Toolkit.getEventQueue().postEvent(componentEvent);
      } 
      if (paramBoolean2) {
        ComponentEvent componentEvent = new ComponentEvent(this, 100);
        Toolkit.getEventQueue().postEvent(componentEvent);
      } 
    } else if (this instanceof Container && ((Container)this).countComponents() > 0) {
      boolean bool = Toolkit.enabledOnToolkit(65536L);
      if (paramBoolean1)
        ((Container)this).createChildHierarchyEvents(1402, 0L, bool); 
      if (paramBoolean2)
        ((Container)this).createChildHierarchyEvents(1401, 0L, bool); 
    } 
  }
  
  public void setBounds(Rectangle paramRectangle) { setBounds(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height); }
  
  public int getX() { return this.x; }
  
  public int getY() { return this.y; }
  
  public int getWidth() { return this.width; }
  
  public int getHeight() { return this.height; }
  
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
  
  public boolean isOpaque() { return (getPeer() == null) ? false : (!isLightweight()); }
  
  public boolean isLightweight() { return getPeer() instanceof java.awt.peer.LightweightPeer; }
  
  public void setPreferredSize(Dimension paramDimension) {
    Object object;
    if (this.prefSizeSet) {
      object = this.prefSize;
    } else {
      object = null;
    } 
    this.prefSize = paramDimension;
    this.prefSizeSet = (paramDimension != null);
    firePropertyChange("preferredSize", object, paramDimension);
  }
  
  public boolean isPreferredSizeSet() { return this.prefSizeSet; }
  
  public Dimension getPreferredSize() { return preferredSize(); }
  
  @Deprecated
  public Dimension preferredSize() {
    Dimension dimension = this.prefSize;
    if (dimension == null || (!isPreferredSizeSet() && !isValid()))
      synchronized (getTreeLock()) {
        this.prefSize = (this.peer != null) ? this.peer.getPreferredSize() : getMinimumSize();
        dimension = this.prefSize;
      }  
    return new Dimension(dimension);
  }
  
  public void setMinimumSize(Dimension paramDimension) {
    Object object;
    if (this.minSizeSet) {
      object = this.minSize;
    } else {
      object = null;
    } 
    this.minSize = paramDimension;
    this.minSizeSet = (paramDimension != null);
    firePropertyChange("minimumSize", object, paramDimension);
  }
  
  public boolean isMinimumSizeSet() { return this.minSizeSet; }
  
  public Dimension getMinimumSize() { return minimumSize(); }
  
  @Deprecated
  public Dimension minimumSize() {
    Dimension dimension = this.minSize;
    if (dimension == null || (!isMinimumSizeSet() && !isValid()))
      synchronized (getTreeLock()) {
        this.minSize = (this.peer != null) ? this.peer.getMinimumSize() : size();
        dimension = this.minSize;
      }  
    return new Dimension(dimension);
  }
  
  public void setMaximumSize(Dimension paramDimension) {
    Object object;
    if (this.maxSizeSet) {
      object = this.maxSize;
    } else {
      object = null;
    } 
    this.maxSize = paramDimension;
    this.maxSizeSet = (paramDimension != null);
    firePropertyChange("maximumSize", object, paramDimension);
  }
  
  public boolean isMaximumSizeSet() { return this.maxSizeSet; }
  
  public Dimension getMaximumSize() { return isMaximumSizeSet() ? new Dimension(this.maxSize) : new Dimension(32767, 32767); }
  
  public float getAlignmentX() { return 0.5F; }
  
  public float getAlignmentY() { return 0.5F; }
  
  public int getBaseline(int paramInt1, int paramInt2) {
    if (paramInt1 < 0 || paramInt2 < 0)
      throw new IllegalArgumentException("Width and height must be >= 0"); 
    return -1;
  }
  
  public BaselineResizeBehavior getBaselineResizeBehavior() { return BaselineResizeBehavior.OTHER; }
  
  public void doLayout() { layout(); }
  
  @Deprecated
  public void layout() {}
  
  public void validate() {
    synchronized (getTreeLock()) {
      ComponentPeer componentPeer = this.peer;
      boolean bool = isValid();
      if (!bool && componentPeer != null) {
        Font font1 = getFont();
        Font font2 = this.peerFont;
        if (font1 != font2 && (font2 == null || !font2.equals(font1))) {
          componentPeer.setFont(font1);
          this.peerFont = font1;
        } 
        componentPeer.layout();
      } 
      this.valid = true;
      if (!bool)
        mixOnValidating(); 
    } 
  }
  
  public void invalidate() {
    synchronized (getTreeLock()) {
      this.valid = false;
      if (!isPreferredSizeSet())
        this.prefSize = null; 
      if (!isMinimumSizeSet())
        this.minSize = null; 
      if (!isMaximumSizeSet())
        this.maxSize = null; 
      invalidateParent();
    } 
  }
  
  void invalidateParent() {
    if (this.parent != null)
      this.parent.invalidateIfValid(); 
  }
  
  final void invalidateIfValid() {
    if (isValid())
      invalidate(); 
  }
  
  public void revalidate() { revalidateSynchronously(); }
  
  final void revalidateSynchronously() {
    synchronized (getTreeLock()) {
      invalidate();
      Container container = getContainer();
      if (container == null) {
        validate();
      } else {
        while (!container.isValidateRoot() && container.getContainer() != null)
          container = container.getContainer(); 
        container.validate();
      } 
    } 
  }
  
  public Graphics getGraphics() {
    if (this.peer instanceof java.awt.peer.LightweightPeer) {
      if (this.parent == null)
        return null; 
      Graphics graphics = this.parent.getGraphics();
      if (graphics == null)
        return null; 
      if (graphics instanceof ConstrainableGraphics) {
        ((ConstrainableGraphics)graphics).constrain(this.x, this.y, this.width, this.height);
      } else {
        graphics.translate(this.x, this.y);
        graphics.setClip(0, 0, this.width, this.height);
      } 
      graphics.setFont(getFont());
      return graphics;
    } 
    ComponentPeer componentPeer = this.peer;
    return (componentPeer != null) ? componentPeer.getGraphics() : null;
  }
  
  final Graphics getGraphics_NoClientCode() {
    ComponentPeer componentPeer = this.peer;
    if (componentPeer instanceof java.awt.peer.LightweightPeer) {
      Container container = this.parent;
      if (container == null)
        return null; 
      Graphics graphics = container.getGraphics_NoClientCode();
      if (graphics == null)
        return null; 
      if (graphics instanceof ConstrainableGraphics) {
        ((ConstrainableGraphics)graphics).constrain(this.x, this.y, this.width, this.height);
      } else {
        graphics.translate(this.x, this.y);
        graphics.setClip(0, 0, this.width, this.height);
      } 
      graphics.setFont(getFont_NoClientCode());
      return graphics;
    } 
    return (componentPeer != null) ? componentPeer.getGraphics() : null;
  }
  
  public FontMetrics getFontMetrics(Font paramFont) {
    FontManager fontManager = FontManagerFactory.getInstance();
    return (fontManager instanceof SunFontManager && ((SunFontManager)fontManager).usePlatformFontMetrics() && this.peer != null && !(this.peer instanceof java.awt.peer.LightweightPeer)) ? this.peer.getFontMetrics(paramFont) : FontDesignMetrics.getMetrics(paramFont);
  }
  
  public void setCursor(Cursor paramCursor) {
    this.cursor = paramCursor;
    updateCursorImmediately();
  }
  
  final void updateCursorImmediately() {
    if (this.peer instanceof java.awt.peer.LightweightPeer) {
      Container container = getNativeContainer();
      if (container == null)
        return; 
      ComponentPeer componentPeer = container.getPeer();
      if (componentPeer != null)
        componentPeer.updateCursorImmediately(); 
    } else if (this.peer != null) {
      this.peer.updateCursorImmediately();
    } 
  }
  
  public Cursor getCursor() { return getCursor_NoClientCode(); }
  
  final Cursor getCursor_NoClientCode() {
    Cursor cursor1 = this.cursor;
    if (cursor1 != null)
      return cursor1; 
    Container container = this.parent;
    return (container != null) ? container.getCursor_NoClientCode() : Cursor.getPredefinedCursor(0);
  }
  
  public boolean isCursorSet() { return (this.cursor != null); }
  
  public void paint(Graphics paramGraphics) {}
  
  public void update(Graphics paramGraphics) { paint(paramGraphics); }
  
  public void paintAll(Graphics paramGraphics) {
    if (isShowing())
      GraphicsCallback.PeerPaintCallback.getInstance().runOneComponent(this, new Rectangle(0, 0, this.width, this.height), paramGraphics, paramGraphics.getClip(), 3); 
  }
  
  void lightweightPaint(Graphics paramGraphics) { paint(paramGraphics); }
  
  void paintHeavyweightComponents(Graphics paramGraphics) {}
  
  public void repaint() { repaint(0L, 0, 0, this.width, this.height); }
  
  public void repaint(long paramLong) { repaint(paramLong, 0, 0, this.width, this.height); }
  
  public void repaint(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { repaint(0L, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public void repaint(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (this.peer instanceof java.awt.peer.LightweightPeer) {
      if (this.parent != null) {
        if (paramInt1 < 0) {
          paramInt3 += paramInt1;
          paramInt1 = 0;
        } 
        if (paramInt2 < 0) {
          paramInt4 += paramInt2;
          paramInt2 = 0;
        } 
        int i = (paramInt3 > this.width) ? this.width : paramInt3;
        int j = (paramInt4 > this.height) ? this.height : paramInt4;
        if (i <= 0 || j <= 0)
          return; 
        int k = this.x + paramInt1;
        int m = this.y + paramInt2;
        this.parent.repaint(paramLong, k, m, i, j);
      } 
    } else if (isVisible() && this.peer != null && paramInt3 > 0 && paramInt4 > 0) {
      PaintEvent paintEvent = new PaintEvent(this, 801, new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4));
      SunToolkit.postEvent(SunToolkit.targetToAppContext(this), paintEvent);
    } 
  }
  
  public void print(Graphics paramGraphics) { paint(paramGraphics); }
  
  public void printAll(Graphics paramGraphics) {
    if (isShowing())
      GraphicsCallback.PeerPrintCallback.getInstance().runOneComponent(this, new Rectangle(0, 0, this.width, this.height), paramGraphics, paramGraphics.getClip(), 3); 
  }
  
  void lightweightPrint(Graphics paramGraphics) { print(paramGraphics); }
  
  void printHeavyweightComponents(Graphics paramGraphics) {}
  
  private Insets getInsets_NoClientCode() {
    ComponentPeer componentPeer = this.peer;
    return (componentPeer instanceof ContainerPeer) ? (Insets)((ContainerPeer)componentPeer).getInsets().clone() : new Insets(0, 0, 0, 0);
  }
  
  public boolean imageUpdate(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    int i = -1;
    if ((paramInt1 & 0x30) != 0) {
      i = 0;
    } else if ((paramInt1 & 0x8) != 0 && isInc) {
      i = incRate;
      if (i < 0)
        i = 0; 
    } 
    if (i >= 0)
      repaint(i, 0, 0, this.width, this.height); 
    return ((paramInt1 & 0xA0) == 0);
  }
  
  public Image createImage(ImageProducer paramImageProducer) {
    ComponentPeer componentPeer = this.peer;
    return (componentPeer != null && !(componentPeer instanceof java.awt.peer.LightweightPeer)) ? componentPeer.createImage(paramImageProducer) : getToolkit().createImage(paramImageProducer);
  }
  
  public Image createImage(int paramInt1, int paramInt2) {
    ComponentPeer componentPeer = this.peer;
    return (componentPeer instanceof java.awt.peer.LightweightPeer) ? ((this.parent != null) ? this.parent.createImage(paramInt1, paramInt2) : null) : ((componentPeer != null) ? componentPeer.createImage(paramInt1, paramInt2) : null);
  }
  
  public VolatileImage createVolatileImage(int paramInt1, int paramInt2) {
    ComponentPeer componentPeer = this.peer;
    return (componentPeer instanceof java.awt.peer.LightweightPeer) ? ((this.parent != null) ? this.parent.createVolatileImage(paramInt1, paramInt2) : null) : ((componentPeer != null) ? componentPeer.createVolatileImage(paramInt1, paramInt2) : null);
  }
  
  public VolatileImage createVolatileImage(int paramInt1, int paramInt2, ImageCapabilities paramImageCapabilities) throws AWTException { return createVolatileImage(paramInt1, paramInt2); }
  
  public boolean prepareImage(Image paramImage, ImageObserver paramImageObserver) { return prepareImage(paramImage, -1, -1, paramImageObserver); }
  
  public boolean prepareImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver) {
    ComponentPeer componentPeer = this.peer;
    return (componentPeer instanceof java.awt.peer.LightweightPeer) ? ((this.parent != null) ? this.parent.prepareImage(paramImage, paramInt1, paramInt2, paramImageObserver) : getToolkit().prepareImage(paramImage, paramInt1, paramInt2, paramImageObserver)) : ((componentPeer != null) ? componentPeer.prepareImage(paramImage, paramInt1, paramInt2, paramImageObserver) : getToolkit().prepareImage(paramImage, paramInt1, paramInt2, paramImageObserver));
  }
  
  public int checkImage(Image paramImage, ImageObserver paramImageObserver) { return checkImage(paramImage, -1, -1, paramImageObserver); }
  
  public int checkImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver) {
    ComponentPeer componentPeer = this.peer;
    return (componentPeer instanceof java.awt.peer.LightweightPeer) ? ((this.parent != null) ? this.parent.checkImage(paramImage, paramInt1, paramInt2, paramImageObserver) : getToolkit().checkImage(paramImage, paramInt1, paramInt2, paramImageObserver)) : ((componentPeer != null) ? componentPeer.checkImage(paramImage, paramInt1, paramInt2, paramImageObserver) : getToolkit().checkImage(paramImage, paramInt1, paramInt2, paramImageObserver));
  }
  
  void createBufferStrategy(int paramInt) {
    if (paramInt > 1) {
      BufferCapabilities bufferCapabilities1 = new BufferCapabilities(new ImageCapabilities(true), new ImageCapabilities(true), BufferCapabilities.FlipContents.UNDEFINED);
      try {
        createBufferStrategy(paramInt, bufferCapabilities1);
        return;
      } catch (AWTException aWTException) {}
    } 
    BufferCapabilities bufferCapabilities = new BufferCapabilities(new ImageCapabilities(true), new ImageCapabilities(true), null);
    try {
      createBufferStrategy(paramInt, bufferCapabilities);
      return;
    } catch (AWTException aWTException) {
      bufferCapabilities = new BufferCapabilities(new ImageCapabilities(false), new ImageCapabilities(false), null);
      try {
        createBufferStrategy(paramInt, bufferCapabilities);
        return;
      } catch (AWTException aWTException) {
        throw new InternalError("Could not create a buffer strategy", aWTException);
      } 
    } 
  }
  
  void createBufferStrategy(int paramInt, BufferCapabilities paramBufferCapabilities) throws AWTException {
    if (paramInt < 1)
      throw new IllegalArgumentException("Number of buffers must be at least 1"); 
    if (paramBufferCapabilities == null)
      throw new IllegalArgumentException("No capabilities specified"); 
    if (this.bufferStrategy != null)
      this.bufferStrategy.dispose(); 
    if (paramInt == 1) {
      this.bufferStrategy = new SingleBufferStrategy(paramBufferCapabilities);
    } else {
      SunGraphicsEnvironment sunGraphicsEnvironment = (SunGraphicsEnvironment)GraphicsEnvironment.getLocalGraphicsEnvironment();
      if (!paramBufferCapabilities.isPageFlipping() && sunGraphicsEnvironment.isFlipStrategyPreferred(this.peer))
        paramBufferCapabilities = new ProxyCapabilities(paramBufferCapabilities, null); 
      if (paramBufferCapabilities.isPageFlipping()) {
        this.bufferStrategy = new FlipSubRegionBufferStrategy(paramInt, paramBufferCapabilities);
      } else {
        this.bufferStrategy = new BltSubRegionBufferStrategy(paramInt, paramBufferCapabilities);
      } 
    } 
  }
  
  BufferStrategy getBufferStrategy() { return this.bufferStrategy; }
  
  Image getBackBuffer() {
    if (this.bufferStrategy != null) {
      if (this.bufferStrategy instanceof BltBufferStrategy) {
        BltBufferStrategy bltBufferStrategy = (BltBufferStrategy)this.bufferStrategy;
        return bltBufferStrategy.getBackBuffer();
      } 
      if (this.bufferStrategy instanceof FlipBufferStrategy) {
        FlipBufferStrategy flipBufferStrategy = (FlipBufferStrategy)this.bufferStrategy;
        return flipBufferStrategy.getBackBuffer();
      } 
    } 
    return null;
  }
  
  public void setIgnoreRepaint(boolean paramBoolean) { this.ignoreRepaint = paramBoolean; }
  
  public boolean getIgnoreRepaint() { return this.ignoreRepaint; }
  
  public boolean contains(int paramInt1, int paramInt2) { return inside(paramInt1, paramInt2); }
  
  @Deprecated
  public boolean inside(int paramInt1, int paramInt2) { return (paramInt1 >= 0 && paramInt1 < this.width && paramInt2 >= 0 && paramInt2 < this.height); }
  
  public boolean contains(Point paramPoint) { return contains(paramPoint.x, paramPoint.y); }
  
  public Component getComponentAt(int paramInt1, int paramInt2) { return locate(paramInt1, paramInt2); }
  
  @Deprecated
  public Component locate(int paramInt1, int paramInt2) { return contains(paramInt1, paramInt2) ? this : null; }
  
  public Component getComponentAt(Point paramPoint) { return getComponentAt(paramPoint.x, paramPoint.y); }
  
  @Deprecated
  public void deliverEvent(Event paramEvent) { postEvent(paramEvent); }
  
  public final void dispatchEvent(AWTEvent paramAWTEvent) { dispatchEventImpl(paramAWTEvent); }
  
  void dispatchEventImpl(AWTEvent paramAWTEvent) {
    Container container;
    int i = paramAWTEvent.getID();
    AppContext appContext1;
    if (appContext1 != null && !appContext1.equals((appContext1 = this.appContext).getAppContext()) && eventLog.isLoggable(PlatformLogger.Level.FINE))
      eventLog.fine("Event " + paramAWTEvent + " is being dispatched on the wrong AppContext"); 
    if (eventLog.isLoggable(PlatformLogger.Level.FINEST))
      eventLog.finest("{0}", new Object[] { paramAWTEvent }); 
    if (!(paramAWTEvent instanceof KeyEvent))
      EventQueue.setCurrentEventAndMostRecentTime(paramAWTEvent); 
    if (paramAWTEvent instanceof SunDropTargetEvent) {
      ((SunDropTargetEvent)paramAWTEvent).dispatch();
      return;
    } 
    if (!paramAWTEvent.focusManagerIsDispatching) {
      if (paramAWTEvent.isPosted) {
        paramAWTEvent = KeyboardFocusManager.retargetFocusEvent(paramAWTEvent);
        paramAWTEvent.isPosted = true;
      } 
      if (KeyboardFocusManager.getCurrentKeyboardFocusManager().dispatchEvent(paramAWTEvent))
        return; 
    } 
    if (paramAWTEvent instanceof FocusEvent && focusLog.isLoggable(PlatformLogger.Level.FINEST))
      focusLog.finest("" + paramAWTEvent); 
    if (i == 507 && !eventTypeEnabled(i) && this.peer != null && !this.peer.handlesWheelScrolling() && dispatchMouseWheelToAncestor((MouseWheelEvent)paramAWTEvent))
      return; 
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    toolkit.notifyAWTEventListeners(paramAWTEvent);
    if (!paramAWTEvent.isConsumed() && paramAWTEvent instanceof KeyEvent) {
      KeyboardFocusManager.getCurrentKeyboardFocusManager().processKeyEvent(this, (KeyEvent)paramAWTEvent);
      if (paramAWTEvent.isConsumed())
        return; 
    } 
    if (areInputMethodsEnabled()) {
      if ((paramAWTEvent instanceof InputMethodEvent && !(this instanceof sun.awt.im.CompositionArea)) || paramAWTEvent instanceof java.awt.event.InputEvent || paramAWTEvent instanceof FocusEvent) {
        InputContext inputContext = getInputContext();
        if (inputContext != null) {
          inputContext.dispatchEvent(paramAWTEvent);
          if (paramAWTEvent.isConsumed()) {
            if (paramAWTEvent instanceof FocusEvent && focusLog.isLoggable(PlatformLogger.Level.FINEST))
              focusLog.finest("3579: Skipping " + paramAWTEvent); 
            return;
          } 
        } 
      } 
    } else if (i == 1004) {
      InputContext inputContext = getInputContext();
      if (inputContext != null && inputContext instanceof InputContext)
        ((InputContext)inputContext).disableNativeIM(); 
    } 
    switch (i) {
      case 401:
      case 402:
        container = (Container)((this instanceof Container) ? this : this.parent);
        if (container != null) {
          container.preProcessKeyEvent((KeyEvent)paramAWTEvent);
          if (paramAWTEvent.isConsumed()) {
            if (focusLog.isLoggable(PlatformLogger.Level.FINEST))
              focusLog.finest("Pre-process consumed event"); 
            return;
          } 
        } 
        break;
      case 201:
        if (toolkit instanceof WindowClosingListener) {
          this.windowClosingException = ((WindowClosingListener)toolkit).windowClosingNotify((WindowEvent)paramAWTEvent);
          if (checkWindowClosingException())
            return; 
        } 
        break;
    } 
    if (this.newEventsOnly) {
      if (eventEnabled(paramAWTEvent))
        processEvent(paramAWTEvent); 
    } else if (i == 507) {
      autoProcessMouseWheel((MouseWheelEvent)paramAWTEvent);
    } else if (!(paramAWTEvent instanceof MouseEvent) || postsOldMouseEvents()) {
      Event event = paramAWTEvent.convertToOld();
      if (event != null) {
        int j = event.key;
        int k = event.modifiers;
        postEvent(event);
        if (event.isConsumed())
          paramAWTEvent.consume(); 
        switch (event.id) {
          case 401:
          case 402:
          case 403:
          case 404:
            if (event.key != j)
              ((KeyEvent)paramAWTEvent).setKeyChar(event.getKeyEventChar()); 
            if (event.modifiers != k)
              ((KeyEvent)paramAWTEvent).setModifiers(event.modifiers); 
            break;
        } 
      } 
    } 
    if (i == 201 && !paramAWTEvent.isConsumed() && toolkit instanceof WindowClosingListener) {
      this.windowClosingException = ((WindowClosingListener)toolkit).windowClosingDelivered((WindowEvent)paramAWTEvent);
      if (checkWindowClosingException())
        return; 
    } 
    if (!(paramAWTEvent instanceof KeyEvent)) {
      ComponentPeer componentPeer = this.peer;
      if (paramAWTEvent instanceof FocusEvent && (componentPeer == null || componentPeer instanceof java.awt.peer.LightweightPeer)) {
        Component component = (Component)paramAWTEvent.getSource();
        if (component != null) {
          Container container1 = component.getNativeContainer();
          if (container1 != null)
            componentPeer = container1.getPeer(); 
        } 
      } 
      if (componentPeer != null)
        componentPeer.handleEvent(paramAWTEvent); 
    } 
    if (SunToolkit.isTouchKeyboardAutoShowEnabled() && toolkit instanceof SunToolkit && (paramAWTEvent instanceof MouseEvent || paramAWTEvent instanceof FocusEvent))
      ((SunToolkit)toolkit).showOrHideTouchKeyboard(this, paramAWTEvent); 
  }
  
  void autoProcessMouseWheel(MouseWheelEvent paramMouseWheelEvent) {}
  
  boolean dispatchMouseWheelToAncestor(MouseWheelEvent paramMouseWheelEvent) {
    int i = paramMouseWheelEvent.getX() + getX();
    int j = paramMouseWheelEvent.getY() + getY();
    if (eventLog.isLoggable(PlatformLogger.Level.FINEST)) {
      eventLog.finest("dispatchMouseWheelToAncestor");
      eventLog.finest("orig event src is of " + paramMouseWheelEvent.getSource().getClass());
    } 
    synchronized (getTreeLock()) {
      Container container = getParent();
      while (container != null && !container.eventEnabled(paramMouseWheelEvent)) {
        i += container.getX();
        j += container.getY();
        if (!(container instanceof Window))
          container = container.getParent(); 
      } 
      if (eventLog.isLoggable(PlatformLogger.Level.FINEST))
        eventLog.finest("new event src is " + container.getClass()); 
      if (container != null && container.eventEnabled(paramMouseWheelEvent)) {
        MouseWheelEvent mouseWheelEvent = new MouseWheelEvent(container, paramMouseWheelEvent.getID(), paramMouseWheelEvent.getWhen(), paramMouseWheelEvent.getModifiers(), i, j, paramMouseWheelEvent.getXOnScreen(), paramMouseWheelEvent.getYOnScreen(), paramMouseWheelEvent.getClickCount(), paramMouseWheelEvent.isPopupTrigger(), paramMouseWheelEvent.getScrollType(), paramMouseWheelEvent.getScrollAmount(), paramMouseWheelEvent.getWheelRotation(), paramMouseWheelEvent.getPreciseWheelRotation());
        paramMouseWheelEvent.copyPrivateDataInto(mouseWheelEvent);
        container.dispatchEventToSelf(mouseWheelEvent);
        if (mouseWheelEvent.isConsumed())
          paramMouseWheelEvent.consume(); 
        return true;
      } 
    } 
    return false;
  }
  
  boolean checkWindowClosingException() {
    if (this.windowClosingException != null) {
      if (this instanceof Dialog) {
        ((Dialog)this).interruptBlocking();
      } else {
        this.windowClosingException.fillInStackTrace();
        this.windowClosingException.printStackTrace();
        this.windowClosingException = null;
      } 
      return true;
    } 
    return false;
  }
  
  boolean areInputMethodsEnabled() { return ((this.eventMask & 0x1000L) != 0L && ((this.eventMask & 0x8L) != 0L || this.keyListener != null)); }
  
  boolean eventEnabled(AWTEvent paramAWTEvent) { return eventTypeEnabled(paramAWTEvent.id); }
  
  boolean eventTypeEnabled(int paramInt) {
    switch (paramInt) {
      case 100:
      case 101:
      case 102:
      case 103:
        if ((this.eventMask & 0x1L) != 0L || this.componentListener != null)
          return true; 
        break;
      case 1004:
      case 1005:
        if ((this.eventMask & 0x4L) != 0L || this.focusListener != null)
          return true; 
        break;
      case 400:
      case 401:
      case 402:
        if ((this.eventMask & 0x8L) != 0L || this.keyListener != null)
          return true; 
        break;
      case 500:
      case 501:
      case 502:
      case 504:
      case 505:
        if ((this.eventMask & 0x10L) != 0L || this.mouseListener != null)
          return true; 
        break;
      case 503:
      case 506:
        if ((this.eventMask & 0x20L) != 0L || this.mouseMotionListener != null)
          return true; 
        break;
      case 507:
        if ((this.eventMask & 0x20000L) != 0L || this.mouseWheelListener != null)
          return true; 
        break;
      case 1100:
      case 1101:
        if ((this.eventMask & 0x800L) != 0L || this.inputMethodListener != null)
          return true; 
        break;
      case 1400:
        if ((this.eventMask & 0x8000L) != 0L || this.hierarchyListener != null)
          return true; 
        break;
      case 1401:
      case 1402:
        if ((this.eventMask & 0x10000L) != 0L || this.hierarchyBoundsListener != null)
          return true; 
        break;
      case 1001:
        if ((this.eventMask & 0x80L) != 0L)
          return true; 
        break;
      case 900:
        if ((this.eventMask & 0x400L) != 0L)
          return true; 
        break;
      case 701:
        if ((this.eventMask & 0x200L) != 0L)
          return true; 
        break;
      case 601:
        if ((this.eventMask & 0x100L) != 0L)
          return true; 
        break;
    } 
    return (paramInt > 1999);
  }
  
  @Deprecated
  public boolean postEvent(Event paramEvent) {
    ComponentPeer componentPeer = this.peer;
    if (handleEvent(paramEvent)) {
      paramEvent.consume();
      return true;
    } 
    Container container = this.parent;
    int i = paramEvent.x;
    int j = paramEvent.y;
    if (container != null) {
      paramEvent.translate(this.x, this.y);
      if (container.postEvent(paramEvent)) {
        paramEvent.consume();
        return true;
      } 
      paramEvent.x = i;
      paramEvent.y = j;
    } 
    return false;
  }
  
  public void addComponentListener(ComponentListener paramComponentListener) {
    if (paramComponentListener == null)
      return; 
    this.componentListener = AWTEventMulticaster.add(this.componentListener, paramComponentListener);
    this.newEventsOnly = true;
  }
  
  public void removeComponentListener(ComponentListener paramComponentListener) {
    if (paramComponentListener == null)
      return; 
    this.componentListener = AWTEventMulticaster.remove(this.componentListener, paramComponentListener);
  }
  
  public ComponentListener[] getComponentListeners() { return (ComponentListener[])getListeners(ComponentListener.class); }
  
  public void addFocusListener(FocusListener paramFocusListener) {
    if (paramFocusListener == null)
      return; 
    this.focusListener = AWTEventMulticaster.add(this.focusListener, paramFocusListener);
    this.newEventsOnly = true;
    if (this.peer instanceof java.awt.peer.LightweightPeer)
      this.parent.proxyEnableEvents(4L); 
  }
  
  public void removeFocusListener(FocusListener paramFocusListener) {
    if (paramFocusListener == null)
      return; 
    this.focusListener = AWTEventMulticaster.remove(this.focusListener, paramFocusListener);
  }
  
  public FocusListener[] getFocusListeners() { return (FocusListener[])getListeners(FocusListener.class); }
  
  public void addHierarchyListener(HierarchyListener paramHierarchyListener) {
    boolean bool;
    if (paramHierarchyListener == null)
      return; 
    synchronized (this) {
      bool = (this.hierarchyListener == null && (this.eventMask & 0x8000L) == 0L) ? 1 : 0;
      this.hierarchyListener = AWTEventMulticaster.add(this.hierarchyListener, paramHierarchyListener);
      bool = (bool && this.hierarchyListener != null) ? 1 : 0;
      this.newEventsOnly = true;
    } 
    if (bool)
      synchronized (getTreeLock()) {
        adjustListeningChildrenOnParent(32768L, 1);
      }  
  }
  
  public void removeHierarchyListener(HierarchyListener paramHierarchyListener) {
    boolean bool;
    if (paramHierarchyListener == null)
      return; 
    synchronized (this) {
      bool = (this.hierarchyListener != null && (this.eventMask & 0x8000L) == 0L) ? 1 : 0;
      this.hierarchyListener = AWTEventMulticaster.remove(this.hierarchyListener, paramHierarchyListener);
      bool = (bool && this.hierarchyListener == null) ? 1 : 0;
    } 
    if (bool)
      synchronized (getTreeLock()) {
        adjustListeningChildrenOnParent(32768L, -1);
      }  
  }
  
  public HierarchyListener[] getHierarchyListeners() { return (HierarchyListener[])getListeners(HierarchyListener.class); }
  
  public void addHierarchyBoundsListener(HierarchyBoundsListener paramHierarchyBoundsListener) {
    boolean bool;
    if (paramHierarchyBoundsListener == null)
      return; 
    synchronized (this) {
      bool = (this.hierarchyBoundsListener == null && (this.eventMask & 0x10000L) == 0L) ? 1 : 0;
      this.hierarchyBoundsListener = AWTEventMulticaster.add(this.hierarchyBoundsListener, paramHierarchyBoundsListener);
      bool = (bool && this.hierarchyBoundsListener != null) ? 1 : 0;
      this.newEventsOnly = true;
    } 
    if (bool)
      synchronized (getTreeLock()) {
        adjustListeningChildrenOnParent(65536L, 1);
      }  
  }
  
  public void removeHierarchyBoundsListener(HierarchyBoundsListener paramHierarchyBoundsListener) {
    boolean bool;
    if (paramHierarchyBoundsListener == null)
      return; 
    synchronized (this) {
      bool = (this.hierarchyBoundsListener != null && (this.eventMask & 0x10000L) == 0L) ? 1 : 0;
      this.hierarchyBoundsListener = AWTEventMulticaster.remove(this.hierarchyBoundsListener, paramHierarchyBoundsListener);
      bool = (bool && this.hierarchyBoundsListener == null) ? 1 : 0;
    } 
    if (bool)
      synchronized (getTreeLock()) {
        adjustListeningChildrenOnParent(65536L, -1);
      }  
  }
  
  int numListening(long paramLong) {
    if (eventLog.isLoggable(PlatformLogger.Level.FINE) && paramLong != 32768L && paramLong != 65536L)
      eventLog.fine("Assertion failed"); 
    return ((paramLong == 32768L && (this.hierarchyListener != null || (this.eventMask & 0x8000L) != 0L)) || (paramLong == 65536L && (this.hierarchyBoundsListener != null || (this.eventMask & 0x10000L) != 0L))) ? 1 : 0;
  }
  
  int countHierarchyMembers() { return 1; }
  
  int createHierarchyEvents(int paramInt, Component paramComponent, Container paramContainer, long paramLong, boolean paramBoolean) {
    switch (paramInt) {
      case 1400:
        if (this.hierarchyListener != null || (this.eventMask & 0x8000L) != 0L || paramBoolean) {
          HierarchyEvent hierarchyEvent = new HierarchyEvent(this, paramInt, paramComponent, paramContainer, paramLong);
          dispatchEvent(hierarchyEvent);
          return 1;
        } 
        return 0;
      case 1401:
      case 1402:
        if (eventLog.isLoggable(PlatformLogger.Level.FINE) && paramLong != 0L)
          eventLog.fine("Assertion (changeFlags == 0) failed"); 
        if (this.hierarchyBoundsListener != null || (this.eventMask & 0x10000L) != 0L || paramBoolean) {
          HierarchyEvent hierarchyEvent = new HierarchyEvent(this, paramInt, paramComponent, paramContainer);
          dispatchEvent(hierarchyEvent);
          return 1;
        } 
        return 0;
    } 
    if (eventLog.isLoggable(PlatformLogger.Level.FINE))
      eventLog.fine("This code must never be reached"); 
    return 0;
  }
  
  public HierarchyBoundsListener[] getHierarchyBoundsListeners() { return (HierarchyBoundsListener[])getListeners(HierarchyBoundsListener.class); }
  
  void adjustListeningChildrenOnParent(long paramLong, int paramInt) {
    if (this.parent != null)
      this.parent.adjustListeningChildren(paramLong, paramInt); 
  }
  
  public void addKeyListener(KeyListener paramKeyListener) {
    if (paramKeyListener == null)
      return; 
    this.keyListener = AWTEventMulticaster.add(this.keyListener, paramKeyListener);
    this.newEventsOnly = true;
    if (this.peer instanceof java.awt.peer.LightweightPeer)
      this.parent.proxyEnableEvents(8L); 
  }
  
  public void removeKeyListener(KeyListener paramKeyListener) {
    if (paramKeyListener == null)
      return; 
    this.keyListener = AWTEventMulticaster.remove(this.keyListener, paramKeyListener);
  }
  
  public KeyListener[] getKeyListeners() { return (KeyListener[])getListeners(KeyListener.class); }
  
  public void addMouseListener(MouseListener paramMouseListener) {
    if (paramMouseListener == null)
      return; 
    this.mouseListener = AWTEventMulticaster.add(this.mouseListener, paramMouseListener);
    this.newEventsOnly = true;
    if (this.peer instanceof java.awt.peer.LightweightPeer)
      this.parent.proxyEnableEvents(16L); 
  }
  
  public void removeMouseListener(MouseListener paramMouseListener) {
    if (paramMouseListener == null)
      return; 
    this.mouseListener = AWTEventMulticaster.remove(this.mouseListener, paramMouseListener);
  }
  
  public MouseListener[] getMouseListeners() { return (MouseListener[])getListeners(MouseListener.class); }
  
  public void addMouseMotionListener(MouseMotionListener paramMouseMotionListener) {
    if (paramMouseMotionListener == null)
      return; 
    this.mouseMotionListener = AWTEventMulticaster.add(this.mouseMotionListener, paramMouseMotionListener);
    this.newEventsOnly = true;
    if (this.peer instanceof java.awt.peer.LightweightPeer)
      this.parent.proxyEnableEvents(32L); 
  }
  
  public void removeMouseMotionListener(MouseMotionListener paramMouseMotionListener) {
    if (paramMouseMotionListener == null)
      return; 
    this.mouseMotionListener = AWTEventMulticaster.remove(this.mouseMotionListener, paramMouseMotionListener);
  }
  
  public MouseMotionListener[] getMouseMotionListeners() { return (MouseMotionListener[])getListeners(MouseMotionListener.class); }
  
  public void addMouseWheelListener(MouseWheelListener paramMouseWheelListener) {
    if (paramMouseWheelListener == null)
      return; 
    this.mouseWheelListener = AWTEventMulticaster.add(this.mouseWheelListener, paramMouseWheelListener);
    this.newEventsOnly = true;
    if (this.peer instanceof java.awt.peer.LightweightPeer)
      this.parent.proxyEnableEvents(131072L); 
  }
  
  public void removeMouseWheelListener(MouseWheelListener paramMouseWheelListener) {
    if (paramMouseWheelListener == null)
      return; 
    this.mouseWheelListener = AWTEventMulticaster.remove(this.mouseWheelListener, paramMouseWheelListener);
  }
  
  public MouseWheelListener[] getMouseWheelListeners() { return (MouseWheelListener[])getListeners(MouseWheelListener.class); }
  
  public void addInputMethodListener(InputMethodListener paramInputMethodListener) {
    if (paramInputMethodListener == null)
      return; 
    this.inputMethodListener = AWTEventMulticaster.add(this.inputMethodListener, paramInputMethodListener);
    this.newEventsOnly = true;
  }
  
  public void removeInputMethodListener(InputMethodListener paramInputMethodListener) {
    if (paramInputMethodListener == null)
      return; 
    this.inputMethodListener = AWTEventMulticaster.remove(this.inputMethodListener, paramInputMethodListener);
  }
  
  public InputMethodListener[] getInputMethodListeners() { return (InputMethodListener[])getListeners(InputMethodListener.class); }
  
  public <T extends EventListener> T[] getListeners(Class<T> paramClass) {
    InputMethodListener inputMethodListener1 = null;
    if (paramClass == ComponentListener.class) {
      inputMethodListener1 = this.componentListener;
    } else if (paramClass == FocusListener.class) {
      FocusListener focusListener1 = this.focusListener;
    } else if (paramClass == HierarchyListener.class) {
      HierarchyListener hierarchyListener1 = this.hierarchyListener;
    } else if (paramClass == HierarchyBoundsListener.class) {
      HierarchyBoundsListener hierarchyBoundsListener1 = this.hierarchyBoundsListener;
    } else if (paramClass == KeyListener.class) {
      KeyListener keyListener1 = this.keyListener;
    } else if (paramClass == MouseListener.class) {
      MouseListener mouseListener1 = this.mouseListener;
    } else if (paramClass == MouseMotionListener.class) {
      MouseMotionListener mouseMotionListener1 = this.mouseMotionListener;
    } else if (paramClass == MouseWheelListener.class) {
      MouseWheelListener mouseWheelListener1 = this.mouseWheelListener;
    } else if (paramClass == InputMethodListener.class) {
      inputMethodListener1 = this.inputMethodListener;
    } else if (paramClass == PropertyChangeListener.class) {
      return (T[])(EventListener[])getPropertyChangeListeners();
    } 
    return (T[])AWTEventMulticaster.getListeners(inputMethodListener1, paramClass);
  }
  
  public InputMethodRequests getInputMethodRequests() { return null; }
  
  public InputContext getInputContext() {
    Container container = this.parent;
    return (container == null) ? null : container.getInputContext();
  }
  
  protected final void enableEvents(long paramLong) {
    long l = 0L;
    synchronized (this) {
      if ((paramLong & 0x8000L) != 0L && this.hierarchyListener == null && (this.eventMask & 0x8000L) == 0L)
        l |= 0x8000L; 
      if ((paramLong & 0x10000L) != 0L && this.hierarchyBoundsListener == null && (this.eventMask & 0x10000L) == 0L)
        l |= 0x10000L; 
      this.eventMask |= paramLong;
      this.newEventsOnly = true;
    } 
    if (this.peer instanceof java.awt.peer.LightweightPeer)
      this.parent.proxyEnableEvents(this.eventMask); 
    if (l != 0L)
      synchronized (getTreeLock()) {
        adjustListeningChildrenOnParent(l, 1);
      }  
  }
  
  protected final void disableEvents(long paramLong) {
    long l = 0L;
    synchronized (this) {
      if ((paramLong & 0x8000L) != 0L && this.hierarchyListener == null && (this.eventMask & 0x8000L) != 0L)
        l |= 0x8000L; 
      if ((paramLong & 0x10000L) != 0L && this.hierarchyBoundsListener == null && (this.eventMask & 0x10000L) != 0L)
        l |= 0x10000L; 
      this.eventMask &= (paramLong ^ 0xFFFFFFFFFFFFFFFFL);
    } 
    if (l != 0L)
      synchronized (getTreeLock()) {
        adjustListeningChildrenOnParent(l, -1);
      }  
  }
  
  private boolean checkCoalescing() {
    if (getClass().getClassLoader() == null)
      return false; 
    final Class clazz = getClass();
    synchronized (coalesceMap) {
      Boolean bool1 = (Boolean)coalesceMap.get(clazz);
      if (bool1 != null)
        return bool1.booleanValue(); 
      Boolean bool2 = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            public Boolean run() { return Boolean.valueOf(Component.isCoalesceEventsOverriden(clazz)); }
          });
      coalesceMap.put(clazz, bool2);
      return bool2.booleanValue();
    } 
  }
  
  private static boolean isCoalesceEventsOverriden(Class<?> paramClass) {
    assert Thread.holdsLock(coalesceMap);
    Class clazz = paramClass.getSuperclass();
    if (clazz == null)
      return false; 
    if (clazz.getClassLoader() != null) {
      Boolean bool = (Boolean)coalesceMap.get(clazz);
      if (bool == null) {
        if (isCoalesceEventsOverriden(clazz)) {
          coalesceMap.put(clazz, Boolean.valueOf(true));
          return true;
        } 
      } else if (bool.booleanValue()) {
        return true;
      } 
    } 
    try {
      paramClass.getDeclaredMethod("coalesceEvents", coalesceEventsParams);
      return true;
    } catch (NoSuchMethodException noSuchMethodException) {
      return false;
    } 
  }
  
  final boolean isCoalescingEnabled() { return this.coalescingEnabled; }
  
  protected AWTEvent coalesceEvents(AWTEvent paramAWTEvent1, AWTEvent paramAWTEvent2) { return null; }
  
  protected void processEvent(AWTEvent paramAWTEvent) {
    if (paramAWTEvent instanceof FocusEvent) {
      processFocusEvent((FocusEvent)paramAWTEvent);
    } else if (paramAWTEvent instanceof MouseEvent) {
      switch (paramAWTEvent.getID()) {
        case 500:
        case 501:
        case 502:
        case 504:
        case 505:
          processMouseEvent((MouseEvent)paramAWTEvent);
          break;
        case 503:
        case 506:
          processMouseMotionEvent((MouseEvent)paramAWTEvent);
          break;
        case 507:
          processMouseWheelEvent((MouseWheelEvent)paramAWTEvent);
          break;
      } 
    } else if (paramAWTEvent instanceof KeyEvent) {
      processKeyEvent((KeyEvent)paramAWTEvent);
    } else if (paramAWTEvent instanceof ComponentEvent) {
      processComponentEvent((ComponentEvent)paramAWTEvent);
    } else if (paramAWTEvent instanceof InputMethodEvent) {
      processInputMethodEvent((InputMethodEvent)paramAWTEvent);
    } else if (paramAWTEvent instanceof HierarchyEvent) {
      switch (paramAWTEvent.getID()) {
        case 1400:
          processHierarchyEvent((HierarchyEvent)paramAWTEvent);
          break;
        case 1401:
        case 1402:
          processHierarchyBoundsEvent((HierarchyEvent)paramAWTEvent);
          break;
      } 
    } 
  }
  
  protected void processComponentEvent(ComponentEvent paramComponentEvent) {
    ComponentListener componentListener1 = this.componentListener;
    if (componentListener1 != null) {
      int i = paramComponentEvent.getID();
      switch (i) {
        case 101:
          componentListener1.componentResized(paramComponentEvent);
          break;
        case 100:
          componentListener1.componentMoved(paramComponentEvent);
          break;
        case 102:
          componentListener1.componentShown(paramComponentEvent);
          break;
        case 103:
          componentListener1.componentHidden(paramComponentEvent);
          break;
      } 
    } 
  }
  
  protected void processFocusEvent(FocusEvent paramFocusEvent) {
    FocusListener focusListener1 = this.focusListener;
    if (focusListener1 != null) {
      int i = paramFocusEvent.getID();
      switch (i) {
        case 1004:
          focusListener1.focusGained(paramFocusEvent);
          break;
        case 1005:
          focusListener1.focusLost(paramFocusEvent);
          break;
      } 
    } 
  }
  
  protected void processKeyEvent(KeyEvent paramKeyEvent) {
    KeyListener keyListener1 = this.keyListener;
    if (keyListener1 != null) {
      int i = paramKeyEvent.getID();
      switch (i) {
        case 400:
          keyListener1.keyTyped(paramKeyEvent);
          break;
        case 401:
          keyListener1.keyPressed(paramKeyEvent);
          break;
        case 402:
          keyListener1.keyReleased(paramKeyEvent);
          break;
      } 
    } 
  }
  
  protected void processMouseEvent(MouseEvent paramMouseEvent) {
    MouseListener mouseListener1 = this.mouseListener;
    if (mouseListener1 != null) {
      int i = paramMouseEvent.getID();
      switch (i) {
        case 501:
          mouseListener1.mousePressed(paramMouseEvent);
          break;
        case 502:
          mouseListener1.mouseReleased(paramMouseEvent);
          break;
        case 500:
          mouseListener1.mouseClicked(paramMouseEvent);
          break;
        case 505:
          mouseListener1.mouseExited(paramMouseEvent);
          break;
        case 504:
          mouseListener1.mouseEntered(paramMouseEvent);
          break;
      } 
    } 
  }
  
  protected void processMouseMotionEvent(MouseEvent paramMouseEvent) {
    MouseMotionListener mouseMotionListener1 = this.mouseMotionListener;
    if (mouseMotionListener1 != null) {
      int i = paramMouseEvent.getID();
      switch (i) {
        case 503:
          mouseMotionListener1.mouseMoved(paramMouseEvent);
          break;
        case 506:
          mouseMotionListener1.mouseDragged(paramMouseEvent);
          break;
      } 
    } 
  }
  
  protected void processMouseWheelEvent(MouseWheelEvent paramMouseWheelEvent) {
    MouseWheelListener mouseWheelListener1 = this.mouseWheelListener;
    if (mouseWheelListener1 != null) {
      int i = paramMouseWheelEvent.getID();
      switch (i) {
        case 507:
          mouseWheelListener1.mouseWheelMoved(paramMouseWheelEvent);
          break;
      } 
    } 
  }
  
  boolean postsOldMouseEvents() { return false; }
  
  protected void processInputMethodEvent(InputMethodEvent paramInputMethodEvent) {
    InputMethodListener inputMethodListener1 = this.inputMethodListener;
    if (inputMethodListener1 != null) {
      int i = paramInputMethodEvent.getID();
      switch (i) {
        case 1100:
          inputMethodListener1.inputMethodTextChanged(paramInputMethodEvent);
          break;
        case 1101:
          inputMethodListener1.caretPositionChanged(paramInputMethodEvent);
          break;
      } 
    } 
  }
  
  protected void processHierarchyEvent(HierarchyEvent paramHierarchyEvent) {
    HierarchyListener hierarchyListener1 = this.hierarchyListener;
    if (hierarchyListener1 != null) {
      int i = paramHierarchyEvent.getID();
      switch (i) {
        case 1400:
          hierarchyListener1.hierarchyChanged(paramHierarchyEvent);
          break;
      } 
    } 
  }
  
  protected void processHierarchyBoundsEvent(HierarchyEvent paramHierarchyEvent) {
    HierarchyBoundsListener hierarchyBoundsListener1 = this.hierarchyBoundsListener;
    if (hierarchyBoundsListener1 != null) {
      int i = paramHierarchyEvent.getID();
      switch (i) {
        case 1401:
          hierarchyBoundsListener1.ancestorMoved(paramHierarchyEvent);
          break;
        case 1402:
          hierarchyBoundsListener1.ancestorResized(paramHierarchyEvent);
          break;
      } 
    } 
  }
  
  @Deprecated
  public boolean handleEvent(Event paramEvent) {
    switch (paramEvent.id) {
      case 504:
        return mouseEnter(paramEvent, paramEvent.x, paramEvent.y);
      case 505:
        return mouseExit(paramEvent, paramEvent.x, paramEvent.y);
      case 503:
        return mouseMove(paramEvent, paramEvent.x, paramEvent.y);
      case 501:
        return mouseDown(paramEvent, paramEvent.x, paramEvent.y);
      case 506:
        return mouseDrag(paramEvent, paramEvent.x, paramEvent.y);
      case 502:
        return mouseUp(paramEvent, paramEvent.x, paramEvent.y);
      case 401:
      case 403:
        return keyDown(paramEvent, paramEvent.key);
      case 402:
      case 404:
        return keyUp(paramEvent, paramEvent.key);
      case 1001:
        return action(paramEvent, paramEvent.arg);
      case 1004:
        return gotFocus(paramEvent, paramEvent.arg);
      case 1005:
        return lostFocus(paramEvent, paramEvent.arg);
    } 
    return false;
  }
  
  @Deprecated
  public boolean mouseDown(Event paramEvent, int paramInt1, int paramInt2) { return false; }
  
  @Deprecated
  public boolean mouseDrag(Event paramEvent, int paramInt1, int paramInt2) { return false; }
  
  @Deprecated
  public boolean mouseUp(Event paramEvent, int paramInt1, int paramInt2) { return false; }
  
  @Deprecated
  public boolean mouseMove(Event paramEvent, int paramInt1, int paramInt2) { return false; }
  
  @Deprecated
  public boolean mouseEnter(Event paramEvent, int paramInt1, int paramInt2) { return false; }
  
  @Deprecated
  public boolean mouseExit(Event paramEvent, int paramInt1, int paramInt2) { return false; }
  
  @Deprecated
  public boolean keyDown(Event paramEvent, int paramInt) { return false; }
  
  @Deprecated
  public boolean keyUp(Event paramEvent, int paramInt) { return false; }
  
  @Deprecated
  public boolean action(Event paramEvent, Object paramObject) { return false; }
  
  public void addNotify() {
    synchronized (getTreeLock()) {
      ComponentPeer componentPeer = this.peer;
      if (componentPeer == null || componentPeer instanceof java.awt.peer.LightweightPeer) {
        if (componentPeer == null)
          this.peer = componentPeer = getToolkit().createComponent(this); 
        if (this.parent != null) {
          long l = 0L;
          if (this.mouseListener != null || (this.eventMask & 0x10L) != 0L)
            l |= 0x10L; 
          if (this.mouseMotionListener != null || (this.eventMask & 0x20L) != 0L)
            l |= 0x20L; 
          if (this.mouseWheelListener != null || (this.eventMask & 0x20000L) != 0L)
            l |= 0x20000L; 
          if (this.focusListener != null || (this.eventMask & 0x4L) != 0L)
            l |= 0x4L; 
          if (this.keyListener != null || (this.eventMask & 0x8L) != 0L)
            l |= 0x8L; 
          if (l != 0L)
            this.parent.proxyEnableEvents(l); 
        } 
      } else {
        Container container = getContainer();
        if (container != null && container.isLightweight()) {
          relocateComponent();
          if (!container.isRecursivelyVisibleUpToHeavyweightContainer())
            componentPeer.setVisible(false); 
        } 
      } 
      invalidate();
      int i = (this.popups != null) ? this.popups.size() : 0;
      for (byte b = 0; b < i; b++) {
        PopupMenu popupMenu = (PopupMenu)this.popups.elementAt(b);
        popupMenu.addNotify();
      } 
      if (this.dropTarget != null)
        this.dropTarget.addNotify(componentPeer); 
      this.peerFont = getFont();
      if (getContainer() != null && !this.isAddNotifyComplete)
        getContainer().increaseComponentCount(this); 
      updateZOrder();
      if (!this.isAddNotifyComplete)
        mixOnShowing(); 
      this.isAddNotifyComplete = true;
      if (this.hierarchyListener != null || (this.eventMask & 0x8000L) != 0L || Toolkit.enabledOnToolkit(32768L)) {
        HierarchyEvent hierarchyEvent = new HierarchyEvent(this, 1400, this, this.parent, (0x2 | (isRecursivelyVisible() ? 4 : 0)));
        dispatchEvent(hierarchyEvent);
      } 
    } 
  }
  
  public void removeNotify() {
    KeyboardFocusManager.clearMostRecentFocusOwner(this);
    if (KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner() == this)
      KeyboardFocusManager.getCurrentKeyboardFocusManager().setGlobalPermanentFocusOwner(null); 
    synchronized (getTreeLock()) {
      if (isFocusOwner() && KeyboardFocusManager.isAutoFocusTransferEnabledFor(this))
        transferFocus(true); 
      if (getContainer() != null && this.isAddNotifyComplete)
        getContainer().decreaseComponentCount(this); 
      int i = (this.popups != null) ? this.popups.size() : 0;
      for (byte b = 0; b < i; b++) {
        PopupMenu popupMenu = (PopupMenu)this.popups.elementAt(b);
        popupMenu.removeNotify();
      } 
      if ((this.eventMask & 0x1000L) != 0L) {
        InputContext inputContext = getInputContext();
        if (inputContext != null)
          inputContext.removeNotify(this); 
      } 
      ComponentPeer componentPeer = this.peer;
      if (componentPeer != null) {
        boolean bool = isLightweight();
        if (this.bufferStrategy instanceof FlipBufferStrategy)
          ((FlipBufferStrategy)this.bufferStrategy).destroyBuffers(); 
        if (this.dropTarget != null)
          this.dropTarget.removeNotify(this.peer); 
        if (this.visible)
          componentPeer.setVisible(false); 
        this.peer = null;
        this.peerFont = null;
        Toolkit.getEventQueue().removeSourceEvents(this, false);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().discardKeyEvents(this);
        componentPeer.dispose();
        mixOnHiding(bool);
        this.isAddNotifyComplete = false;
        this.compoundShape = null;
      } 
      if (this.hierarchyListener != null || (this.eventMask & 0x8000L) != 0L || Toolkit.enabledOnToolkit(32768L)) {
        HierarchyEvent hierarchyEvent = new HierarchyEvent(this, 1400, this, this.parent, (0x2 | (isRecursivelyVisible() ? 4 : 0)));
        dispatchEvent(hierarchyEvent);
      } 
    } 
  }
  
  @Deprecated
  public boolean gotFocus(Event paramEvent, Object paramObject) { return false; }
  
  @Deprecated
  public boolean lostFocus(Event paramEvent, Object paramObject) { return false; }
  
  @Deprecated
  public boolean isFocusTraversable() {
    if (this.isFocusTraversableOverridden == 0)
      this.isFocusTraversableOverridden = 1; 
    return this.focusable;
  }
  
  public boolean isFocusable() { return isFocusTraversable(); }
  
  public void setFocusable(boolean paramBoolean) {
    boolean bool;
    synchronized (this) {
      bool = this.focusable;
      this.focusable = paramBoolean;
    } 
    this.isFocusTraversableOverridden = 2;
    firePropertyChange("focusable", bool, paramBoolean);
    if (bool && !paramBoolean) {
      if (isFocusOwner() && KeyboardFocusManager.isAutoFocusTransferEnabled())
        transferFocus(true); 
      KeyboardFocusManager.clearMostRecentFocusOwner(this);
    } 
  }
  
  final boolean isFocusTraversableOverridden() { return (this.isFocusTraversableOverridden != 1); }
  
  public void setFocusTraversalKeys(int paramInt, Set<? extends AWTKeyStroke> paramSet) {
    if (paramInt < 0 || paramInt >= 3)
      throw new IllegalArgumentException("invalid focus traversal key identifier"); 
    setFocusTraversalKeys_NoIDCheck(paramInt, paramSet);
  }
  
  public Set<AWTKeyStroke> getFocusTraversalKeys(int paramInt) {
    if (paramInt < 0 || paramInt >= 3)
      throw new IllegalArgumentException("invalid focus traversal key identifier"); 
    return getFocusTraversalKeys_NoIDCheck(paramInt);
  }
  
  final void setFocusTraversalKeys_NoIDCheck(int paramInt, Set<? extends AWTKeyStroke> paramSet) {
    Set set;
    synchronized (this) {
      if (this.focusTraversalKeys == null)
        initializeFocusTraversalKeys(); 
      if (paramSet != null)
        for (AWTKeyStroke aWTKeyStroke : paramSet) {
          if (aWTKeyStroke == null)
            throw new IllegalArgumentException("cannot set null focus traversal key"); 
          if (aWTKeyStroke.getKeyChar() != Character.MAX_VALUE)
            throw new IllegalArgumentException("focus traversal keys cannot map to KEY_TYPED events"); 
          for (byte b = 0; b < this.focusTraversalKeys.length; b++) {
            if (b != paramInt && getFocusTraversalKeys_NoIDCheck(b).contains(aWTKeyStroke))
              throw new IllegalArgumentException("focus traversal keys must be unique for a Component"); 
          } 
        }  
      set = this.focusTraversalKeys[paramInt];
      this.focusTraversalKeys[paramInt] = (paramSet != null) ? Collections.unmodifiableSet(new HashSet(paramSet)) : null;
    } 
    firePropertyChange(focusTraversalKeyPropertyNames[paramInt], set, paramSet);
  }
  
  final Set<AWTKeyStroke> getFocusTraversalKeys_NoIDCheck(int paramInt) {
    Set set = (this.focusTraversalKeys != null) ? this.focusTraversalKeys[paramInt] : null;
    if (set != null)
      return set; 
    Container container = this.parent;
    return (container != null) ? container.getFocusTraversalKeys(paramInt) : KeyboardFocusManager.getCurrentKeyboardFocusManager().getDefaultFocusTraversalKeys(paramInt);
  }
  
  public boolean areFocusTraversalKeysSet(int paramInt) {
    if (paramInt < 0 || paramInt >= 3)
      throw new IllegalArgumentException("invalid focus traversal key identifier"); 
    return (this.focusTraversalKeys != null && this.focusTraversalKeys[paramInt] != null);
  }
  
  public void setFocusTraversalKeysEnabled(boolean paramBoolean) {
    boolean bool;
    synchronized (this) {
      bool = this.focusTraversalKeysEnabled;
      this.focusTraversalKeysEnabled = paramBoolean;
    } 
    firePropertyChange("focusTraversalKeysEnabled", bool, paramBoolean);
  }
  
  public boolean getFocusTraversalKeysEnabled() { return this.focusTraversalKeysEnabled; }
  
  public void requestFocus() { requestFocusHelper(false, true); }
  
  boolean requestFocus(CausedFocusEvent.Cause paramCause) { return requestFocusHelper(false, true, paramCause); }
  
  protected boolean requestFocus(boolean paramBoolean) { return requestFocusHelper(paramBoolean, true); }
  
  boolean requestFocus(boolean paramBoolean, CausedFocusEvent.Cause paramCause) { return requestFocusHelper(paramBoolean, true, paramCause); }
  
  public boolean requestFocusInWindow() { return requestFocusHelper(false, false); }
  
  boolean requestFocusInWindow(CausedFocusEvent.Cause paramCause) { return requestFocusHelper(false, false, paramCause); }
  
  protected boolean requestFocusInWindow(boolean paramBoolean) { return requestFocusHelper(paramBoolean, false); }
  
  boolean requestFocusInWindow(boolean paramBoolean, CausedFocusEvent.Cause paramCause) { return requestFocusHelper(paramBoolean, false, paramCause); }
  
  final boolean requestFocusHelper(boolean paramBoolean1, boolean paramBoolean2) { return requestFocusHelper(paramBoolean1, paramBoolean2, CausedFocusEvent.Cause.UNKNOWN); }
  
  final boolean requestFocusHelper(boolean paramBoolean1, boolean paramBoolean2, CausedFocusEvent.Cause paramCause) {
    AWTEvent aWTEvent = EventQueue.getCurrentEvent();
    if (aWTEvent instanceof MouseEvent && SunToolkit.isSystemGenerated(aWTEvent)) {
      Component component1 = ((MouseEvent)aWTEvent).getComponent();
      if (component1 == null || component1.getContainingWindow() == getContainingWindow()) {
        focusLog.finest("requesting focus by mouse event \"in window\"");
        paramBoolean2 = false;
      } 
    } 
    if (!isRequestFocusAccepted(paramBoolean1, paramBoolean2, paramCause)) {
      if (focusLog.isLoggable(PlatformLogger.Level.FINEST))
        focusLog.finest("requestFocus is not accepted"); 
      return false;
    } 
    KeyboardFocusManager.setMostRecentFocusOwner(this);
    for (Component component = this; component != null && !(component instanceof Window); component = component.parent) {
      if (!component.isVisible()) {
        if (focusLog.isLoggable(PlatformLogger.Level.FINEST))
          focusLog.finest("component is recurively invisible"); 
        return false;
      } 
    } 
    ComponentPeer componentPeer = this.peer;
    Container container = (componentPeer instanceof java.awt.peer.LightweightPeer) ? getNativeContainer() : this;
    if (container == null || !container.isVisible()) {
      if (focusLog.isLoggable(PlatformLogger.Level.FINEST))
        focusLog.finest("Component is not a part of visible hierarchy"); 
      return false;
    } 
    componentPeer = container.peer;
    if (componentPeer == null) {
      if (focusLog.isLoggable(PlatformLogger.Level.FINEST))
        focusLog.finest("Peer is null"); 
      return false;
    } 
    long l = 0L;
    if (EventQueue.isDispatchThread()) {
      l = Toolkit.getEventQueue().getMostRecentKeyEventTime();
    } else {
      l = System.currentTimeMillis();
    } 
    boolean bool = componentPeer.requestFocus(this, paramBoolean1, paramBoolean2, l, paramCause);
    if (!bool) {
      KeyboardFocusManager.getCurrentKeyboardFocusManager(this.appContext).dequeueKeyEvents(l, this);
      if (focusLog.isLoggable(PlatformLogger.Level.FINEST))
        focusLog.finest("Peer request failed"); 
    } else if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
      focusLog.finest("Pass for " + this);
    } 
    return bool;
  }
  
  private boolean isRequestFocusAccepted(boolean paramBoolean1, boolean paramBoolean2, CausedFocusEvent.Cause paramCause) {
    if (!isFocusable() || !isVisible()) {
      if (focusLog.isLoggable(PlatformLogger.Level.FINEST))
        focusLog.finest("Not focusable or not visible"); 
      return false;
    } 
    ComponentPeer componentPeer = this.peer;
    if (componentPeer == null) {
      if (focusLog.isLoggable(PlatformLogger.Level.FINEST))
        focusLog.finest("peer is null"); 
      return false;
    } 
    Window window = getContainingWindow();
    if (window == null || !window.isFocusableWindow()) {
      if (focusLog.isLoggable(PlatformLogger.Level.FINEST))
        focusLog.finest("Component doesn't have toplevel"); 
      return false;
    } 
    Component component = KeyboardFocusManager.getMostRecentFocusOwner(window);
    if (component == null) {
      component = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
      if (component != null && component.getContainingWindow() != window)
        component = null; 
    } 
    if (component == this || component == null) {
      if (focusLog.isLoggable(PlatformLogger.Level.FINEST))
        focusLog.finest("focus owner is null or this"); 
      return true;
    } 
    if (CausedFocusEvent.Cause.ACTIVATION == paramCause) {
      if (focusLog.isLoggable(PlatformLogger.Level.FINEST))
        focusLog.finest("cause is activation"); 
      return true;
    } 
    boolean bool = requestFocusController.acceptRequestFocus(component, this, paramBoolean1, paramBoolean2, paramCause);
    if (focusLog.isLoggable(PlatformLogger.Level.FINEST))
      focusLog.finest("RequestFocusController returns {0}", new Object[] { Boolean.valueOf(bool) }); 
    return bool;
  }
  
  static void setRequestFocusController(RequestFocusController paramRequestFocusController) {
    if (paramRequestFocusController == null) {
      requestFocusController = new DummyRequestFocusController(null);
    } else {
      requestFocusController = paramRequestFocusController;
    } 
  }
  
  public Container getFocusCycleRootAncestor() {
    Container container;
    for (container = this.parent; container != null && !container.isFocusCycleRoot(); container = container.parent);
    return container;
  }
  
  public boolean isFocusCycleRoot(Container paramContainer) {
    Container container = getFocusCycleRootAncestor();
    return (container == paramContainer);
  }
  
  Container getTraversalRoot() { return getFocusCycleRootAncestor(); }
  
  public void transferFocus() { nextFocus(); }
  
  @Deprecated
  public void nextFocus() { transferFocus(false); }
  
  boolean transferFocus(boolean paramBoolean) {
    if (focusLog.isLoggable(PlatformLogger.Level.FINER))
      focusLog.finer("clearOnFailure = " + paramBoolean); 
    Component component = getNextFocusCandidate();
    boolean bool = false;
    if (component != null && !component.isFocusOwner() && component != this)
      bool = component.requestFocusInWindow(CausedFocusEvent.Cause.TRAVERSAL_FORWARD); 
    if (paramBoolean && !bool) {
      if (focusLog.isLoggable(PlatformLogger.Level.FINER))
        focusLog.finer("clear global focus owner"); 
      KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwnerPriv();
    } 
    if (focusLog.isLoggable(PlatformLogger.Level.FINER))
      focusLog.finer("returning result: " + bool); 
    return bool;
  }
  
  final Component getNextFocusCandidate() {
    Container container = getTraversalRoot();
    Component component1 = this;
    while (container != null && (!container.isShowing() || !container.canBeFocusOwner())) {
      component1 = container;
      container = component1.getFocusCycleRootAncestor();
    } 
    if (focusLog.isLoggable(PlatformLogger.Level.FINER))
      focusLog.finer("comp = " + component1 + ", root = " + container); 
    Component component2 = null;
    if (container != null) {
      FocusTraversalPolicy focusTraversalPolicy = container.getFocusTraversalPolicy();
      Component component = focusTraversalPolicy.getComponentAfter(container, component1);
      if (focusLog.isLoggable(PlatformLogger.Level.FINER))
        focusLog.finer("component after is " + component); 
      if (component == null) {
        component = focusTraversalPolicy.getDefaultComponent(container);
        if (focusLog.isLoggable(PlatformLogger.Level.FINER))
          focusLog.finer("default component is " + component); 
      } 
      if (component == null) {
        Applet applet = EmbeddedFrame.getAppletIfAncestorOf(this);
        if (applet != null)
          component = applet; 
      } 
      component2 = component;
    } 
    if (focusLog.isLoggable(PlatformLogger.Level.FINER))
      focusLog.finer("Focus transfer candidate: " + component2); 
    return component2;
  }
  
  public void transferFocusBackward() { transferFocusBackward(false); }
  
  boolean transferFocusBackward(boolean paramBoolean) {
    Container container = getTraversalRoot();
    Component component = this;
    while (container != null && (!container.isShowing() || !container.canBeFocusOwner())) {
      component = container;
      container = component.getFocusCycleRootAncestor();
    } 
    boolean bool = false;
    if (container != null) {
      FocusTraversalPolicy focusTraversalPolicy = container.getFocusTraversalPolicy();
      Component component1 = focusTraversalPolicy.getComponentBefore(container, component);
      if (component1 == null)
        component1 = focusTraversalPolicy.getDefaultComponent(container); 
      if (component1 != null)
        bool = component1.requestFocusInWindow(CausedFocusEvent.Cause.TRAVERSAL_BACKWARD); 
    } 
    if (paramBoolean && !bool) {
      if (focusLog.isLoggable(PlatformLogger.Level.FINER))
        focusLog.finer("clear global focus owner"); 
      KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwnerPriv();
    } 
    if (focusLog.isLoggable(PlatformLogger.Level.FINER))
      focusLog.finer("returning result: " + bool); 
    return bool;
  }
  
  public void transferFocusUpCycle() {
    Container container;
    for (container = getFocusCycleRootAncestor(); container != null && (!container.isShowing() || !container.isFocusable() || !container.isEnabled()); container = container.getFocusCycleRootAncestor());
    if (container != null) {
      Container container1 = container.getFocusCycleRootAncestor();
      Container container2 = (container1 != null) ? container1 : container;
      KeyboardFocusManager.getCurrentKeyboardFocusManager().setGlobalCurrentFocusCycleRootPriv(container2);
      container.requestFocus(CausedFocusEvent.Cause.TRAVERSAL_UP);
    } else {
      Window window = getContainingWindow();
      if (window != null) {
        Component component = window.getFocusTraversalPolicy().getDefaultComponent(window);
        if (component != null) {
          KeyboardFocusManager.getCurrentKeyboardFocusManager().setGlobalCurrentFocusCycleRootPriv(window);
          component.requestFocus(CausedFocusEvent.Cause.TRAVERSAL_UP);
        } 
      } 
    } 
  }
  
  public boolean hasFocus() { return (KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() == this); }
  
  public boolean isFocusOwner() { return hasFocus(); }
  
  void setAutoFocusTransferOnDisposal(boolean paramBoolean) { this.autoFocusTransferOnDisposal = paramBoolean; }
  
  boolean isAutoFocusTransferOnDisposal() { return this.autoFocusTransferOnDisposal; }
  
  public void add(PopupMenu paramPopupMenu) {
    synchronized (getTreeLock()) {
      if (paramPopupMenu.parent != null)
        paramPopupMenu.parent.remove(paramPopupMenu); 
      if (this.popups == null)
        this.popups = new Vector(); 
      this.popups.addElement(paramPopupMenu);
      paramPopupMenu.parent = this;
      if (this.peer != null && paramPopupMenu.peer == null)
        paramPopupMenu.addNotify(); 
    } 
  }
  
  public void remove(MenuComponent paramMenuComponent) {
    synchronized (getTreeLock()) {
      if (this.popups == null)
        return; 
      int i = this.popups.indexOf(paramMenuComponent);
      if (i >= 0) {
        PopupMenu popupMenu = (PopupMenu)paramMenuComponent;
        if (popupMenu.peer != null)
          popupMenu.removeNotify(); 
        popupMenu.parent = null;
        this.popups.removeElementAt(i);
        if (this.popups.size() == 0)
          this.popups = null; 
      } 
    } 
  }
  
  protected String paramString() {
    String str1 = Objects.toString(getName(), "");
    String str2 = isValid() ? "" : ",invalid";
    String str3 = this.visible ? "" : ",hidden";
    String str4 = this.enabled ? "" : ",disabled";
    return str1 + ',' + this.x + ',' + this.y + ',' + this.width + 'x' + this.height + str2 + str3 + str4;
  }
  
  public String toString() { return getClass().getName() + '[' + paramString() + ']'; }
  
  public void list() { list(System.out, 0); }
  
  public void list(PrintStream paramPrintStream) { list(paramPrintStream, 0); }
  
  public void list(PrintStream paramPrintStream, int paramInt) {
    for (byte b = 0; b < paramInt; b++)
      paramPrintStream.print(" "); 
    paramPrintStream.println(this);
  }
  
  public void list(PrintWriter paramPrintWriter) { list(paramPrintWriter, 0); }
  
  public void list(PrintWriter paramPrintWriter, int paramInt) {
    for (byte b = 0; b < paramInt; b++)
      paramPrintWriter.print(" "); 
    paramPrintWriter.println(this);
  }
  
  final Container getNativeContainer() {
    Container container;
    for (container = getContainer(); container != null && container.peer instanceof java.awt.peer.LightweightPeer; container = container.getContainer());
    return container;
  }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    synchronized (getObjectLock()) {
      if (paramPropertyChangeListener == null)
        return; 
      if (this.changeSupport == null)
        this.changeSupport = new PropertyChangeSupport(this); 
      this.changeSupport.addPropertyChangeListener(paramPropertyChangeListener);
    } 
  }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    synchronized (getObjectLock()) {
      if (paramPropertyChangeListener == null || this.changeSupport == null)
        return; 
      this.changeSupport.removePropertyChangeListener(paramPropertyChangeListener);
    } 
  }
  
  public PropertyChangeListener[] getPropertyChangeListeners() {
    synchronized (getObjectLock()) {
      if (this.changeSupport == null)
        return new PropertyChangeListener[0]; 
      return this.changeSupport.getPropertyChangeListeners();
    } 
  }
  
  public void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener) {
    synchronized (getObjectLock()) {
      if (paramPropertyChangeListener == null)
        return; 
      if (this.changeSupport == null)
        this.changeSupport = new PropertyChangeSupport(this); 
      this.changeSupport.addPropertyChangeListener(paramString, paramPropertyChangeListener);
    } 
  }
  
  public void removePropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener) {
    synchronized (getObjectLock()) {
      if (paramPropertyChangeListener == null || this.changeSupport == null)
        return; 
      this.changeSupport.removePropertyChangeListener(paramString, paramPropertyChangeListener);
    } 
  }
  
  public PropertyChangeListener[] getPropertyChangeListeners(String paramString) {
    synchronized (getObjectLock()) {
      if (this.changeSupport == null)
        return new PropertyChangeListener[0]; 
      return this.changeSupport.getPropertyChangeListeners(paramString);
    } 
  }
  
  protected void firePropertyChange(String paramString, Object paramObject1, Object paramObject2) {
    PropertyChangeSupport propertyChangeSupport;
    synchronized (getObjectLock()) {
      propertyChangeSupport = this.changeSupport;
    } 
    if (propertyChangeSupport == null || (paramObject1 != null && paramObject2 != null && paramObject1.equals(paramObject2)))
      return; 
    propertyChangeSupport.firePropertyChange(paramString, paramObject1, paramObject2);
  }
  
  protected void firePropertyChange(String paramString, boolean paramBoolean1, boolean paramBoolean2) {
    PropertyChangeSupport propertyChangeSupport = this.changeSupport;
    if (propertyChangeSupport == null || paramBoolean1 == paramBoolean2)
      return; 
    propertyChangeSupport.firePropertyChange(paramString, paramBoolean1, paramBoolean2);
  }
  
  protected void firePropertyChange(String paramString, int paramInt1, int paramInt2) {
    PropertyChangeSupport propertyChangeSupport = this.changeSupport;
    if (propertyChangeSupport == null || paramInt1 == paramInt2)
      return; 
    propertyChangeSupport.firePropertyChange(paramString, paramInt1, paramInt2);
  }
  
  public void firePropertyChange(String paramString, byte paramByte1, byte paramByte2) {
    if (this.changeSupport == null || paramByte1 == paramByte2)
      return; 
    firePropertyChange(paramString, Byte.valueOf(paramByte1), Byte.valueOf(paramByte2));
  }
  
  public void firePropertyChange(String paramString, char paramChar1, char paramChar2) {
    if (this.changeSupport == null || paramChar1 == paramChar2)
      return; 
    firePropertyChange(paramString, new Character(paramChar1), new Character(paramChar2));
  }
  
  public void firePropertyChange(String paramString, short paramShort1, short paramShort2) {
    if (this.changeSupport == null || paramShort1 == paramShort2)
      return; 
    firePropertyChange(paramString, Short.valueOf(paramShort1), Short.valueOf(paramShort2));
  }
  
  public void firePropertyChange(String paramString, long paramLong1, long paramLong2) {
    if (this.changeSupport == null || paramLong1 == paramLong2)
      return; 
    firePropertyChange(paramString, Long.valueOf(paramLong1), Long.valueOf(paramLong2));
  }
  
  public void firePropertyChange(String paramString, float paramFloat1, float paramFloat2) {
    if (this.changeSupport == null || paramFloat1 == paramFloat2)
      return; 
    firePropertyChange(paramString, Float.valueOf(paramFloat1), Float.valueOf(paramFloat2));
  }
  
  public void firePropertyChange(String paramString, double paramDouble1, double paramDouble2) {
    if (this.changeSupport == null || paramDouble1 == paramDouble2)
      return; 
    firePropertyChange(paramString, Double.valueOf(paramDouble1), Double.valueOf(paramDouble2));
  }
  
  private void doSwingSerialization() {
    Package package = Package.getPackage("javax.swing");
    for (Class clazz = getClass(); clazz != null; clazz = clazz.getSuperclass()) {
      if (clazz.getPackage() == package && clazz.getClassLoader() == null) {
        final Class swingClass = clazz;
        Method[] arrayOfMethod = (Method[])AccessController.doPrivileged(new PrivilegedAction<Method[]>() {
              public Method[] run() { return swingClass.getDeclaredMethods(); }
            });
        for (int i = arrayOfMethod.length - 1; i >= 0; i--) {
          final Method method = arrayOfMethod[i];
          if (method.getName().equals("compWriteObjectNotify")) {
            AccessController.doPrivileged(new PrivilegedAction<Void>() {
                  public Void run() {
                    method.setAccessible(true);
                    return null;
                  }
                });
            try {
              method.invoke(this, (Object[])null);
            } catch (IllegalAccessException illegalAccessException) {
            
            } catch (InvocationTargetException invocationTargetException) {}
            return;
          } 
        } 
      } 
    } 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    doSwingSerialization();
    paramObjectOutputStream.defaultWriteObject();
    AWTEventMulticaster.save(paramObjectOutputStream, "componentL", this.componentListener);
    AWTEventMulticaster.save(paramObjectOutputStream, "focusL", this.focusListener);
    AWTEventMulticaster.save(paramObjectOutputStream, "keyL", this.keyListener);
    AWTEventMulticaster.save(paramObjectOutputStream, "mouseL", this.mouseListener);
    AWTEventMulticaster.save(paramObjectOutputStream, "mouseMotionL", this.mouseMotionListener);
    AWTEventMulticaster.save(paramObjectOutputStream, "inputMethodL", this.inputMethodListener);
    paramObjectOutputStream.writeObject(null);
    paramObjectOutputStream.writeObject(this.componentOrientation);
    AWTEventMulticaster.save(paramObjectOutputStream, "hierarchyL", this.hierarchyListener);
    AWTEventMulticaster.save(paramObjectOutputStream, "hierarchyBoundsL", this.hierarchyBoundsListener);
    paramObjectOutputStream.writeObject(null);
    AWTEventMulticaster.save(paramObjectOutputStream, "mouseWheelL", this.mouseWheelListener);
    paramObjectOutputStream.writeObject(null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException {
    this.objectLock = new Object();
    this.acc = AccessController.getContext();
    paramObjectInputStream.defaultReadObject();
    this.appContext = AppContext.getAppContext();
    this.coalescingEnabled = checkCoalescing();
    if (this.componentSerializedDataVersion < 4) {
      this.focusable = true;
      this.isFocusTraversableOverridden = 0;
      initializeFocusTraversalKeys();
      this.focusTraversalKeysEnabled = true;
    } 
    Object object1;
    while (null != (object1 = paramObjectInputStream.readObject())) {
      String str = ((String)object1).intern();
      if ("componentL" == str) {
        addComponentListener((ComponentListener)paramObjectInputStream.readObject());
        continue;
      } 
      if ("focusL" == str) {
        addFocusListener((FocusListener)paramObjectInputStream.readObject());
        continue;
      } 
      if ("keyL" == str) {
        addKeyListener((KeyListener)paramObjectInputStream.readObject());
        continue;
      } 
      if ("mouseL" == str) {
        addMouseListener((MouseListener)paramObjectInputStream.readObject());
        continue;
      } 
      if ("mouseMotionL" == str) {
        addMouseMotionListener((MouseMotionListener)paramObjectInputStream.readObject());
        continue;
      } 
      if ("inputMethodL" == str) {
        addInputMethodListener((InputMethodListener)paramObjectInputStream.readObject());
        continue;
      } 
      paramObjectInputStream.readObject();
    } 
    Object object2 = null;
    try {
      object2 = paramObjectInputStream.readObject();
    } catch (OptionalDataException optionalDataException) {
      if (!optionalDataException.eof)
        throw optionalDataException; 
    } 
    if (object2 != null) {
      this.componentOrientation = (ComponentOrientation)object2;
    } else {
      this.componentOrientation = ComponentOrientation.UNKNOWN;
    } 
    while (null != (object1 = paramObjectInputStream.readObject())) {
      String str = ((String)object1).intern();
      if ("hierarchyL" == str) {
        addHierarchyListener((HierarchyListener)paramObjectInputStream.readObject());
        continue;
      } 
      if ("hierarchyBoundsL" == str) {
        addHierarchyBoundsListener((HierarchyBoundsListener)paramObjectInputStream.readObject());
        continue;
      } 
      paramObjectInputStream.readObject();
    } 
    while (null != (object1 = paramObjectInputStream.readObject())) {
      String str = ((String)object1).intern();
      if ("mouseWheelL" == str) {
        addMouseWheelListener((MouseWheelListener)paramObjectInputStream.readObject());
        continue;
      } 
      paramObjectInputStream.readObject();
    } 
    if (this.popups != null) {
      int i = this.popups.size();
      for (byte b = 0; b < i; b++) {
        PopupMenu popupMenu = (PopupMenu)this.popups.elementAt(b);
        popupMenu.parent = this;
      } 
    } 
  }
  
  public void setComponentOrientation(ComponentOrientation paramComponentOrientation) {
    ComponentOrientation componentOrientation1 = this.componentOrientation;
    this.componentOrientation = paramComponentOrientation;
    firePropertyChange("componentOrientation", componentOrientation1, paramComponentOrientation);
    invalidateIfValid();
  }
  
  public ComponentOrientation getComponentOrientation() { return this.componentOrientation; }
  
  public void applyComponentOrientation(ComponentOrientation paramComponentOrientation) {
    if (paramComponentOrientation == null)
      throw new NullPointerException(); 
    setComponentOrientation(paramComponentOrientation);
  }
  
  final boolean canBeFocusOwner() { return (isEnabled() && isDisplayable() && isVisible() && isFocusable()); }
  
  final boolean canBeFocusOwnerRecursively() {
    if (!canBeFocusOwner())
      return false; 
    synchronized (getTreeLock()) {
      if (this.parent != null)
        return this.parent.canContainFocusOwner(this); 
    } 
    return true;
  }
  
  final void relocateComponent() {
    synchronized (getTreeLock()) {
      if (this.peer == null)
        return; 
      int i = this.x;
      int j = this.y;
      for (Container container = getContainer(); container != null && container.isLightweight(); container = container.getContainer()) {
        i += container.x;
        j += container.y;
      } 
      this.peer.setBounds(i, j, this.width, this.height, 1);
    } 
  }
  
  Window getContainingWindow() { return SunToolkit.getContainingWindow(this); }
  
  private static native void initIDs();
  
  public AccessibleContext getAccessibleContext() { return this.accessibleContext; }
  
  int getAccessibleIndexInParent() {
    synchronized (getTreeLock()) {
      byte b = -1;
      Container container = getParent();
      if (container != null && container instanceof Accessible) {
        Component[] arrayOfComponent = container.getComponents();
        for (byte b1 = 0; b1 < arrayOfComponent.length; b1++) {
          if (arrayOfComponent[b1] instanceof Accessible)
            b++; 
          if (equals(arrayOfComponent[b1]))
            return b; 
        } 
      } 
      return -1;
    } 
  }
  
  AccessibleStateSet getAccessibleStateSet() {
    synchronized (getTreeLock()) {
      AccessibleStateSet accessibleStateSet = new AccessibleStateSet();
      if (isEnabled())
        accessibleStateSet.add(AccessibleState.ENABLED); 
      if (isFocusTraversable())
        accessibleStateSet.add(AccessibleState.FOCUSABLE); 
      if (isVisible())
        accessibleStateSet.add(AccessibleState.VISIBLE); 
      if (isShowing())
        accessibleStateSet.add(AccessibleState.SHOWING); 
      if (isFocusOwner())
        accessibleStateSet.add(AccessibleState.FOCUSED); 
      if (this instanceof Accessible) {
        AccessibleContext accessibleContext1 = ((Accessible)this).getAccessibleContext();
        if (accessibleContext1 != null) {
          Accessible accessible = accessibleContext1.getAccessibleParent();
          if (accessible != null) {
            AccessibleContext accessibleContext2 = accessible.getAccessibleContext();
            if (accessibleContext2 != null) {
              AccessibleSelection accessibleSelection = accessibleContext2.getAccessibleSelection();
              if (accessibleSelection != null) {
                accessibleStateSet.add(AccessibleState.SELECTABLE);
                int i = accessibleContext1.getAccessibleIndexInParent();
                if (i >= 0 && accessibleSelection.isAccessibleChildSelected(i))
                  accessibleStateSet.add(AccessibleState.SELECTED); 
              } 
            } 
          } 
        } 
      } 
      if (isInstanceOf(this, "javax.swing.JComponent") && ((JComponent)this).isOpaque())
        accessibleStateSet.add(AccessibleState.OPAQUE); 
      return accessibleStateSet;
    } 
  }
  
  static boolean isInstanceOf(Object paramObject, String paramString) {
    if (paramObject == null)
      return false; 
    if (paramString == null)
      return false; 
    for (Class clazz = paramObject.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
      if (clazz.getName().equals(paramString))
        return true; 
    } 
    return false;
  }
  
  final boolean areBoundsValid() {
    Container container = getContainer();
    return (container == null || container.isValid() || container.getLayout() == null);
  }
  
  void applyCompoundShape(Region paramRegion) {
    checkTreeLock();
    if (!areBoundsValid()) {
      if (mixingLog.isLoggable(PlatformLogger.Level.FINE))
        mixingLog.fine("this = " + this + "; areBoundsValid = " + areBoundsValid()); 
      return;
    } 
    if (!isLightweight()) {
      ComponentPeer componentPeer = getPeer();
      if (componentPeer != null) {
        if (paramRegion.isEmpty())
          paramRegion = Region.EMPTY_REGION; 
        if (paramRegion.equals(getNormalShape())) {
          if (this.compoundShape == null)
            return; 
          this.compoundShape = null;
          componentPeer.applyShape(null);
        } else {
          if (paramRegion.equals(getAppliedShape()))
            return; 
          this.compoundShape = paramRegion;
          Point point = getLocationOnWindow();
          if (mixingLog.isLoggable(PlatformLogger.Level.FINER))
            mixingLog.fine("this = " + this + "; compAbsolute=" + point + "; shape=" + paramRegion); 
          componentPeer.applyShape(paramRegion.getTranslatedRegion(-point.x, -point.y));
        } 
      } 
    } 
  }
  
  private Region getAppliedShape() {
    checkTreeLock();
    return (this.compoundShape == null || isLightweight()) ? getNormalShape() : this.compoundShape;
  }
  
  Point getLocationOnWindow() throws HeadlessException {
    checkTreeLock();
    Point point = getLocation();
    for (Container container = getContainer(); container != null && !(container instanceof Window); container = container.getContainer()) {
      point.x += container.getX();
      point.y += container.getY();
    } 
    return point;
  }
  
  final Region getNormalShape() {
    checkTreeLock();
    Point point = getLocationOnWindow();
    return Region.getInstanceXYWH(point.x, point.y, getWidth(), getHeight());
  }
  
  Region getOpaqueShape() {
    checkTreeLock();
    return (this.mixingCutoutRegion != null) ? this.mixingCutoutRegion : getNormalShape();
  }
  
  final int getSiblingIndexAbove() {
    checkTreeLock();
    Container container = getContainer();
    if (container == null)
      return -1; 
    int i = container.getComponentZOrder(this) - 1;
    return (i < 0) ? -1 : i;
  }
  
  final ComponentPeer getHWPeerAboveMe() {
    checkTreeLock();
    Container container = getContainer();
    int i = getSiblingIndexAbove();
    while (container != null) {
      for (int j = i; j > -1; j--) {
        Component component = container.getComponent(j);
        if (component != null && component.isDisplayable() && !component.isLightweight())
          return component.getPeer(); 
      } 
      if (!container.isLightweight())
        break; 
      i = container.getSiblingIndexAbove();
      container = container.getContainer();
    } 
    return null;
  }
  
  final int getSiblingIndexBelow() {
    checkTreeLock();
    Container container = getContainer();
    if (container == null)
      return -1; 
    int i = container.getComponentZOrder(this) + 1;
    return (i >= container.getComponentCount()) ? -1 : i;
  }
  
  final boolean isNonOpaqueForMixing() { return (this.mixingCutoutRegion != null && this.mixingCutoutRegion.isEmpty()); }
  
  private Region calculateCurrentShape() {
    checkTreeLock();
    Region region = getNormalShape();
    if (mixingLog.isLoggable(PlatformLogger.Level.FINE))
      mixingLog.fine("this = " + this + "; normalShape=" + region); 
    if (getContainer() != null) {
      Component component = this;
      Container container = component.getContainer();
      while (container != null) {
        for (int i = component.getSiblingIndexAbove(); i != -1; i--) {
          Component component1 = container.getComponent(i);
          if (component1.isLightweight() && component1.isShowing())
            region = region.getDifference(component1.getOpaqueShape()); 
        } 
        if (container.isLightweight()) {
          region = region.getIntersection(container.getNormalShape());
          component = container;
          container = container.getContainer();
        } 
      } 
    } 
    if (mixingLog.isLoggable(PlatformLogger.Level.FINE))
      mixingLog.fine("currentShape=" + region); 
    return region;
  }
  
  void applyCurrentShape() {
    checkTreeLock();
    if (!areBoundsValid()) {
      if (mixingLog.isLoggable(PlatformLogger.Level.FINE))
        mixingLog.fine("this = " + this + "; areBoundsValid = " + areBoundsValid()); 
      return;
    } 
    if (mixingLog.isLoggable(PlatformLogger.Level.FINE))
      mixingLog.fine("this = " + this); 
    applyCompoundShape(calculateCurrentShape());
  }
  
  final void subtractAndApplyShape(Region paramRegion) {
    checkTreeLock();
    if (mixingLog.isLoggable(PlatformLogger.Level.FINE))
      mixingLog.fine("this = " + this + "; s=" + paramRegion); 
    applyCompoundShape(getAppliedShape().getDifference(paramRegion));
  }
  
  private final void applyCurrentShapeBelowMe() {
    checkTreeLock();
    Container container = getContainer();
    if (container != null && container.isShowing()) {
      container.recursiveApplyCurrentShape(getSiblingIndexBelow());
      for (Container container1 = container.getContainer(); !container.isOpaque() && container1 != null; container1 = container.getContainer()) {
        container1.recursiveApplyCurrentShape(container.getSiblingIndexBelow());
        container = container1;
      } 
    } 
  }
  
  final void subtractAndApplyShapeBelowMe() {
    checkTreeLock();
    Container container = getContainer();
    if (container != null && isShowing()) {
      Region region = getOpaqueShape();
      container.recursiveSubtractAndApplyShape(region, getSiblingIndexBelow());
      for (Container container1 = container.getContainer(); !container.isOpaque() && container1 != null; container1 = container.getContainer()) {
        container1.recursiveSubtractAndApplyShape(region, container.getSiblingIndexBelow());
        container = container1;
      } 
    } 
  }
  
  void mixOnShowing() {
    synchronized (getTreeLock()) {
      if (mixingLog.isLoggable(PlatformLogger.Level.FINE))
        mixingLog.fine("this = " + this); 
      if (!isMixingNeeded())
        return; 
      if (isLightweight()) {
        subtractAndApplyShapeBelowMe();
      } else {
        applyCurrentShape();
      } 
    } 
  }
  
  void mixOnHiding(boolean paramBoolean) {
    synchronized (getTreeLock()) {
      if (mixingLog.isLoggable(PlatformLogger.Level.FINE))
        mixingLog.fine("this = " + this + "; isLightweight = " + paramBoolean); 
      if (!isMixingNeeded())
        return; 
      if (paramBoolean)
        applyCurrentShapeBelowMe(); 
    } 
  }
  
  void mixOnReshaping() {
    synchronized (getTreeLock()) {
      if (mixingLog.isLoggable(PlatformLogger.Level.FINE))
        mixingLog.fine("this = " + this); 
      if (!isMixingNeeded())
        return; 
      if (isLightweight()) {
        applyCurrentShapeBelowMe();
      } else {
        applyCurrentShape();
      } 
    } 
  }
  
  void mixOnZOrderChanging(int paramInt1, int paramInt2) {
    synchronized (getTreeLock()) {
      boolean bool = (paramInt2 < paramInt1) ? 1 : 0;
      Container container = getContainer();
      if (mixingLog.isLoggable(PlatformLogger.Level.FINE))
        mixingLog.fine("this = " + this + "; oldZorder=" + paramInt1 + "; newZorder=" + paramInt2 + "; parent=" + container); 
      if (!isMixingNeeded())
        return; 
      if (isLightweight()) {
        if (bool) {
          if (container != null && isShowing())
            container.recursiveSubtractAndApplyShape(getOpaqueShape(), getSiblingIndexBelow(), paramInt1); 
        } else if (container != null) {
          container.recursiveApplyCurrentShape(paramInt1, paramInt2);
        } 
      } else if (bool) {
        applyCurrentShape();
      } else if (container != null) {
        Region region = getAppliedShape();
        for (int i = paramInt1; i < paramInt2; i++) {
          Component component = container.getComponent(i);
          if (component.isLightweight() && component.isShowing())
            region = region.getDifference(component.getOpaqueShape()); 
        } 
        applyCompoundShape(region);
      } 
    } 
  }
  
  void mixOnValidating() {}
  
  final boolean isMixingNeeded() {
    if (SunToolkit.getSunAwtDisableMixing()) {
      if (mixingLog.isLoggable(PlatformLogger.Level.FINEST))
        mixingLog.finest("this = " + this + "; Mixing disabled via sun.awt.disableMixing"); 
      return false;
    } 
    if (!areBoundsValid()) {
      if (mixingLog.isLoggable(PlatformLogger.Level.FINE))
        mixingLog.fine("this = " + this + "; areBoundsValid = " + areBoundsValid()); 
      return false;
    } 
    Window window = getContainingWindow();
    if (window != null) {
      if (!window.hasHeavyweightDescendants() || !window.hasLightweightDescendants() || window.isDisposing()) {
        if (mixingLog.isLoggable(PlatformLogger.Level.FINE))
          mixingLog.fine("containing window = " + window + "; has h/w descendants = " + window.hasHeavyweightDescendants() + "; has l/w descendants = " + window.hasLightweightDescendants() + "; disposing = " + window.isDisposing()); 
        return false;
      } 
    } else {
      if (mixingLog.isLoggable(PlatformLogger.Level.FINE))
        mixingLog.fine("this = " + this + "; containing window is null"); 
      return false;
    } 
    return true;
  }
  
  void updateZOrder() { this.peer.setZOrder(getHWPeerAboveMe()); }
  
  static  {
    Toolkit.loadLibraries();
    if (!GraphicsEnvironment.isHeadless())
      initIDs(); 
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("awt.image.incrementaldraw"));
    isInc = (str == null || str.equals("true"));
    str = (String)AccessController.doPrivileged(new GetPropertyAction("awt.image.redrawrate"));
    incRate = (str != null) ? Integer.parseInt(str) : 100;
    AWTAccessor.setComponentAccessor(new AWTAccessor.ComponentAccessor() {
          public void setBackgroundEraseDisabled(Component param1Component, boolean param1Boolean) { param1Component.backgroundEraseDisabled = param1Boolean; }
          
          public boolean getBackgroundEraseDisabled(Component param1Component) { return param1Component.backgroundEraseDisabled; }
          
          public Rectangle getBounds(Component param1Component) { return new Rectangle(param1Component.x, param1Component.y, param1Component.width, param1Component.height); }
          
          public void setMixingCutoutShape(Component param1Component, Shape param1Shape) {
            Region region = (param1Shape == null) ? null : Region.getInstance(param1Shape, null);
            synchronized (param1Component.getTreeLock()) {
              boolean bool1 = false;
              boolean bool2 = false;
              if (!param1Component.isNonOpaqueForMixing())
                bool2 = true; 
              param1Component.mixingCutoutRegion = region;
              if (!param1Component.isNonOpaqueForMixing())
                bool1 = true; 
              if (param1Component.isMixingNeeded()) {
                if (bool2)
                  param1Component.mixOnHiding(param1Component.isLightweight()); 
                if (bool1)
                  param1Component.mixOnShowing(); 
              } 
            } 
          }
          
          public void setGraphicsConfiguration(Component param1Component, GraphicsConfiguration param1GraphicsConfiguration) { param1Component.setGraphicsConfiguration(param1GraphicsConfiguration); }
          
          public boolean requestFocus(Component param1Component, CausedFocusEvent.Cause param1Cause) { return param1Component.requestFocus(param1Cause); }
          
          public boolean canBeFocusOwner(Component param1Component) { return param1Component.canBeFocusOwner(); }
          
          public boolean isVisible(Component param1Component) { return param1Component.isVisible_NoClientCode(); }
          
          public void setRequestFocusController(RequestFocusController param1RequestFocusController) { Component.setRequestFocusController(param1RequestFocusController); }
          
          public AppContext getAppContext(Component param1Component) { return param1Component.appContext; }
          
          public void setAppContext(Component param1Component, AppContext param1AppContext) { param1Component.appContext = param1AppContext; }
          
          public Container getParent(Component param1Component) { return param1Component.getParent_NoClientCode(); }
          
          public void setParent(Component param1Component, Container param1Container) { param1Component.parent = param1Container; }
          
          public void setSize(Component param1Component, int param1Int1, int param1Int2) {
            param1Component.width = param1Int1;
            param1Component.height = param1Int2;
          }
          
          public Point getLocation(Component param1Component) { return param1Component.location_NoClientCode(); }
          
          public void setLocation(Component param1Component, int param1Int1, int param1Int2) {
            param1Component.x = param1Int1;
            param1Component.y = param1Int2;
          }
          
          public boolean isEnabled(Component param1Component) { return param1Component.isEnabledImpl(); }
          
          public boolean isDisplayable(Component param1Component) { return (param1Component.peer != null); }
          
          public Cursor getCursor(Component param1Component) { return param1Component.getCursor_NoClientCode(); }
          
          public ComponentPeer getPeer(Component param1Component) { return param1Component.peer; }
          
          public void setPeer(Component param1Component, ComponentPeer param1ComponentPeer) { param1Component.peer = param1ComponentPeer; }
          
          public boolean isLightweight(Component param1Component) { return param1Component.peer instanceof java.awt.peer.LightweightPeer; }
          
          public boolean getIgnoreRepaint(Component param1Component) { return param1Component.ignoreRepaint; }
          
          public int getWidth(Component param1Component) { return param1Component.width; }
          
          public int getHeight(Component param1Component) { return param1Component.height; }
          
          public int getX(Component param1Component) { return param1Component.x; }
          
          public int getY(Component param1Component) { return param1Component.y; }
          
          public Color getForeground(Component param1Component) { return param1Component.foreground; }
          
          public Color getBackground(Component param1Component) { return param1Component.background; }
          
          public void setBackground(Component param1Component, Color param1Color) { param1Component.background = param1Color; }
          
          public Font getFont(Component param1Component) { return param1Component.getFont_NoClientCode(); }
          
          public void processEvent(Component param1Component, AWTEvent param1AWTEvent) { param1Component.processEvent(param1AWTEvent); }
          
          public AccessControlContext getAccessControlContext(Component param1Component) { return param1Component.getAccessControlContext(); }
          
          public void revalidateSynchronously(Component param1Component) { param1Component.revalidateSynchronously(); }
        });
    coalesceMap = new WeakHashMap();
    coalesceEventsParams = new Class[] { AWTEvent.class, AWTEvent.class };
    requestFocusController = new DummyRequestFocusController(null);
  }
  
  static class AWTTreeLock {}
  
  protected abstract class AccessibleAWTComponent extends AccessibleContext implements Serializable, AccessibleComponent {
    private static final long serialVersionUID = 642321655757800191L;
    
    protected ComponentListener accessibleAWTComponentHandler = null;
    
    protected FocusListener accessibleAWTFocusHandler = null;
    
    public void addPropertyChangeListener(PropertyChangeListener param1PropertyChangeListener) {
      if (this.accessibleAWTComponentHandler == null)
        this.accessibleAWTComponentHandler = new AccessibleAWTComponentHandler(); 
      if (this.accessibleAWTFocusHandler == null)
        this.accessibleAWTFocusHandler = new AccessibleAWTFocusHandler(); 
      if (this.propertyListenersCount++ == 0) {
        Component.this.addComponentListener(this.accessibleAWTComponentHandler);
        Component.this.addFocusListener(this.accessibleAWTFocusHandler);
      } 
      super.addPropertyChangeListener(param1PropertyChangeListener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener param1PropertyChangeListener) {
      if (--this.propertyListenersCount == 0) {
        Component.this.removeComponentListener(this.accessibleAWTComponentHandler);
        Component.this.removeFocusListener(this.accessibleAWTFocusHandler);
      } 
      super.removePropertyChangeListener(param1PropertyChangeListener);
    }
    
    public String getAccessibleName() { return this.accessibleName; }
    
    public String getAccessibleDescription() { return this.accessibleDescription; }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.AWT_COMPONENT; }
    
    public AccessibleStateSet getAccessibleStateSet() { return Component.this.getAccessibleStateSet(); }
    
    public Accessible getAccessibleParent() {
      if (this.accessibleParent != null)
        return this.accessibleParent; 
      Container container = Component.this.getParent();
      return (container instanceof Accessible) ? (Accessible)container : null;
    }
    
    public int getAccessibleIndexInParent() { return Component.this.getAccessibleIndexInParent(); }
    
    public int getAccessibleChildrenCount() { return 0; }
    
    public Accessible getAccessibleChild(int param1Int) { return null; }
    
    public Locale getLocale() { return Component.this.getLocale(); }
    
    public AccessibleComponent getAccessibleComponent() { return this; }
    
    public Color getBackground() { return Component.this.getBackground(); }
    
    public void setBackground(Color param1Color) { Component.this.setBackground(param1Color); }
    
    public Color getForeground() { return Component.this.getForeground(); }
    
    public void setForeground(Color param1Color) { Component.this.setForeground(param1Color); }
    
    public Cursor getCursor() { return Component.this.getCursor(); }
    
    public void setCursor(Cursor param1Cursor) { Component.this.setCursor(param1Cursor); }
    
    public Font getFont() { return Component.this.getFont(); }
    
    public void setFont(Font param1Font) { Component.this.setFont(param1Font); }
    
    public FontMetrics getFontMetrics(Font param1Font) { return (param1Font == null) ? null : Component.this.getFontMetrics(param1Font); }
    
    public boolean isEnabled() { return Component.this.isEnabled(); }
    
    public void setEnabled(boolean param1Boolean) {
      boolean bool = Component.this.isEnabled();
      Component.this.setEnabled(param1Boolean);
      if (param1Boolean != bool && Component.this.accessibleContext != null)
        if (param1Boolean) {
          Component.this.accessibleContext.firePropertyChange("AccessibleState", null, AccessibleState.ENABLED);
        } else {
          Component.this.accessibleContext.firePropertyChange("AccessibleState", AccessibleState.ENABLED, null);
        }  
    }
    
    public boolean isVisible() { return Component.this.isVisible(); }
    
    public void setVisible(boolean param1Boolean) {
      boolean bool = Component.this.isVisible();
      Component.this.setVisible(param1Boolean);
      if (param1Boolean != bool && Component.this.accessibleContext != null)
        if (param1Boolean) {
          Component.this.accessibleContext.firePropertyChange("AccessibleState", null, AccessibleState.VISIBLE);
        } else {
          Component.this.accessibleContext.firePropertyChange("AccessibleState", AccessibleState.VISIBLE, null);
        }  
    }
    
    public boolean isShowing() { return Component.this.isShowing(); }
    
    public boolean contains(Point param1Point) { return Component.this.contains(param1Point); }
    
    public Point getLocationOnScreen() throws HeadlessException {
      synchronized (Component.this.getTreeLock()) {
        if (Component.this.isShowing())
          return Component.this.getLocationOnScreen(); 
        return null;
      } 
    }
    
    public Point getLocation() throws HeadlessException { return Component.this.getLocation(); }
    
    public void setLocation(Point param1Point) { Component.this.setLocation(param1Point); }
    
    public Rectangle getBounds() { return Component.this.getBounds(); }
    
    public void setBounds(Rectangle param1Rectangle) { Component.this.setBounds(param1Rectangle); }
    
    public Dimension getSize() { return Component.this.getSize(); }
    
    public void setSize(Dimension param1Dimension) { Component.this.setSize(param1Dimension); }
    
    public Accessible getAccessibleAt(Point param1Point) { return null; }
    
    public boolean isFocusTraversable() { return Component.this.isFocusTraversable(); }
    
    public void requestFocus() { Component.this.requestFocus(); }
    
    public void addFocusListener(FocusListener param1FocusListener) { Component.this.addFocusListener(param1FocusListener); }
    
    public void removeFocusListener(FocusListener param1FocusListener) { Component.this.removeFocusListener(param1FocusListener); }
    
    protected class AccessibleAWTComponentHandler implements ComponentListener {
      public void componentHidden(ComponentEvent param2ComponentEvent) {
        if (this.this$1.this$0.accessibleContext != null)
          this.this$1.this$0.accessibleContext.firePropertyChange("AccessibleState", AccessibleState.VISIBLE, null); 
      }
      
      public void componentShown(ComponentEvent param2ComponentEvent) {
        if (this.this$1.this$0.accessibleContext != null)
          this.this$1.this$0.accessibleContext.firePropertyChange("AccessibleState", null, AccessibleState.VISIBLE); 
      }
      
      public void componentMoved(ComponentEvent param2ComponentEvent) {}
      
      public void componentResized(ComponentEvent param2ComponentEvent) {}
    }
    
    protected class AccessibleAWTFocusHandler implements FocusListener {
      public void focusGained(FocusEvent param2FocusEvent) {
        if (this.this$1.this$0.accessibleContext != null)
          this.this$1.this$0.accessibleContext.firePropertyChange("AccessibleState", null, AccessibleState.FOCUSED); 
      }
      
      public void focusLost(FocusEvent param2FocusEvent) {
        if (this.this$1.this$0.accessibleContext != null)
          this.this$1.this$0.accessibleContext.firePropertyChange("AccessibleState", AccessibleState.FOCUSED, null); 
      }
    }
  }
  
  public enum BaselineResizeBehavior {
    CONSTANT_ASCENT, CONSTANT_DESCENT, CENTER_OFFSET, OTHER;
  }
  
  protected class BltBufferStrategy extends BufferStrategy {
    protected BufferCapabilities caps;
    
    protected VolatileImage[] backBuffers;
    
    protected boolean validatedContents;
    
    protected int width;
    
    protected int height;
    
    private Insets insets;
    
    protected BltBufferStrategy(int param1Int, BufferCapabilities param1BufferCapabilities) {
      this.caps = param1BufferCapabilities;
      createBackBuffers(param1Int - 1);
    }
    
    public void dispose() {
      if (this.backBuffers != null)
        for (int i = this.backBuffers.length - 1; i >= 0; i--) {
          if (this.backBuffers[i] != null) {
            this.backBuffers[i].flush();
            this.backBuffers[i] = null;
          } 
        }  
      if (Component.this.bufferStrategy == this)
        Component.this.bufferStrategy = null; 
    }
    
    protected void createBackBuffers(int param1Int) {
      if (param1Int == 0) {
        this.backBuffers = null;
      } else {
        this.width = Component.this.getWidth();
        this.height = Component.this.getHeight();
        this.insets = Component.this.getInsets_NoClientCode();
        int i = this.width - this.insets.left - this.insets.right;
        int j = this.height - this.insets.top - this.insets.bottom;
        i = Math.max(1, i);
        j = Math.max(1, j);
        if (this.backBuffers == null) {
          this.backBuffers = new VolatileImage[param1Int];
        } else {
          for (byte b1 = 0; b1 < param1Int; b1++) {
            if (this.backBuffers[b1] != null) {
              this.backBuffers[b1].flush();
              this.backBuffers[b1] = null;
            } 
          } 
        } 
        for (byte b = 0; b < param1Int; b++)
          this.backBuffers[b] = Component.this.createVolatileImage(i, j); 
      } 
    }
    
    public BufferCapabilities getCapabilities() { return this.caps; }
    
    public Graphics getDrawGraphics() {
      revalidate();
      Image image = getBackBuffer();
      if (image == null)
        return Component.this.getGraphics(); 
      SunGraphics2D sunGraphics2D = (SunGraphics2D)image.getGraphics();
      sunGraphics2D.constrain(-this.insets.left, -this.insets.top, image.getWidth(null) + this.insets.left, image.getHeight(null) + this.insets.top);
      return sunGraphics2D;
    }
    
    Image getBackBuffer() { return (this.backBuffers != null) ? this.backBuffers[this.backBuffers.length - 1] : null; }
    
    public void show() { showSubRegion(this.insets.left, this.insets.top, this.width - this.insets.right, this.height - this.insets.bottom); }
    
    void showSubRegion(int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      if (this.backBuffers == null)
        return; 
      param1Int1 -= this.insets.left;
      param1Int3 -= this.insets.left;
      param1Int2 -= this.insets.top;
      param1Int4 -= this.insets.top;
      graphics = Component.this.getGraphics_NoClientCode();
      if (graphics == null)
        return; 
      try {
        graphics.translate(this.insets.left, this.insets.top);
        for (byte b = 0; b < this.backBuffers.length; b++) {
          graphics.drawImage(this.backBuffers[b], param1Int1, param1Int2, param1Int3, param1Int4, param1Int1, param1Int2, param1Int3, param1Int4, null);
          graphics.dispose();
          graphics = null;
          graphics = this.backBuffers[b].getGraphics();
        } 
      } finally {
        if (graphics != null)
          graphics.dispose(); 
      } 
    }
    
    protected void revalidate() { revalidate(true); }
    
    void revalidate(boolean param1Boolean) {
      this.validatedContents = false;
      if (this.backBuffers == null)
        return; 
      if (param1Boolean) {
        Insets insets1 = Component.this.getInsets_NoClientCode();
        if (Component.this.getWidth() != this.width || Component.this.getHeight() != this.height || !insets1.equals(this.insets)) {
          createBackBuffers(this.backBuffers.length);
          this.validatedContents = true;
        } 
      } 
      GraphicsConfiguration graphicsConfiguration = Component.this.getGraphicsConfiguration_NoClientCode();
      int i = this.backBuffers[this.backBuffers.length - 1].validate(graphicsConfiguration);
      if (i == 2) {
        if (param1Boolean) {
          createBackBuffers(this.backBuffers.length);
          this.backBuffers[this.backBuffers.length - 1].validate(graphicsConfiguration);
        } 
        this.validatedContents = true;
      } else if (i == 1) {
        this.validatedContents = true;
      } 
    }
    
    public boolean contentsLost() { return (this.backBuffers == null) ? false : this.backBuffers[this.backBuffers.length - 1].contentsLost(); }
    
    public boolean contentsRestored() { return this.validatedContents; }
  }
  
  private class BltSubRegionBufferStrategy extends BltBufferStrategy implements SubRegionShowable {
    protected BltSubRegionBufferStrategy(int param1Int, BufferCapabilities param1BufferCapabilities) { super(Component.this, param1Int, param1BufferCapabilities); }
    
    public void show(int param1Int1, int param1Int2, int param1Int3, int param1Int4) { showSubRegion(param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public boolean showIfNotLost(int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      if (!contentsLost()) {
        showSubRegion(param1Int1, param1Int2, param1Int3, param1Int4);
        return !contentsLost();
      } 
      return false;
    }
  }
  
  private static class DummyRequestFocusController implements RequestFocusController {
    private DummyRequestFocusController() {}
    
    public boolean acceptRequestFocus(Component param1Component1, Component param1Component2, boolean param1Boolean1, boolean param1Boolean2, CausedFocusEvent.Cause param1Cause) { return true; }
  }
  
  protected class FlipBufferStrategy extends BufferStrategy {
    protected int numBuffers;
    
    protected BufferCapabilities caps;
    
    protected Image drawBuffer;
    
    protected VolatileImage drawVBuffer;
    
    protected boolean validatedContents;
    
    int width;
    
    int height;
    
    protected FlipBufferStrategy(int param1Int, BufferCapabilities param1BufferCapabilities) {
      if (!(this$0 instanceof Window) && !(this$0 instanceof Canvas))
        throw new ClassCastException("Component must be a Canvas or Window"); 
      this.numBuffers = param1Int;
      this.caps = param1BufferCapabilities;
      createBuffers(param1Int, param1BufferCapabilities);
    }
    
    protected void createBuffers(int param1Int, BufferCapabilities param1BufferCapabilities) throws AWTException {
      if (param1Int < 2)
        throw new IllegalArgumentException("Number of buffers cannot be less than two"); 
      if (Component.this.peer == null)
        throw new IllegalStateException("Component must have a valid peer"); 
      if (param1BufferCapabilities == null || !param1BufferCapabilities.isPageFlipping())
        throw new IllegalArgumentException("Page flipping capabilities must be specified"); 
      this.width = Component.this.getWidth();
      this.height = Component.this.getHeight();
      if (this.drawBuffer != null) {
        this.drawBuffer = null;
        this.drawVBuffer = null;
        destroyBuffers();
      } 
      if (param1BufferCapabilities instanceof ExtendedBufferCapabilities) {
        ExtendedBufferCapabilities extendedBufferCapabilities = (ExtendedBufferCapabilities)param1BufferCapabilities;
        if (extendedBufferCapabilities.getVSync() == ExtendedBufferCapabilities.VSyncType.VSYNC_ON && !VSyncedBSManager.vsyncAllowed(this))
          param1BufferCapabilities = extendedBufferCapabilities.derive(ExtendedBufferCapabilities.VSyncType.VSYNC_DEFAULT); 
      } 
      Component.this.peer.createBuffers(param1Int, param1BufferCapabilities);
      updateInternalBuffers();
    }
    
    private void updateInternalBuffers() {
      this.drawBuffer = getBackBuffer();
      if (this.drawBuffer instanceof VolatileImage) {
        this.drawVBuffer = (VolatileImage)this.drawBuffer;
      } else {
        this.drawVBuffer = null;
      } 
    }
    
    protected Image getBackBuffer() {
      if (Component.this.peer != null)
        return Component.this.peer.getBackBuffer(); 
      throw new IllegalStateException("Component must have a valid peer");
    }
    
    protected void flip(BufferCapabilities.FlipContents param1FlipContents) {
      if (Component.this.peer != null) {
        Image image = getBackBuffer();
        if (image != null)
          Component.this.peer.flip(0, 0, image.getWidth(null), image.getHeight(null), param1FlipContents); 
      } else {
        throw new IllegalStateException("Component must have a valid peer");
      } 
    }
    
    void flipSubRegion(int param1Int1, int param1Int2, int param1Int3, int param1Int4, BufferCapabilities.FlipContents param1FlipContents) {
      if (Component.this.peer != null) {
        Component.this.peer.flip(param1Int1, param1Int2, param1Int3, param1Int4, param1FlipContents);
      } else {
        throw new IllegalStateException("Component must have a valid peer");
      } 
    }
    
    protected void destroyBuffers() {
      VSyncedBSManager.releaseVsync(this);
      if (Component.this.peer != null) {
        Component.this.peer.destroyBuffers();
      } else {
        throw new IllegalStateException("Component must have a valid peer");
      } 
    }
    
    public BufferCapabilities getCapabilities() { return (this.caps instanceof Component.ProxyCapabilities) ? ((Component.ProxyCapabilities)this.caps).orig : this.caps; }
    
    public Graphics getDrawGraphics() {
      revalidate();
      return this.drawBuffer.getGraphics();
    }
    
    protected void revalidate() { revalidate(true); }
    
    void revalidate(boolean param1Boolean) {
      this.validatedContents = false;
      if (param1Boolean && (Component.this.getWidth() != this.width || Component.this.getHeight() != this.height)) {
        try {
          createBuffers(this.numBuffers, this.caps);
        } catch (AWTException aWTException) {}
        this.validatedContents = true;
      } 
      updateInternalBuffers();
      if (this.drawVBuffer != null) {
        GraphicsConfiguration graphicsConfiguration = Component.this.getGraphicsConfiguration_NoClientCode();
        int i = this.drawVBuffer.validate(graphicsConfiguration);
        if (i == 2) {
          try {
            createBuffers(this.numBuffers, this.caps);
          } catch (AWTException aWTException) {}
          if (this.drawVBuffer != null)
            this.drawVBuffer.validate(graphicsConfiguration); 
          this.validatedContents = true;
        } else if (i == 1) {
          this.validatedContents = true;
        } 
      } 
    }
    
    public boolean contentsLost() { return (this.drawVBuffer == null) ? false : this.drawVBuffer.contentsLost(); }
    
    public boolean contentsRestored() { return this.validatedContents; }
    
    public void show() { flip(this.caps.getFlipContents()); }
    
    void showSubRegion(int param1Int1, int param1Int2, int param1Int3, int param1Int4) { flipSubRegion(param1Int1, param1Int2, param1Int3, param1Int4, this.caps.getFlipContents()); }
    
    public void dispose() {
      if (Component.this.bufferStrategy == this) {
        Component.this.bufferStrategy = null;
        if (Component.this.peer != null)
          destroyBuffers(); 
      } 
    }
  }
  
  private class FlipSubRegionBufferStrategy extends FlipBufferStrategy implements SubRegionShowable {
    protected FlipSubRegionBufferStrategy(int param1Int, BufferCapabilities param1BufferCapabilities) { super(Component.this, param1Int, param1BufferCapabilities); }
    
    public void show(int param1Int1, int param1Int2, int param1Int3, int param1Int4) { showSubRegion(param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public boolean showIfNotLost(int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      if (!contentsLost()) {
        showSubRegion(param1Int1, param1Int2, param1Int3, param1Int4);
        return !contentsLost();
      } 
      return false;
    }
  }
  
  private class ProxyCapabilities extends ExtendedBufferCapabilities {
    private BufferCapabilities orig;
    
    private ProxyCapabilities(BufferCapabilities param1BufferCapabilities) {
      super(param1BufferCapabilities.getFrontBufferCapabilities(), param1BufferCapabilities.getBackBufferCapabilities(), (param1BufferCapabilities.getFlipContents() == BufferCapabilities.FlipContents.BACKGROUND) ? BufferCapabilities.FlipContents.BACKGROUND : BufferCapabilities.FlipContents.COPIED);
      this.orig = param1BufferCapabilities;
    }
  }
  
  private class SingleBufferStrategy extends BufferStrategy {
    private BufferCapabilities caps;
    
    public SingleBufferStrategy(BufferCapabilities param1BufferCapabilities) { this.caps = param1BufferCapabilities; }
    
    public BufferCapabilities getCapabilities() { return this.caps; }
    
    public Graphics getDrawGraphics() { return Component.this.getGraphics(); }
    
    public boolean contentsLost() { return false; }
    
    public boolean contentsRestored() { return false; }
    
    public void show() {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\Component.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */