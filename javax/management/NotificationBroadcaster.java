package javax.management;

public interface NotificationBroadcaster {
  void addNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject) throws IllegalArgumentException;
  
  void removeNotificationListener(NotificationListener paramNotificationListener) throws ListenerNotFoundException;
  
  MBeanNotificationInfo[] getNotificationInfo();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\NotificationBroadcaster.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */