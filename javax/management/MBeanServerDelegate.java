package javax.management;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.mbeanserver.Util;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MBeanServerDelegate implements MBeanServerDelegateMBean, NotificationEmitter {
  private String mbeanServerId;
  
  private final NotificationBroadcasterSupport broadcaster = new NotificationBroadcasterSupport();
  
  private static long oldStamp = 0L;
  
  private final long stamp = getStamp();
  
  private long sequenceNumber = 1L;
  
  private static final MBeanNotificationInfo[] notifsInfo;
  
  public static final ObjectName DELEGATE_NAME;
  
  public String getMBeanServerId() {
    if (this.mbeanServerId == null) {
      String str;
      try {
        str = InetAddress.getLocalHost().getHostName();
      } catch (UnknownHostException unknownHostException) {
        JmxProperties.MISC_LOGGER.finest("Can't get local host name, using \"localhost\" instead. Cause is: " + unknownHostException);
        str = "localhost";
      } 
      this.mbeanServerId = str + "_" + this.stamp;
    } 
    return this.mbeanServerId;
  }
  
  public String getSpecificationName() { return "Java Management Extensions"; }
  
  public String getSpecificationVersion() { return "1.4"; }
  
  public String getSpecificationVendor() { return "Oracle Corporation"; }
  
  public String getImplementationName() { return "JMX"; }
  
  public String getImplementationVersion() {
    try {
      return System.getProperty("java.runtime.version");
    } catch (SecurityException securityException) {
      return "";
    } 
  }
  
  public String getImplementationVendor() { return "Oracle Corporation"; }
  
  public MBeanNotificationInfo[] getNotificationInfo() {
    int i = notifsInfo.length;
    MBeanNotificationInfo[] arrayOfMBeanNotificationInfo = new MBeanNotificationInfo[i];
    System.arraycopy(notifsInfo, 0, arrayOfMBeanNotificationInfo, 0, i);
    return arrayOfMBeanNotificationInfo;
  }
  
  public void addNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject) throws IllegalArgumentException { this.broadcaster.addNotificationListener(paramNotificationListener, paramNotificationFilter, paramObject); }
  
  public void removeNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject) throws IllegalArgumentException { this.broadcaster.removeNotificationListener(paramNotificationListener, paramNotificationFilter, paramObject); }
  
  public void removeNotificationListener(NotificationListener paramNotificationListener) throws ListenerNotFoundException { this.broadcaster.removeNotificationListener(paramNotificationListener); }
  
  public void sendNotification(Notification paramNotification) {
    if (paramNotification.getSequenceNumber() < 1L)
      synchronized (this) {
        paramNotification.setSequenceNumber(this.sequenceNumber++);
      }  
    this.broadcaster.sendNotification(paramNotification);
  }
  
  private static long getStamp() {
    long l = System.currentTimeMillis();
    if (oldStamp >= l)
      l = oldStamp + 1L; 
    oldStamp = l;
    return l;
  }
  
  static  {
    String[] arrayOfString = { "JMX.mbean.unregistered", "JMX.mbean.registered" };
    notifsInfo = new MBeanNotificationInfo[1];
    notifsInfo[0] = new MBeanNotificationInfo(arrayOfString, "javax.management.MBeanServerNotification", "Notifications sent by the MBeanServerDelegate MBean");
    DELEGATE_NAME = Util.newObjectName("JMImplementation:type=MBeanServerDelegate");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\MBeanServerDelegate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */