package com.sun.corba.se.impl.oa.poa;

import org.omg.PortableServer.ForwardRequest;
import org.omg.PortableServer.POAPackage.NoServant;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantManager;

public class POAPolicyMediatorImpl_R_UDS extends POAPolicyMediatorBase_R {
  private Servant defaultServant = null;
  
  POAPolicyMediatorImpl_R_UDS(Policies paramPolicies, POAImpl paramPOAImpl) {
    super(paramPolicies, paramPOAImpl);
    if (!paramPolicies.useDefaultServant())
      throw paramPOAImpl.invocationWrapper().policyMediatorBadPolicyInFactory(); 
  }
  
  protected Object internalGetServant(byte[] paramArrayOfByte, String paramString) throws ForwardRequest {
    Servant servant = internalIdToServant(paramArrayOfByte);
    if (servant == null)
      servant = this.defaultServant; 
    if (servant == null)
      throw this.poa.invocationWrapper().poaNoDefaultServant(); 
    return servant;
  }
  
  public void etherealizeAll() {}
  
  public ServantManager getServantManager() throws WrongPolicy { throw new WrongPolicy(); }
  
  public void setServantManager(ServantManager paramServantManager) throws WrongPolicy { throw new WrongPolicy(); }
  
  public Servant getDefaultServant() throws NoServant, WrongPolicy {
    if (this.defaultServant == null)
      throw new NoServant(); 
    return this.defaultServant;
  }
  
  public void setDefaultServant(Servant paramServant) throws WrongPolicy {
    this.defaultServant = paramServant;
    setDelegate(this.defaultServant, "DefaultServant".getBytes());
  }
  
  public Servant idToServant(byte[] paramArrayOfByte) throws WrongPolicy, ObjectNotActive {
    ActiveObjectMap.Key key = new ActiveObjectMap.Key(paramArrayOfByte);
    Servant servant = internalKeyToServant(key);
    if (servant == null && this.defaultServant != null)
      servant = this.defaultServant; 
    if (servant == null)
      throw new ObjectNotActive(); 
    return servant;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\oa\poa\POAPolicyMediatorImpl_R_UDS.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */