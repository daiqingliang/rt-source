package javax.management.timer;

import com.sun.jmx.defaults.JmxProperties;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TreeSet;
import java.util.Vector;
import java.util.logging.Level;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ObjectName;

public class Timer extends NotificationBroadcasterSupport implements TimerMBean, MBeanRegistration {
  public static final long ONE_SECOND = 1000L;
  
  public static final long ONE_MINUTE = 60000L;
  
  public static final long ONE_HOUR = 3600000L;
  
  public static final long ONE_DAY = 86400000L;
  
  public static final long ONE_WEEK = 604800000L;
  
  private final Map<Integer, Object[]> timerTable = new HashMap();
  
  private boolean sendPastNotifications = false;
  
  private boolean isActive = false;
  
  private long sequenceNumber = 0L;
  
  private static final int TIMER_NOTIF_INDEX = 0;
  
  private static final int TIMER_DATE_INDEX = 1;
  
  private static final int TIMER_PERIOD_INDEX = 2;
  
  private static final int TIMER_NB_OCCUR_INDEX = 3;
  
  private static final int ALARM_CLOCK_INDEX = 4;
  
  private static final int FIXED_RATE_INDEX = 5;
  
  private Timer timer;
  
  public ObjectName preRegister(MBeanServer paramMBeanServer, ObjectName paramObjectName) throws Exception { return paramObjectName; }
  
  public void postRegister(Boolean paramBoolean) {}
  
  public void preDeregister() {
    JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "preDeregister", "stop the timer");
    stop();
  }
  
  public void postDeregister() {}
  
  public MBeanNotificationInfo[] getNotificationInfo() {
    TreeSet treeSet = new TreeSet();
    for (Object[] arrayOfObject : this.timerTable.values()) {
      TimerNotification timerNotification = (TimerNotification)arrayOfObject[0];
      treeSet.add(timerNotification.getType());
    } 
    String[] arrayOfString = (String[])treeSet.toArray(new String[0]);
    return new MBeanNotificationInfo[] { new MBeanNotificationInfo(arrayOfString, TimerNotification.class.getName(), "Notification sent by Timer MBean") };
  }
  
  public void start() {
    JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "start", "starting the timer");
    if (!this.isActive) {
      this.timer = new Timer();
      Date date = new Date();
      sendPastNotifications(date, this.sendPastNotifications);
      for (Object[] arrayOfObject : this.timerTable.values()) {
        Date date1 = (Date)arrayOfObject[1];
        boolean bool = ((Boolean)arrayOfObject[5]).booleanValue();
        if (bool) {
          TimerAlarmClock timerAlarmClock1 = new TimerAlarmClock(this, date1);
          arrayOfObject[4] = timerAlarmClock1;
          this.timer.schedule(timerAlarmClock1, timerAlarmClock1.next);
          continue;
        } 
        TimerAlarmClock timerAlarmClock = new TimerAlarmClock(this, date1.getTime() - date.getTime());
        arrayOfObject[4] = timerAlarmClock;
        this.timer.schedule(timerAlarmClock, timerAlarmClock.timeout);
      } 
      this.isActive = true;
      JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "start", "timer started");
    } else {
      JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "start", "the timer is already activated");
    } 
  }
  
  public void stop() {
    JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "stop", "stopping the timer");
    if (this.isActive == true) {
      for (Object[] arrayOfObject : this.timerTable.values()) {
        TimerAlarmClock timerAlarmClock = (TimerAlarmClock)arrayOfObject[4];
        if (timerAlarmClock != null)
          timerAlarmClock.cancel(); 
      } 
      this.timer.cancel();
      this.isActive = false;
      JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "stop", "timer stopped");
    } else {
      JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "stop", "the timer is already deactivated");
    } 
  }
  
  public Integer addNotification(String paramString1, String paramString2, Object paramObject, Date paramDate, long paramLong1, long paramLong2, boolean paramBoolean) throws IllegalArgumentException {
    TimerAlarmClock timerAlarmClock;
    if (paramDate == null)
      throw new IllegalArgumentException("Timer notification date cannot be null."); 
    if (paramLong1 < 0L || paramLong2 < 0L)
      throw new IllegalArgumentException("Negative values for the periodicity"); 
    Date date1 = new Date();
    if (date1.after(paramDate)) {
      paramDate.setTime(date1.getTime());
      if (JmxProperties.TIMER_LOGGER.isLoggable(Level.FINER))
        JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "addNotification", "update timer notification to add with:\n\tNotification date = " + paramDate); 
    } 
    Integer integer = Integer.valueOf(++this.counterID);
    TimerNotification timerNotification = new TimerNotification(paramString1, this, 0L, 0L, paramString2, integer);
    timerNotification.setUserData(paramObject);
    Object[] arrayOfObject = new Object[6];
    if (paramBoolean) {
      timerAlarmClock = new TimerAlarmClock(this, paramDate);
    } else {
      timerAlarmClock = new TimerAlarmClock(this, paramDate.getTime() - date1.getTime());
    } 
    Date date2 = new Date(paramDate.getTime());
    arrayOfObject[0] = timerNotification;
    arrayOfObject[1] = date2;
    arrayOfObject[2] = Long.valueOf(paramLong1);
    arrayOfObject[3] = Long.valueOf(paramLong2);
    arrayOfObject[4] = timerAlarmClock;
    arrayOfObject[5] = Boolean.valueOf(paramBoolean);
    if (JmxProperties.TIMER_LOGGER.isLoggable(Level.FINER)) {
      StringBuilder stringBuilder = (new StringBuilder()).append("adding timer notification:\n\t").append("Notification source = ").append(timerNotification.getSource()).append("\n\tNotification type = ").append(timerNotification.getType()).append("\n\tNotification ID = ").append(integer).append("\n\tNotification date = ").append(date2).append("\n\tNotification period = ").append(paramLong1).append("\n\tNotification nb of occurrences = ").append(paramLong2).append("\n\tNotification executes at fixed rate = ").append(paramBoolean);
      JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "addNotification", stringBuilder.toString());
    } 
    this.timerTable.put(integer, arrayOfObject);
    if (this.isActive == true)
      if (paramBoolean) {
        this.timer.schedule(timerAlarmClock, timerAlarmClock.next);
      } else {
        this.timer.schedule(timerAlarmClock, timerAlarmClock.timeout);
      }  
    JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "addNotification", "timer notification added");
    return integer;
  }
  
  public Integer addNotification(String paramString1, String paramString2, Object paramObject, Date paramDate, long paramLong1, long paramLong2) throws IllegalArgumentException { return addNotification(paramString1, paramString2, paramObject, paramDate, paramLong1, paramLong2, false); }
  
  public Integer addNotification(String paramString1, String paramString2, Object paramObject, Date paramDate, long paramLong) throws IllegalArgumentException { return addNotification(paramString1, paramString2, paramObject, paramDate, paramLong, 0L); }
  
  public Integer addNotification(String paramString1, String paramString2, Object paramObject, Date paramDate) throws IllegalArgumentException { return addNotification(paramString1, paramString2, paramObject, paramDate, 0L, 0L); }
  
  public void removeNotification(Integer paramInteger) throws InstanceNotFoundException {
    if (!this.timerTable.containsKey(paramInteger))
      throw new InstanceNotFoundException("Timer notification to remove not in the list of notifications"); 
    Object[] arrayOfObject = (Object[])this.timerTable.get(paramInteger);
    TimerAlarmClock timerAlarmClock = (TimerAlarmClock)arrayOfObject[4];
    if (timerAlarmClock != null)
      timerAlarmClock.cancel(); 
    if (JmxProperties.TIMER_LOGGER.isLoggable(Level.FINER)) {
      StringBuilder stringBuilder = (new StringBuilder()).append("removing timer notification:").append("\n\tNotification source = ").append(((TimerNotification)arrayOfObject[0]).getSource()).append("\n\tNotification type = ").append(((TimerNotification)arrayOfObject[0]).getType()).append("\n\tNotification ID = ").append(((TimerNotification)arrayOfObject[0]).getNotificationID()).append("\n\tNotification date = ").append(arrayOfObject[1]).append("\n\tNotification period = ").append(arrayOfObject[2]).append("\n\tNotification nb of occurrences = ").append(arrayOfObject[3]).append("\n\tNotification executes at fixed rate = ").append(arrayOfObject[5]);
      JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "removeNotification", stringBuilder.toString());
    } 
    this.timerTable.remove(paramInteger);
    JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "removeNotification", "timer notification removed");
  }
  
  public void removeNotifications(String paramString) throws InstanceNotFoundException {
    Vector vector = getNotificationIDs(paramString);
    if (vector.isEmpty())
      throw new InstanceNotFoundException("Timer notifications to remove not in the list of notifications"); 
    for (Integer integer : vector)
      removeNotification(integer); 
  }
  
  public void removeAllNotifications() {
    for (Object[] arrayOfObject : this.timerTable.values()) {
      TimerAlarmClock timerAlarmClock = (TimerAlarmClock)arrayOfObject[4];
      timerAlarmClock.cancel();
    } 
    JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "removeAllNotifications", "removing all timer notifications");
    this.timerTable.clear();
    JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "removeAllNotifications", "all timer notifications removed");
    this.counterID = 0;
    JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "removeAllNotifications", "timer notification counter ID reset");
  }
  
  public int getNbNotifications() { return this.timerTable.size(); }
  
  public Vector<Integer> getAllNotificationIDs() { return new Vector(this.timerTable.keySet()); }
  
  public Vector<Integer> getNotificationIDs(String paramString) {
    Vector vector = new Vector();
    for (Map.Entry entry : this.timerTable.entrySet()) {
      Object[] arrayOfObject = (Object[])entry.getValue();
      String str = ((TimerNotification)arrayOfObject[0]).getType();
      if ((paramString == null) ? (str == null) : paramString.equals(str))
        vector.addElement(entry.getKey()); 
    } 
    return vector;
  }
  
  public String getNotificationType(Integer paramInteger) {
    Object[] arrayOfObject = (Object[])this.timerTable.get(paramInteger);
    return (arrayOfObject != null) ? ((TimerNotification)arrayOfObject[0]).getType() : null;
  }
  
  public String getNotificationMessage(Integer paramInteger) {
    Object[] arrayOfObject = (Object[])this.timerTable.get(paramInteger);
    return (arrayOfObject != null) ? ((TimerNotification)arrayOfObject[0]).getMessage() : null;
  }
  
  public Object getNotificationUserData(Integer paramInteger) {
    Object[] arrayOfObject = (Object[])this.timerTable.get(paramInteger);
    return (arrayOfObject != null) ? ((TimerNotification)arrayOfObject[0]).getUserData() : null;
  }
  
  public Date getDate(Integer paramInteger) {
    Object[] arrayOfObject = (Object[])this.timerTable.get(paramInteger);
    if (arrayOfObject != null) {
      Date date = (Date)arrayOfObject[1];
      return new Date(date.getTime());
    } 
    return null;
  }
  
  public Long getPeriod(Integer paramInteger) {
    Object[] arrayOfObject = (Object[])this.timerTable.get(paramInteger);
    return (arrayOfObject != null) ? (Long)arrayOfObject[2] : null;
  }
  
  public Long getNbOccurences(Integer paramInteger) {
    Object[] arrayOfObject = (Object[])this.timerTable.get(paramInteger);
    return (arrayOfObject != null) ? (Long)arrayOfObject[3] : null;
  }
  
  public Boolean getFixedRate(Integer paramInteger) {
    Object[] arrayOfObject = (Object[])this.timerTable.get(paramInteger);
    return (arrayOfObject != null) ? (bool = (Boolean)arrayOfObject[5]).valueOf(bool.booleanValue()) : null;
  }
  
  public boolean getSendPastNotifications() { return this.sendPastNotifications; }
  
  public void setSendPastNotifications(boolean paramBoolean) { this.sendPastNotifications = paramBoolean; }
  
  public boolean isActive() { return this.isActive; }
  
  public boolean isEmpty() { return this.timerTable.isEmpty(); }
  
  private void sendPastNotifications(Date paramDate, boolean paramBoolean) {
    ArrayList arrayList = new ArrayList(this.timerTable.values());
    for (Object[] arrayOfObject : arrayList) {
      TimerNotification timerNotification = (TimerNotification)arrayOfObject[0];
      Integer integer = timerNotification.getNotificationID();
      Date date = (Date)arrayOfObject[1];
      while (paramDate.after(date) && this.timerTable.containsKey(integer)) {
        if (paramBoolean == true) {
          if (JmxProperties.TIMER_LOGGER.isLoggable(Level.FINER)) {
            StringBuilder stringBuilder = (new StringBuilder()).append("sending past timer notification:").append("\n\tNotification source = ").append(timerNotification.getSource()).append("\n\tNotification type = ").append(timerNotification.getType()).append("\n\tNotification ID = ").append(timerNotification.getNotificationID()).append("\n\tNotification date = ").append(date).append("\n\tNotification period = ").append(arrayOfObject[2]).append("\n\tNotification nb of occurrences = ").append(arrayOfObject[3]).append("\n\tNotification executes at fixed rate = ").append(arrayOfObject[5]);
            JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "sendPastNotifications", stringBuilder.toString());
          } 
          sendNotification(date, timerNotification);
          JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "sendPastNotifications", "past timer notification sent");
        } 
        updateTimerTable(timerNotification.getNotificationID());
      } 
    } 
  }
  
  private void updateTimerTable(Integer paramInteger) throws InstanceNotFoundException {
    Object[] arrayOfObject = (Object[])this.timerTable.get(paramInteger);
    Date date = (Date)arrayOfObject[1];
    Long long1 = (Long)arrayOfObject[2];
    Long long2 = (Long)arrayOfObject[3];
    Boolean bool = (Boolean)arrayOfObject[5];
    TimerAlarmClock timerAlarmClock = (TimerAlarmClock)arrayOfObject[4];
    if (long1.longValue() != 0L) {
      if (long2.longValue() == 0L || long2.longValue() > 1L) {
        date.setTime(date.getTime() + long1.longValue());
        arrayOfObject[3] = Long.valueOf(Math.max(0L, long2.longValue() - 1L));
        long2 = (Long)arrayOfObject[3];
        if (this.isActive == true)
          if (bool.booleanValue()) {
            timerAlarmClock = new TimerAlarmClock(this, date);
            arrayOfObject[4] = timerAlarmClock;
            this.timer.schedule(timerAlarmClock, timerAlarmClock.next);
          } else {
            timerAlarmClock = new TimerAlarmClock(this, long1.longValue());
            arrayOfObject[4] = timerAlarmClock;
            this.timer.schedule(timerAlarmClock, timerAlarmClock.timeout);
          }  
        if (JmxProperties.TIMER_LOGGER.isLoggable(Level.FINER)) {
          TimerNotification timerNotification = (TimerNotification)arrayOfObject[0];
          StringBuilder stringBuilder = (new StringBuilder()).append("update timer notification with:").append("\n\tNotification source = ").append(timerNotification.getSource()).append("\n\tNotification type = ").append(timerNotification.getType()).append("\n\tNotification ID = ").append(paramInteger).append("\n\tNotification date = ").append(date).append("\n\tNotification period = ").append(long1).append("\n\tNotification nb of occurrences = ").append(long2).append("\n\tNotification executes at fixed rate = ").append(bool);
          JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "updateTimerTable", stringBuilder.toString());
        } 
      } else {
        if (timerAlarmClock != null)
          timerAlarmClock.cancel(); 
        this.timerTable.remove(paramInteger);
      } 
    } else {
      if (timerAlarmClock != null)
        timerAlarmClock.cancel(); 
      this.timerTable.remove(paramInteger);
    } 
  }
  
  void notifyAlarmClock(TimerAlarmClockNotification paramTimerAlarmClockNotification) {
    TimerNotification timerNotification = null;
    Date date = null;
    TimerAlarmClock timerAlarmClock = (TimerAlarmClock)paramTimerAlarmClockNotification.getSource();
    synchronized (this) {
      for (Object[] arrayOfObject : this.timerTable.values()) {
        if (arrayOfObject[4] == timerAlarmClock) {
          timerNotification = (TimerNotification)arrayOfObject[0];
          date = (Date)arrayOfObject[1];
          break;
        } 
      } 
    } 
    sendNotification(date, timerNotification);
    updateTimerTable(timerNotification.getNotificationID());
  }
  
  void sendNotification(Date paramDate, TimerNotification paramTimerNotification) {
    long l;
    if (JmxProperties.TIMER_LOGGER.isLoggable(Level.FINER)) {
      StringBuilder stringBuilder = (new StringBuilder()).append("sending timer notification:").append("\n\tNotification source = ").append(paramTimerNotification.getSource()).append("\n\tNotification type = ").append(paramTimerNotification.getType()).append("\n\tNotification ID = ").append(paramTimerNotification.getNotificationID()).append("\n\tNotification date = ").append(paramDate);
      JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "sendNotification", stringBuilder.toString());
    } 
    synchronized (this) {
      l = ++this.sequenceNumber;
    } 
    synchronized (paramTimerNotification) {
      paramTimerNotification.setTimeStamp(paramDate.getTime());
      paramTimerNotification.setSequenceNumber(l);
      sendNotification((TimerNotification)paramTimerNotification.cloneTimerNotification());
    } 
    JmxProperties.TIMER_LOGGER.logp(Level.FINER, Timer.class.getName(), "sendNotification", "timer notification sent");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\timer\Timer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */