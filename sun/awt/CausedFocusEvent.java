package sun.awt;

import java.awt.Component;
import java.awt.event.FocusEvent;

public class CausedFocusEvent extends FocusEvent {
  private final Cause cause;
  
  public Cause getCause() { return this.cause; }
  
  public String toString() { return "java.awt.FocusEvent[" + paramString() + ",cause=" + this.cause + "] on " + getSource(); }
  
  public CausedFocusEvent(Component paramComponent1, int paramInt, boolean paramBoolean, Component paramComponent2, Cause paramCause) {
    super(paramComponent1, paramInt, paramBoolean, paramComponent2);
    if (paramCause == null)
      paramCause = Cause.UNKNOWN; 
    this.cause = paramCause;
  }
  
  public static FocusEvent retarget(FocusEvent paramFocusEvent, Component paramComponent) { return (paramFocusEvent == null) ? null : new CausedFocusEvent(paramComponent, paramFocusEvent.getID(), paramFocusEvent.isTemporary(), paramFocusEvent.getOppositeComponent(), (paramFocusEvent instanceof CausedFocusEvent) ? ((CausedFocusEvent)paramFocusEvent).getCause() : Cause.RETARGETED); }
  
  public enum Cause {
    UNKNOWN, MOUSE_EVENT, TRAVERSAL, TRAVERSAL_UP, TRAVERSAL_DOWN, TRAVERSAL_FORWARD, TRAVERSAL_BACKWARD, MANUAL_REQUEST, AUTOMATIC_TRAVERSE, ROLLBACK, NATIVE_SYSTEM, ACTIVATION, CLEAR_GLOBAL_FOCUS_OWNER, RETARGETED;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\CausedFocusEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */