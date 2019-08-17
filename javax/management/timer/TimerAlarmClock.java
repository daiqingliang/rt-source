package javax.management.timer;

import com.sun.jmx.defaults.JmxProperties;
import java.util.Date;
import java.util.TimerTask;
import java.util.logging.Level;

class TimerAlarmClock extends TimerTask {
  Timer listener = null;
  
  long timeout = 10000L;
  
  Date next = null;
  
  public TimerAlarmClock(Timer paramTimer, long paramLong) {
    this.listener = paramTimer;
    this.timeout = Math.max(0L, paramLong);
  }
  
  public TimerAlarmClock(Timer paramTimer, Date paramDate) {
    this.listener = paramTimer;
    this.next = paramDate;
  }
  
  public void run() {
    try {
      TimerAlarmClockNotification timerAlarmClockNotification = new TimerAlarmClockNotification(this);
      this.listener.notifyAlarmClock(timerAlarmClockNotification);
    } catch (Exception exception) {
      JmxProperties.TIMER_LOGGER.logp(Level.FINEST, Timer.class.getName(), "run", "Got unexpected exception when sending a notification", exception);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\timer\TimerAlarmClock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */