package com.sun.corba.se.spi.extension;

import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Policy;

public class ZeroPortPolicy extends LocalObject implements Policy {
  private static ZeroPortPolicy policy = new ZeroPortPolicy(true);
  
  private boolean flag = true;
  
  private ZeroPortPolicy(boolean paramBoolean) { this.flag = paramBoolean; }
  
  public String toString() { return "ZeroPortPolicy[" + this.flag + "]"; }
  
  public boolean forceZeroPort() { return this.flag; }
  
  public static ZeroPortPolicy getPolicy() { return policy; }
  
  public int policy_type() { return 1398079489; }
  
  public Policy copy() { return this; }
  
  public void destroy() {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\extension\ZeroPortPolicy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */