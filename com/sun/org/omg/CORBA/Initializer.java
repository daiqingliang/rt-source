package com.sun.org.omg.CORBA;

import org.omg.CORBA.StructMember;
import org.omg.CORBA.portable.IDLEntity;

public final class Initializer implements IDLEntity {
  public StructMember[] members = null;
  
  public String name = null;
  
  public Initializer() {}
  
  public Initializer(StructMember[] paramArrayOfStructMember, String paramString) {
    this.members = paramArrayOfStructMember;
    this.name = paramString;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\omg\CORBA\Initializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */