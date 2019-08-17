package com.sun.org.omg.CORBA;

import org.omg.CORBA.IDLType;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.IDLEntity;

public final class ParameterDescription implements IDLEntity {
  public String name = null;
  
  public TypeCode type = null;
  
  public IDLType type_def = null;
  
  public ParameterMode mode = null;
  
  public ParameterDescription() {}
  
  public ParameterDescription(String paramString, TypeCode paramTypeCode, IDLType paramIDLType, ParameterMode paramParameterMode) {
    this.name = paramString;
    this.type = paramTypeCode;
    this.type_def = paramIDLType;
    this.mode = paramParameterMode;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\omg\CORBA\ParameterDescription.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */