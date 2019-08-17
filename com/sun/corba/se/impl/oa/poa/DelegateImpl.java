package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.spi.orb.ORB;
import java.util.EmptyStackException;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.portable.Delegate;

public class DelegateImpl implements Delegate {
  private ORB orb;
  
  private POASystemException wrapper;
  
  private POAFactory factory;
  
  public DelegateImpl(ORB paramORB, POAFactory paramPOAFactory) {
    this.orb = paramORB;
    this.wrapper = POASystemException.get(paramORB, "oa");
    this.factory = paramPOAFactory;
  }
  
  public ORB orb(Servant paramServant) { return this.orb; }
  
  public Object this_object(Servant paramServant) {
    try {
      byte[] arrayOfByte = this.orb.peekInvocationInfo().id();
      POA pOA = (POA)this.orb.peekInvocationInfo().oa();
      String str = paramServant._all_interfaces(pOA, arrayOfByte)[0];
      return pOA.create_reference_with_id(arrayOfByte, str);
    } catch (EmptyStackException emptyStackException) {
      POAImpl pOAImpl = null;
      try {
        pOAImpl = (POAImpl)paramServant._default_POA();
      } catch (ClassCastException classCastException) {
        throw this.wrapper.defaultPoaNotPoaimpl(classCastException);
      } 
      try {
        if (pOAImpl.getPolicies().isImplicitlyActivated() || (pOAImpl.getPolicies().isUniqueIds() && pOAImpl.getPolicies().retainServants()))
          return pOAImpl.servant_to_reference(paramServant); 
        throw this.wrapper.wrongPoliciesForThisObject();
      } catch (ServantNotActive servantNotActive) {
        throw this.wrapper.thisObjectServantNotActive(servantNotActive);
      } catch (WrongPolicy wrongPolicy) {
        throw this.wrapper.thisObjectWrongPolicy(wrongPolicy);
      } 
    } catch (ClassCastException classCastException) {
      throw this.wrapper.defaultPoaNotPoaimpl(classCastException);
    } 
  }
  
  public POA poa(Servant paramServant) {
    try {
      return (POA)this.orb.peekInvocationInfo().oa();
    } catch (EmptyStackException emptyStackException) {
      POA pOA = this.factory.lookupPOA(paramServant);
      if (pOA != null)
        return pOA; 
      throw this.wrapper.noContext(emptyStackException);
    } 
  }
  
  public byte[] object_id(Servant paramServant) {
    try {
      return this.orb.peekInvocationInfo().id();
    } catch (EmptyStackException emptyStackException) {
      throw this.wrapper.noContext(emptyStackException);
    } 
  }
  
  public POA default_POA(Servant paramServant) { return this.factory.getRootPOA(); }
  
  public boolean is_a(Servant paramServant, String paramString) {
    String[] arrayOfString = paramServant._all_interfaces(poa(paramServant), object_id(paramServant));
    for (byte b = 0; b < arrayOfString.length; b++) {
      if (paramString.equals(arrayOfString[b]))
        return true; 
    } 
    return false;
  }
  
  public boolean non_existent(Servant paramServant) {
    try {
      byte[] arrayOfByte = this.orb.peekInvocationInfo().id();
      return (arrayOfByte == null);
    } catch (EmptyStackException emptyStackException) {
      throw this.wrapper.noContext(emptyStackException);
    } 
  }
  
  public Object get_interface_def(Servant paramServant) { throw this.wrapper.methodNotImplemented(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\oa\poa\DelegateImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */