package javax.management.timer;

import javax.management.Notification;

class TimerAlarmClockNotification extends Notification {
  private static final long serialVersionUID = -4841061275673620641L;
  
  public TimerAlarmClockNotification(TimerAlarmClock paramTimerAlarmClock) { super("", paramTimerAlarmClock, 0L); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\timer\TimerAlarmClockNotification.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */