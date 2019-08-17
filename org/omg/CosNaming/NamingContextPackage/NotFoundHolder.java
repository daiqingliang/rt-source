package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class NotFoundHolder implements Streamable {
  public NotFound value = null;
  
  public NotFoundHolder() {}
  
  public NotFoundHolder(NotFound paramNotFound) { this.value = paramNotFound; }
  
  public void _read(InputStream paramInputStream) { this.value = NotFoundHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { NotFoundHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return NotFoundHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CosNaming\NamingContextPackage\NotFoundHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */