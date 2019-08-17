package sun.awt;

import java.awt.Component;
import java.awt.Panel;
import java.awt.Window;
import java.awt.peer.ComponentPeer;
import java.awt.peer.KeyboardFocusManagerPeer;
import sun.util.logging.PlatformLogger;

public abstract class KeyboardFocusManagerPeerImpl implements KeyboardFocusManagerPeer {
  private static final PlatformLogger focusLog = PlatformLogger.getLogger("sun.awt.focus.KeyboardFocusManagerPeerImpl");
  
  private static AWTAccessor.KeyboardFocusManagerAccessor kfmAccessor = AWTAccessor.getKeyboardFocusManagerAccessor();
  
  public static final int SNFH_FAILURE = 0;
  
  public static final int SNFH_SUCCESS_HANDLED = 1;
  
  public static final int SNFH_SUCCESS_PROCEED = 2;
  
  public void clearGlobalFocusOwner(Window paramWindow) {
    if (paramWindow != null) {
      Component component = paramWindow.getFocusOwner();
      if (focusLog.isLoggable(PlatformLogger.Level.FINE))
        focusLog.fine("Clearing global focus owner " + component); 
      if (component != null) {
        CausedFocusEvent causedFocusEvent = new CausedFocusEvent(component, 1005, false, null, CausedFocusEvent.Cause.CLEAR_GLOBAL_FOCUS_OWNER);
        SunToolkit.postPriorityEvent(causedFocusEvent);
      } 
    } 
  }
  
  public static boolean shouldFocusOnClick(Component paramComponent) {
    boolean bool = false;
    if (paramComponent instanceof java.awt.Canvas || paramComponent instanceof java.awt.Scrollbar) {
      bool = true;
    } else if (paramComponent instanceof Panel) {
      bool = (((Panel)paramComponent).getComponentCount() == 0);
    } else {
      ComponentPeer componentPeer = (paramComponent != null) ? paramComponent.getPeer() : null;
      bool = (componentPeer != null) ? componentPeer.isFocusable() : 0;
    } 
    return (bool && AWTAccessor.getComponentAccessor().canBeFocusOwner(paramComponent));
  }
  
  public static boolean deliverFocus(Component paramComponent1, Component paramComponent2, boolean paramBoolean1, boolean paramBoolean2, long paramLong, CausedFocusEvent.Cause paramCause, Component paramComponent3) {
    if (paramComponent1 == null)
      paramComponent1 = paramComponent2; 
    Component component = paramComponent3;
    if (component != null && component.getPeer() == null)
      component = null; 
    if (component != null) {
      CausedFocusEvent causedFocusEvent1 = new CausedFocusEvent(component, 1005, false, paramComponent1, paramCause);
      if (focusLog.isLoggable(PlatformLogger.Level.FINER))
        focusLog.finer("Posting focus event: " + causedFocusEvent1); 
      SunToolkit.postEvent(SunToolkit.targetToAppContext(component), causedFocusEvent1);
    } 
    CausedFocusEvent causedFocusEvent = new CausedFocusEvent(paramComponent1, 1004, false, component, paramCause);
    if (focusLog.isLoggable(PlatformLogger.Level.FINER))
      focusLog.finer("Posting focus event: " + causedFocusEvent); 
    SunToolkit.postEvent(SunToolkit.targetToAppContext(paramComponent1), causedFocusEvent);
    return true;
  }
  
  public static boolean requestFocusFor(Component paramComponent, CausedFocusEvent.Cause paramCause) { return AWTAccessor.getComponentAccessor().requestFocus(paramComponent, paramCause); }
  
  public static int shouldNativelyFocusHeavyweight(Component paramComponent1, Component paramComponent2, boolean paramBoolean1, boolean paramBoolean2, long paramLong, CausedFocusEvent.Cause paramCause) { return kfmAccessor.shouldNativelyFocusHeavyweight(paramComponent1, paramComponent2, paramBoolean1, paramBoolean2, paramLong, paramCause); }
  
  public static void removeLastFocusRequest(Component paramComponent) { kfmAccessor.removeLastFocusRequest(paramComponent); }
  
  public static boolean processSynchronousLightweightTransfer(Component paramComponent1, Component paramComponent2, boolean paramBoolean1, boolean paramBoolean2, long paramLong) { return kfmAccessor.processSynchronousLightweightTransfer(paramComponent1, paramComponent2, paramBoolean1, paramBoolean2, paramLong); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\KeyboardFocusManagerPeerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */