package javax.management;

import java.io.Serializable;

public interface NotificationFilter extends Serializable {
  boolean isNotificationEnabled(Notification paramNotification);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\NotificationFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */