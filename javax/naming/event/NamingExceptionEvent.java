package javax.naming.event;

import java.util.EventObject;
import javax.naming.NamingException;

public class NamingExceptionEvent extends EventObject {
  private NamingException exception;
  
  private static final long serialVersionUID = -4877678086134736336L;
  
  public NamingExceptionEvent(EventContext paramEventContext, NamingException paramNamingException) {
    super(paramEventContext);
    this.exception = paramNamingException;
  }
  
  public NamingException getException() { return this.exception; }
  
  public EventContext getEventContext() { return (EventContext)getSource(); }
  
  public void dispatch(NamingListener paramNamingListener) { paramNamingListener.namingExceptionThrown(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\event\NamingExceptionEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */