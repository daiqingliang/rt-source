package org.omg.IOP;

import org.omg.CORBA.portable.IDLEntity;

public final class TaggedProfile implements IDLEntity {
  public int tag = 0;
  
  public byte[] profile_data = null;
  
  public TaggedProfile() {}
  
  public TaggedProfile(int paramInt, byte[] paramArrayOfByte) {
    this.tag = paramInt;
    this.profile_data = paramArrayOfByte;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\IOP\TaggedProfile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */