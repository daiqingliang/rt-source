package javax.management;

import java.util.EventListener;

public interface NotificationListener extends EventListener {
  void handleNotification(Notification paramNotification, Object paramObject);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\NotificationListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */