package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.impl.orbutil.ORBConstants;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.PortableServer.ForwardRequest;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.Servant;

public abstract class POAPolicyMediatorBase implements POAPolicyMediator {
  protected POAImpl poa;
  
  protected ORB orb;
  
  private int sysIdCounter;
  
  private Policies policies;
  
  private DelegateImpl delegateImpl;
  
  private int serverid;
  
  private int scid;
  
  protected boolean isImplicit;
  
  protected boolean isUnique;
  
  protected boolean isSystemId;
  
  public final Policies getPolicies() { return this.policies; }
  
  public final int getScid() { return this.scid; }
  
  public final int getServerId() { return this.serverid; }
  
  POAPolicyMediatorBase(Policies paramPolicies, POAImpl paramPOAImpl) {
    if (paramPolicies.isSingleThreaded())
      throw paramPOAImpl.invocationWrapper().singleThreadNotSupported(); 
    POAManagerImpl pOAManagerImpl = (POAManagerImpl)paramPOAImpl.the_POAManager();
    POAFactory pOAFactory = pOAManagerImpl.getFactory();
    this.delegateImpl = (DelegateImpl)pOAFactory.getDelegateImpl();
    this.policies = paramPolicies;
    this.poa = paramPOAImpl;
    this.orb = paramPOAImpl.getORB();
    switch (paramPolicies.servantCachingLevel()) {
      case 0:
        this.scid = 32;
        break;
      case 1:
        this.scid = 36;
        break;
      case 2:
        this.scid = 40;
        break;
      case 3:
        this.scid = 44;
        break;
    } 
    if (paramPolicies.isTransient()) {
      this.serverid = this.orb.getTransientServerId();
    } else {
      this.serverid = this.orb.getORBData().getPersistentServerId();
      this.scid = ORBConstants.makePersistent(this.scid);
    } 
    this.isImplicit = paramPolicies.isImplicitlyActivated();
    this.isUnique = paramPolicies.isUniqueIds();
    this.isSystemId = paramPolicies.isSystemAssignedIds();
    this.sysIdCounter = 0;
  }
  
  public final Object getInvocationServant(byte[] paramArrayOfByte, String paramString) throws ForwardRequest { return internalGetServant(paramArrayOfByte, paramString); }
  
  protected final void setDelegate(Servant paramServant, byte[] paramArrayOfByte) { paramServant._set_delegate(this.delegateImpl); }
  
  public byte[] newSystemId() throws WrongPolicy {
    if (!this.isSystemId)
      throw new WrongPolicy(); 
    byte[] arrayOfByte = new byte[8];
    ORBUtility.intToBytes(++this.sysIdCounter, arrayOfByte, 0);
    ORBUtility.intToBytes(this.poa.getPOAId(), arrayOfByte, 4);
    return arrayOfByte;
  }
  
  protected abstract Object internalGetServant(byte[] paramArrayOfByte, String paramString) throws ForwardRequest;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\oa\poa\POAPolicyMediatorBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */