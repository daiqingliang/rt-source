package java.awt;

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.peer.ComponentPeer;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import sun.awt.AWTAccessor;
import sun.awt.AppContext;
import sun.awt.CausedFocusEvent;
import sun.awt.SunToolkit;
import sun.awt.TimedWindowEvent;
import sun.util.logging.PlatformLogger;

public class DefaultKeyboardFocusManager extends KeyboardFocusManager {
  private static final PlatformLogger focusLog = PlatformLogger.getLogger("java.awt.focus.DefaultKeyboardFocusManager");
  
  private static final WeakReference<Window> NULL_WINDOW_WR = new WeakReference(null);
  
  private static final WeakReference<Component> NULL_COMPONENT_WR = new WeakReference(null);
  
  private WeakReference<Window> realOppositeWindowWR = NULL_WINDOW_WR;
  
  private WeakReference<Component> realOppositeComponentWR = NULL_COMPONENT_WR;
  
  private int inSendMessage;
  
  private LinkedList<KeyEvent> enqueuedKeyEvents = new LinkedList();
  
  private LinkedList<TypeAheadMarker> typeAheadMarkers = new LinkedList();
  
  private boolean consumeNextKeyTyped;
  
  private Component restoreFocusTo;
  
  private Window getOwningFrameDialog(Window paramWindow) {
    while (paramWindow != null && !(paramWindow instanceof Frame) && !(paramWindow instanceof Dialog))
      paramWindow = (Window)paramWindow.getParent(); 
    return paramWindow;
  }
  
  private void restoreFocus(FocusEvent paramFocusEvent, Window paramWindow) {
    Component component1 = (Component)this.realOppositeComponentWR.get();
    Component component2 = paramFocusEvent.getComponent();
    if ((paramWindow == null || !restoreFocus(paramWindow, component2, false)) && (component1 == null || !doRestoreFocus(component1, component2, false)) && (paramFocusEvent.getOppositeComponent() == null || !doRestoreFocus(paramFocusEvent.getOppositeComponent(), component2, false)))
      clearGlobalFocusOwnerPriv(); 
  }
  
  private void restoreFocus(WindowEvent paramWindowEvent) {
    Window window = (Window)this.realOppositeWindowWR.get();
    if ((window == null || !restoreFocus(window, null, false)) && (paramWindowEvent.getOppositeWindow() == null || !restoreFocus(paramWindowEvent.getOppositeWindow(), null, false)))
      clearGlobalFocusOwnerPriv(); 
  }
  
  private boolean restoreFocus(Window paramWindow, Component paramComponent, boolean paramBoolean) {
    this.restoreFocusTo = null;
    Component component = KeyboardFocusManager.getMostRecentFocusOwner(paramWindow);
    if (component != null && component != paramComponent)
      if (getHeavyweight(paramWindow) != getNativeFocusOwner()) {
        if (!component.isShowing() || !component.canBeFocusOwner())
          component = component.getNextFocusCandidate(); 
        if (component != null && component != paramComponent) {
          if (!component.requestFocus(false, CausedFocusEvent.Cause.ROLLBACK))
            this.restoreFocusTo = component; 
          return true;
        } 
      } else if (doRestoreFocus(component, paramComponent, false)) {
        return true;
      }  
    if (paramBoolean) {
      clearGlobalFocusOwnerPriv();
      return true;
    } 
    return false;
  }
  
  private boolean restoreFocus(Component paramComponent, boolean paramBoolean) { return doRestoreFocus(paramComponent, null, paramBoolean); }
  
  private boolean doRestoreFocus(Component paramComponent1, Component paramComponent2, boolean paramBoolean) {
    boolean bool = true;
    if (paramComponent1 != paramComponent2 && paramComponent1.isShowing() && paramComponent1.canBeFocusOwner() && (bool = paramComponent1.requestFocus(false, CausedFocusEvent.Cause.ROLLBACK)))
      return true; 
    if (!bool && getGlobalFocusedWindow() != SunToolkit.getContainingWindow(paramComponent1)) {
      this.restoreFocusTo = paramComponent1;
      return true;
    } 
    Component component = paramComponent1.getNextFocusCandidate();
    if (component != null && component != paramComponent2 && component.requestFocusInWindow(CausedFocusEvent.Cause.ROLLBACK))
      return true; 
    if (paramBoolean) {
      clearGlobalFocusOwnerPriv();
      return true;
    } 
    return false;
  }
  
  static boolean sendMessage(Component paramComponent, AWTEvent paramAWTEvent) {
    paramAWTEvent.isPosted = true;
    AppContext appContext1 = AppContext.getAppContext();
    final AppContext targetAppContext = paramComponent.appContext;
    final DefaultKeyboardFocusManagerSentEvent se = new DefaultKeyboardFocusManagerSentEvent(paramAWTEvent, appContext1);
    if (appContext1 == appContext2) {
      defaultKeyboardFocusManagerSentEvent.dispatch();
    } else {
      if (appContext2.isDisposed())
        return false; 
      SunToolkit.postEvent(appContext2, defaultKeyboardFocusManagerSentEvent);
      if (EventQueue.isDispatchThread()) {
        EventDispatchThread eventDispatchThread = (EventDispatchThread)Thread.currentThread();
        eventDispatchThread.pumpEvents(1007, new Conditional() {
              public boolean evaluate() { return (!this.val$se.dispatched && !targetAppContext.isDisposed()); }
            });
      } else {
        synchronized (defaultKeyboardFocusManagerSentEvent) {
          while (!defaultKeyboardFocusManagerSentEvent.dispatched && !appContext2.isDisposed()) {
            try {
              defaultKeyboardFocusManagerSentEvent.wait(1000L);
            } catch (InterruptedException interruptedException) {
              break;
            } 
          } 
        } 
      } 
    } 
    return defaultKeyboardFocusManagerSentEvent.dispatched;
  }
  
  private boolean repostIfFollowsKeyEvents(WindowEvent paramWindowEvent) {
    if (!(paramWindowEvent instanceof TimedWindowEvent))
      return false; 
    TimedWindowEvent timedWindowEvent = (TimedWindowEvent)paramWindowEvent;
    long l = timedWindowEvent.getWhen();
    synchronized (this) {
      KeyEvent keyEvent = this.enqueuedKeyEvents.isEmpty() ? null : (KeyEvent)this.enqueuedKeyEvents.getFirst();
      if (keyEvent != null && l >= keyEvent.getWhen()) {
        TypeAheadMarker typeAheadMarker = this.typeAheadMarkers.isEmpty() ? null : (TypeAheadMarker)this.typeAheadMarkers.getFirst();
        if (typeAheadMarker != null) {
          Window window = typeAheadMarker.untilFocused.getContainingWindow();
          if (window != null && window.isFocused()) {
            SunToolkit.postEvent(AppContext.getAppContext(), new SequencedEvent(paramWindowEvent));
            return true;
          } 
        } 
      } 
    } 
    return false;
  }
  
  public boolean dispatchEvent(AWTEvent paramAWTEvent) {
    Component component3;
    Component component2;
    Window window2;
    CausedFocusEvent.Cause cause;
    Window window1;
    Component component1;
    WindowEvent windowEvent2;
    FocusEvent focusEvent;
    WindowEvent windowEvent1;
    if (focusLog.isLoggable(PlatformLogger.Level.FINE) && (paramAWTEvent instanceof WindowEvent || paramAWTEvent instanceof FocusEvent))
      focusLog.fine("" + paramAWTEvent); 
    switch (paramAWTEvent.getID()) {
      case 207:
        if (!repostIfFollowsKeyEvents((WindowEvent)paramAWTEvent)) {
          WindowEvent windowEvent = (WindowEvent)paramAWTEvent;
          Window window3 = getGlobalFocusedWindow();
          Window window4 = windowEvent.getWindow();
          if (window4 != window3)
            if (!window4.isFocusableWindow() || !window4.isVisible() || !window4.isDisplayable()) {
              restoreFocus(windowEvent);
            } else {
              if (window3 != null) {
                boolean bool = sendMessage(window3, new WindowEvent(window3, 208, window4));
                if (!bool) {
                  setGlobalFocusOwner(null);
                  setGlobalFocusedWindow(null);
                } 
              } 
              Window window5 = getOwningFrameDialog(window4);
              Window window6 = getGlobalActiveWindow();
              if (window5 != window6) {
                sendMessage(window5, new WindowEvent(window5, 205, window6));
                if (window5 != getGlobalActiveWindow()) {
                  restoreFocus(windowEvent);
                  return true;
                } 
              } 
              setGlobalFocusedWindow(window4);
              if (window4 != getGlobalFocusedWindow()) {
                restoreFocus(windowEvent);
              } else {
                if (this.inSendMessage == 0) {
                  Component component4 = KeyboardFocusManager.getMostRecentFocusOwner(window4);
                  boolean bool = (this.restoreFocusTo != null && component4 == this.restoreFocusTo) ? 1 : 0;
                  if (component4 == null && window4.isFocusableWindow())
                    component4 = window4.getFocusTraversalPolicy().getInitialComponent(window4); 
                  Component component5 = null;
                  synchronized (KeyboardFocusManager.class) {
                    component5 = window4.setTemporaryLostComponent(null);
                  } 
                  if (focusLog.isLoggable(PlatformLogger.Level.FINER))
                    focusLog.finer("tempLost {0}, toFocus {1}", new Object[] { component5, component4 }); 
                  if (component5 != null)
                    component5.requestFocusInWindow((bool && component5 == component4) ? CausedFocusEvent.Cause.ROLLBACK : CausedFocusEvent.Cause.ACTIVATION); 
                  if (component4 != null && component4 != component5)
                    component4.requestFocusInWindow(CausedFocusEvent.Cause.ACTIVATION); 
                } 
                this.restoreFocusTo = null;
                Window window = (Window)this.realOppositeWindowWR.get();
                if (window != windowEvent.getOppositeWindow())
                  windowEvent = new WindowEvent(window4, 207, window); 
                return typeAheadAssertions(window4, windowEvent);
              } 
            }  
        } 
        return true;
      case 205:
        windowEvent2 = (WindowEvent)paramAWTEvent;
        window1 = getGlobalActiveWindow();
        window2 = windowEvent2.getWindow();
        if (window1 != window2) {
          if (window1 != null) {
            boolean bool = sendMessage(window1, new WindowEvent(window1, 206, window2));
            if (!bool)
              setGlobalActiveWindow(null); 
            if (getGlobalActiveWindow() != null)
              return true; 
          } 
          setGlobalActiveWindow(window2);
          if (window2 == getGlobalActiveWindow())
            return typeAheadAssertions(window2, windowEvent2); 
        } 
        return true;
      case 1004:
        this.restoreFocusTo = null;
        focusEvent = (FocusEvent)paramAWTEvent;
        cause = (focusEvent instanceof CausedFocusEvent) ? ((CausedFocusEvent)focusEvent).getCause() : CausedFocusEvent.Cause.UNKNOWN;
        component2 = getGlobalFocusOwner();
        component3 = focusEvent.getComponent();
        if (component2 == component3) {
          if (focusLog.isLoggable(PlatformLogger.Level.FINE))
            focusLog.fine("Skipping {0} because focus owner is the same", new Object[] { paramAWTEvent }); 
          dequeueKeyEvents(-1L, component3);
        } else {
          if (component2 != null) {
            boolean bool = sendMessage(component2, new CausedFocusEvent(component2, 1005, focusEvent.isTemporary(), component3, cause));
            if (!bool) {
              setGlobalFocusOwner(null);
              if (!focusEvent.isTemporary())
                setGlobalPermanentFocusOwner(null); 
            } 
          } 
          Window window3 = SunToolkit.getContainingWindow(component3);
          Window window4 = getGlobalFocusedWindow();
          if (window3 != null && window3 != window4) {
            sendMessage(window3, new WindowEvent(window3, 207, window4));
            if (window3 != getGlobalFocusedWindow()) {
              dequeueKeyEvents(-1L, component3);
              return true;
            } 
          } 
          if (!component3.isFocusable() || !component3.isShowing() || (!component3.isEnabled() && !cause.equals(CausedFocusEvent.Cause.UNKNOWN))) {
            dequeueKeyEvents(-1L, component3);
            if (KeyboardFocusManager.isAutoFocusTransferEnabled()) {
              if (window3 == null) {
                restoreFocus(focusEvent, window4);
              } else {
                restoreFocus(focusEvent, window3);
              } 
              setMostRecentFocusOwner(window3, null);
            } 
          } else {
            setGlobalFocusOwner(component3);
            if (component3 != getGlobalFocusOwner()) {
              dequeueKeyEvents(-1L, component3);
              if (KeyboardFocusManager.isAutoFocusTransferEnabled())
                restoreFocus(focusEvent, window3); 
            } else {
              if (!focusEvent.isTemporary()) {
                setGlobalPermanentFocusOwner(component3);
                if (component3 != getGlobalPermanentFocusOwner()) {
                  dequeueKeyEvents(-1L, component3);
                  if (KeyboardFocusManager.isAutoFocusTransferEnabled())
                    restoreFocus(focusEvent, window3); 
                  return true;
                } 
              } 
              setNativeFocusOwner(getHeavyweight(component3));
              Component component = (Component)this.realOppositeComponentWR.get();
              if (component != null && component != focusEvent.getOppositeComponent()) {
                focusEvent = new CausedFocusEvent(component3, 1004, focusEvent.isTemporary(), component, cause);
                focusEvent.isPosted = true;
              } 
              return typeAheadAssertions(component3, focusEvent);
            } 
          } 
        } 
        return true;
      case 1005:
        focusEvent = (FocusEvent)paramAWTEvent;
        component1 = getGlobalFocusOwner();
        if (component1 == null) {
          if (focusLog.isLoggable(PlatformLogger.Level.FINE))
            focusLog.fine("Skipping {0} because focus owner is null", new Object[] { paramAWTEvent }); 
        } else if (component1 == focusEvent.getOppositeComponent()) {
          if (focusLog.isLoggable(PlatformLogger.Level.FINE))
            focusLog.fine("Skipping {0} because current focus owner is equal to opposite", new Object[] { paramAWTEvent }); 
        } else {
          setGlobalFocusOwner(null);
          if (getGlobalFocusOwner() != null) {
            restoreFocus(component1, true);
          } else {
            if (!focusEvent.isTemporary()) {
              setGlobalPermanentFocusOwner(null);
              if (getGlobalPermanentFocusOwner() != null) {
                restoreFocus(component1, true);
                return true;
              } 
            } else {
              component2 = component1.getContainingWindow();
              if (component2 != null)
                component2.setTemporaryLostComponent(component1); 
            } 
            setNativeFocusOwner(null);
            focusEvent.setSource(component1);
            this.realOppositeComponentWR = (focusEvent.getOppositeComponent() != null) ? new WeakReference(component1) : NULL_COMPONENT_WR;
            return typeAheadAssertions(component1, focusEvent);
          } 
        } 
        return true;
      case 206:
        windowEvent1 = (WindowEvent)paramAWTEvent;
        component1 = getGlobalActiveWindow();
        if (component1 != null && component1 == paramAWTEvent.getSource()) {
          setGlobalActiveWindow(null);
          if (getGlobalActiveWindow() == null) {
            windowEvent1.setSource(component1);
            return typeAheadAssertions(component1, windowEvent1);
          } 
        } 
        return true;
      case 208:
        if (!repostIfFollowsKeyEvents((WindowEvent)paramAWTEvent)) {
          windowEvent1 = (WindowEvent)paramAWTEvent;
          component1 = getGlobalFocusedWindow();
          component2 = windowEvent1.getWindow();
          component3 = getGlobalActiveWindow();
          Window window = windowEvent1.getOppositeWindow();
          if (focusLog.isLoggable(PlatformLogger.Level.FINE))
            focusLog.fine("Active {0}, Current focused {1}, losing focus {2} opposite {3}", new Object[] { component3, component1, component2, window }); 
          if (component1 != null && (this.inSendMessage != 0 || component2 != component3 || window != component1)) {
            Component component = getGlobalFocusOwner();
            if (component != null) {
              Component component4 = null;
              if (window != null) {
                component4 = window.getTemporaryLostComponent();
                if (component4 == null)
                  component4 = window.getMostRecentFocusOwner(); 
              } 
              if (component4 == null)
                component4 = window; 
              sendMessage(component, new CausedFocusEvent(component, 1005, true, component4, CausedFocusEvent.Cause.ACTIVATION));
            } 
            setGlobalFocusedWindow(null);
            if (getGlobalFocusedWindow() != null) {
              restoreFocus(component1, null, true);
            } else {
              windowEvent1.setSource(component1);
              this.realOppositeWindowWR = (window != null) ? new WeakReference(component1) : NULL_WINDOW_WR;
              typeAheadAssertions(component1, windowEvent1);
              if (window == null) {
                sendMessage(component3, new WindowEvent(component3, 206, null));
                if (getGlobalActiveWindow() != null)
                  restoreFocus(component1, null, true); 
              } 
            } 
          } 
        } 
        return true;
      case 400:
      case 401:
      case 402:
        return typeAheadAssertions(null, paramAWTEvent);
    } 
    return false;
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent) {
    Component component1 = paramKeyEvent.isPosted ? getFocusOwner() : paramKeyEvent.getComponent();
    if (component1 != null && component1.isShowing() && component1.canBeFocusOwner() && !paramKeyEvent.isConsumed()) {
      Component component = paramKeyEvent.getComponent();
      if (component != null && component.isEnabled())
        redispatchEvent(component, paramKeyEvent); 
    } 
    boolean bool = false;
    List list = getKeyEventPostProcessors();
    if (list != null) {
      Iterator iterator = list.iterator();
      while (!bool && iterator.hasNext())
        bool = ((KeyEventPostProcessor)iterator.next()).postProcessKeyEvent(paramKeyEvent); 
    } 
    if (!bool)
      postProcessKeyEvent(paramKeyEvent); 
    Component component2 = paramKeyEvent.getComponent();
    ComponentPeer componentPeer = component2.getPeer();
    if (componentPeer == null || componentPeer instanceof java.awt.peer.LightweightPeer) {
      Container container = component2.getNativeContainer();
      if (container != null)
        componentPeer = container.getPeer(); 
    } 
    if (componentPeer != null)
      componentPeer.handleEvent(paramKeyEvent); 
    return true;
  }
  
  public boolean postProcessKeyEvent(KeyEvent paramKeyEvent) {
    if (!paramKeyEvent.isConsumed()) {
      Component component = paramKeyEvent.getComponent();
      Container container = (Container)((component instanceof Container) ? component : component.getParent());
      if (container != null)
        container.postProcessKeyEvent(paramKeyEvent); 
    } 
    return true;
  }
  
  private void pumpApprovedKeyEvents() {
    KeyEvent keyEvent;
    do {
      keyEvent = null;
      synchronized (this) {
        if (this.enqueuedKeyEvents.size() != 0) {
          keyEvent = (KeyEvent)this.enqueuedKeyEvents.getFirst();
          if (this.typeAheadMarkers.size() != 0) {
            TypeAheadMarker typeAheadMarker = (TypeAheadMarker)this.typeAheadMarkers.getFirst();
            if (keyEvent.getWhen() > typeAheadMarker.after)
              keyEvent = null; 
          } 
          if (keyEvent != null) {
            if (focusLog.isLoggable(PlatformLogger.Level.FINER))
              focusLog.finer("Pumping approved event {0}", new Object[] { keyEvent }); 
            this.enqueuedKeyEvents.removeFirst();
          } 
        } 
      } 
      if (keyEvent == null)
        continue; 
      preDispatchKeyEvent(keyEvent);
    } while (keyEvent != null);
  }
  
  void dumpMarkers() {
    if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
      focusLog.finest(">>> Markers dump, time: {0}", new Object[] { Long.valueOf(System.currentTimeMillis()) });
      synchronized (this) {
        if (this.typeAheadMarkers.size() != 0)
          for (TypeAheadMarker typeAheadMarker : this.typeAheadMarkers) {
            focusLog.finest("    {0}", new Object[] { typeAheadMarker });
          }  
      } 
    } 
  }
  
  private boolean typeAheadAssertions(Component paramComponent, AWTEvent paramAWTEvent) {
    KeyEvent keyEvent;
    pumpApprovedKeyEvents();
    switch (paramAWTEvent.getID()) {
      case 400:
      case 401:
      case 402:
        keyEvent = (KeyEvent)paramAWTEvent;
        synchronized (this) {
          if (paramAWTEvent.isPosted && this.typeAheadMarkers.size() != 0) {
            TypeAheadMarker typeAheadMarker = (TypeAheadMarker)this.typeAheadMarkers.getFirst();
            if (keyEvent.getWhen() > typeAheadMarker.after) {
              if (focusLog.isLoggable(PlatformLogger.Level.FINER))
                focusLog.finer("Storing event {0} because of marker {1}", new Object[] { keyEvent, typeAheadMarker }); 
              this.enqueuedKeyEvents.addLast(keyEvent);
              return true;
            } 
          } 
        } 
        return preDispatchKeyEvent(keyEvent);
      case 1004:
        if (focusLog.isLoggable(PlatformLogger.Level.FINEST))
          focusLog.finest("Markers before FOCUS_GAINED on {0}", new Object[] { paramComponent }); 
        dumpMarkers();
        synchronized (this) {
          boolean bool = false;
          if (hasMarker(paramComponent)) {
            Iterator iterator = this.typeAheadMarkers.iterator();
            while (iterator.hasNext()) {
              if (((TypeAheadMarker)iterator.next()).untilFocused == paramComponent) {
                bool = true;
              } else if (bool) {
                break;
              } 
              iterator.remove();
            } 
          } else if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
            focusLog.finer("Event without marker {0}", new Object[] { paramAWTEvent });
          } 
        } 
        focusLog.finest("Markers after FOCUS_GAINED");
        dumpMarkers();
        redispatchEvent(paramComponent, paramAWTEvent);
        pumpApprovedKeyEvents();
        return true;
    } 
    redispatchEvent(paramComponent, paramAWTEvent);
    return true;
  }
  
  private boolean hasMarker(Component paramComponent) {
    Iterator iterator = this.typeAheadMarkers.iterator();
    while (iterator.hasNext()) {
      if (((TypeAheadMarker)iterator.next()).untilFocused == paramComponent)
        return true; 
    } 
    return false;
  }
  
  void clearMarkers() {
    synchronized (this) {
      this.typeAheadMarkers.clear();
    } 
  }
  
  private boolean preDispatchKeyEvent(KeyEvent paramKeyEvent) {
    if (paramKeyEvent.isPosted) {
      Component component = getFocusOwner();
      paramKeyEvent.setSource((component != null) ? component : getFocusedWindow());
    } 
    if (paramKeyEvent.getSource() == null)
      return true; 
    EventQueue.setCurrentEventAndMostRecentTime(paramKeyEvent);
    if (KeyboardFocusManager.isProxyActive(paramKeyEvent)) {
      Component component = (Component)paramKeyEvent.getSource();
      Container container = component.getNativeContainer();
      if (container != null) {
        ComponentPeer componentPeer = container.getPeer();
        if (componentPeer != null) {
          componentPeer.handleEvent(paramKeyEvent);
          paramKeyEvent.consume();
        } 
      } 
      return true;
    } 
    List list = getKeyEventDispatchers();
    if (list != null) {
      Iterator iterator = list.iterator();
      while (iterator.hasNext()) {
        if (((KeyEventDispatcher)iterator.next()).dispatchKeyEvent(paramKeyEvent))
          return true; 
      } 
    } 
    return dispatchKeyEvent(paramKeyEvent);
  }
  
  private void consumeNextKeyTyped(KeyEvent paramKeyEvent) { this.consumeNextKeyTyped = true; }
  
  private void consumeTraversalKey(KeyEvent paramKeyEvent) {
    paramKeyEvent.consume();
    this.consumeNextKeyTyped = (paramKeyEvent.getID() == 401 && !paramKeyEvent.isActionKey());
  }
  
  private boolean consumeProcessedKeyEvent(KeyEvent paramKeyEvent) {
    if (paramKeyEvent.getID() == 400 && this.consumeNextKeyTyped) {
      paramKeyEvent.consume();
      this.consumeNextKeyTyped = false;
      return true;
    } 
    return false;
  }
  
  public void processKeyEvent(Component paramComponent, KeyEvent paramKeyEvent) {
    if (consumeProcessedKeyEvent(paramKeyEvent))
      return; 
    if (paramKeyEvent.getID() == 400)
      return; 
    if (paramComponent.getFocusTraversalKeysEnabled() && !paramKeyEvent.isConsumed()) {
      AWTKeyStroke aWTKeyStroke1;
      AWTKeyStroke aWTKeyStroke2 = (aWTKeyStroke1 = AWTKeyStroke.getAWTKeyStrokeForEvent(paramKeyEvent)).getAWTKeyStroke(aWTKeyStroke1.getKeyCode(), aWTKeyStroke1.getModifiers(), !aWTKeyStroke1.isOnKeyRelease());
      Set set = paramComponent.getFocusTraversalKeys(0);
      boolean bool1 = set.contains(aWTKeyStroke1);
      boolean bool2 = set.contains(aWTKeyStroke2);
      if (bool1 || bool2) {
        consumeTraversalKey(paramKeyEvent);
        if (bool1)
          focusNextComponent(paramComponent); 
        return;
      } 
      if (paramKeyEvent.getID() == 401)
        this.consumeNextKeyTyped = false; 
      set = paramComponent.getFocusTraversalKeys(1);
      bool1 = set.contains(aWTKeyStroke1);
      bool2 = set.contains(aWTKeyStroke2);
      if (bool1 || bool2) {
        consumeTraversalKey(paramKeyEvent);
        if (bool1)
          focusPreviousComponent(paramComponent); 
        return;
      } 
      set = paramComponent.getFocusTraversalKeys(2);
      bool1 = set.contains(aWTKeyStroke1);
      bool2 = set.contains(aWTKeyStroke2);
      if (bool1 || bool2) {
        consumeTraversalKey(paramKeyEvent);
        if (bool1)
          upFocusCycle(paramComponent); 
        return;
      } 
      if (!(paramComponent instanceof Container) || !((Container)paramComponent).isFocusCycleRoot())
        return; 
      set = paramComponent.getFocusTraversalKeys(3);
      bool1 = set.contains(aWTKeyStroke1);
      bool2 = set.contains(aWTKeyStroke2);
      if (bool1 || bool2) {
        consumeTraversalKey(paramKeyEvent);
        if (bool1)
          downFocusCycle((Container)paramComponent); 
      } 
    } 
  }
  
  protected void enqueueKeyEvents(long paramLong, Component paramComponent) {
    if (paramComponent == null)
      return; 
    if (focusLog.isLoggable(PlatformLogger.Level.FINER))
      focusLog.finer("Enqueue at {0} for {1}", new Object[] { Long.valueOf(paramLong), paramComponent }); 
    int i = 0;
    int j = this.typeAheadMarkers.size();
    ListIterator listIterator = this.typeAheadMarkers.listIterator(j);
    while (j > 0) {
      TypeAheadMarker typeAheadMarker = (TypeAheadMarker)listIterator.previous();
      if (typeAheadMarker.after <= paramLong) {
        i = j;
        break;
      } 
      j--;
    } 
    this.typeAheadMarkers.add(i, new TypeAheadMarker(paramLong, paramComponent));
  }
  
  protected void dequeueKeyEvents(long paramLong, Component paramComponent) {
    if (paramComponent == null)
      return; 
    if (focusLog.isLoggable(PlatformLogger.Level.FINER))
      focusLog.finer("Dequeue at {0} for {1}", new Object[] { Long.valueOf(paramLong), paramComponent }); 
    ListIterator listIterator = this.typeAheadMarkers.listIterator((paramLong >= 0L) ? this.typeAheadMarkers.size() : 0);
    if (paramLong < 0L) {
      while (listIterator.hasNext()) {
        TypeAheadMarker typeAheadMarker = (TypeAheadMarker)listIterator.next();
        if (typeAheadMarker.untilFocused == paramComponent) {
          listIterator.remove();
          return;
        } 
      } 
    } else {
      while (listIterator.hasPrevious()) {
        TypeAheadMarker typeAheadMarker = (TypeAheadMarker)listIterator.previous();
        if (typeAheadMarker.untilFocused == paramComponent && typeAheadMarker.after == paramLong) {
          listIterator.remove();
          return;
        } 
      } 
    } 
  }
  
  protected void discardKeyEvents(Component paramComponent) {
    if (paramComponent == null)
      return; 
    long l = -1L;
    Iterator iterator = this.typeAheadMarkers.iterator();
    while (iterator.hasNext()) {
      TypeAheadMarker typeAheadMarker = (TypeAheadMarker)iterator.next();
      Component component = typeAheadMarker.untilFocused;
      boolean bool;
      for (bool = (component == paramComponent) ? 1 : 0; !bool && component != null && !(component instanceof Window); bool = (component == paramComponent) ? 1 : 0)
        component = component.getParent(); 
      if (bool) {
        if (l < 0L)
          l = typeAheadMarker.after; 
        iterator.remove();
        continue;
      } 
      if (l >= 0L) {
        purgeStampedEvents(l, typeAheadMarker.after);
        l = -1L;
      } 
    } 
    purgeStampedEvents(l, -1L);
  }
  
  private void purgeStampedEvents(long paramLong1, long paramLong2) {
    if (paramLong1 < 0L)
      return; 
    Iterator iterator = this.enqueuedKeyEvents.iterator();
    while (iterator.hasNext()) {
      KeyEvent keyEvent = (KeyEvent)iterator.next();
      long l = keyEvent.getWhen();
      if (paramLong1 < l && (paramLong2 < 0L || l <= paramLong2))
        iterator.remove(); 
      if (paramLong2 >= 0L && l > paramLong2)
        break; 
    } 
  }
  
  public void focusPreviousComponent(Component paramComponent) {
    if (paramComponent != null)
      paramComponent.transferFocusBackward(); 
  }
  
  public void focusNextComponent(Component paramComponent) {
    if (paramComponent != null)
      paramComponent.transferFocus(); 
  }
  
  public void upFocusCycle(Component paramComponent) {
    if (paramComponent != null)
      paramComponent.transferFocusUpCycle(); 
  }
  
  public void downFocusCycle(Container paramContainer) {
    if (paramContainer != null && paramContainer.isFocusCycleRoot())
      paramContainer.transferFocusDownCycle(); 
  }
  
  static  {
    AWTAccessor.setDefaultKeyboardFocusManagerAccessor(new AWTAccessor.DefaultKeyboardFocusManagerAccessor() {
          public void consumeNextKeyTyped(DefaultKeyboardFocusManager param1DefaultKeyboardFocusManager, KeyEvent param1KeyEvent) { param1DefaultKeyboardFocusManager.consumeNextKeyTyped(param1KeyEvent); }
        });
  }
  
  private static class DefaultKeyboardFocusManagerSentEvent extends SentEvent {
    private static final long serialVersionUID = -2924743257508701758L;
    
    public DefaultKeyboardFocusManagerSentEvent(AWTEvent param1AWTEvent, AppContext param1AppContext) { super(param1AWTEvent, param1AppContext); }
    
    public final void dispatch() {
      KeyboardFocusManager keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
      DefaultKeyboardFocusManager defaultKeyboardFocusManager = (keyboardFocusManager instanceof DefaultKeyboardFocusManager) ? (DefaultKeyboardFocusManager)keyboardFocusManager : null;
      if (defaultKeyboardFocusManager != null)
        synchronized (defaultKeyboardFocusManager) {
          defaultKeyboardFocusManager.inSendMessage++;
        }  
      super.dispatch();
      if (defaultKeyboardFocusManager != null)
        synchronized (defaultKeyboardFocusManager) {
          defaultKeyboardFocusManager.inSendMessage--;
        }  
    }
  }
  
  private static class TypeAheadMarker {
    long after;
    
    Component untilFocused;
    
    TypeAheadMarker(long param1Long, Component param1Component) {
      this.after = param1Long;
      this.untilFocused = param1Component;
    }
    
    public String toString() { return ">>> Marker after " + this.after + " on " + this.untilFocused; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\DefaultKeyboardFocusManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */