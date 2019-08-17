package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class AlreadyBoundHolder implements Streamable {
  public AlreadyBound value = null;
  
  public AlreadyBoundHolder() {}
  
  public AlreadyBoundHolder(AlreadyBound paramAlreadyBound) { this.value = paramAlreadyBound; }
  
  public void _read(InputStream paramInputStream) { this.value = AlreadyBoundHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { AlreadyBoundHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return AlreadyBoundHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CosNaming\NamingContextPackage\AlreadyBoundHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */