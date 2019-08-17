package javax.management.remote;

import javax.management.Notification;

public class JMXConnectionNotification extends Notification {
  private static final long serialVersionUID = -2331308725952627538L;
  
  public static final String OPENED = "jmx.remote.connection.opened";
  
  public static final String CLOSED = "jmx.remote.connection.closed";
  
  public static final String FAILED = "jmx.remote.connection.failed";
  
  public static final String NOTIFS_LOST = "jmx.remote.connection.notifs.lost";
  
  private final String connectionId;
  
  public JMXConnectionNotification(String paramString1, Object paramObject1, String paramString2, long paramLong, String paramString3, Object paramObject2) {
    super((String)nonNull(paramString1), nonNull(paramObject1), Math.max(0L, paramLong), System.currentTimeMillis(), paramString3);
    if (paramString1 == null || paramObject1 == null || paramString2 == null)
      throw new NullPointerException("Illegal null argument"); 
    if (paramLong < 0L)
      throw new IllegalArgumentException("Negative sequence number"); 
    this.connectionId = paramString2;
    setUserData(paramObject2);
  }
  
  private static Object nonNull(Object paramObject) { return (paramObject == null) ? "" : paramObject; }
  
  public String getConnectionId() { return this.connectionId; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\remote\JMXConnectionNotification.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */