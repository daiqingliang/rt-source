package javax.management.remote.rmi;

import com.sun.jmx.mbeanserver.Util;
import com.sun.jmx.remote.internal.ServerCommunicatorAdmin;
import com.sun.jmx.remote.internal.ServerNotifForwarder;
import com.sun.jmx.remote.security.JMXSubjectDomainCombiner;
import com.sun.jmx.remote.security.SubjectDelegator;
import com.sun.jmx.remote.util.ClassLoaderWithRepository;
import com.sun.jmx.remote.util.ClassLogger;
import com.sun.jmx.remote.util.EnvHelp;
import com.sun.jmx.remote.util.OrderClassLoaders;
import java.io.IOException;
import java.rmi.MarshalledObject;
import java.rmi.UnmarshalException;
import java.rmi.server.Unreferenced;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Permission;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
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
import javax.management.MBeanPermission;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationFilter;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import javax.management.loading.ClassLoaderRepository;
import javax.management.remote.JMXServerErrorException;
import javax.management.remote.NotificationResult;
import javax.security.auth.Subject;
import sun.reflect.misc.ReflectUtil;

public class RMIConnectionImpl implements RMIConnection, Unreferenced {
  private static final Object[] NO_OBJECTS = new Object[0];
  
  private static final String[] NO_STRINGS = new String[0];
  
  private final Subject subject;
  
  private final SubjectDelegator subjectDelegator;
  
  private final boolean removeCallerContext;
  
  private final AccessControlContext acc;
  
  private final RMIServerImpl rmiServer;
  
  private final MBeanServer mbeanServer;
  
  private final ClassLoader defaultClassLoader;
  
  private final ClassLoader defaultContextClassLoader;
  
  private final ClassLoaderWithRepository classLoaderWithRepository;
  
  private boolean terminated = false;
  
  private final String connectionId;
  
  private final ServerCommunicatorAdmin serverCommunicatorAdmin;
  
  private static final int ADD_NOTIFICATION_LISTENERS = 1;
  
  private static final int ADD_NOTIFICATION_LISTENER_OBJECTNAME = 2;
  
  private static final int CREATE_MBEAN = 3;
  
  private static final int CREATE_MBEAN_PARAMS = 4;
  
  private static final int CREATE_MBEAN_LOADER = 5;
  
  private static final int CREATE_MBEAN_LOADER_PARAMS = 6;
  
  private static final int GET_ATTRIBUTE = 7;
  
  private static final int GET_ATTRIBUTES = 8;
  
  private static final int GET_DEFAULT_DOMAIN = 9;
  
  private static final int GET_DOMAINS = 10;
  
  private static final int GET_MBEAN_COUNT = 11;
  
  private static final int GET_MBEAN_INFO = 12;
  
  private static final int GET_OBJECT_INSTANCE = 13;
  
  private static final int INVOKE = 14;
  
  private static final int IS_INSTANCE_OF = 15;
  
  private static final int IS_REGISTERED = 16;
  
  private static final int QUERY_MBEANS = 17;
  
  private static final int QUERY_NAMES = 18;
  
  private static final int REMOVE_NOTIFICATION_LISTENER = 19;
  
  private static final int REMOVE_NOTIFICATION_LISTENER_OBJECTNAME = 20;
  
  private static final int REMOVE_NOTIFICATION_LISTENER_OBJECTNAME_FILTER_HANDBACK = 21;
  
  private static final int SET_ATTRIBUTE = 22;
  
  private static final int SET_ATTRIBUTES = 23;
  
  private static final int UNREGISTER_MBEAN = 24;
  
  private ServerNotifForwarder serverNotifForwarder;
  
  private Map<String, ?> env;
  
  private static final ClassLogger logger = new ClassLogger("javax.management.remote.rmi", "RMIConnectionImpl");
  
  public RMIConnectionImpl(RMIServerImpl paramRMIServerImpl, String paramString, ClassLoader paramClassLoader, Subject paramSubject, Map<String, ?> paramMap) {
    if (paramRMIServerImpl == null || paramString == null)
      throw new NullPointerException("Illegal null argument"); 
    if (paramMap == null)
      paramMap = Collections.emptyMap(); 
    this.rmiServer = paramRMIServerImpl;
    this.connectionId = paramString;
    this.defaultClassLoader = paramClassLoader;
    this.subjectDelegator = new SubjectDelegator();
    this.subject = paramSubject;
    if (paramSubject == null) {
      this.acc = null;
      this.removeCallerContext = false;
    } else {
      this.removeCallerContext = SubjectDelegator.checkRemoveCallerContext(paramSubject);
      if (this.removeCallerContext) {
        this.acc = JMXSubjectDomainCombiner.getDomainCombinerContext(paramSubject);
      } else {
        this.acc = JMXSubjectDomainCombiner.getContext(paramSubject);
      } 
    } 
    this.mbeanServer = paramRMIServerImpl.getMBeanServer();
    final ClassLoader dcl = paramClassLoader;
    final ClassLoaderRepository repository = (ClassLoaderRepository)AccessController.doPrivileged(new PrivilegedAction<ClassLoaderRepository>() {
          public ClassLoaderRepository run() { return RMIConnectionImpl.this.mbeanServer.getClassLoaderRepository(); }
        },  withPermissions(new Permission[] { new MBeanPermission("*", "getClassLoaderRepository") }));
    this.classLoaderWithRepository = (ClassLoaderWithRepository)AccessController.doPrivileged(new PrivilegedAction<ClassLoaderWithRepository>() {
          public ClassLoaderWithRepository run() { return new ClassLoaderWithRepository(repository, dcl); }
        }withPermissions(new Permission[] { new RuntimePermission("createClassLoader") }));
    this.defaultContextClassLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
          public ClassLoader run() { return new RMIConnectionImpl.CombinedClassLoader(Thread.currentThread().getContextClassLoader(), dcl, null); }
        });
    this.serverCommunicatorAdmin = new RMIServerCommunicatorAdmin(EnvHelp.getServerConnectionTimeout(paramMap));
    this.env = paramMap;
  }
  
  private static AccessControlContext withPermissions(Permission... paramVarArgs) {
    Permissions permissions = new Permissions();
    for (Permission permission : paramVarArgs)
      permissions.add(permission); 
    ProtectionDomain protectionDomain = new ProtectionDomain(null, permissions);
    return new AccessControlContext(new ProtectionDomain[] { protectionDomain });
  }
  
  private ServerNotifForwarder getServerNotifFwd() {
    if (this.serverNotifForwarder == null)
      this.serverNotifForwarder = new ServerNotifForwarder(this.mbeanServer, this.env, this.rmiServer.getNotifBuffer(), this.connectionId); 
    return this.serverNotifForwarder;
  }
  
  public String getConnectionId() throws IOException { return this.connectionId; }
  
  public void close() throws IOException {
    boolean bool = logger.debugOn();
    String str = bool ? ("[" + toString() + "]") : null;
    synchronized (this) {
      if (this.terminated) {
        if (bool)
          logger.debug("close", str + " already terminated."); 
        return;
      } 
      if (bool)
        logger.debug("close", str + " closing."); 
      this.terminated = true;
      if (this.serverCommunicatorAdmin != null)
        this.serverCommunicatorAdmin.terminate(); 
      if (this.serverNotifForwarder != null)
        this.serverNotifForwarder.terminate(); 
    } 
    this.rmiServer.clientClosed(this);
    if (bool)
      logger.debug("close", str + " closed."); 
  }
  
  public void unreferenced() throws IOException {
    logger.debug("unreferenced", "called");
    try {
      close();
      logger.debug("unreferenced", "done");
    } catch (IOException iOException) {
      logger.fine("unreferenced", iOException);
    } 
  }
  
  public ObjectInstance createMBean(String paramString, ObjectName paramObjectName, Subject paramSubject) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException {
    try {
      Object[] arrayOfObject = { paramString, paramObjectName };
      if (logger.debugOn())
        logger.debug("createMBean(String,ObjectName)", "connectionId=" + this.connectionId + ", className=" + paramString + ", name=" + paramObjectName); 
      return (ObjectInstance)doPrivilegedOperation(3, arrayOfObject, paramSubject);
    } catch (PrivilegedActionException privilegedActionException) {
      Exception exception = extractException(privilegedActionException);
      if (exception instanceof ReflectionException)
        throw (ReflectionException)exception; 
      if (exception instanceof InstanceAlreadyExistsException)
        throw (InstanceAlreadyExistsException)exception; 
      if (exception instanceof MBeanRegistrationException)
        throw (MBeanRegistrationException)exception; 
      if (exception instanceof MBeanException)
        throw (MBeanException)exception; 
      if (exception instanceof NotCompliantMBeanException)
        throw (NotCompliantMBeanException)exception; 
      if (exception instanceof IOException)
        throw (IOException)exception; 
      throw newIOException("Got unexpected server exception: " + exception, exception);
    } 
  }
  
  public ObjectInstance createMBean(String paramString, ObjectName paramObjectName1, ObjectName paramObjectName2, Subject paramSubject) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException {
    try {
      Object[] arrayOfObject = { paramString, paramObjectName1, paramObjectName2 };
      if (logger.debugOn())
        logger.debug("createMBean(String,ObjectName,ObjectName)", "connectionId=" + this.connectionId + ", className=" + paramString + ", name=" + paramObjectName1 + ", loaderName=" + paramObjectName2); 
      return (ObjectInstance)doPrivilegedOperation(5, arrayOfObject, paramSubject);
    } catch (PrivilegedActionException privilegedActionException) {
      Exception exception = extractException(privilegedActionException);
      if (exception instanceof ReflectionException)
        throw (ReflectionException)exception; 
      if (exception instanceof InstanceAlreadyExistsException)
        throw (InstanceAlreadyExistsException)exception; 
      if (exception instanceof MBeanRegistrationException)
        throw (MBeanRegistrationException)exception; 
      if (exception instanceof MBeanException)
        throw (MBeanException)exception; 
      if (exception instanceof NotCompliantMBeanException)
        throw (NotCompliantMBeanException)exception; 
      if (exception instanceof InstanceNotFoundException)
        throw (InstanceNotFoundException)exception; 
      if (exception instanceof IOException)
        throw (IOException)exception; 
      throw newIOException("Got unexpected server exception: " + exception, exception);
    } 
  }
  
  public ObjectInstance createMBean(String paramString, ObjectName paramObjectName, MarshalledObject paramMarshalledObject, String[] paramArrayOfString, Subject paramSubject) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException {
    boolean bool = logger.debugOn();
    if (bool)
      logger.debug("createMBean(String,ObjectName,Object[],String[])", "connectionId=" + this.connectionId + ", unwrapping parameters using classLoaderWithRepository."); 
    Object[] arrayOfObject = nullIsEmpty((Object[])unwrap(paramMarshalledObject, this.classLoaderWithRepository, Object[].class, paramSubject));
    try {
      Object[] arrayOfObject1 = { paramString, paramObjectName, arrayOfObject, nullIsEmpty(paramArrayOfString) };
      if (bool)
        logger.debug("createMBean(String,ObjectName,Object[],String[])", "connectionId=" + this.connectionId + ", className=" + paramString + ", name=" + paramObjectName + ", signature=" + strings(paramArrayOfString)); 
      return (ObjectInstance)doPrivilegedOperation(4, arrayOfObject1, paramSubject);
    } catch (PrivilegedActionException privilegedActionException) {
      Exception exception = extractException(privilegedActionException);
      if (exception instanceof ReflectionException)
        throw (ReflectionException)exception; 
      if (exception instanceof InstanceAlreadyExistsException)
        throw (InstanceAlreadyExistsException)exception; 
      if (exception instanceof MBeanRegistrationException)
        throw (MBeanRegistrationException)exception; 
      if (exception instanceof MBeanException)
        throw (MBeanException)exception; 
      if (exception instanceof NotCompliantMBeanException)
        throw (NotCompliantMBeanException)exception; 
      if (exception instanceof IOException)
        throw (IOException)exception; 
      throw newIOException("Got unexpected server exception: " + exception, exception);
    } 
  }
  
  public ObjectInstance createMBean(String paramString, ObjectName paramObjectName1, ObjectName paramObjectName2, MarshalledObject paramMarshalledObject, String[] paramArrayOfString, Subject paramSubject) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException {
    boolean bool = logger.debugOn();
    if (bool)
      logger.debug("createMBean(String,ObjectName,ObjectName,Object[],String[])", "connectionId=" + this.connectionId + ", unwrapping params with MBean extended ClassLoader."); 
    Object[] arrayOfObject = nullIsEmpty((Object[])unwrap(paramMarshalledObject, getClassLoader(paramObjectName2), this.defaultClassLoader, Object[].class, paramSubject));
    try {
      Object[] arrayOfObject1 = { paramString, paramObjectName1, paramObjectName2, arrayOfObject, nullIsEmpty(paramArrayOfString) };
      if (bool)
        logger.debug("createMBean(String,ObjectName,ObjectName,Object[],String[])", "connectionId=" + this.connectionId + ", className=" + paramString + ", name=" + paramObjectName1 + ", loaderName=" + paramObjectName2 + ", signature=" + strings(paramArrayOfString)); 
      return (ObjectInstance)doPrivilegedOperation(6, arrayOfObject1, paramSubject);
    } catch (PrivilegedActionException privilegedActionException) {
      Exception exception = extractException(privilegedActionException);
      if (exception instanceof ReflectionException)
        throw (ReflectionException)exception; 
      if (exception instanceof InstanceAlreadyExistsException)
        throw (InstanceAlreadyExistsException)exception; 
      if (exception instanceof MBeanRegistrationException)
        throw (MBeanRegistrationException)exception; 
      if (exception instanceof MBeanException)
        throw (MBeanException)exception; 
      if (exception instanceof NotCompliantMBeanException)
        throw (NotCompliantMBeanException)exception; 
      if (exception instanceof InstanceNotFoundException)
        throw (InstanceNotFoundException)exception; 
      if (exception instanceof IOException)
        throw (IOException)exception; 
      throw newIOException("Got unexpected server exception: " + exception, exception);
    } 
  }
  
  public void unregisterMBean(ObjectName paramObjectName, Subject paramSubject) throws InstanceNotFoundException, MBeanRegistrationException, IOException {
    try {
      Object[] arrayOfObject = { paramObjectName };
      if (logger.debugOn())
        logger.debug("unregisterMBean", "connectionId=" + this.connectionId + ", name=" + paramObjectName); 
      doPrivilegedOperation(24, arrayOfObject, paramSubject);
    } catch (PrivilegedActionException privilegedActionException) {
      Exception exception = extractException(privilegedActionException);
      if (exception instanceof InstanceNotFoundException)
        throw (InstanceNotFoundException)exception; 
      if (exception instanceof MBeanRegistrationException)
        throw (MBeanRegistrationException)exception; 
      if (exception instanceof IOException)
        throw (IOException)exception; 
      throw newIOException("Got unexpected server exception: " + exception, exception);
    } 
  }
  
  public ObjectInstance getObjectInstance(ObjectName paramObjectName, Subject paramSubject) throws InstanceNotFoundException, IOException {
    checkNonNull("ObjectName", paramObjectName);
    try {
      Object[] arrayOfObject = { paramObjectName };
      if (logger.debugOn())
        logger.debug("getObjectInstance", "connectionId=" + this.connectionId + ", name=" + paramObjectName); 
      return (ObjectInstance)doPrivilegedOperation(13, arrayOfObject, paramSubject);
    } catch (PrivilegedActionException privilegedActionException) {
      Exception exception = extractException(privilegedActionException);
      if (exception instanceof InstanceNotFoundException)
        throw (InstanceNotFoundException)exception; 
      if (exception instanceof IOException)
        throw (IOException)exception; 
      throw newIOException("Got unexpected server exception: " + exception, exception);
    } 
  }
  
  public Set<ObjectInstance> queryMBeans(ObjectName paramObjectName, MarshalledObject paramMarshalledObject, Subject paramSubject) throws IOException {
    boolean bool = logger.debugOn();
    if (bool)
      logger.debug("queryMBeans", "connectionId=" + this.connectionId + " unwrapping query with defaultClassLoader."); 
    QueryExp queryExp = (QueryExp)unwrap(paramMarshalledObject, this.defaultContextClassLoader, QueryExp.class, paramSubject);
    try {
      Object[] arrayOfObject = { paramObjectName, queryExp };
      if (bool)
        logger.debug("queryMBeans", "connectionId=" + this.connectionId + ", name=" + paramObjectName + ", query=" + paramMarshalledObject); 
      return (Set)Util.cast(doPrivilegedOperation(17, arrayOfObject, paramSubject));
    } catch (PrivilegedActionException privilegedActionException) {
      Exception exception = extractException(privilegedActionException);
      if (exception instanceof IOException)
        throw (IOException)exception; 
      throw newIOException("Got unexpected server exception: " + exception, exception);
    } 
  }
  
  public Set<ObjectName> queryNames(ObjectName paramObjectName, MarshalledObject paramMarshalledObject, Subject paramSubject) throws IOException {
    boolean bool = logger.debugOn();
    if (bool)
      logger.debug("queryNames", "connectionId=" + this.connectionId + " unwrapping query with defaultClassLoader."); 
    QueryExp queryExp = (QueryExp)unwrap(paramMarshalledObject, this.defaultContextClassLoader, QueryExp.class, paramSubject);
    try {
      Object[] arrayOfObject = { paramObjectName, queryExp };
      if (bool)
        logger.debug("queryNames", "connectionId=" + this.connectionId + ", name=" + paramObjectName + ", query=" + paramMarshalledObject); 
      return (Set)Util.cast(doPrivilegedOperation(18, arrayOfObject, paramSubject));
    } catch (PrivilegedActionException privilegedActionException) {
      Exception exception = extractException(privilegedActionException);
      if (exception instanceof IOException)
        throw (IOException)exception; 
      throw newIOException("Got unexpected server exception: " + exception, exception);
    } 
  }
  
  public boolean isRegistered(ObjectName paramObjectName, Subject paramSubject) throws IOException {
    try {
      Object[] arrayOfObject = { paramObjectName };
      return ((Boolean)doPrivilegedOperation(16, arrayOfObject, paramSubject)).booleanValue();
    } catch (PrivilegedActionException privilegedActionException) {
      Exception exception = extractException(privilegedActionException);
      if (exception instanceof IOException)
        throw (IOException)exception; 
      throw newIOException("Got unexpected server exception: " + exception, exception);
    } 
  }
  
  public Integer getMBeanCount(Subject paramSubject) throws IOException {
    try {
      Object[] arrayOfObject = new Object[0];
      if (logger.debugOn())
        logger.debug("getMBeanCount", "connectionId=" + this.connectionId); 
      return (Integer)doPrivilegedOperation(11, arrayOfObject, paramSubject);
    } catch (PrivilegedActionException privilegedActionException) {
      Exception exception = extractException(privilegedActionException);
      if (exception instanceof IOException)
        throw (IOException)exception; 
      throw newIOException("Got unexpected server exception: " + exception, exception);
    } 
  }
  
  public Object getAttribute(ObjectName paramObjectName, String paramString, Subject paramSubject) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, IOException {
    try {
      Object[] arrayOfObject = { paramObjectName, paramString };
      if (logger.debugOn())
        logger.debug("getAttribute", "connectionId=" + this.connectionId + ", name=" + paramObjectName + ", attribute=" + paramString); 
      return doPrivilegedOperation(7, arrayOfObject, paramSubject);
    } catch (PrivilegedActionException privilegedActionException) {
      Exception exception = extractException(privilegedActionException);
      if (exception instanceof MBeanException)
        throw (MBeanException)exception; 
      if (exception instanceof AttributeNotFoundException)
        throw (AttributeNotFoundException)exception; 
      if (exception instanceof InstanceNotFoundException)
        throw (InstanceNotFoundException)exception; 
      if (exception instanceof ReflectionException)
        throw (ReflectionException)exception; 
      if (exception instanceof IOException)
        throw (IOException)exception; 
      throw newIOException("Got unexpected server exception: " + exception, exception);
    } 
  }
  
  public AttributeList getAttributes(ObjectName paramObjectName, String[] paramArrayOfString, Subject paramSubject) throws InstanceNotFoundException, ReflectionException, IOException {
    try {
      Object[] arrayOfObject = { paramObjectName, paramArrayOfString };
      if (logger.debugOn())
        logger.debug("getAttributes", "connectionId=" + this.connectionId + ", name=" + paramObjectName + ", attributes=" + strings(paramArrayOfString)); 
      return (AttributeList)doPrivilegedOperation(8, arrayOfObject, paramSubject);
    } catch (PrivilegedActionException privilegedActionException) {
      Exception exception = extractException(privilegedActionException);
      if (exception instanceof InstanceNotFoundException)
        throw (InstanceNotFoundException)exception; 
      if (exception instanceof ReflectionException)
        throw (ReflectionException)exception; 
      if (exception instanceof IOException)
        throw (IOException)exception; 
      throw newIOException("Got unexpected server exception: " + exception, exception);
    } 
  }
  
  public void setAttribute(ObjectName paramObjectName, MarshalledObject paramMarshalledObject, Subject paramSubject) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException, IOException {
    boolean bool = logger.debugOn();
    if (bool)
      logger.debug("setAttribute", "connectionId=" + this.connectionId + " unwrapping attribute with MBean extended ClassLoader."); 
    Attribute attribute = (Attribute)unwrap(paramMarshalledObject, getClassLoaderFor(paramObjectName), this.defaultClassLoader, Attribute.class, paramSubject);
    try {
      Object[] arrayOfObject = { paramObjectName, attribute };
      if (bool)
        logger.debug("setAttribute", "connectionId=" + this.connectionId + ", name=" + paramObjectName + ", attribute name=" + attribute.getName()); 
      doPrivilegedOperation(22, arrayOfObject, paramSubject);
    } catch (PrivilegedActionException privilegedActionException) {
      Exception exception = extractException(privilegedActionException);
      if (exception instanceof InstanceNotFoundException)
        throw (InstanceNotFoundException)exception; 
      if (exception instanceof AttributeNotFoundException)
        throw (AttributeNotFoundException)exception; 
      if (exception instanceof InvalidAttributeValueException)
        throw (InvalidAttributeValueException)exception; 
      if (exception instanceof MBeanException)
        throw (MBeanException)exception; 
      if (exception instanceof ReflectionException)
        throw (ReflectionException)exception; 
      if (exception instanceof IOException)
        throw (IOException)exception; 
      throw newIOException("Got unexpected server exception: " + exception, exception);
    } 
  }
  
  public AttributeList setAttributes(ObjectName paramObjectName, MarshalledObject paramMarshalledObject, Subject paramSubject) throws InstanceNotFoundException, ReflectionException, IOException {
    boolean bool = logger.debugOn();
    if (bool)
      logger.debug("setAttributes", "connectionId=" + this.connectionId + " unwrapping attributes with MBean extended ClassLoader."); 
    AttributeList attributeList = (AttributeList)unwrap(paramMarshalledObject, getClassLoaderFor(paramObjectName), this.defaultClassLoader, AttributeList.class, paramSubject);
    try {
      Object[] arrayOfObject = { paramObjectName, attributeList };
      if (bool)
        logger.debug("setAttributes", "connectionId=" + this.connectionId + ", name=" + paramObjectName + ", attribute names=" + RMIConnector.getAttributesNames(attributeList)); 
      return (AttributeList)doPrivilegedOperation(23, arrayOfObject, paramSubject);
    } catch (PrivilegedActionException privilegedActionException) {
      Exception exception = extractException(privilegedActionException);
      if (exception instanceof InstanceNotFoundException)
        throw (InstanceNotFoundException)exception; 
      if (exception instanceof ReflectionException)
        throw (ReflectionException)exception; 
      if (exception instanceof IOException)
        throw (IOException)exception; 
      throw newIOException("Got unexpected server exception: " + exception, exception);
    } 
  }
  
  public Object invoke(ObjectName paramObjectName, String paramString, MarshalledObject paramMarshalledObject, String[] paramArrayOfString, Subject paramSubject) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException {
    checkNonNull("ObjectName", paramObjectName);
    checkNonNull("Operation name", paramString);
    boolean bool = logger.debugOn();
    if (bool)
      logger.debug("invoke", "connectionId=" + this.connectionId + " unwrapping params with MBean extended ClassLoader."); 
    Object[] arrayOfObject = nullIsEmpty((Object[])unwrap(paramMarshalledObject, getClassLoaderFor(paramObjectName), this.defaultClassLoader, Object[].class, paramSubject));
    try {
      Object[] arrayOfObject1 = { paramObjectName, paramString, arrayOfObject, nullIsEmpty(paramArrayOfString) };
      if (bool)
        logger.debug("invoke", "connectionId=" + this.connectionId + ", name=" + paramObjectName + ", operationName=" + paramString + ", signature=" + strings(paramArrayOfString)); 
      return doPrivilegedOperation(14, arrayOfObject1, paramSubject);
    } catch (PrivilegedActionException privilegedActionException) {
      Exception exception = extractException(privilegedActionException);
      if (exception instanceof InstanceNotFoundException)
        throw (InstanceNotFoundException)exception; 
      if (exception instanceof MBeanException)
        throw (MBeanException)exception; 
      if (exception instanceof ReflectionException)
        throw (ReflectionException)exception; 
      if (exception instanceof IOException)
        throw (IOException)exception; 
      throw newIOException("Got unexpected server exception: " + exception, exception);
    } 
  }
  
  public String getDefaultDomain(Subject paramSubject) throws IOException {
    try {
      Object[] arrayOfObject = new Object[0];
      if (logger.debugOn())
        logger.debug("getDefaultDomain", "connectionId=" + this.connectionId); 
      return (String)doPrivilegedOperation(9, arrayOfObject, paramSubject);
    } catch (PrivilegedActionException privilegedActionException) {
      Exception exception = extractException(privilegedActionException);
      if (exception instanceof IOException)
        throw (IOException)exception; 
      throw newIOException("Got unexpected server exception: " + exception, exception);
    } 
  }
  
  public String[] getDomains(Subject paramSubject) throws IOException {
    try {
      Object[] arrayOfObject = new Object[0];
      if (logger.debugOn())
        logger.debug("getDomains", "connectionId=" + this.connectionId); 
      return (String[])doPrivilegedOperation(10, arrayOfObject, paramSubject);
    } catch (PrivilegedActionException privilegedActionException) {
      Exception exception = extractException(privilegedActionException);
      if (exception instanceof IOException)
        throw (IOException)exception; 
      throw newIOException("Got unexpected server exception: " + exception, exception);
    } 
  }
  
  public MBeanInfo getMBeanInfo(ObjectName paramObjectName, Subject paramSubject) throws InstanceNotFoundException, IntrospectionException, ReflectionException, IOException {
    checkNonNull("ObjectName", paramObjectName);
    try {
      Object[] arrayOfObject = { paramObjectName };
      if (logger.debugOn())
        logger.debug("getMBeanInfo", "connectionId=" + this.connectionId + ", name=" + paramObjectName); 
      return (MBeanInfo)doPrivilegedOperation(12, arrayOfObject, paramSubject);
    } catch (PrivilegedActionException privilegedActionException) {
      Exception exception = extractException(privilegedActionException);
      if (exception instanceof InstanceNotFoundException)
        throw (InstanceNotFoundException)exception; 
      if (exception instanceof IntrospectionException)
        throw (IntrospectionException)exception; 
      if (exception instanceof ReflectionException)
        throw (ReflectionException)exception; 
      if (exception instanceof IOException)
        throw (IOException)exception; 
      throw newIOException("Got unexpected server exception: " + exception, exception);
    } 
  }
  
  public boolean isInstanceOf(ObjectName paramObjectName, String paramString, Subject paramSubject) throws InstanceNotFoundException, IOException {
    checkNonNull("ObjectName", paramObjectName);
    try {
      Object[] arrayOfObject = { paramObjectName, paramString };
      if (logger.debugOn())
        logger.debug("isInstanceOf", "connectionId=" + this.connectionId + ", name=" + paramObjectName + ", className=" + paramString); 
      return ((Boolean)doPrivilegedOperation(15, arrayOfObject, paramSubject)).booleanValue();
    } catch (PrivilegedActionException privilegedActionException) {
      Exception exception = extractException(privilegedActionException);
      if (exception instanceof InstanceNotFoundException)
        throw (InstanceNotFoundException)exception; 
      if (exception instanceof IOException)
        throw (IOException)exception; 
      throw newIOException("Got unexpected server exception: " + exception, exception);
    } 
  }
  
  public Integer[] addNotificationListeners(ObjectName[] paramArrayOfObjectName, MarshalledObject[] paramArrayOfMarshalledObject, Subject[] paramArrayOfSubject) throws InstanceNotFoundException, IOException {
    if (paramArrayOfObjectName == null || paramArrayOfMarshalledObject == null)
      throw new IllegalArgumentException("Got null arguments."); 
    Subject[] arrayOfSubject = (paramArrayOfSubject != null) ? paramArrayOfSubject : new Subject[paramArrayOfObjectName.length];
    if (paramArrayOfObjectName.length != paramArrayOfMarshalledObject.length || paramArrayOfMarshalledObject.length != arrayOfSubject.length)
      throw new IllegalArgumentException("The value lengths of 3 parameters are not same."); 
    byte b;
    for (b = 0; b < paramArrayOfObjectName.length; b++) {
      if (paramArrayOfObjectName[b] == null)
        throw new IllegalArgumentException("Null Object name."); 
    } 
    b = 0;
    NotificationFilter[] arrayOfNotificationFilter = new NotificationFilter[paramArrayOfObjectName.length];
    Integer[] arrayOfInteger = new Integer[paramArrayOfObjectName.length];
    boolean bool = logger.debugOn();
    try {
      while (b < paramArrayOfObjectName.length) {
        ClassLoader classLoader = getClassLoaderFor(paramArrayOfObjectName[b]);
        if (bool)
          logger.debug("addNotificationListener(ObjectName,NotificationFilter)", "connectionId=" + this.connectionId + " unwrapping filter with target extended ClassLoader."); 
        arrayOfNotificationFilter[b] = (NotificationFilter)unwrap(paramArrayOfMarshalledObject[b], classLoader, this.defaultClassLoader, NotificationFilter.class, arrayOfSubject[b]);
        if (bool)
          logger.debug("addNotificationListener(ObjectName,NotificationFilter)", "connectionId=" + this.connectionId + ", name=" + paramArrayOfObjectName[b] + ", filter=" + arrayOfNotificationFilter[b]); 
        arrayOfInteger[b] = (Integer)doPrivilegedOperation(1, new Object[] { paramArrayOfObjectName[b], arrayOfNotificationFilter[b] }, arrayOfSubject[b]);
        b++;
      } 
      return arrayOfInteger;
    } catch (Exception exception) {
      for (byte b1 = 0; b1 < b; b1++) {
        try {
          getServerNotifFwd().removeNotificationListener(paramArrayOfObjectName[b1], arrayOfInteger[b1]);
        } catch (Exception exception1) {}
      } 
      if (exception instanceof PrivilegedActionException)
        exception = extractException(exception); 
      if (exception instanceof ClassCastException)
        throw (ClassCastException)exception; 
      if (exception instanceof IOException)
        throw (IOException)exception; 
      if (exception instanceof InstanceNotFoundException)
        throw (InstanceNotFoundException)exception; 
      if (exception instanceof RuntimeException)
        throw (RuntimeException)exception; 
      throw newIOException("Got unexpected server exception: " + exception, exception);
    } 
  }
  
  public void addNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2, MarshalledObject paramMarshalledObject1, MarshalledObject paramMarshalledObject2, Subject paramSubject) throws InstanceNotFoundException, IOException {
    checkNonNull("Target MBean name", paramObjectName1);
    checkNonNull("Listener MBean name", paramObjectName2);
    boolean bool = logger.debugOn();
    ClassLoader classLoader = getClassLoaderFor(paramObjectName1);
    if (bool)
      logger.debug("addNotificationListener(ObjectName,ObjectName,NotificationFilter,Object)", "connectionId=" + this.connectionId + " unwrapping filter with target extended ClassLoader."); 
    NotificationFilter notificationFilter = (NotificationFilter)unwrap(paramMarshalledObject1, classLoader, this.defaultClassLoader, NotificationFilter.class, paramSubject);
    if (bool)
      logger.debug("addNotificationListener(ObjectName,ObjectName,NotificationFilter,Object)", "connectionId=" + this.connectionId + " unwrapping handback with target extended ClassLoader."); 
    Object object = unwrap(paramMarshalledObject2, classLoader, this.defaultClassLoader, Object.class, paramSubject);
    try {
      Object[] arrayOfObject = { paramObjectName1, paramObjectName2, notificationFilter, object };
      if (bool)
        logger.debug("addNotificationListener(ObjectName,ObjectName,NotificationFilter,Object)", "connectionId=" + this.connectionId + ", name=" + paramObjectName1 + ", listenerName=" + paramObjectName2 + ", filter=" + notificationFilter + ", handback=" + object); 
      doPrivilegedOperation(2, arrayOfObject, paramSubject);
    } catch (PrivilegedActionException privilegedActionException) {
      Exception exception = extractException(privilegedActionException);
      if (exception instanceof InstanceNotFoundException)
        throw (InstanceNotFoundException)exception; 
      if (exception instanceof IOException)
        throw (IOException)exception; 
      throw newIOException("Got unexpected server exception: " + exception, exception);
    } 
  }
  
  public void removeNotificationListeners(ObjectName paramObjectName, Integer[] paramArrayOfInteger, Subject paramSubject) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
    if (paramObjectName == null || paramArrayOfInteger == null)
      throw new IllegalArgumentException("Illegal null parameter"); 
    for (b = 0; b < paramArrayOfInteger.length; b++) {
      if (paramArrayOfInteger[b] == null)
        throw new IllegalArgumentException("Null listener ID"); 
    } 
    try {
      Object[] arrayOfObject = { paramObjectName, paramArrayOfInteger };
      if (logger.debugOn())
        logger.debug("removeNotificationListener(ObjectName,Integer[])", "connectionId=" + this.connectionId + ", name=" + paramObjectName + ", listenerIDs=" + objects(paramArrayOfInteger)); 
      doPrivilegedOperation(19, arrayOfObject, paramSubject);
    } catch (PrivilegedActionException b) {
      PrivilegedActionException privilegedActionException;
      Exception exception = extractException(privilegedActionException);
      if (exception instanceof InstanceNotFoundException)
        throw (InstanceNotFoundException)exception; 
      if (exception instanceof ListenerNotFoundException)
        throw (ListenerNotFoundException)exception; 
      if (exception instanceof IOException)
        throw (IOException)exception; 
      throw newIOException("Got unexpected server exception: " + exception, exception);
    } 
  }
  
  public void removeNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2, Subject paramSubject) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
    checkNonNull("Target MBean name", paramObjectName1);
    checkNonNull("Listener MBean name", paramObjectName2);
    try {
      Object[] arrayOfObject = { paramObjectName1, paramObjectName2 };
      if (logger.debugOn())
        logger.debug("removeNotificationListener(ObjectName,ObjectName)", "connectionId=" + this.connectionId + ", name=" + paramObjectName1 + ", listenerName=" + paramObjectName2); 
      doPrivilegedOperation(20, arrayOfObject, paramSubject);
    } catch (PrivilegedActionException privilegedActionException) {
      Exception exception = extractException(privilegedActionException);
      if (exception instanceof InstanceNotFoundException)
        throw (InstanceNotFoundException)exception; 
      if (exception instanceof ListenerNotFoundException)
        throw (ListenerNotFoundException)exception; 
      if (exception instanceof IOException)
        throw (IOException)exception; 
      throw newIOException("Got unexpected server exception: " + exception, exception);
    } 
  }
  
  public void removeNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2, MarshalledObject paramMarshalledObject1, MarshalledObject paramMarshalledObject2, Subject paramSubject) throws InstanceNotFoundException, IOException {
    checkNonNull("Target MBean name", paramObjectName1);
    checkNonNull("Listener MBean name", paramObjectName2);
    boolean bool = logger.debugOn();
    ClassLoader classLoader = getClassLoaderFor(paramObjectName1);
    if (bool)
      logger.debug("removeNotificationListener(ObjectName,ObjectName,NotificationFilter,Object)", "connectionId=" + this.connectionId + " unwrapping filter with target extended ClassLoader."); 
    NotificationFilter notificationFilter = (NotificationFilter)unwrap(paramMarshalledObject1, classLoader, this.defaultClassLoader, NotificationFilter.class, paramSubject);
    if (bool)
      logger.debug("removeNotificationListener(ObjectName,ObjectName,NotificationFilter,Object)", "connectionId=" + this.connectionId + " unwrapping handback with target extended ClassLoader."); 
    Object object = unwrap(paramMarshalledObject2, classLoader, this.defaultClassLoader, Object.class, paramSubject);
    try {
      Object[] arrayOfObject = { paramObjectName1, paramObjectName2, notificationFilter, object };
      if (bool)
        logger.debug("removeNotificationListener(ObjectName,ObjectName,NotificationFilter,Object)", "connectionId=" + this.connectionId + ", name=" + paramObjectName1 + ", listenerName=" + paramObjectName2 + ", filter=" + notificationFilter + ", handback=" + object); 
      doPrivilegedOperation(21, arrayOfObject, paramSubject);
    } catch (PrivilegedActionException privilegedActionException) {
      Exception exception = extractException(privilegedActionException);
      if (exception instanceof InstanceNotFoundException)
        throw (InstanceNotFoundException)exception; 
      if (exception instanceof ListenerNotFoundException)
        throw (ListenerNotFoundException)exception; 
      if (exception instanceof IOException)
        throw (IOException)exception; 
      throw newIOException("Got unexpected server exception: " + exception, exception);
    } 
  }
  
  public NotificationResult fetchNotifications(long paramLong1, int paramInt, long paramLong2) throws IOException {
    if (logger.debugOn())
      logger.debug("fetchNotifications", "connectionId=" + this.connectionId + ", timeout=" + paramLong2); 
    if (paramInt < 0 || paramLong2 < 0L)
      throw new IllegalArgumentException("Illegal negative argument"); 
    boolean bool = this.serverCommunicatorAdmin.reqIncoming();
    try {
      if (bool) {
        if (logger.debugOn())
          logger.debug("fetchNotifications", "The notification server has been closed, returns null to force the client to stop fetching"); 
        return null;
      } 
      final long csn = paramLong1;
      final int mn = paramInt;
      final long t = paramLong2;
      PrivilegedAction<NotificationResult> privilegedAction = new PrivilegedAction<NotificationResult>() {
          public NotificationResult run() { return RMIConnectionImpl.this.getServerNotifFwd().fetchNotifs(csn, t, mn); }
        };
      if (this.acc == null)
        return (NotificationResult)privilegedAction.run(); 
      return (NotificationResult)AccessController.doPrivileged(privilegedAction, this.acc);
    } finally {
      this.serverCommunicatorAdmin.rspOutgoing();
    } 
  }
  
  public String toString() throws IOException { return super.toString() + ": connectionId=" + this.connectionId; }
  
  private ClassLoader getClassLoader(final ObjectName name) throws InstanceNotFoundException {
    try {
      return (ClassLoader)AccessController.doPrivileged(new PrivilegedExceptionAction<ClassLoader>() {
            public ClassLoader run() { return RMIConnectionImpl.this.mbeanServer.getClassLoader(name); }
          },  withPermissions(new Permission[] { new MBeanPermission("*", "getClassLoader") }));
    } catch (PrivilegedActionException privilegedActionException) {
      throw (InstanceNotFoundException)extractException(privilegedActionException);
    } 
  }
  
  private ClassLoader getClassLoaderFor(final ObjectName name) throws InstanceNotFoundException {
    try {
      return (ClassLoader)AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
            public Object run() { return RMIConnectionImpl.this.mbeanServer.getClassLoaderFor(name); }
          },  withPermissions(new Permission[] { new MBeanPermission("*", "getClassLoaderFor") }));
    } catch (PrivilegedActionException privilegedActionException) {
      throw (InstanceNotFoundException)extractException(privilegedActionException);
    } 
  }
  
  private Object doPrivilegedOperation(int paramInt, Object[] paramArrayOfObject, Subject paramSubject) throws PrivilegedActionException, IOException {
    this.serverCommunicatorAdmin.reqIncoming();
    try {
      AccessControlContext accessControlContext;
      if (paramSubject == null) {
        accessControlContext = this.acc;
      } else {
        if (this.subject == null)
          throw new SecurityException("Subject delegation cannot be enabled unless an authenticated subject is put in place"); 
        accessControlContext = this.subjectDelegator.delegatedContext(this.acc, paramSubject, this.removeCallerContext);
      } 
      PrivilegedOperation privilegedOperation = new PrivilegedOperation(paramInt, paramArrayOfObject);
      if (accessControlContext == null)
        try {
          return privilegedOperation.run();
        } catch (Exception exception) {
          if (exception instanceof RuntimeException)
            throw (RuntimeException)exception; 
          throw new PrivilegedActionException(exception);
        }  
      return AccessController.doPrivileged(privilegedOperation, accessControlContext);
    } catch (Error error) {
      throw new JMXServerErrorException(error.toString(), error);
    } finally {
      this.serverCommunicatorAdmin.rspOutgoing();
    } 
  }
  
  private Object doOperation(int paramInt, Object[] paramArrayOfObject) throws Exception {
    switch (paramInt) {
      case 3:
        return this.mbeanServer.createMBean((String)paramArrayOfObject[0], (ObjectName)paramArrayOfObject[1]);
      case 5:
        return this.mbeanServer.createMBean((String)paramArrayOfObject[0], (ObjectName)paramArrayOfObject[1], (ObjectName)paramArrayOfObject[2]);
      case 4:
        return this.mbeanServer.createMBean((String)paramArrayOfObject[0], (ObjectName)paramArrayOfObject[1], (Object[])paramArrayOfObject[2], (String[])paramArrayOfObject[3]);
      case 6:
        return this.mbeanServer.createMBean((String)paramArrayOfObject[0], (ObjectName)paramArrayOfObject[1], (ObjectName)paramArrayOfObject[2], (Object[])paramArrayOfObject[3], (String[])paramArrayOfObject[4]);
      case 7:
        return this.mbeanServer.getAttribute((ObjectName)paramArrayOfObject[0], (String)paramArrayOfObject[1]);
      case 8:
        return this.mbeanServer.getAttributes((ObjectName)paramArrayOfObject[0], (String[])paramArrayOfObject[1]);
      case 9:
        return this.mbeanServer.getDefaultDomain();
      case 10:
        return this.mbeanServer.getDomains();
      case 11:
        return this.mbeanServer.getMBeanCount();
      case 12:
        return this.mbeanServer.getMBeanInfo((ObjectName)paramArrayOfObject[0]);
      case 13:
        return this.mbeanServer.getObjectInstance((ObjectName)paramArrayOfObject[0]);
      case 14:
        return this.mbeanServer.invoke((ObjectName)paramArrayOfObject[0], (String)paramArrayOfObject[1], (Object[])paramArrayOfObject[2], (String[])paramArrayOfObject[3]);
      case 15:
        return this.mbeanServer.isInstanceOf((ObjectName)paramArrayOfObject[0], (String)paramArrayOfObject[1]) ? Boolean.TRUE : Boolean.FALSE;
      case 16:
        return this.mbeanServer.isRegistered((ObjectName)paramArrayOfObject[0]) ? Boolean.TRUE : Boolean.FALSE;
      case 17:
        return this.mbeanServer.queryMBeans((ObjectName)paramArrayOfObject[0], (QueryExp)paramArrayOfObject[1]);
      case 18:
        return this.mbeanServer.queryNames((ObjectName)paramArrayOfObject[0], (QueryExp)paramArrayOfObject[1]);
      case 22:
        this.mbeanServer.setAttribute((ObjectName)paramArrayOfObject[0], (Attribute)paramArrayOfObject[1]);
        return null;
      case 23:
        return this.mbeanServer.setAttributes((ObjectName)paramArrayOfObject[0], (AttributeList)paramArrayOfObject[1]);
      case 24:
        this.mbeanServer.unregisterMBean((ObjectName)paramArrayOfObject[0]);
        return null;
      case 1:
        return getServerNotifFwd().addNotificationListener((ObjectName)paramArrayOfObject[0], (NotificationFilter)paramArrayOfObject[1]);
      case 2:
        this.mbeanServer.addNotificationListener((ObjectName)paramArrayOfObject[0], (ObjectName)paramArrayOfObject[1], (NotificationFilter)paramArrayOfObject[2], paramArrayOfObject[3]);
        return null;
      case 19:
        getServerNotifFwd().removeNotificationListener((ObjectName)paramArrayOfObject[0], (Integer[])paramArrayOfObject[1]);
        return null;
      case 20:
        this.mbeanServer.removeNotificationListener((ObjectName)paramArrayOfObject[0], (ObjectName)paramArrayOfObject[1]);
        return null;
      case 21:
        this.mbeanServer.removeNotificationListener((ObjectName)paramArrayOfObject[0], (ObjectName)paramArrayOfObject[1], (NotificationFilter)paramArrayOfObject[2], paramArrayOfObject[3]);
        return null;
    } 
    throw new IllegalArgumentException("Invalid operation");
  }
  
  private <T> T unwrap(MarshalledObject<?> paramMarshalledObject, ClassLoader paramClassLoader, Class<T> paramClass, Subject paramSubject) throws IOException {
    if (paramMarshalledObject == null)
      return null; 
    try {
      classLoader = (ClassLoader)AccessController.doPrivileged(new SetCcl(paramClassLoader));
      try {
        AccessControlContext accessControlContext;
        if (paramSubject == null) {
          accessControlContext = this.acc;
        } else {
          if (this.subject == null)
            throw new SecurityException("Subject delegation cannot be enabled unless an authenticated subject is put in place"); 
          accessControlContext = this.subjectDelegator.delegatedContext(this.acc, paramSubject, this.removeCallerContext);
        } 
        if (accessControlContext != null) {
          object1 = AccessController.doPrivileged(() -> paramClass.cast(paramMarshalledObject.get()), accessControlContext);
          return (T)object1;
        } 
        object = paramClass.cast(paramMarshalledObject.get());
        return (T)object;
      } finally {
        AccessController.doPrivileged(new SetCcl(classLoader));
      } 
    } catch (PrivilegedActionException privilegedActionException) {
      Exception exception = extractException(privilegedActionException);
      if (exception instanceof IOException)
        throw (IOException)exception; 
      if (exception instanceof ClassNotFoundException)
        throw new UnmarshalException(exception.toString(), exception); 
      logger.warning("unwrap", "Failed to unmarshall object: " + exception);
      logger.debug("unwrap", exception);
    } catch (ClassNotFoundException classNotFoundException) {
      logger.warning("unwrap", "Failed to unmarshall object: " + classNotFoundException);
      logger.debug("unwrap", classNotFoundException);
      throw new UnmarshalException(classNotFoundException.toString(), classNotFoundException);
    } 
    return null;
  }
  
  private <T> T unwrap(MarshalledObject<?> paramMarshalledObject, final ClassLoader cl1, final ClassLoader cl2, Class<T> paramClass, Subject paramSubject) throws IOException {
    if (paramMarshalledObject == null)
      return null; 
    try {
      ClassLoader classLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedExceptionAction<ClassLoader>() {
            public ClassLoader run() { return new RMIConnectionImpl.CombinedClassLoader(Thread.currentThread().getContextClassLoader(), new OrderClassLoaders(cl1, cl2), null); }
          });
      return (T)unwrap(paramMarshalledObject, classLoader, paramClass, paramSubject);
    } catch (PrivilegedActionException privilegedActionException) {
      Exception exception = extractException(privilegedActionException);
      if (exception instanceof IOException)
        throw (IOException)exception; 
      if (exception instanceof ClassNotFoundException)
        throw new UnmarshalException(exception.toString(), exception); 
      logger.warning("unwrap", "Failed to unmarshall object: " + exception);
      logger.debug("unwrap", exception);
      return null;
    } 
  }
  
  private static IOException newIOException(String paramString, Throwable paramThrowable) {
    IOException iOException = new IOException(paramString);
    return (IOException)EnvHelp.initCause(iOException, paramThrowable);
  }
  
  private static Exception extractException(Exception paramException) {
    while (paramException instanceof PrivilegedActionException)
      paramException = ((PrivilegedActionException)paramException).getException(); 
    return paramException;
  }
  
  private static Object[] nullIsEmpty(Object[] paramArrayOfObject) { return (paramArrayOfObject == null) ? NO_OBJECTS : paramArrayOfObject; }
  
  private static String[] nullIsEmpty(String[] paramArrayOfString) { return (paramArrayOfString == null) ? NO_STRINGS : paramArrayOfString; }
  
  private static void checkNonNull(String paramString, Object paramObject) {
    if (paramObject == null) {
      IllegalArgumentException illegalArgumentException = new IllegalArgumentException(paramString + " must not be null");
      throw new RuntimeOperationsException(illegalArgumentException);
    } 
  }
  
  private static String objects(Object[] paramArrayOfObject) { return (paramArrayOfObject == null) ? "null" : Arrays.asList(paramArrayOfObject).toString(); }
  
  private static String strings(String[] paramArrayOfString) { return objects(paramArrayOfString); }
  
  private static final class CombinedClassLoader extends ClassLoader {
    final ClassLoaderWrapper defaultCL;
    
    private CombinedClassLoader(ClassLoader param1ClassLoader1, ClassLoader param1ClassLoader2) {
      super(param1ClassLoader1);
      this.defaultCL = new ClassLoaderWrapper(param1ClassLoader2);
    }
    
    protected Class<?> loadClass(String param1String, boolean param1Boolean) throws ClassNotFoundException {
      ReflectUtil.checkPackageAccess(param1String);
      try {
        super.loadClass(param1String, param1Boolean);
      } catch (Exception exception1) {
        Exception exception2 = exception1;
        while (exception2 != null) {
          if (exception2 instanceof SecurityException)
            throw (exception2 == exception1) ? (SecurityException)exception2 : new SecurityException(exception2.getMessage(), exception1); 
          Throwable throwable = exception2.getCause();
        } 
      } 
      return this.defaultCL.loadClass(param1String, param1Boolean);
    }
    
    private static final class ClassLoaderWrapper extends ClassLoader {
      ClassLoaderWrapper(ClassLoader param2ClassLoader) { super(param2ClassLoader); }
      
      protected Class<?> loadClass(String param2String, boolean param2Boolean) throws ClassNotFoundException { return super.loadClass(param2String, param2Boolean); }
    }
  }
  
  private class PrivilegedOperation extends Object implements PrivilegedExceptionAction<Object> {
    private int operation;
    
    private Object[] params;
    
    public PrivilegedOperation(int param1Int, Object[] param1ArrayOfObject) {
      this.operation = param1Int;
      this.params = param1ArrayOfObject;
    }
    
    public Object run() { return RMIConnectionImpl.this.doOperation(this.operation, this.params); }
  }
  
  private class RMIServerCommunicatorAdmin extends ServerCommunicatorAdmin {
    public RMIServerCommunicatorAdmin(long param1Long) { super(param1Long); }
    
    protected void doStop() throws IOException {
      try {
        RMIConnectionImpl.this.close();
      } catch (IOException iOException) {
        logger.warning("RMIServerCommunicatorAdmin-doStop", "Failed to close: " + iOException);
        logger.debug("RMIServerCommunicatorAdmin-doStop", iOException);
      } 
    }
  }
  
  private static class SetCcl extends Object implements PrivilegedExceptionAction<ClassLoader> {
    private final ClassLoader classLoader;
    
    SetCcl(ClassLoader param1ClassLoader) { this.classLoader = param1ClassLoader; }
    
    public ClassLoader run() {
      Thread thread = Thread.currentThread();
      ClassLoader classLoader1 = thread.getContextClassLoader();
      thread.setContextClassLoader(this.classLoader);
      return classLoader1;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\remote\rmi\RMIConnectionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */