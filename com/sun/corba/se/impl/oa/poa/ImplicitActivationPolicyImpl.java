package com.sun.corba.se.impl.oa.poa;

import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Policy;
import org.omg.PortableServer.ImplicitActivationPolicy;
import org.omg.PortableServer.ImplicitActivationPolicyValue;

final class ImplicitActivationPolicyImpl extends LocalObject implements ImplicitActivationPolicy {
  private ImplicitActivationPolicyValue value;
  
  public ImplicitActivationPolicyImpl(ImplicitActivationPolicyValue paramImplicitActivationPolicyValue) { this.value = paramImplicitActivationPolicyValue; }
  
  public ImplicitActivationPolicyValue value() { return this.value; }
  
  public int policy_type() { return 20; }
  
  public Policy copy() { return new ImplicitActivationPolicyImpl(this.value); }
  
  public void destroy() { this.value = null; }
  
  public String toString() { return "ImplicitActivationPolicy[" + ((this.value.value() == 0) ? "IMPLICIT_ACTIVATION" : "NO_IMPLICIT_ACTIVATION]"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\oa\poa\ImplicitActivationPolicyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */