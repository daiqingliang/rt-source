package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.UserException;

public final class InvalidPolicy extends UserException {
  public short index = 0;
  
  public InvalidPolicy() { super(InvalidPolicyHelper.id()); }
  
  public InvalidPolicy(short paramShort) {
    super(InvalidPolicyHelper.id());
    this.index = paramShort;
  }
  
  public InvalidPolicy(String paramString, short paramShort) {
    super(InvalidPolicyHelper.id() + "  " + paramString);
    this.index = paramShort;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableServer\POAPackage\InvalidPolicy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */