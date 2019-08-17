package java.awt;

import java.awt.event.AWTEventListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.awt.AWTAccessor;
import sun.awt.AppContext;
import sun.awt.SunToolkit;
import sun.awt.dnd.SunDropTargetEvent;
import sun.util.logging.PlatformLogger;

class LightweightDispatcher implements Serializable, AWTEventListener {
  private static final long serialVersionUID = 5184291520170872969L;
  
  private static final int LWD_MOUSE_DRAGGED_OVER = 1500;
  
  private static final PlatformLogger eventLog = PlatformLogger.getLogger("java.awt.event.LightweightDispatcher");
  
  private static final int BUTTONS_DOWN_MASK;
  
  private Container nativeContainer;
  
  private Component focus;
  
  private WeakReference<Component> mouseEventTarget;
  
  private WeakReference<Component> targetLastEntered;
  
  private WeakReference<Component> targetLastEnteredDT;
  
  private boolean isMouseInNativeContainer = false;
  
  private boolean isMouseDTInNativeContainer = false;
  
  private Cursor nativeCursor;
  
  private long eventMask;
  
  private static final long PROXY_EVENT_MASK = 131132L;
  
  private static final long MOUSE_MASK = 131120L;
  
  LightweightDispatcher(Container paramContainer) {
    this.nativeContainer = paramContainer;
    this.mouseEventTarget = new WeakReference(null);
    this.targetLastEntered = new WeakReference(null);
    this.targetLastEnteredDT = new WeakReference(null);
    this.eventMask = 0L;
  }
  
  void dispose() {
    stopListeningForOtherDrags();
    this.mouseEventTarget.clear();
    this.targetLastEntered.clear();
    this.targetLastEnteredDT.clear();
  }
  
  void enableEvents(long paramLong) { this.eventMask |= paramLong; }
  
  boolean dispatchEvent(AWTEvent paramAWTEvent) {
    boolean bool = false;
    if (paramAWTEvent instanceof SunDropTargetEvent) {
      SunDropTargetEvent sunDropTargetEvent = (SunDropTargetEvent)paramAWTEvent;
      bool = processDropTargetEvent(sunDropTargetEvent);
    } else {
      if (paramAWTEvent instanceof MouseEvent && (this.eventMask & 0x20030L) != 0L) {
        MouseEvent mouseEvent = (MouseEvent)paramAWTEvent;
        bool = processMouseEvent(mouseEvent);
      } 
      if (paramAWTEvent.getID() == 503)
        this.nativeContainer.updateCursorImmediately(); 
    } 
    return bool;
  }
  
  private boolean isMouseGrab(MouseEvent paramMouseEvent) {
    int i = paramMouseEvent.getModifiersEx();
    if (paramMouseEvent.getID() == 501 || paramMouseEvent.getID() == 502)
      i ^= InputEvent.getMaskForButton(paramMouseEvent.getButton()); 
    return ((i & BUTTONS_DOWN_MASK) != 0);
  }
  
  private boolean processMouseEvent(MouseEvent paramMouseEvent) {
    int i = paramMouseEvent.getID();
    Component component1 = this.nativeContainer.getMouseEventTarget(paramMouseEvent.getX(), paramMouseEvent.getY(), true);
    trackMouseEnterExit(component1, paramMouseEvent);
    Component component2 = (Component)this.mouseEventTarget.get();
    if (!isMouseGrab(paramMouseEvent) && i != 500) {
      component2 = (component1 != this.nativeContainer) ? component1 : null;
      this.mouseEventTarget = new WeakReference(component2);
    } 
    if (component2 != null) {
      switch (i) {
        case 501:
          retargetMouseEvent(component2, i, paramMouseEvent);
          break;
        case 502:
          retargetMouseEvent(component2, i, paramMouseEvent);
          break;
        case 500:
          if (component1 == component2)
            retargetMouseEvent(component1, i, paramMouseEvent); 
          break;
        case 503:
          retargetMouseEvent(component2, i, paramMouseEvent);
          break;
        case 506:
          if (isMouseGrab(paramMouseEvent))
            retargetMouseEvent(component2, i, paramMouseEvent); 
          break;
        case 507:
          if (eventLog.isLoggable(PlatformLogger.Level.FINEST) && component1 != null)
            eventLog.finest("retargeting mouse wheel to " + component1.getName() + ", " + component1.getClass()); 
          retargetMouseEvent(component1, i, paramMouseEvent);
          break;
      } 
      if (i != 507)
        paramMouseEvent.consume(); 
    } 
    return paramMouseEvent.isConsumed();
  }
  
  private boolean processDropTargetEvent(SunDropTargetEvent paramSunDropTargetEvent) {
    int i = paramSunDropTargetEvent.getID();
    int j = paramSunDropTargetEvent.getX();
    int k = paramSunDropTargetEvent.getY();
    if (!this.nativeContainer.contains(j, k)) {
      Dimension dimension = this.nativeContainer.getSize();
      if (dimension.width <= j) {
        j = dimension.width - 1;
      } else if (j < 0) {
        j = 0;
      } 
      if (dimension.height <= k) {
        k = dimension.height - 1;
      } else if (k < 0) {
        k = 0;
      } 
    } 
    Component component = this.nativeContainer.getDropTargetEventTarget(j, k, true);
    trackMouseEnterExit(component, paramSunDropTargetEvent);
    if (component != this.nativeContainer && component != null) {
      switch (i) {
        case 504:
        case 505:
          return paramSunDropTargetEvent.isConsumed();
      } 
      retargetMouseEvent(component, i, paramSunDropTargetEvent);
      paramSunDropTargetEvent.consume();
    } 
  }
  
  private void trackDropTargetEnterExit(Component paramComponent, MouseEvent paramMouseEvent) {
    int i = paramMouseEvent.getID();
    if (i == 504 && this.isMouseDTInNativeContainer) {
      this.targetLastEnteredDT.clear();
    } else if (i == 504) {
      this.isMouseDTInNativeContainer = true;
    } else if (i == 505) {
      this.isMouseDTInNativeContainer = false;
    } 
    Component component = retargetMouseEnterExit(paramComponent, paramMouseEvent, (Component)this.targetLastEnteredDT.get(), this.isMouseDTInNativeContainer);
    this.targetLastEnteredDT = new WeakReference(component);
  }
  
  private void trackMouseEnterExit(Component paramComponent, MouseEvent paramMouseEvent) {
    if (paramMouseEvent instanceof SunDropTargetEvent) {
      trackDropTargetEnterExit(paramComponent, paramMouseEvent);
      return;
    } 
    int i = paramMouseEvent.getID();
    if (i != 505 && i != 506 && i != 1500 && !this.isMouseInNativeContainer) {
      this.isMouseInNativeContainer = true;
      startListeningForOtherDrags();
    } else if (i == 505) {
      this.isMouseInNativeContainer = false;
      stopListeningForOtherDrags();
    } 
    Component component = retargetMouseEnterExit(paramComponent, paramMouseEvent, (Component)this.targetLastEntered.get(), this.isMouseInNativeContainer);
    this.targetLastEntered = new WeakReference(component);
  }
  
  private Component retargetMouseEnterExit(Component paramComponent1, MouseEvent paramMouseEvent, Component paramComponent2, boolean paramBoolean) {
    int i = paramMouseEvent.getID();
    Component component = paramBoolean ? paramComponent1 : null;
    if (paramComponent2 != component) {
      if (paramComponent2 != null)
        retargetMouseEvent(paramComponent2, 505, paramMouseEvent); 
      if (i == 505)
        paramMouseEvent.consume(); 
      if (component != null)
        retargetMouseEvent(component, 504, paramMouseEvent); 
      if (i == 504)
        paramMouseEvent.consume(); 
    } 
    return component;
  }
  
  private void startListeningForOtherDrags() { AccessController.doPrivileged(new PrivilegedAction<Object>() {
          public Object run() {
            LightweightDispatcher.this.nativeContainer.getToolkit().addAWTEventListener(LightweightDispatcher.this, 48L);
            return null;
          }
        }); }
  
  private void stopListeningForOtherDrags() { AccessController.doPrivileged(new PrivilegedAction<Object>() {
          public Object run() {
            LightweightDispatcher.this.nativeContainer.getToolkit().removeAWTEventListener(LightweightDispatcher.this);
            return null;
          }
        }); }
  
  public void eventDispatched(AWTEvent paramAWTEvent) {
    MouseEvent mouseEvent2;
    boolean bool = (paramAWTEvent instanceof MouseEvent && !(paramAWTEvent instanceof SunDropTargetEvent) && paramAWTEvent.id == 506 && paramAWTEvent.getSource() != this.nativeContainer) ? 1 : 0;
    if (!bool)
      return; 
    MouseEvent mouseEvent1 = (MouseEvent)paramAWTEvent;
    synchronized (this.nativeContainer.getTreeLock()) {
      Component component1 = mouseEvent1.getComponent();
      if (!component1.isShowing())
        return; 
      Container container;
      for (container = this.nativeContainer; container != null && !(container instanceof Window); container = container.getParent_NoClientCode());
      if (container == null || ((Window)container).isModalBlocked())
        return; 
      mouseEvent2 = new MouseEvent(this.nativeContainer, 1500, mouseEvent1.getWhen(), mouseEvent1.getModifiersEx() | mouseEvent1.getModifiers(), mouseEvent1.getX(), mouseEvent1.getY(), mouseEvent1.getXOnScreen(), mouseEvent1.getYOnScreen(), mouseEvent1.getClickCount(), mouseEvent1.isPopupTrigger(), mouseEvent1.getButton());
      AWTAccessor.MouseEventAccessor mouseEventAccessor = AWTAccessor.getMouseEventAccessor();
      mouseEventAccessor.setCausedByTouchEvent(mouseEvent2, mouseEventAccessor.isCausedByTouchEvent(mouseEvent1));
      mouseEvent1.copyPrivateDataInto(mouseEvent2);
      final Point ptSrcOrigin = component1.getLocationOnScreen();
      if (AppContext.getAppContext() != this.nativeContainer.appContext) {
        final MouseEvent mouseEvent = mouseEvent2;
        Runnable runnable = new Runnable() {
            public void run() {
              if (!LightweightDispatcher.this.nativeContainer.isShowing())
                return; 
              Point point = LightweightDispatcher.this.nativeContainer.getLocationOnScreen();
              mouseEvent.translatePoint(this.val$ptSrcOrigin.x - point.x, this.val$ptSrcOrigin.y - point.y);
              Component component = LightweightDispatcher.this.nativeContainer.getMouseEventTarget(mouseEvent.getX(), mouseEvent.getY(), true);
              LightweightDispatcher.this.trackMouseEnterExit(component, mouseEvent);
            }
          };
        SunToolkit.executeOnEventHandlerThread(this.nativeContainer, runnable);
        return;
      } 
      if (!this.nativeContainer.isShowing())
        return; 
      Point point2 = this.nativeContainer.getLocationOnScreen();
      mouseEvent2.translatePoint(point1.x - point2.x, point1.y - point2.y);
    } 
    Component component = this.nativeContainer.getMouseEventTarget(mouseEvent2.getX(), mouseEvent2.getY(), true);
    trackMouseEnterExit(component, mouseEvent2);
  }
  
  void retargetMouseEvent(Component paramComponent, int paramInt, MouseEvent paramMouseEvent) {
    if (paramComponent == null)
      return; 
    int i = paramMouseEvent.getX();
    int j = paramMouseEvent.getY();
    Component component;
    for (component = paramComponent; component != null && component != this.nativeContainer; component = component.getParent()) {
      i -= component.x;
      j -= component.y;
    } 
    if (component != null) {
      MouseEvent mouseEvent;
      if (paramMouseEvent instanceof SunDropTargetEvent) {
        mouseEvent = new SunDropTargetEvent(paramComponent, paramInt, i, j, ((SunDropTargetEvent)paramMouseEvent).getDispatcher());
      } else if (paramInt == 507) {
        mouseEvent = new MouseWheelEvent(paramComponent, paramInt, paramMouseEvent.getWhen(), paramMouseEvent.getModifiersEx() | paramMouseEvent.getModifiers(), i, j, paramMouseEvent.getXOnScreen(), paramMouseEvent.getYOnScreen(), paramMouseEvent.getClickCount(), paramMouseEvent.isPopupTrigger(), ((MouseWheelEvent)paramMouseEvent).getScrollType(), ((MouseWheelEvent)paramMouseEvent).getScrollAmount(), ((MouseWheelEvent)paramMouseEvent).getWheelRotation(), ((MouseWheelEvent)paramMouseEvent).getPreciseWheelRotation());
      } else {
        mouseEvent = new MouseEvent(paramComponent, paramInt, paramMouseEvent.getWhen(), paramMouseEvent.getModifiersEx() | paramMouseEvent.getModifiers(), i, j, paramMouseEvent.getXOnScreen(), paramMouseEvent.getYOnScreen(), paramMouseEvent.getClickCount(), paramMouseEvent.isPopupTrigger(), paramMouseEvent.getButton());
        AWTAccessor.MouseEventAccessor mouseEventAccessor = AWTAccessor.getMouseEventAccessor();
        mouseEventAccessor.setCausedByTouchEvent(mouseEvent, mouseEventAccessor.isCausedByTouchEvent(paramMouseEvent));
      } 
      paramMouseEvent.copyPrivateDataInto(mouseEvent);
      if (paramComponent == this.nativeContainer) {
        ((Container)paramComponent).dispatchEventToSelf(mouseEvent);
      } else {
        assert AppContext.getAppContext() == paramComponent.appContext;
        if (this.nativeContainer.modalComp != null) {
          if (((Container)this.nativeContainer.modalComp).isAncestorOf(paramComponent)) {
            paramComponent.dispatchEvent(mouseEvent);
          } else {
            paramMouseEvent.consume();
          } 
        } else {
          paramComponent.dispatchEvent(mouseEvent);
        } 
      } 
      if (paramInt == 507 && mouseEvent.isConsumed())
        paramMouseEvent.consume(); 
    } 
  }
  
  static  {
    int[] arrayOfInt = AWTAccessor.getInputEventAccessor().getButtonDownMasks();
    int i = 0;
    for (int j : arrayOfInt)
      i |= j; 
    BUTTONS_DOWN_MASK = i;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\LightweightDispatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */