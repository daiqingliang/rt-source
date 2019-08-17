package sun.rmi.registry;

import java.io.FilePermission;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketPermission;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.UnknownHostException;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.ObjID;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.security.Security;
import java.security.cert.Certificate;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import sun.misc.ObjectInputFilter;
import sun.misc.URLClassPath;
import sun.rmi.runtime.Log;
import sun.rmi.server.LoaderHandler;
import sun.rmi.server.UnicastServerRef;
import sun.rmi.server.UnicastServerRef2;
import sun.rmi.transport.LiveRef;

public class RegistryImpl extends RemoteServer implements Registry {
  private static final long serialVersionUID = 4666870661827494597L;
  
  private Hashtable<String, Remote> bindings = new Hashtable(101);
  
  private static Hashtable<InetAddress, InetAddress> allowedAccessCache = new Hashtable(3);
  
  private static RegistryImpl registry;
  
  private static ObjID id = new ObjID(0);
  
  private static ResourceBundle resources = null;
  
  private static final String REGISTRY_FILTER_PROPNAME = "sun.rmi.registry.registryFilter";
  
  private static final int REGISTRY_MAX_DEPTH = 20;
  
  private static final int REGISTRY_MAX_ARRAY_SIZE = 1000000;
  
  private static final ObjectInputFilter registryFilter = (ObjectInputFilter)AccessController.doPrivileged(RegistryImpl::initRegistryFilter);
  
  private static ObjectInputFilter initRegistryFilter() {
    ObjectInputFilter objectInputFilter = null;
    String str = System.getProperty("sun.rmi.registry.registryFilter");
    if (str == null)
      str = Security.getProperty("sun.rmi.registry.registryFilter"); 
    if (str != null) {
      objectInputFilter = ObjectInputFilter.Config.createFilter2(str);
      Log log = Log.getLog("sun.rmi.registry", "registry", -1);
      if (log.isLoggable(Log.BRIEF))
        log.log(Log.BRIEF, "registryFilter = " + objectInputFilter); 
    } 
    return objectInputFilter;
  }
  
  public RegistryImpl(int paramInt, RMIClientSocketFactory paramRMIClientSocketFactory, RMIServerSocketFactory paramRMIServerSocketFactory) throws RemoteException { this(paramInt, paramRMIClientSocketFactory, paramRMIServerSocketFactory, RegistryImpl::registryFilter); }
  
  public RegistryImpl(final int port, final RMIClientSocketFactory csf, final RMIServerSocketFactory ssf, final ObjectInputFilter serialFilter) throws RemoteException {
    if (paramInt == 1099 && System.getSecurityManager() != null) {
      try {
        AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
              public Void run() throws RemoteException {
                LiveRef liveRef = new LiveRef(id, port, csf, ssf);
                RegistryImpl.this.setup(new UnicastServerRef2(liveRef, serialFilter));
                return null;
              }
            }null, new Permission[] { new SocketPermission("localhost:" + paramInt, "listen,accept") });
      } catch (PrivilegedActionException privilegedActionException) {
        throw (RemoteException)privilegedActionException.getException();
      } 
    } else {
      LiveRef liveRef = new LiveRef(id, paramInt, paramRMIClientSocketFactory, paramRMIServerSocketFactory);
      setup(new UnicastServerRef2(liveRef, paramObjectInputFilter));
    } 
  }
  
  public RegistryImpl(final int port) throws RemoteException {
    if (paramInt == 1099 && System.getSecurityManager() != null) {
      try {
        AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
              public Void run() throws RemoteException {
                LiveRef liveRef = new LiveRef(id, port);
                RegistryImpl.this.setup(new UnicastServerRef(liveRef, param1FilterInfo -> RegistryImpl.registryFilter(param1FilterInfo)));
                return null;
              }
            }null, new Permission[] { new SocketPermission("localhost:" + paramInt, "listen,accept") });
      } catch (PrivilegedActionException privilegedActionException) {
        throw (RemoteException)privilegedActionException.getException();
      } 
    } else {
      LiveRef liveRef = new LiveRef(id, paramInt);
      setup(new UnicastServerRef(liveRef, RegistryImpl::registryFilter));
    } 
  }
  
  private void setup(UnicastServerRef paramUnicastServerRef) throws RemoteException {
    this.ref = paramUnicastServerRef;
    paramUnicastServerRef.exportObject(this, null, true);
  }
  
  public Remote lookup(String paramString) throws RemoteException, NotBoundException {
    synchronized (this.bindings) {
      Remote remote = (Remote)this.bindings.get(paramString);
      if (remote == null)
        throw new NotBoundException(paramString); 
      return remote;
    } 
  }
  
  public void bind(String paramString, Remote paramRemote) throws RemoteException, AlreadyBoundException, AccessException {
    synchronized (this.bindings) {
      Remote remote = (Remote)this.bindings.get(paramString);
      if (remote != null)
        throw new AlreadyBoundException(paramString); 
      this.bindings.put(paramString, paramRemote);
    } 
  }
  
  public void unbind(String paramString) throws RemoteException, NotBoundException, AccessException {
    synchronized (this.bindings) {
      Remote remote = (Remote)this.bindings.get(paramString);
      if (remote == null)
        throw new NotBoundException(paramString); 
      this.bindings.remove(paramString);
    } 
  }
  
  public void rebind(String paramString, Remote paramRemote) throws RemoteException, AlreadyBoundException, AccessException { this.bindings.put(paramString, paramRemote); }
  
  public String[] list() throws RemoteException {
    String[] arrayOfString;
    synchronized (this.bindings) {
      int i = this.bindings.size();
      arrayOfString = new String[i];
      Enumeration enumeration = this.bindings.keys();
      while (--i >= 0)
        arrayOfString[i] = (String)enumeration.nextElement(); 
    } 
    return arrayOfString;
  }
  
  public static void checkAccess(String paramString) throws RemoteException, NotBoundException, AccessException {
    try {
      InetAddress inetAddress;
      final String clientHostName = getClientHost();
      try {
        inetAddress = (InetAddress)AccessController.doPrivileged(new PrivilegedExceptionAction<InetAddress>() {
              public InetAddress run() throws UnknownHostException { return InetAddress.getByName(clientHostName); }
            });
      } catch (PrivilegedActionException privilegedActionException) {
        throw (UnknownHostException)privilegedActionException.getException();
      } 
      if (allowedAccessCache.get(inetAddress) == null) {
        if (inetAddress.isAnyLocalAddress())
          throw new AccessException(paramString + " disallowed; origin unknown"); 
        try {
          final InetAddress finalClientHost = inetAddress;
          AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
                public Void run() throws RemoteException {
                  (new ServerSocket(0, 10, finalClientHost)).close();
                  allowedAccessCache.put(finalClientHost, finalClientHost);
                  return null;
                }
              });
        } catch (PrivilegedActionException privilegedActionException) {
          throw new AccessException(paramString + " disallowed; origin " + inetAddress + " is non-local host");
        } 
      } 
    } catch (ServerNotActiveException serverNotActiveException) {
    
    } catch (UnknownHostException unknownHostException) {
      throw new AccessException(paramString + " disallowed; origin is unknown host");
    } 
  }
  
  public static ObjID getID() { return id; }
  
  private static String getTextResource(String paramString) {
    if (resources == null) {
      try {
        resources = ResourceBundle.getBundle("sun.rmi.registry.resources.rmiregistry");
      } catch (MissingResourceException missingResourceException) {}
      if (resources == null)
        return "[missing resource file: " + paramString + "]"; 
    } 
    String str = null;
    try {
      str = resources.getString(paramString);
    } catch (MissingResourceException missingResourceException) {}
    return (str == null) ? ("[missing resource: " + paramString + "]") : str;
  }
  
  private static ObjectInputFilter.Status registryFilter(ObjectInputFilter.FilterInfo paramFilterInfo) {
    if (registryFilter != null) {
      ObjectInputFilter.Status status = registryFilter.checkInput(paramFilterInfo);
      if (status != ObjectInputFilter.Status.UNDECIDED)
        return status; 
    } 
    if (paramFilterInfo.depth() > 20L)
      return ObjectInputFilter.Status.REJECTED; 
    Class clazz = paramFilterInfo.serialClass();
    return (clazz != null) ? (clazz.isArray() ? ((paramFilterInfo.arrayLength() >= 0L && paramFilterInfo.arrayLength() > 1000000L) ? ObjectInputFilter.Status.REJECTED : ObjectInputFilter.Status.UNDECIDED) : ((String.class == clazz || Number.class.isAssignableFrom(clazz) || Remote.class.isAssignableFrom(clazz) || java.lang.reflect.Proxy.class.isAssignableFrom(clazz) || sun.rmi.server.UnicastRef.class.isAssignableFrom(clazz) || RMIClientSocketFactory.class.isAssignableFrom(clazz) || RMIServerSocketFactory.class.isAssignableFrom(clazz) || java.rmi.activation.ActivationID.class.isAssignableFrom(clazz) || java.rmi.server.UID.class.isAssignableFrom(clazz)) ? ObjectInputFilter.Status.ALLOWED : ObjectInputFilter.Status.REJECTED)) : ObjectInputFilter.Status.UNDECIDED;
  }
  
  public static void main(String[] paramArrayOfString) {
    if (System.getSecurityManager() == null)
      System.setSecurityManager(new RMISecurityManager()); 
    try {
      String str = System.getProperty("env.class.path");
      if (str == null)
        str = "."; 
      URL[] arrayOfURL = URLClassPath.pathToURLs(str);
      URLClassLoader uRLClassLoader = new URLClassLoader(arrayOfURL);
      LoaderHandler.registerCodebaseLoader(uRLClassLoader);
      Thread.currentThread().setContextClassLoader(uRLClassLoader);
      final int regPort = (paramArrayOfString.length >= 1) ? Integer.parseInt(paramArrayOfString[0]) : 1099;
      try {
        registry = (RegistryImpl)AccessController.doPrivileged(new PrivilegedExceptionAction<RegistryImpl>() {
              public RegistryImpl run() throws RemoteException { return new RegistryImpl(regPort); }
            },  getAccessControlContext(i));
      } catch (PrivilegedActionException privilegedActionException) {
        throw (RemoteException)privilegedActionException.getException();
      } 
      while (true) {
        try {
          while (true)
            Thread.sleep(Float.MAX_VALUE); 
          break;
        } catch (InterruptedException interruptedException) {}
      } 
    } catch (NumberFormatException numberFormatException) {
      System.err.println(MessageFormat.format(getTextResource("rmiregistry.port.badnumber"), new Object[] { paramArrayOfString[0] }));
      System.err.println(MessageFormat.format(getTextResource("rmiregistry.usage"), new Object[] { "rmiregistry" }));
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
    System.exit(1);
  }
  
  private static AccessControlContext getAccessControlContext(int paramInt) {
    PermissionCollection permissionCollection = (PermissionCollection)AccessController.doPrivileged(new PrivilegedAction<PermissionCollection>() {
          public PermissionCollection run() {
            CodeSource codeSource = new CodeSource(null, (Certificate[])null);
            Policy policy = Policy.getPolicy();
            return (policy != null) ? policy.getPermissions(codeSource) : new Permissions();
          }
        });
    permissionCollection.add(new SocketPermission("*", "connect,accept"));
    permissionCollection.add(new SocketPermission("localhost:" + paramInt, "listen,accept"));
    permissionCollection.add(new RuntimePermission("accessClassInPackage.sun.jvmstat.*"));
    permissionCollection.add(new RuntimePermission("accessClassInPackage.sun.jvm.hotspot.*"));
    permissionCollection.add(new FilePermission("<<ALL FILES>>", "read"));
    ProtectionDomain protectionDomain = new ProtectionDomain(new CodeSource(null, (Certificate[])null), permissionCollection);
    return new AccessControlContext(new ProtectionDomain[] { protectionDomain });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\registry\RegistryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */