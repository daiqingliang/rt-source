package com.sun.jndi.rmi.registry;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;
import javax.naming.Binding;
import javax.naming.CommunicationException;
import javax.naming.CompositeName;
import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NoPermissionException;
import javax.naming.OperationNotSupportedException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.ServiceUnavailableException;
import javax.naming.StringRefAddr;
import javax.naming.spi.NamingManager;

public class RegistryContext implements Context, Referenceable {
  private Hashtable<String, Object> environment;
  
  private Registry registry;
  
  private String host;
  
  private int port;
  
  private static final NameParser nameParser = new AtomicNameParser();
  
  private static final String SOCKET_FACTORY = "com.sun.jndi.rmi.factory.socket";
  
  static final boolean trustURLCodebase;
  
  Reference reference = null;
  
  public static final String SECURITY_MGR = "java.naming.rmi.security.manager";
  
  public RegistryContext(String paramString, int paramInt, Hashtable<?, ?> paramHashtable) throws NamingException {
    this.environment = (paramHashtable == null) ? new Hashtable(5) : paramHashtable;
    if (this.environment.get("java.naming.rmi.security.manager") != null)
      installSecurityMgr(); 
    if (paramString != null && paramString.charAt(0) == '[')
      paramString = paramString.substring(1, paramString.length() - 1); 
    RMIClientSocketFactory rMIClientSocketFactory = (RMIClientSocketFactory)this.environment.get("com.sun.jndi.rmi.factory.socket");
    this.registry = getRegistry(paramString, paramInt, rMIClientSocketFactory);
    this.host = paramString;
    this.port = paramInt;
  }
  
  RegistryContext(RegistryContext paramRegistryContext) {
    this.environment = (Hashtable)paramRegistryContext.environment.clone();
    this.registry = paramRegistryContext.registry;
    this.host = paramRegistryContext.host;
    this.port = paramRegistryContext.port;
    this.reference = paramRegistryContext.reference;
  }
  
  protected void finalize() { close(); }
  
  public Object lookup(Name paramName) throws NamingException {
    Remote remote;
    if (paramName.isEmpty())
      return new RegistryContext(this); 
    try {
      remote = this.registry.lookup(paramName.get(0));
    } catch (NotBoundException notBoundException) {
      throw new NameNotFoundException(paramName.get(0));
    } catch (RemoteException remoteException) {
      throw (NamingException)wrapRemoteException(remoteException).fillInStackTrace();
    } 
    return decodeObject(remote, paramName.getPrefix(1));
  }
  
  public Object lookup(String paramString) throws NamingException { return lookup(new CompositeName(paramString)); }
  
  public void bind(Name paramName, Object paramObject) throws NamingException {
    if (paramName.isEmpty())
      throw new InvalidNameException("RegistryContext: Cannot bind empty name"); 
    try {
      this.registry.bind(paramName.get(0), encodeObject(paramObject, paramName.getPrefix(1)));
    } catch (AlreadyBoundException alreadyBoundException) {
      NameAlreadyBoundException nameAlreadyBoundException = new NameAlreadyBoundException(paramName.get(0));
      nameAlreadyBoundException.setRootCause(alreadyBoundException);
      throw nameAlreadyBoundException;
    } catch (RemoteException remoteException) {
      throw (NamingException)wrapRemoteException(remoteException).fillInStackTrace();
    } 
  }
  
  public void bind(String paramString, Object paramObject) throws NamingException { bind(new CompositeName(paramString), paramObject); }
  
  public void rebind(Name paramName, Object paramObject) throws NamingException {
    if (paramName.isEmpty())
      throw new InvalidNameException("RegistryContext: Cannot rebind empty name"); 
    try {
      this.registry.rebind(paramName.get(0), encodeObject(paramObject, paramName.getPrefix(1)));
    } catch (RemoteException remoteException) {
      throw (NamingException)wrapRemoteException(remoteException).fillInStackTrace();
    } 
  }
  
  public void rebind(String paramString, Object paramObject) throws NamingException { rebind(new CompositeName(paramString), paramObject); }
  
  public void unbind(Name paramName) throws NamingException {
    if (paramName.isEmpty())
      throw new InvalidNameException("RegistryContext: Cannot unbind empty name"); 
    try {
      this.registry.unbind(paramName.get(0));
    } catch (NotBoundException notBoundException) {
    
    } catch (RemoteException remoteException) {
      throw (NamingException)wrapRemoteException(remoteException).fillInStackTrace();
    } 
  }
  
  public void unbind(String paramString) throws NamingException { unbind(new CompositeName(paramString)); }
  
  public void rename(Name paramName1, Name paramName2) throws NamingException {
    bind(paramName2, lookup(paramName1));
    unbind(paramName1);
  }
  
  public void rename(String paramString1, String paramString2) throws NamingException { rename(new CompositeName(paramString1), new CompositeName(paramString2)); }
  
  public NamingEnumeration<NameClassPair> list(Name paramName) throws NamingException {
    if (!paramName.isEmpty())
      throw new InvalidNameException("RegistryContext: can only list \"\""); 
    try {
      String[] arrayOfString = this.registry.list();
      return new NameClassPairEnumeration(arrayOfString);
    } catch (RemoteException remoteException) {
      throw (NamingException)wrapRemoteException(remoteException).fillInStackTrace();
    } 
  }
  
  public NamingEnumeration<NameClassPair> list(String paramString) throws NamingException { return list(new CompositeName(paramString)); }
  
  public NamingEnumeration<Binding> listBindings(Name paramName) throws NamingException {
    if (!paramName.isEmpty())
      throw new InvalidNameException("RegistryContext: can only list \"\""); 
    try {
      String[] arrayOfString = this.registry.list();
      return new BindingEnumeration(this, arrayOfString);
    } catch (RemoteException remoteException) {
      throw (NamingException)wrapRemoteException(remoteException).fillInStackTrace();
    } 
  }
  
  public NamingEnumeration<Binding> listBindings(String paramString) throws NamingException { return listBindings(new CompositeName(paramString)); }
  
  public void destroySubcontext(Name paramName) throws NamingException { throw new OperationNotSupportedException(); }
  
  public void destroySubcontext(String paramString) throws NamingException { throw new OperationNotSupportedException(); }
  
  public Context createSubcontext(Name paramName) throws NamingException { throw new OperationNotSupportedException(); }
  
  public Context createSubcontext(String paramString) throws NamingException { throw new OperationNotSupportedException(); }
  
  public Object lookupLink(Name paramName) throws NamingException { return lookup(paramName); }
  
  public Object lookupLink(String paramString) throws NamingException { return lookup(paramString); }
  
  public NameParser getNameParser(Name paramName) throws NamingException { return nameParser; }
  
  public NameParser getNameParser(String paramString) throws NamingException { return nameParser; }
  
  public Name composeName(Name paramName1, Name paramName2) throws NamingException {
    Name name = (Name)paramName2.clone();
    return name.addAll(paramName1);
  }
  
  public String composeName(String paramString1, String paramString2) throws NamingException { return composeName(new CompositeName(paramString1), new CompositeName(paramString2)).toString(); }
  
  public Object removeFromEnvironment(String paramString) throws NamingException { return this.environment.remove(paramString); }
  
  public Object addToEnvironment(String paramString, Object paramObject) throws NamingException {
    if (paramString.equals("java.naming.rmi.security.manager"))
      installSecurityMgr(); 
    return this.environment.put(paramString, paramObject);
  }
  
  public Hashtable<String, Object> getEnvironment() throws NamingException { return (Hashtable)this.environment.clone(); }
  
  public void close() {
    this.environment = null;
    this.registry = null;
  }
  
  public String getNameInNamespace() { return ""; }
  
  public Reference getReference() throws NamingException {
    if (this.reference != null)
      return (Reference)this.reference.clone(); 
    if (this.host == null || this.host.equals("localhost"))
      throw new ConfigurationException("Cannot create a reference for an RMI registry whose host was unspecified or specified as \"localhost\""); 
    String str = "rmi://";
    str = (this.host.indexOf(":") > -1) ? (str + "[" + this.host + "]") : (str + this.host);
    if (this.port > 0)
      str = str + ":" + Integer.toString(this.port); 
    StringRefAddr stringRefAddr = new StringRefAddr("URL", str);
    return new Reference(RegistryContext.class.getName(), stringRefAddr, RegistryContextFactory.class.getName(), null);
  }
  
  public static NamingException wrapRemoteException(RemoteException paramRemoteException) {
    NamingException namingException;
    if (paramRemoteException instanceof java.rmi.ConnectException) {
      namingException = new ServiceUnavailableException();
    } else if (paramRemoteException instanceof java.rmi.AccessException) {
      namingException = new NoPermissionException();
    } else if (paramRemoteException instanceof java.rmi.StubNotFoundException || paramRemoteException instanceof java.rmi.UnknownHostException || paramRemoteException instanceof java.rmi.server.SocketSecurityException) {
      namingException = new ConfigurationException();
    } else if (paramRemoteException instanceof java.rmi.server.ExportException || paramRemoteException instanceof java.rmi.ConnectIOException || paramRemoteException instanceof java.rmi.MarshalException || paramRemoteException instanceof java.rmi.UnmarshalException || paramRemoteException instanceof java.rmi.NoSuchObjectException) {
      namingException = new CommunicationException();
    } else if (paramRemoteException instanceof java.rmi.ServerException && paramRemoteException.detail instanceof RemoteException) {
      namingException = wrapRemoteException((RemoteException)paramRemoteException.detail);
    } else {
      namingException = new NamingException();
    } 
    namingException.setRootCause(paramRemoteException);
    return namingException;
  }
  
  private static Registry getRegistry(String paramString, int paramInt, RMIClientSocketFactory paramRMIClientSocketFactory) throws NamingException {
    try {
      return (paramRMIClientSocketFactory == null) ? LocateRegistry.getRegistry(paramString, paramInt) : LocateRegistry.getRegistry(paramString, paramInt, paramRMIClientSocketFactory);
    } catch (RemoteException remoteException) {
      throw (NamingException)wrapRemoteException(remoteException).fillInStackTrace();
    } 
  }
  
  private static void installSecurityMgr() {
    try {
      System.setSecurityManager(new RMISecurityManager());
    } catch (Exception exception) {}
  }
  
  private Remote encodeObject(Object paramObject, Name paramName) throws NamingException, RemoteException {
    paramObject = NamingManager.getStateToBind(paramObject, paramName, this, this.environment);
    if (paramObject instanceof Remote)
      return (Remote)paramObject; 
    if (paramObject instanceof Reference)
      return new ReferenceWrapper((Reference)paramObject); 
    if (paramObject instanceof Referenceable)
      return new ReferenceWrapper(((Referenceable)paramObject).getReference()); 
    throw new IllegalArgumentException("RegistryContext: object to bind must be Remote, Reference, or Referenceable");
  }
  
  private Object decodeObject(Remote paramRemote, Name paramName) throws NamingException {
    try {
      Reference reference1 = (paramRemote instanceof RemoteReference) ? ((RemoteReference)paramRemote).getReference() : paramRemote;
      Reference reference2 = null;
      if (reference1 instanceof Reference) {
        reference2 = (Reference)reference1;
      } else if (reference1 instanceof Referenceable) {
        reference2 = ((Referenceable)reference1).getReference();
      } 
      if (reference2 != null && reference2.getFactoryClassLocation() != null && !trustURLCodebase)
        throw new ConfigurationException("The object factory is untrusted. Set the system property 'com.sun.jndi.rmi.object.trustURLCodebase' to 'true'."); 
      return NamingManager.getObjectInstance(reference1, paramName, this, this.environment);
    } catch (NamingException namingException) {
      throw namingException;
    } catch (RemoteException remoteException) {
      throw (NamingException)wrapRemoteException(remoteException).fillInStackTrace();
    } catch (Exception exception) {
      NamingException namingException = new NamingException();
      namingException.setRootCause(exception);
      throw namingException;
    } 
  }
  
  static  {
    PrivilegedAction privilegedAction = () -> System.getProperty("com.sun.jndi.rmi.object.trustURLCodebase", "false");
    String str = (String)AccessController.doPrivileged(privilegedAction);
    trustURLCodebase = "true".equalsIgnoreCase(str);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\rmi\registry\RegistryContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */