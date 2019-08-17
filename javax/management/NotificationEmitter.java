package javax.management;

public interface NotificationEmitter extends NotificationBroadcaster {
  void removeNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject) throws ListenerNotFoundException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\NotificationEmitter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */