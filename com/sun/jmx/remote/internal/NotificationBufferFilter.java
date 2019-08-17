package com.sun.jmx.remote.internal;

import java.util.List;
import javax.management.Notification;
import javax.management.ObjectName;
import javax.management.remote.TargetedNotification;

public interface NotificationBufferFilter {
  void apply(List<TargetedNotification> paramList, ObjectName paramObjectName, Notification paramNotification);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\remote\internal\NotificationBufferFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */