package javax.management.timer;

import java.util.Date;
import java.util.Vector;
import javax.management.InstanceNotFoundException;

public interface TimerMBean {
  void start();
  
  void stop();
  
  Integer addNotification(String paramString1, String paramString2, Object paramObject, Date paramDate, long paramLong1, long paramLong2, boolean paramBoolean) throws IllegalArgumentException;
  
  Integer addNotification(String paramString1, String paramString2, Object paramObject, Date paramDate, long paramLong1, long paramLong2) throws IllegalArgumentException;
  
  Integer addNotification(String paramString1, String paramString2, Object paramObject, Date paramDate, long paramLong) throws IllegalArgumentException;
  
  Integer addNotification(String paramString1, String paramString2, Object paramObject, Date paramDate) throws IllegalArgumentException;
  
  void removeNotification(Integer paramInteger) throws InstanceNotFoundException;
  
  void removeNotifications(String paramString) throws InstanceNotFoundException;
  
  void removeAllNotifications();
  
  int getNbNotifications();
  
  Vector<Integer> getAllNotificationIDs();
  
  Vector<Integer> getNotificationIDs(String paramString);
  
  String getNotificationType(Integer paramInteger);
  
  String getNotificationMessage(Integer paramInteger);
  
  Object getNotificationUserData(Integer paramInteger);
  
  Date getDate(Integer paramInteger);
  
  Long getPeriod(Integer paramInteger);
  
  Long getNbOccurences(Integer paramInteger);
  
  Boolean getFixedRate(Integer paramInteger);
  
  boolean getSendPastNotifications();
  
  void setSendPastNotifications(boolean paramBoolean);
  
  boolean isActive();
  
  boolean isEmpty();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\timer\TimerMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */