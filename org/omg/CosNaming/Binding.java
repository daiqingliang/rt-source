package org.omg.CosNaming;

import org.omg.CORBA.portable.IDLEntity;

public final class Binding implements IDLEntity {
  public NameComponent[] binding_name = null;
  
  public BindingType binding_type = null;
  
  public Binding() {}
  
  public Binding(NameComponent[] paramArrayOfNameComponent, BindingType paramBindingType) {
    this.binding_name = paramArrayOfNameComponent;
    this.binding_type = paramBindingType;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CosNaming\Binding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */