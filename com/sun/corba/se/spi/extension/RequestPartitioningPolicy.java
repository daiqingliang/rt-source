package com.sun.corba.se.spi.extension;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Policy;

public class RequestPartitioningPolicy extends LocalObject implements Policy {
  private static ORBUtilSystemException wrapper = ORBUtilSystemException.get("oa.ior");
  
  public static final int DEFAULT_VALUE = 0;
  
  private final int value;
  
  public RequestPartitioningPolicy(int paramInt) {
    if (paramInt < 0 || paramInt > 63)
      throw wrapper.invalidRequestPartitioningPolicyValue(new Integer(paramInt), new Integer(0), new Integer(63)); 
    this.value = paramInt;
  }
  
  public int getValue() { return this.value; }
  
  public int policy_type() { return 1398079491; }
  
  public Policy copy() { return this; }
  
  public void destroy() {}
  
  public String toString() { return "RequestPartitioningPolicy[" + this.value + "]"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\extension\RequestPartitioningPolicy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */