package org.omg.IOP;

import org.omg.CORBA.portable.IDLEntity;

public final class ServiceContext implements IDLEntity {
  public int context_id = 0;
  
  public byte[] context_data = null;
  
  public ServiceContext() {}
  
  public ServiceContext(int paramInt, byte[] paramArrayOfByte) {
    this.context_id = paramInt;
    this.context_data = paramArrayOfByte;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\IOP\ServiceContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */