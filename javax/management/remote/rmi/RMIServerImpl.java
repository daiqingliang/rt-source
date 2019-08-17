package javax.management.remote.rmi;

import com.sun.jmx.remote.internal.ArrayNotificationBuffer;
import com.sun.jmx.remote.internal.NotificationBuffer;
import com.sun.jmx.remote.security.JMXPluggableAuthenticator;
import com.sun.jmx.remote.util.ClassLogger;
import java.io.Closeable;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.rmi.Remote;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.management.MBeanServer;
import javax.management.remote.JMXAuthenticator;
import javax.security.auth.Subject;

public abstract class RMIServerImpl implements Closeable, RMIServer {
  private static final ClassLogger logger = new ClassLogger("javax.management.remote.rmi", "RMIServerImpl");
  
  private final List<WeakReference<RMIConnection>> clientList = new ArrayList();
  
  private ClassLoader cl;
  
  private MBeanServer mbeanServer;
  
  private final Map<String, ?> env;
  
  private RMIConnectorServer connServer;
  
  private static int connectionIdNumber;
  
  private NotificationBuffer notifBuffer;
  
  public RMIServerImpl(Map<String, ?> paramMap) { this.env = (paramMap == null) ? Collections.emptyMap() : paramMap; }
  
  void setRMIConnectorServer(RMIConnectorServer paramRMIConnectorServer) throws IOException { this.connServer = paramRMIConnectorServer; }
  
  protected abstract void export() throws IOException;
  
  public abstract Remote toStub() throws IOException;
  
  public void setDefaultClassLoader(ClassLoader paramClassLoader) { this.cl = paramClassLoader; }
  
  public ClassLoader getDefaultClassLoader() { return this.cl; }
  
  public void setMBeanServer(MBeanServer paramMBeanServer) { this.mbeanServer = paramMBeanServer; }
  
  public MBeanServer getMBeanServer() { return this.mbeanServer; }
  
  public String getVersion() {
    try {
      return "1.0 java_runtime_" + System.getProperty("java.runtime.version");
    } catch (SecurityException securityException) {
      return "1.0 ";
    } 
  }
  
  public RMIConnection newClient(Object paramObject) throws IOException { return doNewClient(paramObject); }
  
  RMIConnection doNewClient(Object paramObject) throws IOException {
    boolean bool = logger.traceOn();
    if (bool)
      logger.trace("newClient", "making new client"); 
    if (getMBeanServer() == null)
      throw new IllegalStateException("Not attached to an MBean server"); 
    Subject subject = null;
    JMXAuthenticator jMXAuthenticator = (JMXAuthenticator)this.env.get("jmx.remote.authenticator");
    if (jMXAuthenticator == null && (this.env.get("jmx.remote.x.password.file") != null || this.env.get("jmx.remote.x.login.config") != null))
      jMXAuthenticator = new JMXPluggableAuthenticator(this.env); 
    if (jMXAuthenticator != null) {
      if (bool)
        logger.trace("newClient", "got authenticator: " + jMXAuthenticator.getClass().getName()); 
      try {
        subject = jMXAuthenticator.authenticate(paramObject);
      } catch (SecurityException securityException) {
        logger.trace("newClient", "Authentication failed: " + securityException);
        throw securityException;
      } 
    } 
    if (bool)
      if (subject != null) {
        logger.trace("newClient", "subject is not null");
      } else {
        logger.trace("newClient", "no subject");
      }  
    String str = makeConnectionId(getProtocol(), subject);
    if (bool)
      logger.trace("newClient", "making new connection: " + str); 
    RMIConnection rMIConnection = makeClient(str, subject);
    dropDeadReferences();
    WeakReference weakReference = new WeakReference(rMIConnection);
    synchronized (this.clientList) {
      this.clientList.add(weakReference);
    } 
    this.connServer.connectionOpened(str, "Connection opened", null);
    synchronized (this.clientList) {
      if (!this.clientList.contains(weakReference))
        throw new IOException("The connection is refused."); 
    } 
    if (bool)
      logger.trace("newClient", "new connection done: " + str); 
    return rMIConnection;
  }
  
  protected abstract RMIConnection makeClient(String paramString, Subject paramSubject) throws IOException;
  
  protected abstract void closeClient(RMIConnection paramRMIConnection) throws IOException;
  
  protected abstract String getProtocol();
  
  protected void clientClosed(RMIConnection paramRMIConnection) throws IOException {
    boolean bool = logger.debugOn();
    if (bool)
      logger.trace("clientClosed", "client=" + paramRMIConnection); 
    if (paramRMIConnection == null)
      throw new NullPointerException("Null client"); 
    synchronized (this.clientList) {
      dropDeadReferences();
      Iterator iterator = this.clientList.iterator();
      while (iterator.hasNext()) {
        WeakReference weakReference = (WeakReference)iterator.next();
        if (weakReference.get() == paramRMIConnection) {
          iterator.remove();
          break;
        } 
      } 
    } 
    if (bool)
      logger.trace("clientClosed", "closing client."); 
    closeClient(paramRMIConnection);
    if (bool)
      logger.trace("clientClosed", "sending notif"); 
    this.connServer.connectionClosed(paramRMIConnection.getConnectionId(), "Client connection closed", null);
    if (bool)
      logger.trace("clientClosed", "done"); 
  }
  
  public void close() throws IOException {
    boolean bool1 = logger.traceOn();
    boolean bool2 = logger.debugOn();
    if (bool1)
      logger.trace("close", "closing"); 
    IOException iOException = null;
    try {
      if (bool2)
        logger.debug("close", "closing Server"); 
      closeServer();
    } catch (IOException iOException1) {
      if (bool1)
        logger.trace("close", "Failed to close server: " + iOException1); 
      if (bool2)
        logger.debug("close", iOException1); 
      iOException = iOException1;
    } 
    if (bool2)
      logger.debug("close", "closing Clients"); 
    while (true) {
      synchronized (this.clientList) {
        if (bool2)
          logger.debug("close", "droping dead references"); 
        dropDeadReferences();
        if (bool2)
          logger.debug("close", "client count: " + this.clientList.size()); 
        if (this.clientList.size() == 0)
          break; 
        Iterator iterator = this.clientList.iterator();
        while (true) {
          if (iterator.hasNext()) {
            WeakReference weakReference = (WeakReference)iterator.next();
            RMIConnection rMIConnection = (RMIConnection)weakReference.get();
            iterator.remove();
            if (rMIConnection != null) {
              try {
                rMIConnection.close();
                break;
              } catch (IOException iOException1) {
                if (bool1)
                  logger.trace("close", "Failed to close client: " + iOException1); 
                if (bool2)
                  logger.debug("close", iOException1); 
                if (iOException == null)
                  iOException = iOException1; 
              } 
            } else {
              continue;
            } 
          } else {
            break;
          } 
        } 
      } 
    } 
    if (this.notifBuffer != null)
      this.notifBuffer.dispose(); 
    if (iOException != null) {
      if (bool1)
        logger.trace("close", "close failed."); 
      throw iOException;
    } 
    if (bool1)
      logger.trace("close", "closed."); 
  }
  
  protected abstract void closeServer() throws IOException;
  
  private static String makeConnectionId(String paramString, Subject paramSubject) {
    connectionIdNumber++;
    String str = "";
    try {
      str = RemoteServer.getClientHost();
      if (str.contains(":"))
        str = "[" + str + "]"; 
    } catch (ServerNotActiveException serverNotActiveException) {
      logger.trace("makeConnectionId", "getClientHost", serverNotActiveException);
    } 
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(paramString).append(":");
    if (str.length() > 0)
      stringBuilder.append("//").append(str); 
    stringBuilder.append(" ");
    if (paramSubject != null) {
      Set set = paramSubject.getPrincipals();
      String str1 = "";
      for (Principal principal : set) {
        String str2 = principal.getName().replace(' ', '_').replace(';', ':');
        stringBuilder.append(str1).append(str2);
        str1 = ";";
      } 
    } 
    stringBuilder.append(" ").append(connectionIdNumber);
    if (logger.traceOn())
      logger.trace("newConnectionId", "connectionId=" + stringBuilder); 
    return stringBuilder.toString();
  }
  
  private void dropDeadReferences() throws IOException {
    synchronized (this.clientList) {
      Iterator iterator = this.clientList.iterator();
      while (iterator.hasNext()) {
        WeakReference weakReference = (WeakReference)iterator.next();
        if (weakReference.get() == null)
          iterator.remove(); 
      } 
    } 
  }
  
  NotificationBuffer getNotifBuffer() {
    if (this.notifBuffer == null)
      this.notifBuffer = ArrayNotificationBuffer.getNotificationBuffer(this.mbeanServer, this.env); 
    return this.notifBuffer;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\remote\rmi\RMIServerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */