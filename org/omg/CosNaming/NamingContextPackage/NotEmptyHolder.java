package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class NotEmptyHolder implements Streamable {
  public NotEmpty value = null;
  
  public NotEmptyHolder() {}
  
  public NotEmptyHolder(NotEmpty paramNotEmpty) { this.value = paramNotEmpty; }
  
  public void _read(InputStream paramInputStream) { this.value = NotEmptyHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { NotEmptyHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return NotEmptyHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CosNaming\NamingContextPackage\NotEmptyHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */