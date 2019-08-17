package com.sun.corba.se.impl.oa.poa;

import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Policy;
import org.omg.PortableServer.ServantRetentionPolicy;
import org.omg.PortableServer.ServantRetentionPolicyValue;

final class ServantRetentionPolicyImpl extends LocalObject implements ServantRetentionPolicy {
  private ServantRetentionPolicyValue value;
  
  public ServantRetentionPolicyImpl(ServantRetentionPolicyValue paramServantRetentionPolicyValue) { this.value = paramServantRetentionPolicyValue; }
  
  public ServantRetentionPolicyValue value() { return this.value; }
  
  public int policy_type() { return 21; }
  
  public Policy copy() { return new ServantRetentionPolicyImpl(this.value); }
  
  public void destroy() { this.value = null; }
  
  public String toString() { return "ServantRetentionPolicy[" + ((this.value.value() == 0) ? "RETAIN" : "NON_RETAIN]"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\oa\poa\ServantRetentionPolicyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */