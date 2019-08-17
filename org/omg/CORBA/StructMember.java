package org.omg.CORBA;

import org.omg.CORBA.portable.IDLEntity;

public final class StructMember implements IDLEntity {
  public String name;
  
  public TypeCode type;
  
  public IDLType type_def;
  
  public StructMember() {}
  
  public StructMember(String paramString, TypeCode paramTypeCode, IDLType paramIDLType) {
    this.name = paramString;
    this.type = paramTypeCode;
    this.type_def = paramIDLType;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\StructMember.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */