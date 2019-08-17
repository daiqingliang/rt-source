package com.sun.corba.se.impl.protocol.giopmsgheaders;

import org.omg.CORBA.portable.IDLEntity;
import org.omg.IOP.IOR;

public final class IORAddressingInfo implements IDLEntity {
  public int selected_profile_index = 0;
  
  public IOR ior = null;
  
  public IORAddressingInfo() {}
  
  public IORAddressingInfo(int paramInt, IOR paramIOR) {
    this.selected_profile_index = paramInt;
    this.ior = paramIOR;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\IORAddressingInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */