package java.awt;

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.peer.KeyboardFocusManagerPeer;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.WeakHashMap;
import sun.awt.AWTAccessor;
import sun.awt.AppContext;
import sun.awt.CausedFocusEvent;
import sun.awt.KeyboardFocusManagerPeerProvider;
import sun.awt.SunToolkit;
import sun.util.logging.PlatformLogger;

public abstract class KeyboardFocusManager implements KeyEventDispatcher, KeyEventPostProcessor {
  private static final PlatformLogger focusLog = PlatformLogger.getLogger("java.awt.focus.KeyboardFocusManager");
  
  KeyboardFocusManagerPeer peer;
  
  private static final PlatformLogger log;
  
  public static final int FORWARD_TRAVERSAL_KEYS = 0;
  
  public static final int BACKWARD_TRAVERSAL_KEYS = 1;
  
  public static final int UP_CYCLE_TRAVERSAL_KEYS = 2;
  
  public static final int DOWN_CYCLE_TRAVERSAL_KEYS = 3;
  
  static final int TRAVERSAL_KEY_LENGTH = 4;
  
  private static Component focusOwner;
  
  private static Component permanentFocusOwner;
  
  private static Window focusedWindow;
  
  private static Window activeWindow;
  
  private FocusTraversalPolicy defaultPolicy = new DefaultFocusTraversalPolicy();
  
  private static final String[] defaultFocusTraversalKeyPropertyNames;
  
  private static final AWTKeyStroke[][] defaultFocusTraversalKeyStrokes;
  
  private Set<AWTKeyStroke>[] defaultFocusTraversalKeys = new Set[4];
  
  private static Container currentFocusCycleRoot;
  
  private VetoableChangeSupport vetoableSupport;
  
  private PropertyChangeSupport changeSupport;
  
  private LinkedList<KeyEventDispatcher> keyEventDispatchers;
  
  private LinkedList<KeyEventPostProcessor> keyEventPostProcessors;
  
  private static Map<Window, WeakReference<Component>> mostRecentFocusOwners;
  
  private static AWTPermission replaceKeyboardFocusManagerPermission;
  
  SequencedEvent currentSequencedEvent = null;
  
  private static LinkedList<HeavyweightFocusRequest> heavyweightRequests;
  
  private static LinkedList<LightweightFocusRequest> currentLightweightRequests;
  
  private static boolean clearingCurrentLightweightRequests;
  
  private static boolean allowSyncFocusRequests;
  
  private static Component newFocusOwner;
  
  static final int SNFH_FAILURE = 0;
  
  static final int SNFH_SUCCESS_HANDLED = 1;
  
  static final int SNFH_SUCCESS_PROCEED = 2;
  
  static Field proxyActive;
  
  private static native void initIDs();
  
  public static KeyboardFocusManager getCurrentKeyboardFocusManager() { return getCurrentKeyboardFocusManager(AppContext.getAppContext()); }
  
  static KeyboardFocusManager getCurrentKeyboardFocusManager(AppContext paramAppContext) {
    KeyboardFocusManager keyboardFocusManager = (KeyboardFocusManager)paramAppContext.get(KeyboardFocusManager.class);
    if (keyboardFocusManager == null) {
      keyboardFocusManager = new DefaultKeyboardFocusManager();
      paramAppContext.put(KeyboardFocusManager.class, keyboardFocusManager);
    } 
    return keyboardFocusManager;
  }
  
  public static void setCurrentKeyboardFocusManager(KeyboardFocusManager paramKeyboardFocusManager) throws SecurityException {
    checkReplaceKFMPermission();
    KeyboardFocusManager keyboardFocusManager = null;
    synchronized (KeyboardFocusManager.class) {
      AppContext appContext = AppContext.getAppContext();
      if (paramKeyboardFocusManager != null) {
        keyboardFocusManager = getCurrentKeyboardFocusManager(appContext);
        appContext.put(KeyboardFocusManager.class, paramKeyboardFocusManager);
      } else {
        keyboardFocusManager = getCurrentKeyboardFocusManager(appContext);
        appContext.remove(KeyboardFocusManager.class);
      } 
    } 
    if (keyboardFocusManager != null)
      keyboardFocusManager.firePropertyChange("managingFocus", Boolean.TRUE, Boolean.FALSE); 
    if (paramKeyboardFocusManager != null)
      paramKeyboardFocusManager.firePropertyChange("managingFocus", Boolean.FALSE, Boolean.TRUE); 
  }
  
  final void setCurrentSequencedEvent(SequencedEvent paramSequencedEvent) {
    synchronized (SequencedEvent.class) {
      assert paramSequencedEvent == null || this.currentSequencedEvent == null;
      this.currentSequencedEvent = paramSequencedEvent;
    } 
  }
  
  final SequencedEvent getCurrentSequencedEvent() {
    synchronized (SequencedEvent.class) {
      return this.currentSequencedEvent;
    } 
  }
  
  static Set<AWTKeyStroke> initFocusTraversalKeysSet(String paramString, Set<AWTKeyStroke> paramSet) {
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, ",");
    while (stringTokenizer.hasMoreTokens())
      paramSet.add(AWTKeyStroke.getAWTKeyStroke(stringTokenizer.nextToken())); 
    return paramSet.isEmpty() ? Collections.EMPTY_SET : Collections.unmodifiableSet(paramSet);
  }
  
  public KeyboardFocusManager() {
    for (byte b = 0; b < 4; b++) {
      HashSet hashSet = new HashSet();
      for (byte b1 = 0; b1 < defaultFocusTraversalKeyStrokes[b].length; b1++)
        hashSet.add(defaultFocusTraversalKeyStrokes[b][b1]); 
      this.defaultFocusTraversalKeys[b] = hashSet.isEmpty() ? Collections.EMPTY_SET : Collections.unmodifiableSet(hashSet);
    } 
    initPeer();
  }
  
  private void initPeer() {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    KeyboardFocusManagerPeerProvider keyboardFocusManagerPeerProvider = (KeyboardFocusManagerPeerProvider)toolkit;
    this.peer = keyboardFocusManagerPeerProvider.getKeyboardFocusManagerPeer();
  }
  
  public Component getFocusOwner() {
    synchronized (KeyboardFocusManager.class) {
      if (focusOwner == null)
        return null; 
      return (focusOwner.appContext == AppContext.getAppContext()) ? focusOwner : null;
    } 
  }
  
  protected Component getGlobalFocusOwner() {
    synchronized (KeyboardFocusManager.class) {
      checkKFMSecurity();
      return focusOwner;
    } 
  }
  
  protected void setGlobalFocusOwner(Component paramComponent) throws SecurityException {
    Component component = null;
    boolean bool = false;
    if (paramComponent == null || paramComponent.isFocusable())
      synchronized (KeyboardFocusManager.class) {
        checkKFMSecurity();
        component = getFocusOwner();
        try {
          fireVetoableChange("focusOwner", component, paramComponent);
        } catch (PropertyVetoException propertyVetoException) {
          return;
        } 
        focusOwner = paramComponent;
        if (paramComponent != null && (getCurrentFocusCycleRoot() == null || !paramComponent.isFocusCycleRoot(getCurrentFocusCycleRoot()))) {
          Container container = paramComponent.getFocusCycleRootAncestor();
          if (container == null && paramComponent instanceof Window)
            container = (Container)paramComponent; 
          if (container != null)
            setGlobalCurrentFocusCycleRootPriv(container); 
        } 
        bool = true;
      }  
    if (bool)
      firePropertyChange("focusOwner", component, paramComponent); 
  }
  
  public void clearFocusOwner() {
    if (getFocusOwner() != null)
      clearGlobalFocusOwner(); 
  }
  
  public void clearGlobalFocusOwner() {
    checkReplaceKFMPermission();
    if (!GraphicsEnvironment.isHeadless()) {
      Toolkit.getDefaultToolkit();
      _clearGlobalFocusOwner();
    } 
  }
  
  private void _clearGlobalFocusOwner() {
    Window window = markClearGlobalFocusOwner();
    this.peer.clearGlobalFocusOwner(window);
  }
  
  void clearGlobalFocusOwnerPriv() { AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            KeyboardFocusManager.this.clearGlobalFocusOwner();
            return null;
          }
        }); }
  
  Component getNativeFocusOwner() { return this.peer.getCurrentFocusOwner(); }
  
  void setNativeFocusOwner(Component paramComponent) throws SecurityException {
    if (focusLog.isLoggable(PlatformLogger.Level.FINEST))
      focusLog.finest("Calling peer {0} setCurrentFocusOwner for {1}", new Object[] { String.valueOf(this.peer), String.valueOf(paramComponent) }); 
    this.peer.setCurrentFocusOwner(paramComponent);
  }
  
  Window getNativeFocusedWindow() { return this.peer.getCurrentFocusedWindow(); }
  
  public Component getPermanentFocusOwner() {
    synchronized (KeyboardFocusManager.class) {
      if (permanentFocusOwner == null)
        return null; 
      return (permanentFocusOwner.appContext == AppContext.getAppContext()) ? permanentFocusOwner : null;
    } 
  }
  
  protected Component getGlobalPermanentFocusOwner() {
    synchronized (KeyboardFocusManager.class) {
      checkKFMSecurity();
      return permanentFocusOwner;
    } 
  }
  
  protected void setGlobalPermanentFocusOwner(Component paramComponent) throws SecurityException {
    Component component = null;
    boolean bool = false;
    if (paramComponent == null || paramComponent.isFocusable())
      synchronized (KeyboardFocusManager.class) {
        checkKFMSecurity();
        component = getPermanentFocusOwner();
        try {
          fireVetoableChange("permanentFocusOwner", component, paramComponent);
        } catch (PropertyVetoException propertyVetoException) {
          return;
        } 
        permanentFocusOwner = paramComponent;
        setMostRecentFocusOwner(paramComponent);
        bool = true;
      }  
    if (bool)
      firePropertyChange("permanentFocusOwner", component, paramComponent); 
  }
  
  public Window getFocusedWindow() {
    synchronized (KeyboardFocusManager.class) {
      if (focusedWindow == null)
        return null; 
      return (focusedWindow.appContext == AppContext.getAppContext()) ? focusedWindow : null;
    } 
  }
  
  protected Window getGlobalFocusedWindow() {
    synchronized (KeyboardFocusManager.class) {
      checkKFMSecurity();
      return focusedWindow;
    } 
  }
  
  protected void setGlobalFocusedWindow(Window paramWindow) throws SecurityException {
    Window window = null;
    boolean bool = false;
    if (paramWindow == null || paramWindow.isFocusableWindow())
      synchronized (KeyboardFocusManager.class) {
        checkKFMSecurity();
        window = getFocusedWindow();
        try {
          fireVetoableChange("focusedWindow", window, paramWindow);
        } catch (PropertyVetoException propertyVetoException) {
          return;
        } 
        focusedWindow = paramWindow;
        bool = true;
      }  
    if (bool)
      firePropertyChange("focusedWindow", window, paramWindow); 
  }
  
  public Window getActiveWindow() {
    synchronized (KeyboardFocusManager.class) {
      if (activeWindow == null)
        return null; 
      return (activeWindow.appContext == AppContext.getAppContext()) ? activeWindow : null;
    } 
  }
  
  protected Window getGlobalActiveWindow() {
    synchronized (KeyboardFocusManager.class) {
      checkKFMSecurity();
      return activeWindow;
    } 
  }
  
  protected void setGlobalActiveWindow(Window paramWindow) throws SecurityException {
    Window window;
    synchronized (KeyboardFocusManager.class) {
      checkKFMSecurity();
      window = getActiveWindow();
      if (focusLog.isLoggable(PlatformLogger.Level.FINER))
        focusLog.finer("Setting global active window to " + paramWindow + ", old active " + window); 
      try {
        fireVetoableChange("activeWindow", window, paramWindow);
      } catch (PropertyVetoException propertyVetoException) {
        return;
      } 
      activeWindow = paramWindow;
    } 
    firePropertyChange("activeWindow", window, paramWindow);
  }
  
  public FocusTraversalPolicy getDefaultFocusTraversalPolicy() { return this.defaultPolicy; }
  
  public void setDefaultFocusTraversalPolicy(FocusTraversalPolicy paramFocusTraversalPolicy) {
    FocusTraversalPolicy focusTraversalPolicy;
    if (paramFocusTraversalPolicy == null)
      throw new IllegalArgumentException("default focus traversal policy cannot be null"); 
    synchronized (this) {
      focusTraversalPolicy = this.defaultPolicy;
      this.defaultPolicy = paramFocusTraversalPolicy;
    } 
    firePropertyChange("defaultFocusTraversalPolicy", focusTraversalPolicy, paramFocusTraversalPolicy);
  }
  
  public void setDefaultFocusTraversalKeys(int paramInt, Set<? extends AWTKeyStroke> paramSet) {
    Set set;
    if (paramInt < 0 || paramInt >= 4)
      throw new IllegalArgumentException("invalid focus traversal key identifier"); 
    if (paramSet == null)
      throw new IllegalArgumentException("cannot set null Set of default focus traversal keys"); 
    synchronized (this) {
      for (AWTKeyStroke aWTKeyStroke : paramSet) {
        if (aWTKeyStroke == null)
          throw new IllegalArgumentException("cannot set null focus traversal key"); 
        if (aWTKeyStroke.getKeyChar() != Character.MAX_VALUE)
          throw new IllegalArgumentException("focus traversal keys cannot map to KEY_TYPED events"); 
        for (byte b = 0; b < 4; b++) {
          if (b != paramInt && this.defaultFocusTraversalKeys[b].contains(aWTKeyStroke))
            throw new IllegalArgumentException("focus traversal keys must be unique for a Component"); 
        } 
      } 
      set = this.defaultFocusTraversalKeys[paramInt];
      this.defaultFocusTraversalKeys[paramInt] = Collections.unmodifiableSet(new HashSet(paramSet));
    } 
    firePropertyChange(defaultFocusTraversalKeyPropertyNames[paramInt], set, paramSet);
  }
  
  public Set<AWTKeyStroke> getDefaultFocusTraversalKeys(int paramInt) {
    if (paramInt < 0 || paramInt >= 4)
      throw new IllegalArgumentException("invalid focus traversal key identifier"); 
    return this.defaultFocusTraversalKeys[paramInt];
  }
  
  public Container getCurrentFocusCycleRoot() {
    synchronized (KeyboardFocusManager.class) {
      if (currentFocusCycleRoot == null)
        return null; 
      return (currentFocusCycleRoot.appContext == AppContext.getAppContext()) ? currentFocusCycleRoot : null;
    } 
  }
  
  protected Container getGlobalCurrentFocusCycleRoot() {
    synchronized (KeyboardFocusManager.class) {
      checkKFMSecurity();
      return currentFocusCycleRoot;
    } 
  }
  
  public void setGlobalCurrentFocusCycleRoot(Container paramContainer) throws SecurityException {
    Container container;
    checkReplaceKFMPermission();
    synchronized (KeyboardFocusManager.class) {
      container = getCurrentFocusCycleRoot();
      currentFocusCycleRoot = paramContainer;
    } 
    firePropertyChange("currentFocusCycleRoot", container, paramContainer);
  }
  
  void setGlobalCurrentFocusCycleRootPriv(final Container newFocusCycleRoot) throws SecurityException { AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            KeyboardFocusManager.this.setGlobalCurrentFocusCycleRoot(newFocusCycleRoot);
            return null;
          }
        }); }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    if (paramPropertyChangeListener != null)
      synchronized (this) {
        if (this.changeSupport == null)
          this.changeSupport = new PropertyChangeSupport(this); 
        this.changeSupport.addPropertyChangeListener(paramPropertyChangeListener);
      }  
  }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    if (paramPropertyChangeListener != null)
      synchronized (this) {
        if (this.changeSupport != null)
          this.changeSupport.removePropertyChangeListener(paramPropertyChangeListener); 
      }  
  }
  
  public PropertyChangeListener[] getPropertyChangeListeners() {
    if (this.changeSupport == null)
      this.changeSupport = new PropertyChangeSupport(this); 
    return this.changeSupport.getPropertyChangeListeners();
  }
  
  public void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener) {
    if (paramPropertyChangeListener != null)
      synchronized (this) {
        if (this.changeSupport == null)
          this.changeSupport = new PropertyChangeSupport(this); 
        this.changeSupport.addPropertyChangeListener(paramString, paramPropertyChangeListener);
      }  
  }
  
  public void removePropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener) {
    if (paramPropertyChangeListener != null)
      synchronized (this) {
        if (this.changeSupport != null)
          this.changeSupport.removePropertyChangeListener(paramString, paramPropertyChangeListener); 
      }  
  }
  
  public PropertyChangeListener[] getPropertyChangeListeners(String paramString) {
    if (this.changeSupport == null)
      this.changeSupport = new PropertyChangeSupport(this); 
    return this.changeSupport.getPropertyChangeListeners(paramString);
  }
  
  protected void firePropertyChange(String paramString, Object paramObject1, Object paramObject2) {
    if (paramObject1 == paramObject2)
      return; 
    PropertyChangeSupport propertyChangeSupport = this.changeSupport;
    if (propertyChangeSupport != null)
      propertyChangeSupport.firePropertyChange(paramString, paramObject1, paramObject2); 
  }
  
  public void addVetoableChangeListener(VetoableChangeListener paramVetoableChangeListener) {
    if (paramVetoableChangeListener != null)
      synchronized (this) {
        if (this.vetoableSupport == null)
          this.vetoableSupport = new VetoableChangeSupport(this); 
        this.vetoableSupport.addVetoableChangeListener(paramVetoableChangeListener);
      }  
  }
  
  public void removeVetoableChangeListener(VetoableChangeListener paramVetoableChangeListener) {
    if (paramVetoableChangeListener != null)
      synchronized (this) {
        if (this.vetoableSupport != null)
          this.vetoableSupport.removeVetoableChangeListener(paramVetoableChangeListener); 
      }  
  }
  
  public VetoableChangeListener[] getVetoableChangeListeners() {
    if (this.vetoableSupport == null)
      this.vetoableSupport = new VetoableChangeSupport(this); 
    return this.vetoableSupport.getVetoableChangeListeners();
  }
  
  public void addVetoableChangeListener(String paramString, VetoableChangeListener paramVetoableChangeListener) {
    if (paramVetoableChangeListener != null)
      synchronized (this) {
        if (this.vetoableSupport == null)
          this.vetoableSupport = new VetoableChangeSupport(this); 
        this.vetoableSupport.addVetoableChangeListener(paramString, paramVetoableChangeListener);
      }  
  }
  
  public void removeVetoableChangeListener(String paramString, VetoableChangeListener paramVetoableChangeListener) {
    if (paramVetoableChangeListener != null)
      synchronized (this) {
        if (this.vetoableSupport != null)
          this.vetoableSupport.removeVetoableChangeListener(paramString, paramVetoableChangeListener); 
      }  
  }
  
  public VetoableChangeListener[] getVetoableChangeListeners(String paramString) {
    if (this.vetoableSupport == null)
      this.vetoableSupport = new VetoableChangeSupport(this); 
    return this.vetoableSupport.getVetoableChangeListeners(paramString);
  }
  
  protected void fireVetoableChange(String paramString, Object paramObject1, Object paramObject2) {
    if (paramObject1 == paramObject2)
      return; 
    VetoableChangeSupport vetoableChangeSupport = this.vetoableSupport;
    if (vetoableChangeSupport != null)
      vetoableChangeSupport.fireVetoableChange(paramString, paramObject1, paramObject2); 
  }
  
  public void addKeyEventDispatcher(KeyEventDispatcher paramKeyEventDispatcher) {
    if (paramKeyEventDispatcher != null)
      synchronized (this) {
        if (this.keyEventDispatchers == null)
          this.keyEventDispatchers = new LinkedList(); 
        this.keyEventDispatchers.add(paramKeyEventDispatcher);
      }  
  }
  
  public void removeKeyEventDispatcher(KeyEventDispatcher paramKeyEventDispatcher) {
    if (paramKeyEventDispatcher != null)
      synchronized (this) {
        if (this.keyEventDispatchers != null)
          this.keyEventDispatchers.remove(paramKeyEventDispatcher); 
      }  
  }
  
  protected List<KeyEventDispatcher> getKeyEventDispatchers() { return (this.keyEventDispatchers != null) ? (List)this.keyEventDispatchers.clone() : null; }
  
  public void addKeyEventPostProcessor(KeyEventPostProcessor paramKeyEventPostProcessor) {
    if (paramKeyEventPostProcessor != null)
      synchronized (this) {
        if (this.keyEventPostProcessors == null)
          this.keyEventPostProcessors = new LinkedList(); 
        this.keyEventPostProcessors.add(paramKeyEventPostProcessor);
      }  
  }
  
  public void removeKeyEventPostProcessor(KeyEventPostProcessor paramKeyEventPostProcessor) {
    if (paramKeyEventPostProcessor != null)
      synchronized (this) {
        if (this.keyEventPostProcessors != null)
          this.keyEventPostProcessors.remove(paramKeyEventPostProcessor); 
      }  
  }
  
  protected List<KeyEventPostProcessor> getKeyEventPostProcessors() { return (this.keyEventPostProcessors != null) ? (List)this.keyEventPostProcessors.clone() : null; }
  
  static void setMostRecentFocusOwner(Component paramComponent) throws SecurityException {
    Component component;
    for (component = paramComponent; component != null && !(component instanceof Window); component = component.parent);
    if (component != null)
      setMostRecentFocusOwner((Window)component, paramComponent); 
  }
  
  static void setMostRecentFocusOwner(Window paramWindow, Component paramComponent) {
    WeakReference weakReference = null;
    if (paramComponent != null)
      weakReference = new WeakReference(paramComponent); 
    mostRecentFocusOwners.put(paramWindow, weakReference);
  }
  
  static void clearMostRecentFocusOwner(Component paramComponent) throws SecurityException {
    Container container;
    if (paramComponent == null)
      return; 
    synchronized (paramComponent.getTreeLock()) {
      for (container = paramComponent.getParent(); container != null && !(container instanceof Window); container = container.getParent());
    } 
    synchronized (KeyboardFocusManager.class) {
      if (container != null && getMostRecentFocusOwner((Window)container) == paramComponent)
        setMostRecentFocusOwner((Window)container, null); 
      if (container != null) {
        Window window = (Window)container;
        if (window.getTemporaryLostComponent() == paramComponent)
          window.setTemporaryLostComponent(null); 
      } 
    } 
  }
  
  static Component getMostRecentFocusOwner(Window paramWindow) {
    WeakReference weakReference = (WeakReference)mostRecentFocusOwners.get(paramWindow);
    return (weakReference == null) ? null : (Component)weakReference.get();
  }
  
  public abstract boolean dispatchEvent(AWTEvent paramAWTEvent);
  
  public final void redispatchEvent(Component paramComponent, AWTEvent paramAWTEvent) {
    paramAWTEvent.focusManagerIsDispatching = true;
    paramComponent.dispatchEvent(paramAWTEvent);
    paramAWTEvent.focusManagerIsDispatching = false;
  }
  
  public abstract boolean dispatchKeyEvent(KeyEvent paramKeyEvent);
  
  public abstract boolean postProcessKeyEvent(KeyEvent paramKeyEvent);
  
  public abstract void processKeyEvent(Component paramComponent, KeyEvent paramKeyEvent);
  
  protected abstract void enqueueKeyEvents(long paramLong, Component paramComponent);
  
  protected abstract void dequeueKeyEvents(long paramLong, Component paramComponent);
  
  protected abstract void discardKeyEvents(Component paramComponent) throws SecurityException;
  
  public abstract void focusNextComponent(Component paramComponent) throws SecurityException;
  
  public abstract void focusPreviousComponent(Component paramComponent) throws SecurityException;
  
  public abstract void upFocusCycle(Component paramComponent) throws SecurityException;
  
  public abstract void downFocusCycle(Container paramContainer) throws SecurityException;
  
  public final void focusNextComponent() {
    Component component = getFocusOwner();
    if (component != null)
      focusNextComponent(component); 
  }
  
  public final void focusPreviousComponent() {
    Component component = getFocusOwner();
    if (component != null)
      focusPreviousComponent(component); 
  }
  
  public final void upFocusCycle() {
    Component component = getFocusOwner();
    if (component != null)
      upFocusCycle(component); 
  }
  
  public final void downFocusCycle() {
    Component component = getFocusOwner();
    if (component instanceof Container)
      downFocusCycle((Container)component); 
  }
  
  void dumpRequests() {
    System.err.println(">>> Requests dump, time: " + System.currentTimeMillis());
    synchronized (heavyweightRequests) {
      for (HeavyweightFocusRequest heavyweightFocusRequest : heavyweightRequests)
        System.err.println(">>> Req: " + heavyweightFocusRequest); 
    } 
    System.err.println("");
  }
  
  static boolean processSynchronousLightweightTransfer(Component paramComponent1, Component paramComponent2, boolean paramBoolean1, boolean paramBoolean2, long paramLong) {
    Window window = SunToolkit.getContainingWindow(paramComponent1);
    if (window == null || !window.syncLWRequests)
      return false; 
    if (paramComponent2 == null)
      paramComponent2 = paramComponent1; 
    KeyboardFocusManager keyboardFocusManager = getCurrentKeyboardFocusManager(SunToolkit.targetToAppContext(paramComponent2));
    FocusEvent focusEvent1 = null;
    FocusEvent focusEvent2 = null;
    Component component = keyboardFocusManager.getGlobalFocusOwner();
    synchronized (heavyweightRequests) {
      HeavyweightFocusRequest heavyweightFocusRequest = getLastHWRequest();
      if (heavyweightFocusRequest == null && paramComponent1 == keyboardFocusManager.getNativeFocusOwner() && allowSyncFocusRequests) {
        if (paramComponent2 == component)
          return true; 
        keyboardFocusManager.enqueueKeyEvents(paramLong, paramComponent2);
        heavyweightFocusRequest = new HeavyweightFocusRequest(paramComponent1, paramComponent2, paramBoolean1, CausedFocusEvent.Cause.UNKNOWN);
        heavyweightRequests.add(heavyweightFocusRequest);
        if (component != null)
          focusEvent1 = new FocusEvent(component, 1005, paramBoolean1, paramComponent2); 
        focusEvent2 = new FocusEvent(paramComponent2, 1004, paramBoolean1, component);
      } 
    } 
    boolean bool1 = false;
    bool2 = clearingCurrentLightweightRequests;
    Throwable throwable = null;
    try {
      clearingCurrentLightweightRequests = false;
      synchronized (Component.LOCK) {
        if (focusEvent1 != null && component != null) {
          focusEvent1.isPosted = true;
          throwable = dispatchAndCatchException(throwable, component, focusEvent1);
          bool1 = true;
        } 
        if (focusEvent2 != null && paramComponent2 != null) {
          focusEvent2.isPosted = true;
          throwable = dispatchAndCatchException(throwable, paramComponent2, focusEvent2);
          bool1 = true;
        } 
      } 
    } finally {
      clearingCurrentLightweightRequests = bool2;
    } 
    if (throwable instanceof RuntimeException)
      throw (RuntimeException)throwable; 
    if (throwable instanceof Error)
      throw (Error)throwable; 
    return bool1;
  }
  
  static int shouldNativelyFocusHeavyweight(Component paramComponent1, Component paramComponent2, boolean paramBoolean1, boolean paramBoolean2, long paramLong, CausedFocusEvent.Cause paramCause) {
    if (log.isLoggable(PlatformLogger.Level.FINE)) {
      if (paramComponent1 == null)
        log.fine("Assertion (heavyweight != null) failed"); 
      if (paramLong == 0L)
        log.fine("Assertion (time != 0) failed"); 
    } 
    if (paramComponent2 == null)
      paramComponent2 = paramComponent1; 
    KeyboardFocusManager keyboardFocusManager1;
    KeyboardFocusManager keyboardFocusManager2 = (keyboardFocusManager1 = getCurrentKeyboardFocusManager(SunToolkit.targetToAppContext(paramComponent2))).getCurrentKeyboardFocusManager();
    Component component1 = keyboardFocusManager2.getGlobalFocusOwner();
    Component component2 = keyboardFocusManager2.getNativeFocusOwner();
    Window window = keyboardFocusManager2.getNativeFocusedWindow();
    if (focusLog.isLoggable(PlatformLogger.Level.FINER))
      focusLog.finer("SNFH for {0} in {1}", new Object[] { String.valueOf(paramComponent2), String.valueOf(paramComponent1) }); 
    if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
      focusLog.finest("0. Current focus owner {0}", new Object[] { String.valueOf(component1) });
      focusLog.finest("0. Native focus owner {0}", new Object[] { String.valueOf(component2) });
      focusLog.finest("0. Native focused window {0}", new Object[] { String.valueOf(window) });
    } 
    synchronized (heavyweightRequests) {
      HeavyweightFocusRequest heavyweightFocusRequest = getLastHWRequest();
      if (focusLog.isLoggable(PlatformLogger.Level.FINEST))
        focusLog.finest("Request {0}", new Object[] { String.valueOf(heavyweightFocusRequest) }); 
      if (heavyweightFocusRequest == null && paramComponent1 == component2 && paramComponent1.getContainingWindow() == window) {
        if (paramComponent2 == component1) {
          if (focusLog.isLoggable(PlatformLogger.Level.FINEST))
            focusLog.finest("1. SNFH_FAILURE for {0}", new Object[] { String.valueOf(paramComponent2) }); 
          return 0;
        } 
        keyboardFocusManager1.enqueueKeyEvents(paramLong, paramComponent2);
        heavyweightFocusRequest = new HeavyweightFocusRequest(paramComponent1, paramComponent2, paramBoolean1, paramCause);
        heavyweightRequests.add(heavyweightFocusRequest);
        if (component1 != null) {
          CausedFocusEvent causedFocusEvent1 = new CausedFocusEvent(component1, 1005, paramBoolean1, paramComponent2, paramCause);
          SunToolkit.postEvent(component1.appContext, causedFocusEvent1);
        } 
        CausedFocusEvent causedFocusEvent = new CausedFocusEvent(paramComponent2, 1004, paramBoolean1, component1, paramCause);
        SunToolkit.postEvent(paramComponent2.appContext, causedFocusEvent);
        if (focusLog.isLoggable(PlatformLogger.Level.FINEST))
          focusLog.finest("2. SNFH_HANDLED for {0}", new Object[] { String.valueOf(paramComponent2) }); 
        return 1;
      } 
      if (heavyweightFocusRequest != null && heavyweightFocusRequest.heavyweight == paramComponent1) {
        if (heavyweightFocusRequest.addLightweightRequest(paramComponent2, paramBoolean1, paramCause))
          keyboardFocusManager1.enqueueKeyEvents(paramLong, paramComponent2); 
        if (focusLog.isLoggable(PlatformLogger.Level.FINEST))
          focusLog.finest("3. SNFH_HANDLED for lightweight" + paramComponent2 + " in " + paramComponent1); 
        return 1;
      } 
      if (!paramBoolean2) {
        if (heavyweightFocusRequest == HeavyweightFocusRequest.CLEAR_GLOBAL_FOCUS_OWNER) {
          int i = heavyweightRequests.size();
          heavyweightFocusRequest = (i >= 2) ? (HeavyweightFocusRequest)heavyweightRequests.get(i - 2) : null;
        } 
        if (focusedWindowChanged(paramComponent1, (heavyweightFocusRequest != null) ? heavyweightFocusRequest.heavyweight : window)) {
          if (focusLog.isLoggable(PlatformLogger.Level.FINEST))
            focusLog.finest("4. SNFH_FAILURE for " + paramComponent2); 
          return 0;
        } 
      } 
      keyboardFocusManager1.enqueueKeyEvents(paramLong, paramComponent2);
      heavyweightRequests.add(new HeavyweightFocusRequest(paramComponent1, paramComponent2, paramBoolean1, paramCause));
      if (focusLog.isLoggable(PlatformLogger.Level.FINEST))
        focusLog.finest("5. SNFH_PROCEED for " + paramComponent2); 
      return 2;
    } 
  }
  
  static Window markClearGlobalFocusOwner() {
    Window window = getCurrentKeyboardFocusManager().getNativeFocusedWindow();
    synchronized (heavyweightRequests) {
      HeavyweightFocusRequest heavyweightFocusRequest = getLastHWRequest();
      if (heavyweightFocusRequest == HeavyweightFocusRequest.CLEAR_GLOBAL_FOCUS_OWNER)
        return null; 
      heavyweightRequests.add(HeavyweightFocusRequest.CLEAR_GLOBAL_FOCUS_OWNER);
      Container container = (heavyweightFocusRequest != null) ? SunToolkit.getContainingWindow(heavyweightFocusRequest.heavyweight) : window;
      while (container != null && !(container instanceof Frame) && !(container instanceof Dialog))
        container = container.getParent_NoClientCode(); 
      return (Window)container;
    } 
  }
  
  Component getCurrentWaitingRequest(Component paramComponent) {
    synchronized (heavyweightRequests) {
      HeavyweightFocusRequest heavyweightFocusRequest = getFirstHWRequest();
      if (heavyweightFocusRequest != null && heavyweightFocusRequest.heavyweight == paramComponent) {
        LightweightFocusRequest lightweightFocusRequest = (LightweightFocusRequest)heavyweightFocusRequest.lightweightRequests.getFirst();
        if (lightweightFocusRequest != null)
          return lightweightFocusRequest.component; 
      } 
    } 
    return null;
  }
  
  static boolean isAutoFocusTransferEnabled() {
    synchronized (heavyweightRequests) {
      return (heavyweightRequests.size() == 0 && !disableRestoreFocus && null == currentLightweightRequests);
    } 
  }
  
  static boolean isAutoFocusTransferEnabledFor(Component paramComponent) { return (isAutoFocusTransferEnabled() && paramComponent.isAutoFocusTransferOnDisposal()); }
  
  private static Throwable dispatchAndCatchException(Throwable paramThrowable, Component paramComponent, FocusEvent paramFocusEvent) {
    Error error = null;
    try {
      paramComponent.dispatchEvent(paramFocusEvent);
    } catch (RuntimeException runtimeException) {
      error = runtimeException;
    } catch (Error error1) {
      error = error1;
    } 
    if (error != null) {
      if (paramThrowable != null)
        handleException(paramThrowable); 
      return error;
    } 
    return paramThrowable;
  }
  
  private static void handleException(Throwable paramThrowable) { paramThrowable.printStackTrace(); }
  
  static void processCurrentLightweightRequests() {
    KeyboardFocusManager keyboardFocusManager = getCurrentKeyboardFocusManager();
    linkedList = null;
    Component component = keyboardFocusManager.getGlobalFocusOwner();
    if (component != null && component.appContext != AppContext.getAppContext())
      return; 
    synchronized (heavyweightRequests) {
      if (currentLightweightRequests != null) {
        clearingCurrentLightweightRequests = true;
        disableRestoreFocus = true;
        linkedList = currentLightweightRequests;
        allowSyncFocusRequests = (linkedList.size() < 2);
        currentLightweightRequests = null;
      } else {
        return;
      } 
    } 
    Throwable throwable = null;
    try {
      if (linkedList != null) {
        Component component1 = null;
        Component component2 = null;
        Iterator iterator = linkedList.iterator();
        while (iterator.hasNext()) {
          component2 = keyboardFocusManager.getGlobalFocusOwner();
          LightweightFocusRequest lightweightFocusRequest = (LightweightFocusRequest)iterator.next();
          if (!iterator.hasNext())
            disableRestoreFocus = false; 
          CausedFocusEvent causedFocusEvent1 = null;
          if (component2 != null)
            causedFocusEvent1 = new CausedFocusEvent(component2, 1005, lightweightFocusRequest.temporary, lightweightFocusRequest.component, lightweightFocusRequest.cause); 
          CausedFocusEvent causedFocusEvent2 = new CausedFocusEvent(lightweightFocusRequest.component, 1004, lightweightFocusRequest.temporary, (component2 == null) ? component1 : component2, lightweightFocusRequest.cause);
          if (component2 != null) {
            causedFocusEvent1.isPosted = true;
            throwable = dispatchAndCatchException(throwable, component2, causedFocusEvent1);
          } 
          causedFocusEvent2.isPosted = true;
          throwable = dispatchAndCatchException(throwable, lightweightFocusRequest.component, causedFocusEvent2);
          if (keyboardFocusManager.getGlobalFocusOwner() == lightweightFocusRequest.component)
            component1 = lightweightFocusRequest.component; 
        } 
      } 
    } finally {
      clearingCurrentLightweightRequests = false;
      disableRestoreFocus = false;
      linkedList = null;
      allowSyncFocusRequests = true;
    } 
    if (throwable instanceof RuntimeException)
      throw (RuntimeException)throwable; 
    if (throwable instanceof Error)
      throw (Error)throwable; 
  }
  
  static FocusEvent retargetUnexpectedFocusEvent(FocusEvent paramFocusEvent) {
    synchronized (heavyweightRequests) {
      if (removeFirstRequest())
        return (FocusEvent)retargetFocusEvent(paramFocusEvent); 
      Component component1 = paramFocusEvent.getComponent();
      Component component2 = paramFocusEvent.getOppositeComponent();
      boolean bool = false;
      if (paramFocusEvent.getID() == 1005 && (component2 == null || isTemporary(component2, component1)))
        bool = true; 
      return new CausedFocusEvent(component1, paramFocusEvent.getID(), bool, component2, CausedFocusEvent.Cause.NATIVE_SYSTEM);
    } 
  }
  
  static FocusEvent retargetFocusGained(FocusEvent paramFocusEvent) {
    assert paramFocusEvent.getID() == 1004;
    Component component1 = getCurrentKeyboardFocusManager().getGlobalFocusOwner();
    Component component2 = paramFocusEvent.getComponent();
    Component component3 = paramFocusEvent.getOppositeComponent();
    Component component4 = getHeavyweight(component2);
    synchronized (heavyweightRequests) {
      HeavyweightFocusRequest heavyweightFocusRequest = getFirstHWRequest();
      if (heavyweightFocusRequest == HeavyweightFocusRequest.CLEAR_GLOBAL_FOCUS_OWNER)
        return retargetUnexpectedFocusEvent(paramFocusEvent); 
      if (component2 != null && component4 == null && heavyweightFocusRequest != null && component2 == (heavyweightFocusRequest.getFirstLightweightRequest()).component) {
        component2 = heavyweightFocusRequest.heavyweight;
        component4 = component2;
      } 
      if (heavyweightFocusRequest != null && component4 == heavyweightFocusRequest.heavyweight) {
        heavyweightRequests.removeFirst();
        LightweightFocusRequest lightweightFocusRequest = (LightweightFocusRequest)heavyweightFocusRequest.lightweightRequests.removeFirst();
        Component component = lightweightFocusRequest.component;
        if (component1 != null)
          newFocusOwner = component; 
        boolean bool = (component3 == null || isTemporary(component, component3)) ? false : lightweightFocusRequest.temporary;
        if (heavyweightFocusRequest.lightweightRequests.size() > 0) {
          currentLightweightRequests = heavyweightFocusRequest.lightweightRequests;
          EventQueue.invokeLater(new Runnable() {
                public void run() { KeyboardFocusManager.processCurrentLightweightRequests(); }
              });
        } 
        return new CausedFocusEvent(component, 1004, bool, component3, lightweightFocusRequest.cause);
      } 
      if (component1 != null && component1.getContainingWindow() == component2 && (heavyweightFocusRequest == null || component2 != heavyweightFocusRequest.heavyweight))
        return new CausedFocusEvent(component1, 1004, false, null, CausedFocusEvent.Cause.ACTIVATION); 
      return retargetUnexpectedFocusEvent(paramFocusEvent);
    } 
  }
  
  static FocusEvent retargetFocusLost(FocusEvent paramFocusEvent) {
    assert paramFocusEvent.getID() == 1005;
    Component component1 = getCurrentKeyboardFocusManager().getGlobalFocusOwner();
    Component component2 = paramFocusEvent.getOppositeComponent();
    Component component3 = getHeavyweight(component2);
    synchronized (heavyweightRequests) {
      HeavyweightFocusRequest heavyweightFocusRequest = getFirstHWRequest();
      if (heavyweightFocusRequest == HeavyweightFocusRequest.CLEAR_GLOBAL_FOCUS_OWNER) {
        if (component1 != null) {
          heavyweightRequests.removeFirst();
          return new CausedFocusEvent(component1, 1005, false, null, CausedFocusEvent.Cause.CLEAR_GLOBAL_FOCUS_OWNER);
        } 
      } else {
        if (component2 == null) {
          if (component1 != null)
            return new CausedFocusEvent(component1, 1005, true, null, CausedFocusEvent.Cause.ACTIVATION); 
          return paramFocusEvent;
        } 
        if (heavyweightFocusRequest != null && (component3 == heavyweightFocusRequest.heavyweight || (component3 == null && component2 == (heavyweightFocusRequest.getFirstLightweightRequest()).component))) {
          if (component1 == null)
            return paramFocusEvent; 
          LightweightFocusRequest lightweightFocusRequest = (LightweightFocusRequest)heavyweightFocusRequest.lightweightRequests.getFirst();
          boolean bool = isTemporary(component2, component1) ? true : lightweightFocusRequest.temporary;
          return new CausedFocusEvent(component1, 1005, bool, lightweightFocusRequest.component, lightweightFocusRequest.cause);
        } 
        if (focusedWindowChanged(component2, component1)) {
          if (!paramFocusEvent.isTemporary() && component1 != null)
            paramFocusEvent = new CausedFocusEvent(component1, 1005, true, component2, CausedFocusEvent.Cause.ACTIVATION); 
          return paramFocusEvent;
        } 
      } 
      return retargetUnexpectedFocusEvent(paramFocusEvent);
    } 
  }
  
  static AWTEvent retargetFocusEvent(AWTEvent paramAWTEvent) {
    if (clearingCurrentLightweightRequests)
      return paramAWTEvent; 
    KeyboardFocusManager keyboardFocusManager = getCurrentKeyboardFocusManager();
    if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
      if (paramAWTEvent instanceof FocusEvent || paramAWTEvent instanceof java.awt.event.WindowEvent)
        focusLog.finer(">>> {0}", new Object[] { String.valueOf(paramAWTEvent) }); 
      if (focusLog.isLoggable(PlatformLogger.Level.FINER) && paramAWTEvent instanceof KeyEvent) {
        focusLog.finer("    focus owner is {0}", new Object[] { String.valueOf(keyboardFocusManager.getGlobalFocusOwner()) });
        focusLog.finer(">>> {0}", new Object[] { String.valueOf(paramAWTEvent) });
      } 
    } 
    synchronized (heavyweightRequests) {
      if (newFocusOwner != null && paramAWTEvent.getID() == 1005) {
        FocusEvent focusEvent = (FocusEvent)paramAWTEvent;
        if (keyboardFocusManager.getGlobalFocusOwner() == focusEvent.getComponent() && focusEvent.getOppositeComponent() == newFocusOwner) {
          newFocusOwner = null;
          return paramAWTEvent;
        } 
      } 
    } 
    processCurrentLightweightRequests();
    switch (paramAWTEvent.getID()) {
      case 1004:
        paramAWTEvent = retargetFocusGained((FocusEvent)paramAWTEvent);
        break;
      case 1005:
        paramAWTEvent = retargetFocusLost((FocusEvent)paramAWTEvent);
        break;
    } 
    return paramAWTEvent;
  }
  
  void clearMarkers() {}
  
  static boolean removeFirstRequest() {
    KeyboardFocusManager keyboardFocusManager = getCurrentKeyboardFocusManager();
    synchronized (heavyweightRequests) {
      HeavyweightFocusRequest heavyweightFocusRequest = getFirstHWRequest();
      if (heavyweightFocusRequest != null) {
        heavyweightRequests.removeFirst();
        if (heavyweightFocusRequest.lightweightRequests != null) {
          Iterator iterator = heavyweightFocusRequest.lightweightRequests.iterator();
          while (iterator.hasNext())
            keyboardFocusManager.dequeueKeyEvents(-1L, ((LightweightFocusRequest)iterator.next()).component); 
        } 
      } 
      if (heavyweightRequests.size() == 0)
        keyboardFocusManager.clearMarkers(); 
      return (heavyweightRequests.size() > 0);
    } 
  }
  
  static void removeLastFocusRequest(Component paramComponent) throws SecurityException {
    if (log.isLoggable(PlatformLogger.Level.FINE) && paramComponent == null)
      log.fine("Assertion (heavyweight != null) failed"); 
    KeyboardFocusManager keyboardFocusManager = getCurrentKeyboardFocusManager();
    synchronized (heavyweightRequests) {
      HeavyweightFocusRequest heavyweightFocusRequest = getLastHWRequest();
      if (heavyweightFocusRequest != null && heavyweightFocusRequest.heavyweight == paramComponent)
        heavyweightRequests.removeLast(); 
      if (heavyweightRequests.size() == 0)
        keyboardFocusManager.clearMarkers(); 
    } 
  }
  
  private static boolean focusedWindowChanged(Component paramComponent1, Component paramComponent2) {
    Window window1 = SunToolkit.getContainingWindow(paramComponent1);
    Window window2 = SunToolkit.getContainingWindow(paramComponent2);
    return (window1 == null && window2 == null) ? true : ((window1 == null) ? true : ((window2 == null) ? true : ((window1 != window2))));
  }
  
  private static boolean isTemporary(Component paramComponent1, Component paramComponent2) {
    Window window1 = SunToolkit.getContainingWindow(paramComponent1);
    Window window2 = SunToolkit.getContainingWindow(paramComponent2);
    return (window1 == null && window2 == null) ? false : ((window1 == null) ? true : ((window2 == null) ? false : ((window1 != window2))));
  }
  
  static Component getHeavyweight(Component paramComponent) { return (paramComponent == null || paramComponent.getPeer() == null) ? null : ((paramComponent.getPeer() instanceof java.awt.peer.LightweightPeer) ? paramComponent.getNativeContainer() : paramComponent); }
  
  private static boolean isProxyActiveImpl(KeyEvent paramKeyEvent) {
    if (proxyActive == null)
      proxyActive = (Field)AccessController.doPrivileged(new PrivilegedAction<Field>() {
            public Field run() {
              Field field = null;
              try {
                field = KeyEvent.class.getDeclaredField("isProxyActive");
                if (field != null)
                  field.setAccessible(true); 
              } catch (NoSuchFieldException noSuchFieldException) {
                assert false;
              } 
              return field;
            }
          }); 
    try {
      return proxyActive.getBoolean(paramKeyEvent);
    } catch (IllegalAccessException illegalAccessException) {
      assert false;
      return false;
    } 
  }
  
  static boolean isProxyActive(KeyEvent paramKeyEvent) { return !GraphicsEnvironment.isHeadless() ? isProxyActiveImpl(paramKeyEvent) : 0; }
  
  private static HeavyweightFocusRequest getLastHWRequest() {
    synchronized (heavyweightRequests) {
      return (heavyweightRequests.size() > 0) ? (HeavyweightFocusRequest)heavyweightRequests.getLast() : null;
    } 
  }
  
  private static HeavyweightFocusRequest getFirstHWRequest() {
    synchronized (heavyweightRequests) {
      return (heavyweightRequests.size() > 0) ? (HeavyweightFocusRequest)heavyweightRequests.getFirst() : null;
    } 
  }
  
  private static void checkReplaceKFMPermission() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      if (replaceKeyboardFocusManagerPermission == null)
        replaceKeyboardFocusManagerPermission = new AWTPermission("replaceKeyboardFocusManager"); 
      securityManager.checkPermission(replaceKeyboardFocusManagerPermission);
    } 
  }
  
  private void checkKFMSecurity() {
    if (this != getCurrentKeyboardFocusManager())
      checkReplaceKFMPermission(); 
  }
  
  static  {
    Toolkit.loadLibraries();
    if (!GraphicsEnvironment.isHeadless())
      initIDs(); 
    AWTAccessor.setKeyboardFocusManagerAccessor(new AWTAccessor.KeyboardFocusManagerAccessor() {
          public int shouldNativelyFocusHeavyweight(Component param1Component1, Component param1Component2, boolean param1Boolean1, boolean param1Boolean2, long param1Long, CausedFocusEvent.Cause param1Cause) { return KeyboardFocusManager.shouldNativelyFocusHeavyweight(param1Component1, param1Component2, param1Boolean1, param1Boolean2, param1Long, param1Cause); }
          
          public boolean processSynchronousLightweightTransfer(Component param1Component1, Component param1Component2, boolean param1Boolean1, boolean param1Boolean2, long param1Long) { return KeyboardFocusManager.processSynchronousLightweightTransfer(param1Component1, param1Component2, param1Boolean1, param1Boolean2, param1Long); }
          
          public void removeLastFocusRequest(Component param1Component) throws SecurityException { KeyboardFocusManager.removeLastFocusRequest(param1Component); }
          
          public void setMostRecentFocusOwner(Window param1Window, Component param1Component) { KeyboardFocusManager.setMostRecentFocusOwner(param1Window, param1Component); }
          
          public KeyboardFocusManager getCurrentKeyboardFocusManager(AppContext param1AppContext) { return KeyboardFocusManager.getCurrentKeyboardFocusManager(param1AppContext); }
          
          public Container getCurrentFocusCycleRoot() { return currentFocusCycleRoot; }
        });
    log = PlatformLogger.getLogger("java.awt.KeyboardFocusManager");
    defaultFocusTraversalKeyPropertyNames = new String[] { "forwardDefaultFocusTraversalKeys", "backwardDefaultFocusTraversalKeys", "upCycleDefaultFocusTraversalKeys", "downCycleDefaultFocusTraversalKeys" };
    defaultFocusTraversalKeyStrokes = new AWTKeyStroke[][] { { null, (new AWTKeyStroke[2][0] = AWTKeyStroke.getAWTKeyStroke(9, 0, false)).getAWTKeyStroke(9, 130, false) }, { null, (new AWTKeyStroke[2][0] = AWTKeyStroke.getAWTKeyStroke(9, 65, false)).getAWTKeyStroke(9, 195, false) }, {}, {} };
    mostRecentFocusOwners = new WeakHashMap();
    heavyweightRequests = new LinkedList();
    allowSyncFocusRequests = true;
    newFocusOwner = null;
  }
  
  private static final class HeavyweightFocusRequest {
    final Component heavyweight;
    
    final LinkedList<KeyboardFocusManager.LightweightFocusRequest> lightweightRequests;
    
    static final HeavyweightFocusRequest CLEAR_GLOBAL_FOCUS_OWNER = new HeavyweightFocusRequest();
    
    private HeavyweightFocusRequest() {
      this.heavyweight = null;
      this.lightweightRequests = null;
    }
    
    HeavyweightFocusRequest(Component param1Component1, Component param1Component2, boolean param1Boolean, CausedFocusEvent.Cause param1Cause) {
      if (log.isLoggable(PlatformLogger.Level.FINE) && param1Component1 == null)
        log.fine("Assertion (heavyweight != null) failed"); 
      this.heavyweight = param1Component1;
      this.lightweightRequests = new LinkedList();
      addLightweightRequest(param1Component2, param1Boolean, param1Cause);
    }
    
    boolean addLightweightRequest(Component param1Component, boolean param1Boolean, CausedFocusEvent.Cause param1Cause) {
      if (log.isLoggable(PlatformLogger.Level.FINE)) {
        if (this == CLEAR_GLOBAL_FOCUS_OWNER)
          log.fine("Assertion (this != HeavyweightFocusRequest.CLEAR_GLOBAL_FOCUS_OWNER) failed"); 
        if (param1Component == null)
          log.fine("Assertion (descendant != null) failed"); 
      } 
      Component component = (this.lightweightRequests.size() > 0) ? ((KeyboardFocusManager.LightweightFocusRequest)this.lightweightRequests.getLast()).component : null;
      if (param1Component != component) {
        this.lightweightRequests.add(new KeyboardFocusManager.LightweightFocusRequest(param1Component, param1Boolean, param1Cause));
        return true;
      } 
      return false;
    }
    
    KeyboardFocusManager.LightweightFocusRequest getFirstLightweightRequest() { return (this == CLEAR_GLOBAL_FOCUS_OWNER) ? null : (KeyboardFocusManager.LightweightFocusRequest)this.lightweightRequests.getFirst(); }
    
    public String toString() {
      boolean bool = true;
      null = "HeavyweightFocusRequest[heavweight=" + this.heavyweight + ",lightweightRequests=";
      if (this.lightweightRequests == null) {
        null = null + null;
      } else {
        null = null + "[";
        for (KeyboardFocusManager.LightweightFocusRequest lightweightFocusRequest : this.lightweightRequests) {
          if (bool) {
            bool = false;
          } else {
            null = null + ",";
          } 
          null = null + lightweightFocusRequest;
        } 
        null = null + "]";
      } 
      return null + "]";
    }
  }
  
  private static final class LightweightFocusRequest {
    final Component component;
    
    final boolean temporary;
    
    final CausedFocusEvent.Cause cause;
    
    LightweightFocusRequest(Component param1Component, boolean param1Boolean, CausedFocusEvent.Cause param1Cause) {
      this.component = param1Component;
      this.temporary = param1Boolean;
      this.cause = param1Cause;
    }
    
    public String toString() { return "LightweightFocusRequest[component=" + this.component + ",temporary=" + this.temporary + ", cause=" + this.cause + "]"; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\KeyboardFocusManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */