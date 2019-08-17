package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.impl.oa.NullServantImpl;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import java.util.Set;
import org.omg.CORBA.SystemException;
import org.omg.PortableServer.ForwardRequest;
import org.omg.PortableServer.POAPackage.NoServant;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantActivator;
import org.omg.PortableServer.ServantManager;

public class POAPolicyMediatorImpl_R_USM extends POAPolicyMediatorBase_R {
  protected ServantActivator activator = null;
  
  POAPolicyMediatorImpl_R_USM(Policies paramPolicies, POAImpl paramPOAImpl) {
    super(paramPolicies, paramPOAImpl);
    if (!paramPolicies.useServantManager())
      throw paramPOAImpl.invocationWrapper().policyMediatorBadPolicyInFactory(); 
  }
  
  private AOMEntry enterEntry(ActiveObjectMap.Key paramKey) {
    boolean bool;
    AOMEntry aOMEntry = null;
    do {
      bool = false;
      aOMEntry = this.activeObjectMap.get(paramKey);
      try {
        aOMEntry.enter();
      } catch (Exception exception) {
        bool = true;
      } 
    } while (bool);
    return aOMEntry;
  }
  
  protected Object internalGetServant(byte[] paramArrayOfByte, String paramString) throws ForwardRequest {
    if (this.poa.getDebug())
      ORBUtility.dprint(this, "Calling POAPolicyMediatorImpl_R_USM.internalGetServant for poa " + this.poa + " operation=" + paramString); 
    try {
      key = new ActiveObjectMap.Key(paramArrayOfByte);
      aOMEntry = enterEntry(key);
      nullServantImpl = this.activeObjectMap.getServant(aOMEntry);
      if (nullServantImpl != null) {
        if (this.poa.getDebug())
          ORBUtility.dprint(this, "internalGetServant: servant already activated"); 
        return nullServantImpl;
      } 
      if (this.activator == null) {
        if (this.poa.getDebug())
          ORBUtility.dprint(this, "internalGetServant: no servant activator in POA"); 
        aOMEntry.incarnateFailure();
        throw this.poa.invocationWrapper().poaNoServantManager();
      } 
      try {
        if (this.poa.getDebug())
          ORBUtility.dprint(this, "internalGetServant: upcall to incarnate"); 
        this.poa.unlock();
        nullServantImpl = this.activator.incarnate(paramArrayOfByte, this.poa);
        if (nullServantImpl == null)
          nullServantImpl = new NullServantImpl(this.poa.omgInvocationWrapper().nullServantReturned()); 
      } catch (ForwardRequest forwardRequest) {
        if (this.poa.getDebug())
          ORBUtility.dprint(this, "internalGetServant: incarnate threw ForwardRequest"); 
        throw forwardRequest;
      } catch (SystemException systemException) {
        if (this.poa.getDebug())
          ORBUtility.dprint(this, "internalGetServant: incarnate threw SystemException " + systemException); 
        throw systemException;
      } catch (Throwable throwable) {
        if (this.poa.getDebug())
          ORBUtility.dprint(this, "internalGetServant: incarnate threw Throwable " + throwable); 
        throw this.poa.invocationWrapper().poaServantActivatorLookupFailed(throwable);
      } finally {
        this.poa.lock();
        if (nullServantImpl == null || nullServantImpl instanceof com.sun.corba.se.spi.oa.NullServant) {
          if (this.poa.getDebug())
            ORBUtility.dprint(this, "internalGetServant: incarnate failed"); 
          aOMEntry.incarnateFailure();
        } else {
          if (this.isUnique && this.activeObjectMap.contains((Servant)nullServantImpl)) {
            if (this.poa.getDebug())
              ORBUtility.dprint(this, "internalGetServant: servant already assigned to ID"); 
            aOMEntry.incarnateFailure();
            throw this.poa.invocationWrapper().poaServantNotUnique();
          } 
          if (this.poa.getDebug())
            ORBUtility.dprint(this, "internalGetServant: incarnate complete"); 
          aOMEntry.incarnateComplete();
          activateServant(key, aOMEntry, (Servant)nullServantImpl);
        } 
      } 
      return nullServantImpl;
    } finally {
      if (this.poa.getDebug())
        ORBUtility.dprint(this, "Exiting POAPolicyMediatorImpl_R_USM.internalGetServant for poa " + this.poa); 
    } 
  }
  
  public void returnServant() {
    OAInvocationInfo oAInvocationInfo = this.orb.peekInvocationInfo();
    byte[] arrayOfByte = oAInvocationInfo.id();
    ActiveObjectMap.Key key = new ActiveObjectMap.Key(arrayOfByte);
    AOMEntry aOMEntry = this.activeObjectMap.get(key);
    aOMEntry.exit();
  }
  
  public void etherealizeAll() {
    if (this.activator != null) {
      Set set = this.activeObjectMap.keySet();
      Key[] arrayOfKey = (Key[])set.toArray(new ActiveObjectMap.Key[set.size()]);
      for (byte b = 0; b < set.size(); b++) {
        Key key = arrayOfKey[b];
        aOMEntry = this.activeObjectMap.get(key);
        Servant servant = this.activeObjectMap.getServant(aOMEntry);
        if (servant != null) {
          boolean bool = this.activeObjectMap.hasMultipleIDs(aOMEntry);
          aOMEntry.startEtherealize(null);
          try {
            this.poa.unlock();
            try {
              this.activator.etherealize(key.id, this.poa, servant, true, bool);
            } catch (Exception exception) {}
          } finally {
            this.poa.lock();
            aOMEntry.etherealizeComplete();
          } 
        } 
      } 
    } 
  }
  
  public ServantManager getServantManager() throws WrongPolicy { return this.activator; }
  
  public void setServantManager(ServantManager paramServantManager) throws WrongPolicy {
    if (this.activator != null)
      throw this.poa.invocationWrapper().servantManagerAlreadySet(); 
    if (paramServantManager instanceof ServantActivator) {
      this.activator = (ServantActivator)paramServantManager;
    } else {
      throw this.poa.invocationWrapper().servantManagerBadType();
    } 
  }
  
  public Servant getDefaultServant() throws NoServant, WrongPolicy { throw new WrongPolicy(); }
  
  public void setDefaultServant(Servant paramServant) throws WrongPolicy { throw new WrongPolicy(); }
  
  public void deactivateHelper(ActiveObjectMap.Key paramKey, AOMEntry paramAOMEntry, Servant paramServant) throws ObjectNotActive, WrongPolicy {
    if (this.activator == null)
      throw this.poa.invocationWrapper().poaNoServantManager(); 
    Etherealizer etherealizer = new Etherealizer(this, paramKey, paramAOMEntry, paramServant, this.poa.getDebug());
    paramAOMEntry.startEtherealize(etherealizer);
  }
  
  public Servant idToServant(byte[] paramArrayOfByte) throws WrongPolicy, ObjectNotActive {
    ActiveObjectMap.Key key = new ActiveObjectMap.Key(paramArrayOfByte);
    AOMEntry aOMEntry = this.activeObjectMap.get(key);
    Servant servant = this.activeObjectMap.getServant(aOMEntry);
    if (servant != null)
      return servant; 
    throw new ObjectNotActive();
  }
  
  class Etherealizer extends Thread {
    private POAPolicyMediatorImpl_R_USM mediator;
    
    private ActiveObjectMap.Key key;
    
    private AOMEntry entry;
    
    private Servant servant;
    
    private boolean debug;
    
    public Etherealizer(POAPolicyMediatorImpl_R_USM param1POAPolicyMediatorImpl_R_USM1, ActiveObjectMap.Key param1Key, AOMEntry param1AOMEntry, Servant param1Servant, boolean param1Boolean) {
      this.mediator = param1POAPolicyMediatorImpl_R_USM1;
      this.key = param1Key;
      this.entry = param1AOMEntry;
      this.servant = param1Servant;
      this.debug = param1Boolean;
    }
    
    public void run() {
      if (this.debug)
        ORBUtility.dprint(this, "Calling Etherealizer.run on key " + this.key); 
      try {
        try {
          this.mediator.activator.etherealize(this.key.id, this.mediator.poa, this.servant, false, this.mediator.activeObjectMap.hasMultipleIDs(this.entry));
        } catch (Exception exception) {}
        try {
          this.mediator.poa.lock();
          this.entry.etherealizeComplete();
          this.mediator.activeObjectMap.remove(this.key);
          POAManagerImpl pOAManagerImpl = (POAManagerImpl)this.mediator.poa.the_POAManager();
          POAFactory pOAFactory = pOAManagerImpl.getFactory();
          pOAFactory.unregisterPOAForServant(this.mediator.poa, this.servant);
        } finally {
          this.mediator.poa.unlock();
        } 
      } finally {
        if (this.debug)
          ORBUtility.dprint(this, "Exiting Etherealizer.run"); 
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\oa\poa\POAPolicyMediatorImpl_R_USM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */