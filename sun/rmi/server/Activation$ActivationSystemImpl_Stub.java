package sun.rmi.server;

import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.UnexpectedException;
import java.rmi.activation.ActivationDesc;
import java.rmi.activation.ActivationException;
import java.rmi.activation.ActivationGroupDesc;
import java.rmi.activation.ActivationGroupID;
import java.rmi.activation.ActivationID;
import java.rmi.activation.ActivationInstantiator;
import java.rmi.activation.ActivationMonitor;
import java.rmi.activation.ActivationSystem;
import java.rmi.activation.UnknownGroupException;
import java.rmi.activation.UnknownObjectException;
import java.rmi.server.RemoteRef;
import java.rmi.server.RemoteStub;

public final class Activation$ActivationSystemImpl_Stub extends RemoteStub implements ActivationSystem, Remote {
  private static final long serialVersionUID = 2L;
  
  private static Method $method_activeGroup_0;
  
  private static Method $method_getActivationDesc_1;
  
  private static Method $method_getActivationGroupDesc_2;
  
  private static Method $method_registerGroup_3;
  
  private static Method $method_registerObject_4;
  
  private static Method $method_setActivationDesc_5;
  
  private static Method $method_setActivationGroupDesc_6;
  
  private static Method $method_shutdown_7;
  
  private static Method $method_unregisterGroup_8;
  
  private static Method $method_unregisterObject_9;
  
  static  {
    try {
      $method_activeGroup_0 = ActivationSystem.class.getMethod("activeGroup", new Class[] { ActivationGroupID.class, ActivationInstantiator.class, long.class });
      $method_getActivationDesc_1 = ActivationSystem.class.getMethod("getActivationDesc", new Class[] { ActivationID.class });
      $method_getActivationGroupDesc_2 = ActivationSystem.class.getMethod("getActivationGroupDesc", new Class[] { ActivationGroupID.class });
      $method_registerGroup_3 = ActivationSystem.class.getMethod("registerGroup", new Class[] { ActivationGroupDesc.class });
      $method_registerObject_4 = ActivationSystem.class.getMethod("registerObject", new Class[] { ActivationDesc.class });
      $method_setActivationDesc_5 = ActivationSystem.class.getMethod("setActivationDesc", new Class[] { ActivationID.class, ActivationDesc.class });
      $method_setActivationGroupDesc_6 = ActivationSystem.class.getMethod("setActivationGroupDesc", new Class[] { ActivationGroupID.class, ActivationGroupDesc.class });
      $method_shutdown_7 = ActivationSystem.class.getMethod("shutdown", new Class[0]);
      $method_unregisterGroup_8 = ActivationSystem.class.getMethod("unregisterGroup", new Class[] { ActivationGroupID.class });
      $method_unregisterObject_9 = ActivationSystem.class.getMethod("unregisterObject", new Class[] { ActivationID.class });
    } catch (NoSuchMethodException noSuchMethodException) {
      throw new NoSuchMethodError("stub class initialization failed");
    } 
  }
  
  public Activation$ActivationSystemImpl_Stub(RemoteRef paramRemoteRef) { super(paramRemoteRef); }
  
  public ActivationMonitor activeGroup(ActivationGroupID paramActivationGroupID, ActivationInstantiator paramActivationInstantiator, long paramLong) throws RemoteException, ActivationException, UnknownGroupException {
    try {
      Object object = this.ref.invoke(this, $method_activeGroup_0, new Object[] { paramActivationGroupID, paramActivationInstantiator, new Long(paramLong) }, -4575843150759415294L);
      return (ActivationMonitor)object;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (RemoteException remoteException) {
      throw remoteException;
    } catch (ActivationException activationException) {
      throw activationException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public ActivationDesc getActivationDesc(ActivationID paramActivationID) throws RemoteException, ActivationException, UnknownObjectException {
    try {
      Object object = this.ref.invoke(this, $method_getActivationDesc_1, new Object[] { paramActivationID }, 4830055440982622087L);
      return (ActivationDesc)object;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (RemoteException remoteException) {
      throw remoteException;
    } catch (ActivationException activationException) {
      throw activationException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public ActivationGroupDesc getActivationGroupDesc(ActivationGroupID paramActivationGroupID) throws RemoteException, ActivationException, UnknownGroupException {
    try {
      Object object = this.ref.invoke(this, $method_getActivationGroupDesc_2, new Object[] { paramActivationGroupID }, -8701843806548736528L);
      return (ActivationGroupDesc)object;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (RemoteException remoteException) {
      throw remoteException;
    } catch (ActivationException activationException) {
      throw activationException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public ActivationGroupID registerGroup(ActivationGroupDesc paramActivationGroupDesc) throws RemoteException, ActivationException {
    try {
      Object object = this.ref.invoke(this, $method_registerGroup_3, new Object[] { paramActivationGroupDesc }, 6921515268192657754L);
      return (ActivationGroupID)object;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (RemoteException remoteException) {
      throw remoteException;
    } catch (ActivationException activationException) {
      throw activationException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public ActivationID registerObject(ActivationDesc paramActivationDesc) throws RemoteException, ActivationException, UnknownGroupException {
    try {
      Object object = this.ref.invoke(this, $method_registerObject_4, new Object[] { paramActivationDesc }, -3006759798994351347L);
      return (ActivationID)object;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (RemoteException remoteException) {
      throw remoteException;
    } catch (ActivationException activationException) {
      throw activationException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public ActivationDesc setActivationDesc(ActivationID paramActivationID, ActivationDesc paramActivationDesc) throws RemoteException, ActivationException, UnknownGroupException, UnknownObjectException {
    try {
      Object object = this.ref.invoke(this, $method_setActivationDesc_5, new Object[] { paramActivationID, paramActivationDesc }, 7128043237057180796L);
      return (ActivationDesc)object;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (RemoteException remoteException) {
      throw remoteException;
    } catch (ActivationException activationException) {
      throw activationException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public ActivationGroupDesc setActivationGroupDesc(ActivationGroupID paramActivationGroupID, ActivationGroupDesc paramActivationGroupDesc) throws RemoteException, ActivationException, UnknownGroupException {
    try {
      Object object = this.ref.invoke(this, $method_setActivationGroupDesc_6, new Object[] { paramActivationGroupID, paramActivationGroupDesc }, 1213918527826541191L);
      return (ActivationGroupDesc)object;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (RemoteException remoteException) {
      throw remoteException;
    } catch (ActivationException activationException) {
      throw activationException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public void shutdown() throws RemoteException {
    try {
      this.ref.invoke(this, $method_shutdown_7, null, -7207851917985848402L);
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (RemoteException remoteException) {
      throw remoteException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public void unregisterGroup(ActivationGroupID paramActivationGroupID) throws RemoteException, ActivationException, UnknownGroupException {
    try {
      this.ref.invoke(this, $method_unregisterGroup_8, new Object[] { paramActivationGroupID }, 3768097077835970701L);
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (RemoteException remoteException) {
      throw remoteException;
    } catch (ActivationException activationException) {
      throw activationException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
  
  public void unregisterObject(ActivationID paramActivationID) throws RemoteException, ActivationException, UnknownObjectException {
    try {
      this.ref.invoke(this, $method_unregisterObject_9, new Object[] { paramActivationID }, -6843850585331411084L);
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (RemoteException remoteException) {
      throw remoteException;
    } catch (ActivationException activationException) {
      throw activationException;
    } catch (Exception exception) {
      throw new UnexpectedException("undeclared checked exception", exception);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\server\Activation$ActivationSystemImpl_Stub.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.0.7
 */