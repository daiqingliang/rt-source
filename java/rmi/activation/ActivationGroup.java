package java.rmi.activation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.rmi.MarshalledObject;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RMIClassLoader;
import java.rmi.server.UnicastRemoteObject;
import java.security.AccessController;
import sun.security.action.GetIntegerAction;

public abstract class ActivationGroup extends UnicastRemoteObject implements ActivationInstantiator {
  private ActivationGroupID groupID;
  
  private ActivationMonitor monitor;
  
  private long incarnation;
  
  private static ActivationGroup currGroup;
  
  private static ActivationGroupID currGroupID;
  
  private static ActivationSystem currSystem;
  
  private static boolean canCreate = true;
  
  private static final long serialVersionUID = -7696947875314805420L;
  
  protected ActivationGroup(ActivationGroupID paramActivationGroupID) throws RemoteException { this.groupID = paramActivationGroupID; }
  
  public boolean inactiveObject(ActivationID paramActivationID) throws ActivationException, UnknownObjectException, RemoteException {
    getMonitor().inactiveObject(paramActivationID);
    return true;
  }
  
  public abstract void activeObject(ActivationID paramActivationID, Remote paramRemote) throws ActivationException, UnknownObjectException, RemoteException;
  
  public static ActivationGroup createGroup(ActivationGroupID paramActivationGroupID, ActivationGroupDesc paramActivationGroupDesc, long paramLong) throws ActivationException {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkSetFactory(); 
    if (currGroup != null)
      throw new ActivationException("group already exists"); 
    if (!canCreate)
      throw new ActivationException("group deactivated and cannot be recreated"); 
    try {
      Class clazz1;
      String str = paramActivationGroupDesc.getClassName();
      Class clazz2 = sun.rmi.server.ActivationGroupImpl.class;
      if (str == null || str.equals(clazz2.getName())) {
        clazz1 = clazz2;
      } else {
        Class clazz;
        try {
          clazz = RMIClassLoader.loadClass(paramActivationGroupDesc.getLocation(), str);
        } catch (Exception exception) {
          throw new ActivationException("Could not load group implementation class", exception);
        } 
        if (ActivationGroup.class.isAssignableFrom(clazz)) {
          clazz1 = clazz.asSubclass(ActivationGroup.class);
        } else {
          throw new ActivationException("group not correct class: " + clazz.getName());
        } 
      } 
      Constructor constructor = clazz1.getConstructor(new Class[] { ActivationGroupID.class, MarshalledObject.class });
      ActivationGroup activationGroup = (ActivationGroup)constructor.newInstance(new Object[] { paramActivationGroupID, paramActivationGroupDesc.getData() });
      currSystem = paramActivationGroupID.getSystem();
      activationGroup.incarnation = paramLong;
      activationGroup.monitor = currSystem.activeGroup(paramActivationGroupID, activationGroup, paramLong);
      currGroup = activationGroup;
      currGroupID = paramActivationGroupID;
      canCreate = false;
    } catch (InvocationTargetException invocationTargetException) {
      invocationTargetException.getTargetException().printStackTrace();
      throw new ActivationException("exception in group constructor", invocationTargetException.getTargetException());
    } catch (ActivationException activationException) {
      throw activationException;
    } catch (Exception exception) {
      throw new ActivationException("exception creating group", exception);
    } 
    return currGroup;
  }
  
  public static ActivationGroupID currentGroupID() { return currGroupID; }
  
  static ActivationGroupID internalCurrentGroupID() {
    if (currGroupID == null)
      throw new ActivationException("nonexistent group"); 
    return currGroupID;
  }
  
  public static void setSystem(ActivationSystem paramActivationSystem) throws ActivationException {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkSetFactory(); 
    if (currSystem != null)
      throw new ActivationException("activation system already set"); 
    currSystem = paramActivationSystem;
  }
  
  public static ActivationSystem getSystem() throws ActivationException {
    if (currSystem == null)
      try {
        int i = ((Integer)AccessController.doPrivileged(new GetIntegerAction("java.rmi.activation.port", 1098))).intValue();
        currSystem = (ActivationSystem)Naming.lookup("//:" + i + "/java.rmi.activation.ActivationSystem");
      } catch (Exception exception) {
        throw new ActivationException("unable to obtain ActivationSystem", exception);
      }  
    return currSystem;
  }
  
  protected void activeObject(ActivationID paramActivationID, MarshalledObject<? extends Remote> paramMarshalledObject) throws ActivationException, UnknownObjectException, RemoteException { getMonitor().activeObject(paramActivationID, paramMarshalledObject); }
  
  protected void inactiveGroup() throws UnknownGroupException, RemoteException {
    try {
      getMonitor().inactiveGroup(this.groupID, this.incarnation);
    } finally {
      destroyGroup();
    } 
  }
  
  private ActivationMonitor getMonitor() throws RemoteException {
    synchronized (ActivationGroup.class) {
      if (this.monitor != null)
        return this.monitor; 
    } 
    throw new RemoteException("monitor not received");
  }
  
  private static void destroyGroup() throws UnknownGroupException, RemoteException {
    currGroup = null;
    currGroupID = null;
  }
  
  static ActivationGroup currentGroup() throws ActivationException {
    if (currGroup == null)
      throw new ActivationException("group is not active"); 
    return currGroup;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\rmi\activation\ActivationGroup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */