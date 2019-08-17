package org.omg.DynamicAny;

import org.omg.CORBA.portable.IDLEntity;

public final class NameDynAnyPair implements IDLEntity {
  public String id = null;
  
  public DynAny value = null;
  
  public NameDynAnyPair() {}
  
  public NameDynAnyPair(String paramString, DynAny paramDynAny) {
    this.id = paramString;
    this.value = paramDynAny;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\DynamicAny\NameDynAnyPair.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */