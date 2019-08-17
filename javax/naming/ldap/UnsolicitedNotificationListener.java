package javax.naming.ldap;

import javax.naming.event.NamingListener;

public interface UnsolicitedNotificationListener extends NamingListener {
  void notificationReceived(UnsolicitedNotificationEvent paramUnsolicitedNotificationEvent);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\ldap\UnsolicitedNotificationListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */