package com.sun.corba.se.spi.presentation.rmi;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.oa.poa.POAManagerImpl;
import com.sun.corba.se.spi.orb.ORB;
import java.rmi.RemoteException;
import javax.rmi.CORBA.Stub;
import javax.rmi.CORBA.Tie;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.Servant;

public abstract class StubAdapter {
  private static ORBUtilSystemException wrapper = ORBUtilSystemException.get("rpc.presentation");
  
  public static boolean isStubClass(Class paramClass) { return (ObjectImpl.class.isAssignableFrom(paramClass) || DynamicStub.class.isAssignableFrom(paramClass)); }
  
  public static boolean isStub(Object paramObject) { return (paramObject instanceof DynamicStub || paramObject instanceof ObjectImpl); }
  
  public static void setDelegate(Object paramObject, Delegate paramDelegate) {
    if (paramObject instanceof DynamicStub) {
      ((DynamicStub)paramObject).setDelegate(paramDelegate);
    } else if (paramObject instanceof ObjectImpl) {
      ((ObjectImpl)paramObject)._set_delegate(paramDelegate);
    } else {
      throw wrapper.setDelegateRequiresStub();
    } 
  }
  
  public static Object activateServant(Servant paramServant) {
    POA pOA = paramServant._default_POA();
    Object object = null;
    try {
      object = pOA.servant_to_reference(paramServant);
    } catch (ServantNotActive servantNotActive) {
      throw wrapper.getDelegateServantNotActive(servantNotActive);
    } catch (WrongPolicy wrongPolicy) {
      throw wrapper.getDelegateWrongPolicy(wrongPolicy);
    } 
    POAManager pOAManager = pOA.the_POAManager();
    if (pOAManager instanceof POAManagerImpl) {
      POAManagerImpl pOAManagerImpl = (POAManagerImpl)pOAManager;
      pOAManagerImpl.implicitActivation();
    } 
    return object;
  }
  
  public static Object activateTie(Tie paramTie) {
    if (paramTie instanceof ObjectImpl)
      return paramTie.thisObject(); 
    if (paramTie instanceof Servant) {
      Servant servant = (Servant)paramTie;
      return activateServant(servant);
    } 
    throw wrapper.badActivateTieCall();
  }
  
  public static Delegate getDelegate(Object paramObject) {
    if (paramObject instanceof DynamicStub)
      return ((DynamicStub)paramObject).getDelegate(); 
    if (paramObject instanceof ObjectImpl)
      return ((ObjectImpl)paramObject)._get_delegate(); 
    if (paramObject instanceof Tie) {
      Tie tie = (Tie)paramObject;
      Object object = activateTie(tie);
      return getDelegate(object);
    } 
    throw wrapper.getDelegateRequiresStub();
  }
  
  public static ORB getORB(Object paramObject) {
    if (paramObject instanceof DynamicStub)
      return ((DynamicStub)paramObject).getORB(); 
    if (paramObject instanceof ObjectImpl)
      return ((ObjectImpl)paramObject)._orb(); 
    throw wrapper.getOrbRequiresStub();
  }
  
  public static String[] getTypeIds(Object paramObject) {
    if (paramObject instanceof DynamicStub)
      return ((DynamicStub)paramObject).getTypeIds(); 
    if (paramObject instanceof ObjectImpl)
      return ((ObjectImpl)paramObject)._ids(); 
    throw wrapper.getTypeIdsRequiresStub();
  }
  
  public static void connect(Object paramObject, ORB paramORB) throws RemoteException {
    if (paramObject instanceof DynamicStub) {
      ((DynamicStub)paramObject).connect((ORB)paramORB);
    } else if (paramObject instanceof Stub) {
      ((Stub)paramObject).connect(paramORB);
    } else if (paramObject instanceof ObjectImpl) {
      paramORB.connect((Object)paramObject);
    } else {
      throw wrapper.connectRequiresStub();
    } 
  }
  
  public static boolean isLocal(Object paramObject) {
    if (paramObject instanceof DynamicStub)
      return ((DynamicStub)paramObject).isLocal(); 
    if (paramObject instanceof ObjectImpl)
      return ((ObjectImpl)paramObject)._is_local(); 
    throw wrapper.isLocalRequiresStub();
  }
  
  public static OutputStream request(Object paramObject, String paramString, boolean paramBoolean) {
    if (paramObject instanceof DynamicStub)
      return ((DynamicStub)paramObject).request(paramString, paramBoolean); 
    if (paramObject instanceof ObjectImpl)
      return ((ObjectImpl)paramObject)._request(paramString, paramBoolean); 
    throw wrapper.requestRequiresStub();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\presentation\rmi\StubAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */