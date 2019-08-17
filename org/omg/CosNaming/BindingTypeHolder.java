package org.omg.CosNaming;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class BindingTypeHolder implements Streamable {
  public BindingType value = null;
  
  public BindingTypeHolder() {}
  
  public BindingTypeHolder(BindingType paramBindingType) { this.value = paramBindingType; }
  
  public void _read(InputStream paramInputStream) { this.value = BindingTypeHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { BindingTypeHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return BindingTypeHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CosNaming\BindingTypeHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */