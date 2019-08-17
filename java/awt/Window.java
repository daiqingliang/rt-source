package java.awt;

import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.im.InputContext;
import java.awt.image.BufferStrategy;
import java.awt.peer.WindowPeer;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JRootPane;
import javax.swing.RootPaneContainer;
import sun.awt.AWTAccessor;
import sun.awt.AppContext;
import sun.awt.CausedFocusEvent;
import sun.awt.SunToolkit;
import sun.awt.util.IdentityArrayList;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;
import sun.java2d.pipe.Region;
import sun.security.action.GetPropertyAction;
import sun.security.util.SecurityConstants;
import sun.util.logging.PlatformLogger;

public class Window extends Container implements Accessible {
  String warningString;
  
  List<Image> icons;
  
  private Component temporaryLostComponent;
  
  static boolean systemSyncLWRequests = false;
  
  boolean syncLWRequests = false;
  
  boolean beforeFirstShow = true;
  
  private boolean disposing = false;
  
  WindowDisposerRecord disposerRecord = null;
  
  static final int OPENED = 1;
  
  int state;
  
  private boolean alwaysOnTop;
  
  private static final IdentityArrayList<Window> allWindows = new IdentityArrayList();
  
  Vector<WeakReference<Window>> ownedWindowList = new Vector();
  
  private WeakReference<Window> weakThis;
  
  boolean showWithParent;
  
  Dialog modalBlocker;
  
  Dialog.ModalExclusionType modalExclusionType;
  
  WindowListener windowListener;
  
  WindowStateListener windowStateListener;
  
  WindowFocusListener windowFocusListener;
  
  InputContext inputContext;
  
  private Object inputContextLock = new Object();
  
  private FocusManager focusMgr;
  
  private boolean focusableWindowState = true;
  
  boolean isInShow = false;
  
  private Shape shape = null;
  
  private static final String base = "win";
  
  private static int nameCounter = 0;
  
  private static final long serialVersionUID = 4497834738069338734L;
  
  private static final PlatformLogger log = PlatformLogger.getLogger("java.awt.Window");
  
  private static final boolean locationByPlatformProp;
  
  boolean isTrayIconWindow = false;
  
  private double securityWarningPointX = 2.0D;
  
  private double securityWarningPointY = 0.0D;
  
  private float securityWarningAlignmentX = 1.0F;
  
  private float securityWarningAlignmentY = 0.0F;
  
  Object anchor = new Object();
  
  private static final AtomicBoolean beforeFirstWindowShown;
  
  private Type type = Type.NORMAL;
  
  private int windowSerializedDataVersion = 2;
  
  private static native void initIDs();
  
  Window(GraphicsConfiguration paramGraphicsConfiguration) { init(paramGraphicsConfiguration); }
  
  private GraphicsConfiguration initGC(GraphicsConfiguration paramGraphicsConfiguration) {
    GraphicsEnvironment.checkHeadless();
    if (paramGraphicsConfiguration == null)
      paramGraphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration(); 
    setGraphicsConfiguration(paramGraphicsConfiguration);
    return paramGraphicsConfiguration;
  }
  
  private void init(GraphicsConfiguration paramGraphicsConfiguration) {
    GraphicsEnvironment.checkHeadless();
    this.syncLWRequests = systemSyncLWRequests;
    this.weakThis = new WeakReference(this);
    addToWindowList();
    setWarningString();
    this.cursor = Cursor.getPredefinedCursor(0);
    this.visible = false;
    paramGraphicsConfiguration = initGC(paramGraphicsConfiguration);
    if (paramGraphicsConfiguration.getDevice().getType() != 0)
      throw new IllegalArgumentException("not a screen device"); 
    setLayout(new BorderLayout());
    Rectangle rectangle = paramGraphicsConfiguration.getBounds();
    Insets insets = getToolkit().getScreenInsets(paramGraphicsConfiguration);
    int i = getX() + rectangle.x + insets.left;
    int j = getY() + rectangle.y + insets.top;
    if (i != this.x || j != this.y) {
      setLocation(i, j);
      setLocationByPlatform(locationByPlatformProp);
    } 
    this.modalExclusionType = Dialog.ModalExclusionType.NO_EXCLUDE;
    this.disposerRecord = new WindowDisposerRecord(this.appContext, this);
    Disposer.addRecord(this.anchor, this.disposerRecord);
    SunToolkit.checkAndSetPolicy(this);
  }
  
  Window() {
    GraphicsEnvironment.checkHeadless();
    init((GraphicsConfiguration)null);
  }
  
  public Window(Frame paramFrame) {
    this((paramFrame == null) ? (GraphicsConfiguration)null : paramFrame.getGraphicsConfiguration());
    ownedInit(paramFrame);
  }
  
  public Window(Window paramWindow) {
    this((paramWindow == null) ? (GraphicsConfiguration)null : paramWindow.getGraphicsConfiguration());
    ownedInit(paramWindow);
  }
  
  public Window(Window paramWindow, GraphicsConfiguration paramGraphicsConfiguration) {
    this(paramGraphicsConfiguration);
    ownedInit(paramWindow);
  }
  
  private void ownedInit(Window paramWindow) {
    this.parent = paramWindow;
    if (paramWindow != null) {
      paramWindow.addOwnedWindow(this.weakThis);
      if (paramWindow.isAlwaysOnTop())
        try {
          setAlwaysOnTop(true);
        } catch (SecurityException securityException) {} 
    } 
    this.disposerRecord.updateOwner();
  }
  
  String constructComponentName() {
    synchronized (Window.class) {
      return "win" + nameCounter++;
    } 
  }
  
  public List<Image> getIconImages() {
    List list = this.icons;
    return (list == null || list.size() == 0) ? new ArrayList() : new ArrayList(list);
  }
  
  public void setIconImages(List<? extends Image> paramList) {
    this.icons = (paramList == null) ? new ArrayList() : new ArrayList(paramList);
    WindowPeer windowPeer = (WindowPeer)this.peer;
    if (windowPeer != null)
      windowPeer.updateIconImages(); 
    firePropertyChange("iconImage", null, null);
  }
  
  public void setIconImage(Image paramImage) {
    ArrayList arrayList = new ArrayList();
    if (paramImage != null)
      arrayList.add(paramImage); 
    setIconImages(arrayList);
  }
  
  public void addNotify() {
    synchronized (getTreeLock()) {
      Container container = this.parent;
      if (container != null && container.getPeer() == null)
        container.addNotify(); 
      if (this.peer == null)
        this.peer = getToolkit().createWindow(this); 
      synchronized (allWindows) {
        allWindows.add(this);
      } 
      super.addNotify();
    } 
  }
  
  public void removeNotify() {
    synchronized (getTreeLock()) {
      synchronized (allWindows) {
        allWindows.remove(this);
      } 
      super.removeNotify();
    } 
  }
  
  public void pack() {
    Container container = this.parent;
    if (container != null && container.getPeer() == null)
      container.addNotify(); 
    if (this.peer == null)
      addNotify(); 
    Dimension dimension = getPreferredSize();
    if (this.peer != null)
      setClientSize(dimension.width, dimension.height); 
    if (this.beforeFirstShow)
      this.isPacked = true; 
    validateUnconditionally();
  }
  
  public void setMinimumSize(Dimension paramDimension) {
    synchronized (getTreeLock()) {
      super.setMinimumSize(paramDimension);
      Dimension dimension = getSize();
      if (isMinimumSizeSet() && (dimension.width < paramDimension.width || dimension.height < paramDimension.height)) {
        int i = Math.max(this.width, paramDimension.width);
        int j = Math.max(this.height, paramDimension.height);
        setSize(i, j);
      } 
      if (this.peer != null)
        ((WindowPeer)this.peer).updateMinimumSize(); 
    } 
  }
  
  public void setSize(Dimension paramDimension) { super.setSize(paramDimension); }
  
  public void setSize(int paramInt1, int paramInt2) { super.setSize(paramInt1, paramInt2); }
  
  public void setLocation(int paramInt1, int paramInt2) { super.setLocation(paramInt1, paramInt2); }
  
  public void setLocation(Point paramPoint) { super.setLocation(paramPoint); }
  
  @Deprecated
  public void reshape(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (isMinimumSizeSet()) {
      Dimension dimension = getMinimumSize();
      if (paramInt3 < dimension.width)
        paramInt3 = dimension.width; 
      if (paramInt4 < dimension.height)
        paramInt4 = dimension.height; 
    } 
    super.reshape(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  void setClientSize(int paramInt1, int paramInt2) {
    synchronized (getTreeLock()) {
      setBoundsOp(4);
      setBounds(this.x, this.y, paramInt1, paramInt2);
    } 
  }
  
  final void closeSplashScreen() {
    if (this.isTrayIconWindow)
      return; 
    if (beforeFirstWindowShown.getAndSet(false)) {
      SunToolkit.closeSplashScreen();
      SplashScreen.markClosed();
    } 
  }
  
  public void setVisible(boolean paramBoolean) { super.setVisible(paramBoolean); }
  
  @Deprecated
  public void show() {
    if (this.peer == null)
      addNotify(); 
    validateUnconditionally();
    this.isInShow = true;
    if (this.visible) {
      toFront();
    } else {
      this.beforeFirstShow = false;
      closeSplashScreen();
      Dialog.checkShouldBeBlocked(this);
      super.show();
      this.locationByPlatform = false;
      for (byte b = 0; b < this.ownedWindowList.size(); b++) {
        Window window = (Window)((WeakReference)this.ownedWindowList.elementAt(b)).get();
        if (window != null && window.showWithParent) {
          window.show();
          window.showWithParent = false;
        } 
      } 
      if (!isModalBlocked()) {
        updateChildrenBlocking();
      } else {
        this.modalBlocker.toFront_NoClientCode();
      } 
      if (this instanceof Frame || this instanceof Dialog)
        updateChildFocusableWindowState(this); 
    } 
    this.isInShow = false;
    if ((this.state & true) == 0) {
      postWindowEvent(200);
      this.state |= 0x1;
    } 
  }
  
  static void updateChildFocusableWindowState(Window paramWindow) {
    if (paramWindow.getPeer() != null && paramWindow.isShowing())
      ((WindowPeer)paramWindow.getPeer()).updateFocusableWindowState(); 
    for (byte b = 0; b < paramWindow.ownedWindowList.size(); b++) {
      Window window = (Window)((WeakReference)paramWindow.ownedWindowList.elementAt(b)).get();
      if (window != null)
        updateChildFocusableWindowState(window); 
    } 
  }
  
  void postWindowEvent(int paramInt) {
    if (this.windowListener != null || (this.eventMask & 0x40L) != 0L || Toolkit.enabledOnToolkit(64L)) {
      WindowEvent windowEvent = new WindowEvent(this, paramInt);
      Toolkit.getEventQueue().postEvent(windowEvent);
    } 
  }
  
  @Deprecated
  public void hide() {
    synchronized (this.ownedWindowList) {
      for (byte b = 0; b < this.ownedWindowList.size(); b++) {
        Window window = (Window)((WeakReference)this.ownedWindowList.elementAt(b)).get();
        if (window != null && window.visible) {
          window.hide();
          window.showWithParent = true;
        } 
      } 
    } 
    if (isModalBlocked())
      this.modalBlocker.unblockWindow(this); 
    super.hide();
    this.locationByPlatform = false;
  }
  
  final void clearMostRecentFocusOwnerOnHide() {}
  
  public void dispose() { doDispose(); }
  
  void disposeImpl() {
    dispose();
    if (getPeer() != null)
      doDispose(); 
  }
  
  void doDispose() {
    boolean bool = isDisplayable();
    class DisposeAction implements Runnable {
      public void run() {
        Window.this.disposing = true;
        try {
          Object[] arrayOfObject;
          GraphicsDevice graphicsDevice = Window.this.getGraphicsConfiguration().getDevice();
          if (graphicsDevice.getFullScreenWindow() == Window.this)
            graphicsDevice.setFullScreenWindow(null); 
          synchronized (Window.this.ownedWindowList) {
            arrayOfObject = new Object[Window.this.ownedWindowList.size()];
            Window.this.ownedWindowList.copyInto(arrayOfObject);
          } 
          for (byte b = 0; b < arrayOfObject.length; b++) {
            Window window = (Window)((WeakReference)arrayOfObject[b]).get();
            if (window != null)
              window.disposeImpl(); 
          } 
          Window.this.hide();
          Window.this.beforeFirstShow = true;
          Window.this.removeNotify();
          synchronized (Window.this.inputContextLock) {
            if (Window.this.inputContext != null) {
              Window.this.inputContext.dispose();
              Window.this.inputContext = null;
            } 
          } 
          Window.this.clearCurrentFocusCycleRootOnHide();
        } finally {
          Window.this.disposing = false;
        } 
      }
    };
    DisposeAction disposeAction = new DisposeAction();
    if (EventQueue.isDispatchThread()) {
      disposeAction.run();
    } else {
      try {
        EventQueue.invokeAndWait(this, disposeAction);
      } catch (InterruptedException interruptedException) {
        System.err.println("Disposal was interrupted:");
        interruptedException.printStackTrace();
      } catch (InvocationTargetException invocationTargetException) {
        System.err.println("Exception during disposal:");
        invocationTargetException.printStackTrace();
      } 
    } 
    if (bool)
      postWindowEvent(202); 
  }
  
  void adjustListeningChildrenOnParent(long paramLong, int paramInt) {}
  
  void adjustDecendantsOnParent(int paramInt) {}
  
  public void toFront() { toFront_NoClientCode(); }
  
  final void toFront_NoClientCode() {
    if (this.visible) {
      WindowPeer windowPeer = (WindowPeer)this.peer;
      if (windowPeer != null)
        windowPeer.toFront(); 
      if (isModalBlocked())
        this.modalBlocker.toFront_NoClientCode(); 
    } 
  }
  
  public void toBack() { toBack_NoClientCode(); }
  
  final void toBack_NoClientCode() {
    if (isAlwaysOnTop())
      try {
        setAlwaysOnTop(false);
      } catch (SecurityException securityException) {} 
    if (this.visible) {
      WindowPeer windowPeer = (WindowPeer)this.peer;
      if (windowPeer != null)
        windowPeer.toBack(); 
    } 
  }
  
  public Toolkit getToolkit() { return Toolkit.getDefaultToolkit(); }
  
  public final String getWarningString() { return this.warningString; }
  
  private void setWarningString() {
    this.warningString = null;
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      try {
        securityManager.checkPermission(SecurityConstants.AWT.TOPLEVEL_WINDOW_PERMISSION);
      } catch (SecurityException securityException) {
        this.warningString = (String)AccessController.doPrivileged(new GetPropertyAction("awt.appletWarning", "Java Applet Window"));
      }  
  }
  
  public Locale getLocale() { return (this.locale == null) ? Locale.getDefault() : this.locale; }
  
  public InputContext getInputContext() {
    synchronized (this.inputContextLock) {
      if (this.inputContext == null)
        this.inputContext = InputContext.getInstance(); 
    } 
    return this.inputContext;
  }
  
  public void setCursor(Cursor paramCursor) {
    if (paramCursor == null)
      paramCursor = Cursor.getPredefinedCursor(0); 
    super.setCursor(paramCursor);
  }
  
  public Window getOwner() { return getOwner_NoClientCode(); }
  
  final Window getOwner_NoClientCode() { return (Window)this.parent; }
  
  public Window[] getOwnedWindows() { return getOwnedWindows_NoClientCode(); }
  
  final Window[] getOwnedWindows_NoClientCode() {
    Window[] arrayOfWindow;
    synchronized (this.ownedWindowList) {
      int i = this.ownedWindowList.size();
      byte b1 = 0;
      Window[] arrayOfWindow1 = new Window[i];
      for (byte b2 = 0; b2 < i; b2++) {
        arrayOfWindow1[b1] = (Window)((WeakReference)this.ownedWindowList.elementAt(b2)).get();
        if (arrayOfWindow1[b1] != null)
          b1++; 
      } 
      if (i != b1) {
        arrayOfWindow = (Window[])Arrays.copyOf(arrayOfWindow1, b1);
      } else {
        arrayOfWindow = arrayOfWindow1;
      } 
    } 
    return arrayOfWindow;
  }
  
  boolean isModalBlocked() { return (this.modalBlocker != null); }
  
  void setModalBlocked(Dialog paramDialog, boolean paramBoolean1, boolean paramBoolean2) {
    this.modalBlocker = paramBoolean1 ? paramDialog : null;
    if (paramBoolean2) {
      WindowPeer windowPeer = (WindowPeer)this.peer;
      if (windowPeer != null)
        windowPeer.setModalBlocked(paramDialog, paramBoolean1); 
    } 
  }
  
  Dialog getModalBlocker() { return this.modalBlocker; }
  
  static IdentityArrayList<Window> getAllWindows() {
    synchronized (allWindows) {
      IdentityArrayList identityArrayList = new IdentityArrayList();
      identityArrayList.addAll(allWindows);
      return identityArrayList;
    } 
  }
  
  static IdentityArrayList<Window> getAllUnblockedWindows() {
    synchronized (allWindows) {
      IdentityArrayList identityArrayList = new IdentityArrayList();
      for (byte b = 0; b < allWindows.size(); b++) {
        Window window = (Window)allWindows.get(b);
        if (!window.isModalBlocked())
          identityArrayList.add(window); 
      } 
      return identityArrayList;
    } 
  }
  
  private static Window[] getWindows(AppContext paramAppContext) {
    synchronized (Window.class) {
      Window[] arrayOfWindow;
      Vector vector = (Vector)paramAppContext.get(Window.class);
      if (vector != null) {
        int i = vector.size();
        byte b1 = 0;
        Window[] arrayOfWindow1 = new Window[i];
        for (byte b2 = 0; b2 < i; b2++) {
          Window window = (Window)((WeakReference)vector.get(b2)).get();
          if (window != null)
            arrayOfWindow1[b1++] = window; 
        } 
        if (i != b1) {
          arrayOfWindow = (Window[])Arrays.copyOf(arrayOfWindow1, b1);
        } else {
          arrayOfWindow = arrayOfWindow1;
        } 
      } else {
        arrayOfWindow = new Window[0];
      } 
      return arrayOfWindow;
    } 
  }
  
  public static Window[] getWindows() { return getWindows(AppContext.getAppContext()); }
  
  public static Window[] getOwnerlessWindows() {
    Window[] arrayOfWindow1 = getWindows();
    byte b1 = 0;
    for (Window window : arrayOfWindow1) {
      if (window.getOwner() == null)
        b1++; 
    } 
    Window[] arrayOfWindow2 = new Window[b1];
    byte b2 = 0;
    for (Window window : arrayOfWindow1) {
      if (window.getOwner() == null)
        arrayOfWindow2[b2++] = window; 
    } 
    return arrayOfWindow2;
  }
  
  Window getDocumentRoot() {
    synchronized (getTreeLock()) {
      Window window;
      for (window = this; window.getOwner() != null; window = window.getOwner());
      return window;
    } 
  }
  
  public void setModalExclusionType(Dialog.ModalExclusionType paramModalExclusionType) {
    if (paramModalExclusionType == null)
      paramModalExclusionType = Dialog.ModalExclusionType.NO_EXCLUDE; 
    if (!Toolkit.getDefaultToolkit().isModalExclusionTypeSupported(paramModalExclusionType))
      paramModalExclusionType = Dialog.ModalExclusionType.NO_EXCLUDE; 
    if (this.modalExclusionType == paramModalExclusionType)
      return; 
    if (paramModalExclusionType == Dialog.ModalExclusionType.TOOLKIT_EXCLUDE) {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null)
        securityManager.checkPermission(SecurityConstants.AWT.TOOLKIT_MODALITY_PERMISSION); 
    } 
    this.modalExclusionType = paramModalExclusionType;
  }
  
  public Dialog.ModalExclusionType getModalExclusionType() { return this.modalExclusionType; }
  
  boolean isModalExcluded(Dialog.ModalExclusionType paramModalExclusionType) {
    if (this.modalExclusionType != null && this.modalExclusionType.compareTo(paramModalExclusionType) >= 0)
      return true; 
    Window window = getOwner_NoClientCode();
    return (window != null && window.isModalExcluded(paramModalExclusionType));
  }
  
  void updateChildrenBlocking() {
    Vector vector = new Vector();
    Window[] arrayOfWindow = getOwnedWindows();
    byte b;
    for (b = 0; b < arrayOfWindow.length; b++)
      vector.add(arrayOfWindow[b]); 
    for (b = 0; b < vector.size(); b++) {
      Window window = (Window)vector.get(b);
      if (window.isVisible()) {
        if (window.isModalBlocked()) {
          Dialog dialog = window.getModalBlocker();
          dialog.unblockWindow(window);
        } 
        Dialog.checkShouldBeBlocked(window);
        Window[] arrayOfWindow1 = window.getOwnedWindows();
        for (byte b1 = 0; b1 < arrayOfWindow1.length; b1++)
          vector.add(arrayOfWindow1[b1]); 
      } 
    } 
  }
  
  public void addWindowListener(WindowListener paramWindowListener) {
    if (paramWindowListener == null)
      return; 
    this.newEventsOnly = true;
    this.windowListener = AWTEventMulticaster.add(this.windowListener, paramWindowListener);
  }
  
  public void addWindowStateListener(WindowStateListener paramWindowStateListener) {
    if (paramWindowStateListener == null)
      return; 
    this.windowStateListener = AWTEventMulticaster.add(this.windowStateListener, paramWindowStateListener);
    this.newEventsOnly = true;
  }
  
  public void addWindowFocusListener(WindowFocusListener paramWindowFocusListener) {
    if (paramWindowFocusListener == null)
      return; 
    this.windowFocusListener = AWTEventMulticaster.add(this.windowFocusListener, paramWindowFocusListener);
    this.newEventsOnly = true;
  }
  
  public void removeWindowListener(WindowListener paramWindowListener) {
    if (paramWindowListener == null)
      return; 
    this.windowListener = AWTEventMulticaster.remove(this.windowListener, paramWindowListener);
  }
  
  public void removeWindowStateListener(WindowStateListener paramWindowStateListener) {
    if (paramWindowStateListener == null)
      return; 
    this.windowStateListener = AWTEventMulticaster.remove(this.windowStateListener, paramWindowStateListener);
  }
  
  public void removeWindowFocusListener(WindowFocusListener paramWindowFocusListener) {
    if (paramWindowFocusListener == null)
      return; 
    this.windowFocusListener = AWTEventMulticaster.remove(this.windowFocusListener, paramWindowFocusListener);
  }
  
  public WindowListener[] getWindowListeners() { return (WindowListener[])getListeners(WindowListener.class); }
  
  public WindowFocusListener[] getWindowFocusListeners() { return (WindowFocusListener[])getListeners(WindowFocusListener.class); }
  
  public WindowStateListener[] getWindowStateListeners() { return (WindowStateListener[])getListeners(WindowStateListener.class); }
  
  public <T extends java.util.EventListener> T[] getListeners(Class<T> paramClass) {
    WindowListener windowListener1 = null;
    if (paramClass == WindowFocusListener.class) {
      windowListener1 = this.windowFocusListener;
    } else if (paramClass == WindowStateListener.class) {
      WindowStateListener windowStateListener1 = this.windowStateListener;
    } else if (paramClass == WindowListener.class) {
      windowListener1 = this.windowListener;
    } else {
      return (T[])super.getListeners(paramClass);
    } 
    return (T[])AWTEventMulticaster.getListeners(windowListener1, paramClass);
  }
  
  boolean eventEnabled(AWTEvent paramAWTEvent) {
    switch (paramAWTEvent.id) {
      case 200:
      case 201:
      case 202:
      case 203:
      case 204:
      case 205:
      case 206:
        return ((this.eventMask & 0x40L) != 0L || this.windowListener != null);
      case 207:
      case 208:
        return ((this.eventMask & 0x80000L) != 0L || this.windowFocusListener != null);
      case 209:
        return ((this.eventMask & 0x40000L) != 0L || this.windowStateListener != null);
    } 
    return super.eventEnabled(paramAWTEvent);
  }
  
  protected void processEvent(AWTEvent paramAWTEvent) {
    if (paramAWTEvent instanceof WindowEvent) {
      switch (paramAWTEvent.getID()) {
        case 200:
        case 201:
        case 202:
        case 203:
        case 204:
        case 205:
        case 206:
          processWindowEvent((WindowEvent)paramAWTEvent);
          break;
        case 207:
        case 208:
          processWindowFocusEvent((WindowEvent)paramAWTEvent);
          break;
        case 209:
          processWindowStateEvent((WindowEvent)paramAWTEvent);
          break;
      } 
      return;
    } 
    super.processEvent(paramAWTEvent);
  }
  
  protected void processWindowEvent(WindowEvent paramWindowEvent) {
    WindowListener windowListener1 = this.windowListener;
    if (windowListener1 != null)
      switch (paramWindowEvent.getID()) {
        case 200:
          windowListener1.windowOpened(paramWindowEvent);
          break;
        case 201:
          windowListener1.windowClosing(paramWindowEvent);
          break;
        case 202:
          windowListener1.windowClosed(paramWindowEvent);
          break;
        case 203:
          windowListener1.windowIconified(paramWindowEvent);
          break;
        case 204:
          windowListener1.windowDeiconified(paramWindowEvent);
          break;
        case 205:
          windowListener1.windowActivated(paramWindowEvent);
          break;
        case 206:
          windowListener1.windowDeactivated(paramWindowEvent);
          break;
      }  
  }
  
  protected void processWindowFocusEvent(WindowEvent paramWindowEvent) {
    WindowFocusListener windowFocusListener1 = this.windowFocusListener;
    if (windowFocusListener1 != null)
      switch (paramWindowEvent.getID()) {
        case 207:
          windowFocusListener1.windowGainedFocus(paramWindowEvent);
          break;
        case 208:
          windowFocusListener1.windowLostFocus(paramWindowEvent);
          break;
      }  
  }
  
  protected void processWindowStateEvent(WindowEvent paramWindowEvent) {
    WindowStateListener windowStateListener1 = this.windowStateListener;
    if (windowStateListener1 != null)
      switch (paramWindowEvent.getID()) {
        case 209:
          windowStateListener1.windowStateChanged(paramWindowEvent);
          break;
      }  
  }
  
  void preProcessKeyEvent(KeyEvent paramKeyEvent) {
    if (paramKeyEvent.isActionKey() && paramKeyEvent.getKeyCode() == 112 && paramKeyEvent.isControlDown() && paramKeyEvent.isShiftDown() && paramKeyEvent.getID() == 401)
      list(System.out, 0); 
  }
  
  void postProcessKeyEvent(KeyEvent paramKeyEvent) {}
  
  public final void setAlwaysOnTop(boolean paramBoolean) {
    boolean bool;
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(SecurityConstants.AWT.SET_WINDOW_ALWAYS_ON_TOP_PERMISSION); 
    synchronized (this) {
      bool = this.alwaysOnTop;
      this.alwaysOnTop = paramBoolean;
    } 
    if (bool != paramBoolean) {
      if (isAlwaysOnTopSupported()) {
        WindowPeer windowPeer = (WindowPeer)this.peer;
        synchronized (getTreeLock()) {
          if (windowPeer != null)
            windowPeer.updateAlwaysOnTopState(); 
        } 
      } 
      firePropertyChange("alwaysOnTop", bool, paramBoolean);
    } 
    setOwnedWindowsAlwaysOnTop(paramBoolean);
  }
  
  private void setOwnedWindowsAlwaysOnTop(boolean paramBoolean) {
    WeakReference[] arrayOfWeakReference;
    synchronized (this.ownedWindowList) {
      arrayOfWeakReference = new WeakReference[this.ownedWindowList.size()];
      this.ownedWindowList.copyInto(arrayOfWeakReference);
    } 
    for (WeakReference weakReference : arrayOfWeakReference) {
      Window window = (Window)weakReference.get();
      if (window != null)
        try {
          window.setAlwaysOnTop(paramBoolean);
        } catch (SecurityException securityException) {} 
    } 
  }
  
  public boolean isAlwaysOnTopSupported() { return Toolkit.getDefaultToolkit().isAlwaysOnTopSupported(); }
  
  public final boolean isAlwaysOnTop() { return this.alwaysOnTop; }
  
  public Component getFocusOwner() { return isFocused() ? KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() : null; }
  
  public Component getMostRecentFocusOwner() {
    if (isFocused())
      return getFocusOwner(); 
    Component component = KeyboardFocusManager.getMostRecentFocusOwner(this);
    return (component != null) ? component : (isFocusableWindow() ? getFocusTraversalPolicy().getInitialComponent(this) : null);
  }
  
  public boolean isActive() { return (KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow() == this); }
  
  public boolean isFocused() { return (KeyboardFocusManager.getCurrentKeyboardFocusManager().getGlobalFocusedWindow() == this); }
  
  public Set<AWTKeyStroke> getFocusTraversalKeys(int paramInt) {
    if (paramInt < 0 || paramInt >= 4)
      throw new IllegalArgumentException("invalid focus traversal key identifier"); 
    Set set = (this.focusTraversalKeys != null) ? this.focusTraversalKeys[paramInt] : null;
    return (set != null) ? set : KeyboardFocusManager.getCurrentKeyboardFocusManager().getDefaultFocusTraversalKeys(paramInt);
  }
  
  public final void setFocusCycleRoot(boolean paramBoolean) {}
  
  public final boolean isFocusCycleRoot() { return true; }
  
  public final Container getFocusCycleRootAncestor() { return null; }
  
  public final boolean isFocusableWindow() {
    if (!getFocusableWindowState())
      return false; 
    if (this instanceof Frame || this instanceof Dialog)
      return true; 
    if (getFocusTraversalPolicy().getDefaultComponent(this) == null)
      return false; 
    for (Window window = getOwner(); window != null; window = window.getOwner()) {
      if (window instanceof Frame || window instanceof Dialog)
        return window.isShowing(); 
    } 
    return false;
  }
  
  public boolean getFocusableWindowState() { return this.focusableWindowState; }
  
  public void setFocusableWindowState(boolean paramBoolean) {
    boolean bool;
    synchronized (this) {
      bool = this.focusableWindowState;
      this.focusableWindowState = paramBoolean;
    } 
    WindowPeer windowPeer = (WindowPeer)this.peer;
    if (windowPeer != null)
      windowPeer.updateFocusableWindowState(); 
    firePropertyChange("focusableWindowState", bool, paramBoolean);
    if (bool && !paramBoolean && isFocused()) {
      for (Window window = getOwner(); window != null; window = window.getOwner()) {
        Component component = KeyboardFocusManager.getMostRecentFocusOwner(window);
        if (component != null && component.requestFocus(false, CausedFocusEvent.Cause.ACTIVATION))
          return; 
      } 
      KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwnerPriv();
    } 
  }
  
  public void setAutoRequestFocus(boolean paramBoolean) { this.autoRequestFocus = paramBoolean; }
  
  public boolean isAutoRequestFocus() { return this.autoRequestFocus; }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) { super.addPropertyChangeListener(paramPropertyChangeListener); }
  
  public void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener) { super.addPropertyChangeListener(paramString, paramPropertyChangeListener); }
  
  public boolean isValidateRoot() { return true; }
  
  void dispatchEventImpl(AWTEvent paramAWTEvent) {
    if (paramAWTEvent.getID() == 101) {
      invalidate();
      validate();
    } 
    super.dispatchEventImpl(paramAWTEvent);
  }
  
  @Deprecated
  public boolean postEvent(Event paramEvent) {
    if (handleEvent(paramEvent)) {
      paramEvent.consume();
      return true;
    } 
    return false;
  }
  
  public boolean isShowing() { return this.visible; }
  
  boolean isDisposing() { return this.disposing; }
  
  @Deprecated
  public void applyResourceBundle(ResourceBundle paramResourceBundle) { applyComponentOrientation(ComponentOrientation.getOrientation(paramResourceBundle)); }
  
  @Deprecated
  public void applyResourceBundle(String paramString) { applyResourceBundle(ResourceBundle.getBundle(paramString, Locale.getDefault(), ClassLoader.getSystemClassLoader())); }
  
  void addOwnedWindow(WeakReference<Window> paramWeakReference) {
    if (paramWeakReference != null)
      synchronized (this.ownedWindowList) {
        if (!this.ownedWindowList.contains(paramWeakReference))
          this.ownedWindowList.addElement(paramWeakReference); 
      }  
  }
  
  void removeOwnedWindow(WeakReference<Window> paramWeakReference) {
    if (paramWeakReference != null)
      this.ownedWindowList.removeElement(paramWeakReference); 
  }
  
  void connectOwnedWindow(Window paramWindow) {
    paramWindow.parent = this;
    addOwnedWindow(paramWindow.weakThis);
    paramWindow.disposerRecord.updateOwner();
  }
  
  private void addToWindowList() {
    synchronized (Window.class) {
      Vector vector = (Vector)this.appContext.get(Window.class);
      if (vector == null) {
        vector = new Vector();
        this.appContext.put(Window.class, vector);
      } 
      vector.add(this.weakThis);
    } 
  }
  
  private static void removeFromWindowList(AppContext paramAppContext, WeakReference<Window> paramWeakReference) {
    synchronized (Window.class) {
      Vector vector = (Vector)paramAppContext.get(Window.class);
      if (vector != null)
        vector.remove(paramWeakReference); 
    } 
  }
  
  private void removeFromWindowList() { removeFromWindowList(this.appContext, this.weakThis); }
  
  public void setType(Type paramType) {
    if (paramType == null)
      throw new IllegalArgumentException("type should not be null."); 
    synchronized (getTreeLock()) {
      if (isDisplayable())
        throw new IllegalComponentStateException("The window is displayable."); 
      synchronized (getObjectLock()) {
        this.type = paramType;
      } 
    } 
  }
  
  public Type getType() {
    synchronized (getObjectLock()) {
      return this.type;
    } 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    synchronized (this) {
      this.focusMgr = new FocusManager();
      this.focusMgr.focusRoot = this;
      this.focusMgr.focusOwner = getMostRecentFocusOwner();
      paramObjectOutputStream.defaultWriteObject();
      this.focusMgr = null;
      AWTEventMulticaster.save(paramObjectOutputStream, "windowL", this.windowListener);
      AWTEventMulticaster.save(paramObjectOutputStream, "windowFocusL", this.windowFocusListener);
      AWTEventMulticaster.save(paramObjectOutputStream, "windowStateL", this.windowStateListener);
    } 
    paramObjectOutputStream.writeObject(null);
    synchronized (this.ownedWindowList) {
      for (byte b = 0; b < this.ownedWindowList.size(); b++) {
        Window window = (Window)((WeakReference)this.ownedWindowList.elementAt(b)).get();
        if (window != null) {
          paramObjectOutputStream.writeObject("ownedL");
          paramObjectOutputStream.writeObject(window);
        } 
      } 
    } 
    paramObjectOutputStream.writeObject(null);
    if (this.icons != null)
      for (Image image : this.icons) {
        if (image instanceof java.io.Serializable)
          paramObjectOutputStream.writeObject(image); 
      }  
    paramObjectOutputStream.writeObject(null);
  }
  
  private void initDeserializedWindow() {
    setWarningString();
    this.inputContextLock = new Object();
    this.visible = false;
    this.weakThis = new WeakReference(this);
    this.anchor = new Object();
    this.disposerRecord = new WindowDisposerRecord(this.appContext, this);
    Disposer.addRecord(this.anchor, this.disposerRecord);
    addToWindowList();
    initGC(null);
    this.ownedWindowList = new Vector();
  }
  
  private void deserializeResources(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException, HeadlessException {
    if (this.windowSerializedDataVersion < 2) {
      if (this.focusMgr != null && this.focusMgr.focusOwner != null)
        KeyboardFocusManager.setMostRecentFocusOwner(this, this.focusMgr.focusOwner); 
      this.focusableWindowState = true;
    } 
    Object object;
    while (null != (object = paramObjectInputStream.readObject())) {
      String str = ((String)object).intern();
      if ("windowL" == str) {
        addWindowListener((WindowListener)paramObjectInputStream.readObject());
        continue;
      } 
      if ("windowFocusL" == str) {
        addWindowFocusListener((WindowFocusListener)paramObjectInputStream.readObject());
        continue;
      } 
      if ("windowStateL" == str) {
        addWindowStateListener((WindowStateListener)paramObjectInputStream.readObject());
        continue;
      } 
      paramObjectInputStream.readObject();
    } 
    try {
      while (null != (object = paramObjectInputStream.readObject())) {
        String str = ((String)object).intern();
        if ("ownedL" == str) {
          connectOwnedWindow((Window)paramObjectInputStream.readObject());
          continue;
        } 
        paramObjectInputStream.readObject();
      } 
      Object object1 = paramObjectInputStream.readObject();
      this.icons = new ArrayList();
      while (object1 != null) {
        if (object1 instanceof Image)
          this.icons.add((Image)object1); 
        object1 = paramObjectInputStream.readObject();
      } 
    } catch (OptionalDataException optionalDataException) {}
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException, HeadlessException {
    GraphicsEnvironment.checkHeadless();
    initDeserializedWindow();
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    this.syncLWRequests = getField.get("syncLWRequests", systemSyncLWRequests);
    this.state = getField.get("state", 0);
    this.focusableWindowState = getField.get("focusableWindowState", true);
    this.windowSerializedDataVersion = getField.get("windowSerializedDataVersion", 1);
    this.locationByPlatform = getField.get("locationByPlatform", locationByPlatformProp);
    this.focusMgr = (FocusManager)getField.get("focusMgr", null);
    Dialog.ModalExclusionType modalExclusionType1 = (Dialog.ModalExclusionType)getField.get("modalExclusionType", Dialog.ModalExclusionType.NO_EXCLUDE);
    setModalExclusionType(modalExclusionType1);
    boolean bool = getField.get("alwaysOnTop", false);
    if (bool)
      setAlwaysOnTop(bool); 
    this.shape = (Shape)getField.get("shape", null);
    this.opacity = Float.valueOf(getField.get("opacity", 1.0F)).floatValue();
    this.securityWarningWidth = 0;
    this.securityWarningHeight = 0;
    this.securityWarningPointX = 2.0D;
    this.securityWarningPointY = 0.0D;
    this.securityWarningAlignmentX = 1.0F;
    this.securityWarningAlignmentY = 0.0F;
    deserializeResources(paramObjectInputStream);
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleAWTWindow(); 
    return this.accessibleContext;
  }
  
  void setGraphicsConfiguration(GraphicsConfiguration paramGraphicsConfiguration) {
    if (paramGraphicsConfiguration == null)
      paramGraphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration(); 
    synchronized (getTreeLock()) {
      super.setGraphicsConfiguration(paramGraphicsConfiguration);
      if (log.isLoggable(PlatformLogger.Level.FINER))
        log.finer("+ Window.setGraphicsConfiguration(): new GC is \n+ " + getGraphicsConfiguration_NoClientCode() + "\n+ this is " + this); 
    } 
  }
  
  public void setLocationRelativeTo(Component paramComponent) {
    int i = 0;
    int j = 0;
    GraphicsConfiguration graphicsConfiguration = getGraphicsConfiguration_NoClientCode();
    Rectangle rectangle = graphicsConfiguration.getBounds();
    Dimension dimension = getSize();
    Window window = SunToolkit.getContainingWindow(paramComponent);
    if (paramComponent == null || window == null) {
      GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
      graphicsConfiguration = graphicsEnvironment.getDefaultScreenDevice().getDefaultConfiguration();
      rectangle = graphicsConfiguration.getBounds();
      Point point = graphicsEnvironment.getCenterPoint();
      i = point.x - dimension.width / 2;
      j = point.y - dimension.height / 2;
    } else if (!paramComponent.isShowing()) {
      graphicsConfiguration = window.getGraphicsConfiguration();
      rectangle = graphicsConfiguration.getBounds();
      i = rectangle.x + (rectangle.width - dimension.width) / 2;
      j = rectangle.y + (rectangle.height - dimension.height) / 2;
    } else {
      graphicsConfiguration = window.getGraphicsConfiguration();
      rectangle = graphicsConfiguration.getBounds();
      Dimension dimension1 = paramComponent.getSize();
      Point point = paramComponent.getLocationOnScreen();
      i = point.x + (dimension1.width - dimension.width) / 2;
      j = point.y + (dimension1.height - dimension.height) / 2;
      if (j + dimension.height > rectangle.y + rectangle.height) {
        j = rectangle.y + rectangle.height - dimension.height;
        if (point.x - rectangle.x + dimension1.width / 2 < rectangle.width / 2) {
          i = point.x + dimension1.width;
        } else {
          i = point.x - dimension.width;
        } 
      } 
    } 
    if (j + dimension.height > rectangle.y + rectangle.height)
      j = rectangle.y + rectangle.height - dimension.height; 
    if (j < rectangle.y)
      j = rectangle.y; 
    if (i + dimension.width > rectangle.x + rectangle.width)
      i = rectangle.x + rectangle.width - dimension.width; 
    if (i < rectangle.x)
      i = rectangle.x; 
    setLocation(i, j);
  }
  
  void deliverMouseWheelToAncestor(MouseWheelEvent paramMouseWheelEvent) {}
  
  boolean dispatchMouseWheelToAncestor(MouseWheelEvent paramMouseWheelEvent) { return false; }
  
  public void createBufferStrategy(int paramInt) { super.createBufferStrategy(paramInt); }
  
  public void createBufferStrategy(int paramInt, BufferCapabilities paramBufferCapabilities) throws AWTException { super.createBufferStrategy(paramInt, paramBufferCapabilities); }
  
  public BufferStrategy getBufferStrategy() { return super.getBufferStrategy(); }
  
  Component getTemporaryLostComponent() { return this.temporaryLostComponent; }
  
  Component setTemporaryLostComponent(Component paramComponent) {
    Component component = this.temporaryLostComponent;
    if (paramComponent == null || paramComponent.canBeFocusOwner()) {
      this.temporaryLostComponent = paramComponent;
    } else {
      this.temporaryLostComponent = null;
    } 
    return component;
  }
  
  boolean canContainFocusOwner(Component paramComponent) { return (super.canContainFocusOwner(paramComponent) && isFocusableWindow()); }
  
  public void setLocationByPlatform(boolean paramBoolean) {
    synchronized (getTreeLock()) {
      if (paramBoolean && isShowing())
        throw new IllegalComponentStateException("The window is showing on screen."); 
      this.locationByPlatform = paramBoolean;
    } 
  }
  
  public boolean isLocationByPlatform() { return this.locationByPlatform; }
  
  public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    synchronized (getTreeLock()) {
      if (getBoundsOp() == 1 || getBoundsOp() == 3)
        this.locationByPlatform = false; 
      super.setBounds(paramInt1, paramInt2, paramInt3, paramInt4);
    } 
  }
  
  public void setBounds(Rectangle paramRectangle) { setBounds(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height); }
  
  boolean isRecursivelyVisible() { return this.visible; }
  
  public float getOpacity() { return this.opacity; }
  
  public void setOpacity(float paramFloat) {
    synchronized (getTreeLock()) {
      if (paramFloat < 0.0F || paramFloat > 1.0F)
        throw new IllegalArgumentException("The value of opacity should be in the range [0.0f .. 1.0f]."); 
      if (paramFloat < 1.0F) {
        GraphicsConfiguration graphicsConfiguration = getGraphicsConfiguration();
        GraphicsDevice graphicsDevice = graphicsConfiguration.getDevice();
        if (graphicsConfiguration.getDevice().getFullScreenWindow() == this)
          throw new IllegalComponentStateException("Setting opacity for full-screen window is not supported."); 
        if (!graphicsDevice.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.TRANSLUCENT))
          throw new UnsupportedOperationException("TRANSLUCENT translucency is not supported."); 
      } 
      this.opacity = paramFloat;
      WindowPeer windowPeer = (WindowPeer)getPeer();
      if (windowPeer != null)
        windowPeer.setOpacity(paramFloat); 
    } 
  }
  
  public Shape getShape() {
    synchronized (getTreeLock()) {
      return (this.shape == null) ? null : new Path2D.Float(this.shape);
    } 
  }
  
  public void setShape(Shape paramShape) {
    synchronized (getTreeLock()) {
      if (paramShape != null) {
        GraphicsConfiguration graphicsConfiguration = getGraphicsConfiguration();
        GraphicsDevice graphicsDevice = graphicsConfiguration.getDevice();
        if (graphicsConfiguration.getDevice().getFullScreenWindow() == this)
          throw new IllegalComponentStateException("Setting shape for full-screen window is not supported."); 
        if (!graphicsDevice.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSPARENT))
          throw new UnsupportedOperationException("PERPIXEL_TRANSPARENT translucency is not supported."); 
      } 
      this.shape = (paramShape == null) ? null : new Path2D.Float(paramShape);
      WindowPeer windowPeer = (WindowPeer)getPeer();
      if (windowPeer != null)
        windowPeer.applyShape((paramShape == null) ? null : Region.getInstance(paramShape, null)); 
    } 
  }
  
  public Color getBackground() { return super.getBackground(); }
  
  public void setBackground(Color paramColor) {
    Color color = getBackground();
    super.setBackground(paramColor);
    if (color != null && color.equals(paramColor))
      return; 
    int i = (color != null) ? color.getAlpha() : 255;
    int j = (paramColor != null) ? paramColor.getAlpha() : 255;
    if (i == 255 && j < 255) {
      GraphicsConfiguration graphicsConfiguration = getGraphicsConfiguration();
      GraphicsDevice graphicsDevice = graphicsConfiguration.getDevice();
      if (graphicsConfiguration.getDevice().getFullScreenWindow() == this)
        throw new IllegalComponentStateException("Making full-screen window non opaque is not supported."); 
      if (!graphicsConfiguration.isTranslucencyCapable()) {
        GraphicsConfiguration graphicsConfiguration1 = graphicsDevice.getTranslucencyCapableGC();
        if (graphicsConfiguration1 == null)
          throw new UnsupportedOperationException("PERPIXEL_TRANSLUCENT translucency is not supported"); 
        setGraphicsConfiguration(graphicsConfiguration1);
      } 
      setLayersOpaque(this, false);
    } else if (i < 255 && j == 255) {
      setLayersOpaque(this, true);
    } 
    WindowPeer windowPeer = (WindowPeer)getPeer();
    if (windowPeer != null)
      windowPeer.setOpaque((j == 255)); 
  }
  
  public boolean isOpaque() {
    Color color = getBackground();
    return (color != null) ? ((color.getAlpha() == 255)) : true;
  }
  
  private void updateWindow() {
    synchronized (getTreeLock()) {
      WindowPeer windowPeer = (WindowPeer)getPeer();
      if (windowPeer != null)
        windowPeer.updateWindow(); 
    } 
  }
  
  public void paint(Graphics paramGraphics) {
    if (!isOpaque()) {
      graphics = paramGraphics.create();
      try {
        if (graphics instanceof Graphics2D) {
          graphics.setColor(getBackground());
          ((Graphics2D)graphics).setComposite(AlphaComposite.getInstance(2));
          graphics.fillRect(0, 0, getWidth(), getHeight());
        } 
      } finally {
        graphics.dispose();
      } 
    } 
    super.paint(paramGraphics);
  }
  
  private static void setLayersOpaque(Component paramComponent, boolean paramBoolean) {
    if (SunToolkit.isInstanceOf(paramComponent, "javax.swing.RootPaneContainer")) {
      RootPaneContainer rootPaneContainer = (RootPaneContainer)paramComponent;
      JRootPane jRootPane = rootPaneContainer.getRootPane();
      JLayeredPane jLayeredPane = jRootPane.getLayeredPane();
      Container container = jRootPane.getContentPane();
      JComponent jComponent = (container instanceof JComponent) ? (JComponent)container : null;
      jLayeredPane.setOpaque(paramBoolean);
      jRootPane.setOpaque(paramBoolean);
      if (jComponent != null) {
        jComponent.setOpaque(paramBoolean);
        int i = jComponent.getComponentCount();
        if (i > 0) {
          Component component = jComponent.getComponent(0);
          if (component instanceof RootPaneContainer)
            setLayersOpaque(component, paramBoolean); 
        } 
      } 
    } 
  }
  
  final Container getContainer() { return null; }
  
  final void applyCompoundShape(Region paramRegion) {}
  
  final void applyCurrentShape() {}
  
  final void mixOnReshaping() {}
  
  final Point getLocationOnWindow() { return new Point(0, 0); }
  
  private static double limit(double paramDouble1, double paramDouble2, double paramDouble3) {
    paramDouble1 = Math.max(paramDouble1, paramDouble2);
    return Math.min(paramDouble1, paramDouble3);
  }
  
  private Point2D calculateSecurityWarningPosition(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
    double d1 = paramDouble1 + paramDouble3 * this.securityWarningAlignmentX + this.securityWarningPointX;
    double d2 = paramDouble2 + paramDouble4 * this.securityWarningAlignmentY + this.securityWarningPointY;
    d1 = limit(d1, paramDouble1 - this.securityWarningWidth - 2.0D, paramDouble1 + paramDouble3 + 2.0D);
    d2 = limit(d2, paramDouble2 - this.securityWarningHeight - 2.0D, paramDouble2 + paramDouble4 + 2.0D);
    GraphicsConfiguration graphicsConfiguration = getGraphicsConfiguration_NoClientCode();
    Rectangle rectangle = graphicsConfiguration.getBounds();
    Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(graphicsConfiguration);
    d1 = limit(d1, (rectangle.x + insets.left), (rectangle.x + rectangle.width - insets.right - this.securityWarningWidth));
    d2 = limit(d2, (rectangle.y + insets.top), (rectangle.y + rectangle.height - insets.bottom - this.securityWarningHeight));
    return new Point2D.Double(d1, d2);
  }
  
  void updateZOrder() {}
  
  static  {
    Toolkit.loadLibraries();
    if (!GraphicsEnvironment.isHeadless())
      initIDs(); 
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("java.awt.syncLWRequests"));
    systemSyncLWRequests = (str != null && str.equals("true"));
    str = (String)AccessController.doPrivileged(new GetPropertyAction("java.awt.Window.locationByPlatform"));
    locationByPlatformProp = (str != null && str.equals("true"));
    beforeFirstWindowShown = new AtomicBoolean(true);
    AWTAccessor.setWindowAccessor(new AWTAccessor.WindowAccessor() {
          public float getOpacity(Window param1Window) { return param1Window.opacity; }
          
          public void setOpacity(Window param1Window, float param1Float) { param1Window.setOpacity(param1Float); }
          
          public Shape getShape(Window param1Window) { return param1Window.getShape(); }
          
          public void setShape(Window param1Window, Shape param1Shape) { param1Window.setShape(param1Shape); }
          
          public void setOpaque(Window param1Window, boolean param1Boolean) {
            Color color = param1Window.getBackground();
            if (color == null)
              color = new Color(0, 0, 0, 0); 
            param1Window.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), param1Boolean ? 255 : 0));
          }
          
          public void updateWindow(Window param1Window) { param1Window.updateWindow(); }
          
          public Dimension getSecurityWarningSize(Window param1Window) { return new Dimension(param1Window.securityWarningWidth, param1Window.securityWarningHeight); }
          
          public void setSecurityWarningSize(Window param1Window, int param1Int1, int param1Int2) {
            param1Window.securityWarningWidth = param1Int1;
            param1Window.securityWarningHeight = param1Int2;
          }
          
          public void setSecurityWarningPosition(Window param1Window, Point2D param1Point2D, float param1Float1, float param1Float2) {
            param1Window.securityWarningPointX = param1Point2D.getX();
            param1Window.securityWarningPointY = param1Point2D.getY();
            param1Window.securityWarningAlignmentX = param1Float1;
            param1Window.securityWarningAlignmentY = param1Float2;
            synchronized (param1Window.getTreeLock()) {
              WindowPeer windowPeer = (WindowPeer)param1Window.getPeer();
              if (windowPeer != null)
                windowPeer.repositionSecurityWarning(); 
            } 
          }
          
          public Point2D calculateSecurityWarningPosition(Window param1Window, double param1Double1, double param1Double2, double param1Double3, double param1Double4) { return param1Window.calculateSecurityWarningPosition(param1Double1, param1Double2, param1Double3, param1Double4); }
          
          public void setLWRequestStatus(Window param1Window, boolean param1Boolean) { param1Window.syncLWRequests = param1Boolean; }
          
          public boolean isAutoRequestFocus(Window param1Window) { return param1Window.autoRequestFocus; }
          
          public boolean isTrayIconWindow(Window param1Window) { return param1Window.isTrayIconWindow; }
          
          public void setTrayIconWindow(Window param1Window, boolean param1Boolean) { param1Window.isTrayIconWindow = param1Boolean; }
          
          public Window[] getOwnedWindows(Window param1Window) { return param1Window.getOwnedWindows_NoClientCode(); }
        });
  }
  
  protected class AccessibleAWTWindow extends Container.AccessibleAWTContainer {
    private static final long serialVersionUID = 4215068635060671780L;
    
    protected AccessibleAWTWindow() { super(Window.this); }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.WINDOW; }
    
    public AccessibleStateSet getAccessibleStateSet() {
      AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
      if (Window.this.getFocusOwner() != null)
        accessibleStateSet.add(AccessibleState.ACTIVE); 
      return accessibleStateSet;
    }
  }
  
  public enum Type {
    NORMAL, UTILITY, POPUP;
  }
  
  static class WindowDisposerRecord implements DisposerRecord {
    WeakReference<Window> owner;
    
    final WeakReference<Window> weakThis;
    
    final WeakReference<AppContext> context;
    
    WindowDisposerRecord(AppContext param1AppContext, Window param1Window) {
      this.weakThis = param1Window.weakThis;
      this.context = new WeakReference(param1AppContext);
    }
    
    public void updateOwner() {
      Window window = (Window)this.weakThis.get();
      this.owner = (window == null) ? null : new WeakReference(window.getOwner());
    }
    
    public void dispose() {
      if (this.owner != null) {
        Window window = (Window)this.owner.get();
        if (window != null)
          window.removeOwnedWindow(this.weakThis); 
      } 
      AppContext appContext = (AppContext)this.context.get();
      if (null != appContext)
        Window.removeFromWindowList(appContext, this.weakThis); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\Window.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */