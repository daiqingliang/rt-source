package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.impl.javax.rmi.CORBA.Util;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import org.omg.PortableServer.POAPackage.ObjectAlreadyActive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.Servant;

public abstract class POAPolicyMediatorBase_R extends POAPolicyMediatorBase {
  protected ActiveObjectMap activeObjectMap;
  
  POAPolicyMediatorBase_R(Policies paramPolicies, POAImpl paramPOAImpl) {
    super(paramPolicies, paramPOAImpl);
    if (!paramPolicies.retainServants())
      throw paramPOAImpl.invocationWrapper().policyMediatorBadPolicyInFactory(); 
    this.activeObjectMap = ActiveObjectMap.create(paramPOAImpl, !this.isUnique);
  }
  
  public void returnServant() {}
  
  public void clearAOM() {
    this.activeObjectMap.clear();
    this.activeObjectMap = null;
  }
  
  protected Servant internalKeyToServant(ActiveObjectMap.Key paramKey) {
    AOMEntry aOMEntry = this.activeObjectMap.get(paramKey);
    return (aOMEntry == null) ? null : this.activeObjectMap.getServant(aOMEntry);
  }
  
  protected Servant internalIdToServant(byte[] paramArrayOfByte) {
    ActiveObjectMap.Key key = new ActiveObjectMap.Key(paramArrayOfByte);
    return internalKeyToServant(key);
  }
  
  protected void activateServant(ActiveObjectMap.Key paramKey, AOMEntry paramAOMEntry, Servant paramServant) {
    setDelegate(paramServant, paramKey.id);
    if (this.orb.shutdownDebugFlag)
      System.out.println("Activating object " + paramServant + " with POA " + this.poa); 
    this.activeObjectMap.putServant(paramServant, paramAOMEntry);
    if (Util.isInstanceDefined()) {
      POAManagerImpl pOAManagerImpl = (POAManagerImpl)this.poa.the_POAManager();
      POAFactory pOAFactory = pOAManagerImpl.getFactory();
      pOAFactory.registerPOAForServant(this.poa, paramServant);
    } 
  }
  
  public final void activateObject(byte[] paramArrayOfByte, Servant paramServant) throws WrongPolicy, ServantAlreadyActive, ObjectAlreadyActive {
    if (this.isUnique && this.activeObjectMap.contains(paramServant))
      throw new ServantAlreadyActive(); 
    ActiveObjectMap.Key key = new ActiveObjectMap.Key(paramArrayOfByte);
    AOMEntry aOMEntry = this.activeObjectMap.get(key);
    aOMEntry.activateObject();
    activateServant(key, aOMEntry, paramServant);
  }
  
  public Servant deactivateObject(byte[] paramArrayOfByte) {
    ActiveObjectMap.Key key = new ActiveObjectMap.Key(paramArrayOfByte);
    return deactivateObject(key);
  }
  
  protected void deactivateHelper(ActiveObjectMap.Key paramKey, AOMEntry paramAOMEntry, Servant paramServant) {
    this.activeObjectMap.remove(paramKey);
    if (Util.isInstanceDefined()) {
      POAManagerImpl pOAManagerImpl = (POAManagerImpl)this.poa.the_POAManager();
      POAFactory pOAFactory = pOAManagerImpl.getFactory();
      pOAFactory.unregisterPOAForServant(this.poa, paramServant);
    } 
  }
  
  public Servant deactivateObject(ActiveObjectMap.Key paramKey) {
    if (this.orb.poaDebugFlag)
      ORBUtility.dprint(this, "Calling deactivateObject for key " + paramKey); 
    try {
      AOMEntry aOMEntry = this.activeObjectMap.get(paramKey);
      if (aOMEntry == null)
        throw new ObjectNotActive(); 
      Servant servant = this.activeObjectMap.getServant(aOMEntry);
      if (servant == null)
        throw new ObjectNotActive(); 
      if (this.orb.poaDebugFlag)
        System.out.println("Deactivating object " + servant + " with POA " + this.poa); 
      deactivateHelper(paramKey, aOMEntry, servant);
      return servant;
    } finally {
      if (this.orb.poaDebugFlag)
        ORBUtility.dprint(this, "Exiting deactivateObject"); 
    } 
  }
  
  public byte[] servantToId(Servant paramServant) throws ServantNotActive, WrongPolicy {
    if (!this.isUnique && !this.isImplicit)
      throw new WrongPolicy(); 
    if (this.isUnique) {
      ActiveObjectMap.Key key = this.activeObjectMap.getKey(paramServant);
      if (key != null)
        return key.id; 
    } 
    if (this.isImplicit)
      try {
        byte[] arrayOfByte = newSystemId();
        activateObject(arrayOfByte, paramServant);
        return arrayOfByte;
      } catch (ObjectAlreadyActive objectAlreadyActive) {
        throw this.poa.invocationWrapper().servantToIdOaa(objectAlreadyActive);
      } catch (ServantAlreadyActive servantAlreadyActive) {
        throw this.poa.invocationWrapper().servantToIdSaa(servantAlreadyActive);
      } catch (WrongPolicy wrongPolicy) {
        throw this.poa.invocationWrapper().servantToIdWp(wrongPolicy);
      }  
    throw new ServantNotActive();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\oa\poa\POAPolicyMediatorBase_R.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */