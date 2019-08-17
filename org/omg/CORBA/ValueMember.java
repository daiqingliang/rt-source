package org.omg.CORBA;

import org.omg.CORBA.portable.IDLEntity;

public final class ValueMember implements IDLEntity {
  public String name;
  
  public String id;
  
  public String defined_in;
  
  public String version;
  
  public TypeCode type;
  
  public IDLType type_def;
  
  public short access;
  
  public ValueMember() {}
  
  public ValueMember(String paramString1, String paramString2, String paramString3, String paramString4, TypeCode paramTypeCode, IDLType paramIDLType, short paramShort) {
    this.name = paramString1;
    this.id = paramString2;
    this.defined_in = paramString3;
    this.version = paramString4;
    this.type = paramTypeCode;
    this.type_def = paramIDLType;
    this.access = paramShort;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\ValueMember.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */