package javax.management.modelmbean;

import javax.management.Attribute;
import javax.management.AttributeChangeNotification;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.Notification;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationListener;
import javax.management.RuntimeOperationsException;

public interface ModelMBeanNotificationBroadcaster extends NotificationBroadcaster {
  void sendNotification(Notification paramNotification) throws MBeanException, RuntimeOperationsException;
  
  void sendNotification(String paramString) throws MBeanException, RuntimeOperationsException;
  
  void sendAttributeChangeNotification(AttributeChangeNotification paramAttributeChangeNotification) throws MBeanException, RuntimeOperationsException;
  
  void sendAttributeChangeNotification(Attribute paramAttribute1, Attribute paramAttribute2) throws MBeanException, RuntimeOperationsException;
  
  void addAttributeChangeNotificationListener(NotificationListener paramNotificationListener, String paramString, Object paramObject) throws MBeanException, RuntimeOperationsException, IllegalArgumentException;
  
  void removeAttributeChangeNotificationListener(NotificationListener paramNotificationListener, String paramString) throws MBeanException, RuntimeOperationsException, ListenerNotFoundException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\modelmbean\ModelMBeanNotificationBroadcaster.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */