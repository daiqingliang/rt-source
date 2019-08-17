package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.impl.oa.NullServantImpl;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import org.omg.PortableServer.ForwardRequest;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAPackage.NoServant;
import org.omg.PortableServer.POAPackage.ObjectAlreadyActive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantLocator;
import org.omg.PortableServer.ServantLocatorPackage.CookieHolder;
import org.omg.PortableServer.ServantManager;

public class POAPolicyMediatorImpl_NR_USM extends POAPolicyMediatorBase {
  private ServantLocator locator;
  
  POAPolicyMediatorImpl_NR_USM(Policies paramPolicies, POAImpl paramPOAImpl) {
    super(paramPolicies, paramPOAImpl);
    if (paramPolicies.retainServants())
      throw paramPOAImpl.invocationWrapper().policyMediatorBadPolicyInFactory(); 
    if (!paramPolicies.useServantManager())
      throw paramPOAImpl.invocationWrapper().policyMediatorBadPolicyInFactory(); 
    this.locator = null;
  }
  
  protected Object internalGetServant(byte[] paramArrayOfByte, String paramString) throws ForwardRequest {
    NullServantImpl nullServantImpl;
    if (this.locator == null)
      throw this.poa.invocationWrapper().poaNoServantManager(); 
    CookieHolder cookieHolder = this.orb.peekInvocationInfo().getCookieHolder();
    try {
      this.poa.unlock();
      nullServantImpl = this.locator.preinvoke(paramArrayOfByte, this.poa, paramString, cookieHolder);
      if (nullServantImpl == null) {
        nullServantImpl = new NullServantImpl(this.poa.omgInvocationWrapper().nullServantReturned());
      } else {
        setDelegate((Servant)nullServantImpl, paramArrayOfByte);
      } 
    } finally {
      this.poa.lock();
    } 
    return nullServantImpl;
  }
  
  public void returnServant() {
    OAInvocationInfo oAInvocationInfo = this.orb.peekInvocationInfo();
    if (this.locator == null)
      return; 
    try {
      this.poa.unlock();
      this.locator.postinvoke(oAInvocationInfo.id(), (POA)oAInvocationInfo.oa(), oAInvocationInfo.getOperation(), (oAInvocationInfo.getCookieHolder()).value, (Servant)oAInvocationInfo.getServantContainer());
    } finally {
      this.poa.lock();
    } 
  }
  
  public void etherealizeAll() {}
  
  public void clearAOM() {}
  
  public ServantManager getServantManager() throws WrongPolicy { return this.locator; }
  
  public void setServantManager(ServantManager paramServantManager) throws WrongPolicy {
    if (this.locator != null)
      throw this.poa.invocationWrapper().servantManagerAlreadySet(); 
    if (paramServantManager instanceof ServantLocator) {
      this.locator = (ServantLocator)paramServantManager;
    } else {
      throw this.poa.invocationWrapper().servantManagerBadType();
    } 
  }
  
  public Servant getDefaultServant() throws NoServant, WrongPolicy { throw new WrongPolicy(); }
  
  public void setDefaultServant(Servant paramServant) throws WrongPolicy { throw new WrongPolicy(); }
  
  public final void activateObject(byte[] paramArrayOfByte, Servant paramServant) throws WrongPolicy, ServantAlreadyActive, ObjectAlreadyActive { throw new WrongPolicy(); }
  
  public Servant deactivateObject(byte[] paramArrayOfByte) throws ObjectNotActive, WrongPolicy { throw new WrongPolicy(); }
  
  public byte[] servantToId(Servant paramServant) throws ServantNotActive, WrongPolicy { throw new WrongPolicy(); }
  
  public Servant idToServant(byte[] paramArrayOfByte) throws ObjectNotActive, WrongPolicy { throw new WrongPolicy(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\oa\poa\POAPolicyMediatorImpl_NR_USM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */