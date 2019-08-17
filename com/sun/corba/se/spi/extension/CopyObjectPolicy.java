package com.sun.corba.se.spi.extension;

import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Policy;

public class CopyObjectPolicy extends LocalObject implements Policy {
  private final int value;
  
  public CopyObjectPolicy(int paramInt) { this.value = paramInt; }
  
  public int getValue() { return this.value; }
  
  public int policy_type() { return 1398079490; }
  
  public Policy copy() { return this; }
  
  public void destroy() {}
  
  public String toString() { return "CopyObjectPolicy[" + this.value + "]"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\extension\CopyObjectPolicy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */