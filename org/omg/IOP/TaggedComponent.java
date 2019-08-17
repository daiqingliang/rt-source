package org.omg.IOP;

import org.omg.CORBA.portable.IDLEntity;

public final class TaggedComponent implements IDLEntity {
  public int tag = 0;
  
  public byte[] component_data = null;
  
  public TaggedComponent() {}
  
  public TaggedComponent(int paramInt, byte[] paramArrayOfByte) {
    this.tag = paramInt;
    this.component_data = paramArrayOfByte;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\IOP\TaggedComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */