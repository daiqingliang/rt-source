package org.omg.CosNaming;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class BindingListHolder implements Streamable {
  public Binding[] value = null;
  
  public BindingListHolder() {}
  
  public BindingListHolder(Binding[] paramArrayOfBinding) { this.value = paramArrayOfBinding; }
  
  public void _read(InputStream paramInputStream) { this.value = BindingListHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { BindingListHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return BindingListHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CosNaming\BindingListHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */