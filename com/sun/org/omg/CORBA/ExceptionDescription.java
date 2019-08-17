package com.sun.org.omg.CORBA;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.IDLEntity;

public final class ExceptionDescription implements IDLEntity {
  public String name = null;
  
  public String id = null;
  
  public String defined_in = null;
  
  public String version = null;
  
  public TypeCode type = null;
  
  public ExceptionDescription() {}
  
  public ExceptionDescription(String paramString1, String paramString2, String paramString3, String paramString4, TypeCode paramTypeCode) {
    this.name = paramString1;
    this.id = paramString2;
    this.defined_in = paramString3;
    this.version = paramString4;
    this.type = paramTypeCode;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\omg\CORBA\ExceptionDescription.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */