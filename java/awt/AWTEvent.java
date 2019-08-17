package java.awt;

import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.peer.ComponentPeer;
import java.lang.reflect.Field;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.EventObject;
import sun.awt.AWTAccessor;
import sun.util.logging.PlatformLogger;

public abstract class AWTEvent extends EventObject {
  private static final PlatformLogger log = PlatformLogger.getLogger("java.awt.AWTEvent");
  
  private byte[] bdata;
  
  protected int id;
  
  protected boolean consumed = false;
  
  boolean focusManagerIsDispatching = false;
  
  boolean isPosted;
  
  private boolean isSystemGenerated;
  
  public static final long COMPONENT_EVENT_MASK = 1L;
  
  public static final long CONTAINER_EVENT_MASK = 2L;
  
  public static final long FOCUS_EVENT_MASK = 4L;
  
  public static final long KEY_EVENT_MASK = 8L;
  
  public static final long MOUSE_EVENT_MASK = 16L;
  
  public static final long MOUSE_MOTION_EVENT_MASK = 32L;
  
  public static final long WINDOW_EVENT_MASK = 64L;
  
  public static final long ACTION_EVENT_MASK = 128L;
  
  public static final long ADJUSTMENT_EVENT_MASK = 256L;
  
  public static final long ITEM_EVENT_MASK = 512L;
  
  public static final long TEXT_EVENT_MASK = 1024L;
  
  public static final long INPUT_METHOD_EVENT_MASK = 2048L;
  
  static final long INPUT_METHODS_ENABLED_MASK = 4096L;
  
  public static final long PAINT_EVENT_MASK = 8192L;
  
  public static final long INVOCATION_EVENT_MASK = 16384L;
  
  public static final long HIERARCHY_EVENT_MASK = 32768L;
  
  public static final long HIERARCHY_BOUNDS_EVENT_MASK = 65536L;
  
  public static final long MOUSE_WHEEL_EVENT_MASK = 131072L;
  
  public static final long WINDOW_STATE_EVENT_MASK = 262144L;
  
  public static final long WINDOW_FOCUS_EVENT_MASK = 524288L;
  
  public static final int RESERVED_ID_MAX = 1999;
  
  private static Field inputEvent_CanAccessSystemClipboard_Field = null;
  
  private static final long serialVersionUID = -1825314779160409405L;
  
  final AccessControlContext getAccessControlContext() {
    if (this.acc == null)
      throw new SecurityException("AWTEvent is missing AccessControlContext"); 
    return this.acc;
  }
  
  private static Field get_InputEvent_CanAccessSystemClipboard() {
    if (inputEvent_CanAccessSystemClipboard_Field == null)
      inputEvent_CanAccessSystemClipboard_Field = (Field)AccessController.doPrivileged(new PrivilegedAction<Field>() {
            public Field run() {
              Field field = null;
              try {
                field = java.awt.event.InputEvent.class.getDeclaredField("canAccessSystemClipboard");
                field.setAccessible(true);
                return field;
              } catch (SecurityException securityException) {
                if (log.isLoggable(PlatformLogger.Level.FINE))
                  log.fine("AWTEvent.get_InputEvent_CanAccessSystemClipboard() got SecurityException ", securityException); 
              } catch (NoSuchFieldException noSuchFieldException) {
                if (log.isLoggable(PlatformLogger.Level.FINE))
                  log.fine("AWTEvent.get_InputEvent_CanAccessSystemClipboard() got NoSuchFieldException ", noSuchFieldException); 
              } 
              return null;
            }
          }); 
    return inputEvent_CanAccessSystemClipboard_Field;
  }
  
  private static native void initIDs();
  
  public AWTEvent(Event paramEvent) { this(paramEvent.target, paramEvent.id); }
  
  public AWTEvent(Object paramObject, int paramInt) {
    super(paramObject);
    this.id = paramInt;
    switch (paramInt) {
      case 601:
      case 701:
      case 900:
      case 1001:
        this.consumed = true;
        break;
    } 
  }
  
  public void setSource(Object paramObject) {
    if (this.source == paramObject)
      return; 
    Component component = null;
    if (paramObject instanceof Component)
      for (component = (Component)paramObject; component != null && component.peer != null && component.peer instanceof java.awt.peer.LightweightPeer; component = component.parent); 
    synchronized (this) {
      this.source = paramObject;
      if (component != null) {
        ComponentPeer componentPeer = component.peer;
        if (componentPeer != null)
          nativeSetSource(componentPeer); 
      } 
    } 
  }
  
  private native void nativeSetSource(ComponentPeer paramComponentPeer);
  
  public int getID() { return this.id; }
  
  public String toString() {
    String str = null;
    if (this.source instanceof Component) {
      str = ((Component)this.source).getName();
    } else if (this.source instanceof MenuComponent) {
      str = ((MenuComponent)this.source).getName();
    } 
    return getClass().getName() + "[" + paramString() + "] on " + ((str != null) ? str : this.source);
  }
  
  public String paramString() { return ""; }
  
  protected void consume() {
    switch (this.id) {
      case 401:
      case 402:
      case 501:
      case 502:
      case 503:
      case 504:
      case 505:
      case 506:
      case 507:
      case 1100:
      case 1101:
        this.consumed = true;
        break;
    } 
  }
  
  protected boolean isConsumed() { return this.consumed; }
  
  Event convertToOld() {
    AdjustmentEvent adjustmentEvent;
    Boolean bool;
    ItemEvent itemEvent;
    String str;
    ActionEvent actionEvent;
    Event event;
    MouseEvent mouseEvent;
    int j;
    KeyEvent keyEvent;
    Object object = getSource();
    int i = this.id;
    switch (this.id) {
      case 401:
      case 402:
        keyEvent = (KeyEvent)this;
        if (keyEvent.isActionKey())
          i = (this.id == 401) ? 403 : 404; 
        j = keyEvent.getKeyCode();
        return (j == 16 || j == 17 || j == 18) ? null : new Event(object, keyEvent.getWhen(), i, 0, 0, Event.getOldEventKey(keyEvent), keyEvent.getModifiers() & 0xFFFFFFEF);
      case 501:
      case 502:
      case 503:
      case 504:
      case 505:
      case 506:
        mouseEvent = (MouseEvent)this;
        event = new Event(object, mouseEvent.getWhen(), i, mouseEvent.getX(), mouseEvent.getY(), 0, mouseEvent.getModifiers() & 0xFFFFFFEF);
        event.clickCount = mouseEvent.getClickCount();
        return event;
      case 1004:
        return new Event(object, 1004, null);
      case 1005:
        return new Event(object, 1005, null);
      case 201:
      case 203:
      case 204:
        return new Event(object, i, null);
      case 100:
        if (object instanceof Frame || object instanceof Dialog) {
          Point point = ((Component)object).getLocation();
          return new Event(object, 0L, 205, point.x, point.y, 0, 0);
        } 
        break;
      case 1001:
        actionEvent = (ActionEvent)this;
        if (object instanceof Button) {
          str = ((Button)object).getLabel();
        } else if (object instanceof MenuItem) {
          str = ((MenuItem)object).getLabel();
        } else {
          str = actionEvent.getActionCommand();
        } 
        return new Event(object, 0L, i, 0, 0, 0, actionEvent.getModifiers(), str);
      case 701:
        itemEvent = (ItemEvent)this;
        if (object instanceof List) {
          i = (itemEvent.getStateChange() == 1) ? 701 : 702;
          bool = itemEvent.getItem();
        } else {
          i = 1001;
          if (object instanceof Choice) {
            bool = itemEvent.getItem();
          } else {
            bool = Boolean.valueOf((itemEvent.getStateChange() == 1));
          } 
        } 
        return new Event(object, i, bool);
      case 601:
        adjustmentEvent = (AdjustmentEvent)this;
        switch (adjustmentEvent.getAdjustmentType()) {
          case 1:
            i = 602;
            return new Event(object, i, Integer.valueOf(adjustmentEvent.getValue()));
          case 2:
            i = 601;
            return new Event(object, i, Integer.valueOf(adjustmentEvent.getValue()));
          case 4:
            i = 604;
            return new Event(object, i, Integer.valueOf(adjustmentEvent.getValue()));
          case 3:
            i = 603;
            return new Event(object, i, Integer.valueOf(adjustmentEvent.getValue()));
          case 5:
            if (adjustmentEvent.getValueIsAdjusting()) {
              i = 605;
            } else {
              i = 607;
            } 
            return new Event(object, i, Integer.valueOf(adjustmentEvent.getValue()));
        } 
        return null;
    } 
    return null;
  }
  
  void copyPrivateDataInto(AWTEvent paramAWTEvent) {
    paramAWTEvent.bdata = this.bdata;
    if (this instanceof java.awt.event.InputEvent && paramAWTEvent instanceof java.awt.event.InputEvent) {
      Field field = get_InputEvent_CanAccessSystemClipboard();
      if (field != null)
        try {
          boolean bool = field.getBoolean(this);
          field.setBoolean(paramAWTEvent, bool);
        } catch (IllegalAccessException illegalAccessException) {
          if (log.isLoggable(PlatformLogger.Level.FINE))
            log.fine("AWTEvent.copyPrivateDataInto() got IllegalAccessException ", illegalAccessException); 
        }  
    } 
    paramAWTEvent.isSystemGenerated = this.isSystemGenerated;
  }
  
  void dispatched() {
    if (this instanceof java.awt.event.InputEvent) {
      Field field = get_InputEvent_CanAccessSystemClipboard();
      if (field != null)
        try {
          field.setBoolean(this, false);
        } catch (IllegalAccessException illegalAccessException) {
          if (log.isLoggable(PlatformLogger.Level.FINE))
            log.fine("AWTEvent.dispatched() got IllegalAccessException ", illegalAccessException); 
        }  
    } 
  }
  
  static  {
    Toolkit.loadLibraries();
    if (!GraphicsEnvironment.isHeadless())
      initIDs(); 
    AWTAccessor.setAWTEventAccessor(new AWTAccessor.AWTEventAccessor() {
          public void setPosted(AWTEvent param1AWTEvent) { param1AWTEvent.isPosted = true; }
          
          public void setSystemGenerated(AWTEvent param1AWTEvent) { param1AWTEvent.isSystemGenerated = true; }
          
          public boolean isSystemGenerated(AWTEvent param1AWTEvent) { return param1AWTEvent.isSystemGenerated; }
          
          public AccessControlContext getAccessControlContext(AWTEvent param1AWTEvent) { return param1AWTEvent.getAccessControlContext(); }
          
          public byte[] getBData(AWTEvent param1AWTEvent) { return param1AWTEvent.bdata; }
          
          public void setBData(AWTEvent param1AWTEvent, byte[] param1ArrayOfByte) { param1AWTEvent.bdata = param1ArrayOfByte; }
        });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\AWTEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */