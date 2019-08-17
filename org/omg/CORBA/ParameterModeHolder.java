package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ParameterModeHolder implements Streamable {
  public ParameterMode value = null;
  
  public ParameterModeHolder() {}
  
  public ParameterModeHolder(ParameterMode paramParameterMode) { this.value = paramParameterMode; }
  
  public void _read(InputStream paramInputStream) { this.value = ParameterModeHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { ParameterModeHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return ParameterModeHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\ParameterModeHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */