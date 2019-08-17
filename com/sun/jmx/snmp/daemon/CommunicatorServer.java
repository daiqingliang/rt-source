package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.logging.Level;
import javax.management.AttributeChangeNotification;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.remote.MBeanServerForwarder;

public abstract class CommunicatorServer implements Runnable, MBeanRegistration, NotificationBroadcaster, CommunicatorServerMBean {
  public static final int ONLINE = 0;
  
  public static final int OFFLINE = 1;
  
  public static final int STOPPING = 2;
  
  public static final int STARTING = 3;
  
  public static final int SNMP_TYPE = 4;
  
  ObjectName objectName;
  
  MBeanServer topMBS;
  
  MBeanServer bottomMBS;
  
  String dbgTag = null;
  
  int maxActiveClientCount = 1;
  
  int servedClientCount = 0;
  
  String host = null;
  
  int port = -1;
  
  private Object stateLock = new Object();
  
  private Vector<ClientHandler> clientHandlerVector = new Vector();
  
  private Thread mainThread = null;
  
  private boolean interrupted = false;
  
  private Exception startException = null;
  
  private long notifCount = 0L;
  
  private NotificationBroadcasterSupport notifBroadcaster = new NotificationBroadcasterSupport();
  
  private MBeanNotificationInfo[] notifInfos = null;
  
  public CommunicatorServer(int paramInt) throws IllegalArgumentException {
    switch (paramInt) {
      case 4:
        break;
      default:
        throw new IllegalArgumentException("Invalid connector Type");
    } 
    this.dbgTag = makeDebugTag();
  }
  
  protected Thread createMainThread() { return new Thread(this, makeThreadName()); }
  
  public void start(long paramLong) throws CommunicationException, InterruptedException {
    boolean bool;
    synchronized (this.stateLock) {
      if (this.state == 2)
        waitState(1, 60000L); 
      bool = (this.state == 1) ? 1 : 0;
      if (bool) {
        changeState(3);
        this.stopRequested = false;
        this.interrupted = false;
        this.startException = null;
      } 
    } 
    if (!bool) {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "start", "Connector is not OFFLINE"); 
      return;
    } 
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "start", "--> Start connector "); 
    this.mainThread = createMainThread();
    this.mainThread.start();
    if (paramLong > 0L)
      waitForStart(paramLong); 
  }
  
  public void start() {
    try {
      start(0L);
    } catch (InterruptedException interruptedException) {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "start", "interrupted", interruptedException); 
    } 
  }
  
  public void stop() {
    synchronized (this.stateLock) {
      if (this.state == 1 || this.state == 2) {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "stop", "Connector is not ONLINE"); 
        return;
      } 
      changeState(2);
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "stop", "Interrupt main thread"); 
      this.stopRequested = true;
      if (!this.interrupted) {
        this.interrupted = true;
        this.mainThread.interrupt();
      } 
    } 
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "stop", "terminateAllClient"); 
    terminateAllClient();
    synchronized (this.stateLock) {
      if (this.state == 3)
        changeState(1); 
    } 
  }
  
  public boolean isActive() {
    synchronized (this.stateLock) {
      return (this.state == 0);
    } 
  }
  
  public boolean waitState(int paramInt, long paramLong) {
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "waitState", paramInt + "(0on,1off,2st) TO=" + paramLong + " ; current state = " + getStateString()); 
    long l = 0L;
    if (paramLong > 0L)
      l = System.currentTimeMillis() + paramLong; 
    synchronized (this.stateLock) {
      while (this.state != paramInt) {
        if (paramLong < 0L) {
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "waitState", "timeOut < 0, return without wait"); 
          return false;
        } 
        try {
          if (paramLong > 0L) {
            long l1 = l - System.currentTimeMillis();
            if (l1 <= 0L) {
              if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
                JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "waitState", "timed out"); 
              return false;
            } 
            this.stateLock.wait(l1);
            continue;
          } 
          this.stateLock.wait();
        } catch (InterruptedException interruptedException) {
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "waitState", "wait interrupted"); 
          return (this.state == paramInt);
        } 
      } 
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "waitState", "returning in desired state"); 
      return true;
    } 
  }
  
  private void waitForStart(long paramLong) throws CommunicationException, InterruptedException {
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "waitForStart", "Timeout=" + paramLong + " ; current state = " + getStateString()); 
    long l = System.currentTimeMillis();
    synchronized (this.stateLock) {
      while (this.state == 3) {
        long l1 = System.currentTimeMillis() - l;
        long l2 = paramLong - l1;
        if (l2 < 0L) {
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "waitForStart", "timeout < 0, return without wait"); 
          throw new InterruptedException("Timeout expired");
        } 
        try {
          this.stateLock.wait(l2);
        } catch (InterruptedException interruptedException) {
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "waitForStart", "wait interrupted"); 
          if (this.state != 0)
            throw interruptedException; 
        } 
      } 
      if (this.state == 0) {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "waitForStart", "started"); 
        return;
      } 
      if (this.startException instanceof CommunicationException)
        throw (CommunicationException)this.startException; 
      if (this.startException instanceof InterruptedException)
        throw (InterruptedException)this.startException; 
      if (this.startException != null)
        throw new CommunicationException(this.startException, "Failed to start: " + this.startException); 
      throw new CommunicationException("Failed to start: state is " + getStringForState(this.state));
    } 
  }
  
  public int getState() {
    synchronized (this.stateLock) {
      return this.state;
    } 
  }
  
  public String getStateString() { return getStringForState(this.state); }
  
  public String getHost() {
    try {
      this.host = InetAddress.getLocalHost().getHostName();
    } catch (Exception exception) {
      this.host = "Unknown host";
    } 
    return this.host;
  }
  
  public int getPort() {
    synchronized (this.stateLock) {
      return this.port;
    } 
  }
  
  public void setPort(int paramInt) throws IllegalArgumentException {
    synchronized (this.stateLock) {
      if (this.state == 0 || this.state == 3)
        throw new IllegalStateException("Stop server before carrying out this operation"); 
      this.port = paramInt;
      this.dbgTag = makeDebugTag();
    } 
  }
  
  public abstract String getProtocol();
  
  int getServedClientCount() { return this.servedClientCount; }
  
  int getActiveClientCount() { return this.clientHandlerVector.size(); }
  
  int getMaxActiveClientCount() { return this.maxActiveClientCount; }
  
  void setMaxActiveClientCount(int paramInt) throws IllegalArgumentException {
    synchronized (this.stateLock) {
      if (this.state == 0 || this.state == 3)
        throw new IllegalStateException("Stop server before carrying out this operation"); 
      this.maxActiveClientCount = paramInt;
    } 
  }
  
  void notifyClientHandlerCreated(ClientHandler paramClientHandler) { this.clientHandlerVector.addElement(paramClientHandler); }
  
  void notifyClientHandlerDeleted(ClientHandler paramClientHandler) {
    this.clientHandlerVector.removeElement(paramClientHandler);
    notifyAll();
  }
  
  protected int getBindTries() { return 50; }
  
  protected long getBindSleepTime() { return 100L; }
  
  public void run() {
    byte b = 0;
    boolean bool = false;
    try {
      int i = getBindTries();
      long l = getBindSleepTime();
      while (b < i && !bool) {
        try {
          doBind();
          bool = true;
        } catch (CommunicationException communicationException) {
          b++;
          try {
            Thread.sleep(l);
          } catch (InterruptedException interruptedException) {
            throw interruptedException;
          } 
        } 
      } 
      if (!bool)
        doBind(); 
    } catch (Exception exception) {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "run", "Got unexpected exception", exception); 
      synchronized (this.stateLock) {
        this.startException = exception;
        changeState(1);
      } 
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "run", "State is OFFLINE"); 
      doError(exception);
      return;
    } 
    try {
      changeState(0);
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "run", "State is ONLINE"); 
      while (!this.stopRequested) {
        this.servedClientCount++;
        doReceive();
        waitIfTooManyClients();
        doProcess();
      } 
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "run", "Stop has been requested"); 
    } catch (InterruptedException interruptedException) {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "run", "Interrupt caught"); 
      changeState(2);
    } catch (Exception exception) {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "run", "Got unexpected exception", exception); 
      changeState(2);
    } finally {
      synchronized (this.stateLock) {
        this.interrupted = true;
        Thread.interrupted();
      } 
      try {
        doUnbind();
        waitClientTermination();
        changeState(1);
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "run", "State is OFFLINE"); 
      } catch (Exception exception) {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "run", "Got unexpected exception", exception); 
        changeState(1);
      } 
    } 
  }
  
  protected abstract void doError(Exception paramException) throws CommunicationException;
  
  protected abstract void doBind();
  
  protected abstract void doReceive();
  
  protected abstract void doProcess();
  
  protected abstract void doUnbind();
  
  public MBeanServer getMBeanServer() { return this.topMBS; }
  
  public void setMBeanServer(MBeanServer paramMBeanServer) throws IllegalArgumentException, IllegalStateException {
    synchronized (this.stateLock) {
      if (this.state == 0 || this.state == 3)
        throw new IllegalStateException("Stop server before carrying out this operation"); 
    } 
    Vector vector = new Vector();
    for (MBeanServer mBeanServer = paramMBeanServer; mBeanServer != this.bottomMBS; mBeanServer = ((MBeanServerForwarder)mBeanServer).getMBeanServer()) {
      if (!(mBeanServer instanceof MBeanServerForwarder))
        throw new IllegalArgumentException("MBeanServer argument must be MBean server where this server is registered, or an MBeanServerForwarder leading to that server"); 
      if (vector.contains(mBeanServer))
        throw new IllegalArgumentException("MBeanServerForwarder loop"); 
      vector.addElement(mBeanServer);
    } 
    this.topMBS = paramMBeanServer;
  }
  
  ObjectName getObjectName() { return this.objectName; }
  
  void changeState(int paramInt) throws IllegalArgumentException {
    int i;
    synchronized (this.stateLock) {
      if (this.state == paramInt)
        return; 
      i = this.state;
      this.state = paramInt;
      this.stateLock.notifyAll();
    } 
    sendStateChangeNotification(i, paramInt);
  }
  
  String makeDebugTag() { return "CommunicatorServer[" + getProtocol() + ":" + getPort() + "]"; }
  
  String makeThreadName() {
    String str;
    if (this.objectName == null) {
      str = "CommunicatorServer";
    } else {
      str = this.objectName.toString();
    } 
    return str;
  }
  
  private void waitIfTooManyClients() {
    while (getActiveClientCount() >= this.maxActiveClientCount) {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "waitIfTooManyClients", "Waiting for a client to terminate"); 
      wait();
    } 
  }
  
  private void waitClientTermination() {
    int i = this.clientHandlerVector.size();
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER) && i >= 1)
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "waitClientTermination", "waiting for " + i + " clients to terminate"); 
    while (!this.clientHandlerVector.isEmpty()) {
      try {
        ((ClientHandler)this.clientHandlerVector.firstElement()).join();
      } catch (NoSuchElementException noSuchElementException) {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "waitClientTermination", "No elements left", noSuchElementException); 
      } 
    } 
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER) && i >= 1)
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "waitClientTermination", "Ok, let's go..."); 
  }
  
  private void terminateAllClient() {
    int i = this.clientHandlerVector.size();
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER) && i >= 1)
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "terminateAllClient", "Interrupting " + i + " clients"); 
    ClientHandler[] arrayOfClientHandler = (ClientHandler[])this.clientHandlerVector.toArray(new ClientHandler[0]);
    for (ClientHandler clientHandler : arrayOfClientHandler) {
      try {
        clientHandler.interrupt();
      } catch (Exception exception) {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "terminateAllClient", "Failed to interrupt pending request. Ignore the exception.", exception); 
      } 
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    this.stateLock = new Object();
    this.state = 1;
    this.stopRequested = false;
    this.servedClientCount = 0;
    this.clientHandlerVector = new Vector();
    this.mainThread = null;
    this.notifCount = 0L;
    this.notifInfos = null;
    this.notifBroadcaster = new NotificationBroadcasterSupport();
    this.dbgTag = makeDebugTag();
  }
  
  public void addNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject) throws IllegalArgumentException {
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "addNotificationListener", "Adding listener " + paramNotificationListener + " with filter " + paramNotificationFilter + " and handback " + paramObject); 
    this.notifBroadcaster.addNotificationListener(paramNotificationListener, paramNotificationFilter, paramObject);
  }
  
  public void removeNotificationListener(NotificationListener paramNotificationListener) throws ListenerNotFoundException {
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "removeNotificationListener", "Removing listener " + paramNotificationListener); 
    this.notifBroadcaster.removeNotificationListener(paramNotificationListener);
  }
  
  public MBeanNotificationInfo[] getNotificationInfo() {
    if (this.notifInfos == null) {
      this.notifInfos = new MBeanNotificationInfo[1];
      String[] arrayOfString = { "jmx.attribute.change" };
      this.notifInfos[0] = new MBeanNotificationInfo(arrayOfString, AttributeChangeNotification.class.getName(), "Sent to notify that the value of the State attribute of this CommunicatorServer instance has changed.");
    } 
    return (MBeanNotificationInfo[])this.notifInfos.clone();
  }
  
  private void sendStateChangeNotification(int paramInt1, int paramInt2) {
    String str1 = getStringForState(paramInt1);
    String str2 = getStringForState(paramInt2);
    String str3 = this.dbgTag + " The value of attribute State has changed from " + paramInt1 + " (" + str1 + ") to " + paramInt2 + " (" + str2 + ").";
    this.notifCount++;
    AttributeChangeNotification attributeChangeNotification = new AttributeChangeNotification(this, this.notifCount, System.currentTimeMillis(), str3, "State", "int", new Integer(paramInt1), new Integer(paramInt2));
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "sendStateChangeNotification", "Sending AttributeChangeNotification #" + this.notifCount + " with message: " + str3); 
    this.notifBroadcaster.sendNotification(attributeChangeNotification);
  }
  
  private static String getStringForState(int paramInt) {
    switch (paramInt) {
      case 0:
        return "ONLINE";
      case 3:
        return "STARTING";
      case 1:
        return "OFFLINE";
      case 2:
        return "STOPPING";
    } 
    return "UNDEFINED";
  }
  
  public ObjectName preRegister(MBeanServer paramMBeanServer, ObjectName paramObjectName) throws Exception {
    this.objectName = paramObjectName;
    synchronized (this) {
      if (this.bottomMBS != null)
        throw new IllegalArgumentException("connector already registered in an MBean server"); 
      this.topMBS = this.bottomMBS = paramMBeanServer;
    } 
    this.dbgTag = makeDebugTag();
    return paramObjectName;
  }
  
  public void postRegister(Boolean paramBoolean) {
    if (!paramBoolean.booleanValue())
      synchronized (this) {
        this.topMBS = this.bottomMBS = null;
      }  
  }
  
  public void preDeregister() {
    synchronized (this) {
      this.topMBS = this.bottomMBS = null;
    } 
    this.objectName = null;
    int i = getState();
    if (i == 0 || i == 3)
      stop(); 
  }
  
  public void postDeregister() {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\daemon\CommunicatorServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */