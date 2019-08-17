package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.impl.oa.NullServantImpl;
import org.omg.PortableServer.ForwardRequest;
import org.omg.PortableServer.POAPackage.NoServant;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantManager;

public class POAPolicyMediatorImpl_R_AOM extends POAPolicyMediatorBase_R {
  POAPolicyMediatorImpl_R_AOM(Policies paramPolicies, POAImpl paramPOAImpl) {
    super(paramPolicies, paramPOAImpl);
    if (!paramPolicies.useActiveMapOnly())
      throw paramPOAImpl.invocationWrapper().policyMediatorBadPolicyInFactory(); 
  }
  
  protected Object internalGetServant(byte[] paramArrayOfByte, String paramString) throws ForwardRequest {
    NullServantImpl nullServantImpl = internalIdToServant(paramArrayOfByte);
    if (nullServantImpl == null)
      nullServantImpl = new NullServantImpl(this.poa.invocationWrapper().nullServant()); 
    return nullServantImpl;
  }
  
  public void etherealizeAll() {}
  
  public ServantManager getServantManager() throws WrongPolicy { throw new WrongPolicy(); }
  
  public void setServantManager(ServantManager paramServantManager) throws WrongPolicy { throw new WrongPolicy(); }
  
  public Servant getDefaultServant() throws NoServant, WrongPolicy { throw new WrongPolicy(); }
  
  public void setDefaultServant(Servant paramServant) throws WrongPolicy { throw new WrongPolicy(); }
  
  public Servant idToServant(byte[] paramArrayOfByte) throws WrongPolicy, ObjectNotActive {
    Servant servant = internalIdToServant(paramArrayOfByte);
    if (servant == null)
      throw new ObjectNotActive(); 
    return servant;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\oa\poa\POAPolicyMediatorImpl_R_AOM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */