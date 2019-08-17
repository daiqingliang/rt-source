package org.omg.CosNaming;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class NameHolder implements Streamable {
  public NameComponent[] value = null;
  
  public NameHolder() {}
  
  public NameHolder(NameComponent[] paramArrayOfNameComponent) { this.value = paramArrayOfNameComponent; }
  
  public void _read(InputStream paramInputStream) { this.value = NameHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { NameHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return NameHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CosNaming\NameHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */