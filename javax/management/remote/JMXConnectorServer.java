package javax.management.remote;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ObjectName;

public abstract class JMXConnectorServer extends NotificationBroadcasterSupport implements JMXConnectorServerMBean, MBeanRegistration, JMXAddressable {
  public static final String AUTHENTICATOR = "jmx.remote.authenticator";
  
  private MBeanServer mbeanServer = null;
  
  private ObjectName myName;
  
  private final List<String> connectionIds = new ArrayList();
  
  private static final int[] sequenceNumberLock = new int[0];
  
  private static long sequenceNumber;
  
  public JMXConnectorServer() { this(null); }
  
  public JMXConnectorServer(MBeanServer paramMBeanServer) { this.mbeanServer = paramMBeanServer; }
  
  public MBeanServer getMBeanServer() { return this.mbeanServer; }
  
  public void setMBeanServerForwarder(MBeanServerForwarder paramMBeanServerForwarder) {
    if (paramMBeanServerForwarder == null)
      throw new IllegalArgumentException("Invalid null argument: mbsf"); 
    if (this.mbeanServer != null)
      paramMBeanServerForwarder.setMBeanServer(this.mbeanServer); 
    this.mbeanServer = paramMBeanServerForwarder;
  }
  
  public String[] getConnectionIds() {
    synchronized (this.connectionIds) {
      return (String[])this.connectionIds.toArray(new String[this.connectionIds.size()]);
    } 
  }
  
  public JMXConnector toJMXConnector(Map<String, ?> paramMap) throws IOException {
    if (!isActive())
      throw new IllegalStateException("Connector is not active"); 
    JMXServiceURL jMXServiceURL = getAddress();
    return JMXConnectorFactory.newJMXConnector(jMXServiceURL, paramMap);
  }
  
  public MBeanNotificationInfo[] getNotificationInfo() {
    String[] arrayOfString = { "jmx.remote.connection.opened", "jmx.remote.connection.closed", "jmx.remote.connection.failed" };
    String str = JMXConnectionNotification.class.getName();
    return new MBeanNotificationInfo[] { new MBeanNotificationInfo(arrayOfString, str, "A client connection has been opened or closed") };
  }
  
  protected void connectionOpened(String paramString1, String paramString2, Object paramObject) {
    if (paramString1 == null)
      throw new NullPointerException("Illegal null argument"); 
    synchronized (this.connectionIds) {
      this.connectionIds.add(paramString1);
    } 
    sendNotification("jmx.remote.connection.opened", paramString1, paramString2, paramObject);
  }
  
  protected void connectionClosed(String paramString1, String paramString2, Object paramObject) {
    if (paramString1 == null)
      throw new NullPointerException("Illegal null argument"); 
    synchronized (this.connectionIds) {
      this.connectionIds.remove(paramString1);
    } 
    sendNotification("jmx.remote.connection.closed", paramString1, paramString2, paramObject);
  }
  
  protected void connectionFailed(String paramString1, String paramString2, Object paramObject) {
    if (paramString1 == null)
      throw new NullPointerException("Illegal null argument"); 
    synchronized (this.connectionIds) {
      this.connectionIds.remove(paramString1);
    } 
    sendNotification("jmx.remote.connection.failed", paramString1, paramString2, paramObject);
  }
  
  private void sendNotification(String paramString1, String paramString2, String paramString3, Object paramObject) {
    JMXConnectionNotification jMXConnectionNotification = new JMXConnectionNotification(paramString1, getNotificationSource(), paramString2, nextSequenceNumber(), paramString3, paramObject);
    sendNotification(jMXConnectionNotification);
  }
  
  private Object getNotificationSource() { return (this.myName != null) ? this.myName : this; }
  
  private static long nextSequenceNumber() {
    synchronized (sequenceNumberLock) {
      return sequenceNumber++;
    } 
  }
  
  public ObjectName preRegister(MBeanServer paramMBeanServer, ObjectName paramObjectName) {
    if (paramMBeanServer == null || paramObjectName == null)
      throw new NullPointerException("Null MBeanServer or ObjectName"); 
    if (this.mbeanServer == null) {
      this.mbeanServer = paramMBeanServer;
      this.myName = paramObjectName;
    } 
    return paramObjectName;
  }
  
  public void postRegister(Boolean paramBoolean) {}
  
  public void preDeregister() {
    if (this.myName != null && isActive()) {
      stop();
      this.myName = null;
    } 
  }
  
  public void postDeregister() { this.myName = null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\remote\JMXConnectorServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */