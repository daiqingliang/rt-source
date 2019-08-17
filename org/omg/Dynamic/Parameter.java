package org.omg.Dynamic;

import org.omg.CORBA.Any;
import org.omg.CORBA.ParameterMode;
import org.omg.CORBA.portable.IDLEntity;

public final class Parameter implements IDLEntity {
  public Any argument = null;
  
  public ParameterMode mode = null;
  
  public Parameter() {}
  
  public Parameter(Any paramAny, ParameterMode paramParameterMode) {
    this.argument = paramAny;
    this.mode = paramParameterMode;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\Dynamic\Parameter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */