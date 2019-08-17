package javax.management.timer;

import javax.management.Notification;

public class TimerNotification extends Notification {
  private static final long serialVersionUID = 1798492029603825750L;
  
  private Integer notificationID;
  
  public TimerNotification(String paramString1, Object paramObject, long paramLong1, long paramLong2, String paramString2, Integer paramInteger) {
    super(paramString1, paramObject, paramLong1, paramLong2, paramString2);
    this.notificationID = paramInteger;
  }
  
  public Integer getNotificationID() { return this.notificationID; }
  
  Object cloneTimerNotification() {
    TimerNotification timerNotification = new TimerNotification(getType(), getSource(), getSequenceNumber(), getTimeStamp(), getMessage(), this.notificationID);
    timerNotification.setUserData(getUserData());
    return timerNotification;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\timer\TimerNotification.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */