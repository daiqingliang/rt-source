package org.omg.IOP;

import org.omg.CORBA.portable.IDLEntity;

public final class IOR implements IDLEntity {
  public String type_id = null;
  
  public TaggedProfile[] profiles = null;
  
  public IOR() {}
  
  public IOR(String paramString, TaggedProfile[] paramArrayOfTaggedProfile) {
    this.type_id = paramString;
    this.profiles = paramArrayOfTaggedProfile;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\IOP\IOR.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */