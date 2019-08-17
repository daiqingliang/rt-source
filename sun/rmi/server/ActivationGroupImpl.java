package sun.rmi.server;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.rmi.MarshalledObject;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.activation.Activatable;
import java.rmi.activation.ActivationDesc;
import java.rmi.activation.ActivationException;
import java.rmi.activation.ActivationGroup;
import java.rmi.activation.ActivationGroupID;
import java.rmi.activation.ActivationID;
import java.rmi.activation.UnknownObjectException;
import java.rmi.server.RMIClassLoader;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RMISocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import sun.rmi.registry.RegistryImpl;

public class ActivationGroupImpl extends ActivationGroup {
  private static final long serialVersionUID = 5758693559430427303L;
  
  private final Hashtable<ActivationID, ActiveEntry> active = new Hashtable();
  
  private boolean groupInactive = false;
  
  private final ActivationGroupID groupID;
  
  private final List<ActivationID> lockedIDs = new ArrayList();
  
  public ActivationGroupImpl(ActivationGroupID paramActivationGroupID, MarshalledObject<?> paramMarshalledObject) throws RemoteException {
    super(paramActivationGroupID);
    this.groupID = paramActivationGroupID;
    unexportObject(this, true);
    ServerSocketFactoryImpl serverSocketFactoryImpl = new ServerSocketFactoryImpl(null);
    UnicastRemoteObject.exportObject(this, 0, null, serverSocketFactoryImpl);
    if (System.getSecurityManager() == null)
      try {
        System.setSecurityManager(new SecurityManager());
      } catch (Exception exception) {
        throw new RemoteException("unable to set security manager", exception);
      }  
  }
  
  private void acquireLock(ActivationID paramActivationID) {
    while (true) {
      ActivationID activationID;
      synchronized (this.lockedIDs) {
        int i = this.lockedIDs.indexOf(paramActivationID);
        if (i < 0) {
          this.lockedIDs.add(paramActivationID);
          return;
        } 
        activationID = (ActivationID)this.lockedIDs.get(i);
      } 
      synchronized (activationID) {
        synchronized (this.lockedIDs) {
          int i = this.lockedIDs.indexOf(activationID);
          if (i < 0)
            continue; 
          ActivationID activationID1 = (ActivationID)this.lockedIDs.get(i);
          if (activationID1 != activationID)
            continue; 
        } 
        try {
          activationID.wait();
        } catch (InterruptedException interruptedException) {}
      } 
    } 
  }
  
  private void releaseLock(ActivationID paramActivationID) {
    synchronized (this.lockedIDs) {
      paramActivationID = (ActivationID)this.lockedIDs.remove(this.lockedIDs.indexOf(paramActivationID));
    } 
    synchronized (paramActivationID) {
      paramActivationID.notifyAll();
    } 
  }
  
  public MarshalledObject<? extends Remote> newInstance(final ActivationID id, final ActivationDesc desc) throws ActivationException, RemoteException {
    RegistryImpl.checkAccess("ActivationInstantiator.newInstance");
    if (!this.groupID.equals(paramActivationDesc.getGroupID()))
      throw new ActivationException("newInstance in wrong group"); 
    try {
      acquireLock(paramActivationID);
      synchronized (this) {
        if (this.groupInactive == true)
          throw new InactiveGroupException("group is inactive"); 
      } 
      ActiveEntry activeEntry = (ActiveEntry)this.active.get(paramActivationID);
      if (activeEntry != null)
        return activeEntry.mobj; 
      String str = paramActivationDesc.getClassName();
      final Class cl = RMIClassLoader.loadClass(paramActivationDesc.getLocation(), str).asSubclass(Remote.class);
      Remote remote = null;
      final Thread t = Thread.currentThread();
      final ClassLoader savedCcl = thread.getContextClassLoader();
      ClassLoader classLoader2 = clazz.getClassLoader();
      final ClassLoader ccl = covers(classLoader2, classLoader1) ? classLoader2 : classLoader1;
      try {
        remote = (Remote)AccessController.doPrivileged(new PrivilegedExceptionAction<Remote>() {
              public Remote run() throws InstantiationException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
                Constructor constructor = cl.getDeclaredConstructor(new Class[] { ActivationID.class, MarshalledObject.class });
                constructor.setAccessible(true);
                try {
                  t.setContextClassLoader(ccl);
                  return (Remote)constructor.newInstance(new Object[] { id, desc.getData() });
                } finally {
                  t.setContextClassLoader(savedCcl);
                } 
              }
            });
      } catch (PrivilegedActionException privilegedActionException) {
        Exception exception = privilegedActionException.getException();
        if (exception instanceof InstantiationException)
          throw (InstantiationException)exception; 
        if (exception instanceof NoSuchMethodException)
          throw (NoSuchMethodException)exception; 
        if (exception instanceof IllegalAccessException)
          throw (IllegalAccessException)exception; 
        if (exception instanceof InvocationTargetException)
          throw (InvocationTargetException)exception; 
        if (exception instanceof RuntimeException)
          throw (RuntimeException)exception; 
        if (exception instanceof Error)
          throw (Error)exception; 
      } 
      activeEntry = new ActiveEntry(remote);
      this.active.put(paramActivationID, activeEntry);
      return activeEntry.mobj;
    } catch (NoSuchMethodException|NoSuchMethodError noSuchMethodException) {
      throw new ActivationException("Activatable object must provide an activation constructor", noSuchMethodException);
    } catch (InvocationTargetException invocationTargetException) {
      throw new ActivationException("exception in object constructor", invocationTargetException.getTargetException());
    } catch (Exception exception) {
      throw new ActivationException("unable to activate object", exception);
    } finally {
      releaseLock(paramActivationID);
      checkInactiveGroup();
    } 
  }
  
  public boolean inactiveObject(ActivationID paramActivationID) throws ActivationException, UnknownObjectException, RemoteException {
    try {
      acquireLock(paramActivationID);
      synchronized (this) {
        if (this.groupInactive == true)
          throw new ActivationException("group is inactive"); 
      } 
      ActiveEntry activeEntry = (ActiveEntry)this.active.get(paramActivationID);
      if (activeEntry == null)
        throw new UnknownObjectException("object not active"); 
      try {
        if (!Activatable.unexportObject(activeEntry.impl, false))
          return false; 
      } catch (NoSuchObjectException noSuchObjectException) {}
      try {
        super.inactiveObject(paramActivationID);
      } catch (UnknownObjectException unknownObjectException) {}
      this.active.remove(paramActivationID);
      releaseLock(paramActivationID);
      checkInactiveGroup();
    } finally {
      releaseLock(paramActivationID);
      checkInactiveGroup();
    } 
    return true;
  }
  
  private void checkInactiveGroup() {
    boolean bool = false;
    synchronized (this) {
      if (this.active.size() == 0 && this.lockedIDs.size() == 0 && !this.groupInactive) {
        this.groupInactive = true;
        bool = true;
      } 
    } 
    if (bool) {
      try {
        inactiveGroup();
      } catch (Exception exception) {}
      try {
        UnicastRemoteObject.unexportObject(this, true);
      } catch (NoSuchObjectException noSuchObjectException) {}
    } 
  }
  
  public void activeObject(ActivationID paramActivationID, Remote paramRemote) throws ActivationException, UnknownObjectException, RemoteException {
    try {
      acquireLock(paramActivationID);
      synchronized (this) {
        if (this.groupInactive == true)
          throw new ActivationException("group is inactive"); 
      } 
      if (!this.active.contains(paramActivationID)) {
        ActiveEntry activeEntry = new ActiveEntry(paramRemote);
        this.active.put(paramActivationID, activeEntry);
        try {
          activeObject(paramActivationID, activeEntry.mobj);
        } catch (RemoteException remoteException) {}
      } 
    } finally {
      releaseLock(paramActivationID);
      checkInactiveGroup();
    } 
  }
  
  private static boolean covers(ClassLoader paramClassLoader1, ClassLoader paramClassLoader2) {
    if (paramClassLoader2 == null)
      return true; 
    if (paramClassLoader1 == null)
      return false; 
    do {
      if (paramClassLoader1 == paramClassLoader2)
        return true; 
      paramClassLoader1 = paramClassLoader1.getParent();
    } while (paramClassLoader1 != null);
    return false;
  }
  
  private static class ActiveEntry {
    Remote impl;
    
    MarshalledObject<Remote> mobj;
    
    ActiveEntry(Remote param1Remote) throws ActivationException {
      this.impl = param1Remote;
      try {
        this.mobj = new MarshalledObject(param1Remote);
      } catch (IOException iOException) {
        throw new ActivationException("failed to marshal remote object", iOException);
      } 
    }
  }
  
  private static class ServerSocketFactoryImpl implements RMIServerSocketFactory {
    private ServerSocketFactoryImpl() {}
    
    public ServerSocket createServerSocket(int param1Int) throws IOException {
      RMISocketFactory rMISocketFactory = RMISocketFactory.getSocketFactory();
      if (rMISocketFactory == null)
        rMISocketFactory = RMISocketFactory.getDefaultSocketFactory(); 
      return rMISocketFactory.createServerSocket(param1Int);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\server\ActivationGroupImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */