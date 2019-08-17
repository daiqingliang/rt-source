package javax.management.remote.rmi;

import com.sun.jmx.remote.internal.IIOPHelper;
import com.sun.jmx.remote.security.MBeanServerFileAccessController;
import com.sun.jmx.remote.util.ClassLogger;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.MBeanServerForwarder;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class RMIConnectorServer extends JMXConnectorServer {
  public static final String JNDI_REBIND_ATTRIBUTE = "jmx.remote.jndi.rebind";
  
  public static final String RMI_CLIENT_SOCKET_FACTORY_ATTRIBUTE = "jmx.remote.rmi.client.socket.factory";
  
  public static final String RMI_SERVER_SOCKET_FACTORY_ATTRIBUTE = "jmx.remote.rmi.server.socket.factory";
  
  private static final char[] intToAlpha = { 
      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 
      'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 
      'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 
      'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 
      'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 
      'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', 
      '8', '9', '+', '/' };
  
  private static ClassLogger logger = new ClassLogger("javax.management.remote.rmi", "RMIConnectorServer");
  
  private JMXServiceURL address;
  
  private RMIServerImpl rmiServerImpl;
  
  private final Map<String, ?> attributes;
  
  private ClassLoader defaultClassLoader = null;
  
  private String boundJndiUrl;
  
  private static final int CREATED = 0;
  
  private static final int STARTED = 1;
  
  private static final int STOPPED = 2;
  
  private int state = 0;
  
  private static final Set<RMIConnectorServer> openedServers = new HashSet();
  
  public RMIConnectorServer(JMXServiceURL paramJMXServiceURL, Map<String, ?> paramMap) throws IOException { this(paramJMXServiceURL, paramMap, (MBeanServer)null); }
  
  public RMIConnectorServer(JMXServiceURL paramJMXServiceURL, Map<String, ?> paramMap, MBeanServer paramMBeanServer) throws IOException { this(paramJMXServiceURL, paramMap, (RMIServerImpl)null, paramMBeanServer); }
  
  public RMIConnectorServer(JMXServiceURL paramJMXServiceURL, Map<String, ?> paramMap, RMIServerImpl paramRMIServerImpl, MBeanServer paramMBeanServer) throws IOException {
    super(paramMBeanServer);
    if (paramJMXServiceURL == null)
      throw new IllegalArgumentException("Null JMXServiceURL"); 
    if (paramRMIServerImpl == null) {
      String str1 = paramJMXServiceURL.getProtocol();
      if (str1 == null || (!str1.equals("rmi") && !str1.equals("iiop"))) {
        String str = "Invalid protocol type: " + str1;
        throw new MalformedURLException(str);
      } 
      String str2 = paramJMXServiceURL.getURLPath();
      if (!str2.equals("") && !str2.equals("/") && !str2.startsWith("/jndi/"))
        throw new MalformedURLException("URL path must be empty or start with /jndi/"); 
    } 
    if (paramMap == null) {
      this.attributes = Collections.emptyMap();
    } else {
      EnvHelp.checkAttributes(paramMap);
      this.attributes = Collections.unmodifiableMap(paramMap);
    } 
    this.address = paramJMXServiceURL;
    this.rmiServerImpl = paramRMIServerImpl;
  }
  
  public JMXConnector toJMXConnector(Map<String, ?> paramMap) throws IOException {
    if (!isActive())
      throw new IllegalStateException("Connector is not active"); 
    HashMap hashMap = new HashMap((this.attributes == null) ? Collections.emptyMap() : this.attributes);
    if (paramMap != null) {
      EnvHelp.checkAttributes(paramMap);
      hashMap.putAll(paramMap);
    } 
    Map map = EnvHelp.filterAttributes(hashMap);
    RMIServer rMIServer = (RMIServer)this.rmiServerImpl.toStub();
    return new RMIConnector(rMIServer, map);
  }
  
  public void start() throws IOException {
    RMIServerImpl rMIServerImpl;
    boolean bool = logger.traceOn();
    if (this.state == 1) {
      if (bool)
        logger.trace("start", "already started"); 
      return;
    } 
    if (this.state == 2) {
      if (bool)
        logger.trace("start", "already stopped"); 
      throw new IOException("The server has been stopped.");
    } 
    if (getMBeanServer() == null)
      throw new IllegalStateException("This connector server is not attached to an MBean server"); 
    if (this.attributes != null) {
      rMIServerImpl = (String)this.attributes.get("jmx.remote.x.access.file");
      if (rMIServerImpl != null) {
        MBeanServerFileAccessController mBeanServerFileAccessController;
        try {
          mBeanServerFileAccessController = new MBeanServerFileAccessController(rMIServerImpl);
        } catch (IOException iOException) {
          throw (IllegalArgumentException)EnvHelp.initCause(new IllegalArgumentException(iOException.getMessage()), iOException);
        } 
        setMBeanServerForwarder(mBeanServerFileAccessController);
      } 
    } 
    try {
      if (bool)
        logger.trace("start", "setting default class loader"); 
      this.defaultClassLoader = EnvHelp.resolveServerClassLoader(this.attributes, getMBeanServer());
    } catch (InstanceNotFoundException null) {
      IllegalArgumentException illegalArgumentException = new IllegalArgumentException("ClassLoader not found: " + rMIServerImpl);
      throw (IllegalArgumentException)EnvHelp.initCause(illegalArgumentException, rMIServerImpl);
    } 
    if (bool)
      logger.trace("start", "setting RMIServer object"); 
    if (this.rmiServerImpl != null) {
      rMIServerImpl = this.rmiServerImpl;
    } else {
      rMIServerImpl = newServer();
    } 
    rMIServerImpl.setMBeanServer(getMBeanServer());
    rMIServerImpl.setDefaultClassLoader(this.defaultClassLoader);
    rMIServerImpl.setRMIConnectorServer(this);
    rMIServerImpl.export();
    try {
      if (bool)
        logger.trace("start", "getting RMIServer object to export"); 
      RMIServer rMIServer = objectToBind(rMIServerImpl, this.attributes);
      if (this.address != null && this.address.getURLPath().startsWith("/jndi/")) {
        String str1 = this.address.getURLPath().substring(6);
        if (bool)
          logger.trace("start", "Using external directory: " + str1); 
        String str2 = (String)this.attributes.get("jmx.remote.jndi.rebind");
        boolean bool1 = EnvHelp.computeBooleanFromString(str2);
        if (bool)
          logger.trace("start", "jmx.remote.jndi.rebind=" + bool1); 
        try {
          if (bool)
            logger.trace("start", "binding to " + str1); 
          Hashtable hashtable = EnvHelp.mapToHashtable(this.attributes);
          bind(str1, hashtable, rMIServer, bool1);
          this.boundJndiUrl = str1;
        } catch (NamingException namingException) {
          throw newIOException("Cannot bind to URL [" + str1 + "]: " + namingException, namingException);
        } 
      } else {
        if (bool)
          logger.trace("start", "Encoding URL"); 
        encodeStubInAddress(rMIServer, this.attributes);
        if (bool)
          logger.trace("start", "Encoded URL: " + this.address); 
      } 
    } catch (Exception exception) {
      try {
        rMIServerImpl.close();
      } catch (Exception exception1) {}
      if (exception instanceof RuntimeException)
        throw (RuntimeException)exception; 
      if (exception instanceof IOException)
        throw (IOException)exception; 
      throw newIOException("Got unexpected exception while starting the connector server: " + exception, exception);
    } 
    this.rmiServerImpl = rMIServerImpl;
    synchronized (openedServers) {
      openedServers.add(this);
    } 
    this.state = 1;
    if (bool) {
      logger.trace("start", "Connector Server Address = " + this.address);
      logger.trace("start", "started.");
    } 
  }
  
  public void stop() throws IOException {
    boolean bool = logger.traceOn();
    synchronized (this) {
      if (this.state == 2) {
        if (bool)
          logger.trace("stop", "already stopped."); 
        return;
      } 
      if (this.state == 0 && bool)
        logger.trace("stop", "not started yet."); 
      if (bool)
        logger.trace("stop", "stopping."); 
      this.state = 2;
    } 
    synchronized (openedServers) {
      openedServers.remove(this);
    } 
    IOException iOException = null;
    if (this.rmiServerImpl != null)
      try {
        if (bool)
          logger.trace("stop", "closing RMI server."); 
        this.rmiServerImpl.close();
      } catch (IOException iOException1) {
        if (bool)
          logger.trace("stop", "failed to close RMI server: " + iOException1); 
        if (logger.debugOn())
          logger.debug("stop", iOException1); 
        iOException = iOException1;
      }  
    if (this.boundJndiUrl != null)
      try {
        if (bool)
          logger.trace("stop", "unbind from external directory: " + this.boundJndiUrl); 
        Hashtable hashtable = EnvHelp.mapToHashtable(this.attributes);
        InitialContext initialContext = new InitialContext(hashtable);
        initialContext.unbind(this.boundJndiUrl);
        initialContext.close();
      } catch (NamingException namingException) {
        if (bool)
          logger.trace("stop", "failed to unbind RMI server: " + namingException); 
        if (logger.debugOn())
          logger.debug("stop", namingException); 
        if (iOException == null)
          iOException = newIOException("Cannot bind to URL: " + namingException, namingException); 
      }  
    if (iOException != null)
      throw iOException; 
    if (bool)
      logger.trace("stop", "stopped"); 
  }
  
  public boolean isActive() { return (this.state == 1); }
  
  public JMXServiceURL getAddress() { return !isActive() ? null : this.address; }
  
  public Map<String, ?> getAttributes() {
    Map map = EnvHelp.filterAttributes(this.attributes);
    return Collections.unmodifiableMap(map);
  }
  
  public void setMBeanServerForwarder(MBeanServerForwarder paramMBeanServerForwarder) {
    super.setMBeanServerForwarder(paramMBeanServerForwarder);
    if (this.rmiServerImpl != null)
      this.rmiServerImpl.setMBeanServer(getMBeanServer()); 
  }
  
  protected void connectionOpened(String paramString1, String paramString2, Object paramObject) { super.connectionOpened(paramString1, paramString2, paramObject); }
  
  protected void connectionClosed(String paramString1, String paramString2, Object paramObject) { super.connectionClosed(paramString1, paramString2, paramObject); }
  
  protected void connectionFailed(String paramString1, String paramString2, Object paramObject) { super.connectionFailed(paramString1, paramString2, paramObject); }
  
  void bind(String paramString, Hashtable<?, ?> paramHashtable, RMIServer paramRMIServer, boolean paramBoolean) throws NamingException, MalformedURLException {
    InitialContext initialContext = new InitialContext(paramHashtable);
    if (paramBoolean) {
      initialContext.rebind(paramString, paramRMIServer);
    } else {
      initialContext.bind(paramString, paramRMIServer);
    } 
    initialContext.close();
  }
  
  RMIServerImpl newServer() throws IOException {
    int i;
    boolean bool = isIiopURL(this.address, true);
    if (this.address == null) {
      i = 0;
    } else {
      i = this.address.getPort();
    } 
    return bool ? newIIOPServer(this.attributes) : newJRMPServer(this.attributes, i);
  }
  
  private void encodeStubInAddress(RMIServer paramRMIServer, Map<String, ?> paramMap) throws IOException {
    int i;
    String str2;
    String str1;
    if (this.address == null) {
      if (IIOPHelper.isStub(paramRMIServer)) {
        str1 = "iiop";
      } else {
        str1 = "rmi";
      } 
      str2 = null;
      i = 0;
    } else {
      str1 = this.address.getProtocol();
      str2 = this.address.getHost().equals("") ? null : this.address.getHost();
      i = this.address.getPort();
    } 
    String str3 = encodeStub(paramRMIServer, paramMap);
    this.address = new JMXServiceURL(str1, str2, i, str3);
  }
  
  static boolean isIiopURL(JMXServiceURL paramJMXServiceURL, boolean paramBoolean) throws MalformedURLException {
    String str = paramJMXServiceURL.getProtocol();
    if (str.equals("rmi"))
      return false; 
    if (str.equals("iiop"))
      return true; 
    if (paramBoolean)
      throw new MalformedURLException("URL must have protocol \"rmi\" or \"iiop\": \"" + str + "\""); 
    return false;
  }
  
  static String encodeStub(RMIServer paramRMIServer, Map<String, ?> paramMap) throws IOException { return IIOPHelper.isStub(paramRMIServer) ? ("/ior/" + encodeIIOPStub(paramRMIServer, paramMap)) : ("/stub/" + encodeJRMPStub(paramRMIServer, paramMap)); }
  
  static String encodeJRMPStub(RMIServer paramRMIServer, Map<String, ?> paramMap) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
    objectOutputStream.writeObject(paramRMIServer);
    objectOutputStream.close();
    byte[] arrayOfByte = byteArrayOutputStream.toByteArray();
    return byteArrayToBase64(arrayOfByte);
  }
  
  static String encodeIIOPStub(RMIServer paramRMIServer, Map<String, ?> paramMap) throws IOException {
    try {
      Object object = IIOPHelper.getOrb(paramRMIServer);
      return IIOPHelper.objectToString(object, paramRMIServer);
    } catch (RuntimeException runtimeException) {
      throw newIOException(runtimeException.getMessage(), runtimeException);
    } 
  }
  
  private static RMIServer objectToBind(RMIServerImpl paramRMIServerImpl, Map<String, ?> paramMap) throws IOException { return RMIConnector.connectStub((RMIServer)paramRMIServerImpl.toStub(), paramMap); }
  
  private static RMIServerImpl newJRMPServer(Map<String, ?> paramMap, int paramInt) throws IOException {
    RMIClientSocketFactory rMIClientSocketFactory = (RMIClientSocketFactory)paramMap.get("jmx.remote.rmi.client.socket.factory");
    RMIServerSocketFactory rMIServerSocketFactory = (RMIServerSocketFactory)paramMap.get("jmx.remote.rmi.server.socket.factory");
    return new RMIJRMPServerImpl(paramInt, rMIClientSocketFactory, rMIServerSocketFactory, paramMap);
  }
  
  private static RMIServerImpl newIIOPServer(Map<String, ?> paramMap) throws IOException { return new RMIIIOPServerImpl(paramMap); }
  
  private static String byteArrayToBase64(byte[] paramArrayOfByte) {
    int i = paramArrayOfByte.length;
    int j = i / 3;
    int k = i - 3 * j;
    int m = 4 * (i + 2) / 3;
    StringBuilder stringBuilder = new StringBuilder(m);
    byte b = 0;
    byte b1;
    for (b1 = 0; b1 < j; b1++) {
      byte b2 = paramArrayOfByte[b++] & 0xFF;
      byte b3 = paramArrayOfByte[b++] & 0xFF;
      byte b4 = paramArrayOfByte[b++] & 0xFF;
      stringBuilder.append(intToAlpha[b2 >> 2]);
      stringBuilder.append(intToAlpha[b2 << 4 & 0x3F | b3 >> 4]);
      stringBuilder.append(intToAlpha[b3 << 2 & 0x3F | b4 >> 6]);
      stringBuilder.append(intToAlpha[b4 & 0x3F]);
    } 
    if (k != 0) {
      b1 = paramArrayOfByte[b++] & 0xFF;
      stringBuilder.append(intToAlpha[b1 >> 2]);
      if (k == 1) {
        stringBuilder.append(intToAlpha[b1 << 4 & 0x3F]);
        stringBuilder.append("==");
      } else {
        byte b2 = paramArrayOfByte[b++] & 0xFF;
        stringBuilder.append(intToAlpha[b1 << 4 & 0x3F | b2 >> 4]);
        stringBuilder.append(intToAlpha[b2 << 2 & 0x3F]);
        stringBuilder.append('=');
      } 
    } 
    return stringBuilder.toString();
  }
  
  private static IOException newIOException(String paramString, Throwable paramThrowable) {
    IOException iOException = new IOException(paramString);
    return (IOException)EnvHelp.initCause(iOException, paramThrowable);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\remote\rmi\RMIConnectorServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */