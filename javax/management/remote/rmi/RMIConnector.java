package javax.management.remote.rmi;

import com.sun.jmx.mbeanserver.Util;
import com.sun.jmx.remote.internal.ClientCommunicatorAdmin;
import com.sun.jmx.remote.internal.ClientListenerInfo;
import com.sun.jmx.remote.internal.ClientNotifForwarder;
import com.sun.jmx.remote.internal.IIOPHelper;
import com.sun.jmx.remote.util.ClassLogger;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.rmi.MarshalException;
import java.rmi.MarshalledObject;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.ServerException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RemoteObject;
import java.rmi.server.RemoteRef;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerDelegate;
import javax.management.NotCompliantMBeanException;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationFilter;
import javax.management.NotificationFilterSupport;
import javax.management.NotificationListener;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.management.ReflectionException;
import javax.management.remote.JMXAddressable;
import javax.management.remote.JMXConnectionNotification;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.NotificationResult;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.Subject;
import sun.reflect.misc.ReflectUtil;
import sun.rmi.server.UnicastRef2;
import sun.rmi.transport.LiveRef;

public class RMIConnector implements JMXConnector, Serializable, JMXAddressable {
  private static final ClassLogger logger = new ClassLogger("javax.management.remote.rmi", "RMIConnector");
  
  private static final long serialVersionUID = 817323035842634473L;
  
  private static final String rmiServerImplStubClassName = RMIServer.class.getName() + "Impl_Stub";
  
  private static final Class<?> rmiServerImplStubClass;
  
  private static final String rmiConnectionImplStubClassName = RMIConnection.class.getName() + "Impl_Stub";
  
  private static final Class<?> rmiConnectionImplStubClass;
  
  private static final String pRefClassName = "com.sun.jmx.remote.internal.PRef";
  
  private static final Constructor<?> proxyRefConstructor;
  
  private static final String iiopConnectionStubClassName = "org.omg.stub.javax.management.remote.rmi._RMIConnection_Stub";
  
  private static final String proxyStubClassName = "com.sun.jmx.remote.protocol.iiop.ProxyStub";
  
  private static final String ProxyInputStreamClassName = "com.sun.jmx.remote.protocol.iiop.ProxyInputStream";
  
  private static final String pInputStreamClassName = "com.sun.jmx.remote.protocol.iiop.PInputStream";
  
  private static final Class<?> proxyStubClass;
  
  private static final byte[] base64ToInt;
  
  private final RMIServer rmiServer;
  
  private final JMXServiceURL jmxServiceURL;
  
  private Map<String, Object> env;
  
  private ClassLoader defaultClassLoader;
  
  private RMIConnection connection;
  
  private String connectionId;
  
  private long clientNotifSeqNo = 0L;
  
  private WeakHashMap<Subject, WeakReference<MBeanServerConnection>> rmbscMap;
  
  private WeakReference<MBeanServerConnection> nullSubjectConnRef = null;
  
  private RMINotifClient rmiNotifClient;
  
  private long clientNotifCounter = 0L;
  
  private boolean connected;
  
  private boolean terminated;
  
  private Exception closeException;
  
  private NotificationBroadcasterSupport connectionBroadcaster;
  
  private ClientCommunicatorAdmin communicatorAdmin;
  
  private RMIConnector(RMIServer paramRMIServer, JMXServiceURL paramJMXServiceURL, Map<String, ?> paramMap) {
    if (paramRMIServer == null && paramJMXServiceURL == null)
      throw new IllegalArgumentException("rmiServer and jmxServiceURL both null"); 
    initTransients();
    this.rmiServer = paramRMIServer;
    this.jmxServiceURL = paramJMXServiceURL;
    if (paramMap == null) {
      this.env = Collections.emptyMap();
    } else {
      EnvHelp.checkAttributes(paramMap);
      this.env = Collections.unmodifiableMap(paramMap);
    } 
  }
  
  public RMIConnector(JMXServiceURL paramJMXServiceURL, Map<String, ?> paramMap) { this(null, paramJMXServiceURL, paramMap); }
  
  public RMIConnector(RMIServer paramRMIServer, Map<String, ?> paramMap) { this(paramRMIServer, null, paramMap); }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder(getClass().getName());
    stringBuilder.append(":");
    if (this.rmiServer != null)
      stringBuilder.append(" rmiServer=").append(this.rmiServer.toString()); 
    if (this.jmxServiceURL != null) {
      if (this.rmiServer != null)
        stringBuilder.append(","); 
      stringBuilder.append(" jmxServiceURL=").append(this.jmxServiceURL.toString());
    } 
    return stringBuilder.toString();
  }
  
  public JMXServiceURL getAddress() { return this.jmxServiceURL; }
  
  public void connect() throws IOException { connect(null); }
  
  public void connect(Map<String, ?> paramMap) throws IOException {
    boolean bool = logger.traceOn();
    String str = bool ? ("[" + toString() + "]") : null;
    if (this.terminated) {
      logger.trace("connect", str + " already closed.");
      throw new IOException("Connector closed");
    } 
    if (this.connected) {
      logger.trace("connect", str + " already connected.");
      return;
    } 
    try {
      if (bool)
        logger.trace("connect", str + " connecting..."); 
      HashMap hashMap = new HashMap((this.env == null) ? Collections.emptyMap() : this.env);
      if (paramMap != null) {
        EnvHelp.checkAttributes(paramMap);
        hashMap.putAll(paramMap);
      } 
      if (bool)
        logger.trace("connect", str + " finding stub..."); 
      RMIServer rMIServer = (this.rmiServer != null) ? this.rmiServer : findRMIServer(this.jmxServiceURL, hashMap);
      String str1 = (String)hashMap.get("jmx.remote.x.check.stub");
      boolean bool1 = EnvHelp.computeBooleanFromString(str1);
      if (bool1)
        checkStub(rMIServer, rmiServerImplStubClass); 
      if (bool)
        logger.trace("connect", str + " connecting stub..."); 
      rMIServer = connectStub(rMIServer, hashMap);
      str = bool ? ("[" + toString() + "]") : null;
      if (bool)
        logger.trace("connect", str + " getting connection..."); 
      Object object = hashMap.get("jmx.remote.credentials");
      try {
        this.connection = getConnection(rMIServer, object, bool1);
      } catch (RemoteException remoteException) {
        if (this.jmxServiceURL != null) {
          String str2 = this.jmxServiceURL.getProtocol();
          String str3 = this.jmxServiceURL.getURLPath();
          if ("rmi".equals(str2) && str3.startsWith("/jndi/iiop:")) {
            MalformedURLException malformedURLException = new MalformedURLException("Protocol is rmi but JNDI scheme is iiop: " + this.jmxServiceURL);
            malformedURLException.initCause(remoteException);
            throw malformedURLException;
          } 
        } 
        throw remoteException;
      } 
      if (bool)
        logger.trace("connect", str + " getting class loader..."); 
      this.defaultClassLoader = EnvHelp.resolveClientClassLoader(hashMap);
      hashMap.put("jmx.remote.default.class.loader", this.defaultClassLoader);
      this.rmiNotifClient = new RMINotifClient(this.defaultClassLoader, hashMap);
      this.env = hashMap;
      long l = EnvHelp.getConnectionCheckPeriod(hashMap);
      this.communicatorAdmin = new RMIClientCommunicatorAdmin(l);
      this.connected = true;
      this.connectionId = getConnectionId();
      JMXConnectionNotification jMXConnectionNotification = new JMXConnectionNotification("jmx.remote.connection.opened", this, this.connectionId, this.clientNotifSeqNo++, "Successful connection", null);
      sendNotification(jMXConnectionNotification);
      if (bool)
        logger.trace("connect", str + " done..."); 
    } catch (IOException iOException) {
      if (bool)
        logger.trace("connect", str + " failed to connect: " + iOException); 
      throw iOException;
    } catch (RuntimeException runtimeException) {
      if (bool)
        logger.trace("connect", str + " failed to connect: " + runtimeException); 
      throw runtimeException;
    } catch (NamingException namingException) {
      String str1 = "Failed to retrieve RMIServer stub: " + namingException;
      if (bool)
        logger.trace("connect", str + " " + str1); 
      throw (IOException)EnvHelp.initCause(new IOException(str1), namingException);
    } 
  }
  
  public String getConnectionId() {
    if (this.terminated || !this.connected) {
      if (logger.traceOn())
        logger.trace("getConnectionId", "[" + toString() + "] not connected."); 
      throw new IOException("Not connected");
    } 
    return this.connection.getConnectionId();
  }
  
  public MBeanServerConnection getMBeanServerConnection() throws IOException { return getMBeanServerConnection(null); }
  
  public MBeanServerConnection getMBeanServerConnection(Subject paramSubject) throws IOException {
    if (this.terminated) {
      if (logger.traceOn())
        logger.trace("getMBeanServerConnection", "[" + toString() + "] already closed."); 
      throw new IOException("Connection closed");
    } 
    if (!this.connected) {
      if (logger.traceOn())
        logger.trace("getMBeanServerConnection", "[" + toString() + "] is not connected."); 
      throw new IOException("Not connected");
    } 
    return getConnectionWithSubject(paramSubject);
  }
  
  public void addConnectionNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject) {
    if (paramNotificationListener == null)
      throw new NullPointerException("listener"); 
    this.connectionBroadcaster.addNotificationListener(paramNotificationListener, paramNotificationFilter, paramObject);
  }
  
  public void removeConnectionNotificationListener(NotificationListener paramNotificationListener) throws ListenerNotFoundException {
    if (paramNotificationListener == null)
      throw new NullPointerException("listener"); 
    this.connectionBroadcaster.removeNotificationListener(paramNotificationListener);
  }
  
  public void removeConnectionNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject) {
    if (paramNotificationListener == null)
      throw new NullPointerException("listener"); 
    this.connectionBroadcaster.removeNotificationListener(paramNotificationListener, paramNotificationFilter, paramObject);
  }
  
  private void sendNotification(Notification paramNotification) { this.connectionBroadcaster.sendNotification(paramNotification); }
  
  public void close() throws IOException { close(false); }
  
  private void close(boolean paramBoolean) throws IOException {
    boolean bool1 = logger.traceOn();
    boolean bool2 = logger.debugOn();
    String str1 = bool1 ? ("[" + toString() + "]") : null;
    if (!paramBoolean)
      if (this.terminated) {
        if (this.closeException == null) {
          if (bool1)
            logger.trace("close", str1 + " already closed."); 
          return;
        } 
      } else {
        this.terminated = true;
      }  
    if (this.closeException != null && bool1 && bool1) {
      logger.trace("close", str1 + " had failed: " + this.closeException);
      logger.trace("close", str1 + " attempting to close again.");
    } 
    String str2 = null;
    if (this.connected)
      str2 = this.connectionId; 
    this.closeException = null;
    if (bool1)
      logger.trace("close", str1 + " closing."); 
    if (this.communicatorAdmin != null)
      this.communicatorAdmin.terminate(); 
    if (this.rmiNotifClient != null)
      try {
        this.rmiNotifClient.terminate();
        if (bool1)
          logger.trace("close", str1 + " RMI Notification client terminated."); 
      } catch (RuntimeException runtimeException) {
        this.closeException = runtimeException;
        if (bool1)
          logger.trace("close", str1 + " Failed to terminate RMI Notification client: " + runtimeException); 
        if (bool2)
          logger.debug("close", runtimeException); 
      }  
    if (this.connection != null)
      try {
        this.connection.close();
        if (bool1)
          logger.trace("close", str1 + " closed."); 
      } catch (NoSuchObjectException noSuchObjectException) {
      
      } catch (IOException iOException) {
        this.closeException = iOException;
        if (bool1)
          logger.trace("close", str1 + " Failed to close RMIServer: " + iOException); 
        if (bool2)
          logger.debug("close", iOException); 
      }  
    this.rmbscMap.clear();
    if (str2 != null) {
      JMXConnectionNotification jMXConnectionNotification = new JMXConnectionNotification("jmx.remote.connection.closed", this, str2, this.clientNotifSeqNo++, "Client has been closed", null);
      sendNotification(jMXConnectionNotification);
    } 
    if (this.closeException != null) {
      if (bool1)
        logger.trace("close", str1 + " failed to close: " + this.closeException); 
      if (this.closeException instanceof IOException)
        throw (IOException)this.closeException; 
      if (this.closeException instanceof RuntimeException)
        throw (RuntimeException)this.closeException; 
      IOException iOException = new IOException("Failed to close: " + this.closeException);
      throw (IOException)EnvHelp.initCause(iOException, this.closeException);
    } 
  }
  
  private Integer addListenerWithSubject(ObjectName paramObjectName, MarshalledObject<NotificationFilter> paramMarshalledObject, Subject paramSubject, boolean paramBoolean) throws InstanceNotFoundException, IOException {
    boolean bool = logger.debugOn();
    if (bool)
      logger.debug("addListenerWithSubject", "(ObjectName,MarshalledObject,Subject)"); 
    ObjectName[] arrayOfObjectName = { paramObjectName };
    MarshalledObject[] arrayOfMarshalledObject = (MarshalledObject[])Util.cast(new MarshalledObject[] { paramMarshalledObject });
    Subject[] arrayOfSubject = { paramSubject };
    Integer[] arrayOfInteger = addListenersWithSubjects(arrayOfObjectName, arrayOfMarshalledObject, arrayOfSubject, paramBoolean);
    if (bool)
      logger.debug("addListenerWithSubject", "listenerID=" + arrayOfInteger[0]); 
    return arrayOfInteger[0];
  }
  
  private Integer[] addListenersWithSubjects(ObjectName[] paramArrayOfObjectName, MarshalledObject<NotificationFilter>[] paramArrayOfMarshalledObject, Subject[] paramArrayOfSubject, boolean paramBoolean) throws InstanceNotFoundException, IOException {
    boolean bool = logger.debugOn();
    if (bool)
      logger.debug("addListenersWithSubjects", "(ObjectName[],MarshalledObject[],Subject[])"); 
    classLoader = pushDefaultClassLoader();
    Integer[] arrayOfInteger = null;
    try {
      arrayOfInteger = this.connection.addNotificationListeners(paramArrayOfObjectName, paramArrayOfMarshalledObject, paramArrayOfSubject);
    } catch (NoSuchObjectException noSuchObjectException) {
      if (paramBoolean) {
        this.communicatorAdmin.gotIOException(noSuchObjectException);
        arrayOfInteger = this.connection.addNotificationListeners(paramArrayOfObjectName, paramArrayOfMarshalledObject, paramArrayOfSubject);
      } else {
        throw noSuchObjectException;
      } 
    } catch (IOException iOException) {
      this.communicatorAdmin.gotIOException(iOException);
    } finally {
      popDefaultClassLoader(classLoader);
    } 
    if (bool)
      logger.debug("addListenersWithSubjects", "registered " + ((arrayOfInteger == null) ? 0 : arrayOfInteger.length) + " listener(s)"); 
    return arrayOfInteger;
  }
  
  static RMIServer connectStub(RMIServer paramRMIServer, Map<String, ?> paramMap) throws IOException {
    if (IIOPHelper.isStub(paramRMIServer))
      try {
        IIOPHelper.getOrb(paramRMIServer);
      } catch (UnsupportedOperationException unsupportedOperationException) {
        IIOPHelper.connect(paramRMIServer, resolveOrb(paramMap));
      }  
    return paramRMIServer;
  }
  
  static Object resolveOrb(Map<String, ?> paramMap) throws IOException {
    if (paramMap != null) {
      Object object = paramMap.get("java.naming.corba.orb");
      if (object != null && !IIOPHelper.isOrb(object))
        throw new IllegalArgumentException("java.naming.corba.orb must be an instance of org.omg.CORBA.ORB."); 
      if (object != null)
        return object; 
    } 
    Object object1 = (orb == null) ? null : orb.get();
    if (object1 != null)
      return object1; 
    Object object2 = IIOPHelper.createOrb((String[])null, (Properties)null);
    orb = new WeakReference(object2);
    return object2;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    if (this.rmiServer == null && this.jmxServiceURL == null)
      throw new InvalidObjectException("rmiServer and jmxServiceURL both null"); 
    initTransients();
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    if (this.rmiServer == null && this.jmxServiceURL == null)
      throw new InvalidObjectException("rmiServer and jmxServiceURL both null."); 
    connectStub(this.rmiServer, this.env);
    paramObjectOutputStream.defaultWriteObject();
  }
  
  private void initTransients() throws IOException {
    this.rmbscMap = new WeakHashMap();
    this.connected = false;
    this.terminated = false;
    this.connectionBroadcaster = new NotificationBroadcasterSupport();
  }
  
  private static void checkStub(Remote paramRemote, Class<?> paramClass) {
    if (paramRemote.getClass() != paramClass) {
      if (!Proxy.isProxyClass(paramRemote.getClass()))
        throw new SecurityException("Expecting a " + paramClass.getName() + " stub!"); 
      InvocationHandler invocationHandler = Proxy.getInvocationHandler(paramRemote);
      if (invocationHandler.getClass() != java.rmi.server.RemoteObjectInvocationHandler.class)
        throw new SecurityException("Expecting a dynamic proxy instance with a " + java.rmi.server.RemoteObjectInvocationHandler.class.getName() + " invocation handler!"); 
      paramRemote = (Remote)invocationHandler;
    } 
    RemoteRef remoteRef = ((RemoteObject)paramRemote).getRef();
    if (remoteRef.getClass() != UnicastRef2.class)
      throw new SecurityException("Expecting a " + UnicastRef2.class.getName() + " remote reference in stub!"); 
    LiveRef liveRef = ((UnicastRef2)remoteRef).getLiveRef();
    RMIClientSocketFactory rMIClientSocketFactory = liveRef.getClientSocketFactory();
    if (rMIClientSocketFactory == null || rMIClientSocketFactory.getClass() != javax.rmi.ssl.SslRMIClientSocketFactory.class)
      throw new SecurityException("Expecting a " + javax.rmi.ssl.SslRMIClientSocketFactory.class.getName() + " RMI client socket factory in stub!"); 
  }
  
  private RMIServer findRMIServer(JMXServiceURL paramJMXServiceURL, Map<String, Object> paramMap) throws NamingException, IOException {
    boolean bool = RMIConnectorServer.isIiopURL(paramJMXServiceURL, true);
    if (bool)
      paramMap.put("java.naming.corba.orb", resolveOrb(paramMap)); 
    String str1 = paramJMXServiceURL.getURLPath();
    int i = str1.indexOf(';');
    if (i < 0)
      i = str1.length(); 
    if (str1.startsWith("/jndi/"))
      return findRMIServerJNDI(str1.substring(6, i), paramMap, bool); 
    if (str1.startsWith("/stub/"))
      return findRMIServerJRMP(str1.substring(6, i), paramMap, bool); 
    if (str1.startsWith("/ior/")) {
      if (!IIOPHelper.isAvailable())
        throw new IOException("iiop protocol not available"); 
      return findRMIServerIIOP(str1.substring(5, i), paramMap, bool);
    } 
    String str2 = "URL path must begin with /jndi/ or /stub/ or /ior/: " + str1;
    throw new MalformedURLException(str2);
  }
  
  private RMIServer findRMIServerJNDI(String paramString, Map<String, ?> paramMap, boolean paramBoolean) throws NamingException {
    InitialContext initialContext = new InitialContext(EnvHelp.mapToHashtable(paramMap));
    Object object = initialContext.lookup(paramString);
    initialContext.close();
    return paramBoolean ? narrowIIOPServer(object) : narrowJRMPServer(object);
  }
  
  private static RMIServer narrowJRMPServer(Object paramObject) { return (RMIServer)paramObject; }
  
  private static RMIServer narrowIIOPServer(Object paramObject) {
    try {
      return (RMIServer)IIOPHelper.narrow(paramObject, RMIServer.class);
    } catch (ClassCastException classCastException) {
      if (logger.traceOn())
        logger.trace("narrowIIOPServer", "Failed to narrow objref=" + paramObject + ": " + classCastException); 
      if (logger.debugOn())
        logger.debug("narrowIIOPServer", classCastException); 
      return null;
    } 
  }
  
  private RMIServer findRMIServerIIOP(String paramString, Map<String, ?> paramMap, boolean paramBoolean) throws NamingException {
    Object object1 = paramMap.get("java.naming.corba.orb");
    Object object2 = IIOPHelper.stringToObject(object1, paramString);
    return (RMIServer)IIOPHelper.narrow(object2, RMIServer.class);
  }
  
  private RMIServer findRMIServerJRMP(String paramString, Map<String, ?> paramMap, boolean paramBoolean) throws NamingException {
    Object object;
    byte[] arrayOfByte;
    try {
      arrayOfByte = base64ToByteArray(paramString);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new MalformedURLException("Bad BASE64 encoding: " + illegalArgumentException.getMessage());
    } 
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
    ClassLoader classLoader = EnvHelp.resolveClientClassLoader(paramMap);
    ObjectInputStream objectInputStream = (classLoader == null) ? new ObjectInputStream(byteArrayInputStream) : new ObjectInputStreamWithLoader(byteArrayInputStream, classLoader);
    try {
      object = objectInputStream.readObject();
    } catch (ClassNotFoundException classNotFoundException) {
      throw new MalformedURLException("Class not found: " + classNotFoundException);
    } 
    return (RMIServer)object;
  }
  
  private MBeanServerConnection getConnectionWithSubject(Subject paramSubject) throws IOException {
    MBeanServerConnection mBeanServerConnection = null;
    if (paramSubject == null) {
      if (this.nullSubjectConnRef == null || (mBeanServerConnection = (MBeanServerConnection)this.nullSubjectConnRef.get()) == null) {
        mBeanServerConnection = new RemoteMBeanServerConnection(null);
        this.nullSubjectConnRef = new WeakReference(mBeanServerConnection);
      } 
    } else {
      WeakReference weakReference = (WeakReference)this.rmbscMap.get(paramSubject);
      if (weakReference == null || (mBeanServerConnection = (MBeanServerConnection)weakReference.get()) == null) {
        mBeanServerConnection = new RemoteMBeanServerConnection(paramSubject);
        this.rmbscMap.put(paramSubject, new WeakReference(mBeanServerConnection));
      } 
    } 
    return mBeanServerConnection;
  }
  
  private static RMIConnection shadowJrmpStub(RemoteObject paramRemoteObject) throws InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException {
    RemoteRef remoteRef1 = paramRemoteObject.getRef();
    RemoteRef remoteRef2 = (RemoteRef)proxyRefConstructor.newInstance(new Object[] { remoteRef1 });
    Constructor constructor = rmiConnectionImplStubClass.getConstructor(new Class[] { RemoteRef.class });
    Object[] arrayOfObject = { remoteRef2 };
    return (RMIConnection)constructor.newInstance(arrayOfObject);
  }
  
  private static RMIConnection shadowIiopStub(Object paramObject) throws InstantiationException, IllegalAccessException {
    Object object = null;
    try {
      object = AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
            public Object run() throws Exception { return proxyStubClass.newInstance(); }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      throw new InternalError();
    } 
    IIOPHelper.setDelegate(object, IIOPHelper.getDelegate(paramObject));
    return (RMIConnection)object;
  }
  
  private static RMIConnection getConnection(RMIServer paramRMIServer, Object paramObject, boolean paramBoolean) throws IOException {
    RMIConnection rMIConnection = paramRMIServer.newClient(paramObject);
    if (paramBoolean)
      checkStub(rMIConnection, rmiConnectionImplStubClass); 
    try {
      if (rMIConnection.getClass() == rmiConnectionImplStubClass)
        return shadowJrmpStub((RemoteObject)rMIConnection); 
      if (rMIConnection.getClass().getName().equals("org.omg.stub.javax.management.remote.rmi._RMIConnection_Stub"))
        return shadowIiopStub(rMIConnection); 
      logger.trace("getConnection", "Did not wrap " + rMIConnection.getClass() + " to foil stack search for classes: class loading semantics may be incorrect");
    } catch (Exception exception) {
      logger.error("getConnection", "Could not wrap " + rMIConnection.getClass() + " to foil stack search for classes: class loading semantics may be incorrect: " + exception);
      logger.debug("getConnection", exception);
    } 
    return rMIConnection;
  }
  
  private static byte[] base64ToByteArray(String paramString) {
    int i = paramString.length();
    int j = i / 4;
    if (4 * j != i)
      throw new IllegalArgumentException("String length must be a multiple of four."); 
    int k = 0;
    int m = j;
    if (i != 0) {
      if (paramString.charAt(i - 1) == '=') {
        k++;
        m--;
      } 
      if (paramString.charAt(i - 2) == '=')
        k++; 
    } 
    byte[] arrayOfByte = new byte[3 * j - k];
    byte b1 = 0;
    byte b2 = 0;
    int n;
    for (n = 0; n < m; n++) {
      int i1 = base64toInt(paramString.charAt(b1++));
      int i2 = base64toInt(paramString.charAt(b1++));
      int i3 = base64toInt(paramString.charAt(b1++));
      int i4 = base64toInt(paramString.charAt(b1++));
      arrayOfByte[b2++] = (byte)(i1 << 2 | i2 >> 4);
      arrayOfByte[b2++] = (byte)(i2 << 4 | i3 >> 2);
      arrayOfByte[b2++] = (byte)(i3 << 6 | i4);
    } 
    if (k != 0) {
      n = base64toInt(paramString.charAt(b1++));
      int i1 = base64toInt(paramString.charAt(b1++));
      arrayOfByte[b2++] = (byte)(n << 2 | i1 >> 4);
      if (k == 1) {
        int i2 = base64toInt(paramString.charAt(b1++));
        arrayOfByte[b2++] = (byte)(i1 << 4 | i2 >> 2);
      } 
    } 
    return arrayOfByte;
  }
  
  private static int base64toInt(char paramChar) {
    byte b;
    if (paramChar >= base64ToInt.length) {
      b = -1;
    } else {
      b = base64ToInt[paramChar];
    } 
    if (b < 0)
      throw new IllegalArgumentException("Illegal character " + paramChar); 
    return b;
  }
  
  private ClassLoader pushDefaultClassLoader() {
    final Thread t = Thread.currentThread();
    ClassLoader classLoader = thread.getContextClassLoader();
    if (this.defaultClassLoader != null)
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
              t.setContextClassLoader(RMIConnector.this.defaultClassLoader);
              return null;
            }
          }); 
    return classLoader;
  }
  
  private void popDefaultClassLoader(final ClassLoader old) { AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            Thread.currentThread().setContextClassLoader(old);
            return null;
          }
        }); }
  
  private static String objects(Object[] paramArrayOfObject) { return (paramArrayOfObject == null) ? "null" : Arrays.asList(paramArrayOfObject).toString(); }
  
  private static String strings(String[] paramArrayOfString) { return objects(paramArrayOfString); }
  
  static String getAttributesNames(AttributeList paramAttributeList) { return (paramAttributeList != null) ? (String)paramAttributeList.asList().stream().map(Attribute::getName).collect(Collectors.joining(", ", "[", "]")) : "[]"; }
  
  static  {
    final byte[] pRefByteCode = NoCallStackClassLoader.stringToBytes("Êþº¾\000\000\000.\000\027\n\000\005\000\r\t\000\004\000\016\013\000\017\000\020\007\000\021\007\000\022\001\000\006<init>\001\000\036(Ljava/rmi/server/RemoteRef;)V\001\000\004Code\001\000\006invoke\001\000S(Ljava/rmi/Remote;Ljava/lang/reflect/Method;[Ljava/lang/Object;J)Ljava/lang/Object;\001\000\nExceptions\007\000\023\f\000\006\000\007\f\000\024\000\025\007\000\026\f\000\t\000\n\001\000 com/sun/jmx/remote/internal/PRef\001\000$com/sun/jmx/remote/internal/ProxyRef\001\000\023java/lang/Exception\001\000\003ref\001\000\033Ljava/rmi/server/RemoteRef;\001\000\031java/rmi/server/RemoteRef\000!\000\004\000\005\000\000\000\000\000\002\000\001\000\006\000\007\000\001\000\b\000\000\000\022\000\002\000\002\000\000\000\006*+·\000\001±\000\000\000\000\000\001\000\t\000\n\000\002\000\b\000\000\000\033\000\006\000\006\000\000\000\017*´\000\002+,-\026\004¹\000\003\006\000°\000\000\000\000\000\013\000\000\000\004\000\001\000\f\000\000");
    PrivilegedExceptionAction<Constructor<?>> privilegedExceptionAction = new PrivilegedExceptionAction<Constructor<?>>() {
        public Constructor<?> run() throws Exception {
          Class clazz1 = RMIConnector.class;
          ClassLoader classLoader = clazz1.getClassLoader();
          ProtectionDomain protectionDomain = clazz1.getProtectionDomain();
          String[] arrayOfString = { com.sun.jmx.remote.internal.ProxyRef.class.getName() };
          NoCallStackClassLoader noCallStackClassLoader = new NoCallStackClassLoader("com.sun.jmx.remote.internal.PRef", pRefByteCode, arrayOfString, classLoader, protectionDomain);
          Class clazz2 = noCallStackClassLoader.loadClass("com.sun.jmx.remote.internal.PRef");
          return clazz2.getConstructor(new Class[] { RemoteRef.class });
        }
      };
    try {
      arrayOfByte3 = Class.forName(rmiServerImplStubClassName);
    } catch (Exception null) {
      logger.error("<clinit>", "Failed to instantiate " + rmiServerImplStubClassName + ": " + arrayOfString1);
      logger.debug("<clinit>", arrayOfString1);
      arrayOfByte3 = null;
    } 
    rmiServerImplStubClass = arrayOfByte3;
    try {
      arrayOfString1 = Class.forName(rmiConnectionImplStubClassName);
      arrayOfByte = (Constructor)AccessController.doPrivileged(privilegedExceptionAction);
    } catch (Exception exception) {
      logger.error("<clinit>", "Failed to initialize proxy reference constructor for " + rmiConnectionImplStubClassName + ": " + exception);
      logger.debug("<clinit>", exception);
      arrayOfString1 = null;
      arrayOfByte = null;
    } 
    rmiConnectionImplStubClass = arrayOfString1;
    proxyRefConstructor = arrayOfByte;
    byte[] arrayOfByte2 = NoCallStackClassLoader.stringToBytes("Êþº¾\000\000\0003\000+\n\000\f\000\030\007\000\031\n\000\f\000\032\n\000\002\000\033\007\000\034\n\000\005\000\035\n\000\005\000\036\n\000\005\000\037\n\000\002\000 \n\000\f\000!\007\000\"\007\000#\001\000\006<init>\001\000\003()V\001\000\004Code\001\000\007_invoke\001\000K(Lorg/omg/CORBA/portable/OutputStream;)Lorg/omg/CORBA/portable/InputStream;\001\000\rStackMapTable\007\000\034\001\000\nExceptions\007\000$\001\000\r_releaseReply\001\000'(Lorg/omg/CORBA/portable/InputStream;)V\f\000\r\000\016\001\000-com/sun/jmx/remote/protocol/iiop/PInputStream\f\000\020\000\021\f\000\r\000\027\001\000+org/omg/CORBA/portable/ApplicationException\f\000%\000&\f\000'\000(\f\000\r\000)\f\000*\000&\f\000\026\000\027\001\000*com/sun/jmx/remote/protocol/iiop/ProxyStub\001\000<org/omg/stub/javax/management/remote/rmi/_RMIConnection_Stub\001\000)org/omg/CORBA/portable/RemarshalException\001\000\016getInputStream\001\000&()Lorg/omg/CORBA/portable/InputStream;\001\000\005getId\001\000\024()Ljava/lang/String;\001\0009(Ljava/lang/String;Lorg/omg/CORBA/portable/InputStream;)V\001\000\025getProxiedInputStream\000!\000\013\000\f\000\000\000\000\000\003\000\001\000\r\000\016\000\001\000\017\000\000\000\021\000\001\000\001\000\000\000\005*·\000\001±\000\000\000\000\000\001\000\020\000\021\000\002\000\017\000\000\000G\000\004\000\004\000\000\000'»\000\002Y*+·\000\003·\000\004°M»\000\002Y,¶\000\006·\000\004N»\000\005Y,¶\000\007-·\000\b¿\000\001\000\000\000\f\000\r\000\005\000\001\000\022\000\000\000\006\000\001M\007\000\023\000\024\000\000\000\006\000\002\000\005\000\025\000\001\000\026\000\027\000\001\000\017\000\000\000'\000\002\000\002\000\000\000\022+Æ\000\013+À\000\002¶\000\tL*+·\000\n±\000\000\000\001\000\022\000\000\000\003\000\001\f\000\000");
    byte[] arrayOfByte3 = NoCallStackClassLoader.stringToBytes("Êþº¾\000\000\0003\000\036\n\000\007\000\017\t\000\006\000\020\n\000\021\000\022\n\000\006\000\023\n\000\024\000\025\007\000\026\007\000\027\001\000\006<init>\001\000'(Lorg/omg/CORBA/portable/InputStream;)V\001\000\004Code\001\000\bread_any\001\000\025()Lorg/omg/CORBA/Any;\001\000\nread_value\001\000)(Ljava/lang/Class;)Ljava/io/Serializable;\f\000\b\000\t\f\000\030\000\031\007\000\032\f\000\013\000\f\f\000\033\000\034\007\000\035\f\000\r\000\016\001\000-com/sun/jmx/remote/protocol/iiop/PInputStream\001\0001com/sun/jmx/remote/protocol/iiop/ProxyInputStream\001\000\002in\001\000$Lorg/omg/CORBA/portable/InputStream;\001\000\"org/omg/CORBA/portable/InputStream\001\000\006narrow\001\000*()Lorg/omg/CORBA_2_3/portable/InputStream;\001\000&org/omg/CORBA_2_3/portable/InputStream\000!\000\006\000\007\000\000\000\000\000\003\000\001\000\b\000\t\000\001\000\n\000\000\000\022\000\002\000\002\000\000\000\006*+·\000\001±\000\000\000\000\000\001\000\013\000\f\000\001\000\n\000\000\000\024\000\001\000\001\000\000\000\b*´\000\002¶\000\003°\000\000\000\000\000\001\000\r\000\016\000\001\000\n\000\000\000\025\000\002\000\002\000\000\000\t*¶\000\004+¶\000\005°\000\000\000\000\000\000");
    final String[] classNames = { "com.sun.jmx.remote.protocol.iiop.ProxyStub", "com.sun.jmx.remote.protocol.iiop.PInputStream" };
    final byte[][] byteCodes = { arrayOfByte2, arrayOfByte3 };
    final String[] otherClassNames = { "org.omg.stub.javax.management.remote.rmi._RMIConnection_Stub", "com.sun.jmx.remote.protocol.iiop.ProxyInputStream" };
    if (IIOPHelper.isAvailable()) {
      Object object;
      PrivilegedExceptionAction<Class<?>> privilegedExceptionAction1 = new PrivilegedExceptionAction<Class<?>>() {
          public Class<?> run() throws Exception {
            Class clazz = RMIConnector.class;
            ClassLoader classLoader = clazz.getClassLoader();
            ProtectionDomain protectionDomain = clazz.getProtectionDomain();
            NoCallStackClassLoader noCallStackClassLoader = new NoCallStackClassLoader(classNames, byteCodes, otherClassNames, classLoader, protectionDomain);
            return noCallStackClassLoader.loadClass("com.sun.jmx.remote.protocol.iiop.ProxyStub");
          }
        };
      try {
        object = (Class)AccessController.doPrivileged(privilegedExceptionAction1);
      } catch (Exception exception) {
        logger.error("<clinit>", "Unexpected exception making shadow IIOP stub class: " + exception);
        logger.debug("<clinit>", exception);
        object = null;
      } 
      proxyStubClass = object;
    } else {
      proxyStubClass = null;
    } 
    base64ToInt = new byte[] { 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 
        54, 55, 56, 57, 58, 59, 60, 61, -1, -1, 
        -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 
        5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 
        15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 
        25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 
        29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 
        39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 
        49, 50, 51 };
    orb = null;
  }
  
  private static final class ObjectInputStreamWithLoader extends ObjectInputStream {
    private final ClassLoader loader;
    
    ObjectInputStreamWithLoader(InputStream param1InputStream, ClassLoader param1ClassLoader) throws IOException {
      super(param1InputStream);
      this.loader = param1ClassLoader;
    }
    
    protected Class<?> resolveClass(ObjectStreamClass param1ObjectStreamClass) throws IOException, ClassNotFoundException {
      String str = param1ObjectStreamClass.getName();
      ReflectUtil.checkPackageAccess(str);
      return Class.forName(str, false, this.loader);
    }
  }
  
  private class RMIClientCommunicatorAdmin extends ClientCommunicatorAdmin {
    public RMIClientCommunicatorAdmin(long param1Long) { super(param1Long); }
    
    public void gotIOException(IOException param1IOException) throws IOException {
      if (param1IOException instanceof NoSuchObjectException) {
        super.gotIOException(param1IOException);
        return;
      } 
      try {
        RMIConnector.this.connection.getDefaultDomain(null);
      } catch (IOException iOException) {
        boolean bool = false;
        synchronized (this) {
          if (!RMIConnector.this.terminated) {
            RMIConnector.this.terminated = true;
            bool = true;
          } 
        } 
        if (bool) {
          JMXConnectionNotification jMXConnectionNotification = new JMXConnectionNotification("jmx.remote.connection.failed", this, RMIConnector.this.connectionId, RMIConnector.this.clientNotifSeqNo++, "Failed to communicate with the server: " + param1IOException.toString(), param1IOException);
          RMIConnector.this.sendNotification(jMXConnectionNotification);
          try {
            RMIConnector.this.close(true);
          } catch (Exception exception) {}
        } 
      } 
      if (param1IOException instanceof ServerException) {
        Throwable throwable = ((ServerException)param1IOException).detail;
        if (throwable instanceof IOException)
          throw (IOException)throwable; 
        if (throwable instanceof RuntimeException)
          throw (RuntimeException)throwable; 
      } 
      throw param1IOException;
    }
    
    public void reconnectNotificationListeners(ClientListenerInfo[] param1ArrayOfClientListenerInfo) throws IOException {
      int i = param1ArrayOfClientListenerInfo.length;
      ClientListenerInfo[] arrayOfClientListenerInfo = new ClientListenerInfo[i];
      Subject[] arrayOfSubject = new Subject[i];
      ObjectName[] arrayOfObjectName = new ObjectName[i];
      NotificationListener[] arrayOfNotificationListener = new NotificationListener[i];
      NotificationFilter[] arrayOfNotificationFilter = new NotificationFilter[i];
      MarshalledObject[] arrayOfMarshalledObject = (MarshalledObject[])Util.cast(new MarshalledObject[i]);
      Object[] arrayOfObject = new Object[i];
      byte b;
      for (b = 0; b < i; b++) {
        arrayOfSubject[b] = param1ArrayOfClientListenerInfo[b].getDelegationSubject();
        arrayOfObjectName[b] = param1ArrayOfClientListenerInfo[b].getObjectName();
        arrayOfNotificationListener[b] = param1ArrayOfClientListenerInfo[b].getListener();
        arrayOfNotificationFilter[b] = param1ArrayOfClientListenerInfo[b].getNotificationFilter();
        arrayOfMarshalledObject[b] = new MarshalledObject(arrayOfNotificationFilter[b]);
        arrayOfObject[b] = param1ArrayOfClientListenerInfo[b].getHandback();
      } 
      try {
        Integer[] arrayOfInteger = RMIConnector.this.addListenersWithSubjects(arrayOfObjectName, arrayOfMarshalledObject, arrayOfSubject, false);
        for (b = 0; b < i; b++)
          arrayOfClientListenerInfo[b] = new ClientListenerInfo(arrayOfInteger[b], arrayOfObjectName[b], arrayOfNotificationListener[b], arrayOfNotificationFilter[b], arrayOfObject[b], arrayOfSubject[b]); 
        RMIConnector.this.rmiNotifClient.postReconnection(arrayOfClientListenerInfo);
        return;
      } catch (InstanceNotFoundException instanceNotFoundException) {
        byte b1 = 0;
        for (b = 0; b < i; b++) {
          try {
            Integer integer = RMIConnector.this.addListenerWithSubject(arrayOfObjectName[b], new MarshalledObject(arrayOfNotificationFilter[b]), arrayOfSubject[b], false);
            arrayOfClientListenerInfo[b1++] = new ClientListenerInfo(integer, arrayOfObjectName[b], arrayOfNotificationListener[b], arrayOfNotificationFilter[b], arrayOfObject[b], arrayOfSubject[b]);
          } catch (InstanceNotFoundException instanceNotFoundException1) {
            logger.warning("reconnectNotificationListeners", "Can't reconnect listener for " + arrayOfObjectName[b]);
          } 
        } 
        if (b1 != i) {
          ClientListenerInfo[] arrayOfClientListenerInfo1 = arrayOfClientListenerInfo;
          arrayOfClientListenerInfo = new ClientListenerInfo[b1];
          System.arraycopy(arrayOfClientListenerInfo1, 0, arrayOfClientListenerInfo, 0, b1);
        } 
        RMIConnector.this.rmiNotifClient.postReconnection(arrayOfClientListenerInfo);
        return;
      } 
    }
    
    protected void checkConnection() throws IOException {
      if (logger.debugOn())
        logger.debug("RMIClientCommunicatorAdmin-checkConnection", "Calling the method getDefaultDomain."); 
      RMIConnector.this.connection.getDefaultDomain(null);
    }
    
    protected void doStart() throws IOException {
      try {
        rMIServer = (RMIConnector.this.rmiServer != null) ? RMIConnector.this.rmiServer : RMIConnector.this.findRMIServer(RMIConnector.this.jmxServiceURL, RMIConnector.this.env);
      } catch (NamingException namingException) {
        throw new IOException("Failed to get a RMI stub: " + namingException);
      } 
      RMIServer rMIServer = RMIConnector.connectStub(rMIServer, RMIConnector.this.env);
      Object object = RMIConnector.this.env.get("jmx.remote.credentials");
      RMIConnector.this.connection = rMIServer.newClient(object);
      ClientListenerInfo[] arrayOfClientListenerInfo = RMIConnector.this.rmiNotifClient.preReconnection();
      reconnectNotificationListeners(arrayOfClientListenerInfo);
      RMIConnector.this.connectionId = RMIConnector.this.getConnectionId();
      JMXConnectionNotification jMXConnectionNotification = new JMXConnectionNotification("jmx.remote.connection.opened", this, RMIConnector.this.connectionId, RMIConnector.this.clientNotifSeqNo++, "Reconnected to server", null);
      RMIConnector.this.sendNotification(jMXConnectionNotification);
    }
    
    protected void doStop() throws IOException {
      try {
        RMIConnector.this.close();
      } catch (IOException iOException) {
        logger.warning("RMIClientCommunicatorAdmin-doStop", "Failed to call the method close():" + iOException);
        logger.debug("RMIClientCommunicatorAdmin-doStop", iOException);
      } 
    }
  }
  
  private class RMINotifClient extends ClientNotifForwarder {
    public RMINotifClient(ClassLoader param1ClassLoader, Map<String, ?> param1Map) { super(param1ClassLoader, param1Map); }
    
    protected NotificationResult fetchNotifs(long param1Long1, int param1Int, long param1Long2) throws IOException, ClassNotFoundException {
      boolean bool = false;
      while (true) {
        try {
          return RMIConnector.this.connection.fetchNotifications(param1Long1, param1Int, param1Long2);
        } catch (IOException iOException) {
          rethrowDeserializationException(iOException);
          try {
            RMIConnector.this.communicatorAdmin.gotIOException(iOException);
          } catch (IOException iOException1) {
            boolean bool1 = false;
            synchronized (this) {
              if (RMIConnector.this.terminated)
                throw iOException; 
              if (bool)
                bool1 = true; 
            } 
            if (bool1) {
              JMXConnectionNotification jMXConnectionNotification = new JMXConnectionNotification("jmx.remote.connection.failed", this, RMIConnector.this.connectionId, RMIConnector.this.clientNotifSeqNo++, "Failed to communicate with the server: " + iOException.toString(), iOException);
              RMIConnector.this.sendNotification(jMXConnectionNotification);
              try {
                RMIConnector.this.close(true);
              } catch (Exception exception) {}
              throw iOException;
            } 
            bool = true;
          } 
        } 
      } 
    }
    
    private void rethrowDeserializationException(IOException param1IOException) throws IOException {
      if (param1IOException instanceof java.rmi.UnmarshalException)
        throw param1IOException; 
      if (param1IOException instanceof MarshalException) {
        MarshalException marshalException = (MarshalException)param1IOException;
        if (marshalException.detail instanceof NotSerializableException)
          throw (NotSerializableException)marshalException.detail; 
      } 
    }
    
    protected Integer addListenerForMBeanRemovedNotif() throws IOException, InstanceNotFoundException {
      Integer[] arrayOfInteger;
      NotificationFilterSupport notificationFilterSupport = new NotificationFilterSupport();
      notificationFilterSupport.enableType("JMX.mbean.unregistered");
      MarshalledObject marshalledObject = new MarshalledObject(notificationFilterSupport);
      ObjectName[] arrayOfObjectName = { MBeanServerDelegate.DELEGATE_NAME };
      MarshalledObject[] arrayOfMarshalledObject = (MarshalledObject[])Util.cast(new MarshalledObject[] { marshalledObject });
      Subject[] arrayOfSubject = { null };
      try {
        arrayOfInteger = RMIConnector.this.connection.addNotificationListeners(arrayOfObjectName, arrayOfMarshalledObject, arrayOfSubject);
      } catch (IOException iOException) {
        RMIConnector.this.communicatorAdmin.gotIOException(iOException);
        arrayOfInteger = RMIConnector.this.connection.addNotificationListeners(arrayOfObjectName, arrayOfMarshalledObject, arrayOfSubject);
      } 
      return arrayOfInteger[0];
    }
    
    protected void removeListenerForMBeanRemovedNotif(Integer param1Integer) throws IOException, InstanceNotFoundException, ListenerNotFoundException {
      try {
        RMIConnector.this.connection.removeNotificationListeners(MBeanServerDelegate.DELEGATE_NAME, new Integer[] { param1Integer }, null);
      } catch (IOException iOException) {
        RMIConnector.this.communicatorAdmin.gotIOException(iOException);
        RMIConnector.this.connection.removeNotificationListeners(MBeanServerDelegate.DELEGATE_NAME, new Integer[] { param1Integer }, null);
      } 
    }
    
    protected void lostNotifs(String param1String, long param1Long) {
      JMXConnectionNotification jMXConnectionNotification = new JMXConnectionNotification("jmx.remote.connection.notifs.lost", RMIConnector.this, RMIConnector.this.connectionId, RMIConnector.this.clientNotifCounter++, param1String, Long.valueOf(param1Long));
      RMIConnector.this.sendNotification(jMXConnectionNotification);
    }
  }
  
  private class RemoteMBeanServerConnection implements MBeanServerConnection {
    private Subject delegationSubject;
    
    public RemoteMBeanServerConnection(RMIConnector this$0) { this(null); }
    
    public RemoteMBeanServerConnection(Subject param1Subject) { this.delegationSubject = param1Subject; }
    
    public ObjectInstance createMBean(String param1String, ObjectName param1ObjectName) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException {
      if (logger.debugOn())
        logger.debug("createMBean(String,ObjectName)", "className=" + param1String + ", name=" + param1ObjectName); 
      classLoader = RMIConnector.this.pushDefaultClassLoader();
      try {
        return RMIConnector.this.connection.createMBean(param1String, param1ObjectName, this.delegationSubject);
      } catch (IOException iOException) {
        RMIConnector.this.communicatorAdmin.gotIOException(iOException);
        return RMIConnector.this.connection.createMBean(param1String, param1ObjectName, this.delegationSubject);
      } finally {
        RMIConnector.this.popDefaultClassLoader(classLoader);
      } 
    }
    
    public ObjectInstance createMBean(String param1String, ObjectName param1ObjectName1, ObjectName param1ObjectName2) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException {
      if (logger.debugOn())
        logger.debug("createMBean(String,ObjectName,ObjectName)", "className=" + param1String + ", name=" + param1ObjectName1 + ", loaderName=" + param1ObjectName2 + ")"); 
      classLoader = RMIConnector.this.pushDefaultClassLoader();
      try {
        return RMIConnector.this.connection.createMBean(param1String, param1ObjectName1, param1ObjectName2, this.delegationSubject);
      } catch (IOException iOException) {
        RMIConnector.this.communicatorAdmin.gotIOException(iOException);
        return RMIConnector.this.connection.createMBean(param1String, param1ObjectName1, param1ObjectName2, this.delegationSubject);
      } finally {
        RMIConnector.this.popDefaultClassLoader(classLoader);
      } 
    }
    
    public ObjectInstance createMBean(String param1String, ObjectName param1ObjectName, Object[] param1ArrayOfObject, String[] param1ArrayOfString) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException {
      if (logger.debugOn())
        logger.debug("createMBean(String,ObjectName,Object[],String[])", "className=" + param1String + ", name=" + param1ObjectName + ", signature=" + RMIConnector.strings(param1ArrayOfString)); 
      MarshalledObject marshalledObject = new MarshalledObject(param1ArrayOfObject);
      classLoader = RMIConnector.this.pushDefaultClassLoader();
      try {
        return RMIConnector.this.connection.createMBean(param1String, param1ObjectName, marshalledObject, param1ArrayOfString, this.delegationSubject);
      } catch (IOException iOException) {
        RMIConnector.this.communicatorAdmin.gotIOException(iOException);
        return RMIConnector.this.connection.createMBean(param1String, param1ObjectName, marshalledObject, param1ArrayOfString, this.delegationSubject);
      } finally {
        RMIConnector.this.popDefaultClassLoader(classLoader);
      } 
    }
    
    public ObjectInstance createMBean(String param1String, ObjectName param1ObjectName1, ObjectName param1ObjectName2, Object[] param1ArrayOfObject, String[] param1ArrayOfString) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException {
      if (logger.debugOn())
        logger.debug("createMBean(String,ObjectName,ObjectName,Object[],String[])", "className=" + param1String + ", name=" + param1ObjectName1 + ", loaderName=" + param1ObjectName2 + ", signature=" + RMIConnector.strings(param1ArrayOfString)); 
      MarshalledObject marshalledObject = new MarshalledObject(param1ArrayOfObject);
      classLoader = RMIConnector.this.pushDefaultClassLoader();
      try {
        return RMIConnector.this.connection.createMBean(param1String, param1ObjectName1, param1ObjectName2, marshalledObject, param1ArrayOfString, this.delegationSubject);
      } catch (IOException iOException) {
        RMIConnector.this.communicatorAdmin.gotIOException(iOException);
        return RMIConnector.this.connection.createMBean(param1String, param1ObjectName1, param1ObjectName2, marshalledObject, param1ArrayOfString, this.delegationSubject);
      } finally {
        RMIConnector.this.popDefaultClassLoader(classLoader);
      } 
    }
    
    public void unregisterMBean(ObjectName param1ObjectName) throws InstanceNotFoundException, MBeanRegistrationException, IOException {
      if (logger.debugOn())
        logger.debug("unregisterMBean", "name=" + param1ObjectName); 
      classLoader = RMIConnector.this.pushDefaultClassLoader();
      try {
        RMIConnector.this.connection.unregisterMBean(param1ObjectName, this.delegationSubject);
      } catch (IOException iOException) {
        RMIConnector.this.communicatorAdmin.gotIOException(iOException);
        RMIConnector.this.connection.unregisterMBean(param1ObjectName, this.delegationSubject);
      } finally {
        RMIConnector.this.popDefaultClassLoader(classLoader);
      } 
    }
    
    public ObjectInstance getObjectInstance(ObjectName param1ObjectName) throws InstanceNotFoundException, IOException {
      if (logger.debugOn())
        logger.debug("getObjectInstance", "name=" + param1ObjectName); 
      classLoader = RMIConnector.this.pushDefaultClassLoader();
      try {
        return RMIConnector.this.connection.getObjectInstance(param1ObjectName, this.delegationSubject);
      } catch (IOException iOException) {
        RMIConnector.this.communicatorAdmin.gotIOException(iOException);
        return RMIConnector.this.connection.getObjectInstance(param1ObjectName, this.delegationSubject);
      } finally {
        RMIConnector.this.popDefaultClassLoader(classLoader);
      } 
    }
    
    public Set<ObjectInstance> queryMBeans(ObjectName param1ObjectName, QueryExp param1QueryExp) throws IOException {
      if (logger.debugOn())
        logger.debug("queryMBeans", "name=" + param1ObjectName + ", query=" + param1QueryExp); 
      MarshalledObject marshalledObject = new MarshalledObject(param1QueryExp);
      classLoader = RMIConnector.this.pushDefaultClassLoader();
      try {
        return RMIConnector.this.connection.queryMBeans(param1ObjectName, marshalledObject, this.delegationSubject);
      } catch (IOException iOException) {
        RMIConnector.this.communicatorAdmin.gotIOException(iOException);
        return RMIConnector.this.connection.queryMBeans(param1ObjectName, marshalledObject, this.delegationSubject);
      } finally {
        RMIConnector.this.popDefaultClassLoader(classLoader);
      } 
    }
    
    public Set<ObjectName> queryNames(ObjectName param1ObjectName, QueryExp param1QueryExp) throws IOException {
      if (logger.debugOn())
        logger.debug("queryNames", "name=" + param1ObjectName + ", query=" + param1QueryExp); 
      MarshalledObject marshalledObject = new MarshalledObject(param1QueryExp);
      classLoader = RMIConnector.this.pushDefaultClassLoader();
      try {
        return RMIConnector.this.connection.queryNames(param1ObjectName, marshalledObject, this.delegationSubject);
      } catch (IOException iOException) {
        RMIConnector.this.communicatorAdmin.gotIOException(iOException);
        return RMIConnector.this.connection.queryNames(param1ObjectName, marshalledObject, this.delegationSubject);
      } finally {
        RMIConnector.this.popDefaultClassLoader(classLoader);
      } 
    }
    
    public boolean isRegistered(ObjectName param1ObjectName) throws IOException {
      if (logger.debugOn())
        logger.debug("isRegistered", "name=" + param1ObjectName); 
      classLoader = RMIConnector.this.pushDefaultClassLoader();
      try {
        return RMIConnector.this.connection.isRegistered(param1ObjectName, this.delegationSubject);
      } catch (IOException iOException) {
        RMIConnector.this.communicatorAdmin.gotIOException(iOException);
        return RMIConnector.this.connection.isRegistered(param1ObjectName, this.delegationSubject);
      } finally {
        RMIConnector.this.popDefaultClassLoader(classLoader);
      } 
    }
    
    public Integer getMBeanCount() throws IOException, InstanceNotFoundException {
      if (logger.debugOn())
        logger.debug("getMBeanCount", ""); 
      classLoader = RMIConnector.this.pushDefaultClassLoader();
      try {
        return RMIConnector.this.connection.getMBeanCount(this.delegationSubject);
      } catch (IOException iOException) {
        RMIConnector.this.communicatorAdmin.gotIOException(iOException);
        return RMIConnector.this.connection.getMBeanCount(this.delegationSubject);
      } finally {
        RMIConnector.this.popDefaultClassLoader(classLoader);
      } 
    }
    
    public Object getAttribute(ObjectName param1ObjectName, String param1String) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, IOException {
      if (logger.debugOn())
        logger.debug("getAttribute", "name=" + param1ObjectName + ", attribute=" + param1String); 
      classLoader = RMIConnector.this.pushDefaultClassLoader();
      try {
        return RMIConnector.this.connection.getAttribute(param1ObjectName, param1String, this.delegationSubject);
      } catch (IOException iOException) {
        RMIConnector.this.communicatorAdmin.gotIOException(iOException);
        return RMIConnector.this.connection.getAttribute(param1ObjectName, param1String, this.delegationSubject);
      } finally {
        RMIConnector.this.popDefaultClassLoader(classLoader);
      } 
    }
    
    public AttributeList getAttributes(ObjectName param1ObjectName, String[] param1ArrayOfString) throws InstanceNotFoundException, ReflectionException, IOException {
      if (logger.debugOn())
        logger.debug("getAttributes", "name=" + param1ObjectName + ", attributes=" + RMIConnector.strings(param1ArrayOfString)); 
      classLoader = RMIConnector.this.pushDefaultClassLoader();
      try {
        return RMIConnector.this.connection.getAttributes(param1ObjectName, param1ArrayOfString, this.delegationSubject);
      } catch (IOException iOException) {
        RMIConnector.this.communicatorAdmin.gotIOException(iOException);
        return RMIConnector.this.connection.getAttributes(param1ObjectName, param1ArrayOfString, this.delegationSubject);
      } finally {
        RMIConnector.this.popDefaultClassLoader(classLoader);
      } 
    }
    
    public void setAttribute(ObjectName param1ObjectName, Attribute param1Attribute) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException, IOException {
      if (logger.debugOn())
        logger.debug("setAttribute", "name=" + param1ObjectName + ", attribute name=" + param1Attribute.getName()); 
      MarshalledObject marshalledObject = new MarshalledObject(param1Attribute);
      classLoader = RMIConnector.this.pushDefaultClassLoader();
      try {
        RMIConnector.this.connection.setAttribute(param1ObjectName, marshalledObject, this.delegationSubject);
      } catch (IOException iOException) {
        RMIConnector.this.communicatorAdmin.gotIOException(iOException);
        RMIConnector.this.connection.setAttribute(param1ObjectName, marshalledObject, this.delegationSubject);
      } finally {
        RMIConnector.this.popDefaultClassLoader(classLoader);
      } 
    }
    
    public AttributeList setAttributes(ObjectName param1ObjectName, AttributeList param1AttributeList) throws InstanceNotFoundException, ReflectionException, IOException {
      if (logger.debugOn())
        logger.debug("setAttributes", "name=" + param1ObjectName + ", attribute names=" + RMIConnector.getAttributesNames(param1AttributeList)); 
      MarshalledObject marshalledObject = new MarshalledObject(param1AttributeList);
      classLoader = RMIConnector.this.pushDefaultClassLoader();
      try {
        return RMIConnector.this.connection.setAttributes(param1ObjectName, marshalledObject, this.delegationSubject);
      } catch (IOException iOException) {
        RMIConnector.this.communicatorAdmin.gotIOException(iOException);
        return RMIConnector.this.connection.setAttributes(param1ObjectName, marshalledObject, this.delegationSubject);
      } finally {
        RMIConnector.this.popDefaultClassLoader(classLoader);
      } 
    }
    
    public Object invoke(ObjectName param1ObjectName, String param1String, Object[] param1ArrayOfObject, String[] param1ArrayOfString) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException {
      if (logger.debugOn())
        logger.debug("invoke", "name=" + param1ObjectName + ", operationName=" + param1String + ", signature=" + RMIConnector.strings(param1ArrayOfString)); 
      MarshalledObject marshalledObject = new MarshalledObject(param1ArrayOfObject);
      classLoader = RMIConnector.this.pushDefaultClassLoader();
      try {
        return RMIConnector.this.connection.invoke(param1ObjectName, param1String, marshalledObject, param1ArrayOfString, this.delegationSubject);
      } catch (IOException iOException) {
        RMIConnector.this.communicatorAdmin.gotIOException(iOException);
        return RMIConnector.this.connection.invoke(param1ObjectName, param1String, marshalledObject, param1ArrayOfString, this.delegationSubject);
      } finally {
        RMIConnector.this.popDefaultClassLoader(classLoader);
      } 
    }
    
    public String getDefaultDomain() {
      if (logger.debugOn())
        logger.debug("getDefaultDomain", ""); 
      classLoader = RMIConnector.this.pushDefaultClassLoader();
      try {
        return RMIConnector.this.connection.getDefaultDomain(this.delegationSubject);
      } catch (IOException iOException) {
        RMIConnector.this.communicatorAdmin.gotIOException(iOException);
        return RMIConnector.this.connection.getDefaultDomain(this.delegationSubject);
      } finally {
        RMIConnector.this.popDefaultClassLoader(classLoader);
      } 
    }
    
    public String[] getDomains() throws IOException {
      if (logger.debugOn())
        logger.debug("getDomains", ""); 
      classLoader = RMIConnector.this.pushDefaultClassLoader();
      try {
        return RMIConnector.this.connection.getDomains(this.delegationSubject);
      } catch (IOException iOException) {
        RMIConnector.this.communicatorAdmin.gotIOException(iOException);
        return RMIConnector.this.connection.getDomains(this.delegationSubject);
      } finally {
        RMIConnector.this.popDefaultClassLoader(classLoader);
      } 
    }
    
    public MBeanInfo getMBeanInfo(ObjectName param1ObjectName) throws InstanceNotFoundException, IntrospectionException, ReflectionException, IOException {
      if (logger.debugOn())
        logger.debug("getMBeanInfo", "name=" + param1ObjectName); 
      classLoader = RMIConnector.this.pushDefaultClassLoader();
      try {
        return RMIConnector.this.connection.getMBeanInfo(param1ObjectName, this.delegationSubject);
      } catch (IOException iOException) {
        RMIConnector.this.communicatorAdmin.gotIOException(iOException);
        return RMIConnector.this.connection.getMBeanInfo(param1ObjectName, this.delegationSubject);
      } finally {
        RMIConnector.this.popDefaultClassLoader(classLoader);
      } 
    }
    
    public boolean isInstanceOf(ObjectName param1ObjectName, String param1String) throws InstanceNotFoundException, IOException {
      if (logger.debugOn())
        logger.debug("isInstanceOf", "name=" + param1ObjectName + ", className=" + param1String); 
      classLoader = RMIConnector.this.pushDefaultClassLoader();
      try {
        return RMIConnector.this.connection.isInstanceOf(param1ObjectName, param1String, this.delegationSubject);
      } catch (IOException iOException) {
        RMIConnector.this.communicatorAdmin.gotIOException(iOException);
        return RMIConnector.this.connection.isInstanceOf(param1ObjectName, param1String, this.delegationSubject);
      } finally {
        RMIConnector.this.popDefaultClassLoader(classLoader);
      } 
    }
    
    public void addNotificationListener(ObjectName param1ObjectName1, ObjectName param1ObjectName2, NotificationFilter param1NotificationFilter, Object param1Object) throws InstanceNotFoundException, IOException {
      if (logger.debugOn())
        logger.debug("addNotificationListener(ObjectName,ObjectName,NotificationFilter,Object)", "name=" + param1ObjectName1 + ", listener=" + param1ObjectName2 + ", filter=" + param1NotificationFilter + ", handback=" + param1Object); 
      MarshalledObject marshalledObject1 = new MarshalledObject(param1NotificationFilter);
      MarshalledObject marshalledObject2 = new MarshalledObject(param1Object);
      classLoader = RMIConnector.this.pushDefaultClassLoader();
      try {
        RMIConnector.this.connection.addNotificationListener(param1ObjectName1, param1ObjectName2, marshalledObject1, marshalledObject2, this.delegationSubject);
      } catch (IOException iOException) {
        RMIConnector.this.communicatorAdmin.gotIOException(iOException);
        RMIConnector.this.connection.addNotificationListener(param1ObjectName1, param1ObjectName2, marshalledObject1, marshalledObject2, this.delegationSubject);
      } finally {
        RMIConnector.this.popDefaultClassLoader(classLoader);
      } 
    }
    
    public void removeNotificationListener(ObjectName param1ObjectName1, ObjectName param1ObjectName2) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
      if (logger.debugOn())
        logger.debug("removeNotificationListener(ObjectName,ObjectName)", "name=" + param1ObjectName1 + ", listener=" + param1ObjectName2); 
      classLoader = RMIConnector.this.pushDefaultClassLoader();
      try {
        RMIConnector.this.connection.removeNotificationListener(param1ObjectName1, param1ObjectName2, this.delegationSubject);
      } catch (IOException iOException) {
        RMIConnector.this.communicatorAdmin.gotIOException(iOException);
        RMIConnector.this.connection.removeNotificationListener(param1ObjectName1, param1ObjectName2, this.delegationSubject);
      } finally {
        RMIConnector.this.popDefaultClassLoader(classLoader);
      } 
    }
    
    public void removeNotificationListener(ObjectName param1ObjectName1, ObjectName param1ObjectName2, NotificationFilter param1NotificationFilter, Object param1Object) throws InstanceNotFoundException, IOException {
      if (logger.debugOn())
        logger.debug("removeNotificationListener(ObjectName,ObjectName,NotificationFilter,Object)", "name=" + param1ObjectName1 + ", listener=" + param1ObjectName2 + ", filter=" + param1NotificationFilter + ", handback=" + param1Object); 
      MarshalledObject marshalledObject1 = new MarshalledObject(param1NotificationFilter);
      MarshalledObject marshalledObject2 = new MarshalledObject(param1Object);
      classLoader = RMIConnector.this.pushDefaultClassLoader();
      try {
        RMIConnector.this.connection.removeNotificationListener(param1ObjectName1, param1ObjectName2, marshalledObject1, marshalledObject2, this.delegationSubject);
      } catch (IOException iOException) {
        RMIConnector.this.communicatorAdmin.gotIOException(iOException);
        RMIConnector.this.connection.removeNotificationListener(param1ObjectName1, param1ObjectName2, marshalledObject1, marshalledObject2, this.delegationSubject);
      } finally {
        RMIConnector.this.popDefaultClassLoader(classLoader);
      } 
    }
    
    public void addNotificationListener(ObjectName param1ObjectName, NotificationListener param1NotificationListener, NotificationFilter param1NotificationFilter, Object param1Object) throws InstanceNotFoundException, IOException {
      boolean bool = logger.debugOn();
      if (bool)
        logger.debug("addNotificationListener(ObjectName,NotificationListener,NotificationFilter,Object)", "name=" + param1ObjectName + ", listener=" + param1NotificationListener + ", filter=" + param1NotificationFilter + ", handback=" + param1Object); 
      Integer integer = RMIConnector.this.addListenerWithSubject(param1ObjectName, new MarshalledObject(param1NotificationFilter), this.delegationSubject, true);
      RMIConnector.this.rmiNotifClient.addNotificationListener(integer, param1ObjectName, param1NotificationListener, param1NotificationFilter, param1Object, this.delegationSubject);
    }
    
    public void removeNotificationListener(ObjectName param1ObjectName, NotificationListener param1NotificationListener) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
      boolean bool = logger.debugOn();
      if (bool)
        logger.debug("removeNotificationListener(ObjectName,NotificationListener)", "name=" + param1ObjectName + ", listener=" + param1NotificationListener); 
      Integer[] arrayOfInteger = RMIConnector.this.rmiNotifClient.removeNotificationListener(param1ObjectName, param1NotificationListener);
      if (bool)
        logger.debug("removeNotificationListener", "listenerIDs=" + RMIConnector.objects(arrayOfInteger)); 
      classLoader = RMIConnector.this.pushDefaultClassLoader();
      try {
        RMIConnector.this.connection.removeNotificationListeners(param1ObjectName, arrayOfInteger, this.delegationSubject);
      } catch (IOException iOException) {
        RMIConnector.this.communicatorAdmin.gotIOException(iOException);
        RMIConnector.this.connection.removeNotificationListeners(param1ObjectName, arrayOfInteger, this.delegationSubject);
      } finally {
        RMIConnector.this.popDefaultClassLoader(classLoader);
      } 
    }
    
    public void removeNotificationListener(ObjectName param1ObjectName, NotificationListener param1NotificationListener, NotificationFilter param1NotificationFilter, Object param1Object) throws InstanceNotFoundException, IOException {
      boolean bool = logger.debugOn();
      if (bool)
        logger.debug("removeNotificationListener(ObjectName,NotificationListener,NotificationFilter,Object)", "name=" + param1ObjectName + ", listener=" + param1NotificationListener + ", filter=" + param1NotificationFilter + ", handback=" + param1Object); 
      Integer integer = RMIConnector.this.rmiNotifClient.removeNotificationListener(param1ObjectName, param1NotificationListener, param1NotificationFilter, param1Object);
      if (bool)
        logger.debug("removeNotificationListener", "listenerID=" + integer); 
      classLoader = RMIConnector.this.pushDefaultClassLoader();
      try {
        RMIConnector.this.connection.removeNotificationListeners(param1ObjectName, new Integer[] { integer }, this.delegationSubject);
      } catch (IOException iOException) {
        RMIConnector.this.communicatorAdmin.gotIOException(iOException);
        RMIConnector.this.connection.removeNotificationListeners(param1ObjectName, new Integer[] { integer }, this.delegationSubject);
      } finally {
        RMIConnector.this.popDefaultClassLoader(classLoader);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\remote\rmi\RMIConnector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */