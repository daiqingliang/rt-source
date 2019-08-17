package javax.naming.ldap;

import java.util.EventObject;

public class UnsolicitedNotificationEvent extends EventObject {
  private UnsolicitedNotification notice;
  
  private static final long serialVersionUID = -2382603380799883705L;
  
  public UnsolicitedNotificationEvent(Object paramObject, UnsolicitedNotification paramUnsolicitedNotification) {
    super(paramObject);
    this.notice = paramUnsolicitedNotification;
  }
  
  public UnsolicitedNotification getNotification() { return this.notice; }
  
  public void dispatch(UnsolicitedNotificationListener paramUnsolicitedNotificationListener) { paramUnsolicitedNotificationListener.notificationReceived(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\ldap\UnsolicitedNotificationEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */