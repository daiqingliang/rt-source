package com.sun.java.swing;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import javax.swing.JComponent;
import javax.swing.RepaintManager;
import sun.awt.AppContext;
import sun.awt.EventQueueDelegate;
import sun.awt.SunToolkit;

public class SwingUtilities3 {
  private static final Object DELEGATE_REPAINT_MANAGER_KEY = new StringBuilder("DelegateRepaintManagerKey");
  
  private static final Map<Container, Boolean> vsyncedMap = Collections.synchronizedMap(new WeakHashMap());
  
  public static void setDelegateRepaintManager(JComponent paramJComponent, RepaintManager paramRepaintManager) {
    AppContext.getAppContext().put(DELEGATE_REPAINT_MANAGER_KEY, Boolean.TRUE);
    paramJComponent.putClientProperty(DELEGATE_REPAINT_MANAGER_KEY, paramRepaintManager);
  }
  
  public static void setVsyncRequested(Container paramContainer, boolean paramBoolean) {
    assert paramContainer instanceof java.applet.Applet || paramContainer instanceof java.awt.Window;
    if (paramBoolean) {
      vsyncedMap.put(paramContainer, Boolean.TRUE);
    } else {
      vsyncedMap.remove(paramContainer);
    } 
  }
  
  public static boolean isVsyncRequested(Container paramContainer) {
    assert paramContainer instanceof java.applet.Applet || paramContainer instanceof java.awt.Window;
    return (Boolean.TRUE == vsyncedMap.get(paramContainer));
  }
  
  public static RepaintManager getDelegateRepaintManager(Component paramComponent) {
    RepaintManager repaintManager = null;
    if (Boolean.TRUE == SunToolkit.targetToAppContext(paramComponent).get(DELEGATE_REPAINT_MANAGER_KEY))
      while (repaintManager == null && paramComponent != null) {
        while (paramComponent != null && !(paramComponent instanceof JComponent))
          paramComponent = paramComponent.getParent(); 
        if (paramComponent != null) {
          repaintManager = (RepaintManager)((JComponent)paramComponent).getClientProperty(DELEGATE_REPAINT_MANAGER_KEY);
          paramComponent = paramComponent.getParent();
        } 
      }  
    return repaintManager;
  }
  
  public static void setEventQueueDelegate(Map<String, Map<String, Object>> paramMap) { EventQueueDelegate.setDelegate(new EventQueueDelegateFromMap(paramMap)); }
  
  private static class EventQueueDelegateFromMap implements EventQueueDelegate.Delegate {
    private final AWTEvent[] afterDispatchEventArgument;
    
    private final Object[] afterDispatchHandleArgument;
    
    private final Callable<Void> afterDispatchCallable;
    
    private final AWTEvent[] beforeDispatchEventArgument;
    
    private final Callable<Object> beforeDispatchCallable;
    
    private final EventQueue[] getNextEventEventQueueArgument;
    
    private final Callable<AWTEvent> getNextEventCallable;
    
    public EventQueueDelegateFromMap(Map<String, Map<String, Object>> param1Map) {
      Map map = (Map)param1Map.get("afterDispatch");
      this.afterDispatchEventArgument = (AWTEvent[])map.get("event");
      this.afterDispatchHandleArgument = (Object[])map.get("handle");
      this.afterDispatchCallable = (Callable)map.get("method");
      map = (Map)param1Map.get("beforeDispatch");
      this.beforeDispatchEventArgument = (AWTEvent[])map.get("event");
      this.beforeDispatchCallable = (Callable)map.get("method");
      map = (Map)param1Map.get("getNextEvent");
      this.getNextEventEventQueueArgument = (EventQueue[])map.get("eventQueue");
      this.getNextEventCallable = (Callable)map.get("method");
    }
    
    public void afterDispatch(AWTEvent param1AWTEvent, Object param1Object) throws InterruptedException {
      this.afterDispatchEventArgument[0] = param1AWTEvent;
      this.afterDispatchHandleArgument[0] = param1Object;
      try {
        this.afterDispatchCallable.call();
      } catch (InterruptedException interruptedException) {
        throw interruptedException;
      } catch (RuntimeException runtimeException) {
        throw runtimeException;
      } catch (Exception exception) {
        throw new RuntimeException(exception);
      } 
    }
    
    public Object beforeDispatch(AWTEvent param1AWTEvent) throws InterruptedException {
      this.beforeDispatchEventArgument[0] = param1AWTEvent;
      try {
        return this.beforeDispatchCallable.call();
      } catch (InterruptedException interruptedException) {
        throw interruptedException;
      } catch (RuntimeException runtimeException) {
        throw runtimeException;
      } catch (Exception exception) {
        throw new RuntimeException(exception);
      } 
    }
    
    public AWTEvent getNextEvent(EventQueue param1EventQueue) throws InterruptedException {
      this.getNextEventEventQueueArgument[0] = param1EventQueue;
      try {
        return (AWTEvent)this.getNextEventCallable.call();
      } catch (InterruptedException interruptedException) {
        throw interruptedException;
      } catch (RuntimeException runtimeException) {
        throw runtimeException;
      } catch (Exception exception) {
        throw new RuntimeException(exception);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\SwingUtilities3.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */