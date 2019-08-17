package org.omg.CORBA;

import org.omg.CORBA.portable.IDLEntity;

public final class UnionMember implements IDLEntity {
  public String name;
  
  public Any label;
  
  public TypeCode type;
  
  public IDLType type_def;
  
  public UnionMember() {}
  
  public UnionMember(String paramString, Any paramAny, TypeCode paramTypeCode, IDLType paramIDLType) {
    this.name = paramString;
    this.label = paramAny;
    this.type = paramTypeCode;
    this.type_def = paramIDLType;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\UnionMember.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */