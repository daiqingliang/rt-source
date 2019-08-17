package com.sun.corba.se.impl.corba;

import org.omg.CORBA.Principal;

public class PrincipalImpl extends Principal {
  private byte[] value;
  
  public void name(byte[] paramArrayOfByte) { this.value = paramArrayOfByte; }
  
  public byte[] name() { return this.value; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\corba\PrincipalImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */