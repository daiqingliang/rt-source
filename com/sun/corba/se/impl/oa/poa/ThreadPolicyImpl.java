package com.sun.corba.se.impl.oa.poa;

import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Policy;
import org.omg.PortableServer.ThreadPolicy;
import org.omg.PortableServer.ThreadPolicyValue;

final class ThreadPolicyImpl extends LocalObject implements ThreadPolicy {
  private ThreadPolicyValue value;
  
  public ThreadPolicyImpl(ThreadPolicyValue paramThreadPolicyValue) { this.value = paramThreadPolicyValue; }
  
  public ThreadPolicyValue value() { return this.value; }
  
  public int policy_type() { return 16; }
  
  public Policy copy() { return new ThreadPolicyImpl(this.value); }
  
  public void destroy() { this.value = null; }
  
  public String toString() { return "ThreadPolicy[" + ((this.value.value() == 1) ? "SINGLE_THREAD_MODEL" : "ORB_CTRL_MODEL]"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\oa\poa\ThreadPolicyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */