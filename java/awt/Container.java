package java.awt;

import java.awt.dnd.DropTarget;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.KeyEvent;
import java.awt.peer.ComponentPeer;
import java.awt.peer.ContainerPeer;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.OptionalDataException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.swing.JInternalFrame;
import sun.awt.AWTAccessor;
import sun.awt.AppContext;
import sun.awt.CausedFocusEvent;
import sun.awt.PeerEvent;
import sun.awt.SunToolkit;
import sun.java2d.pipe.Region;
import sun.security.action.GetBooleanAction;
import sun.util.logging.PlatformLogger;

public class Container extends Component {
  private static final PlatformLogger log;
  
  private static final PlatformLogger eventLog = (log = PlatformLogger.getLogger("java.awt.Container")).getLogger("java.awt.event.Container");
  
  private static final Component[] EMPTY_ARRAY = new Component[0];
  
  private List<Component> component = new ArrayList();
  
  LayoutManager layoutMgr;
  
  private LightweightDispatcher dispatcher;
  
  private FocusTraversalPolicy focusTraversalPolicy;
  
  private boolean focusCycleRoot = false;
  
  private boolean focusTraversalPolicyProvider;
  
  private Set<Thread> printingThreads;
  
  private boolean printing = false;
  
  ContainerListener containerListener;
  
  int listeningChildren;
  
  int listeningBoundsChildren;
  
  int descendantsCount;
  
  Color preserveBackgroundColor = null;
  
  private static final long serialVersionUID = 4613797578919906343L;
  
  static final boolean INCLUDE_SELF = true;
  
  static final boolean SEARCH_HEAVYWEIGHTS = true;
  
  private int numOfHWComponents = 0;
  
  private int numOfLWComponents = 0;
  
  private static final PlatformLogger mixingLog = PlatformLogger.getLogger("java.awt.mixing.Container");
  
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("ncomponents", int.class), new ObjectStreamField("component", Component[].class), new ObjectStreamField("layoutMgr", LayoutManager.class), new ObjectStreamField("dispatcher", LightweightDispatcher.class), new ObjectStreamField("maxSize", Dimension.class), new ObjectStreamField("focusCycleRoot", boolean.class), new ObjectStreamField("containerSerializedDataVersion", int.class), new ObjectStreamField("focusTraversalPolicyProvider", boolean.class) };
  
  private static final boolean isJavaAwtSmartInvalidate;
  
  private static boolean descendUnconditionallyWhenValidating;
  
  Component modalComp;
  
  AppContext modalAppContext;
  
  private int containerSerializedDataVersion = 1;
  
  private static native void initIDs();
  
  void initializeFocusTraversalKeys() { this.focusTraversalKeys = new Set[4]; }
  
  public int getComponentCount() { return countComponents(); }
  
  @Deprecated
  public int countComponents() { return this.component.size(); }
  
  public Component getComponent(int paramInt) {
    try {
      return (Component)this.component.get(paramInt);
    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
      throw new ArrayIndexOutOfBoundsException("No such child: " + paramInt);
    } 
  }
  
  public Component[] getComponents() { return getComponents_NoClientCode(); }
  
  final Component[] getComponents_NoClientCode() { return (Component[])this.component.toArray(EMPTY_ARRAY); }
  
  Component[] getComponentsSync() {
    synchronized (getTreeLock()) {
      return getComponents();
    } 
  }
  
  public Insets getInsets() { return insets(); }
  
  @Deprecated
  public Insets insets() {
    ComponentPeer componentPeer = this.peer;
    if (componentPeer instanceof ContainerPeer) {
      ContainerPeer containerPeer = (ContainerPeer)componentPeer;
      return (Insets)containerPeer.getInsets().clone();
    } 
    return new Insets(0, 0, 0, 0);
  }
  
  public Component add(Component paramComponent) {
    addImpl(paramComponent, null, -1);
    return paramComponent;
  }
  
  public Component add(String paramString, Component paramComponent) {
    addImpl(paramComponent, paramString, -1);
    return paramComponent;
  }
  
  public Component add(Component paramComponent, int paramInt) {
    addImpl(paramComponent, null, paramInt);
    return paramComponent;
  }
  
  private void checkAddToSelf(Component paramComponent) {
    if (paramComponent instanceof Container)
      for (Container container = this; container != null; container = container.parent) {
        if (container == paramComponent)
          throw new IllegalArgumentException("adding container's parent to itself"); 
      }  
  }
  
  private void checkNotAWindow(Component paramComponent) {
    if (paramComponent instanceof Window)
      throw new IllegalArgumentException("adding a window to a container"); 
  }
  
  private void checkAdding(Component paramComponent, int paramInt) {
    checkTreeLock();
    GraphicsConfiguration graphicsConfiguration = getGraphicsConfiguration();
    if (paramInt > this.component.size() || paramInt < 0)
      throw new IllegalArgumentException("illegal component position"); 
    if (paramComponent.parent == this && paramInt == this.component.size())
      throw new IllegalArgumentException("illegal component position " + paramInt + " should be less then " + this.component.size()); 
    checkAddToSelf(paramComponent);
    checkNotAWindow(paramComponent);
    Window window1 = getContainingWindow();
    Window window2 = paramComponent.getContainingWindow();
    if (window1 != window2)
      throw new IllegalArgumentException("component and container should be in the same top-level window"); 
    if (graphicsConfiguration != null)
      paramComponent.checkGD(graphicsConfiguration.getDevice().getIDstring()); 
  }
  
  private boolean removeDelicately(Component paramComponent, Container paramContainer, int paramInt) {
    checkTreeLock();
    int i = getComponentZOrder(paramComponent);
    boolean bool = isRemoveNotifyNeeded(paramComponent, this, paramContainer);
    if (bool)
      paramComponent.removeNotify(); 
    if (paramContainer != this) {
      if (this.layoutMgr != null)
        this.layoutMgr.removeLayoutComponent(paramComponent); 
      adjustListeningChildren(32768L, -paramComponent.numListening(32768L));
      adjustListeningChildren(65536L, -paramComponent.numListening(65536L));
      adjustDescendants(-paramComponent.countHierarchyMembers());
      paramComponent.parent = null;
      if (bool)
        paramComponent.setGraphicsConfiguration(null); 
      this.component.remove(i);
      invalidateIfValid();
    } else {
      this.component.remove(i);
      this.component.add(paramInt, paramComponent);
    } 
    if (paramComponent.parent == null) {
      if (this.containerListener != null || (this.eventMask & 0x2L) != 0L || Toolkit.enabledOnToolkit(2L)) {
        ContainerEvent containerEvent = new ContainerEvent(this, 301, paramComponent);
        dispatchEvent(containerEvent);
      } 
      paramComponent.createHierarchyEvents(1400, paramComponent, this, 1L, Toolkit.enabledOnToolkit(32768L));
      if (this.peer != null && this.layoutMgr == null && isVisible())
        updateCursorImmediately(); 
    } 
    return bool;
  }
  
  boolean canContainFocusOwner(Component paramComponent) {
    if (!isEnabled() || !isDisplayable() || !isVisible() || !isFocusable())
      return false; 
    if (isFocusCycleRoot()) {
      FocusTraversalPolicy focusTraversalPolicy1 = getFocusTraversalPolicy();
      if (focusTraversalPolicy1 instanceof DefaultFocusTraversalPolicy && !((DefaultFocusTraversalPolicy)focusTraversalPolicy1).accept(paramComponent))
        return false; 
    } 
    synchronized (getTreeLock()) {
      if (this.parent != null)
        return this.parent.canContainFocusOwner(paramComponent); 
    } 
    return true;
  }
  
  final boolean hasHeavyweightDescendants() {
    checkTreeLock();
    return (this.numOfHWComponents > 0);
  }
  
  final boolean hasLightweightDescendants() {
    checkTreeLock();
    return (this.numOfLWComponents > 0);
  }
  
  Container getHeavyweightContainer() {
    checkTreeLock();
    return (this.peer != null && !(this.peer instanceof java.awt.peer.LightweightPeer)) ? this : getNativeContainer();
  }
  
  private static boolean isRemoveNotifyNeeded(Component paramComponent, Container paramContainer1, Container paramContainer2) {
    if (paramContainer1 == null)
      return false; 
    if (paramComponent.peer == null)
      return false; 
    if (paramContainer2.peer == null)
      return true; 
    if (paramComponent.isLightweight()) {
      boolean bool = paramComponent instanceof Container;
      if (!bool || (bool && !((Container)paramComponent).hasHeavyweightDescendants()))
        return false; 
    } 
    Container container1 = paramContainer1.getHeavyweightContainer();
    Container container2 = paramContainer2.getHeavyweightContainer();
    return (container1 != container2) ? (!paramComponent.peer.isReparentSupported()) : false;
  }
  
  public void setComponentZOrder(Component paramComponent, int paramInt) {
    synchronized (getTreeLock()) {
      Container container = paramComponent.parent;
      int i = getComponentZOrder(paramComponent);
      if (container == this && paramInt == i)
        return; 
      checkAdding(paramComponent, paramInt);
      boolean bool = (container != null) ? container.removeDelicately(paramComponent, this, paramInt) : 0;
      addDelicately(paramComponent, container, paramInt);
      if (!bool && i != -1)
        paramComponent.mixOnZOrderChanging(i, paramInt); 
    } 
  }
  
  private void reparentTraverse(ContainerPeer paramContainerPeer, Container paramContainer) {
    checkTreeLock();
    for (byte b = 0; b < paramContainer.getComponentCount(); b++) {
      Component component1 = paramContainer.getComponent(b);
      if (component1.isLightweight()) {
        if (component1 instanceof Container)
          reparentTraverse(paramContainerPeer, (Container)component1); 
      } else {
        component1.getPeer().reparent(paramContainerPeer);
      } 
    } 
  }
  
  private void reparentChild(Component paramComponent) {
    checkTreeLock();
    if (paramComponent == null)
      return; 
    if (paramComponent.isLightweight()) {
      if (paramComponent instanceof Container)
        reparentTraverse((ContainerPeer)getPeer(), (Container)paramComponent); 
    } else {
      paramComponent.getPeer().reparent((ContainerPeer)getPeer());
    } 
  }
  
  private void addDelicately(Component paramComponent, Container paramContainer, int paramInt) {
    checkTreeLock();
    if (paramContainer != this) {
      if (paramInt == -1) {
        this.component.add(paramComponent);
      } else {
        this.component.add(paramInt, paramComponent);
      } 
      paramComponent.parent = this;
      paramComponent.setGraphicsConfiguration(getGraphicsConfiguration());
      adjustListeningChildren(32768L, paramComponent.numListening(32768L));
      adjustListeningChildren(65536L, paramComponent.numListening(65536L));
      adjustDescendants(paramComponent.countHierarchyMembers());
    } else if (paramInt < this.component.size()) {
      this.component.set(paramInt, paramComponent);
    } 
    invalidateIfValid();
    if (this.peer != null)
      if (paramComponent.peer == null) {
        paramComponent.addNotify();
      } else {
        Container container1 = getHeavyweightContainer();
        Container container2 = paramContainer.getHeavyweightContainer();
        if (container2 != container1)
          container1.reparentChild(paramComponent); 
        paramComponent.updateZOrder();
        if (!paramComponent.isLightweight() && isLightweight())
          paramComponent.relocateComponent(); 
      }  
    if (paramContainer != this) {
      if (this.layoutMgr != null)
        if (this.layoutMgr instanceof LayoutManager2) {
          ((LayoutManager2)this.layoutMgr).addLayoutComponent(paramComponent, null);
        } else {
          this.layoutMgr.addLayoutComponent(null, paramComponent);
        }  
      if (this.containerListener != null || (this.eventMask & 0x2L) != 0L || Toolkit.enabledOnToolkit(2L)) {
        ContainerEvent containerEvent = new ContainerEvent(this, 300, paramComponent);
        dispatchEvent(containerEvent);
      } 
      paramComponent.createHierarchyEvents(1400, paramComponent, this, 1L, Toolkit.enabledOnToolkit(32768L));
      if (paramComponent.isFocusOwner() && !paramComponent.canBeFocusOwnerRecursively()) {
        paramComponent.transferFocus();
      } else if (paramComponent instanceof Container) {
        Component component1 = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        if (component1 != null && isParentOf(component1) && !component1.canBeFocusOwnerRecursively())
          component1.transferFocus(); 
      } 
    } else {
      paramComponent.createHierarchyEvents(1400, paramComponent, this, 1400L, Toolkit.enabledOnToolkit(32768L));
    } 
    if (this.peer != null && this.layoutMgr == null && isVisible())
      updateCursorImmediately(); 
  }
  
  public int getComponentZOrder(Component paramComponent) {
    if (paramComponent == null)
      return -1; 
    synchronized (getTreeLock()) {
      if (paramComponent.parent != this)
        return -1; 
      return this.component.indexOf(paramComponent);
    } 
  }
  
  public void add(Component paramComponent, Object paramObject) { addImpl(paramComponent, paramObject, -1); }
  
  public void add(Component paramComponent, Object paramObject, int paramInt) { addImpl(paramComponent, paramObject, paramInt); }
  
  protected void addImpl(Component paramComponent, Object paramObject, int paramInt) {
    synchronized (getTreeLock()) {
      GraphicsConfiguration graphicsConfiguration = getGraphicsConfiguration();
      if (paramInt > this.component.size() || (paramInt < 0 && paramInt != -1))
        throw new IllegalArgumentException("illegal component position"); 
      checkAddToSelf(paramComponent);
      checkNotAWindow(paramComponent);
      if (paramComponent.parent != null) {
        paramComponent.parent.remove(paramComponent);
        if (paramInt > this.component.size())
          throw new IllegalArgumentException("illegal component position"); 
      } 
      if (graphicsConfiguration != null)
        paramComponent.checkGD(graphicsConfiguration.getDevice().getIDstring()); 
      if (paramInt == -1) {
        this.component.add(paramComponent);
      } else {
        this.component.add(paramInt, paramComponent);
      } 
      paramComponent.parent = this;
      paramComponent.setGraphicsConfiguration(graphicsConfiguration);
      adjustListeningChildren(32768L, paramComponent.numListening(32768L));
      adjustListeningChildren(65536L, paramComponent.numListening(65536L));
      adjustDescendants(paramComponent.countHierarchyMembers());
      invalidateIfValid();
      if (this.peer != null)
        paramComponent.addNotify(); 
      if (this.layoutMgr != null)
        if (this.layoutMgr instanceof LayoutManager2) {
          ((LayoutManager2)this.layoutMgr).addLayoutComponent(paramComponent, paramObject);
        } else if (paramObject instanceof String) {
          this.layoutMgr.addLayoutComponent((String)paramObject, paramComponent);
        }  
      if (this.containerListener != null || (this.eventMask & 0x2L) != 0L || Toolkit.enabledOnToolkit(2L)) {
        ContainerEvent containerEvent = new ContainerEvent(this, 300, paramComponent);
        dispatchEvent(containerEvent);
      } 
      paramComponent.createHierarchyEvents(1400, paramComponent, this, 1L, Toolkit.enabledOnToolkit(32768L));
      if (this.peer != null && this.layoutMgr == null && isVisible())
        updateCursorImmediately(); 
    } 
  }
  
  boolean updateGraphicsData(GraphicsConfiguration paramGraphicsConfiguration) {
    checkTreeLock();
    boolean bool = super.updateGraphicsData(paramGraphicsConfiguration);
    for (Component component1 : this.component) {
      if (component1 != null)
        bool |= component1.updateGraphicsData(paramGraphicsConfiguration); 
    } 
    return bool;
  }
  
  void checkGD(String paramString) {
    for (Component component1 : this.component) {
      if (component1 != null)
        component1.checkGD(paramString); 
    } 
  }
  
  public void remove(int paramInt) {
    synchronized (getTreeLock()) {
      if (paramInt < 0 || paramInt >= this.component.size())
        throw new ArrayIndexOutOfBoundsException(paramInt); 
      Component component1 = (Component)this.component.get(paramInt);
      if (this.peer != null)
        component1.removeNotify(); 
      if (this.layoutMgr != null)
        this.layoutMgr.removeLayoutComponent(component1); 
      adjustListeningChildren(32768L, -component1.numListening(32768L));
      adjustListeningChildren(65536L, -component1.numListening(65536L));
      adjustDescendants(-component1.countHierarchyMembers());
      component1.parent = null;
      this.component.remove(paramInt);
      component1.setGraphicsConfiguration(null);
      invalidateIfValid();
      if (this.containerListener != null || (this.eventMask & 0x2L) != 0L || Toolkit.enabledOnToolkit(2L)) {
        ContainerEvent containerEvent = new ContainerEvent(this, 301, component1);
        dispatchEvent(containerEvent);
      } 
      component1.createHierarchyEvents(1400, component1, this, 1L, Toolkit.enabledOnToolkit(32768L));
      if (this.peer != null && this.layoutMgr == null && isVisible())
        updateCursorImmediately(); 
    } 
  }
  
  public void remove(Component paramComponent) {
    synchronized (getTreeLock()) {
      if (paramComponent.parent == this) {
        int i = this.component.indexOf(paramComponent);
        if (i >= 0)
          remove(i); 
      } 
    } 
  }
  
  public void removeAll() {
    synchronized (getTreeLock()) {
      adjustListeningChildren(32768L, -this.listeningChildren);
      adjustListeningChildren(65536L, -this.listeningBoundsChildren);
      adjustDescendants(-this.descendantsCount);
      while (!this.component.isEmpty()) {
        Component component1 = (Component)this.component.remove(this.component.size() - 1);
        if (this.peer != null)
          component1.removeNotify(); 
        if (this.layoutMgr != null)
          this.layoutMgr.removeLayoutComponent(component1); 
        component1.parent = null;
        component1.setGraphicsConfiguration(null);
        if (this.containerListener != null || (this.eventMask & 0x2L) != 0L || Toolkit.enabledOnToolkit(2L)) {
          ContainerEvent containerEvent = new ContainerEvent(this, 301, component1);
          dispatchEvent(containerEvent);
        } 
        component1.createHierarchyEvents(1400, component1, this, 1L, Toolkit.enabledOnToolkit(32768L));
      } 
      if (this.peer != null && this.layoutMgr == null && isVisible())
        updateCursorImmediately(); 
      invalidateIfValid();
    } 
  }
  
  int numListening(long paramLong) {
    int i = super.numListening(paramLong);
    if (paramLong == 32768L) {
      if (eventLog.isLoggable(PlatformLogger.Level.FINE)) {
        int j = 0;
        for (Component component1 : this.component)
          j += component1.numListening(paramLong); 
        if (this.listeningChildren != j)
          eventLog.fine("Assertion (listeningChildren == sum) failed"); 
      } 
      return this.listeningChildren + i;
    } 
    if (paramLong == 65536L) {
      if (eventLog.isLoggable(PlatformLogger.Level.FINE)) {
        int j = 0;
        for (Component component1 : this.component)
          j += component1.numListening(paramLong); 
        if (this.listeningBoundsChildren != j)
          eventLog.fine("Assertion (listeningBoundsChildren == sum) failed"); 
      } 
      return this.listeningBoundsChildren + i;
    } 
    if (eventLog.isLoggable(PlatformLogger.Level.FINE))
      eventLog.fine("This code must never be reached"); 
    return i;
  }
  
  void adjustListeningChildren(long paramLong, int paramInt) {
    if (eventLog.isLoggable(PlatformLogger.Level.FINE)) {
      boolean bool = (paramLong == 32768L || paramLong == 65536L || paramLong == 98304L) ? 1 : 0;
      if (!bool)
        eventLog.fine("Assertion failed"); 
    } 
    if (paramInt == 0)
      return; 
    if ((paramLong & 0x8000L) != 0L)
      this.listeningChildren += paramInt; 
    if ((paramLong & 0x10000L) != 0L)
      this.listeningBoundsChildren += paramInt; 
    adjustListeningChildrenOnParent(paramLong, paramInt);
  }
  
  void adjustDescendants(int paramInt) {
    if (paramInt == 0)
      return; 
    this.descendantsCount += paramInt;
    adjustDecendantsOnParent(paramInt);
  }
  
  void adjustDecendantsOnParent(int paramInt) {
    if (this.parent != null)
      this.parent.adjustDescendants(paramInt); 
  }
  
  int countHierarchyMembers() {
    if (log.isLoggable(PlatformLogger.Level.FINE)) {
      int i = 0;
      for (Component component1 : this.component)
        i += component1.countHierarchyMembers(); 
      if (this.descendantsCount != i)
        log.fine("Assertion (descendantsCount == sum) failed"); 
    } 
    return this.descendantsCount + 1;
  }
  
  private int getListenersCount(int paramInt, boolean paramBoolean) {
    checkTreeLock();
    if (paramBoolean)
      return this.descendantsCount; 
    switch (paramInt) {
      case 1400:
        return this.listeningChildren;
      case 1401:
      case 1402:
        return this.listeningBoundsChildren;
    } 
    return 0;
  }
  
  final int createHierarchyEvents(int paramInt, Component paramComponent, Container paramContainer, long paramLong, boolean paramBoolean) {
    checkTreeLock();
    int i = getListenersCount(paramInt, paramBoolean);
    int j = i;
    for (byte b = 0; j > 0; b++)
      j -= ((Component)this.component.get(b)).createHierarchyEvents(paramInt, paramComponent, paramContainer, paramLong, paramBoolean); 
    return i + super.createHierarchyEvents(paramInt, paramComponent, paramContainer, paramLong, paramBoolean);
  }
  
  final void createChildHierarchyEvents(int paramInt, long paramLong, boolean paramBoolean) {
    checkTreeLock();
    if (this.component.isEmpty())
      return; 
    int i = getListenersCount(paramInt, paramBoolean);
    int j = i;
    for (byte b = 0; j > 0; b++)
      j -= ((Component)this.component.get(b)).createHierarchyEvents(paramInt, this, this.parent, paramLong, paramBoolean); 
  }
  
  public LayoutManager getLayout() { return this.layoutMgr; }
  
  public void setLayout(LayoutManager paramLayoutManager) {
    this.layoutMgr = paramLayoutManager;
    invalidateIfValid();
  }
  
  public void doLayout() { layout(); }
  
  @Deprecated
  public void layout() {
    LayoutManager layoutManager = this.layoutMgr;
    if (layoutManager != null)
      layoutManager.layoutContainer(this); 
  }
  
  public boolean isValidateRoot() { return false; }
  
  void invalidateParent() {
    if (!isJavaAwtSmartInvalidate || !isValidateRoot())
      super.invalidateParent(); 
  }
  
  public void invalidate() {
    LayoutManager layoutManager = this.layoutMgr;
    if (layoutManager instanceof LayoutManager2) {
      LayoutManager2 layoutManager2 = (LayoutManager2)layoutManager;
      layoutManager2.invalidateLayout(this);
    } 
    super.invalidate();
  }
  
  public void validate() {
    boolean bool = false;
    synchronized (getTreeLock()) {
      if ((!isValid() || descendUnconditionallyWhenValidating) && this.peer != null) {
        ContainerPeer containerPeer = null;
        if (this.peer instanceof ContainerPeer)
          containerPeer = (ContainerPeer)this.peer; 
        if (containerPeer != null)
          containerPeer.beginValidate(); 
        validateTree();
        if (containerPeer != null) {
          containerPeer.endValidate();
          if (!descendUnconditionallyWhenValidating)
            bool = isVisible(); 
        } 
      } 
    } 
    if (bool)
      updateCursorImmediately(); 
  }
  
  final void validateUnconditionally() {
    boolean bool = false;
    synchronized (getTreeLock()) {
      descendUnconditionallyWhenValidating = true;
      validate();
      if (this.peer instanceof ContainerPeer)
        bool = isVisible(); 
      descendUnconditionallyWhenValidating = false;
    } 
    if (bool)
      updateCursorImmediately(); 
  }
  
  protected void validateTree() {
    checkTreeLock();
    if (!isValid() || descendUnconditionallyWhenValidating) {
      if (this.peer instanceof ContainerPeer)
        ((ContainerPeer)this.peer).beginLayout(); 
      if (!isValid())
        doLayout(); 
      for (byte b = 0; b < this.component.size(); b++) {
        Component component1 = (Component)this.component.get(b);
        if (component1 instanceof Container && !(component1 instanceof Window) && (!component1.isValid() || descendUnconditionallyWhenValidating)) {
          ((Container)component1).validateTree();
        } else {
          component1.validate();
        } 
      } 
      if (this.peer instanceof ContainerPeer)
        ((ContainerPeer)this.peer).endLayout(); 
    } 
    super.validate();
  }
  
  void invalidateTree() {
    synchronized (getTreeLock()) {
      for (byte b = 0; b < this.component.size(); b++) {
        Component component1 = (Component)this.component.get(b);
        if (component1 instanceof Container) {
          ((Container)component1).invalidateTree();
        } else {
          component1.invalidateIfValid();
        } 
      } 
      invalidateIfValid();
    } 
  }
  
  public void setFont(Font paramFont) {
    boolean bool = false;
    Font font1 = getFont();
    super.setFont(paramFont);
    Font font2 = getFont();
    if (font2 != font1 && (font1 == null || !font1.equals(font2)))
      invalidateTree(); 
  }
  
  public Dimension getPreferredSize() { return preferredSize(); }
  
  @Deprecated
  public Dimension preferredSize() {
    Dimension dimension = this.prefSize;
    if (dimension == null || (!isPreferredSizeSet() && !isValid()))
      synchronized (getTreeLock()) {
        this.prefSize = (this.layoutMgr != null) ? this.layoutMgr.preferredLayoutSize(this) : super.preferredSize();
        dimension = this.prefSize;
      }  
    return (dimension != null) ? new Dimension(dimension) : dimension;
  }
  
  public Dimension getMinimumSize() { return minimumSize(); }
  
  @Deprecated
  public Dimension minimumSize() {
    Dimension dimension = this.minSize;
    if (dimension == null || (!isMinimumSizeSet() && !isValid()))
      synchronized (getTreeLock()) {
        this.minSize = (this.layoutMgr != null) ? this.layoutMgr.minimumLayoutSize(this) : super.minimumSize();
        dimension = this.minSize;
      }  
    return (dimension != null) ? new Dimension(dimension) : dimension;
  }
  
  public Dimension getMaximumSize() {
    Dimension dimension = this.maxSize;
    if (dimension == null || (!isMaximumSizeSet() && !isValid()))
      synchronized (getTreeLock()) {
        if (this.layoutMgr instanceof LayoutManager2) {
          LayoutManager2 layoutManager2 = (LayoutManager2)this.layoutMgr;
          this.maxSize = layoutManager2.maximumLayoutSize(this);
        } else {
          this.maxSize = super.getMaximumSize();
        } 
        dimension = this.maxSize;
      }  
    return (dimension != null) ? new Dimension(dimension) : dimension;
  }
  
  public float getAlignmentX() {
    float f;
    if (this.layoutMgr instanceof LayoutManager2) {
      synchronized (getTreeLock()) {
        LayoutManager2 layoutManager2 = (LayoutManager2)this.layoutMgr;
        f = layoutManager2.getLayoutAlignmentX(this);
      } 
    } else {
      f = super.getAlignmentX();
    } 
    return f;
  }
  
  public float getAlignmentY() {
    float f;
    if (this.layoutMgr instanceof LayoutManager2) {
      synchronized (getTreeLock()) {
        LayoutManager2 layoutManager2 = (LayoutManager2)this.layoutMgr;
        f = layoutManager2.getLayoutAlignmentY(this);
      } 
    } else {
      f = super.getAlignmentY();
    } 
    return f;
  }
  
  public void paint(Graphics paramGraphics) {
    if (isShowing()) {
      synchronized (getObjectLock()) {
        if (this.printing && this.printingThreads.contains(Thread.currentThread()))
          return; 
      } 
      GraphicsCallback.PaintCallback.getInstance().runComponents(getComponentsSync(), paramGraphics, 2);
    } 
  }
  
  public void update(Graphics paramGraphics) {
    if (isShowing()) {
      if (!(this.peer instanceof java.awt.peer.LightweightPeer))
        paramGraphics.clearRect(0, 0, this.width, this.height); 
      paint(paramGraphics);
    } 
  }
  
  public void print(Graphics paramGraphics) {
    if (isShowing()) {
      thread = Thread.currentThread();
      try {
        synchronized (getObjectLock()) {
          if (this.printingThreads == null)
            this.printingThreads = new HashSet(); 
          this.printingThreads.add(thread);
          this.printing = true;
        } 
        super.print(paramGraphics);
      } finally {
        synchronized (getObjectLock()) {
          this.printingThreads.remove(thread);
          this.printing = !this.printingThreads.isEmpty();
        } 
      } 
      GraphicsCallback.PrintCallback.getInstance().runComponents(getComponentsSync(), paramGraphics, 2);
    } 
  }
  
  public void paintComponents(Graphics paramGraphics) {
    if (isShowing())
      GraphicsCallback.PaintAllCallback.getInstance().runComponents(getComponentsSync(), paramGraphics, 4); 
  }
  
  void lightweightPaint(Graphics paramGraphics) {
    super.lightweightPaint(paramGraphics);
    paintHeavyweightComponents(paramGraphics);
  }
  
  void paintHeavyweightComponents(Graphics paramGraphics) {
    if (isShowing())
      GraphicsCallback.PaintHeavyweightComponentsCallback.getInstance().runComponents(getComponentsSync(), paramGraphics, 3); 
  }
  
  public void printComponents(Graphics paramGraphics) {
    if (isShowing())
      GraphicsCallback.PrintAllCallback.getInstance().runComponents(getComponentsSync(), paramGraphics, 4); 
  }
  
  void lightweightPrint(Graphics paramGraphics) {
    super.lightweightPrint(paramGraphics);
    printHeavyweightComponents(paramGraphics);
  }
  
  void printHeavyweightComponents(Graphics paramGraphics) {
    if (isShowing())
      GraphicsCallback.PrintHeavyweightComponentsCallback.getInstance().runComponents(getComponentsSync(), paramGraphics, 3); 
  }
  
  public void addContainerListener(ContainerListener paramContainerListener) {
    if (paramContainerListener == null)
      return; 
    this.containerListener = AWTEventMulticaster.add(this.containerListener, paramContainerListener);
    this.newEventsOnly = true;
  }
  
  public void removeContainerListener(ContainerListener paramContainerListener) {
    if (paramContainerListener == null)
      return; 
    this.containerListener = AWTEventMulticaster.remove(this.containerListener, paramContainerListener);
  }
  
  public ContainerListener[] getContainerListeners() { return (ContainerListener[])getListeners(ContainerListener.class); }
  
  public <T extends java.util.EventListener> T[] getListeners(Class<T> paramClass) {
    ContainerListener containerListener1 = null;
    if (paramClass == ContainerListener.class) {
      containerListener1 = this.containerListener;
    } else {
      return (T[])super.getListeners(paramClass);
    } 
    return (T[])AWTEventMulticaster.getListeners(containerListener1, paramClass);
  }
  
  boolean eventEnabled(AWTEvent paramAWTEvent) {
    int i = paramAWTEvent.getID();
    return (i == 300 || i == 301) ? (((this.eventMask & 0x2L) != 0L || this.containerListener != null)) : super.eventEnabled(paramAWTEvent);
  }
  
  protected void processEvent(AWTEvent paramAWTEvent) {
    if (paramAWTEvent instanceof ContainerEvent) {
      processContainerEvent((ContainerEvent)paramAWTEvent);
      return;
    } 
    super.processEvent(paramAWTEvent);
  }
  
  protected void processContainerEvent(ContainerEvent paramContainerEvent) {
    ContainerListener containerListener1 = this.containerListener;
    if (containerListener1 != null)
      switch (paramContainerEvent.getID()) {
        case 300:
          containerListener1.componentAdded(paramContainerEvent);
          break;
        case 301:
          containerListener1.componentRemoved(paramContainerEvent);
          break;
      }  
  }
  
  void dispatchEventImpl(AWTEvent paramAWTEvent) {
    if (this.dispatcher != null && this.dispatcher.dispatchEvent(paramAWTEvent)) {
      paramAWTEvent.consume();
      if (this.peer != null)
        this.peer.handleEvent(paramAWTEvent); 
      return;
    } 
    super.dispatchEventImpl(paramAWTEvent);
    synchronized (getTreeLock()) {
      switch (paramAWTEvent.getID()) {
        case 101:
          createChildHierarchyEvents(1402, 0L, Toolkit.enabledOnToolkit(65536L));
          break;
        case 100:
          createChildHierarchyEvents(1401, 0L, Toolkit.enabledOnToolkit(65536L));
          break;
      } 
    } 
  }
  
  void dispatchEventToSelf(AWTEvent paramAWTEvent) { super.dispatchEventImpl(paramAWTEvent); }
  
  Component getMouseEventTarget(int paramInt1, int paramInt2, boolean paramBoolean) { return getMouseEventTarget(paramInt1, paramInt2, paramBoolean, MouseEventTargetFilter.FILTER, false); }
  
  Component getDropTargetEventTarget(int paramInt1, int paramInt2, boolean paramBoolean) { return getMouseEventTarget(paramInt1, paramInt2, paramBoolean, DropTargetEventTargetFilter.FILTER, true); }
  
  private Component getMouseEventTarget(int paramInt1, int paramInt2, boolean paramBoolean1, EventTargetFilter paramEventTargetFilter, boolean paramBoolean2) {
    Component component1 = null;
    if (paramBoolean2)
      component1 = getMouseEventTargetImpl(paramInt1, paramInt2, paramBoolean1, paramEventTargetFilter, true, paramBoolean2); 
    if (component1 == null || component1 == this)
      component1 = getMouseEventTargetImpl(paramInt1, paramInt2, paramBoolean1, paramEventTargetFilter, false, paramBoolean2); 
    return component1;
  }
  
  private Component getMouseEventTargetImpl(int paramInt1, int paramInt2, boolean paramBoolean1, EventTargetFilter paramEventTargetFilter, boolean paramBoolean2, boolean paramBoolean3) {
    synchronized (getTreeLock()) {
      byte b;
      for (b = 0; b < this.component.size(); b++) {
        Component component1 = (Component)this.component.get(b);
        if (component1 != null && component1.visible && ((!paramBoolean2 && component1.peer instanceof java.awt.peer.LightweightPeer) || (paramBoolean2 && !(component1.peer instanceof java.awt.peer.LightweightPeer))) && component1.contains(paramInt1 - component1.x, paramInt2 - component1.y))
          if (component1 instanceof Container) {
            Container container = (Container)component1;
            Component component2 = container.getMouseEventTarget(paramInt1 - container.x, paramInt2 - container.y, paramBoolean1, paramEventTargetFilter, paramBoolean3);
            if (component2 != null)
              return component2; 
          } else if (paramEventTargetFilter.accept(component1)) {
            return component1;
          }  
      } 
      b = (this.peer instanceof java.awt.peer.LightweightPeer || paramBoolean1) ? 1 : 0;
      boolean bool = contains(paramInt1, paramInt2);
      if (bool && b != 0 && paramEventTargetFilter.accept(this))
        return this; 
      return null;
    } 
  }
  
  void proxyEnableEvents(long paramLong) {
    if (this.peer instanceof java.awt.peer.LightweightPeer) {
      if (this.parent != null)
        this.parent.proxyEnableEvents(paramLong); 
    } else if (this.dispatcher != null) {
      this.dispatcher.enableEvents(paramLong);
    } 
  }
  
  @Deprecated
  public void deliverEvent(Event paramEvent) {
    Component component1 = getComponentAt(paramEvent.x, paramEvent.y);
    if (component1 != null && component1 != this) {
      paramEvent.translate(-component1.x, -component1.y);
      component1.deliverEvent(paramEvent);
    } else {
      postEvent(paramEvent);
    } 
  }
  
  public Component getComponentAt(int paramInt1, int paramInt2) { return locate(paramInt1, paramInt2); }
  
  @Deprecated
  public Component locate(int paramInt1, int paramInt2) {
    if (!contains(paramInt1, paramInt2))
      return null; 
    Component component1 = null;
    synchronized (getTreeLock()) {
      for (Component component2 : this.component) {
        if (component2.contains(paramInt1 - component2.x, paramInt2 - component2.y)) {
          if (!component2.isLightweight())
            return component2; 
          if (component1 == null)
            component1 = component2; 
        } 
      } 
    } 
    return (component1 != null) ? component1 : this;
  }
  
  public Component getComponentAt(Point paramPoint) { return getComponentAt(paramPoint.x, paramPoint.y); }
  
  public Point getMousePosition(boolean paramBoolean) throws HeadlessException {
    if (GraphicsEnvironment.isHeadless())
      throw new HeadlessException(); 
    PointerInfo pointerInfo = (PointerInfo)AccessController.doPrivileged(new PrivilegedAction<PointerInfo>() {
          public PointerInfo run() { return MouseInfo.getPointerInfo(); }
        });
    synchronized (getTreeLock()) {
      Component component1 = findUnderMouseInWindow(pointerInfo);
      if (isSameOrAncestorOf(component1, paramBoolean))
        return pointRelativeToComponent(pointerInfo.getLocation()); 
      return null;
    } 
  }
  
  boolean isSameOrAncestorOf(Component paramComponent, boolean paramBoolean) { return (this == paramComponent || (paramBoolean && isParentOf(paramComponent))); }
  
  public Component findComponentAt(int paramInt1, int paramInt2) { return findComponentAt(paramInt1, paramInt2, true); }
  
  final Component findComponentAt(int paramInt1, int paramInt2, boolean paramBoolean) {
    synchronized (getTreeLock()) {
      if (isRecursivelyVisible())
        return findComponentAtImpl(paramInt1, paramInt2, paramBoolean); 
    } 
    return null;
  }
  
  final Component findComponentAtImpl(int paramInt1, int paramInt2, boolean paramBoolean) {
    if (!contains(paramInt1, paramInt2) || !this.visible || (!paramBoolean && !this.enabled))
      return null; 
    Component component1 = null;
    for (Component component2 : this.component) {
      int i = paramInt1 - component2.x;
      int j = paramInt2 - component2.y;
      if (!component2.contains(i, j))
        continue; 
      if (!component2.isLightweight()) {
        Component component3 = getChildAt(component2, i, j, paramBoolean);
        if (component3 != null)
          return component3; 
        continue;
      } 
      if (component1 == null)
        component1 = getChildAt(component2, i, j, paramBoolean); 
    } 
    return (component1 != null) ? component1 : this;
  }
  
  private static Component getChildAt(Component paramComponent, int paramInt1, int paramInt2, boolean paramBoolean) {
    if (paramComponent instanceof Container) {
      paramComponent = ((Container)paramComponent).findComponentAtImpl(paramInt1, paramInt2, paramBoolean);
    } else {
      paramComponent = paramComponent.getComponentAt(paramInt1, paramInt2);
    } 
    return (paramComponent != null && paramComponent.visible && (paramBoolean || paramComponent.enabled)) ? paramComponent : null;
  }
  
  public Component findComponentAt(Point paramPoint) { return findComponentAt(paramPoint.x, paramPoint.y); }
  
  public void addNotify() {
    synchronized (getTreeLock()) {
      super.addNotify();
      if (!(this.peer instanceof java.awt.peer.LightweightPeer))
        this.dispatcher = new LightweightDispatcher(this); 
      for (byte b = 0; b < this.component.size(); b++)
        ((Component)this.component.get(b)).addNotify(); 
    } 
  }
  
  public void removeNotify() {
    synchronized (getTreeLock()) {
      for (int i = this.component.size() - 1; i >= 0; i--) {
        Component component1 = (Component)this.component.get(i);
        if (component1 != null) {
          component1.setAutoFocusTransferOnDisposal(false);
          component1.removeNotify();
          component1.setAutoFocusTransferOnDisposal(true);
        } 
      } 
      if (containsFocus() && KeyboardFocusManager.isAutoFocusTransferEnabledFor(this) && !transferFocus(false))
        transferFocusBackward(true); 
      if (this.dispatcher != null) {
        this.dispatcher.dispose();
        this.dispatcher = null;
      } 
      super.removeNotify();
    } 
  }
  
  public boolean isAncestorOf(Component paramComponent) {
    Container container;
    if (paramComponent == null || (container = paramComponent.getParent()) == null)
      return false; 
    while (container != null) {
      if (container == this)
        return true; 
      container = container.getParent();
    } 
    return false;
  }
  
  private void startLWModal() {
    final Container nativeContainer;
    this.modalAppContext = AppContext.getAppContext();
    long l = Toolkit.getEventQueue().getMostRecentKeyEventTime();
    Component component1 = Component.isInstanceOf(this, "javax.swing.JInternalFrame") ? ((JInternalFrame)this).getMostRecentFocusOwner() : null;
    if (component1 != null)
      KeyboardFocusManager.getCurrentKeyboardFocusManager().enqueueKeyEvents(l, component1); 
    synchronized (getTreeLock()) {
      container = getHeavyweightContainer();
      if (container.modalComp != null) {
        this.modalComp = container.modalComp;
        container.modalComp = this;
        return;
      } 
      container.modalComp = this;
    } 
    Runnable runnable = new Runnable() {
        public void run() {
          EventDispatchThread eventDispatchThread = (EventDispatchThread)Thread.currentThread();
          eventDispatchThread.pumpEventsForHierarchy(new Conditional() {
                public boolean evaluate() { return (Container.this.windowClosingException == null && this.this$1.val$nativeContainer.modalComp != null); }
              },  Container.this);
        }
      };
    if (EventQueue.isDispatchThread()) {
      SequencedEvent sequencedEvent = KeyboardFocusManager.getCurrentKeyboardFocusManager().getCurrentSequencedEvent();
      if (sequencedEvent != null)
        sequencedEvent.dispose(); 
      runnable.run();
    } else {
      synchronized (getTreeLock()) {
        Toolkit.getEventQueue().postEvent(new PeerEvent(this, runnable, 1L));
        while (this.windowClosingException == null && container.modalComp != null) {
          try {
            getTreeLock().wait();
          } catch (InterruptedException interruptedException) {
            break;
          } 
        } 
      } 
    } 
    if (this.windowClosingException != null) {
      this.windowClosingException.fillInStackTrace();
      throw this.windowClosingException;
    } 
    if (component1 != null)
      KeyboardFocusManager.getCurrentKeyboardFocusManager().dequeueKeyEvents(l, component1); 
  }
  
  private void stopLWModal() {
    synchronized (getTreeLock()) {
      if (this.modalAppContext != null) {
        Container container = getHeavyweightContainer();
        if (container != null) {
          if (this.modalComp != null) {
            container.modalComp = this.modalComp;
            this.modalComp = null;
            return;
          } 
          container.modalComp = null;
        } 
        SunToolkit.postEvent(this.modalAppContext, new PeerEvent(this, new WakingRunnable(), 1L));
      } 
      EventQueue.invokeLater(new WakingRunnable());
      getTreeLock().notifyAll();
    } 
  }
  
  protected String paramString() {
    String str = super.paramString();
    LayoutManager layoutManager = this.layoutMgr;
    if (layoutManager != null)
      str = str + ",layout=" + layoutManager.getClass().getName(); 
    return str;
  }
  
  public void list(PrintStream paramPrintStream, int paramInt) {
    super.list(paramPrintStream, paramInt);
    synchronized (getTreeLock()) {
      for (byte b = 0; b < this.component.size(); b++) {
        Component component1 = (Component)this.component.get(b);
        if (component1 != null)
          component1.list(paramPrintStream, paramInt + 1); 
      } 
    } 
  }
  
  public void list(PrintWriter paramPrintWriter, int paramInt) {
    super.list(paramPrintWriter, paramInt);
    synchronized (getTreeLock()) {
      for (byte b = 0; b < this.component.size(); b++) {
        Component component1 = (Component)this.component.get(b);
        if (component1 != null)
          component1.list(paramPrintWriter, paramInt + 1); 
      } 
    } 
  }
  
  public void setFocusTraversalKeys(int paramInt, Set<? extends AWTKeyStroke> paramSet) {
    if (paramInt < 0 || paramInt >= 4)
      throw new IllegalArgumentException("invalid focus traversal key identifier"); 
    setFocusTraversalKeys_NoIDCheck(paramInt, paramSet);
  }
  
  public Set<AWTKeyStroke> getFocusTraversalKeys(int paramInt) {
    if (paramInt < 0 || paramInt >= 4)
      throw new IllegalArgumentException("invalid focus traversal key identifier"); 
    return getFocusTraversalKeys_NoIDCheck(paramInt);
  }
  
  public boolean areFocusTraversalKeysSet(int paramInt) {
    if (paramInt < 0 || paramInt >= 4)
      throw new IllegalArgumentException("invalid focus traversal key identifier"); 
    return (this.focusTraversalKeys != null && this.focusTraversalKeys[paramInt] != null);
  }
  
  public boolean isFocusCycleRoot(Container paramContainer) { return (isFocusCycleRoot() && paramContainer == this) ? true : super.isFocusCycleRoot(paramContainer); }
  
  private Container findTraversalRoot() {
    Container container2;
    Container container1 = KeyboardFocusManager.getCurrentKeyboardFocusManager().getCurrentFocusCycleRoot();
    if (container1 == this) {
      container2 = this;
    } else {
      container2 = getFocusCycleRootAncestor();
      if (container2 == null)
        container2 = this; 
    } 
    if (container2 != container1)
      KeyboardFocusManager.getCurrentKeyboardFocusManager().setGlobalCurrentFocusCycleRootPriv(container2); 
    return container2;
  }
  
  final boolean containsFocus() {
    Component component1 = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
    return isParentOf(component1);
  }
  
  private boolean isParentOf(Component paramComponent) {
    synchronized (getTreeLock()) {
      while (paramComponent != null && paramComponent != this && !(paramComponent instanceof Window))
        paramComponent = paramComponent.getParent(); 
      return (paramComponent == this);
    } 
  }
  
  void clearMostRecentFocusOwnerOnHide() {
    boolean bool = false;
    Window window = null;
    synchronized (getTreeLock()) {
      window = getContainingWindow();
      if (window != null) {
        Component component1 = KeyboardFocusManager.getMostRecentFocusOwner(window);
        bool = (component1 == this || isParentOf(component1)) ? 1 : 0;
        synchronized (KeyboardFocusManager.class) {
          Component component2 = window.getTemporaryLostComponent();
          if (isParentOf(component2) || component2 == this)
            window.setTemporaryLostComponent(null); 
        } 
      } 
    } 
    if (bool)
      KeyboardFocusManager.setMostRecentFocusOwner(window, null); 
  }
  
  void clearCurrentFocusCycleRootOnHide() {
    KeyboardFocusManager keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
    Container container = keyboardFocusManager.getCurrentFocusCycleRoot();
    if (container == this || isParentOf(container))
      keyboardFocusManager.setGlobalCurrentFocusCycleRootPriv(null); 
  }
  
  final Container getTraversalRoot() { return isFocusCycleRoot() ? findTraversalRoot() : super.getTraversalRoot(); }
  
  public void setFocusTraversalPolicy(FocusTraversalPolicy paramFocusTraversalPolicy) {
    FocusTraversalPolicy focusTraversalPolicy1;
    synchronized (this) {
      focusTraversalPolicy1 = this.focusTraversalPolicy;
      this.focusTraversalPolicy = paramFocusTraversalPolicy;
    } 
    firePropertyChange("focusTraversalPolicy", focusTraversalPolicy1, paramFocusTraversalPolicy);
  }
  
  public FocusTraversalPolicy getFocusTraversalPolicy() {
    if (!isFocusTraversalPolicyProvider() && !isFocusCycleRoot())
      return null; 
    FocusTraversalPolicy focusTraversalPolicy1 = this.focusTraversalPolicy;
    if (focusTraversalPolicy1 != null)
      return focusTraversalPolicy1; 
    Container container = getFocusCycleRootAncestor();
    return (container != null) ? container.getFocusTraversalPolicy() : KeyboardFocusManager.getCurrentKeyboardFocusManager().getDefaultFocusTraversalPolicy();
  }
  
  public boolean isFocusTraversalPolicySet() { return (this.focusTraversalPolicy != null); }
  
  public void setFocusCycleRoot(boolean paramBoolean) {
    boolean bool;
    synchronized (this) {
      bool = this.focusCycleRoot;
      this.focusCycleRoot = paramBoolean;
    } 
    firePropertyChange("focusCycleRoot", bool, paramBoolean);
  }
  
  public boolean isFocusCycleRoot() { return this.focusCycleRoot; }
  
  public final void setFocusTraversalPolicyProvider(boolean paramBoolean) {
    boolean bool;
    synchronized (this) {
      bool = this.focusTraversalPolicyProvider;
      this.focusTraversalPolicyProvider = paramBoolean;
    } 
    firePropertyChange("focusTraversalPolicyProvider", bool, paramBoolean);
  }
  
  public final boolean isFocusTraversalPolicyProvider() { return this.focusTraversalPolicyProvider; }
  
  public void transferFocusDownCycle() {
    if (isFocusCycleRoot()) {
      KeyboardFocusManager.getCurrentKeyboardFocusManager().setGlobalCurrentFocusCycleRootPriv(this);
      Component component1 = getFocusTraversalPolicy().getDefaultComponent(this);
      if (component1 != null)
        component1.requestFocus(CausedFocusEvent.Cause.TRAVERSAL_DOWN); 
    } 
  }
  
  void preProcessKeyEvent(KeyEvent paramKeyEvent) {
    Container container = this.parent;
    if (container != null)
      container.preProcessKeyEvent(paramKeyEvent); 
  }
  
  void postProcessKeyEvent(KeyEvent paramKeyEvent) {
    Container container = this.parent;
    if (container != null)
      container.postProcessKeyEvent(paramKeyEvent); 
  }
  
  boolean postsOldMouseEvents() { return true; }
  
  public void applyComponentOrientation(ComponentOrientation paramComponentOrientation) {
    super.applyComponentOrientation(paramComponentOrientation);
    synchronized (getTreeLock()) {
      for (byte b = 0; b < this.component.size(); b++) {
        Component component1 = (Component)this.component.get(b);
        component1.applyComponentOrientation(paramComponentOrientation);
      } 
    } 
  }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) { super.addPropertyChangeListener(paramPropertyChangeListener); }
  
  public void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener) { super.addPropertyChangeListener(paramString, paramPropertyChangeListener); }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
    putField.put("ncomponents", this.component.size());
    putField.put("component", this.component.toArray(EMPTY_ARRAY));
    putField.put("layoutMgr", this.layoutMgr);
    putField.put("dispatcher", this.dispatcher);
    putField.put("maxSize", this.maxSize);
    putField.put("focusCycleRoot", this.focusCycleRoot);
    putField.put("containerSerializedDataVersion", this.containerSerializedDataVersion);
    putField.put("focusTraversalPolicyProvider", this.focusTraversalPolicyProvider);
    paramObjectOutputStream.writeFields();
    AWTEventMulticaster.save(paramObjectOutputStream, "containerL", this.containerListener);
    paramObjectOutputStream.writeObject(null);
    if (this.focusTraversalPolicy instanceof java.io.Serializable) {
      paramObjectOutputStream.writeObject(this.focusTraversalPolicy);
    } else {
      paramObjectOutputStream.writeObject(null);
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException {
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    Component[] arrayOfComponent = (Component[])getField.get("component", null);
    if (arrayOfComponent == null)
      arrayOfComponent = EMPTY_ARRAY; 
    int i = Integer.valueOf(getField.get("ncomponents", 0)).intValue();
    if (i < 0 || i > arrayOfComponent.length)
      throw new InvalidObjectException("Incorrect number of components"); 
    this.component = new ArrayList(i);
    for (byte b = 0; b < i; b++)
      this.component.add(arrayOfComponent[b]); 
    this.layoutMgr = (LayoutManager)getField.get("layoutMgr", null);
    this.dispatcher = (LightweightDispatcher)getField.get("dispatcher", null);
    if (this.maxSize == null)
      this.maxSize = (Dimension)getField.get("maxSize", null); 
    this.focusCycleRoot = getField.get("focusCycleRoot", false);
    this.containerSerializedDataVersion = getField.get("containerSerializedDataVersion", 1);
    this.focusTraversalPolicyProvider = getField.get("focusTraversalPolicyProvider", false);
    List list = this.component;
    for (Component component1 : list) {
      component1.parent = this;
      adjustListeningChildren(32768L, component1.numListening(32768L));
      adjustListeningChildren(65536L, component1.numListening(65536L));
      adjustDescendants(component1.countHierarchyMembers());
    } 
    Object object;
    while (null != (object = paramObjectInputStream.readObject())) {
      String str = ((String)object).intern();
      if ("containerL" == str) {
        addContainerListener((ContainerListener)paramObjectInputStream.readObject());
        continue;
      } 
      paramObjectInputStream.readObject();
    } 
    try {
      Object object1 = paramObjectInputStream.readObject();
      if (object1 instanceof FocusTraversalPolicy)
        this.focusTraversalPolicy = (FocusTraversalPolicy)object1; 
    } catch (OptionalDataException optionalDataException) {
      if (!optionalDataException.eof)
        throw optionalDataException; 
    } 
  }
  
  Accessible getAccessibleAt(Point paramPoint) {
    synchronized (getTreeLock()) {
      if (this instanceof Accessible) {
        Accessible accessible = (Accessible)this;
        AccessibleContext accessibleContext = accessible.getAccessibleContext();
        if (accessibleContext != null) {
          int i = accessibleContext.getAccessibleChildrenCount();
          for (byte b = 0; b < i; b++) {
            accessible = accessibleContext.getAccessibleChild(b);
            if (accessible != null) {
              accessibleContext = accessible.getAccessibleContext();
              if (accessibleContext != null) {
                AccessibleComponent accessibleComponent = accessibleContext.getAccessibleComponent();
                if (accessibleComponent != null && accessibleComponent.isShowing()) {
                  Point point1 = accessibleComponent.getLocation();
                  Point point2 = new Point(paramPoint.x - point1.x, paramPoint.y - point1.y);
                  if (accessibleComponent.contains(point2))
                    return accessible; 
                } 
              } 
            } 
          } 
        } 
        return (Accessible)this;
      } 
      Component component1 = this;
      if (!contains(paramPoint.x, paramPoint.y)) {
        component1 = null;
      } else {
        int i = getComponentCount();
        for (byte b = 0; b < i; b++) {
          Component component2 = getComponent(b);
          if (component2 != null && component2.isShowing()) {
            Point point = component2.getLocation();
            if (component2.contains(paramPoint.x - point.x, paramPoint.y - point.y))
              component1 = component2; 
          } 
        } 
      } 
      if (component1 instanceof Accessible)
        return (Accessible)component1; 
      return null;
    } 
  }
  
  int getAccessibleChildrenCount() {
    synchronized (getTreeLock()) {
      byte b1 = 0;
      Component[] arrayOfComponent = getComponents();
      for (byte b2 = 0; b2 < arrayOfComponent.length; b2++) {
        if (arrayOfComponent[b2] instanceof Accessible)
          b1++; 
      } 
      return b1;
    } 
  }
  
  Accessible getAccessibleChild(int paramInt) {
    synchronized (getTreeLock()) {
      Component[] arrayOfComponent = getComponents();
      byte b1 = 0;
      for (byte b2 = 0; b2 < arrayOfComponent.length; b2++) {
        if (arrayOfComponent[b2] instanceof Accessible) {
          if (b1 == paramInt)
            return (Accessible)arrayOfComponent[b2]; 
          b1++;
        } 
      } 
      return null;
    } 
  }
  
  final void increaseComponentCount(Component paramComponent) {
    synchronized (getTreeLock()) {
      if (!paramComponent.isDisplayable())
        throw new IllegalStateException("Peer does not exist while invoking the increaseComponentCount() method"); 
      int i = 0;
      int j = 0;
      if (paramComponent instanceof Container) {
        j = ((Container)paramComponent).numOfLWComponents;
        i = ((Container)paramComponent).numOfHWComponents;
      } 
      if (paramComponent.isLightweight()) {
        j++;
      } else {
        i++;
      } 
      for (Container container = this; container != null; container = container.getContainer()) {
        container.numOfLWComponents += j;
        container.numOfHWComponents += i;
      } 
    } 
  }
  
  final void decreaseComponentCount(Component paramComponent) {
    synchronized (getTreeLock()) {
      if (!paramComponent.isDisplayable())
        throw new IllegalStateException("Peer does not exist while invoking the decreaseComponentCount() method"); 
      int i = 0;
      int j = 0;
      if (paramComponent instanceof Container) {
        j = ((Container)paramComponent).numOfLWComponents;
        i = ((Container)paramComponent).numOfHWComponents;
      } 
      if (paramComponent.isLightweight()) {
        j++;
      } else {
        i++;
      } 
      for (Container container = this; container != null; container = container.getContainer()) {
        container.numOfLWComponents -= j;
        container.numOfHWComponents -= i;
      } 
    } 
  }
  
  private int getTopmostComponentIndex() {
    checkTreeLock();
    return (getComponentCount() > 0) ? 0 : -1;
  }
  
  private int getBottommostComponentIndex() {
    checkTreeLock();
    return (getComponentCount() > 0) ? (getComponentCount() - 1) : -1;
  }
  
  final Region getOpaqueShape() {
    checkTreeLock();
    if (isLightweight() && isNonOpaqueForMixing() && hasLightweightDescendants()) {
      Region region = Region.EMPTY_REGION;
      for (byte b = 0; b < getComponentCount(); b++) {
        Component component1 = getComponent(b);
        if (component1.isLightweight() && component1.isShowing())
          region = region.getUnion(component1.getOpaqueShape()); 
      } 
      return region.getIntersection(getNormalShape());
    } 
    return super.getOpaqueShape();
  }
  
  final void recursiveSubtractAndApplyShape(Region paramRegion) { recursiveSubtractAndApplyShape(paramRegion, getTopmostComponentIndex(), getBottommostComponentIndex()); }
  
  final void recursiveSubtractAndApplyShape(Region paramRegion, int paramInt) { recursiveSubtractAndApplyShape(paramRegion, paramInt, getBottommostComponentIndex()); }
  
  final void recursiveSubtractAndApplyShape(Region paramRegion, int paramInt1, int paramInt2) {
    checkTreeLock();
    if (mixingLog.isLoggable(PlatformLogger.Level.FINE))
      mixingLog.fine("this = " + this + "; shape=" + paramRegion + "; fromZ=" + paramInt1 + "; toZ=" + paramInt2); 
    if (paramInt1 == -1)
      return; 
    if (paramRegion.isEmpty())
      return; 
    if (getLayout() != null && !isValid())
      return; 
    for (int i = paramInt1; i <= paramInt2; i++) {
      Component component1 = getComponent(i);
      if (!component1.isLightweight()) {
        component1.subtractAndApplyShape(paramRegion);
      } else if (component1 instanceof Container && ((Container)component1).hasHeavyweightDescendants() && component1.isShowing()) {
        ((Container)component1).recursiveSubtractAndApplyShape(paramRegion);
      } 
    } 
  }
  
  final void recursiveApplyCurrentShape() { recursiveApplyCurrentShape(getTopmostComponentIndex(), getBottommostComponentIndex()); }
  
  final void recursiveApplyCurrentShape(int paramInt) { recursiveApplyCurrentShape(paramInt, getBottommostComponentIndex()); }
  
  final void recursiveApplyCurrentShape(int paramInt1, int paramInt2) {
    checkTreeLock();
    if (mixingLog.isLoggable(PlatformLogger.Level.FINE))
      mixingLog.fine("this = " + this + "; fromZ=" + paramInt1 + "; toZ=" + paramInt2); 
    if (paramInt1 == -1)
      return; 
    if (getLayout() != null && !isValid())
      return; 
    for (int i = paramInt1; i <= paramInt2; i++) {
      Component component1 = getComponent(i);
      if (!component1.isLightweight())
        component1.applyCurrentShape(); 
      if (component1 instanceof Container && ((Container)component1).hasHeavyweightDescendants())
        ((Container)component1).recursiveApplyCurrentShape(); 
    } 
  }
  
  private void recursiveShowHeavyweightChildren() {
    if (!hasHeavyweightDescendants() || !isVisible())
      return; 
    for (byte b = 0; b < getComponentCount(); b++) {
      Component component1 = getComponent(b);
      if (component1.isLightweight()) {
        if (component1 instanceof Container)
          ((Container)component1).recursiveShowHeavyweightChildren(); 
      } else if (component1.isVisible()) {
        ComponentPeer componentPeer = component1.getPeer();
        if (componentPeer != null)
          componentPeer.setVisible(true); 
      } 
    } 
  }
  
  private void recursiveHideHeavyweightChildren() {
    if (!hasHeavyweightDescendants())
      return; 
    for (byte b = 0; b < getComponentCount(); b++) {
      Component component1 = getComponent(b);
      if (component1.isLightweight()) {
        if (component1 instanceof Container)
          ((Container)component1).recursiveHideHeavyweightChildren(); 
      } else if (component1.isVisible()) {
        ComponentPeer componentPeer = component1.getPeer();
        if (componentPeer != null)
          componentPeer.setVisible(false); 
      } 
    } 
  }
  
  private void recursiveRelocateHeavyweightChildren(Point paramPoint) {
    for (byte b = 0; b < getComponentCount(); b++) {
      Component component1 = getComponent(b);
      if (component1.isLightweight()) {
        if (component1 instanceof Container && ((Container)component1).hasHeavyweightDescendants()) {
          Point point = new Point(paramPoint);
          point.translate(component1.getX(), component1.getY());
          ((Container)component1).recursiveRelocateHeavyweightChildren(point);
        } 
      } else {
        ComponentPeer componentPeer = component1.getPeer();
        if (componentPeer != null)
          componentPeer.setBounds(paramPoint.x + component1.getX(), paramPoint.y + component1.getY(), component1.getWidth(), component1.getHeight(), 1); 
      } 
    } 
  }
  
  final boolean isRecursivelyVisibleUpToHeavyweightContainer() {
    if (!isLightweight())
      return true; 
    for (Container container = this; container != null && container.isLightweight(); container = container.getContainer()) {
      if (!container.isVisible())
        return false; 
    } 
    return true;
  }
  
  void mixOnShowing() {
    synchronized (getTreeLock()) {
      if (mixingLog.isLoggable(PlatformLogger.Level.FINE))
        mixingLog.fine("this = " + this); 
      boolean bool = isLightweight();
      if (bool && isRecursivelyVisibleUpToHeavyweightContainer())
        recursiveShowHeavyweightChildren(); 
      if (!isMixingNeeded())
        return; 
      if (!bool || (bool && hasHeavyweightDescendants()))
        recursiveApplyCurrentShape(); 
      super.mixOnShowing();
    } 
  }
  
  void mixOnHiding(boolean paramBoolean) {
    synchronized (getTreeLock()) {
      if (mixingLog.isLoggable(PlatformLogger.Level.FINE))
        mixingLog.fine("this = " + this + "; isLightweight=" + paramBoolean); 
      if (paramBoolean)
        recursiveHideHeavyweightChildren(); 
      super.mixOnHiding(paramBoolean);
    } 
  }
  
  void mixOnReshaping() {
    synchronized (getTreeLock()) {
      if (mixingLog.isLoggable(PlatformLogger.Level.FINE))
        mixingLog.fine("this = " + this); 
      boolean bool = isMixingNeeded();
      if (isLightweight() && hasHeavyweightDescendants()) {
        Point point = new Point(getX(), getY());
        for (Container container = getContainer(); container != null && container.isLightweight(); container = container.getContainer())
          point.translate(container.getX(), container.getY()); 
        recursiveRelocateHeavyweightChildren(point);
        if (!bool)
          return; 
        recursiveApplyCurrentShape();
      } 
      if (!bool)
        return; 
      super.mixOnReshaping();
    } 
  }
  
  void mixOnZOrderChanging(int paramInt1, int paramInt2) {
    synchronized (getTreeLock()) {
      if (mixingLog.isLoggable(PlatformLogger.Level.FINE))
        mixingLog.fine("this = " + this + "; oldZ=" + paramInt1 + "; newZ=" + paramInt2); 
      if (!isMixingNeeded())
        return; 
      boolean bool = (paramInt2 < paramInt1) ? 1 : 0;
      if (bool && isLightweight() && hasHeavyweightDescendants())
        recursiveApplyCurrentShape(); 
      super.mixOnZOrderChanging(paramInt1, paramInt2);
    } 
  }
  
  void mixOnValidating() {
    synchronized (getTreeLock()) {
      if (mixingLog.isLoggable(PlatformLogger.Level.FINE))
        mixingLog.fine("this = " + this); 
      if (!isMixingNeeded())
        return; 
      if (hasHeavyweightDescendants())
        recursiveApplyCurrentShape(); 
      if (isLightweight() && isNonOpaqueForMixing())
        subtractAndApplyShapeBelowMe(); 
      super.mixOnValidating();
    } 
  }
  
  static  {
    Toolkit.loadLibraries();
    if (!GraphicsEnvironment.isHeadless())
      initIDs(); 
    AWTAccessor.setContainerAccessor(new AWTAccessor.ContainerAccessor() {
          public void validateUnconditionally(Container param1Container) { param1Container.validateUnconditionally(); }
          
          public Component findComponentAt(Container param1Container, int param1Int1, int param1Int2, boolean param1Boolean) { return param1Container.findComponentAt(param1Int1, param1Int2, param1Boolean); }
        });
    isJavaAwtSmartInvalidate = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("java.awt.smartInvalidate"))).booleanValue();
    descendUnconditionallyWhenValidating = false;
  }
  
  protected class AccessibleAWTContainer extends Component.AccessibleAWTComponent {
    private static final long serialVersionUID = 5081320404842566097L;
    
    protected ContainerListener accessibleContainerHandler = null;
    
    protected AccessibleAWTContainer() { super(Container.this); }
    
    public int getAccessibleChildrenCount() { return Container.this.getAccessibleChildrenCount(); }
    
    public Accessible getAccessibleChild(int param1Int) { return Container.this.getAccessibleChild(param1Int); }
    
    public Accessible getAccessibleAt(Point param1Point) { return Container.this.getAccessibleAt(param1Point); }
    
    public void addPropertyChangeListener(PropertyChangeListener param1PropertyChangeListener) {
      if (this.accessibleContainerHandler == null)
        this.accessibleContainerHandler = new AccessibleContainerHandler(); 
      if (this.propertyListenersCount++ == 0)
        Container.this.addContainerListener(this.accessibleContainerHandler); 
      super.addPropertyChangeListener(param1PropertyChangeListener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener param1PropertyChangeListener) {
      if (--this.propertyListenersCount == 0)
        Container.this.removeContainerListener(this.accessibleContainerHandler); 
      super.removePropertyChangeListener(param1PropertyChangeListener);
    }
    
    protected class AccessibleContainerHandler implements ContainerListener {
      public void componentAdded(ContainerEvent param2ContainerEvent) {
        Component component = param2ContainerEvent.getChild();
        if (component != null && component instanceof Accessible)
          Container.AccessibleAWTContainer.this.firePropertyChange("AccessibleChild", null, ((Accessible)component).getAccessibleContext()); 
      }
      
      public void componentRemoved(ContainerEvent param2ContainerEvent) {
        Component component = param2ContainerEvent.getChild();
        if (component != null && component instanceof Accessible)
          Container.AccessibleAWTContainer.this.firePropertyChange("AccessibleChild", ((Accessible)component).getAccessibleContext(), null); 
      }
    }
  }
  
  static class DropTargetEventTargetFilter implements EventTargetFilter {
    static final Container.EventTargetFilter FILTER = new DropTargetEventTargetFilter();
    
    public boolean accept(Component param1Component) {
      DropTarget dropTarget = param1Component.getDropTarget();
      return (dropTarget != null && dropTarget.isActive());
    }
  }
  
  static interface EventTargetFilter {
    boolean accept(Component param1Component);
  }
  
  static class MouseEventTargetFilter implements EventTargetFilter {
    static final Container.EventTargetFilter FILTER = new MouseEventTargetFilter();
    
    public boolean accept(Component param1Component) { return ((param1Component.eventMask & 0x20L) != 0L || (param1Component.eventMask & 0x10L) != 0L || (param1Component.eventMask & 0x20000L) != 0L || param1Component.mouseListener != null || param1Component.mouseMotionListener != null || param1Component.mouseWheelListener != null); }
  }
  
  static final class WakingRunnable implements Runnable {
    public void run() {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\Container.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */